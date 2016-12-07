package de.mpa.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mpa.analysis.TargetDecoyAnalysis;
import de.mpa.client.model.SearchHit;
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.fasta.FastaLoader;

/**
 * Generic container with global variables and collections.
 * 
 * @author T. Muth
 *
 */
public class GenericContainer {

	/**
	 * Map from spectrum title to spectrum id.
	 */
	public static Map<String, Long> SpectrumTitle2IdMap = new HashMap<String, Long>();

	/**
	 * Map from spectrum title to spectrum file name.
	 */
	public static Map<String, String> SpectrumTitle2FilenameMap = new HashMap<String, String>();
	
	/**
	 * Map from spectrum title to spectrum file name.
	 */
	public static Map<Long, String> SpectrumId2TitleMap = new HashMap<Long, String>();
	
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
	 * List of protein accessions.
	 */
	public static Set<String> ProteinAccs = new HashSet<String>();
	
	/**
	 * The absolute path to the created FASTA file.
	 */
	public static String CreatedFastaFilePath;
	
	/**
	 * Number of total spectra.
	 */
	public static int numberTotalSpectra;
	
	/**
	 * Current open MGFReader instance.
	 */
	public static Map<String, MascotGenericFileReader> MGFReaders = new HashMap<String, MascotGenericFileReader>();

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
	 * File containing the original PSM scores.
	 */
	protected File targetScoreFile;
	
	/**
	 * The FastaLoader instance. 
	 */
	public static FastaLoader FastaLoader;
	
	/**
	 * The current TargetDecoyAnalysis instance.
	 */
	public static TargetDecoyAnalysis currentTDA = null;
	
	/**
	 * The absolute number of hits (peptide-spectrum matches) for each search.
	 */
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
	
	//map must be a bijection in order for this to work properly
	public static <K,V> HashMap<V,K> reverse(Map<K,V> map) {
	    HashMap<V,K> rev = new HashMap<V, K>();
	    for(Map.Entry<K,V> entry : map.entrySet())
	        rev.put(entry.getValue(), entry.getKey());
	    return rev;
	}
	
}
