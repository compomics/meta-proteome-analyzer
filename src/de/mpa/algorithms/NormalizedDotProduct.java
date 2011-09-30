package de.mpa.algorithms;

import java.util.ArrayList;
import java.util.Iterator;

import de.mpa.interfaces.SpectrumComparator;
import de.mpa.io.Peak;

public class NormalizedDotProduct implements SpectrumComparator {
	
	private double deltaMz;
	private double similarity; 
	
	public NormalizedDotProduct(double deltaMz) {
		this.deltaMz = deltaMz;
	}

	@Override
	public void compare(ArrayList<Peak> highA, ArrayList<Peak> highB) {
		ArrayList<Double> sA = new ArrayList<Double>();
		ArrayList<Double> sB = new ArrayList<Double>();
		
		if ((highA != null) && (highB != null)) {
			for (Peak peakA : highA) {
				sA.add(peakA.getIntensity());
				
				for (Iterator<Peak> it = highB.iterator(); it.hasNext();) {
				    Peak peakB = it.next();
					if (Math.abs(peakB.getMz()-peakA.getMz()) < this.deltaMz) {
						sB.add(peakB.getIntensity());
						it.remove();
						break;
					}
				}
				if (sB.size() < sA.size()) {
					sB.add(0.0);	
				}
			}
			for (Peak peakB : highB) {
				sA.add(0.0);
				sB.add(peakB.getIntensity());
			}
			
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

	public double getDeltaMz() {
		return deltaMz;
	}

	public void setDeltaMz(double deltaMz) {
		this.deltaMz = deltaMz;
	}

}
