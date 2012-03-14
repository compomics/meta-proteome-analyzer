package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpProperty extends ExppropertyTableAccessor {
	public ExpProperty() {
		super();
	}

	public ExpProperty(ResultSet aRS) throws SQLException {
		super(aRS);
	}
	
	/**
	 * Calls the super class.
	 * @param params
	 */
	public ExpProperty(HashMap params){
		super(params);
	}

	public static ExpProperty findExpPropertyFromTitle(String title, Connection aConn) throws SQLException {
		ExpProperty temp = null;
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where title = ?");
		ps.setString(1, title);
		ResultSet rs = ps.executeQuery();
		int counter = 0;
		while (rs.next()) {
			counter++;
			temp = new ExpProperty(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}
	// find ExpProperty
	public static ExpProperty findExpPropertyFromID(Long exppropertyid, Connection aConn) throws SQLException {
		ExpProperty temp = null;
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where exppropertyid = ?");
		ps.setLong(1, exppropertyid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new ExpProperty(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}

	public static List<ExpProperty> findAllPropertiesOfExperiment(long experimentid, Connection aConn) throws SQLException {
		ArrayList<ExpProperty> temp = new ArrayList<ExpProperty>();
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect()+ " where fk_experimentid = ?");
		ps.setLong(1, experimentid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp.add(new ExpProperty(rs));
		}
		rs.close();
		ps.close();
		return temp;
	}
}
