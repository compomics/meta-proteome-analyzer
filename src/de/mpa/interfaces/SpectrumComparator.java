package de.mpa.interfaces;

import java.util.Map;

public interface SpectrumComparator {
	public double getSimilarity();
	public void prepare(Map<Double, Double> inputPeaksSrc);
	public void compareTo(Map<Double, Double> inputPeaksTrg);
	public Map<Double, Double> getSourcePeaks();
	public void cleanup();
}	
