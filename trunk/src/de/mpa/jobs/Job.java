package de.mpa.jobs;


/**
 * Common job interface used to run the various processes. 
 * @author Thilo Muth
 *
 */
public interface Job extends Runnable {
	/**
	 * Return a description for the job.
	 * @return description
	 */
	public String getDescription();
	
	/**
	 * Returns an error for the job. 
	 * @return error
	 */
	public String getError();
	
	/**
	 * Returns the status. 
	 * @return status
	 */
	public JobStatus getStatus();
	
	/**
	 * After finishing the job, an array of created objects is returned. 
	 * This is used for the batch processing... 
	 * @return
	 */
	public Object[] getObjects();
	
	/**
	 * The task is being cancelled. 
	 */
	public void cancel();
	
}
