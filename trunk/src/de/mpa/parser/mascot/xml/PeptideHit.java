package de.mpa.parser.mascot.xml;
/* Name:				RobbiesDomParser
 * Last changed:		02.11.2011
 * Author:				Robbie
 * Description:			object representing xml peptide hit
 */

public class PeptideHit {

	// class variables
	private String scanTitle = "";
	private String description = "";
	private String sequence = "";
	private String proteinAccession = "";
	private double mz = 0.0;
	private int charge = 0;	
	
	// constructors
	public PeptideHit(String proteinAccession) {
		this.proteinAccession = proteinAccession;
	}
	
	// methods
	public String getScanTitle() {
		return scanTitle;
	}
	public void setScanTitle(String title) {
		this.scanTitle = title;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public String getProteinAccession() {
		return proteinAccession;
	}	
	public void setProteinAccession(String proteinAccession) {
		this.proteinAccession = proteinAccession;
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
