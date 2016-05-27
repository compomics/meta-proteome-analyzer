package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExperimentAccessor extends ExperimentTableAccessor {
	
	/**
	 * Default constructor.
	 */
	public ExperimentAccessor() {
		super();
	}
	
	/**
	 * Calls the super class.
	 * @param params
	 */
	public ExperimentAccessor(@SuppressWarnings("rawtypes") HashMap params){
		super(params);
	}
	
	public ExperimentAccessor(ResultSet aRS) throws SQLException {
		super(aRS);
	}

	/**
	 * Find all experiments of the project.
	 * @param fk_projectid
	 * @param aConn
	 * @return
	 * @throws SQLException
	 */
	public static List<ExperimentAccessor> findAllExperimentsOfProject(long fk_projectid, Connection aConn) throws SQLException {
		List<ExperimentAccessor> temp = new ArrayList<ExperimentAccessor>();
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect()+ " where fk_projectid = ?");
		ps.setLong(1, fk_projectid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp.add(new ExperimentAccessor(rs));
		}
		rs.close();
		ps.close();
		return temp;
	}

	
	public static ExperimentAccessor findExperimentByID(long experimentid, Connection aConn) throws SQLException {
		ExperimentAccessor temp = new ExperimentAccessor();
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect()+ " where experimentid = ?");
		ps.setLong(1, experimentid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp= new ExperimentAccessor(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}
	
	/**
	 * Returns the experiment for a selected experiment id and project id.
	 * @param experimentid The experiment id.
	 * @param projectid The project id.
	 * @param aConn The database connection.
	 * @return The selected experiment.
	 * @throws SQLException
	 */
	public static ExperimentAccessor findExperimentByIDandProjectID(long experimentid, long projectid, Connection aConn) throws SQLException {
		ExperimentAccessor temp = new ExperimentAccessor();
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect()+ " where experimentid = ? and fk_projectid = ?");
		ps.setLong(1, experimentid);
		ps.setLong(2, projectid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp= new ExperimentAccessor(rs);
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
	
	@Override
	public String toString() {
		return this.iTitle;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExperimentAccessor) {
			ExperimentAccessor that = (ExperimentAccessor) obj;
			return (that.iExperimentid == this.iExperimentid);
		}
		return false;
	}
	
}
