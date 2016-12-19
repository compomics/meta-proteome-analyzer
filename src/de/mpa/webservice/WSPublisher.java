package de.mpa.webservice;

import javax.xml.ws.Endpoint;

import de.mpa.util.PropertyLoader;


public class WSPublisher {
	
	
	/**
	 * Start the WSPublisher. 
	 * @param host The host IP.
	 * @param port The local port.
	 */
	public static void start(String host, String port) {
		Endpoint.publish("http://" + host + ":" + port + "/WS/Server", new ServerImpl());
	}
	
	/**
	 * Main method for publishing the web service on the server.
	 * DO NOT DELETE THIS METHOD!
	 * @param args
	 */
	public static void main(String[] args) {
		// Default hard-coded endpoint - do not change!
		Endpoint.publish("http://0.0.0.0:" + PropertyLoader.getProperty(PropertyLoader.APP_PORT) + "/WS/Server", new ServerImpl());
		
	}
	
	
}
