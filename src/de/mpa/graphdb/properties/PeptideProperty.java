package de.mpa.graphdb.properties;

public enum PeptideProperty implements ElementProperty {
	IDENTIFIER("Identifier"),	
	DESCRIPTION("Description"),
	SPECTRALCOUNT("Spectral Count"),
	TAXONOMY("Taxonomy"),
	MOLECULARWEIGHT("Molecular Weight (kDa)"),
	PROTEINCOUNT("Protein Count");	
	
	PeptideProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return this.propertyName;
	}
}
