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
	 * Holds the search engine type.
	 */
	private SearchEngineType type;
	
	/**
	 * Builds a search engine hit.
	 * @param searchhit The search hit.
	 * @param type The search engine type.
	 */
	public SearchEngineHit(SearchHit searchhit, SearchEngineType type) {
		this.qvalue = searchhit.getQvalue().doubleValue();
		this.searchhit = searchhit;
		this.type = type;
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
	
	/**
	 * Returns the search engine type.
	 * @return The search engine type.
	 */
	public SearchEngineType getType() {
		return type;
	}
}
