package de.mpa.task;

/**
 * Possible values for the job status are:
 * 
 * 	WAITING - job is waiting for processing thread.
 * 
 * 	RUNNING - job is running.
 * 
 * 	FINISHED - job is finished.
 * 
 *  ERROR - job gave an error.
 * 
 * 	CANCELED - job was canceled.
 * 
 * @author Thilo Muth
 *
 */
public enum TaskStatus {
	
	WAITING, RUNNING, FINISHED, ERROR, CANCELED

}
