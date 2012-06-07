package de.mpa.job.storing;

import java.sql.SQLException;

import de.mpa.db.DBManager;
import de.mpa.job.Job;

/**
 * Job class for storing X!Tandem search engine results to the database.
 * 
 * @author A. Behne
 */
public class XTandemStoreJob extends Job {

	/**
	 * The database manager object.
	 */
	private DBManager dbManager;
	
	/**
	 * The QVality results filename.
	 */
	private String xtandemQValued;
	
	/**
	 * Constructs an X!Tandem results storing job.
	 * @param xtandemFilename The X!Tandem results filename.
	 * @param xtandemQValued The QVality results filename.
	 */
	public XTandemStoreJob(String xtandemFilename, String xtandemQValued) {
		try {
			this.dbManager = DBManager.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.xtandemQValued = xtandemQValued;
		// Set the description
		setFilename(xtandemFilename);
		setDescription("X!TANDEM RESULTS STORING");
	}
	
	@Override
	public void execute() {
		try {
			dbManager.storeXTandemResults(getFilename(), xtandemQValued);
		} catch (Exception e) {
			setError(e);
		}
	}

}
