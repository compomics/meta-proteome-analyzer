package de.mpa.interfaces;

import java.util.Map;


/**
 * Interface specification for the spectrum comparisons.
 * @author Thilo Muth
 * @author Alexander Behne
 */
public interface SpectrumComparator {
	
	/**
	 * Method to set up comparator algorithm. Applies transformations to 
	 * supplied peak map depending on the chosen algorithm.
	 * @param peaksSrc The peak map with which is compared.
	 */
	public void prepare(Map<Double, Double> peaksSrc);
	
	/**
	 * Method to execute comparator algorithm.
	 * @param peaksTrg The peak map which is to be searched against.
	 */
	public void compareTo(Map<Double, Double> peaksTrg);

	/**
	 * Method to reset temporary variables to their defaults.
	 */
	public void cleanup();

	/**
	 * @return The computed similarity value
	 */
	public double getSimilarity();
	
	/**
	 * @return The transformed source spectrum map after preparation.
	 */
	public Map<Double, Double> getSourcePeaks();
		
}
