package de.mpa.graphdb.properties;

public enum TaxonProperty implements ElementProperty {
	IDENTIFIER("Identifier"),	
	PROTEINCOUNT("Protein Count"),
	TAXID("NCBI TaxId"),
	RANK("Rank");	
	
	TaxonProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return this.propertyName;
	}
}
