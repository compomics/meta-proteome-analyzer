package de.mpa.client.ui.chart;

/**
 * Enumeration containing members relating to hierarchy levels of search result
 * objects.
 * 
 * @author A. Behne
 */
public enum HierarchyLevel {
	
	META_PROTEIN_LEVEL("Meta-Proteins"),
	PROTEIN_LEVEL("Proteins"),
	PEPTIDE_LEVEL("Peptides"),
	SPECTRUM_LEVEL("Spectra");
	
	/**
	 * The description string.
	 */
	private String description;

	/**
	 * Constructs a hierarchy level member from the specified description.
	 * @param description the description
	 */
	private HierarchyLevel(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
}
