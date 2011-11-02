package de.mpa.parser.mascot.xml;
import java.util.List;

/* Name:				RobbiesDomParser
 * Last changed:		02.11.2011
 * Author:				Robbie
 * Description:			object representing xml protein hit
 */

public class Proteinhit {

	// class variables
	private int number;
	
	private String accession = "";
	private String description= "";
	private double score = 0.0;
	private double mass = 0.0;
	private List<PeptideHit>  peptideHits;
	
	// methods
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	
	public double getMass() {
		return mass;
	}
	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public List<PeptideHit> getPeptideHits() {
		return peptideHits;
	}
	public void setPeptideHits(List<PeptideHit> peptideHits) {
		this.peptideHits = peptideHits;
	}
	
}
