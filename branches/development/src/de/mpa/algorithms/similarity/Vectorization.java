package de.mpa.algorithms.similarity;

import java.util.Map;

/**
 * General interface for correcting m/z keys of spectrum peak maps to conform to
 * a specific shape, e.g. matching that of a different peak map.
 * 
 * @author A. Behne
 */
public interface Vectorization {
	
	public abstract Map<Double, Double> vectorize(Map<Double, Double> input, Transformation trafo);
	
	public abstract void cleanup();
}