package de.mpa.db.neo4j.properties;

public enum EdgeProperty implements ElementProperty {
	ID("id"),
	LABEL("label");
	
	EdgeProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return this.propertyName;
	}
}