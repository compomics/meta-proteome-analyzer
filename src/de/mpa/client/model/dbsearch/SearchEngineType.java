package de.mpa.client.model.dbsearch;

import java.io.Serializable;

public enum SearchEngineType implements Serializable {
	
	XTANDEM("X!Tandem"), 
	OMSSA("OMSSA"), 
	CRUX("Crux"), 
	INSPECT("InsPect"), 
	MASCOT("Mascot"),
	SPECLIB("Spectral Library");
	
	SearchEngineType(final String searchEngineName){
		this.searchEngineName = searchEngineName;
	}
	
	private final String searchEngineName;
	
	@Override
	public String toString() {
		return searchEngineName;
	}
}
