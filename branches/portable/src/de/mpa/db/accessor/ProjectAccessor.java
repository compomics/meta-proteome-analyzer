package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectAccessor extends ProjectTableAccessor {
	/**
	 * Calls the super class.
	 */
	public ProjectAccessor(){
		super();
	}

	/**
	 * Calls the super class.
	 * @param params
	 */
	public ProjectAccessor(HashMap params){
		super(params);
	}

	/**
	 * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
	 * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
	 *
	 * @param aRS ResultSet to read the data from.
	 * @throws SQLException when reading the ResultSet failed.
	 */
	public ProjectAccessor(ResultSet aRS) throws SQLException {
		super(aRS);
	}

	/**
	 * This method will find a spectrum file from the current connection, based on the filename.
	 *
	 * @param fileName String with the filename of the spectrum file to find.
	 * @param aConn Connection to read the spectrum File from.
	 * @return Spectrumfile with the data.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	public static ProjectAccessor findFromTitle(String title, Connection aConn) throws SQLException {
		ProjectAccessor temp = null;
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where title = ?");
		ps.setString(1, title);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new ProjectAccessor(rs);
		}
		rs.close();
		ps.close();

		return temp;
	}

	/**
	 * This method finds all projects from the database.
	 * @param conn The database connection
	 * @return List of retrieved projects.
	 * @throws SQLException
	 */
	public static List<ProjectAccessor> findAllProjects(Connection conn) throws SQLException {
		List<ProjectAccessor> projects = new ArrayList<ProjectAccessor>();
		PreparedStatement ps = conn.prepareStatement(getBasicSelect());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			projects.add(new ProjectAccessor(rs));
		}
		rs.close();
		ps.close();
		return projects;
	}

	public static ProjectAccessor findFromProjectID(long projectid, Connection aConn) throws SQLException {
		ProjectAccessor temp = null;
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where projectid = ?");
		ps.setLong(1, projectid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp = new ProjectAccessor(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}
	
	@Override
	public int persist(Connection aConn) throws SQLException {
		int persist = super.persist(aConn);
		aConn.commit();
		return persist;
	}
	
	@Override
	public int update(Connection aConn) throws SQLException {
		int update = super.update(aConn);
		aConn.commit();
		return update;
	}
}
