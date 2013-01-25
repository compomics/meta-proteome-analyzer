package de.mpa.graphdb.properties;

public enum SpeciesProperty implements ElementProperty {
	NAME("name"),
	TAXON("taxon"),
	RANK("rank"),
	DESCRIPTION("description");
	
	SpeciesProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
