package de.mpa.io.parser.mzid;

import java.io.Serializable;

import de.mpa.client.model.SearchHit;
import de.mpa.client.model.dbsearch.SearchEngineType;

public class MzidHit implements SearchHit, Serializable {
	
	/**
	 * Default serialization ID.
	 */
	private static final long serialVersionUID = 1L;
	private SearchEngineType type;
	private String peptideSequence;
	private String accession;
	private String proteinSequence;
	private String proteinDescription;
	private long spectrumId;
	private String spectrumFilename;
	private String spectrumTitle;
	private double score;
	private double qValue;
	private int charge;
	
	@Override
	public SearchEngineType getType() {
		return type;
	}

	@Override
	public String getPeptideSequence() {
		return peptideSequence;
	}

	@Override
	public String getAccession() {
		return accession;
	}

	@Override
	public double getQvalue() {
		return qValue;
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public String getProteinSequence() {
		return proteinSequence;
	}

	@Override
	public long getSpectrumId() {
		return spectrumId;
	}
	
	@Override
	public String getProteinDescription() {
		return proteinDescription;
	}

	@Override
	public String getSpectrumFilename() {
		return spectrumFilename;
	}

	@Override
	public String getSpectrumTitle() {
		return spectrumTitle;
	}

	@Override
	public int getCharge() {
		return charge;
	}

	public void setType(SearchEngineType type) {
		this.type = type;
	}

	public void setPeptideSequence(String peptideSequence) {
		this.peptideSequence = peptideSequence;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public void setProteinSequence(String proteinSequence) {
		this.proteinSequence = proteinSequence;
	}

	public void setProteinDescription(String proteinDescription) {
		this.proteinDescription = proteinDescription;
	}

	public void setSpectrumId(long spectrumId) {
		this.spectrumId = spectrumId;
	}

	public void setSpectrumFilename(String spectrumFilename) {
		this.spectrumFilename = spectrumFilename;
	}

	public void setSpectrumTitle(String spectrumTitle) {
		this.spectrumTitle = spectrumTitle;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	public void setQValue(double qValue) {
		this.qValue = qValue;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}
	
	public String toString() {
		return peptideSequence + ";" + charge + ";" + score + ";" + qValue + ";" + accession + ";"  + proteinDescription + ";" + proteinSequence;
	}
}
