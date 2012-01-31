package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Property extends PropertyTableAccessor {

	public Property() {
		super();
	}

	public Property(ResultSet aRS) throws SQLException {
		super(aRS);
	}

	public static Property findPropertyFromTitle(String title, Connection aConn) throws SQLException {
		Property temp = null;
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where title = ?");
		ps.setString(1, title);
		ResultSet rs = ps.executeQuery();
		int counter = 0;
		while (rs.next()) {
			counter++;
			temp = new Property(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}
	
	public static Property findPropertyFromPropertyID(long propertyid, Connection aConn) throws SQLException {
		Property temp = null;
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect() +
													" where propertyid = ? ");
		ps.setLong(1, propertyid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new Property(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}
	


	public static List<Property> findAllPropertiesOfProject(long fk_projectid, Connection aConn) throws SQLException {
		ArrayList<Property> temp = new ArrayList<Property>();
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect()+ " where fk_projectid = ?");
		ps.setLong(1, fk_projectid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp.add(new Property(rs));
		}
		rs.close();
		ps.close();
		return temp;
	}
}

