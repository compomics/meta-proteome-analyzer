package de.mpa.webservice;

import javax.xml.ws.Endpoint;

import de.mpa.client.settings.ConnectionParameters;

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
		ConnectionParameters connectionParams = new ConnectionParameters();
		Endpoint.publish("http://" + "0.0.0.0" + ":" + connectionParams.get("srvPort").toString() + "/WS/Server", new ServerImpl());
	}
	
	
}
