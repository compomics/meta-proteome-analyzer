package de.mpa.graphdb.properties;

public enum OntologyProperty implements ElementProperty {
	IDENTIFIER("Identifier"),
	TYPE("Type");
	
	OntologyProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
