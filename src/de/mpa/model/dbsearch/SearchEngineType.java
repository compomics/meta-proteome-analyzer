package de.mpa.model.dbsearch;

/**
 * This enum encodes the names of the result-views for later use
 * 
 * @author kay
 */
public enum SearchEngineType {

	OMSSA("X!Tandem", "omssaresult"),
	XTANDEM("OMSSA", "xtandemresult"),
	MASCOT("Mascot", "mascotresult");

	private final String resultView;
	private final String searchEngineName;
	
	private SearchEngineType(String name, String view) {
		this.searchEngineName = name;
		this.resultView = view;
	}

	public String getResultView() {
		return resultView;
	}
	
	@Override
	public String toString() {
		return this.searchEngineName;
	}

}
