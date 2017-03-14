package de.mpa.client.model.dbsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.client.Client;
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
	private Map<SearchEngineType, SearchHit> searchHits;
	
	/**
	 * The visible search engine hits.
	 */
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
        searchSpectrumID = spectrumid;
        charge = (int) searchHit.getCharge();
        searchHits = new HashMap<SearchEngineType, SearchHit>();
        searchHits.put(searchHit.getType(), searchHit);
	}
	
	/**
	 * Returns the single search hit.
	 * @return The single search hit.
	 */
	public SearchHit getSingleSearchHit() {
		if (this.searchHits.size() > 1) {
			if (Client.isDebug()) {
				System.err.println("PSM " + this + " already contains multiple search hits.");
			}
		}
		return this.searchHits.values().iterator().next();
	}
	
	/**
	 * Returns the search hit associated with the specified search engine type.
	 * @param type the search engine type
	 * @return the search hit or <code>null</code> if no such hit exists
	 */
	public SearchHit getSearchHit(SearchEngineType type) {
		return this.searchHits.get(type);
	}
	
	/**
	 * Returns the list of search hits.
	 * @return The list of search hits.
	 */
	@Override
	public List<SearchHit> getSearchHits() {
		if (this.visSearchHits == null) {
			return new ArrayList<SearchHit>(this.searchHits.values());
		}
		return this.visSearchHits;
	}
	
	/**
	 * Adds a search engine hit to the PSM. Checks for redundancy.
	 * @param hit Another search engine hit to be added.
	 */
	@Override
	public void addSearchHit(SearchHit hit) {
		if (!this.searchHits.containsValue(hit)) {
            searchHits.put(hit.getType(), hit);
//			this.charge = (int) hit.getCharge();
		} else {
		}
	}

	/**
	 * Returns the PSM charge.
	 * @return The PSM charge.
	 */
	public int getCharge() {
		return this.charge;
	}
	
	/**
	 * Sets a new charge for this PSM
	 * @param c  The charge provided 
	 */
	public void setCharge(int c) {
        charge = c;
	}
	
	
	/**
	 * Returns the votes.
	 * @return The number of votes for the search engine hits.
	 */
	public int getVotes() {
		return this.searchHits.size();
	}
	
	/**
	 * Sets the false discovery rate
	 * @param fdr
	 */
	@Override
	public void setFDR(double fdr) {
        visSearchHits = new ArrayList<SearchHit>();
		for (SearchHit hit : searchHits.values()) {
			if (hit.getQvalue().doubleValue() <= fdr) {
                visSearchHits.add(hit);
			}
		}
	}
	
	@Override
	public boolean isVisible() {
		// If null initialize a new empty list
		if (this.visSearchHits == null) {
            this.visSearchHits = getSearchHits();
		}
		return !visSearchHits.isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PeptideSpectrumMatch) {
			PeptideSpectrumMatch that = (PeptideSpectrumMatch) obj;
			if (getSearchSpectrumID() == that.getSearchSpectrumID()) {
				return getSearchHits().containsAll(that.getSearchHits());
			}
		}
		return false;
	}
}
