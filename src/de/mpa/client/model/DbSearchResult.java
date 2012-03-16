package de.mpa.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.XTandemhit;

/**
 * The instance of the SearchResults class holds the results (as maps) for the different search engine results.
 * @author Thilo Muth
 *
 */
public class DbSearchResult {
	
	// Search spectra
	private List<Spectrum> querySpectra = new ArrayList<Spectrum>();
		
	// X!Tandem results
	private Map<String, List<XTandemhit>> xTandemResults = new HashMap<String, List<XTandemhit>>();
	
	// Omssa results
	private Map<String, List<Omssahit>> omssaResults = new HashMap<String, List<Omssahit>>();
	
	// Crux results
	private Map<String, List<Cruxhit>> cruxResults = new HashMap<String, List<Cruxhit>>();
	
	// Inspect results
	private Map<String, List<Inspecthit>> inspectResults = new HashMap<String, List<Inspecthit>>();
	
	// Vote map
	private Map<String, Integer> voteMap = new HashMap<String, Integer>();
	
	// The protein hits.
	private ProteinHitSet proteins;
	
	public List<Spectrum> getQuerySpectra() {
		return querySpectra;
	}

	public void setQuerySpectra(List<Spectrum> querySpectra) {
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
	public Map<String, Integer> getVoteMap() {
		return voteMap;
	}
	public void setVoteMap(Map<String, Integer> voteMap) {
		this.voteMap = voteMap;
	}
	/**
	 * Returns the protein hit set.
	 * @return the proteins
	 */
	public ProteinHitSet getProteins() {
		return proteins;
	}

	/**
	 * Sets the protein hit set.
	 * @param proteins the proteins to set
	 */
	public void setProteins(ProteinHitSet proteins) {
		this.proteins = proteins;
	}


}
