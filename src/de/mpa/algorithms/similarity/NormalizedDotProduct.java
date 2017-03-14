package de.mpa.algorithms.similarity;

import java.util.Map;


public class NormalizedDotProduct implements SpectrumComparator {
	
	/**
	 * The input vectorization method.
	 */
	private final Vectorization vect;
	
	/**
	 * The input transformation method.
	 */
	private final Transformation trafo;

	/**
	 * The peak map of the source spectrum.
	 */
	private Map<Double, Double> peaksSrc; 
	
	/**
	 * The similarity score between source spectrum and target spectrum.
	 * Ranges between 0.0 and 1.0.
	 */
	private double similarity;

	/**
	 * The squared magnitude of the source intensity vector.
	 */
	private double denom1;
	
	/**
	 * Class constructor specifying vectorization and data transformation methods.
	 * @param vect
	 * @param trafo
	 */
	public NormalizedDotProduct(Vectorization vect, Transformation trafo) {
		this.vect = vect;
		this.trafo = trafo;
	}
	
	@Override
	public void prepare(Map<Double, Double> inputPeaksSrc) {

		// bin source spectrum
        this.peaksSrc = this.vect.vectorize(inputPeaksSrc, this.trafo);

        this.denom1 = 0.0;
		for (double intenSrc : this.peaksSrc.values()) {
            this.denom1 += intenSrc * intenSrc; }
		
	}
	
	@Override
	public void compareTo(Map<Double, Double> inputPeaksTrg) {

		// bin target spectrum
		Map<Double, Double> peaksTrg = this.vect.vectorize(inputPeaksTrg, this.trafo);
		
		// calculate dot product
		double numer = 0.0, denom2 = 0.0;
		for (Map.Entry<Double, Double> peakTrg : peaksTrg.entrySet()) {
			double intenTrg = peakTrg.getValue();
			Double intenSrc = this.peaksSrc.get(peakTrg.getKey());
			if (intenSrc != null) {
				numer += intenSrc * intenTrg;
			}
			denom2 += intenTrg * intenTrg;
		}
		
		// normalize score
        similarity = numer / Math.sqrt(this.denom1 * denom2);
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
