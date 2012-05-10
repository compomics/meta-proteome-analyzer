package de.mpa.job.instances;

import de.mpa.job.Job;
import de.mpa.job.JobStatus;

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
}
