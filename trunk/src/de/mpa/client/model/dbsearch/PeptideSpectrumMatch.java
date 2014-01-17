package de.mpa.client.model.dbsearch;

import java.util.ArrayList;
import java.util.List;

import de.mpa.client.model.SpectrumMatch;
import de.mpa.db.accessor.SearchHit;

public class PeptideSpectrumMatch extends SpectrumMatch {
	
	/**
	 * Serialization ID set to default == 1L;
	 */
	private static final long serialVersionUID = 1L; 
	
	/**
	 * The ion charge.
	 */
	private int charge;
	
	/**
	 * The search engine hits.
	 */
	private List<SearchHit> searchHits;
	
	private List<SearchHit> visSearchHits;
	
	/**
	 * Default empty constructor.
	 */
	public PeptideSpectrumMatch() {}
	
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
		return visSearchHits;
	}
	
	/**
	 * Adds a search engine hit to the PSM. Checks for redundancy.
	 * @param hit Another search engine hit to be added.
	 */
	public void addSearchEngineHit(SearchHit hit) {
		if (!searchHits.contains(hit)) {
			this.searchHits.add(hit);
		}
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
	public int getVotes() {
		return searchHits.size();
	}
	
	/**
	 * TODO: API
	 * @param fdr
	 */
	@Override
	public void setFDR(double fdr) {
		this.visSearchHits = new ArrayList<SearchHit>();
		for (SearchHit hit : this.searchHits) {
			if (hit.getQvalue().doubleValue() <= fdr) {
				this.visSearchHits.add(hit);
			}
		}
	}
	
	@Override
	public boolean isVisible() {
		return !this.visSearchHits.isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PeptideSpectrumMatch) {
			PeptideSpectrumMatch that = (PeptideSpectrumMatch) obj;
			if (this.getSearchSpectrumID() == that.getSearchSpectrumID()) {
				return this.getSearchHits().containsAll(that.getSearchHits());
			}
		}
		return false;
	}
}
