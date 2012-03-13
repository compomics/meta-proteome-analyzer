package de.mpa.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.db.accessor.Pepnovohit;
import de.mpa.db.accessor.Spectrum;

/**
 * The instance of the SearchResults class holds the results (as maps) for the different search engine results.
 * @author Thilo Muth
 *
 */
public class DenovoSearchResult {
	
	// Search spectra
	private List<Spectrum> querySpectra = new ArrayList<Spectrum>();
		
	// Pepnovo results
	private Map<String, List<Pepnovohit>> pepnovoResults = new HashMap<String, List<Pepnovohit>>();

	public List<Spectrum> getQuerySpectra() {
		return querySpectra;
	}

	public void setQuerySpectra(List<Spectrum> querySpectra) {
		this.querySpectra = querySpectra;
	}

	public Map<String, List<Pepnovohit>> getPepnovoResults() {
		return pepnovoResults;
	}

	public void setPepnovoResults(Map<String, List<Pepnovohit>> pepnovoResults) {
		this.pepnovoResults = pepnovoResults;
	}

}
