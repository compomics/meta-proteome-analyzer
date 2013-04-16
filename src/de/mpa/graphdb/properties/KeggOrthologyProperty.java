package de.mpa.graphdb.properties;

public enum KeggOrthologyProperty implements ElementProperty{
	IDENTIFIER("identifier"),
	DESCRIPTION("description");
	
	KeggOrthologyProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
