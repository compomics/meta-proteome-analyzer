package de.mpa.parser.ec;

import java.util.Map;
import java.util.TreeMap;

/**
 * This method represents the map of the EC-entries
 * @author heyer
 *
 */
public class ECMap{

	// Map with EC number as key and name of EC-number
	public Map<String,ECEntry> ecMap = new TreeMap<String, ECEntry>();

//	/**
//	 * Empty Constructor
//	 */
//	public ECMap() {
//		ecMap = this.ecMap;
//	}

	/**
	 * Constructor to create ECMap
	 * @param filePath
	 * @return
	 */
	public ECMap(String filePath) {
		Map<String,ECEntry> ecMap = ECReader.readEC(filePath);
	}

	/**
	 * This method gets the EC-map
	 * @return ecMap
	 */
	public Map<String, ECEntry> getEcMap() {
		return ecMap;
	}
}
