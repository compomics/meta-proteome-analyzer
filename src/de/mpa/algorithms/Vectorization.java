package de.mpa.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Class to store vectorization-specific settings and methods.
 * 
 * @author behne
 */
public class Vectorization {

	public final static int PEAK_MATCHING = 0;
	public final static int DIRECT_BINNING = 1;
	public final static int PROFILING = 2;

	private int vectType;
	private double binWidth;
	private double binShift;
	
	public final static int LINEAR = 0;
	public final static int GAUSSIAN = 1;
	
	private int profShape;
	private double baseWidth;
	
	private Map<Double, Double> input = null;
	
	public Vectorization(int vectType, double binWidth) {
		this(vectType, binWidth, 0.0, 0, 0.0);
	}
	
	public Vectorization(int vectType, double binWidth, double binShift) {
		this(vectType, binWidth, binShift, 0, 0.0);
	}
	
	public Vectorization(int vectType, double binWidth, double binShift,
			int profShape, double baseWidth) {
		this.vectType = vectType;
		this.binWidth = binWidth;
		this.binShift = binShift;
		this.profShape = profShape;
		this.baseWidth = baseWidth;
	}
	
	public Map<Double, Double> vectorize(Map<Double, Double> input, Transformation trafo) {
		Map<Double, Double> output = null;
		switch (vectType) {
		case DIRECT_BINNING:
			output = new HashMap<Double, Double>(input.size());
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
			break;
		case PROFILING:
			output = new HashMap<Double, Double>(input.size());
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
			break;
		case PEAK_MATCHING:
			double maxDelta = this.binWidth/2.0;
			if (this.input == null) {
				this.input = input;
				output = input;
			} else {
				TreeMap<Double, Double> input2 = new TreeMap<Double, Double>(input);
				
				for (Double newKey : this.input.keySet()) {
					// skip if key already exists
					if (input2.containsKey(newKey)) continue;
					// find nearest keys and calculate their deltas
					Double loKey = input2.lowerKey(newKey);
					double loDelta = (loKey != null) ? newKey-loKey : Double.MAX_VALUE;
					Double hiKey = input2.higherKey(newKey);
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
					input2.put(newKey, input2.get(oldKey));
					input2.remove(oldKey);
				}
				
				output = input2;
			}
			// transform output values
			for (Entry<Double, Double> entry : output.entrySet()) {
				output.put(entry.getKey(), trafo.transform(entry.getValue()));
			}
			break;
		}
		return output;
	}

	/**
	 * @return the binWidth
	 */
	public double getBinWidth() {
		return binWidth;
	}
	/**
	 * @return the binShift
	 */
	public double getBinShift() {
		return binShift;
	}
	/**
	 * @return the profShape
	 */
	public int getProfileShape() {
		return profShape;
	}
	/**
	 * @return the baseWidth
	 */
	public double getBaseWidth() {
		return baseWidth;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(Map<Double, Double> input) {
		this.input = input;
	}

}
