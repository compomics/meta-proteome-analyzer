package de.mpa.job.storing;

import java.sql.SQLException;

import de.mpa.db.DBManager;
import de.mpa.job.Job;

/**
 * Job class for storing Crux search engine results to the database.
 * 
 * @author A. Behne
 */
public class CruxStoreJob extends Job {
	
	/**
	 * The database manager object.
	 */
	private DBManager dbManager;

	/**
	 * Constructs a Crux results storing job.
	 * @param cruxFilename The Crux results filename.
	 */
	public CruxStoreJob(String cruxFilename) {
		try {
			this.dbManager = DBManager.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Set the description
		setFilename(cruxFilename);
		setDescription("CRUX RESULTS STORING");
	}
	
	@Override
	public void execute() {
		try {
			dbManager.storeCruxResults(getFilename());
		} catch (Exception e) {
			setError(e);
		}
	}
}
