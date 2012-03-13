package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Spectrum extends SpectrumTableAccessor {

	public Spectrum(ResultSet rs) throws SQLException {
		super(rs);
	}

	public Spectrum(HashMap<Object, Object> data) {
		super(data);
	}

	public static Spectrum findFromTitle(String title, Connection conn) throws SQLException{
		Spectrum res = null;
		
		PreparedStatement ps = conn.prepareStatement(getBasicSelect() +
        		" WHERE title = " + title);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            res = new Spectrum(rs);
        }
        rs.close();
        ps.close();
        if (counter != 1) {
            SQLException sqe = new SQLException("Select based on spectrum title '" + title + "' resulted in " + counter + " results instead of 1!");
            sqe.printStackTrace();
            throw sqe;
        }
        return res;
	}

}
