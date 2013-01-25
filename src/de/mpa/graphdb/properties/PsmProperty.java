package de.mpa.graphdb.properties;

public enum PsmProperty implements ElementProperty {
	
	SPECTRUMID("spectrumid"),
	VOTES("votes");
	
	PsmProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
