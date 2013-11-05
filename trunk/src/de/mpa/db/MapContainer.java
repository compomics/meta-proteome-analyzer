package de.mpa.db;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.ProteinTableAccessor;
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
	 
    /**
     * Accession to proteinID mapping used for the UniProt entry querying.
     */
    public static Map<String, Long> UniprotQueryProteins;
    
    private static HashMap<String, Long> proteinIdMap;
    
    public static HashMap<String, Long> getProteinIdMap() {
    	if (proteinIdMap == null) {
    		proteinIdMap = new HashMap<String, Long>();
    		Connection conn;
			try {
				conn = DBManager.getInstance().getConnection();
		 		List<ProteinTableAccessor> retrieveAllEntries = ProteinAccessor.retrieveAllEntries(conn);
	    		for (ProteinTableAccessor protein : retrieveAllEntries) {
					proteinIdMap.put(protein.getAccession(), protein.getProteinid());
				}
	    		System.out.println(proteinIdMap.size());
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
		return proteinIdMap;
	}
    
    
	 
}

