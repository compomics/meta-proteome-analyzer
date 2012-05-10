package de.mpa.client.model.dbsearch;

import java.util.ArrayList;
import java.util.List;

import de.mpa.client.model.SpectrumMatch;
import de.mpa.db.accessor.SearchHit;

public class PeptideSpectrumMatch extends SpectrumMatch {
	
	/**
	 * The ion charge.
	 */
	private int charge;
	
	/**
	 * The search engine hits.
	 */
	private List<SearchHit> searchHits;
	
	/**
	 * Default empty constructor.
	 */
	public PeptideSpectrumMatch() {
		
	}
	
	/**
	 * Constructor for the PeptideSpectrumMatch.
	 * @param spectrumid
	 * @param votes
	 * @param searchHits
	 */
	public PeptideSpectrumMatch(long spectrumid, SearchHit searchHit) {
		this.searchSpectrumID = spectrumid;
		this.charge = (int) searchHit.getCharge();
		this.searchHits = new ArrayList<SearchHit>();
		this.searchHits.add(searchHit);
	}
	
	/**
	 * Returns the search hit.
	 * @return The search hit.
	 */
	public SearchHit getFirstSearchHit() {
		return searchHits.get(0);
	}
	
	/**
	 * Returns the list of search hits.
	 * @return The list of search hits.
	 */
	public List<SearchHit> getSearchHits() {
		return searchHits;
	}
	
	/**
	 * Adds a search engine hit to the PSM.
	 * @param hit Another search engine hit to be added.
	 */
	public void addSearchEngineHit(SearchHit hit) {
		this.searchHits.add(hit);
	}

	/**
	 * Returns the PSM charge.
	 * @return The PSM charge.
	 */
	public int getCharge() {
		return charge;
	}
	
	/**
	 * Returns the votes.
	 * @return The number of votes for the search engine hits.
	 */
	public int getVotes(){
		return searchHits.size();
	}
}
