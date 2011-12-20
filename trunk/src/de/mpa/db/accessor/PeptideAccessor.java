package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PeptideAccessor extends PeptideTableAccessor {
	
	/**
     * Calls the super class.
     * @param aParams
     */
	public PeptideAccessor(HashMap aParams) {
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
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new PeptideAccessor(rs);
        }
        rs.close();
        ps.close();

        return temp;
    }
    
	/**
     * This method return the peptide id the current connection, based on the sequence.
     * If no peptide entry is available in the database, a new entry is provided.
     *
     * @param sequence String with the sequence of the peptide to find.
     * @param aConn Connection to read the spectrum file from.
     * @return long peptide id.
     * @throws SQLException when the retrieval did not succeed.
     */
    
    public static long findPeptideIDfromSequence(String sequence, Connection aConn) throws SQLException {
    	PeptideAccessor peptideHit = findFromSequence(sequence, aConn);
    	long peptideid;
    	
    	// peptideHit != null
		if (peptideHit == null) { // sequence not yet in database
			HashMap<Object, Object> dataPeptide = new HashMap<Object, Object>(2);
			dataPeptide.put(PeptideAccessor.SEQUENCE, sequence);
			peptideHit = new PeptideAccessor(dataPeptide);
			peptideHit.persist(aConn);					
			// Get the peptide id from the generated keys.
			peptideid = (Long) peptideHit.getGeneratedKeys()[0];
		} else {
			peptideid = peptideHit.getPeptideid();
		}		
		return peptideid;
    }
    
    
    public static List<PeptideAccessor> findFromID(long peptideID, Connection aConn) throws SQLException {
    	
    	List<PeptideAccessor> temp = new ArrayList<PeptideAccessor>();
    	PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " WHERE " + PEPTIDEID + " = ?");
        ps.setLong(1, peptideID);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp.add(new PeptideAccessor(rs));
        }
        rs.close();
        ps.close();

    	return temp;
    }
    
    
    public static List<PeptideAccessor> findFromMultipleIDs(List<Long> peptideIDs, Connection aConn) throws SQLException {
    	
    	List<PeptideAccessor> temp = new ArrayList<PeptideAccessor>();
    	String statement = getBasicSelect() + " WHERE " + PEPTIDEID + " = ?";
    	for (int i = 1; i < peptideIDs.size(); i++) {
			statement += "OR " + PEPTIDEID + " = ?";
		}
    	PreparedStatement ps = aConn.prepareStatement(statement);
    	for (int i = 0; i < peptideIDs.size(); i++) {
            ps.setLong(i+1, peptideIDs.get(i));
		}
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
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
									    			  " ON peptide.peptideid = speclibentry.fk_peptideid" +
									    			  " INNER JOIN libspectrum" +
									    			  " ON speclibentry.fk_spectrumid = libspectrum.libspectrumid" +
									    			  " WHERE libspectrum.libspectrumid = ?");
        ps.setLong(1, spectrumID);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp.add(new PeptideAccessor(rs));
        }
        rs.close();
        ps.close();

    	return temp;
    }
	
}
