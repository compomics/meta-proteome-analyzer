package de.mpa.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import de.mpa.interfaces.SpectrumComparator;
import de.mpa.io.Peak;

public class CrossCorrelation implements SpectrumComparator {
	
	// amount of bins into which the peak intensities are consolidated
	private int numBins;

	// maximum negative and positive m/z offset
	private int offset = 75;
	
	// similarity score
	private double similarity = 0.0;
	
	/**
	 * Class constructor. The amount of bins into which peaks will be consolidated will be determined automatically using 1 Da intervals when calling compare().
	 */
	public CrossCorrelation() {
		this(0);
	}
	
	/**
	 * Class constructor.
	 * @param N int defining the amount of bins to consolidate peaks into. A value of 0 causes the amount of bins to be determined automatically using 1 Da intervals.
	 */
	public CrossCorrelation(int N) {
		this.numBins = N;
	}

	@Override
	public void compare(ArrayList<Peak> peaksA, ArrayList<Peak> peaksB) {
		int numBins = this.numBins;
		
		// build intensity vectors
		ArrayList<Double> sA = new ArrayList<Double>();
		ArrayList<Double> sB = new ArrayList<Double>();
		
		if ((peaksA != null) && (peaksB != null)) {
			// determine normalization factors
			double maxIntenA = 0.0, maxIntenB = 0.0;
			for (Peak peakA : peaksA) {
				if (peakA.getIntensity() > maxIntenA) { maxIntenA = peakA.getIntensity(); }
			}
			for (Peak peakB : peaksB) {
				if (peakB.getIntensity() > maxIntenB) { maxIntenB = peakB.getIntensity(); }
			}
			
			// left boundary, rounded down to nearest Da
			double min = Math.floor(Math.min(peaksA.get(0).getMz(), peaksB.get(0).getMz()));
			// right boundary, rounded up to nearest Da
			double max = Math.ceil(Math.max(peaksA.get(peaksA.size()-1).getMz(), peaksB.get(peaksB.size()-1).getMz()));
			// width of a single bin
			double width = 1.0;	// Da
			// auto-determine bins if necessary
			if (numBins == 0) {
				numBins = (int) (max-min);
			} else {
				width = (max-min)/numBins;
			}
			// add additional bins to each end
			min -= this.offset * width;
			max += this.offset * width;
			
//			System.out.println(min + " " + max + " " + width);
			
			// iterate over bins and sum peaks within bounds (direct binning)
			for (int i = 1; i <= numBins + 2*this.offset; i++) {
				double lBound = min + (i-1) * width;
				double rBound = min +   i   * width;
				double intenA = 0.0;
				// iterate through all peaks and check whether they belong in the current bin
				for (Peak peak : peaksA) {
					if (peak.getMz() >= lBound) {
						if  (peak.getMz() < rBound) {
							intenA += peak.getIntensity();
						} else {
							break;
						}
					}
				}
				double intenB = 0.0;
				for (Peak peak : peaksB) {
					if (peak.getMz() >= lBound) {
						if  (peak.getMz() < rBound) {
							intenB += peak.getIntensity();
						} else {
							break;
						}
					}
				}
				// normalization and square root transform for increased sensitivity
				sA.add(Math.sqrt(intenA/maxIntenA));
				sB.add(Math.sqrt(intenB/maxIntenB));
			}
			
			// apply cross correlation processing and compute (normalized) dot product
			double numer = 0.0, denom1 = 0.0, denom2 = 0.0;
			for (int i = 0; i < numBins; i++) {
				// transform intensity vector A element-wise
				double y_tau = 0.0;
				for (int tau = -this.offset; tau <= this.offset; tau++) {
					if (tau == 0) {	// skip non-offset spectrum
						continue;
					}
					y_tau += sA.get(i+this.offset+tau);
				}
				double y_prime = sA.get(i+this.offset) - y_tau/(2*this.offset);
				// normalized dot prod parts
				numer  += sB.get(i+this.offset) * y_prime;
				denom1 += sB.get(i+this.offset) * sB.get(i+this.offset);
				denom2 += y_prime * y_prime;
			}
			
			this.similarity = numer;	// non-normalized
//			this.similarity = numer / Math.sqrt(denom1 * denom2); 
			
		}			
	}

	public void compare(HashMap<Double, Double> peaksA,
						HashMap<Double, Double> peaksB) {
		// determine normalization factors
		TreeSet<Double> temp;
		// sort intensities to find maximum
		temp = new TreeSet<Double>(peaksA.values());
		double maxIntenA = temp.last();
		temp = new TreeSet<Double>(peaksB.values());
		double maxIntenB = temp.last();
		
		// consolidate peaks into single map containing arrays of 2 intensities as values
		HashMap<Double, double[]> peaks = new HashMap<Double, double[]>();
		// iterate over first peak map
		for (double mz : peaksA.keySet()) {
			double roundedMz = Math.floor(mz);
			double[] intensities = peaks.get(roundedMz);
			if (intensities == null) {	// intensity list does not exist yet, therefore create new one
				intensities = new double[] { peaksA.get(mz)/maxIntenA, 0.0 };
			} else {	// intensity list already exists, therefore add new intensity to it
				intensities[0] += peaksA.get(mz)/maxIntenA;
			}
			peaks.put(roundedMz, intensities);
		}
		// iterate over second peak map
		for (double mz : peaksB.keySet()) {
			double roundedMz = Math.floor(mz);
			double[] intensities = peaks.get(roundedMz);
			if (intensities == null) {	// intensity list does not exist yet, therefore create new one
				intensities = new double[] { 0.0, peaksB.get(mz)/maxIntenB };
			} else {	// intensity list already exists, therefore add new intensity to it
				intensities[1] += peaksB.get(mz)/maxIntenB;
			}
			peaks.put(roundedMz, intensities);
		}
		
		// apply square root transform
		for (double[] intensities : peaks.values()) {
			intensities[0] = Math.sqrt(intensities[0]);
			intensities[1] = Math.sqrt(intensities[1]);
		}
		
		// apply cross correlation processing and compute (normalized?) dot product
		double numer = 0.0;
//		double denom1 = 0.0, denom2 = 0.0;
		for (double mz : peaks.keySet()) {
			// transform intensities from first map element-wise
			double y_tau = 0.0;
			for (int tau = -this.offset; tau <= this.offset; tau++) {
				if ((!peaks.containsKey(mz + tau)) || (tau == 0)) {
					continue;	// skip missing or non-offset masses
				}
				y_tau += peaks.get(mz + tau)[0];
			}
			double y_prime = peaks.get(mz)[0] - y_tau/(2*this.offset);
			// dot prod parts
			numer  += peaks.get(mz)[1] * y_prime;
//			denom1 += peaks.get(mz)[1] * peaks.get(mz)[1];
//			denom2 += y_prime * y_prime;
		}
		
		this.similarity = numer;	// non-normalized
//		this.similarity = numer / Math.sqrt(denom1 * denom2); 
	}

	@Override
	public double getSimilarity() {
		return similarity;
	}

	public double getNumBins() {
		return numBins;
	}

	public void setNumBins(int numBins) {
		this.numBins = numBins;
	}

}
