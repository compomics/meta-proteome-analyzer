package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.model.dbsearch.UniProtEntryMPA;
import de.mpa.io.fasta.DigFASTAEntry;
import de.mpa.io.fasta.DigFASTAEntry.Type;

public class ProteinAccessor extends ProteinTableAccessor {

	/**
	 * Calls the super class.
	 * @param aParams
	 */
	public ProteinAccessor(@SuppressWarnings("rawtypes") HashMap aParams) {
		super(aParams);
	}

	/**
	 * This constructor allows the creation of the 'ProteinTableAccessor' object based on a resultset
	 * obtained by a 'select * from Protein' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ProteinAccessor(ResultSet aResultSet) throws SQLException {
		super(aResultSet);
	}

	/**
	 * This method will find all proteins and return a map.
	 *
	 * @param aConn Connection to read the spectrum File from.
	 * @return ProteinAccessor with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static TreeMap<String, Long> findAllProteinsWithID(Connection aConn) throws SQLException {
		TreeMap<String, Long> accession2IdMap = new TreeMap<String, Long>();
		// SELECT * is terrible for performance, replaced with specific query		
		//PreparedStatement ps = aConn.prepareStatement(getBasicSelect());
		PreparedStatement ps = aConn.prepareStatement("SELECT pr.proteinid, pr.accession FROM protein pr");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			accession2IdMap.put(rs.getString("accession"), rs.getLong("proteinid"));
		}
		rs.close();
		ps.close();
		return accession2IdMap;
	}


	/**
	 * This method will find all proteins and return proteinaccessors.
	 *
	 * @param aConn Connection to read the spectrum File from.
	 * @return ProteinAccessor-Set
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static ArrayList<ProteinAccessor> findAllProteinAccessors(Connection aConn) throws SQLException {
		ArrayList<ProteinAccessor> proteins = new ArrayList<ProteinAccessor>();
		//PreparedStatement ps = aConn.prepareStatement(getBasicSelect());
		// this is likely to crash if we have too many proteins ...
		PreparedStatement ps = aConn.prepareStatement("SELECT * FROM protein pr");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ProteinAccessor new_protein = new ProteinAccessor(rs);
			proteins.add(new_protein);
		}		
		rs.close();
		ps.close();
		return proteins;
	}

	//	/**
	//	 * This method will find all proteins for a certain experiment and return proteinaccessors.
	//	 *
	//	 * @param aConn Connection to read the spectrum File from.
	//	 * @param expID The experiment ID.
	//	 * @return ProteinAccessor-Set
	//	 * @throws SQLException when the retrieval did not succeed.
	//	 */
	//	public static ArrayList<ProteinAccessor> findAllProteinAccessorsbyExperimentID(Connection aConn, long expID) throws SQLException {
	//		ArrayList<ProteinAccessor> proteins = new ArrayList<ProteinAccessor>();
	//		//PreparedStatement ps = aConn.prepareStatement(getBasicSelect());
	//		// this is likely to crash if we have too many proteins ...
	//		PreparedStatement ps = aConn.prepareStatement("SELECT * FROM protein pr WHERE");
	//		ResultSet rs = ps.executeQuery();
	//		while (rs.next()) {
	//			ProteinAccessor new_protein = new ProteinAccessor(rs);
	//			proteins.add(new_protein);
	//		}		
	//		rs.close();
	//		ps.close();
	//		return proteins;
	//	}

