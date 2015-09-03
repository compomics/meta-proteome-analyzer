package de.mpa.client.model;

import de.mpa.client.model.dbsearch.SearchEngineType;

public interface SearchHit {
	public SearchEngineType getType();
	public String getPeptideSequence();
	public String getProteinSequence();
	public String getProteinDescription();
	public String getAccession();
	public long getSpectrumId();
	public String getSpectrumFilename();
	public String getSpectrumTitle();
	public double getQvalue();
	public double getPep();
	public double getScore();
	public int getCharge();
}
