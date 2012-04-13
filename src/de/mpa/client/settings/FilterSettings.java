package de.mpa.client.settings;

public class FilterSettings {

	private int minPeaks;
	private double minTIC;
	private double minSNR;
	private double noiseLvl;
		
	public FilterSettings(int minPeaks, double minTIC, double minSNR, double noiseLvl) {
		this.minPeaks = minPeaks;
		this.minTIC = minTIC;
		this.minSNR = minSNR;
		this.noiseLvl = noiseLvl;
	}
	
	public int getMinPeaks() {
		return minPeaks;
	}
	public void setMinPeaks(int minPeaks) {
		this.minPeaks = minPeaks;
	}
	
	public double getMinTIC() {
		return minTIC;
	}
	public void setMinTIC(double minTIC) {
		this.minTIC = minTIC;
	}
	
	public double getMinSNR() {
		return minSNR;
	}
	public void setMinSNR(double minSNR) {
		this.minSNR = minSNR;
	}
	
	public double getNoiseLvl() {
		return noiseLvl;
	}
	public void setNoiseLvl(double noiseLvl) {
		this.noiseLvl = noiseLvl;
	}
	
}
