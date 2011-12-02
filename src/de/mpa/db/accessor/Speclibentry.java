package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Speclibentry extends SpeclibentryTableAccessor {

    /**
     * Calls the super class.
     * @param params
     */
    public Speclibentry(HashMap params){
        super(params);
    }
    
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Speclibentry(ResultSet aRS) throws SQLException {
        super(aRS);
    }
    
    /**
     * Returns linked libspectrum entries within a specified precursor range.
     * @param precursorMz
     * @param tolMz
     * @param aConn
     * @return
     * @throws SQLException
     */
    public static List<Speclibentry> getEntriesWithinPrecursorRange(double precursorMz, double tolMz, Connection aConn) throws SQLException {
    	List<Speclibentry> temp = new ArrayList<Speclibentry>();
//        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where precursor_mz >= ? and precursor_mz <= ?");
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " INNER JOIN libspectrum" +
        																 " ON speclibentry." + Speclibentry.FK_SPECTRUMID +
        																 " = libspectrum." + Libspectrum.LIBSPECTRUMID +
        																 " WHERE " + Libspectrum.PRECURSOR_MZ + " >= ?" +
        																 " AND "   + Libspectrum.PRECURSOR_MZ + " <= ?");
        ps.setDouble(1, precursorMz - tolMz);
        ps.setDouble(2, precursorMz + tolMz);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp.add(new Speclibentry(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    
    /**
     * This method will find a speclibentry entry from the current connection, based on the specified spectrumid.
     *
     * @param aSpectrumID long with the spectrumid of the spectrum file to find.
     * @param aConn Connection to read the spectrum File from.
     * @return Speclibentry with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Speclibentry findFromID(long aSpectrumID, Connection aConn) throws SQLException {
    	Speclibentry temp = null;
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where speclibid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Speclibentry(rs);
        }
        rs.close();
        ps.close();
        if (counter != 1) {
            SQLException sqe = new SQLException("Select based on spectrum ID '" + aSpectrumID + "' resulted in " + counter + " results instead of 1!");
            sqe.printStackTrace();
            throw sqe;
        }
        return temp;
    }

	/**
     * This method will find a speclibentry entry from the current connection, based on foreign spectrum- and peptideIDs.
     *
     * @param spectrumID long with the spectrum ID of the link to find.
     * @param peptideID long with the peptide ID of the link to find.
     * @param aConn     Connection to read the spectrum File from.
     * @return Speclibentry with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Speclibentry findLink(Long spectrumID, Long peptideID, Connection aConn) throws SQLException {

    	Speclibentry temp = null;
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " WHERE " + FK_SPECTRUMID + " = ?" +
        																   " AND " + FK_PEPTIDEID  + " = ?");
        ps.setLong(1, spectrumID);
        ps.setLong(2, peptideID);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Speclibentry(rs);
        }
        rs.close();
        ps.close();

        return temp;
    }
}
