package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import de.mpa.db.MapContainer;

public class Spectrum extends SpectrumTableAccessor {

	public Spectrum(ResultSet rs) throws SQLException {
		super(rs);
	}

    /**
     * Calls the super class.
     * @param params
     */
    public Spectrum(HashMap params){
        super(params);
    }

    /**
     * This method will find a spectrum file from the current connection, based on the spectrum name.
     *
     * @param title String with the spectrum name of the spectrum file to find.
     * @param aConn     Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Spectrum findFromTitle(String title, Connection aConn) throws SQLException {
//    	String formatted = "";
//    	if (omssa) {
//    		formatted = title.replace("\\\\", "/");
//    	} else {
//    		formatted = title.replace('\\', '/');
//    	}
    	Spectrum temp = null;
        // Only get the last 1500 records
        PreparedStatement ps = aConn.prepareStatement(Spectrum.getBasicSelect() +
        		" WHERE title = ? ORDER BY creationdate");
        ps.setString(1, title);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Spectrum(rs);
        }
        rs.close();
        ps.close();
        if (counter > 1) {
            throw new SQLException("Duplicate spectrum found in the database.");
        }
        return temp;
    }
    
    public static long getSpectrumIdFromTitle(String title) {
    	return getSpectrumIdFromTitle(title, false);
    }
    
    /**
     * Finds the Spectrum from a given spectrum id.
     * @param spectrumid The spectrum id given from the actual spectrum.
     * @param conn The database connection.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Spectrum findFromSpectrumID(long spectrumid, Connection conn) throws SQLException{
    	Spectrum temp = null;
         PreparedStatement ps = conn.prepareStatement(Spectrum.getBasicSelect() +
         		" WHERE spectrumid = ? ORDER BY creationdate");
         ps.setLong(1, spectrumid);
         ResultSet rs = ps.executeQuery();
         int counter = 0;
         while (rs.next()) {
             counter++;
             temp = new Spectrum(rs);
         }
         rs.close();
         ps.close();
         return temp;
    }
    
    public static long getSpectrumIdFromTitle(String title, boolean omssa) {
    	String formatted = "";
    	if(omssa){
    		formatted = title.replace("\\\\", "/");
    	} else {
    		formatted = title.replace('\\', '/');
    	}
        return MapContainer.SpectrumTitle2IdMap.get(formatted);
    }

}
