package de.mpa.db;


import java.util.HashMap;

import de.mpa.io.fasta.FastaLoader;

/**
 * Helper container for several spectrum mappings.
 * @author T.Muth
 *
 */
public class MapContainer {
	
	/**
	 * Map from spectrum title to spectrum id.
	 */
	 public static HashMap<String, Long> SpectrumTitle2IdMap;
	 
	 /**
	  * Map from spectrum file name to spectrum id.
	  */
	 public static HashMap<String, Long> FileName2IdMap;
	 
	 /**
	  * Instance of the FastaLoader.
	  */
	 public static FastaLoader FastaLoader;
}

