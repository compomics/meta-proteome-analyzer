package de.mpa.webservice;

import java.util.ArrayDeque;

import org.apache.log4j.Logger;

import de.mpa.job.JobStatus;

/**
 * Custom collection to hold message strings to be transferred between client and server.
 * 
 * @author T. Muth, A.Behne
 */
public class MessageQueue extends ArrayDeque<String> {

	/**
	 * The message queue singleton instance.
	 */
	private static MessageQueue instance;

	/**
	 * Constructs a message queue.
	 */
	private MessageQueue() {
		super();
	}
	
	/**
	 * Returns the message queue singleton instance.
	 * @return The message queue singleton instance.
	 */
	public static MessageQueue getInstance() {
		if (instance == null) {
			instance = new MessageQueue();
		}
		return instance; 
	}
	
	/**
	 * Inserts a message at the end of this deque and simultaneously appends it to the specified logger.
	 * @param msg The message to be queued.
	 * @param log The logger.
	 * @return <code>true</code> if this collection changed as a result of the call.
	 */
	public boolean add(Message msg, Logger log) {
		String composedMessage;
		if (msg.getStatus() == JobStatus.ERROR) {
			composedMessage = JobStatus.ERROR.toString() + ": " + msg.getDescription() + " " + msg.getError();
		} else {
			composedMessage = msg.getDescription() + " " + msg.getStatus().toString();
		}
		log.info(composedMessage);
		return super.add(composedMessage);
	}
	
}
