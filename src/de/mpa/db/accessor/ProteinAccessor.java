package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import de.mpa.io.fasta.DigFASTAEntry;

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
	public static TreeSet<ProteinAccessor> findAllProteinAccessors(Connection aConn) throws SQLException {
		TreeSet<ProteinAccessor> proteins = new TreeSet<ProteinAccessor>();
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
       PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " WHERE " + PROTEINID + " = ?");
       ps.setLong(1, proteinID);
       ResultSet rs = ps.executeQuery();
       while (rs.next()) {
           temp = new ProteinAccessor(rs);
       }
       rs.close();
       ps.close();
       return temp;
   }
   
//	/**
//    * This method will find a protein entry that contains the string "_BLAST_"
//   *
//   * @param aConn Connection to read the spectrum File from.
//   * @return list of ProteinAccessors with the data.
//   * @throws SQLException when the retrieval did not succeed.
//   */
//  public static List<ProteinTableAccessor> findBlastHits(Connection aConn) throws SQLException {
//  	  List<ProteinTableAccessor> temp = new ArrayList<ProteinTableAccessor>();
//  	  System.out.println(getBasicSelect() + " WHERE " + ACCESSION + " LIKE '%_BLAST_%'");
//      PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " WHERE " + ACCESSION + " LIKE '%_BLAST_%'");
//      ResultSet rs = ps.executeQuery();
//      while (rs.next()) {
//          temp.add(new ProteinTableAccessor(rs));
//      }
//      rs.close();
//      ps.close();
//      return temp;
//  }

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
            ps = aConn.prepareStatement(getBasicSelect() + " WHERE accession = ?");
            ps.setString(1, accession);
        } else if (accession == null) {
            ps = aConn.prepareStatement(getBasicSelect() + " WHERE description = ?");
            ps.setString(1, description);
        } else {
            ps = aConn.prepareStatement(getBasicSelect() + " WHERE accession = ? AND description = ?");
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
            ps = aConn.prepareStatement(getBasicSelect() + " WHERE accession = ?");
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
    
//	/**
//     * Adds a new protein with a specific peptide id, accession and description to the database..
//     * @param peptideID Long with the peptide id of the peptide belonging to the new protein.
//     * @param accession String with the accession of the protein to find.
//     * @param description String with the description of the protein to find.
//     * @param conn The database connection object.
//     * @return protein The newly created Protein object.
//     * @throws SQLException when the persistence did not succeed.
//     */
//    public static ProteinAccessor addProteinWithPeptideID(Long peptideID, String accession, String description, String sequence, Connection conn) throws SQLException{
//    	HashMap<Object, Object> dataProtein = new HashMap<Object, Object>(4);
//		dataProtein.put(ProteinAccessor.ACCESSION, accession);
//		dataProtein.put(ProteinAccessor.DESCRIPTION, description);
//		dataProtein.put(ProteinAccessor.SEQUENCE, sequence);
//		ProteinAccessor protein = new ProteinAccessor(dataProtein);
//		protein.persist(conn);
//
//		// get the protein id from the generated keys.
//		Long proteinID = (Long) protein.getGeneratedKeys()[0];
//		
//		// since this is a new protein we also create a new pep2prot entry
//		// to link it to the peptide (no redundancy check needed)
//		Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
//		
//		return protein;
//    }
    
	/**
     * Adds a new protein with accession and description to the database..
     * @param accession String with the accession of the protein to find.
     * @param description String with the description of the protein to find.
     * @param conn The database connection object.
     * @return protein The newly created Protein object.
     * @throws SQLException when the persistence did not succeed.
     */
    public static ProteinAccessor addProteinToDatabase(String accession, String description, String sequence, String type, Long UniProtID, Connection conn) throws SQLException{
    	HashMap<Object, Object> dataProtein = new HashMap<Object, Object>(5);
		dataProtein.put(ProteinAccessor.ACCESSION, accession);
		dataProtein.put(ProteinAccessor.DESCRIPTION, description);
		dataProtein.put(ProteinAccessor.SEQUENCE, sequence);
		dataProtein.put(ProteinAccessor.FK_UNIPROTID, UniProtID);
		dataProtein.put(ProteinAccessor.SOURCE, type);
		ProteinAccessor protein = new ProteinAccessor(dataProtein);
		protein.persist(conn);

		// get the protein id from the generated keys.
		@SuppressWarnings("unused")
		Long proteinID = (Long) protein.getGeneratedKeys()[0];		
		
		return protein;
    }
    
    @Override
    public String toString() {
    	return ("" + iProteinid + " " + iAccession + " " + iDescription);
    }


    /**
     * Adds a new protein with accession and description to the database..
     * @param fastaEntryList The list of all FASTA entries.
     * @param conn The database connection object.
     * @throws SQLException when the persistence did not succeed.
     */
    public static void addMulibleProteinsToDatabase(ArrayList<DigFASTAEntry> fastaEntryList, Connection conn) throws SQLException{
    	
    	// Create a sql statement
    	PreparedStatement lStat = conn.prepareStatement("INSERT INTO protein (proteinid, accession, description, sequence, fk_uniprotentryid, source, creationdate, modificationdate) values(?, ?, ?, ?, ?,?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
		
    	
    				// Get iterator for the FASTA entries
    				Iterator<DigFASTAEntry> protIter2 = fastaEntryList.iterator();		

    				// Add all FASTA entries to the sql statement
    				while (protIter2.hasNext()) {
    					// Get next FASTA entry
    					DigFASTAEntry fastaEntry = (DigFASTAEntry) protIter2.next();

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
    					lStat.addBatch();;
    					
    				}
    				lStat.executeBatch();
    }
}
    				
