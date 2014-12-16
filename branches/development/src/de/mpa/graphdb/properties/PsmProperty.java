package de.mpa.graphdb.properties;

public enum PsmProperty implements ElementProperty {
	IDENTIFIER("Identifier"),
	SPECTRUMID("Spectrum ID"),
	SCORES("Scores"),
	VOTES("Votes");
	
	PsmProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
