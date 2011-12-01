package de.mpa.parser.mascot.xml;
import java.util.List;

/* Name:				RobbiesDomParser
 * Last changed:		02.11.2011
 * Author:				Robbie
 * Description:			object representing xml protein hit
 */

public class ProteinHit {

	// class variables
	private int hitNumber;
	
	private List<String> accessions;
	private List<String> descriptions;
	private List<Double> scores;
	private List<Double> masses;
	private List<PeptideHit> peptideHits;
	
	// methods
	public int getHitNumber() {
		return hitNumber;
	}
	public void setHitNumber(int hitNumber) {
		this.hitNumber = hitNumber;
	}
	
	public List<String> getAccessions() {
		return accessions;
	}
	public void setAccessions(List<String> accessions) {
		this.accessions = accessions;
	}
	
	public List<String> getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}
	
	public List<Double> getScores() {
		return scores;
	}
	public void setScores(List<Double> score) {
		this.scores = score;
	}
	
	public List<Double> getMasses() {
		return masses;
	}
	public void setMasses(List<Double> masses) {
		this.masses = masses;
	}
	
	public List<PeptideHit> getPeptideHits() {
		return peptideHits;
	}
	public void setPeptideHits(List<PeptideHit> peptideHits) {
		this.peptideHits = peptideHits;
	}
	
}
