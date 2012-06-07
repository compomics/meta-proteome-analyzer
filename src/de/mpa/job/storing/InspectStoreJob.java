package de.mpa.job.storing;

import java.sql.SQLException;

import de.mpa.db.DBManager;
import de.mpa.job.Job;

/**
 * Job class for storing InsPecT search engine results to the database.
 * 
 * @author A. Behne
 */
public class InspectStoreJob extends Job {
	
	/**
	 * The database manager object.
	 */
	private DBManager dbManager;

	/**
	 * Constructs a InsPecT results storing job.
	 * @param inspectFilename The InsPecT results filename.
	 */
	public InspectStoreJob(String inspectFilename) {
		try {
			this.dbManager = DBManager.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Set the description
		setFilename(inspectFilename);
		setDescription("INSPECT RESULTS STORING");
	}
	
	@Override
	public void execute() {
		try {
			dbManager.storeInspectResults(getFilename());
		} catch (Exception e) {
			setError(e);
		}
	}
}
