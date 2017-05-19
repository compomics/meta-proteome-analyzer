package de.mpa.db.mysql.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Spec2pep extends Spec2pepTableAccessor {

    /**
     * Calls the super class.
     * @param params
     */
    public Spec2pep(HashMap params){
        super(params);
    }
    
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Spec2pep(ResultSet aRS) throws SQLException {
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
    public static List<Spec2pep> getEntriesWithinPrecursorRange(double precursorMz, double tolMz, Connection aConn) throws SQLException {
    	List<Spec2pep> temp = new ArrayList<Spec2pep>();
        PreparedStatement ps = aConn.prepareStatement(Spec2pepTableAccessor.getBasicSelect() +
        		" INNER JOIN spectrum" + " ON spec2pep." + Spec2pep.FK_SPECTRUMID + " = spectrum." + Spectrum.SPECTRUMID +
        		" WHERE " + Spectrum.PRECURSOR_MZ +
        		" BETWEEN ? AND ?");
        ps.setDouble(1, precursorMz - tolMz);
        ps.setDouble(2, precursorMz + tolMz);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Spec2pep(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    
    
    /**
     * This method will find a spec2pep entry from the current connection, based on the specified spectrumid.
     *
     * @param aSpectrumID long with the spectrumid of the spectrum file to find.
     * @param aConn Connection to read the spectrum File from.
     * @return Spec2pep with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<Spec2pep> findFromID(long aSpectrumID, Connection aConn) throws SQLException {
    	List<Spec2pep> temp = new ArrayList<Spec2pep>();
        PreparedStatement ps = aConn.prepareStatement(Spec2pepTableAccessor.getBasicSelect() +
        		" WHERE " + Spec2pep.FK_SPECTRUMID + "= ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Spec2pep(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }

	/**
     * This method will find a spec2pep entry from the current connection, based on foreign spectrum- and peptideIDs.
     *
     * @param spectrumID long with the spectrum ID of the link to find.
     * @param peptideID long with the peptide ID of the link to find.
     * @param aConn     Connection to read the spectrum File from.
     * @return Spec2pep with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Spec2pep findLink(Long spectrumID, Long peptideID, Connection aConn) throws SQLException {

    	Spec2pep temp = null;
        PreparedStatement ps = aConn.prepareStatement(Spec2pepTableAccessor.getBasicSelect() +
        		" WHERE " + Spec2pepTableAccessor.FK_SPECTRUMID + " = ?" +
        		" AND " + Spec2pepTableAccessor.FK_PEPTIDEID + " = ?");
        ps.setLong(1, spectrumID);
        ps.setLong(2, peptideID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp = new Spec2pep(rs);
        }
        rs.close();
        ps.close();

        return temp;
    }
}
