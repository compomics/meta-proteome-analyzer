package de.mpa.algorithms;

import java.util.HashMap;
import java.util.Map;

import de.mpa.interfaces.SpectrumComparator;

public class CrossCorrelation implements SpectrumComparator {
	
	// TODO: re-normalize after binning?
	
	/**
	 * The bin width into which spectrum intensities are consolidated into during vectorization.
	 */
	private double binWidth;

	/**
	 * The amount the bin centers are shifted along the m/z axis.
	 */
	private double binShift;
	
	/**
	 * The amount of neighboring bins that are evaluated (in both positive and negative m/z direction) during correlation.
	 */
	private int offsets;
	
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
	 * The auto-correlation score of the source spectrum. Used for normalization purposes.
	 */
	private double autoCorr = 1.0;
	
	/**
	 * The similarity score between source spectrum and target spectrum.
	 * Ranges between 0.0 (negative scores are cut off) and around 1.0
	 * (higher scores are possible due to poor auto-correlation of the source spectrum).
	 */
	private double similarity = 0.0;
	
	/**
	 * Default class constructor. Bin width and shift default to 1.0 Da and \u00b10.0 Da respectively.
	 * The algorithm will evaluate the neighboring 75 bins for auto- and cross-correlation.
	 */
	public CrossCorrelation() {
		this(1.0, 0.0, 75, new Transformation() { public double transform(double input) { return input; } });
	}
	
	/**
	 * Class constructor.
	 * @param binWidth The bin width into which spectrum intensities are consolidated into during vectorization.
	 * @param binShift The amount the bin centers are shifted along the m/z axis.
	 * @param offsets The amount of neighboring bins that are evaluated (in either direction) during correlation.
	 */
	public CrossCorrelation(double binWidth, double binShift, int offsets, Transformation trafo) {
		this.binWidth = binWidth;
		this.binShift = binShift;
		this.offsets = offsets;
		this.trafo = trafo;
	}
	
	
	public CrossCorrelation(Vectorization vect, Transformation trafo) {
		this.vect = vect;
		this.trafo = trafo;
	}

	@Override
	public void prepare(Map<Double, Double> inputPeaksSrc) {
		// TODO: implement vectorization, make adjustments for peak matching method
		peaksSrc = new HashMap<Double, Double>(inputPeaksSrc.size());

		// bin source spectrum
		double maxIntenSrc = 0.0;	// maximum intensity, to be used as normalization factor later on
		for (Double mzSrc : inputPeaksSrc.keySet()) {
			// round peak mass to nearest bin center
			double roundedMz = Math.round((mzSrc-binShift)/binWidth)*binWidth + binShift;
			// transform peak intensity
			double intenSrc = trafo.transform(inputPeaksSrc.get(mzSrc));
			// apply cross-correlation processing and store resulting peaks
			for (int tau = -offsets; tau <= offsets; tau++) {
				double offsetMz = roundedMz + tau*binWidth;
				if (peaksSrc.containsKey(offsetMz)) {
					if (tau == 0) {		// add to central bin
						intenSrc += peaksSrc.get(offsetMz);
						peaksSrc.put(offsetMz, intenSrc);
						maxIntenSrc = (intenSrc > maxIntenSrc) ? intenSrc : maxIntenSrc;
					} else {			// substract from surrounding bins
						peaksSrc.put(offsetMz, peaksSrc.get(offsetMz) - intenSrc/(2*offsets));
					}
				} else {
					if (tau == 0) {
						peaksSrc.put(offsetMz, intenSrc);
						maxIntenSrc = (intenSrc > maxIntenSrc) ? intenSrc : maxIntenSrc;
					} else {
						peaksSrc.put(offsetMz, -intenSrc/(2*offsets));
					}
				}
			}
		}
		// normalize source spectrum
		for (double mzSrc : peaksSrc.keySet()) { peaksSrc.put(mzSrc, peaksSrc.get(mzSrc)/maxIntenSrc); }
		
		autoCorr = 0.0;
		for (Double mzSrc : peaksSrc.keySet()) {
			double intenSrc = peaksSrc.get(mzSrc);
			autoCorr += intenSrc * intenSrc; 
		}
	}
	
	@Override
	public void compareTo(Map<Double, Double> inputPeaksTrg) {
		
		HashMap<Double, Double> peaksTrg = new HashMap<Double, Double>(inputPeaksTrg.size());
		
		// bin target spectrum
		double maxIntenTrg = 0.0;	// maximum intensity, to be used as normalization factor later on
		for (Double mzTrg : inputPeaksTrg.keySet()) {
			// round peak mass to nearest bin center
			double roundedMz = Math.round((mzTrg-binShift)/binWidth)*binWidth + binShift;
			// transform peak intensity
			double intenTrg = trafo.transform(inputPeaksTrg.get(mzTrg));
			if (peaksTrg.containsKey(roundedMz)) { intenTrg += peaksTrg.get(roundedMz); }	// add to already existing bin
			maxIntenTrg = (intenTrg > maxIntenTrg) ? intenTrg : maxIntenTrg;
			// store transformed peak
			peaksTrg.put(roundedMz, intenTrg);
		}
		// normalize target spectrum
		for (double mzTrg : peaksTrg.keySet()) { peaksTrg.put(mzTrg, peaksTrg.get(mzTrg)/maxIntenTrg); }
		
		// calculate dot product
		double numer = 0.0;
		for (double mzTrg : peaksTrg.keySet()) {
			Double intenSrc = peaksSrc.get(mzTrg);
			if (intenSrc != null) {
				numer += intenSrc * peaksTrg.get(mzTrg);
			}
		}
		
		// normalize score using auto-correlation
		double similarity = numer/autoCorr;
		this.similarity = (similarity > 0.0) ? similarity : 0.0;	// cut off negative scores, reset to zero
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
