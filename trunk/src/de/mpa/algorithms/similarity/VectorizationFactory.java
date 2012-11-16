package de.mpa.algorithms.similarity;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Factory class providing access to various vectorization method implementations.
 * 
 * @author A. Behne
 */
public abstract class VectorizationFactory {

	/**
	 * Creates a vectorization method that tries to match peak positions of
	 * provided target inputs to those of a reference input inside the specified
	 * mass delta window.<br>
	 * Call <code>vectorize()</code> after creation to specify the reference
	 * input. Further calls to this method will modify input peak positions to
	 * those of peaks inside the reference provided they fall within the mass
	 * delta window.<br>
	 * Call <code>cleanup()</code> to flush the reference from memory and to be
	 * able to specify a new one again.
	 * 
	 * @param delta the mass delta window
	 * @return the peak matching vectorization method
	 */
	public static Vectorization createPeakMatching(final double delta) {
		return new Vectorization() {
			private Map<Double, Double> inputA;
			@Override
			public Map<Double, Double> vectorize(Map<Double, Double> input,
					Transformation trafo) {
				Map<Double, Double> output;
				if (inputA == null) {
					inputA = input;
					output = input;
				} else {
					TreeMap<Double, Double> inputB = new TreeMap<Double, Double>(input);
					double maxDelta = delta/2.0;
					
					for (Double newKey : this.inputA.keySet()) {
						// skip if key already exists
						if (inputB.containsKey(newKey)) continue;
						// find nearest keys and calculate their deltas
						Double loKey = inputB.lowerKey(newKey);
						double loDelta = (loKey != null) ? newKey-loKey : Double.MAX_VALUE;
						Double hiKey = inputB.higherKey(newKey);
						double hiDelta = (hiKey != null) ? hiKey-newKey : Double.MAX_VALUE;
						// evaluate deltas
						Double oldKey;
						if ((loDelta <= maxDelta) && (loDelta <= hiDelta)) {
							// left key inside range and closer (or equally close)
							oldKey = loKey;
						} else if ((hiDelta <= maxDelta) && (hiDelta < loDelta)) {
							// right key inside range and closer
							oldKey = hiKey;
						} else {
							// both keys outside range
							continue;
						}
						
						// change key of entry
						inputB.put(newKey, inputB.get(oldKey));
						inputB.remove(oldKey);
					}
					
					output = inputB;
				}
				// transform output values
				for (Entry<Double, Double> entry : output.entrySet()) {
					output.put(entry.getKey(), trafo.transform(entry.getValue()));
				}
				return output;
			}
			@Override
			public void cleanup() {
				inputA = null;
			}
		};
	}

	/**
	 * Creates a vectorization method that will replace spectrum peak map
	 * position keys which fall into intervals defined by the specified
	 * parameters with the central value of these intervals. Peaks that share
	 * the same bin will be merged, meaning that their intensitie values will be
	 * summed.
	 * @param binWidth The width of the bins.
	 * @param binShift The shift of the bin boundaries along the position axis.
	 * @return the direct binning vectorization method
	 */
	public static Vectorization createDirectBinning(final double binWidth, final double binShift) {
		return new Vectorization() {
			@Override
			public Map<Double, Double> vectorize(Map<Double, Double> input,
					Transformation trafo) {
				Map<Double, Double> output = new HashMap<Double, Double>(input.size());
				for (Entry<Double, Double> entry : input.entrySet()) {
					// round key to nearest bin center
					double roundedKey = Math.round((entry.getKey()-binShift)/binWidth)*binWidth + binShift;
					// grab input value
					double val = entry.getValue();
					if (output.containsKey(roundedKey)) 
						val += output.get(roundedKey);	// add to already existing bin
					// store value in output
					output.put(roundedKey, val);
				}
				// transform output values
				for (Entry<Double, Double> entry : output.entrySet()) {
					output.put(entry.getKey(), trafo.transform(entry.getValue()));
				}
				return output;
			}
			@Override
			public void cleanup() {}
		};
	}

	/**
	 * Creates a vectorization method that will replace spectrum peak map
	 * position keys which fall into intervals defined by the specified
	 * parameters with the central value of these intervals. Peak intensities
	 * will be distributed among neighboring bins according to their specified
	 * profile shape and base width parameters.
	 * @param binWidth The width of the bins.
	 * @param binShift The shift of the bin boundaries along the position axis.
	 * @param profShape The profile shape index. <i>(so far unused, will be 0 for triangular, 1 for gaussian, etc.)</i>
	 * @param baseWidth The base width of the profile shape.
	 * @return the direct binning vectorization method
	 */
	public static Vectorization createProfiling(final double binWidth, final double binShift,
			final int profShape, final double baseWidth) {
		return new Vectorization() {
			@Override
			public Map<Double, Double> vectorize(Map<Double, Double> input,
					Transformation trafo) {
				Map<Double, Double> output = new HashMap<Double, Double>(input.size());
				for (Entry<Double, Double> entry : input.entrySet()) {
					double ky = entry.getKey(), 				// original key
						   lb = ky - baseWidth/2,				// left boundary
						   rb,									// right boundary
						   lr = lb,								// left root
						   rr = ky + baseWidth/2,				// right root
						   h2 = 4*entry.getValue()/baseWidth,	// doubled area height
						   l2 = h2/baseWidth / 2,				// halved area slope
						   rk = Math.round((lb - binShift)/binWidth)*binWidth + binShift,	// rounded key
						   vl;									// transformed value
					while (true) {
						// calculate right boundary
						rb = rk + binWidth/2;
						// reset right boundary if it exceeds either the original key or the right root
						rb = (rb > rr) ? rr : ( ((lb < ky) && (rb > ky)) ? ky : rb );
						// calculate trapezoid area between boundaries
						vl = l2*(lb + rb - 2*lr);
						vl = (rb <= ky) ? vl*(rb-lb) : (h2-vl)*(rb-lb);
						// store calculated value
						if (output.containsKey(rk)) 
							vl += output.get(rk);
						output.put(rk, vl);
						// right boundary becomes left boundary in next iteration
						lb = rb;
						// abort if right root has been reached
						if (lb >= rr)
							break;
						// find nearest bin center
						rk = Math.ceil((lb - binShift)/binWidth)*binWidth + binShift;
					}
				}
				// transform output values
				for (Entry<Double, Double> entry : output.entrySet()) {
					output.put(entry.getKey(), trafo.transform(entry.getValue()));
				}
				return output;
			}
			@Override
			public void cleanup() {}
		};
	}

}
