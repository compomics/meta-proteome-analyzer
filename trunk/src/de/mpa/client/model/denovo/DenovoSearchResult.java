package de.mpa.client.model.denovo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.db.accessor.Pepnovohit;

/**
 * The instance of the DenovoSearchResult class holds the results for the de-novo search result.
 * @author Thilo Muth
 *
 */
public class DenovoSearchResult {
	
	/**
	 * This map contains all the retrieved de novo hits.
	 */
	private Map<String, List<Pepnovohit>> denovoHits = new HashMap<String, List<Pepnovohit>>();
	
	/**
	 * Holds the title to spectrum id mapping.
	 */
	private Map<String, Long> titleToSpectrumIdMap = new HashMap<String, Long>();

	/**
	 * The project title.
	 */
	private String projectTitle;
	
	/**
	 * The experiment title.
	 */
	private String experimentTitle;
	
	/**
	 * The de novo search result.
	 * @param projectTitle The project title.
	 * @param experimentTitle The experiment title.
	 */
	public DenovoSearchResult(String projectTitle, String experimentTitle) {
		this.projectTitle = projectTitle;
		this.experimentTitle = experimentTitle;
	}

	/**
	 * Add a tag hit to the de-novo search result
	 * @param tagHit The de-novo tag hit.
	 */
	public void addHitSet(String title, Long spectrumid, List<Pepnovohit> hits){
		denovoHits.put(title, hits);
		titleToSpectrumIdMap.put(title, spectrumid);
	}
	
	/**
	 * Returns the spectrum id for a specific title.
	 * @param title The spectrum title.
	 * @return The spectrum id.
	 */
	public Long getSpectrumIdfromTitle(String title){
		return titleToSpectrumIdMap.get(title);
	}

	/**
	 * Returns all the de novo hits.
	 * @return
	 */
	public Map<String, List<Pepnovohit>> getDenovoHits() {
		return denovoHits;
	}

	/**
	 * Returns the project title.
	 * @return The project title.
	 */
	public String getProjectTitle() {
		return projectTitle;
	}
	
	/**
	 * The experiment title.
	 * @return The experiment title.
	 */
	public String getExperimentTitle() {
		return experimentTitle;
	}
}
