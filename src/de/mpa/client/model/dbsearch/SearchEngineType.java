package de.mpa.client.model.dbsearch;

import java.io.Serializable;

public enum SearchEngineType {
	
	XTANDEM("X!Tandem"), 
	OMSSA("OMSSA"), 
	CRUX("Crux"), 
	INSPECT("InsPect"), 
	MASCOT("Mascot"),
	SPECLIB("Spectral Library");
	
	SearchEngineType(String searchEngineName){
		this.searchEngineName = searchEngineName;
	}
	
	private final String searchEngineName;
	
	@Override
	public String toString() {
		return this.searchEngineName;
	}
}
