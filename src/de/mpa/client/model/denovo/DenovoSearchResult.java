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
	private Map<String, TagHit> tagHits = new HashMap<String, TagHit>();
	
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
	public void addTagHit(TagHit tagHit){
		
			// The tag sequence
			String tagSequence = tagHit.getTag().getGappedSeq();
			
			// Check if tag hit is already in the tag hit set.
			if (tagHits.containsKey(tagSequence)) {
				
				// Current protein hit
				TagHit currentTagHit = tagHits.get(tagSequence);
					
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
				tagHits.put(tagSequence, tagHit);
			}
	}
	
	/**
	 * Returns the de-novo tag hits from the result set.
	 * @return The de-novo tag hits.
	 */
	public Map<String, TagHit> getTagHits() {
		return tagHits;
	}
	
	/**
	 * Returns a specific de-novo tag hit.
	 * @param tagSequence The tag sequence.
	 * @return The de-novo tag hit for a corresponding sequence.
	 */
	public TagHit getTagHit(String tagSequence){
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
