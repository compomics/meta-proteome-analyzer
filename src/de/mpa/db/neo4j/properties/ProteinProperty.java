package de.mpa.db.neo4j.properties;

public enum ProteinProperty implements ElementProperty {
	IDENTIFIER("Identifier"),
	DESCRIPTION("Description"),
	TAXONOMY("Taxonomy"),
	MOLECULARWEIGHT("Molecular Weight (kDa)"),
	COVERAGE("Sequence Coverage (%)"),
	PROTEINCOUNT("Protein Count"),
	SPECTRALCOUNT("Spectral Count");
	
	
	ProteinProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return this.propertyName;
	}
}
