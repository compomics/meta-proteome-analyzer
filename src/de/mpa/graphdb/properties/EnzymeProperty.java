package de.mpa.graphdb.properties;

public enum EnzymeProperty implements ElementProperty {
	ECNUMBER("ecnumber"),
	DESCRIPTION("description");
	
	EnzymeProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}