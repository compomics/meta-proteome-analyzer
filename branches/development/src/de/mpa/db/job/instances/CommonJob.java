package de.mpa.db.job.instances;

import de.mpa.db.job.Job;
import de.mpa.db.job.JobStatus;

/**
 * Generic job implementation.
 * @author A. Behne, T. Muth
 *
 */
public class CommonJob extends Job {
	
	/**
	 * Constructs the generic job implementation.
	 * @param status The job status.
	 * @param description The job description.
	 */
	public CommonJob(JobStatus status, String description) {
		this.status = status;
		this.description = description;
	}
	
	/**
	 * Run method has no function.
	 */
	public void run() {}
	
}
