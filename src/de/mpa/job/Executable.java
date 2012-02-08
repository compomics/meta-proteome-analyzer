package de.mpa.job;


/**
 * Executable-Interface implemented by the Job class.
 * @author Thilo Muth
 *
 */
public interface Executable {
	/**
	 * Return a description for the job.
	 * @return description The description represented as String
	 */
	public String getDescription();
	
	/**
	 * Returns the job status. 
	 * @return status The JobStatus
	 */
	public JobStatus getStatus();
	
	/**
	 * Returns the error (if any error has occurred)
	 * @return error The error represented as String.
	 */
	public String getError();
	
	/**
	 * Execute-method.
	 */
	public void execute();

}