	/**
	 * This method will find all protein accessions.
	 *
	 * @param aConn Connection to read the spectrum File from.
	 * @return ProteinAccessor-Set
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static TreeSet<String> findAllProteinAccessions(Connection aConn) throws SQLException {
		TreeSet<String> accSet = new TreeSet<String>();
		//PreparedStatement ps = aConn.prepareStatement(getBasicSelect());
		// this is likely to crash if we have too many proteins ...
		PreparedStatement ps = aConn.prepareStatement("SELECT * FROM protein pr");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ProteinAccessor new_protein = new ProteinAccessor(rs);
			accSet.add(new_protein.getAccession());
		}		
		rs.close();
		ps.close();
		return accSet;
	}

	/**
	 * This method will find a protein entry from the current connection, based on the specified protein ID.
	 *
	 * @param proteinID long with the protein ID of the protein to find.
	 * @param aConn Connection to read the spectrum File from.
	 * @return ProteinAccessor with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static ProteinAccessor findFromID(long proteinID, Connection aConn) throws SQLException {
		ProteinAccessor temp = null;
		PreparedStatement ps = aConn.prepareStatement(ProteinTableAccessor.getBasicSelect() + " WHERE " + ProteinTableAccessor.PROTEINID + " = ?");
		ps.setLong(1, proteinID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new ProteinAccessor(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}


	/**
	 * This method will find a protein entry that contains the string "_BLAST_"
	 *
	 * @param aConn Connection to read the spectrum File from.
	 * @return list of ProteinAccessors with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static List<ProteinTableAccessor> findBlastHits(Connection aConn) throws SQLException {
		List<ProteinTableAccessor> temp = new ArrayList<ProteinTableAccessor>();
		PreparedStatement ps = aConn.prepareStatement(ProteinTableAccessor.getBasicSelect() + " WHERE " + ProteinTableAccessor.SOURCE + " LIKE 'BLAST_%'");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp.add(new ProteinTableAccessor(rs));
		}
		rs.close();
		ps.close();
		return temp;
	}

	/**
	 * This method will find a protein entry from the current connection, based on the accession or description.
	 *
	 * @param accession String with the accession of the protein to find.
	 * @param description String with the description of the protein to find.
	 * @param aConn     Connection to read the spectrum File from.
	 * @return Protein with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static ProteinAccessor findFromAttributes(String accession, String description, Connection aConn) throws SQLException {

		ProteinAccessor temp = null;
		PreparedStatement ps;
		if (description == null) {
			ps = aConn.prepareStatement(ProteinTableAccessor.getBasicSelect() + " WHERE accession = ?");
			ps.setString(1, accession);
		} else if (accession == null) {
			ps = aConn.prepareStatement(ProteinTableAccessor.getBasicSelect() + " WHERE description = ?");
			ps.setString(1, description);
		} else {
			ps = aConn.prepareStatement(ProteinTableAccessor.getBasicSelect() + " WHERE accession = ? AND description = ?");
			ps.setString(1, accession);
			ps.setString(2, description);
		}
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new ProteinAccessor(rs);
		}
		rs.close();
		ps.close();

		return temp;
	}

	/**
	 * This method will find a protein entry from the current connection, based on the accession or description.
	 *
	 * @param accession String with the accession of the protein to find.
	 * @param description String with the description of the protein to find.
	 * @param aConn     Connection to read the spectrum File from.
	 * @return Protein with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static ProteinAccessor findFromAttributes(String accession, Connection aConn) throws SQLException {

		ProteinAccessor temp = null;
		PreparedStatement ps = null;
		if (accession != null) {
			ps = aConn.prepareStatement(ProteinTableAccessor.getBasicSelect() + " WHERE accession = ?");
			ps.setString(1, accession);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				temp = new ProteinAccessor(rs);
			}
			rs.close();
			ps.close();
		}

		return temp;
	}

	/**
	 * This method will find a protein entry from an accession, but only return the foreign key for uniprotID 
	 *
	 * @param accession String with the accession of the protein to find.
	 * @param description String with the description of the protein to find.
	 * @param aConn     Connection to read the spectrum File from.
	 * @return Protein with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static long findUniprotidFromAccession(String accession, Connection aConn) throws SQLException {
		PreparedStatement ps = null;
		long uniprotid = 0;
		if (accession != null) {
			ps = aConn.prepareStatement("SELECT protein.fk_uniprotentryid FROM protein WHERE protein.accession = ?");
			ps.setString(1, accession);
			ResultSet rs = ps.executeQuery();
			rs.next();
			uniprotid = rs.getLong("protein.fk_uniprotentryid");
			if (rs.next()) {System.err.println("More than one Uniprotentry for id");}
			rs.close();
			ps.close();
		}

		return uniprotid;
	}

	//    /**
	//     * Adds a new protein with a specific peptide id, accession and description to the database..
	//     * @param peptideID Long with the peptide id of the peptide belonging to the new protein.
	//     * @param accession String with the accession of the protein to find.
	//     * @param description String with the description of the protein to find.
	//     * @param conn The database connection object.
	//     * @return protein The newly created Protein object.
	//     * @throws SQLException when the persistence did not succeed.
	//     */
	//    public static ProteinAccessor upDateProteinEntry(Long protID, String accession, String description, String sequence,  Timestamp creationDate, Connection conn) throws SQLException{
	//    	//TODO UPDATE PROTEIn DESCRIPTIOn
	//    	HashMap<Object, Object> dataProtein = new HashMap<Object, Object>(4);
	//    	dataProtein.put(ProteinAccessor.PROTEINID, protID);
	//		dataProtein.put(ProteinAccessor.ACCESSION, accession);
	//		dataProtein.put(ProteinAccessor.DESCRIPTION, description);
	//		dataProtein.put(ProteinAccessor.SEQUENCE, sequence);
	//		dataProtein.put(ProteinAccessor.CREATIONDATE, creationDate);
	//		ProteinAccessor protein = new ProteinAccessor(dataProtein);
	//		protein.update(conn);
	//
	//		return protein;
	//    }

