package de.mpa.algorithms;

import java.util.HashMap;

import de.mpa.io.MascotGenericFile;

/**
 * This class represents a specification which features are considered for the spectrum quality (filtering).
 * TODO: Optional features!
 * @author Thilo Muth
 * @author Alexander Behne
 *
 */
public class SpectrumFeatures {
	
	//// STANDARD FEATURES ////
	
	// Number of peaks
	private int numPeaks;
	
	// Total ion current / total peak intensity
	private double totalInt;	
	
	// Highest intensity
	private double highestInt;
	
	// Average peak intensity
	private double meanInt;	
	
	// Standard deviation of the spectrum peaks	
	private double devInt;	
	
	// Precursor mass
	private double precursorMass;
	
	// Precursor charge
	private double precursorCharge;
	
	//// OPTIONAL FEATURES ////
	
	// Precursor intensity
	private double precursorInt;
	
	// Normalized total intensity: TIC / MeanTIC (of all runs)
	private double normTotalInt;
	
	// Number of significant peaks --> Peak with a relative intensity higher than 5 % (?)
	private int numSignPeaks;
	
	// Total intensity of the significant peaks
	private double totalSignPeaksInt;
	
	// The intensity of the y1-ion --> lysine /argine for tryptic peptides
	private double y1PeakInt;
	
	// Number of manually de-novo sequenced amino acids (single charged): Calculating peak differences
	private int numDeNovoPeaks;
	
	// Number of isotope peaks
	private int numIsotopePeaks;
	
	// Number of water loss peaks
	private int numH20LossPeaks;
	
	// MGF file
	private MascotGenericFile mgf;
	
	// Fragment ion tolerance
	private double fragTol;
	
	/**
	 * Class constructor.
	 * @param mgf the spectrum file
	 * @param fragTol the fragment ion tolerance
	 */
	public SpectrumFeatures(MascotGenericFile mgf, double fragTol) {
		this.mgf = mgf;
		this.fragTol = fragTol;
		init();
	}
	
	/**
	 * Initializes the spectrum features and calculates the values from the 
	 * information presented in the spectrum.
	 */
	private void init(){
		// Standard
		this.numPeaks = mgf.getPeaks().size();
		this.totalInt = mgf.getTotalIntensity();
		this.highestInt = mgf.getHighestIntensity();		
		this.meanInt = totalInt / numPeaks; 
		this.devInt = calcPeakIntensityDeviation();
		this.precursorMass = mgf.getPrecursorMZ();
		this.precursorCharge = mgf.getCharge();
		
		// Init the masses
		Masses.init();
		
		// Optional
		this.y1PeakInt = calcY1IonIntensity();
	}
	
	/**
	 * Calculates the peak intensity standard deviation.
	 * @return the calculated peak intensity standard deviation
	 */
	private double calcPeakIntensityDeviation(){		
		double sum = 0.0;		
		for(double intensity : mgf.getPeaks().values()){
			sum += Math.pow(intensity - this.meanInt, 2);
		}
		return Math.sqrt(sum / this.numPeaks);		
	}
	
	/**
	 * Calculates the y1-ion intensity by taking arginine or lysine.
	 * If both R and K are possible, the more abundant peak is taken.
	 * @return the calculated y1-ion intensity 
	 */
	private double calcY1IonIntensity(){
		double y1_lysInt = Double.NaN, y1_argInt = Double.NaN;
		
		// y1-Ion matches
		double y1_lys = Masses.C_term + Masses.aaMap.get('K');
		double y1_arg = Masses.C_term + Masses.aaMap.get('R');	
		
		HashMap<Double, Double> peaks = mgf.getPeaks();
		for (double mz : peaks.keySet()) {
			// Check whether both ions match within a certain fragment ion tolerance window.
			if(Math.abs(mz - y1_lys) <= fragTol) {
				y1_lysInt = peaks.get(mz);
			} else if (Math.abs(mz - y1_arg) <= fragTol) {
				y1_argInt = peaks.get(mz);
			}
		}
		return (y1_lysInt > y1_argInt) ? y1_lysInt : y1_argInt;
	}
	
	public int getNumPeaks() {
		return numPeaks;
	}

	public double getTotalInt() {
		return totalInt;
	}

	public double getHighestInt() {
		return highestInt;
	}

	public double getMeanInt() {
		return meanInt;
	}

	public double getDevInt() {
		return devInt;
	}

	public double getPrecursorMass() {
		return precursorMass;
	}

	public double getPrecursorCharge() {
		return precursorCharge;
	}

	public double getPrecursorInt() {
		return precursorInt;
	}

	public double getNormTotalInt() {
		return normTotalInt;
	}

	public int getNumSignPeaks() {
		return numSignPeaks;
	}

	public double getTotalSignPeaksInt() {
		return totalSignPeaksInt;
	}

	public double getY1PeakInt() {
		return y1PeakInt;
	}

	public int getNumDeNovoPeaks() {
		return numDeNovoPeaks;
	}

	public int getNumIsotopePeaks() {
		return numIsotopePeaks;
	}

	public int getNumH20LossPeaks() {
		return numH20LossPeaks;
	}
}	
