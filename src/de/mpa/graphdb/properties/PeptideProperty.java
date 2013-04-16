package de.mpa.graphdb.properties;

public enum PeptideProperty implements ElementProperty {
	IDENTIFIER("identifier"),
	SEQUENCE("sequence"),
	LENGTH("length");
	
	PeptideProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
