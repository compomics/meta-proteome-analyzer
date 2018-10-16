package de.mpa.main;

import de.mpa.webservice.WSPublisher;

public class ServerClientStart {

	/**
	 * Main method, starts the application
	 *
	 * 
	 * @author K.Schallert, rzoun
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
		// starts the MPA-Server
		WSPublisher.main(args);
		
		Thread.sleep(200);
		
		// starts the MPA-Client
		Starter.main(args);
		
	}
	
}
