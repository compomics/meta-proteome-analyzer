package de.mpa.interfaces;


/**
 * Interface specification for the spectrum comparisons.
 * @author Thilo Muth
 *
 */
public interface SpectrumComparator {	
	
	// Method for the execution of the algorithm(s)
	public void compare();
	
	// Returns the similarity value
	public double getSimilarity();
	
}
