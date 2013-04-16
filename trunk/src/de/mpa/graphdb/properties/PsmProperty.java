package de.mpa.graphdb.properties;

public enum PsmProperty implements ElementProperty {
	IDENTIFIER("identifier"),
	SPECTRUMID("SPECTRUMID"),
	SCORES("scores");
	
	PsmProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
