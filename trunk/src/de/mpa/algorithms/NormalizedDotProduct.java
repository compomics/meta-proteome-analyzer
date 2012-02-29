package de.mpa.algorithms;

import java.util.HashMap;
import java.util.Map;

import de.mpa.interfaces.SpectrumComparator;

public class NormalizedDotProduct implements SpectrumComparator {
	
	/**
	 * The bin width into which spectrum intensities are consolidated into during vectorization.
	 */
	private double binWidth;
	
	/**
	 * The amount the bin centers are shifted along the m/z axis.
	 */
	private double binShift;
	
	/**
	 * The input transformation method.
	 */
	private Trafo trafo;

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
	 * Default class constructor. Bin width and shift default to 1.0 Da and \u00b10.0 Da respectively.
	 */
	public NormalizedDotProduct() {
		this(1.0, 0.0, new Trafo() { public double transform(double input) { return input; } });
	}
	
	/**
	 * Class constructor.
	 * @param binWidth The bin width into which spectrum intensities are consolidated into during vectorization.
	 * @param binShift The amount the bin centers are shifted along the m/z axis.
	 */
	public NormalizedDotProduct(double binWidth, double binShift, Trafo trafo) {
		this.binWidth = binWidth;
		this.binShift = binShift;
		this.trafo = trafo;
	}

	@Override
	public void prepare(Map<Double, Double> inputPeaksSrc) {

		peaksSrc = new HashMap<Double, Double>(inputPeaksSrc.size());

		// bin source spectrum
		for (Double mzSrc : inputPeaksSrc.keySet()) {
			// round peak mass to nearest bin center
			double roundedMz = Math.round((mzSrc-binShift)/binWidth)*binWidth + binShift;
			// transform peak intensity
			double intenSrc = trafo.transform(inputPeaksSrc.get(mzSrc));
			if (peaksSrc.containsKey(roundedMz)) { intenSrc += peaksSrc.get(roundedMz); }	// add to already existing bin
			// store transformed peak
			peaksSrc.put(roundedMz, intenSrc);
		}
		
	}
	
	@Override
	public void compareTo(Map<Double, Double> inputPeaksTrg) {
		
		HashMap<Double, Double> peaksTrg = new HashMap<Double, Double>(inputPeaksTrg.size());
		
		// bin target spectrum
		for (Double mzTrg : inputPeaksTrg.keySet()) {
			// round peak mass to nearest bin center
			double roundedMz = Math.round((mzTrg-binShift)/binWidth)*binWidth + binShift;
			// transform peak intensity
			double intenTrg = trafo.transform(inputPeaksTrg.get(mzTrg));
			if (peaksTrg.containsKey(roundedMz)) { intenTrg += peaksTrg.get(roundedMz); }	// add to already existing bin
			// store transformed peak
			peaksTrg.put(roundedMz, intenTrg);
		}
		
		// calculate dot product
		double numer = 0.0, denom1 = 0.0, denom2 = 0.0;
		for (double mzTrg : peaksTrg.keySet()) {
			double intenTrg = peaksTrg.get(mzTrg);
			Double intenSrc = peaksSrc.get(mzTrg);
			if (intenSrc != null) {
				numer  += intenSrc * intenTrg;
				denom1 += intenSrc * intenSrc;
			}
			denom2 += intenTrg * intenTrg;
		}
		
//		for (double mzTrg : inputPeaksTrg.keySet()) {
//			double roundedMz = Math.round((mzTrg-binShift)/binWidth)*binWidth + binShift;
//		}
		
		// normalize score
		this.similarity = numer / Math.sqrt(denom1 * denom2);
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
