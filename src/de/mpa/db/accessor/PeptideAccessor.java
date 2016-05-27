package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Subclassed DAO for the peptide table.
 * @author T. Muth, A. Behne
 *
 */
public class PeptideAccessor extends PeptideTableAccessor {
	
	/**
     * Calls the super class.
     * @param aParams
     */
	public PeptideAccessor(@SuppressWarnings("rawtypes") HashMap aParams) {
		super(aParams);
	}
	
	/**
	 * This constructor allows the creation of the 'PeptideTableAccessor' object based on a resultset
	 * obtained by a 'select * from Peptide' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public PeptideAccessor(ResultSet aResultSet) throws SQLException {
		super(aResultSet);
	}

	/**
     * This method will find a peptide entry from the current connection, based on the sequence.
     *
     * @param sequence String with the sequence of the peptide to find.
     * @param aConn Connection to read the spectrum File from.
     * @return Peptide with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static PeptideAccessor findFromSequence(String sequence, Connection aConn) throws SQLException {

        PeptideAccessor temp = null;
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " WHERE sequence = ?");
        ps.setString(1, sequence);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp = new PeptideAccessor(rs);
        }
        rs.close();
        ps.close();

        return temp;
    }
    
    /**
     * Finds a particular peptide database object for a given peptide id.
     * @param peptideID The peptide id.
     * @param conn The database connection.
     * @return The PeptideAccessor object.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static PeptideAccessor findFromID(long peptideID, Connection conn) throws SQLException {
    	PeptideAccessor temp = null;
    	PreparedStatement ps = conn.prepareStatement(getBasicSelect() + " WHERE " + PEPTIDEID + " = ?");
        ps.setLong(1, peptideID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp = new PeptideAccessor(rs);
        }
        rs.close();
        ps.close();

    	return temp;
    }
    
    /**
     * Finds multiple peptide objects for a list of peptide ids.
     * @param peptideIDs List of peptide ids.
     * @param conn The databse connection.
     * @return
     * @throws SQLException
     */
    public static List<PeptideAccessor> findFromMultipleIDs(List<Long> peptideIDs, Connection conn) throws SQLException {
    	
    	List<PeptideAccessor> temp = new ArrayList<PeptideAccessor>();
    	String statement = getBasicSelect() + " WHERE " + PEPTIDEID + " = ?";
    	for (int i = 1; i < peptideIDs.size(); i++) {
			statement += "OR " + PEPTIDEID + " = ?";
		}
    	PreparedStatement ps = conn.prepareStatement(statement);
    	for (int i = 0; i < peptideIDs.size(); i++) {
            ps.setLong(i+1, peptideIDs.get(i));
		}
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new PeptideAccessor(rs));
        }
        rs.close();
        ps.close();

    	return temp;
    }
    
    
    public static List<PeptideAccessor> findFromSpectrumID(long spectrumID, Connection aConn) throws SQLException {
    	
    	List<PeptideAccessor> temp = new ArrayList<PeptideAccessor>();
    	PreparedStatement ps = aConn.prepareStatement(getBasicSelect() +
									    			  " INNER JOIN speclibentry" +
									    			  " ON peptide.peptideid = spec2pep.fk_peptideid" +
									    			  " INNER JOIN libspectrum" +
									    			  " ON spec2pep.fk_spectrumid = libspectrum.libspectrumid" +
									    			  " WHERE libspectrum.libspectrumid = ?");
        ps.setLong(1, spectrumID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new PeptideAccessor(rs));
        }
        rs.close();
        ps.close();

    	return temp;
    }
	
}
