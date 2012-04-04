package de.mpa.client.model.dbsearch;

import de.mpa.db.accessor.SearchHit;

public class PeptideSpectrumMatch {
	/**
	 * The spectrum id;
	 */
	private long spectrumid;
	
	/**
	 * The ion charge.
	 */
	private int charge;
	
	/**
	 * The search engine hit.
	 */
	private SearchEngineHit searchEngineHit;
	
	/**
	 * Default empty constructor.
	 */
	public PeptideSpectrumMatch() {
		
	}
	
	/**
	 * Constructor for the PeptideSpectrumMatch.
	 * @param spectrumid
	 * @param votes
	 * @param searchEngineHits
	 */
	public PeptideSpectrumMatch(long spectrumid, SearchHit searchHit) {
		this.spectrumid = spectrumid;
		this.charge = (int) searchHit.getCharge();
		this.searchEngineHit = new SearchEngineHit(searchHit);
	}
	
	/**
	 * Returns the spectrum specific id.
	 * @return The spectrum id.
	 */
	public long getSpectrumId() {
		return spectrumid;
	}
	
	/**
	 * Sets the spectrum id.
	 * @param spectrumid The spectrum id
	 */
	public void setSpectrumId(long spectrumid) {
		this.spectrumid = spectrumid;
	}

	/**
	 * Returns the search engine hit.
	 * @return The search engine hit.
	 */
	public SearchEngineHit getSearchEngineHit() {
		return searchEngineHit;
	}
	
	/**
	 * Returns the PSM charge.
	 * @return The PSM charge.
	 */
	public int getCharge() {
		return charge;
	}
}
