package de.mpa.parser.mascot.xml;
/* Name:				RobbiesDomParser
 * letzte Änderung:		25.10.2011
 * Author:				Robbie
 * Beschreibung:		Objekt in dem PeptideInhalt des XML reingeschrieben wird
 */


public class PeptideHit {

	// Deklarieren der Variablen
	private String description ="";
	private String sequence = "";
	private double mz= 0;
	private int charge = 0;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	

	
	
}
