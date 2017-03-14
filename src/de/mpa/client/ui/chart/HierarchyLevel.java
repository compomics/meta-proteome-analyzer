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
	private final String description;
	
	/**
	 * Graphdb identifier string.
	 */
	private final String countIdentifier;
	
	HierarchyLevel(String description, String countIdentifier) {
		this.description = description;
		this.countIdentifier = countIdentifier;
	}
	
	@Override
	public String toString() {
		return this.description;
	}

	@Override
	public String getTitle() {
		return toString();
	}

	public String getCountIdentifier() {
		return this.countIdentifier;
	}
}
