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
     * Returns the entries within a specified precursor range.
     * @param precursorMz
     * @param tolMz
     * @param aConn
     * @return
     * @throws SQLException
     */
    public static List<Speclibentry> getEntriesWithinPrecursorRange(double precursorMz, double tolMz, Connection aConn) throws SQLException {
    	List<Speclibentry> temp = new ArrayList<Speclibentry>();
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where precursor_mz >= ? and precursor_mz <= ?");
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
}
