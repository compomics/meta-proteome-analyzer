package de.mpa.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.mpa.client.model.SearchHit;
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.fasta.FastaLoader;

/**
 * Generic container with global variables.
 * 
 * @author Thilo Muth
 *
 */
public class GenericContainer {

	/**
	 * Map from spectrum title to spectrum id.
	 */
	public static HashMap<String, Long> SpectrumTitle2IdMap = new HashMap<String, Long>();

	/**
	 * Map from spectrum file name to spectrum id.
	 */
	public static HashMap<String, Long> FileName2IdMap = new HashMap<String, Long>();
	
	/**
	 * Mapping from the spectrum to the byte positions.
	 */
	public static Map<String, List<Long>> SpectrumPosMap = new HashMap<String, List<Long>>();
	
	/**
	 * Accession to description mapping used for the UniProt entry querying.
	 */
	public static Map<String, ReducedUniProtEntry> UniprotQueryProteins = new HashMap<String, ReducedUniProtEntry>();
	
	/**
	 * List of global search hits.
	 */
	public static List<SearchHit> SearchHits = new ArrayList<SearchHit>();
	
	/**
	 * Current open MGFReader instance.
	 */
	public static MascotGenericFileReader MGFReader;

	/**
	 * Logger object for the storage classes.
	 */
	protected Logger log = Logger.getLogger(getClass());

	/**
	 * The file instance.
	 */
	protected File file;

	/**
	 * The search engine type.
	 */
	protected SearchEngineType searchEngineType;
    
    /**
     * The q-value file.
     */
	protected File qValueFile = null;
    
	/**
	 * File containing the original PSM scores.
	 */
	protected File targetScoreFile;
	
	public static FastaLoader FastaLoader;
	
	protected int nHits = 0;
	
    /**
     * Formatting spectrum titles.
     * @param spectrumTitle Unformatted spectrum title
     * @return Formatted spectrumTitle
     */
    protected String formatSpectrumTitle(String spectrumTitle) {
    	if (spectrumTitle.contains("RTINSECONDS")) {
    		spectrumTitle = spectrumTitle.substring(0, spectrumTitle.indexOf("RTINSECONDS") - 1);
    	}
		return spectrumTitle;
	}
 	
	public void parse() {
	}

	public int getNumberOfHits() {
		return nHits;
	}
	
	
}
