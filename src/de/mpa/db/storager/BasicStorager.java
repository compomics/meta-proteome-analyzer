package de.mpa.db.storager;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spec2pep;

/**
 * Basic storage functionality: Loading and storing of data.
 * 
 * @author Thilo Muth
 *
 */
public class BasicStorager implements Storager {
	
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
	public void load() { }

	@Override
	public void store() throws Exception { }
	
	/**
	 * Retrieves the database identifier for the peptide with the specified
	 * sequence. If no such peptide exists yet it will be stored in the database
	 * and the generated identifier will be returned.
	 * @param sequence the peptide sequence
	 * @return the database identifier
	 * @throws SQLException if a database error occurs
	 */
	protected long storePeptide(String sequence) throws SQLException {
		// retrieve peptide from database
		PeptideAccessor peptide = PeptideAccessor.findFromSequence(sequence, conn);
		if (peptide == null) {
			// peptide does not yet exist, store a new one
			HashMap<Object, Object> data = new HashMap<Object, Object>(2);
			data.put(PeptideAccessor.SEQUENCE, sequence);
			peptide = new PeptideAccessor(data);
			peptide.persist(conn);
			// return generated peptide identifier
			return (Long) peptide.getGeneratedKeys()[0];
		} else {
			return peptide.getPeptideid();
		}
	}
	
	/**
	 * Attempts to store a spectrum-to-peptide association in the database if it doesn't already exist.<br>
	 * The actual spectrum id is inferred from the specified searchspecrum id.
	 * @param searchspectrumID the searchspectrum id
	 * @param peptideID the peptide id
	 * @return the spec2pep id
	 * @throws SQLException if a database error occurs
	 */
	protected long storeSpec2Pep(long searchspectrumID, long peptideID) throws SQLException {
		// check for errors
		if ((searchspectrumID <= 0) || (peptideID <= 0)) {
			// abort prematurely
			return -1L;
		}
		// retrieve searchspectrum from id, guaranteed to always work if searchspectrumID > 0
		Searchspectrum searchspectrum =
				Searchspectrum.findFromSearchSpectrumID(searchspectrumID, conn);
		// extract spectrum id
		long spectrumID = searchspectrum.getFk_spectrumid();
		
		// check whether spec2pep link already exists in database
		Spec2pep spec2pep = Spec2pep.findLink(spectrumID, peptideID, conn);
		if (spec2pep == null) {
			// link does not yet exist, therefore store a new one
			HashMap<Object, Object> data = new HashMap<Object, Object>();
			data.put(Spec2pep.FK_SPECTRUMID, spectrumID);
			data.put(Spec2pep.FK_PEPTIDEID, peptideID);
			spec2pep = new Spec2pep(data);
			spec2pep.persist(conn);
			return (Long) spec2pep.getGeneratedKeys()[0];
		}
		return spec2pep.getSpec2pepid();
	}
	
//	/**
//	 * This method stores a protein to the database.
//	 * @param peptideID Peptide ID
//	 * @param accession Protein accession
//	 * @return Protein ID
//	 * @throws SQLException
//	 * @throws IOException
//	 */
//	protected Long storeProtein(long peptideID, String accession) throws SQLException, IOException {
//		
//		ProteinAccessor prot = ProteinAccessor.findFromAttributes(accession, conn);
//        String description = prot.getDescription();
//		
//		if (proteinID == null) { // protein not yet in database
//			// Add new protein to the database
//			ProteinAccessor proteinAccessor = ProteinAccessor.addProteinWithPeptideID(peptideID, accession, description, protein.getSequence().getSequence(), conn);
//			proteinID = proteinAccessor.getProteinid();
//			proteinIdMap.put(accession, proteinID);
//		} else {
//			// check whether pep2prot link already exists,
//			// otherwise create new one
//			Pep2prot pep2prot = Pep2prot.findLink(peptideID, proteinID, conn);
//			// If no link from peptide to protein is given.
//			if (pep2prot == null) { 
//				// Link peptide to protein.
//				pep2prot = Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
//			}
//		}
//		// Add protein for UniProt storing.
//		MapContainer.UniprotQueryProteins.put(accession, proteinID);
//		return proteinID;
//	}
	
}
