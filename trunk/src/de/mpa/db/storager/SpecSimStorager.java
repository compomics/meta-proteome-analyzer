package de.mpa.db.storager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.db.accessor.SpecSearchHit;

/**
 * Storager class for persistent storage of spectral similarity search hits.
 * 
 * @author A. Behne
 */
public class SpecSimStorager extends BasicStorager {

	/**
	 * The database connection instance.
	 */
	private Connection conn;
	
	/**
	 * The list of SSMs to store.
	 */
	private List<SpectrumSpectrumMatch> results;

	/**
	 * Constructs a storager instance with the specified database connection and
	 * spectral similarity search result list.
	 * @param conn the database connection instance.
	 * @param results the spectral similarity search result list
	 */
	public SpecSimStorager(Connection conn, List<SpectrumSpectrumMatch> results) {
		this.conn = conn;
		this.results = results;
	}
	
	@Override
	public void store() throws IOException, SQLException {
		int i = 0;
		int packageSize = 10000;
		for (SpectrumSpectrumMatch ssm : results) {
			SpecSearchHit ssh = new SpecSearchHit(ssm);
			ssh.persist(conn);
			// commit in batches of defined size
			if ((++i % packageSize) == 0) {
				conn.commit();
			}
		}
		conn.commit();
	}

}
