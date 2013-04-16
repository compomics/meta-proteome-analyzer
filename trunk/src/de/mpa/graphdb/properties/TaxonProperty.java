package de.mpa.graphdb.properties;

public enum TaxonProperty implements ElementProperty {
	IDENTIFIER("identifier"),
	TAXID("taxid"),
	RANK("rank");	
	
	TaxonProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
