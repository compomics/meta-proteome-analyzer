package de.mpa.graphdb.properties;

public enum FunctionProperty implements ElementProperty {
	NAME("name"),
	DESCRIPTION("description");
	
	FunctionProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
