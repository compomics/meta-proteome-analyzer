package de.mpa.graphdb.properties;

public enum ProteinProperty implements ElementProperty {
	IDENTIFIER("Identifier"),
	DESCRIPTION("Description"),
	TAXONOMY("Taxonomy"),
	MOLECULARWEIGHT("Molecular Weight (kDa)"),
	COVERAGE("Sequence Coverage (%)"),
	PROTEINCOUNT("Protein Count"),
	SPECTRALCOUNT("Spectral Count");	
	
	
	ProteinProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
