package de.mpa.client.model;


/**
 * The instance of the SearchResults class holds the database search engine results.
 * @author Thilo Muth
 *
 */
public class DbSearchResult {
	
	// The protein hits.
	private ExperimentResult proteinHitSet;
	
	/**
	 * Returns the protein hit set results.
	 * @return the proteins
	 */
	public ExperimentResult getExperimentResult() {
		return proteinHitSet;
	}

	/**
	 * Sets the protein hit set results.
	 * @param proteins the proteins to set
	 */
	public void setProteinResults(ExperimentResult proteinHitSet) {
		this.proteinHitSet = proteinHitSet;
	}


}
