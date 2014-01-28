package de.mpa.db.job.instances;

import java.sql.SQLException;

import de.mpa.db.DBManager;
import de.mpa.db.job.Job;
import de.mpa.db.job.JobStatus;

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
			setStatus(JobStatus.RUNNING);
			dbManager.storeSpecSimResults(specSimJob.getResults());
			done();
		} catch (Exception e) {
			setError(e);
		}
	}
}
