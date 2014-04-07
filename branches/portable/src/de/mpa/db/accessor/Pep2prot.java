package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
     * This method will find a pep2prot entry from the current connection, based on foreign protein and peptide IDs.
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
        																   " AND " + FK_PROTEINID + " = ?");
        ps.setLong(1, peptideID);
        ps.setLong(2, proteinID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp = new Pep2prot(rs);
        }
        rs.close();
        ps.close();

        return temp;
    }
    
	/**
     * This method will find  pep2prot entries from the current connection, based on foreign protein ID.
     *
     * @param proteinID long with the protein ID of the link to find.
     * @param conn  The database connection.
     * @return List<Pep2prot> with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<Pep2prot> findLinkByProteinID(Long proteinID, Connection conn) throws SQLException {
    	List<Pep2prot> temp = new ArrayList<Pep2prot>();
        PreparedStatement ps = conn.prepareStatement(getBasicSelect() + " WHERE " + FK_PROTEINID + " = ?");
        ps.setLong(1, proteinID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Pep2prot(rs));
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
		dataPep2Prot.put(Pep2prot.FK_PROTEINID, proteinID);
		Pep2prot pep2prot = new Pep2prot(dataPep2Prot);
		pep2prot.persist(conn);
		return pep2prot;
    }
    
    /**
     * This method will find a list of protein IDs from the current connection, based on a foreign peptide ID.
     *
     * @param peptideID long with the peptide ID of the links to find.
     * @param aConn     Connection to read the spectrum File from.
     * @return List<Long> with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<Long> findProteinIDsFromPeptideID(Long peptideID, Connection aConn) throws SQLException {
    	
    	List<Long> temp = new ArrayList<Long>();
        PreparedStatement ps = aConn.prepareStatement("SELECT " + FK_PROTEINID + " FROM pep2prot" +
        											  " WHERE " + FK_PEPTIDEID  + " = ?");
        ps.setLong(1, peptideID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(rs.getLong(FK_PROTEINID));
        }
        rs.close();
        ps.close();

        return temp;
    }
}
