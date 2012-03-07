package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ArraySpectrum extends ArrayspectrumTableAccessor {

    /**
     * Calls the super class.
     * @param params
     */
    public ArraySpectrum(HashMap params){
        super(params);
    }
    
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public ArraySpectrum(ResultSet aRS) throws SQLException {
        super(aRS);
    }
    
    /**
     * Returns ArraySpectrum entries belonging to a specified libspectrum entry.
     * @param spectrumID
     * @param aConn
     * @return
     */
    public static ArraySpectrum findFromSpectrumID(long spectrumID, Connection aConn) throws SQLException {
    	ArraySpectrum temp = null;
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " WHERE " + ArraySpectrum.FK_LIBSPECTRUMID + " = ?");
        ps.setLong(1, spectrumID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	temp = new ArraySpectrum(rs);
        }
        rs.close();
        ps.close();
        return temp;
    }

}
