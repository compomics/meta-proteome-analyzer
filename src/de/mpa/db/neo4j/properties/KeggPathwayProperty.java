package de.mpa.db.neo4j.properties;

public enum KeggPathwayProperty implements ElementProperty{
	IDENTIFIER("Identifier"),
	DESCRIPTION("Description");
	
	KeggPathwayProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return this.propertyName;
	}
}