package de.mpa.db.neo4j.properties;

public enum OntologyProperty implements ElementProperty {
	IDENTIFIER("Identifier"),
	TYPE("Type");
	
	OntologyProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return this.propertyName;
	}
}
