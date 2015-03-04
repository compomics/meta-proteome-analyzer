package de.mpa.graphdb.properties;

public enum ExperimentProperty implements ElementProperty {
	IDENTIFIER("Experiment Title"),
	PROJECTTITLE("Project Title");
	
	ExperimentProperty(final String propertyName){
		this.propertyName = propertyName;
	}
	
	private final String propertyName;
	
	@Override
	public String toString() {
		return propertyName;
	}
}
