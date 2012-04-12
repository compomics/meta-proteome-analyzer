package de.mpa.webservice;

import java.util.ArrayDeque;

public class MessageQueue extends ArrayDeque<Message> {

	private static MessageQueue instance;

	private MessageQueue() {
		super();
	}
	
	public static MessageQueue getInstance() {
		if (instance == null) {
			instance = new MessageQueue();
		}
		return instance; 
	}
	
}
