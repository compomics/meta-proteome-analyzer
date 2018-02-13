package de.mpa.model.dbsearch;

public class PeakQuant {

	private Double mhPlus;
	private Double peakArea;
	
	public PeakQuant(Double mh, Double area) {
		this.mhPlus = mh;
		this.peakArea = area;
	}
	
	public Double getMHPlus() {
		return this.mhPlus;
	}
	
	public Double getArea() {
		return this.peakArea;
	}
	
}
