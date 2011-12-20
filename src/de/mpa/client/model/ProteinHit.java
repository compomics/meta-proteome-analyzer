package de.mpa.client.model;

import java.util.List;

public class ProteinHit {
	
	private String accession;
	private String description;
	private int start;
	private int end;
	private double coverage;
	private int specCount;
	private double nSAF;
	private List<PeptideHit> peptideHits;
	
	
	public ProteinHit(String accession){
		this.accession = accession;
	}
	/**
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}
	/**
	 * @param accession the accession to set
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	/**
	 * @return the coverage
	 */
	public double getCoverage() {
		return coverage;
	}
	/**
	 * @param coverage the coverage to set
	 */
	public void setCoverage(double coverage) {
		this.coverage = coverage;
	}
	/**
	 * @return the specCount
	 */
	public int getSpecCount() {
		return specCount;
	}
	/**
	 * @param specCount the specCount to set
	 */
	public void setSpecCount(int specCount) {
		this.specCount = specCount;
	}
	/**
	 * @return the nSAF
	 */
	public double getNSAF() {
		return nSAF;
	}
	/**
	 * @param nSAF the nSAF to set
	 */
	public void setNSAF(double nSAF) {
		this.nSAF = nSAF;
	}
	/**
	 * @return the peptideHits
	 */
	public List<PeptideHit> getPeptideHits() {
		return peptideHits;
	}
	/**
	 * @param peptideHits the peptideHits to set
	 */
	public void setPeptideHits(List<PeptideHit> peptideHits) {
		this.peptideHits = peptideHits;
	}
	
	
}
