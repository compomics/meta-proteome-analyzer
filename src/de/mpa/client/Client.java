package de.mpa.client;

import java.io.File;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import de.mpa.client.Server;
import de.mpa.client.ServerImplService;

public class Client {

	// Client instance
	private static Client client = null;
	
	// Server service
	private ServerImplService service;

	// Server instance
	private Server server;

	/**
	 * The constructor for the client.
	 * 
	 * @param name
	 */
	private Client() {
		service = new ServerImplService();
		server = service.getServerImplPort();
		
		// enable MTOM in client
		BindingProvider bp = (BindingProvider) server;
		SOAPBinding binding = (SOAPBinding) bp.getBinding();
		binding.setMTOMEnabled(true);
	}

	/**
	 * Returns a client singleton object.
	 * 
	 * @return client Client singleton object
	 */
	public static Client getInstance() {
		if (client == null) {
			client = new Client();
		}
		return client;
	}
	
	/**
	 * Send the message. 
	 * @param msg
	 */
	public void sendMessage(String msg){
		server.sendMessage(msg);
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		/************ test upload ****************/
		File file = new File("c:\\metaproteomics\\output\\1A1.mgf.out");

		Client client = Client.getInstance();
		
		client.sendMessage("SEND MESSAGE!");
		
		//System.out.println(server.uploadFile(file.getAbsolutePath()));

		// String status = server.
		// System.out.println("imageServer.uploadImage() : " + status);

	}
}
