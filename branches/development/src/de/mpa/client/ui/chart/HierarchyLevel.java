package de.mpa.client.ui.chart;

/**
 * Enumeration containing members relating to hierarchy levels of search result
 * objects.
 * 
 * @author A. Behne
 */
public enum HierarchyLevel implements ChartType {
	META_PROTEIN_LEVEL("Meta-Proteins", "metaproteins"),
	PROTEIN_LEVEL("Proteins", "proteins"),
	PEPTIDE_LEVEL("Peptides", "peptides"),
	SPECTRUM_LEVEL("Spectra", "psms");
	
	/**
	 * The description string.
	 */
	private String description;
	
	/**
	 * Graphdb identifier string.
	 */
	private String countIdentifier;
	
	private HierarchyLevel(String description, String countIdentifier) {
		this.description = description;
		this.countIdentifier = countIdentifier;
	}
	
	@Override
	public String toString() {
		return description;
	}

	@Override
	public String getTitle() {
		return this.toString();
	}

	public String getCountIdentifier() {
		return countIdentifier;
	}
}
