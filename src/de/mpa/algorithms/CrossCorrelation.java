package de.mpa.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.mpa.interfaces.SpectrumComparator;

public class CrossCorrelation implements SpectrumComparator {
	
	/**
	 * The input vectorization method.
	 */
	private Vectorization vect;
	
	/**
	 * The input transformation method.
	 */
	private Transformation trafo;

	/**
	 * The amount of neighboring bins that are evaluated (in both positive and negative m/z direction) during correlation.
	 */
	private int offsets;
	
	/**
	 * The peak map of the source spectrum which gets auto-correlated during preparation.
	 */
	private Map<Double,Double> peaksSrc;
	
	/**
	 * The auto-correlation score of the source spectrum. Used for normalization purposes.
	 */
	private double autoCorr;
	
	/**
	 * The similarity score between source spectrum and target spectrum.
	 * Ranges between 0.0 (negative scores are cut off) and around 1.0
	 * (higher scores are possible due to poor auto-correlation of the source spectrum).
	 */
	private double similarity;
	
	public CrossCorrelation(Vectorization vect, Transformation trafo, int offsets) {
		this.vect = vect;
		this.trafo = trafo;
		this.offsets = offsets;
	}

	@Override
	public void prepare(Map<Double, Double> inputPeaksSrc) {
		
		// TODO: make adjustments for peak matching method, if possible
		
		// vectorize input source spectrum
		Map<Double, Double> vectPeaksSrc = vect.vectorize(inputPeaksSrc, trafo);
		
		peaksSrc = new HashMap<Double, Double>(vectPeaksSrc.size());
		
		// determine source spectrum magnitude
		double magSrc = 0.0;
		for (double intenSrc : vectPeaksSrc.values())
			magSrc += intenSrc * intenSrc;
		magSrc = Math.sqrt(magSrc);
		
		// apply cross-correlation transformation
		for (Entry<Double, Double> peakSrc : vectPeaksSrc.entrySet()) {
			// normalize intensity
			double intenSrc = peakSrc.getValue() / magSrc;
			// apply cross-correlation processing and store resulting peaks
			for (int tau = -offsets; tau <= offsets; tau++) {
				if (tau == 0) {
					peaksSrc.put(peakSrc.getKey(), intenSrc);
				} else {
					double offsetMz = peakSrc.getKey() + tau*vect.getBinWidth();
					Double existingInten = peaksSrc.get(offsetMz);
					if (existingInten == null) existingInten = 0.0;
					peaksSrc.put(offsetMz, existingInten - intenSrc/(2*offsets));
				}
			}
		}
		
		// determine auto-correlation (squared magnitude, essentially)
		autoCorr = 0.0;
		for (double intenSrc : peaksSrc.values()) {
			autoCorr += intenSrc * intenSrc; 
		}
	}
	
	@Override
	public void compareTo(Map<Double, Double> inputPeaksTrg) {
		
		Map<Double, Double> peaksTrg = vect.vectorize(inputPeaksTrg, trafo);
		
		// determine target spectrum magnitude
		double magTrg = 0.0;
		for (double intenTrg : peaksTrg.values())
			magTrg += intenTrg * intenTrg;
		magTrg = Math.sqrt(magTrg);
		
		// calculate dot product
		double numer = 0.0;
		for (Entry<Double, Double> peakTrg : peaksTrg.entrySet()) {
			Double intenSrc = peaksSrc.get(peakTrg.getKey());
			if (intenSrc != null)
				numer += intenSrc * (peakTrg.getValue() / magTrg);
		}
		
		// normalize score using auto-correlation
		double similarity = numer / autoCorr;
		this.similarity = (similarity > 0.0) ? similarity : 0.0;	// cut off negative scores
	}

	@Override
	public void cleanup() {
		this.vect.setInput(null);
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
