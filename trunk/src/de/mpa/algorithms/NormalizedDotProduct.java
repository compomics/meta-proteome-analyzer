package de.mpa.algorithms;

import java.util.ArrayList;
import java.util.Iterator;

import de.mpa.interfaces.SpectrumComparator;
import de.mpa.io.Peak;

public class NormalizedDotProduct implements SpectrumComparator {
	
	// threshold below which multiple masses are consolidated into one 
	private double threshMz;
	
	private double similarity = 0.0; 
	
	public NormalizedDotProduct(double threshMz) {
		this.threshMz = threshMz;
	}

	@Override
	public void compare(ArrayList<Peak> highA, ArrayList<Peak> highB) {
		ArrayList<Double> sA = new ArrayList<Double>();
		ArrayList<Double> sB = new ArrayList<Double>();
		
		if ((highA != null) && (highB != null)) {
			// build intensity vectors
			for (Peak peakA : highA) {
				sA.add(peakA.getIntensity());
//				sA.add(Math.sqrt(peakA.getIntensity()));	// sqrt-transform to condition Poisson-distributed data
				
				// compare each peak mass of spectrum A to all masses of spectrum B
				// and remove duplicates from the latter
				for (Iterator<Peak> it = highB.iterator(); it.hasNext();) {
				    Peak peakB = it.next();
					if (Math.abs(peakB.getMz()-peakA.getMz()) < this.threshMz) {
						sB.add(peakB.getIntensity());
//						sB.add(Math.sqrt(peakB.getIntensity()));
						it.remove();
						break;
					}
				}
				if (sB.size() < sA.size()) {	// in case no duplicates were found,
					sB.add(0.0);				// add zeros instead
				}
			}
			for (Peak peakB : highB) {
				sA.add(0.0);
				sB.add(peakB.getIntensity());
//				sB.add(Math.sqrt(peakB.getIntensity()));
			}
			
			// compute normalized dot product
			double numer = 0.0, denom1 = 0.0, denom2 = 0.0;
			for (int i = 0; i < sA.size(); i++) {
				numer  += sA.get(i) * sB.get(i);
				denom1 += sA.get(i) * sA.get(i);
				denom2 += sB.get(i) * sB.get(i);
			}
			
			this.similarity = numer / Math.sqrt(denom1 * denom2); 
		}			
	}

	@Override
	public double getSimilarity() {
		return similarity;
	}

	public double getThreshMz() {
		return threshMz;
	}

	public void setThreshMz(double threshMz) {
		this.threshMz = threshMz;
	}

}
