package de.mpa.webservice;

import javax.xml.ws.Endpoint;

public class WSPublisher {
	public static void start(String host, String port) {
		Endpoint.publish("http://" + host + ":" + port + "/WS/Server", new ServerImpl());
	}
}
