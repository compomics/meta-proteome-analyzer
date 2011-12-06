package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Pep2prot extends Pep2protTableAccessor {

	/**
     * Calls the super class.
     * @param aParams
     */
	public Pep2prot(HashMap aParams) {
		super(aParams);
	}

	/**
	 * This constructor allows the creation of the 'Pep2protTableAccessor' object based on a resultset
	 * obtained by a 'select * from Pep2prot' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public Pep2prot(ResultSet aResultSet) throws SQLException {
		super(aResultSet);
	}

	/**
     * This method will find a pep2prot entry from the current connection, based on foreign protein- and peptideIDs.
     *
     * @param peptideID long with the peptide ID of the link to find.
     * @param proteinID long with the protein ID of the link to find.
     * @param conn  The database connection.
     * @return Pep2prot with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Pep2prot findLink(Long peptideID, Long proteinID, Connection conn) throws SQLException {

    	Pep2prot temp = null;
        PreparedStatement ps = conn.prepareStatement(getBasicSelect() + " WHERE " + FK_PEPTIDEID  + " = ?" +
        																   " AND " + FK_PROTEINSID + " = ?");
        ps.setLong(1, peptideID);
        ps.setLong(2, proteinID);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Pep2prot(rs);
        }
        rs.close();
        ps.close();

        return temp;
    }
    
    /**
     * Links peptide to protein: Creates a new pep2prot entry, based on peptideID and proteinID.
     * @param peptideID long with the peptide ID of the link to find.
     * @param proteinID long with the protein ID of the link to find.
     * @param conn  The database connection.
     * @return pep2prot The new created Pep2prot entry.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Pep2prot linkPeptideToProtein(Long peptideID, Long proteinID, Connection conn) throws SQLException{
    	HashMap<Object, Object> dataPep2Prot = new HashMap<Object, Object>(3);
		dataPep2Prot.put(Pep2prot.FK_PEPTIDEID, peptideID);
		dataPep2Prot.put(Pep2prot.FK_PROTEINSID, proteinID);
		Pep2prot pep2prot = new Pep2prot(dataPep2Prot);
		pep2prot.persist(conn);
		return pep2prot;
    }
}
