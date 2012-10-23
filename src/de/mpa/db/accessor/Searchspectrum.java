package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Searchspectrum extends SearchspectrumTableAccessor {
    /**
     * Calls the super class.
     * @param params
     */
    public Searchspectrum(HashMap params){
        super(params);
    }
    
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Searchspectrum(ResultSet aRS) throws SQLException {
        super(aRS);
    }
    
    /**
     * Finds the Searchspectrum from a given searchspectrum id.
     * @param searchspectrumid The search spectrum id given from the actual spectrum.
     * @param conn The database connection.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Searchspectrum findFromSearchSpectrumID(long searchspectrumid, Connection conn) throws SQLException {
    	 Searchspectrum temp = null;
         PreparedStatement ps = conn.prepareStatement(Searchspectrum.getBasicSelect() +
         		" WHERE searchspectrumid = ? ORDER BY creationdate");
         ps.setLong(1, searchspectrumid);
         ResultSet rs = ps.executeQuery();
         while (rs.next()) {
             temp = new Searchspectrum(rs);
         }
         rs.close();
         ps.close();
         return temp;
    }
    
    /**
	 * This method finds all search spectra from a particular experiment.
	 * @param experimentid The experiment id.
	 * @param conn The database connection.
	 * @return List of retrieved search spectra.
	 * @throws SQLException when the retrieval did not succeed. 
	 */
    public static List<Searchspectrum> findFromExperimentID(long experimentid, Connection conn) throws SQLException {
    	List<Searchspectrum> spectra = new ArrayList<Searchspectrum>();
		PreparedStatement ps = conn.prepareStatement(getBasicSelect() + " WHERE fk_experimentid = ?");
		ps.setLong(1, experimentid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			spectra.add(new Searchspectrum(rs));
		}
		rs.close();
		ps.close();
		return spectra;
    }
    
    /**
	 * This method finds all search spectra from a particular experiment.
	 * @param experimentid The experiment id.
	 * @param conn The database connection.
	 * @return List of retrieved search spectra.
	 * @throws SQLException when the retrieval did not succeed. 
	 */
    public static Searchspectrum findFromSpectrumIDAndExperimentID(long spectrumid, long experimentid, Connection conn) throws SQLException {
    	Searchspectrum spectrum = null;
		PreparedStatement ps = conn.prepareStatement(getBasicSelect() + " WHERE fk_spectrumid = ? AND fk_experimentid = ?");
		ps.setLong(1, spectrumid);
		ps.setLong(2, experimentid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			spectrum = new Searchspectrum(rs);
		}
		rs.close();
		ps.close();
		return spectrum;
    }
    
    public static int getSpectralCountFromExperimentID(long experimentID, Connection conn) throws SQLException {
    	int specCount = -1;
    	PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM searchspectrum  " +
    			"WHERE " + FK_EXPERIMENTID + " = ?");
    	ps.setLong(1, experimentID);
    	ResultSet rs = ps.executeQuery();
    	rs.next();
    	specCount = rs.getInt(1);
    	if (rs.next()) {
    		throw new SQLException("Count query returned more than one result!");
    	}
    	rs.close();
    	ps.close();
    	return specCount;
    }
    
}
