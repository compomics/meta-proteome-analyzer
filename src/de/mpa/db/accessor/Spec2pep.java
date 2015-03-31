package de.mpa.db.accessor;

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
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() +
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
    
    // XXX
    /**
     * Returns linked libspectrum entries belonging to a specified experiment entry.
     * @param experimentID
     * @param aConn
     * @return
     */
    public static List<Spec2pep> getEntriesFromExperimentID(long experimentID, Connection aConn) throws SQLException {
    	List<Spec2pep> temp = new ArrayList<Spec2pep>();
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() +
        		" INNER JOIN libspectrum ON spec2pep." + Spec2pep.FK_SPECTRUMID + " = libspectrum." + Libspectrum.FK_SPECTRUMID +
        		" WHERE " + Libspectrum.FK_EXPERIMENTID + " = ?");
        ps.setLong(1, experimentID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	temp.add(new Spec2pep(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    
    /**
     * Returns the Spec2pep entries within a certain precursor range.
     * @param precursorMz The precursor mass.
     * @param tolMz The mass tolerance.
     * @param experimentID The experiment id.
     * @param conn The database connection.
     * @return List of Spec2pep entries.
     * @throws SQLException when the database query has not been successful.
     */
    public static List<Spec2pep> getEntriesWithinPrecursorRangeFromExperimentID(double precursorMz, double tolMz, long experimentID, Connection conn) throws SQLException {
    	List<Spec2pep> temp = new ArrayList<Spec2pep>();
		PreparedStatement ps = conn.prepareStatement(getBasicSelect() +
				" INNER JOIN spectrum ON spec2pep." + Spec2pep.FK_SPECTRUMID + " = spectrum." + Spectrum.SPECTRUMID + 
				" INNER JOIN libspectrum ON spec2pep." + Spec2pep.FK_SPECTRUMID + " = libspectrum." + Libspectrum.FK_SPECTRUMID +
				" WHERE spectrum." + Spectrum.PRECURSOR_MZ + " BETWEEN ? AND ?" + 
				" AND libspectrum." + Libspectrum.FK_EXPERIMENTID + " = ?");
    	ps.setDouble(1, precursorMz - tolMz);
    	ps.setDouble(2, precursorMz + tolMz);
    	ps.setLong(3, experimentID);
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
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() +
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
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() +
        		" WHERE " + FK_SPECTRUMID + " = ?" +
        		" AND " + FK_PEPTIDEID  + " = ?");
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
