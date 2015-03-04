package de.mpa.client.model.dbsearch;

import java.io.Serializable;

public enum SearchEngineType implements Serializable {
	XTANDEM("X!Tandem"), 
	OMSSA("OMSSA"), 
	MASCOT("Mascot");
	
	SearchEngineType(final String searchEngineName){
		this.searchEngineName = searchEngineName;
	}
	
	private final String searchEngineName;
	
	@Override
	public String toString() {
		return searchEngineName;
	}
}
