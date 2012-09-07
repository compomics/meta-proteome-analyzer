package de.mpa.main;

import java.util.LinkedHashMap;
import java.util.Map;

import de.mpa.analysis.KeggMaps;
import de.mpa.client.ui.Constants;
import de.mpa.parser.ec.ECEntry;
import de.mpa.parser.ec.ECReader;

/**
 * This class initialize the parameters
 * @author heyer
 */
public class Parameters {

	public static Parameters instance;
	
	private Map<String, ECEntry> ecMap;

	private LinkedHashMap<String, String> keggTaxonomyMap;

	private LinkedHashMap<String, String> keggPathwayMap;
	
	private Parameters() {
		// TODO: Loading parameter dialog.
		initializeParameters();
	}
	
	public static Parameters getInstance() {
		if (instance == null){
			instance = new Parameters();
		}
		return instance;
	}
	
	/**
	 * This method initialize the parameters
	 */
	private void initializeParameters(){
		// Initialize the EC-number map
		String paramsPath = "/" + getConfPath() + "/ECreduced.xml";
		ecMap = ECReader.readEC(paramsPath);
		// Initialize the Kegg pathway map
		String keggPathwayPath = "/" + getConfPath() + "/keggPathways.txt";
		keggPathwayMap = KeggMaps.getKeggPathwayList(keggPathwayPath);
		// Initialize the Kegg taxonomy map
		String keggTaxonomyPath = "/" + getConfPath() + "/keggTaxonomyMap.txt";
		keggTaxonomyMap = KeggMaps.getKeggTaxonomie(keggTaxonomyPath);
	}
	
	/**
	 * Returns the array of enabled panels for the Viewer
	 * @return Array of enabled panels
	 */
	public String[] getEnabledItemsForViewer() {
		return new String[] { "View Results" };
	}
	
	/**
	 * This method returns the path of the parameters.
	 * @return String path. Returns the path of parameters.
	 */
	public String getConfPath() {
		String path = this.getClass().getResource("Parameters.class").getPath();
		if (path.indexOf("/" + Constants.APPTITLE) != -1) {
            path = path.substring(1, path.lastIndexOf("/" + Constants.APPTITLE) + Constants.APPTITLE.length() + 1);
            path = path.replace("%20", " ");
            path = path.replace("%5b", "[");
            path = path.replace("%5d", "]");
            path += "/conf";
		}
		return path;
	}
	
	/**
	 * This method returns the path of the mpa.
	 * @return String path. Returns the path of the mpa.
	 */
	public String getPath() {
		String path = this.getClass().getResource("Parameters.class").getPath();
		if (path.indexOf("/" + Constants.APPTITLE) != -1) {
            path = path.substring(1, path.lastIndexOf("/" + Constants.APPTITLE) + Constants.APPTITLE.length() + 1);
            path = path.replace("%20", " ");
            path = path.replace("%5b", "[");
            path = path.replace("%5d", "]");
		}
		return path;
	}

	/**
	 * This method returns the EC-map
	 * @return Map<String, ECEntry>. Returns the ECMap
	 */
	public Map<String, ECEntry> getEcMap() {
		return ecMap;
	}

	/**
	 * This method returns the map of all KeggPathways
	 * @return keggPathwayMap. The map of all KeggPathways
	 */
	public LinkedHashMap<String, String> getKeggPathwayMap() {
		return keggPathwayMap;
	}

	/**
	 * This method returns the map of all KeggTaxons
	 * @return keggTaxonomyMap
	 */
	public LinkedHashMap<String, String> getKeggTaxonomyMap() {
		return keggTaxonomyMap;
	}
	
	
}
