package de.mpa.graphdb.properties;

public enum ProteinProperty implements ElementProperty {
	IDENTIFIER("Identifier"),
	DESCRIPTION("Description"),
	SPECTRALCOUNT("Spectral Count"),
	TAXONOMY("Taxonomy"),
	COVERAGE("Seq Coverage");
	
	ProteinProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
