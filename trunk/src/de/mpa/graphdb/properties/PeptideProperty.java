package de.mpa.graphdb.properties;

public enum PeptideProperty implements ElementProperty {
	IDENTIFIER("Identifier"),	
	SPECTRALCOUNT("Spectral Count"),
	SPECIES("Species"),
	PROTEINCOUNT("Protein Count");	
	
	PeptideProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
