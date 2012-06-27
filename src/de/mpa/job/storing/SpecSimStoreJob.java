package de.mpa.job.storing;

import java.sql.SQLException;

import de.mpa.db.DBManager;
import de.mpa.job.Job;
import de.mpa.job.instances.SpecSimJob;

/**
 * Job class for storing Spectral Similarity search results to the database.
 * 
 * @author A. Behne
 */
public class SpecSimStoreJob extends Job {

	/**
	 * The database manager object.
	 */
	private DBManager dbManager;
	
	/**
	 * The Spectral Similarity Search job reference.
	 */
	private SpecSimJob specSimJob;

	/**
	 * Constructs a Spectral Similarity results storing job.
	 * @param specSimJob The Spectral Similarity Search job reference.
	 */
	public SpecSimStoreJob(SpecSimJob specSimJob) {
		try {
			this.dbManager = DBManager.getInstance();
		} catch (SQLException e) {
			setError(e);
		}
		this.specSimJob = specSimJob;
		// Set the description
		setDescription("SPECTRAL SIMILARITY SEARCH RESULTS STORING");
	}
	
	@Override
	public void run() {
		try {
			dbManager.storeSpecSimResults(specSimJob.getResults());
		} catch (Exception e) {
			setError(e);
		}
	}
}
