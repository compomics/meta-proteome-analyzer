package de.mpa.db.storager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.db.accessor.SpecSearchHit;

public class SpecSimStorager extends BasicStorager {

	private Connection conn;
	private List<SpectrumSpectrumMatch> results;

	public SpecSimStorager(Connection conn, List<SpectrumSpectrumMatch> results) {
		this.conn = conn;
		this.results = results;
	}
	
	@Override
	public void store() throws IOException, SQLException {
		for (SpectrumSpectrumMatch ssm : results) {
			SpecSearchHit ssh = new SpecSearchHit(ssm);
			ssh.persist(conn);
		}
		conn.commit();
	}

}
