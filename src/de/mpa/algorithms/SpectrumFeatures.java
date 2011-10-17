package de.mpa.algorithms;

/**
 * This class represents a specification which features are considered for the spectrum quality (filtering).
 * @author Thilo Muth
 *
 */
public class SpectrumFeatures {
	
	//// STANDARD FEATURES ////
	
	// Number of peaks
	private int numPeaks;
	
	// Total ion current / total peak intensity
	private double totalInt;	
	
	// Average peak intensity
	private double meanInt;	
	
	// Standard deviation of the spectrum peaks	
	private double devInt;	
	
	// Precursor intensity
	private double precursorInt;
	
	// Precursor charge
	private double precursorCharge;
	
	//// OPTIONAL FEATURES ////
	
	// Normalized total intensity: TIC / MeanTIC (of all runs)
	private double normTotalInt;
	
	// Number of significant peaks --> Peak with a relative intensity higher than 5 % (?)
	private int numSignPeaks;
	
	// Total intensity of the significant peaks
	private int totalSignPeaksInt;
	
	// The intensity of the y1-ion peak --> lysine /argine for tryptic peptides
	private double y1PeakInt;
	
	// Number of manually de-novo sequenced amino acids (single charged): Calculating peak differences
	private int numDeNovoPeaks;
	
	// Number of isotope peaks
	private int numIsotopePeaks;
	
	// Number of water loss peaks
	private int numH20LossPeaks;
	
}	
