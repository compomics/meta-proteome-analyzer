package de.mpa.webservice;

import javax.xml.ws.Endpoint;

public class WSPublisher {
	public static void start(String host, String port) {
		Endpoint.publish("http://" + host + ":" + port + "/WS/Server",new ServerImpl());
	}
	
	public static void main(String[] args) {
		try {
			Endpoint.publish("http://0.0.0.0:8080/WS/Server",new ServerImpl());
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
	}
}
