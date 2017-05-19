package de.mpa.db.mysql.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpProperty extends ExppropertyTableAccessor {
	public ExpProperty() {
    }

	/**
	 * Calls the super class
	 * @param aRS
	 * @throws SQLException
	 */
	public ExpProperty(ResultSet aRS) throws SQLException {
		super(aRS);
	}
	
	/**
	 * Calls the super class and sets the experiment ID, experiment property name and value.
	 * @param experimentID
	 * @param name
	 * @param value
	 */
	public ExpProperty(long experimentID, String name, String value){
        setFk_experimentid(experimentID);
        setName(name);
        setValue(value);
	}
	
	/**
	 * Calls the super class.
	 * @param params
	 */
	public ExpProperty(HashMap params){
		super(params);
	}

	/**
	 * This method returns the experiment property specified by a property title
	 * @param title
	 * @param aConn
	 * @return ExpProperty The experiment property.
	 * @throws SQLException
	 */
	public static ExpProperty findExpPropertyFromTitle(String title, Connection aConn) throws SQLException {
		ExpProperty temp = null;
		PreparedStatement ps = aConn.prepareStatement(ExppropertyTableAccessor.getBasicSelect() + " where title = ?");
		ps.setString(1, title);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new ExpProperty(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}

	/**
	 * This method returns the experiment property specified by the experiment property ID
	 * @param exppropertyid
	 * @param aConn
	 * @return expProp The experiment property
	 * @throws SQLException
	 */
	public static ExpProperty findExpPropertyFromID(Long exppropertyid, Connection aConn) throws SQLException {
		ExpProperty temp = null;
		PreparedStatement ps = aConn.prepareStatement(ExppropertyTableAccessor.getBasicSelect() + " where exppropertyid = ?");
		ps.setLong(1, exppropertyid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new ExpProperty(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}

	/**
	 * This method returns the experiment properties specified by the experiment ID
	 * @param experimentid
	 * @param aConn
	 * @return ArrayList<ExpProperty> The List of experiment properties
	 * @throws SQLException
	 */
	public static List<ExpProperty> findAllPropertiesOfExperiment(long experimentid, Connection aConn) throws SQLException {
		ArrayList<ExpProperty> temp = new ArrayList<ExpProperty>();
		PreparedStatement ps = aConn.prepareStatement(ExppropertyTableAccessor.getBasicSelect()+ " where fk_experimentid = ?");
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
