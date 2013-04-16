package de.mpa.graphdb.properties;

public enum ProteinProperty implements ElementProperty {
	IDENTIFIER("identifier"),
	DESCRIPTION("description"),
	LENGTH("length"),
	SEQUENCE("sequence"),
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
