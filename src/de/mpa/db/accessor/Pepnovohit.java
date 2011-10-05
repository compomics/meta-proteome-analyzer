package de.mpa.db.accessor;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pepnovohit extends PepnovohitTableAccessor {

    /**
     * Calls the super class.
     * @param params
     */
    public Pepnovohit(HashMap params){
        super(params);
    }
    
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Pepnovohit(ResultSet aRS) throws SQLException {
        super(aRS);
    }
    
    public static List<Pepnovohit> getHitsFromSpectrumID(long aSpectrumID, Connection aConn) throws SQLException {
    	List<Pepnovohit> temp = new ArrayList<Pepnovohit>();
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where l_spectrumid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp.add(new Pepnovohit(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
}

