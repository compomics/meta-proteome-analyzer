package de.mpa.webservice;

import java.util.Date;

import de.mpa.job.Job;

/**
 * <b> Message.java</b>
 * <p> 
 * This class represents a message created by the server and received by the client.
 * </p>
 * @author Thilo Muth
 *
 */
public class Message {
	
	// From which job comes the message
	private Job job;
	
	// The data contained in the message
	private String data;
	
	// When was the message sent 
	private Date dateSent;

	
	/**
	 * The message object.
	 * @param job
	 * @param data
	 * @param dateSent
	 */
	public Message(Job job, String data, Date dateSent) {
		this.job = job;
		this.data = data;
		this.dateSent = dateSent;
	}
	
	/**
	 * Returns the job which created the message.
	 * @return
	 */
	public Job getJob() {
		return job;
	}	
	
	/**
	 * Returns the message data content.
	 * @return
	 */
	public String getData() {
		return data;
	}	
	
	/**
	 * Returns the date when the message was sent.
	 * @return
	 */
	public Date getDateSent() {
		return dateSent;
	}	
}
