package de.mpa.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

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
	 * Connects the client to the web service.
	 */
	public void connect(){
		service = new ServerImplService();
		server = service.getServerImplPort();
		
		// enable MTOM in client
		BindingProvider bp = (BindingProvider) server;
		SOAPBinding binding = (SOAPBinding) bp.getBinding();
		binding.setMTOMEnabled(true);
	}
	
	/**
	 * Send the message. 
	 * @param msg
	 */
	public void sendMessage(String msg){
		server.sendMessage(msg);
	}
	
	/**
	 * Send multiple files to the server.
	 * @param files
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void sendFiles(File[] files) throws FileNotFoundException, IOException {
		
		// Number of files to send.
		int nFiles = files.length;
		
		for (int i = 0; i < nFiles; i++){
			server.uploadFile(files[i].getAbsolutePath());
		}
	}
	
	public void process(String filename){
		server.process(filename);
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		// Get instance of the client.
		Client client = Client.getInstance();		
		client.sendMessage("SEND MESSAGE!");	
	}
}
