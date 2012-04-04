package de.mpa.client.model.denovo;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The instance of the DenovoSearchResult class holds the results for the de-novo search result.
 * @author Thilo Muth
 *
 */
public class DenovoSearchResult {
	
	/**
	 * This map contains all the retrieved de-novo tag hits.
	 */
	private Map<String, DenovoTagHit> tagHits = new HashMap<String, DenovoTagHit>();
	
	/**
	 * The project title.
	 */
	private String projectTitle;
	
	/**
	 * The experiment title.
	 */
	private String experimentTitle;
	
	/**
	 * The de-novo search result.
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
	public void addTagHit(DenovoTagHit tagHit){
		
			// The tag sequence
			String tagSequence = tagHit.getTagSequence();
			
			// Check if tag hit is already in the tag hit set.
			if (tagHits.containsKey(tagSequence)) {
				
				// Current protein hit
				DenovoTagHit currentTagHit = tagHits.get(tagSequence);
					
				// Get the first - and only - spectrum hit.
				SpectrumHit spectrumHit = tagHit.getFirstSpectrumHit();
				
				// Get the current spectrum hits.
				TreeMap<Long, SpectrumHit> currentSpectrumHits = currentTagHit.getSpectrumHits();
				
				// If the spectrum hit is not in the list of current hits.
				if(!currentSpectrumHits.containsKey(spectrumHit.getSpectrumid())){
					currentSpectrumHits.put(spectrumHit.getSpectrumid(), spectrumHit);
					currentTagHit.setSpectrumHits(currentSpectrumHits);
				} 
			} else {
				tagHits.put(tagHit.getTagSequence(), tagHit);
			}
	}
	
	/**
	 * Returns the de-novo tag hits from the result set.
	 * @return The de-novo tag hits.
	 */
	public Map<String, DenovoTagHit> getTagHits() {
		return tagHits;
	}
	
	/**
	 * Returns a specific de-novo tag hit.
	 * @param tagSequence The tag sequence.
	 * @return The de-novo tag hit for a corresponding sequence.
	 */
	public DenovoTagHit getTagHit(String tagSequence){
		return tagHits.get(tagSequence);
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
