package de.mpa.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.mpa.interfaces.SpectrumComparator;
import de.mpa.io.Peak;

public class CrossCorrelation implements SpectrumComparator {
	
	// amount of bins into which the peak intensities are consolidated
	private int numBins;

	private int offset = 75;
	
	private double similarity = 0.0; 
	
	public CrossCorrelation(int N) {
		this.numBins = N;
	}

	@Override
	public void compare(ArrayList<Peak> highA, ArrayList<Peak> highB) {
		ArrayList<Double> sA = new ArrayList<Double>();
		ArrayList<Double> sB = new ArrayList<Double>();
		
		if ((highA != null) && (highB != null)) {
			// build intensity vectors
			double min = Math.min(highA.get(0).getMz(), highB.get(0).getMz());
			double max = Math.max(highA.get(highA.size()-1).getMz(), highB.get(highB.size()-1).getMz());
			double width = (max-min)/this.numBins;
			min -= this.offset * width;
			max += this.offset * width;
			
			System.out.println(min + " " + max + " " + width);
			
			for (int i = 1; i <= this.numBins + 2*this.offset; i++) {
				double lBound = min + (i-1) * width;
				double rBound = min +   i   * width;
				double intenA = 0.0;
				for (Peak peak : highA) {
					if (peak.getMz() >= lBound) {
						if  (peak.getMz() < rBound) {
							intenA += peak.getIntensity();
						} else {
							break;
						}
					}
				}
				double intenB = 0.0;
				for (Peak peak : highB) {
					if (peak.getMz() >= lBound) {
						if  (peak.getMz() < rBound) {
							intenB += peak.getIntensity();
						} else {
							break;
						}
					}
				}
				sA.add(Math.sqrt(intenA));
				sB.add(Math.sqrt(intenB));
			}
			
			double res = 0;
			for (int i = 0; i < this.numBins; i++) {
				double y_tau = 0.0;
				for (int tau = -this.offset; tau <= this.offset; tau++) {
					if (tau == 0) {
						continue;
					}
					y_tau += sA.get(i+this.offset+tau);
				}
				double y_prime = sA.get(i+this.offset) - y_tau/(2*this.offset);
				res += sB.get(i+this.offset) * y_prime;
			}
			
			this.similarity = res;
			
		}			
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
