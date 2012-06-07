package de.mpa.job.storing;

import java.sql.SQLException;

import de.mpa.db.DBManager;
import de.mpa.job.Job;

/**
 * Job class for storing PepNovo+ search results to the database.
 * 
 * @author A. Behne
 */
public class PepnovoStoreJob extends Job {

	/**
	 * The database manager object.
	 */
	private DBManager dbManager;

	/**
	 * Constructs a Spectral Similarity results storing job.
	 * @param pepnovoFilename The PepNovo+ results filename.
	 */
	public PepnovoStoreJob(String pepnovoFilename) {
		try {
			this.dbManager = DBManager.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Set the description
		setFilename(pepnovoFilename);
		setDescription("PEPNOVO RESULTS STORING");
	}
	
	@Override
	public void execute() {
		try {
			dbManager.storePepnovoResults(getFilename());
		} catch (Exception e) {
			setError(e);
		}
	}
}
