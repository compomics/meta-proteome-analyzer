package de.mpa.db.accessor;

import de.mpa.client.model.dbsearch.SearchEngineType;

public interface SearchHit {
	public SearchEngineType getType();
	public String getSequence();
	public String getAccession();
	public Number getQvalue();
	public double getScore();
	public long getCharge();
	public long getFk_searchspectrumid();
	public long getFk_peptideid();
	public long getFk_proteinid();
}
