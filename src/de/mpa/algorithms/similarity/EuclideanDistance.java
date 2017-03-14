package de.mpa.algorithms.similarity;

import java.util.Map;


public class EuclideanDistance implements SpectrumComparator {

	private final Vectorization vect;
	private final Transformation trafo;
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
        this.peaksSrc = this.vect.vectorize(inputPeaksSrc, this.trafo);
		
		// calculate squared magnitude of source intensity vector
        this.magSrc = 0.0;
		for (double intenSrc : this.peaksSrc.values()) {
            this.magSrc += intenSrc * intenSrc;
		}
	}

	@Override
	public void compareTo(Map<Double, Double> inputPeaksTrg) {

		// bin target spectrum
		Map<Double, Double> peaksTrg = this.vect.vectorize(inputPeaksTrg, this.trafo);
		
		// calculate euclidean distance
		double distance = this.magSrc;
		for (Map.Entry<Double, Double> peakTrg : peaksTrg.entrySet()) {
			double intenTrg = peakTrg.getValue();
			Double intenSrc = this.peaksSrc.get(peakTrg.getKey());
			if (intenSrc == null) { 
				intenSrc = 0.0;
			} else {
				distance -= intenSrc * intenSrc;
			}
			double delta = intenTrg - intenSrc;
			distance += delta * delta;
		}

        similarity = Math.sqrt(distance);
	}

	@Override
	public double getSimilarity() {
		return this.similarity;
	}

	@Override
	public Map<Double, Double> getSourcePeaks() {
		return this.peaksSrc;
	}

	@Override
	public Vectorization getVectorization() {
		return this.vect;
	}

}
