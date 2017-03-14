package de.mpa.webservice;

import java.util.Date;

import de.mpa.db.job.Job;
import de.mpa.db.job.JobStatus;

/**
 * <b> Message.java</b>
 * <p> 
 * This class represents a message created by the server and received by the client.
 * </p>
 * @author Thilo Muth
 *
 */
public class Message {
	
	// Job error
	protected String error;
	
	// Job status
	protected JobStatus status;
	
	// Job description
	protected String description;
	
	// When was the message sent 
	private final Date dateSent;
	
	/**
	 * Convenience constructor. 
	 * @param job The executed job.
	 */
	public Message(Job job) {
		this(job, new Date());
	}
	
	/**
	 * The message object.
	 * @param job
	 * @param dateSent
	 */
	public Message(Job job, Date dateSent) {
        status = job.getStatus();
        description = job.getDescription();
        error = job.getError();
		this.dateSent = dateSent;
	}
		
	/**
	 * Returns the date when the message was sent.
	 * @return
	 */
	public Date getDateSent() {
		return this.dateSent;
	}
	
	/**
	 * Returns the job error message. 
	 * @return The error message.
	 */
	public String getError() {
		return this.error;
	}
	
	/**
	 * Returns the job status. 
	 * @return The job status.
	 */
	public JobStatus getStatus() {
		return this.status;
	}
	
	/**
	 * Returns the job description.
	 * @return The job description.
	 */
	public String getDescription() {
		return this.description;
	}	
}
