package de.mpa.graphdb.properties;

public enum ProteinProperty implements ElementProperty {
	ACCESSION("accession"),
	DESCRIPTION("description"),
	LENGTH("length"),
	PROTEINSEQUENCE("proteinsequence"),
	COVERAGE("coverage");
	
	ProteinProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