		/**
	     * Adds a new protein with a specific peptide id, accession and description to the database..
	     * @param peptideID Long with the peptide id of the peptide belonging to the new protein.
	     * @param accession String with the accession of the protein to find.
	     * @param description String with the description of the protein to find.
	     * @param source. The type of the fasta entry.
	     * @param uniprotId. The id of the uniprot table.
	     * @param conn The database connection object.
	     * @return protein The newly created Protein object.
	     * @throws SQLException when the persistence did not succeed.
	     */
	    public static ProteinAccessor addProteinWithPeptideID(Long peptideID, String accession, String description, String sequence, Type source, Long uniprotid, Connection conn) throws SQLException{
	    	HashMap<Object, Object> dataProtein = new HashMap<Object, Object>(6);
			dataProtein.put(ProteinAccessor.ACCESSION, accession);
			dataProtein.put(ProteinAccessor.DESCRIPTION, description);
			dataProtein.put(ProteinAccessor.SEQUENCE, sequence);
			dataProtein.put(ProteinAccessor.SOURCE, source.toString());
			dataProtein.put(ProteinAccessor.FK_UNIPROTID, uniprotid);
			ProteinAccessor protein = new ProteinAccessor(dataProtein);
			protein.persist(conn);
	
			// get the protein id from the generated keys.
			Long proteinID = (Long) protein.getGeneratedKeys()[0];
			
			// since this is a new protein we also create a new pep2prot entry
			// to link it to the peptide (no redundancy check needed)
			Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
			
			return protein;
	    }

