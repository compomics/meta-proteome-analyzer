package de.mpa.algorithms.similarity;

import java.util.Map;
import java.util.Map.Entry;


public class EuclideanDistance implements SpectrumComparator {

	private Vectorization vect;
	private Transformation trafo;
	private Map<Double, Double> peaksSrc;
	private double magSrc;
	private double similarity;
	
//	private final double sqrt2 = Math.sqrt(2.0);

	/**
	 * Class constructor specifying vectorization and data transformation methods.
	 * @param vect
	 * @param trafo
	 */
	public EuclideanDistance(Vectorization vect, Transformation trafo) {
		this.vect = vect;
		this.trafo = trafo;
	}
	
	@Override
	public void prepare(Map<Double, Double> inputPeaksSrc) {
		
		// bin source spectrum
		peaksSrc = vect.vectorize(inputPeaksSrc, trafo);
		
		// calculate squared magnitude of source intensity vector
		magSrc = 0.0;
		for (double intenSrc : peaksSrc.values()) {
			magSrc += intenSrc * intenSrc;
		}
	}

	@Override
	public void compareTo(Map<Double, Double> inputPeaksTrg) {

		// bin target spectrum
		Map<Double, Double> peaksTrg = vect.vectorize(inputPeaksTrg, trafo);
		
		// calculate euclidean distance
		double distance = magSrc;
		for (Entry<Double, Double> peakTrg : peaksTrg.entrySet()) {
			double intenTrg = peakTrg.getValue();
			Double intenSrc = peaksSrc.get(peakTrg.getKey());
			if (intenSrc == null) { 
				intenSrc = 0.0;
			} else {
				distance -= intenSrc * intenSrc;
			}
			double delta = intenTrg - intenSrc;
			distance += delta * delta;
		}
		
		this.similarity = Math.sqrt(distance);
	}

	@Override
	public double getSimilarity() {
		return similarity;
	}

	@Override
	public Map<Double, Double> getSourcePeaks() {
		return peaksSrc;
	}

	@Override
	public Vectorization getVectorization() {
		return vect;
	}

}
