package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Experiment extends ExperimentTableAccessor {

	public Experiment() {
		super();
	}

	public Experiment(ResultSet aRS) throws SQLException {
		super(aRS);
	}


	public static List<Experiment> findAllExperimentsOfProject(long fk_projectid, Connection aConn) throws SQLException {
		ArrayList<Experiment> temp = new ArrayList<Experiment>();
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect()+ " where fk_projectid = ?");
		ps.setLong(1, fk_projectid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp.add(new Experiment(rs));
		}
		rs.close();
		ps.close();
		return temp;
	}

	
	public static Experiment findExperimentByID(long experimentid, Connection aConn) throws SQLException {
		Experiment temp = new Experiment();
		PreparedStatement ps = aConn.prepareStatement(getBasicSelect()+ " where experimentid = ?");
		ps.setLong(1, experimentid);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp= new Experiment(rs);
		}
		rs.close();
		ps.close();
		return temp;
	}
}
