package de.mpa.db.accessor;

import gnu.trove.map.hash.TLongDoubleHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.mpa.algorithms.Interval;

public class Searchspectrum extends SearchspectrumTableAccessor {
	
	
    /**
     * Calls the super class.
     * @param params
     */
	public Searchspectrum(HashMap params) {
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
         		" WHERE searchspectrumid = ?");
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
    
    /**
     * Returns the number of searchspectrum entries for the specified experiment ID.
     * @param experimentID the database ID of the experiment
     * @param conn the database connection
     * @return the spectral count
     * @throws SQLException when the retrieval did not succeed
     */
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

	/**
	 * Returns whether the experiment with the specified ID has any
	 * searchspectrum entries associated with it.
	 * @param experimentID the database ID of the experiment
	 * @param conn the database connection
	 * @return <code>true</code> if the experiment has spectra, <code>false</code> otherwise
	 * @throws SQLException when the retrieval did not succeed
	 */
    public static boolean hasSearchSpectra(long experimentID, Connection conn) throws SQLException {
    	boolean res = false;
    	PreparedStatement ps = conn.prepareStatement(
    			"SELECT EXISTS(SELECT 1 FROM searchspectrum ss WHERE ss.fk_experimentid = ?)");
    	ps.setLong(1, experimentID);
    	ResultSet rs = ps.executeQuery();
    	rs.next();
    	res = rs.getBoolean(1);
    	if (rs.next()) {
    		throw new SQLException("Count query returned more than one result!");
    	}
    	rs.close();
    	ps.close();
    	return res;
    }

	/**
	 * Returns the total ion current values of all spectra belonging to the
	 * specified experiment mapped by their search spectrum ID.
	 * @param experimentID the database ID of the experiment
	 * @param conn the database connection
	 * @return a map containing searchspectrumID-to-TIC pairs
	 * @throws SQLException when the retrieval did not succeed
	 */
    public static TLongDoubleHashMap getTICsByExperimentID(long experimentID, Connection conn) throws SQLException {
    	TLongDoubleHashMap res = new TLongDoubleHashMap();
//    	PreparedStatement ps = conn.prepareStatement(
//    			"SELECT ss.searchspectrumid, s.total_int FROM searchspectrum ss " +
//    			"INNER JOIN spectrum s ON ss.fk_spectrumid = s.spectrumid " +
////    			"WHERE ss.fk_experimentid = ?",
////				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
//				"WHERE ss.fk_experimentid = ?");
//    	ps.setLong(1, experimentID);
////		ps.setFetchSize(Integer.MIN_VALUE);
    	
    	PreparedStatement ps = conn.prepareStatement(
    			"SELECT ss.fk_spectrumid FROM searchspectrum ss " +
    			"WHERE ss.fk_experimentid = ?");
    	ps.setLong(1, experimentID);
    	
    	List<Long> ids = new ArrayList<Long>();
		
		long startTime = System.currentTimeMillis();
    	ResultSet rs = ps.executeQuery();
    	System.out.println(System.currentTimeMillis() - startTime);
    	while (rs.next()) {
//			res.put(rs.getLong(1), rs.getDouble(2));
    		ids.add(rs.getLong(1));
		}
    	System.out.println(System.currentTimeMillis() - startTime);
    	rs.close();

    	// build id intervals
    	Collections.sort(ids);
    	List<Interval> intervals = new ArrayList<Interval>();
    	Long oldId = ids.get(0);
    	Interval interval = new Interval(oldId, oldId);
    	int i;
    	for (i = 1; i < ids.size(); i++) {
			Long newId = ids.get(i);
			if (!newId.equals(oldId + 1)) {
				interval.setRightBorder(oldId);
				intervals.add(interval);
				interval = new Interval(newId, newId);
			}
			oldId = newId;
		}
    	interval.setRightBorder(ids.get(i - 1));
    	intervals.add(interval);
    	
    	System.out.println("# intervals:" + intervals.size());
    	
//    	System.out.println(StringUtils.join(ids, ','));
    	
    	String stmt = "SELECT s.total_int FROM spectrum s WHERE s.spectrumid ";
    	boolean first = true;
    	for (Interval iv : intervals) {
    		if (first) {
    			first = false;
    		} else {
    			stmt += " OR s.spectrumid ";
    		}
    		stmt += "BETWEEN " + (long) iv.getLeftBorder() + " AND " + (long) iv.getRightBorder();
		}
    	System.out.println(stmt);
    	
    	ps = conn.prepareStatement(stmt);
    	System.out.println("executing secondary query...");
    	startTime = System.currentTimeMillis();
    	rs = ps.executeQuery();
    	System.out.println(System.currentTimeMillis() - startTime);
    	int size = 0;
    	while (rs.next()) {
//			res.put(rs.getLong(1), rs.getDouble(2));
    		size++;
		}
    	System.out.println(System.currentTimeMillis() - startTime);
    	System.out.println("# ids: " + ids.size() + " # res: " + size);
    	
    	ps.close();
    	return res;
    }
    
}
