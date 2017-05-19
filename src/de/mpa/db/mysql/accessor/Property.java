package de.mpa.db.mysql.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Property extends PropertyTableAccessor {

	public Property() {
    }
	
	/**
	 * Calls the super class.
	 * @param params
	 */
	public Property(HashMap params){
		super(params);
	}
	/**
	 * Calls the super class
	 * @param aRS 
	 */
	public Property(ResultSet aRS) throws SQLException {
		super(aRS);
	}

	/**
	 * Calls the super class and set the projectID, project property name and project property value .
	 * @param projectID
	 * @param name
	 * @param value
	 */
	public Property(long projectID, String name, String value) {
        setFk_projectid(projectID);
        setName(name);
        setValue(value);
	}

	/**
	 *  This method returns a project property specified by the property title
	 * @param title
	 * @param aConn
	 * @return projectProperty The project property.
	 * @throws SQLException
	 */
	public static Property findPropertyFromTitle(String title, Connection aConn) throws SQLException {
		Property temp = null;
		PreparedStatement ps = aConn.prepareStatement(PropertyTableAccessor.getBasicSelect() + " where title = ?");
		ps.setString(1, title);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new Property(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}
	/**
	 * This method returns a project property specified by the property ID
	 * @param propertyid
	 * @param aConn
	 * @return projectProperty. The project Property 
	 * @throws SQLException
	 */
	public static Property findPropertyFromPropertyID(long propertyid, Connection aConn) throws SQLException {
		Property temp = null;
		PreparedStatement ps = aConn.prepareStatement(PropertyTableAccessor.getBasicSelect() +
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
	

	/**
	 *  This method returns the list of project properties specified by the project ID
	 * @param fk_projectid
	 * @param aConn
	 * @return ArrayList<Property> The list of all properties
	 * @throws SQLException
	 */
	public static List<Property> findAllPropertiesOfProject(long fk_projectid, Connection aConn) throws SQLException {
		ArrayList<Property> temp = new ArrayList<Property>();
		PreparedStatement ps = aConn.prepareStatement(PropertyTableAccessor.getBasicSelect()+ " where fk_projectid = ?");
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

