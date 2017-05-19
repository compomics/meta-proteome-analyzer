package de.mpa.db.mysql.job;


/**
 * Executable-Interface implemented by the Job class.
 * @author Thilo Muth
 *
 */
public interface Executable extends Runnable {
	/**
	 * Return a description for the job.
	 * @return description The description represented as String
	 */
    String getDescription();
	
	/**
	 * Returns the job status. 
	 * @return status The JobStatus
	 */
    JobStatus getStatus();
	
	/**
	 * Returns the error (if any error has occurred)
	 * @return error The error represented as String.
	 */
    String getError();
	
	/**
	 * Run-method.
	 */
    void run();

}
