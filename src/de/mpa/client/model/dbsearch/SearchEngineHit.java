package de.mpa.client.model.dbsearch;

import de.mpa.db.accessor.SearchHit;
/**
 * This class contains all information about a search engine result hit.
 * @author T. Muth
 *
 */
public class SearchEngineHit {
	/**
	 * This variable holds the q-value.
	 */
	private double qvalue;
	
	/**
	 * This variable holds the search hit.
	 */
	private SearchHit searchhit;
	
	/**
	 * 
	 * @param qvalue
	 * @param searchhit
	 */
	public SearchEngineHit(SearchHit searchhit) {
		this.qvalue = searchhit.getQvalue().doubleValue();
		this.searchhit = searchhit;
	}

	/**
	 * Returns the q-value.
	 * @return The q-value.
	 */
	public double getQvalue() {
		return qvalue;
	}
	
	/**
	 * Returns the search hit.
	 * @return The search hit.
	 */
	public SearchHit getSearchhit() {
		return searchhit;
	}
}