	/** 
	 * Static method to retrieve all proteins that do not have a uniprotentry associated to them 
	 * 
	 * @param conn. Connection to SQL
	 * @return return_list. List of ProteinAccessor Objects for the proteins
	 * @throws SQLException
	 */
	public static List<ProteinAccessor> getAllProteinsWithoutUniProtEntry(Connection conn) throws SQLException {
		List<ProteinAccessor> return_list = new ArrayList<ProteinAccessor>();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM protein WHERE fk_uniprotentryid = -1");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ProteinAccessor this_accessor = new ProteinAccessor(rs);
			return_list.add(this_accessor);
		}
		return return_list; 
	}

	/** 
	 * Static method to retrieve all proteins that do not have a uniprotentry associated to them 
	 * 
	 * @param conn. Connection to SQL
	 * @return return_list. List of ProteinAccessor Objects for the proteins
	 * @throws SQLException
	 */
	public void updateUniprotForProtein(Connection conn) throws SQLException {
		// Check Length of uniProt accessions is 6 for swissprot and 6 or 12 for trembl, ignore other entries
		if (getAccession().length()  == 6 || getAccession().length() == 10 ) {
			// fetch new uniprotentry from webservice
			// Get UniProt information
			UniProtUtilities utils = new UniProtUtilities();
			// Map with UniProt entries with UniProtAccession as key
			Set<String> proteinSet = new TreeSet<String>();
			proteinSet.add(getAccession());
			TreeMap<String, UniProtEntryMPA> acc2uniprotentry = utils.processBatch(proteinSet, true);
			// create new uniprot entry
			TreeMap<Long, UniProtEntryMPA> protid2uniprotentry = new TreeMap<Long, UniProtEntryMPA>();
			protid2uniprotentry.put(getProteinid(), acc2uniprotentry.get(getAccession()));
			TreeMap<Long, Long> proteinID2uniprotIDmap = UniprotentryAccessor.addMultipleUniProtEntriesToDatabase(protid2uniprotentry, conn);
			// update protein data
            setFK_uniProtID(proteinID2uniprotIDmap.get(getProteinid()));
            update(conn);
			conn.commit();
		}
	}
	

	/**
	 * Adds a new protein with accession and description to the database..
	 * @param accession String with the accession of the protein to find.
	 * @param description String with the description of the protein to find.
	 * @param conn The database connection object.
	 * @return protein The newly created Protein object.
	 * @throws SQLException when the persistence did not succeed.
	 */
	public static ProteinAccessor addProteinToDatabase(String accession, String description, String sequence, Type type, Long UniProtID, Connection conn) throws SQLException{
		HashMap<Object, Object> dataProtein = new HashMap<Object, Object>(5);
		dataProtein.put(ProteinAccessor.ACCESSION, accession);
		dataProtein.put(ProteinAccessor.DESCRIPTION, description);
		dataProtein.put(ProteinAccessor.SEQUENCE, sequence);
		dataProtein.put(ProteinAccessor.FK_UNIPROTID, UniProtID);
		dataProtein.put(ProteinAccessor.SOURCE, type.toString());
		ProteinAccessor protein = new ProteinAccessor(dataProtein);
		protein.persist(conn);

		// get the protein id from the generated keys.
		@SuppressWarnings("unused")
		Long proteinID = (Long) protein.getGeneratedKeys()[0];		

		return protein;
	}

	@Override
	public String toString() {
		return ("" + this.iProteinid + " " + this.iAccession + " " + this.iDescription);
	}


	/**
	 * Adds a new protein with accession and description to the database..
	 * @param fastaEntryList The list of all FASTA entries.
	 * @param conn The database connection object.
	 * @return accession2idmap. A map that links protein accessions to SQL- proteinids 
	 * @throws SQLException when the persistence did not succeed.
	 * 
	 */
	public static void addMutlipleProteinsToDatabase(ArrayList<DigFASTAEntry> fastaEntryList, Connection conn) throws SQLException{
		
//		TreeMap<String, Long > accession2idMap = new TreeMap<String, Long>();
		
		// Create a sql statement
		PreparedStatement lStat = conn.prepareStatement("INSERT IGNORE INTO protein (proteinid, accession, description, sequence, fk_uniprotentryid, source, creationdate, modificationdate) values(?, ?, ?, ?, ?,?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);

		// Get iterator for the FASTA entries
		Iterator<DigFASTAEntry> protIter2 = fastaEntryList.iterator();		

		// Add all FASTA entries to the sql statement
		while (protIter2.hasNext()) {
			// Get next FASTA entry
			DigFASTAEntry fastaEntry = protIter2.next();

			// ProtID is unknown at the beginning
			lStat.setNull(1, 4);
			if(fastaEntry.getIdentifier() == null) {
				lStat.setNull(2, 12);
			} else {
				lStat.setObject(2, fastaEntry.getIdentifier());
			}
			if(fastaEntry.getDescription() == null) {
				lStat.setNull(3, -1);
			} else {
				lStat.setObject(3, fastaEntry.getDescription());
			}
			if(fastaEntry.getSequence() == null) {
				lStat.setNull(4, -1);
			} else {
				lStat.setObject(4, fastaEntry.getSequence());
			}
			if(fastaEntry.getUniProtID() == Long.MIN_VALUE) {
				lStat.setNull(5, -1);
			} else {
				lStat.setLong(5, fastaEntry.getUniProtID());
			}
			if(fastaEntry.getType() == null) {
				lStat.setNull(6, 12);
			} else {
				lStat.setObject(6, fastaEntry.getType().toString());
			}
			lStat.addBatch();
            lStat.executeBatch();
		}
	} 
	 
	
	// super slow method for checking if accession is available
	public static boolean isInDB(String accession, Connection conn) throws SQLException {
		PreparedStatement lStat = conn.prepareStatement("SELECT COUNT(*) FROM protein WHERE protein.accession LIKE ?");
		lStat.setString(1, accession);
		ResultSet result = lStat.executeQuery();
		result.next();
		int count = result.getInt(1);
		if (count == 1) {
			System.out.println("True");
			return true;
		} else if (count == 0) {
			System.out.println("False");
			return false;
		} else {
			System.out.println("terrible error of death");
			return true;	
		}
	}
	
	public static ArrayList<Long> find_uniprot_proteins_without_upentry(Connection conn) throws SQLException {
		ArrayList<Long> protein_ids = new ArrayList<Long>();
		PreparedStatement ps = conn.prepareStatement("SELECT protein.proteinid FROM protein "
				+ "WHERE protein.fk_uniprotentryid = ? AND protein.source = ?");
		ps.setLong(1, -1L);
		ps.setString(2, Type.UNIPROTSPROT.toString());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			protein_ids.add(rs.getLong("protein.proteinid"));
		}
		return protein_ids;
	}
}

