package de.mpa.db.mysql;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import de.mpa.db.mysql.accessor.ProteinTableAccessor;
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
     * Mapping from protein accession to protein ID.
     */
    private static HashMap<String, Long> proteinIdMap;
    
    public static HashMap<String, Long> getProteinIdMap() {
    	if (MapContainer.proteinIdMap == null) {
            MapContainer.proteinIdMap = new HashMap<String, Long>();
    		Connection conn;
			try {
				conn = DBManager.getInstance().getConnection();
		 		List<ProteinTableAccessor> retrieveAllEntries = ProteinTableAccessor.retrieveAllEntries(conn);
	    		for (ProteinTableAccessor protein : retrieveAllEntries) {
                    MapContainer.proteinIdMap.put(protein.getAccession(), protein.getProteinid());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
		return MapContainer.proteinIdMap;
	}
}

