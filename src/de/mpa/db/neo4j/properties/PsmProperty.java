package de.mpa.db.neo4j.properties;

public enum PsmProperty implements ElementProperty {
	IDENTIFIER("Identifier"),
	SPECTRUMID("Spectrum ID"),
	SCORES("Scores"),
	VOTES("Votes"),
	TITLE("Spectrum Title");
	
	PsmProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return this.propertyName;
	}
}
