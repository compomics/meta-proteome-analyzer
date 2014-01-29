package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ProteinAccessor extends ProteinTableAccessor {

	/**
     * Calls the super class.
     * @param aParams
     */
	public ProteinAccessor(HashMap aParams) {
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
	 * This method will find all proteins.
	 *
	 * @param aConn Connection to read the spectrum File from.
	 * @return ProteinAccessor with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	@Deprecated
	public static Map<String, Long> findAllProteins(Connection aConn) throws SQLException {
		Map<String, Long> accession2IdMap = new TreeMap<String, Long>();
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			accession2IdMap.put(rs.getString("accession"), rs.getLong("proteinid"));
		}
		rs.close();
		ps.close();
		return accession2IdMap;
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
//            while (rs.next()) {
//                temp = new ProteinAccessor(rs);
//            }
            if (rs.next()) {
                temp = new ProteinAccessor(rs);
            }
            temp = new ProteinAccessor(rs);
            rs.close();
            ps.close();
        }
        
        return temp;
	}
    
	/**
     * Adds a new protein with a specific peptide id, accession and description to the database..
     * @param peptideID Long with the peptide id of the peptide belonging to the new protein.
     * @param accession String with the accession of the protein to find.
     * @param description String with the description of the protein to find.
     * @param conn The database connection object.
     * @return protein The newly created Protein object.
     * @throws SQLException when the persistence did not succeed.
     */
    public static ProteinAccessor addProteinWithPeptideID(Long peptideID, String accession, String description, String sequence, Connection conn) throws SQLException{
    	HashMap<Object, Object> dataProtein = new HashMap<Object, Object>(4);
		dataProtein.put(ProteinAccessor.ACCESSION, accession);
		dataProtein.put(ProteinAccessor.DESCRIPTION, description);
		dataProtein.put(ProteinAccessor.SEQUENCE, sequence);
		ProteinAccessor protein = new ProteinAccessor(dataProtein);
		protein.persist(conn);

		// get the protein id from the generated keys.
		Long proteinID = (Long) protein.getGeneratedKeys()[0];
		
		// since this is a new protein we also create a new pep2prot entry
		// to link it to the peptide (no redundancy check needed)
		Pep2prot.linkPeptideToProtein(peptideID, proteinID, conn);
		
		return protein;
    }
    
    @Override
    public String toString() {
    	return ("" + iProteinid + " " + iAccession + " " + iDescription);
    }

}
