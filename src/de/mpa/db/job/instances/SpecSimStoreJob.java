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
	private final SpecSimJob specSimJob;

	/**
	 * Constructs a Spectral Similarity results storing job.
	 * @param specSimJob The Spectral Similarity Search job reference.
	 */
	public SpecSimStoreJob(SpecSimJob specSimJob) {
		try {
            dbManager = DBManager.getInstance();
		} catch (SQLException e) {
            this.setError(e);
		}
		this.specSimJob = specSimJob;
		// Set the description
        this.setDescription("SPECTRAL SIMILARITY SEARCH RESULTS STORING");
	}
	
	@Override
	public void run() {
		try {
            this.setStatus(JobStatus.RUNNING);
            this.dbManager.storeSpecSimResults(this.specSimJob.getResults());
            this.done();
		} catch (Exception e) {
            this.setError(e);
		}
	}
}
