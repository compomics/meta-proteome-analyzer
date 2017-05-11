package de.mpa.io.parser.xtandem;

import java.io.Serializable;

import de.mpa.client.model.SearchHit;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.parser.comet.CometHit;

public class XTandemHit implements SearchHit, Serializable {

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
	private int charge;
	private double qValue;
	private double pep;
	
	public XTandemHit() {}
	
	public XTandemHit(SearchEngineType type, String peptideSequence, String accession, String proteinSequence,
			String proteinDescription, long spectrumId, String spectrumFilename, String spectrumTitle, double score,
			int charge, double qValue, double pep) {
		this.type = type;
		this.peptideSequence = peptideSequence;
		this.accession = accession;
		this.proteinSequence = proteinSequence;
		this.proteinDescription = proteinDescription;
		this.spectrumId = spectrumId;
		this.spectrumFilename = spectrumFilename;
		this.spectrumTitle = spectrumTitle;
		this.score = score;
		this.charge = charge;
		this.qValue = qValue;
		this.pep = pep;
	}
	
	/**
	 * Copy constructor.
	 */
	public XTandemHit(XTandemHit hit) {
		this(hit.getType(), hit.getPeptideSequence(), hit.getAccession(), hit.getProteinSequence(), hit.getProteinDescription(), hit.getSpectrumId(), hit.getSpectrumFilename(), hit.getSpectrumTitle(), hit.getScore(), hit.getCharge(), hit.getQvalue(), hit.getPep());
	}

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

	public void setSpectrumId(long spectrumId) {
		this.spectrumId = spectrumId;
	}

	public void setQValue(double qValue) {
		this.qValue = qValue;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public double getPep() {
		return pep;
	}

	public void setPep(double pep) {
		this.pep = pep;
	}

	@Override
	public String getProteinDescription() {
		return proteinDescription;
	}

	public void setProteinDescription(String proteinDescription) {
		this.proteinDescription = proteinDescription;
	}

	@Override
	public String getSpectrumFilename() {
		return spectrumFilename;
	}

	public void setSpectrumFilename(String spectrumFilename) {
		this.spectrumFilename = spectrumFilename;
	}

	@Override
	public String getSpectrumTitle() {
		return spectrumTitle;
	}

	public void setSpectrumTitle(String spectrumTitle) {
		this.spectrumTitle = spectrumTitle;
	}
}
