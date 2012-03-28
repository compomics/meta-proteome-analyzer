package de.mpa.algorithms;

import java.util.Map;
import java.util.Map.Entry;

import de.mpa.interfaces.SpectrumComparator;

public class PearsonCorrelation implements SpectrumComparator {

	/**
	 * The input vectorization method.
	 */
	private Vectorization vect;
	
	/**
	 * The input transformation method.
	 */
	private Transformation trafo;

	/**
	 * The peak map of the source spectrum which gets auto-correlated during preparation.
	 */
	private Map<Double, Double> peaksSrc; 
	
	/**
	 * The similarity score between source spectrum and target spectrum.
	 * Ranges between 0.0 and 1.0.
	 */
	private double similarity = 0.0;

	/**
	 * The squared magnitude of the source intensity vector.
	 */
	private double denom1;
	
	/**
	 * Class constructor specifying vectorization and data transformation methods.
	 * @param vect
	 * @param trafo
	 */
	public PearsonCorrelation(Vectorization vect, Transformation trafo) {
		this.vect = vect;
		this.trafo = trafo;
	}
	
	@Override
	public void prepare(Map<Double, Double> inputPeaksSrc) {
		
		// bin source spectrum
		peaksSrc = vect.vectorize(inputPeaksSrc, trafo);
		
		// calculate mean intensity
		double meanSrc = 0.0;
		for (double intensity : peaksSrc.values()) {
			meanSrc += intensity;
		}
		meanSrc /= peaksSrc.size();
		
		// center source intensities
		denom1 = 0.0;
		for (Entry<Double, Double> peak : peaksSrc.entrySet()) {
			double centInt = peak.getValue() - meanSrc;
			denom1 += centInt * centInt;
			peaksSrc.put(peak.getKey(), centInt);
		}

	}

	@Override
	public void compareTo(Map<Double, Double> inputPeaksTrg) {
		
		// bin target spectrum
		Map<Double, Double> peaksTrg = vect.vectorize(inputPeaksTrg, trafo);
		
		// calculate mean intensity
		double meanTrg = 0.0;
		for (double intensity : peaksTrg.values()) {
			meanTrg += intensity;
		}
		meanTrg /= peaksTrg.size();
		
		// calculate dot product
		double numer = 0.0, denom2 = 0.0;
		for (Entry<Double, Double> peakTrg : peaksTrg.entrySet()) {
			double intenTrg = peakTrg.getValue() - meanTrg;
			Double intenSrc = peaksSrc.get(peakTrg.getKey());
			if (intenSrc != null) {
				numer += intenSrc * intenTrg;
			}
			denom2 += intenTrg * intenTrg;
		}
		
		// normalize score
		this.similarity = numer / Math.sqrt(denom1 * denom2);
	}
	
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
	}

	@Override
	public double getSimilarity() {
		return similarity;
	}

	@Override
	public Map<Double, Double> getSourcePeaks() {
		return peaksSrc;
	}

}
