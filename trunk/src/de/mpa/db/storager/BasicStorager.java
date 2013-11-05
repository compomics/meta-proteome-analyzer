package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.compomics.util.protein.Protein;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.db.MapContainer;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.ProteinAccessor;

/**
 * Basic storage functionality: Loading and storing of data.
 * 
 * @author Thilo Muth
 *
 */
public abstract class BasicStorager implements Storager {
	
	/**
	 * Logger object for the storage classes.
	 */
	protected Logger log = Logger.getLogger(getClass());
	
	/**
	 * Connection instance.
	 */
	protected Connection conn;
	
    /**
     * The file instance.
     */
    protected File file;
    
    /**
     * The search engine type.
     */
    protected SearchEngineType searchEngineType;
    
	@Override
	public void run() {
		this.load();
		try {
			this.store();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error("Could not perform rollback. Error message: " + e.getMessage());
				e1.printStackTrace();
			}
			log.error(searchEngineType.name() + " storing error message: " + e.getMessage());
			e.printStackTrace();
		}
		log.info(searchEngineType.name() + " results stored to the DB.");
	}

	@Override
	public void load() {		
	}

	@Override
	public void store() throws Exception {
	}
	
	protected Long storeProtein(long peptideID, String accession) throws SQLException, IOException {
        Protein protein = MapContainer.FastaLoader.getProteinFromFasta(accession);
        String description = protein.getHeader().getDescription();
        
		HashMap<String, Long> proteinIdMap = MapContainer.getProteinIdMap();
		Long proteinID = proteinIdMap.get(accession);
		
		if (proteinID == null) { // protein not yet in database
			// Add new protein to the database
			ProteinAccessor proteinAccessor = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, protein.getSequence().getSequence(), conn);
			proteinID = proteinAccessor.getProteinid();
			MapContainer.UniprotQueryProteins.put(accession, proteinID);
			proteinIdMap.put(accession, proteinID);
			
		} else {
			// check whether pep2prot link already exists,
			// otherwise create new one
			Pep2prot pep2prot = Pep2prot.findLink(peptideID, proteinID, conn);
			// If no link from peptide to protein is given.
			if (pep2prot == null) { 
				// Link peptide to protein.
				pep2prot = Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
			}
		}
		return proteinID;
	}
	
	
	
}
