package de.mpa.main;

import java.util.Map;

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
		// Initialize the EC-number Map
		String paramsPath = getPath() + "/ECreduced.xml";
		ecMap = ECReader.readEC(paramsPath);		
	}
	
	/**
	 * This method returns the path of the parameters.
	 * @return String patn. Returns the part of parameters.
	 */
	private String getPath() {
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
	 * This method returns the EC-map
	 * @return Map<String, ECEntry>. Returns the ECMap
	 */
	public Map<String, ECEntry> getEcMap() {
		return ecMap;
	}
}
