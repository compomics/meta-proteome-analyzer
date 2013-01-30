package de.mpa.graphdb.properties;

public enum PsmProperty implements ElementProperty {
	
	SPECTRUMID("SPECTRUMID"),
	VOTES("VOTES");
	
	PsmProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
