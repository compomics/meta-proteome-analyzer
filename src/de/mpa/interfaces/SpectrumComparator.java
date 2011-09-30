package de.mpa.interfaces;

import java.util.ArrayList;

import de.mpa.io.Peak;


/**
 * Interface specification for the spectrum comparisons.
 * @author Thilo Muth
 *
 */
public interface SpectrumComparator {	
	
	// Method for the execution of the algorithm(s)
	public void compare(ArrayList<Peak> highA, ArrayList<Peak> highB);

	// Returns the similarity value
	double getSimilarity();
		
}
