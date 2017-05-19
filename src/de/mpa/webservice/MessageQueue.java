package de.mpa.webservice;

import java.util.ArrayDeque;

import org.apache.log4j.Logger;

import de.mpa.db.mysql.job.JobStatus;

/**
 * Custom collection to hold message strings to be transferred between client and server.
 * 
 * @author T. Muth, A.Behne
 */
@SuppressWarnings("serial")
public class MessageQueue extends ArrayDeque<String> {

	/**
	 * The message queue singleton instance.
	 */
	private static MessageQueue instance;

	/**
	 * Constructs a message queue.
	 */
	private MessageQueue() {
    }
	
	/**
	 * Returns the message queue singleton instance.
	 * @return The message queue singleton instance.
	 */
	public static MessageQueue getInstance() {
		if (MessageQueue.instance == null) {
            MessageQueue.instance = new MessageQueue();
		}
		return MessageQueue.instance;
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
			composedMessage = JobStatus.ERROR + ": " + msg.getDescription() + " " + msg.getError();
		} else {
			composedMessage = msg.getDescription() + " " + msg.getStatus();
		}
		log.info(composedMessage);
		return add(composedMessage);
	}
	
}
