package de.mpa.parser.mascot.xml;

import java.util.Map;

public class PeptideHit {

	// class variables
	private String scanTitle = "";
	private Map<String, String> attributes;
	private String sequence = "";
	private ProteinHit parentProteinHit;
	private double mz = 0.0;
	private int charge = 0;

	// constructors
	public PeptideHit(ProteinHit parentProteinHit) {
		this.setParentProteinHit(parentProteinHit);
	}
	
	// methods
	public String getScanTitle() {
		return scanTitle;
	}
	public void setScanTitle(String title) {
		this.scanTitle = title;
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public ProteinHit getParentProteinHit() {
		return parentProteinHit;
	}	
	public void setParentProteinHit(ProteinHit parentProteinHit) {
		this.parentProteinHit = parentProteinHit;
	}
	
	public double getMz() {
		return mz;
	}
	public void setMz(double mz) {
		this.mz = mz;
	}
	
	public int getCharge() {
		return charge;
	}
	public void setCharge(int charge) {
		this.charge = charge;
	}
	
}
