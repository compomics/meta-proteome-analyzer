package de.mpa.job.storing;

import java.sql.SQLException;

import de.mpa.db.DBManager;
import de.mpa.job.Job;

/**
 * Job class for storing OMSSA search engine results to the database.
 * 
 * @author A. Behne
 */
public class OmssaStoreJob extends Job {

	/**
	 * The database manager object.
	 */
	private DBManager dbManager;
	
	/**
	 * The QVality results filename.
	 */
	private String omssaQValued;

	/**
	 * Constructs an OMSSA results storing job.
	 * @param omssaFilename The OMSSA results filename.
	 * @param omssaQValued The QVality results filename.
	 */
	public OmssaStoreJob(String omssaFilename, String omssaQValued) {
		try {
			this.dbManager = DBManager.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.omssaQValued = omssaQValued;
		// Set the description
		setFilename(omssaFilename);
		setDescription("OMSSA RESULTS STORING");
	}
	
	@Override
	public void execute() {
		try {
			dbManager.storeOmssaResults(getFilename(), omssaQValued);
		} catch (Exception e) {
			setError(e);
		}
	}
}
