package de.mpa.algorithms;

import java.util.HashMap;

import de.mpa.io.MascotGenericFile;

/**
 * This class represents a specification which features are considered for the spectrum quality (filtering).
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
	private final MascotGenericFile mgf;
	
	// Fragment ion tolerance
	private final double fragTol;
	
	/**
	 * Class constructor.
	 * @param mgf the spectrum file
	 * @param fragTol the fragment ion tolerance
	 */
	public SpectrumFeatures(MascotGenericFile mgf, double fragTol) {
		this.mgf = mgf;
		this.fragTol = fragTol;
        this.init();
	}
	
	/**
	 * Initializes the spectrum features and calculates the values from the 
	 * information presented in the spectrum.
	 */
	private void init(){
		// Standard
        numPeaks = this.mgf.getPeaks().size();
        totalInt = this.mgf.getTotalIntensity();
        highestInt = this.mgf.getHighestIntensity();
        meanInt = this.totalInt / this.numPeaks;
        devInt = this.calcPeakIntensityDeviation();
        precursorMass = this.mgf.getPrecursorMZ();
        precursorCharge = this.mgf.getCharge();
		
		// Init the masses
		Masses.init();
		
		// Optional
        y1PeakInt = this.calcY1IonIntensity();
	}
	
	/**
	 * Calculates the peak intensity standard deviation.
	 * @return the calculated peak intensity standard deviation
	 */
	private double calcPeakIntensityDeviation(){		
		double sum = 0.0;		
		for(double intensity : this.mgf.getPeaks().values()){
			sum += Math.pow(intensity - meanInt, 2);
		}
		return Math.sqrt(sum / numPeaks);
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
		
		HashMap<Double, Double> peaks = this.mgf.getPeaks();
		for (double mz : peaks.keySet()) {
			// Check whether both ions match within a certain fragment ion tolerance window.
			if(Math.abs(mz - y1_lys) <= this.fragTol) {
				y1_lysInt = peaks.get(mz);
			} else if (Math.abs(mz - y1_arg) <= this.fragTol) {
				y1_argInt = peaks.get(mz);
			}
		}
		return (y1_lysInt > y1_argInt) ? y1_lysInt : y1_argInt;
	}
	
	public int getNumPeaks() {
		return this.numPeaks;
	}

	public double getTotalInt() {
		return this.totalInt;
	}

	public double getHighestInt() {
		return this.highestInt;
	}

	public double getMeanInt() {
		return this.meanInt;
	}

	public double getDevInt() {
		return this.devInt;
	}

	public double getPrecursorMass() {
		return this.precursorMass;
	}

	public double getPrecursorCharge() {
		return this.precursorCharge;
	}

	public double getPrecursorInt() {
		return this.precursorInt;
	}

	public double getNormTotalInt() {
		return this.normTotalInt;
	}

	public int getNumSignPeaks() {
		return this.numSignPeaks;
	}

	public double getTotalSignPeaksInt() {
		return this.totalSignPeaksInt;
	}

	public double getY1PeakInt() {
		return this.y1PeakInt;
	}

	public int getNumDeNovoPeaks() {
		return this.numDeNovoPeaks;
	}

	public int getNumIsotopePeaks() {
		return this.numIsotopePeaks;
	}

	public int getNumH20LossPeaks() {
		return this.numH20LossPeaks;
	}
}	
