package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Searchspectrum extends SearchspectrumTableAccessor{
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
     * Finds the Searchspectrum from a given spectrum id.
     * @param spectrumid The spectrum id given from the actual spectrum.
     * @param conn The database connection.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Searchspectrum findFromSpectrumID(long spectrumid, Connection conn) throws SQLException{
    	 Searchspectrum temp = null;
         PreparedStatement ps = conn.prepareStatement(Searchspectrum.getBasicSelect() +
         		" WHERE fk_spectrumid = ? ORDER BY creationdate");
         ps.setLong(1, spectrumid);
         ResultSet rs = ps.executeQuery();
         int counter = 0;
         while (rs.next()) {
             counter++;
             temp = new Searchspectrum(rs);
         }
         rs.close();
         ps.close();
         return temp;
    }
    
}
