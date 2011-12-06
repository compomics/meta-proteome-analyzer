package de.mpa.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.XTandemhit;

/**
 * The instance of the SearchResults class holds the results (as maps) for the different search engine results.
 * @author Thilo Muth
 *
 */
public class DbSearchResult {
	
	// Search spectra
	private List<Searchspectrum> querySpectra = new ArrayList<Searchspectrum>();
		
	// X!Tandem results
	private Map<String, List<XTandemhit>> xTandemResults = new HashMap<String, List<XTandemhit>>();
	
	// Omssa results
	private Map<String, List<Omssahit>> omssaResults = new HashMap<String, List<Omssahit>>();
	
	// Crux results
	private Map<String, List<Cruxhit>> cruxResults = new HashMap<String, List<Cruxhit>>();
	
	// Inspect results
	private Map<String, List<Inspecthit>> inspectResults = new HashMap<String, List<Inspecthit>>();
	
	public List<Searchspectrum> getQuerySpectra() {
		return querySpectra;
	}

	public void setQuerySpectra(List<Searchspectrum> querySpectra) {
		this.querySpectra = querySpectra;
	}

	public Map<String, List<XTandemhit>> getxTandemResults() {
		return xTandemResults;
	}
	
	public void setxTandemResults(Map<String, List<XTandemhit>> xTandemResults) {
		this.xTandemResults = xTandemResults;
	}
	public Map<String, List<Omssahit>> getOmssaResults() {
		return omssaResults;
	}
	public void setOmssaResults(Map<String, List<Omssahit>> omssaResults) {
		this.omssaResults = omssaResults;
	}
	public Map<String, List<Cruxhit>> getCruxResults() {
		return cruxResults;
	}
	public void setCruxResults(Map<String, List<Cruxhit>> cruxResults) {
		this.cruxResults = cruxResults;
	}
	public Map<String, List<Inspecthit>> getInspectResults() {
		return inspectResults;
	}
	public void setInspectResults(Map<String, List<Inspecthit>> inspectResults) {
		this.inspectResults = inspectResults;
	}
}
