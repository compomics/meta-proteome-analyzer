package de.mpa.db.mysql.accessor;

import de.mpa.model.dbsearch.SearchEngineType;

public interface SearchHit {
	SearchEngineType getType();
	String getSequence();
	String getAccession();
	Number getQvalue();
	double getScore();
	long getCharge();
	long getFk_searchspectrumid();
	long getFk_peptideid();
	long getFk_proteinid();
}
