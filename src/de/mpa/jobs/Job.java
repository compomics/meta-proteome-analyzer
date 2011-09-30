package de.mpa.jobs;


/**
 * Common task interface used to run the various processes. 
 * @author Thilo Muth
 *
 */
public interface Job extends Runnable {
	/**
	 * Return a description for the task.
	 * @return description
	 */
	public String getDescription();
	
	/**
	 * Returns an error for the task. 
	 * @return error
	 */
	public String getError();
	
	/**
	 * Returns the status. 
	 * @return status
	 */
	public JobStatus getStatus();
	
	/**
	 * After finishing the task, an array of created objects is returned. 
	 * This is used for the batch processing... 
	 * @return
	 */
	public Object[] getObjects();
	
	/**
	 * The task is being cancelled. 
	 */
	public void cancel();
	
}
