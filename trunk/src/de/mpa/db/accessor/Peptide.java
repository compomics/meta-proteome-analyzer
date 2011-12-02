package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Peptide extends PeptideTableAccessor {
	
	/**
     * Calls the super class.
     * @param aParams
     */
	public Peptide(HashMap aParams) {
		super(aParams);
	}
	
	/**
	 * This constructor allows the creation of the 'PeptideTableAccessor' object based on a resultset
	 * obtained by a 'select * from Peptide' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public Peptide(ResultSet aResultSet) throws SQLException {
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
    public static Peptide findFromSequence(String sequence, Connection aConn) throws SQLException {

        Peptide temp = null;
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " WHERE sequence = ?");
        ps.setString(1, sequence);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Peptide(rs);
        }
        rs.close();
        ps.close();

        return temp;
    }
    
    public static List<Peptide> findFromID(long peptideID, Connection aConn) throws SQLException {
    	
    	List<Peptide> temp = new ArrayList<Peptide>();
    	PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " WHERE " + PEPTIDEID + " = ?");
        ps.setLong(1, peptideID);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp.add(new Peptide(rs));
        }
        rs.close();
        ps.close();

    	return temp;
    }
	
}
