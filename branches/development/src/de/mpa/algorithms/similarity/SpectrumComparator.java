package de.mpa.algorithms.similarity;

import java.util.Map;


public interface SpectrumComparator {
	public double getSimilarity();
	public void prepare(Map<Double, Double> inputPeaksSrc);
	public void compareTo(Map<Double, Double> inputPeaksTrg);
	public Map<Double, Double> getSourcePeaks();
	public Vectorization getVectorization();
}	
