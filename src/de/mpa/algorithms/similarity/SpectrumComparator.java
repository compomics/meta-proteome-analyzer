package de.mpa.algorithms.similarity;

import java.util.Map;


public interface SpectrumComparator {
	double getSimilarity();
	void prepare(Map<Double, Double> inputPeaksSrc);
	void compareTo(Map<Double, Double> inputPeaksTrg);
	Map<Double, Double> getSourcePeaks();
	Vectorization getVectorization();
}	
