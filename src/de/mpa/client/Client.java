package de.mpa.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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
			server.uploadFile(files[i].getName(), getBytesFromFile(files[i]));
		}
	}
	
	// Returns the contents of the file in a byte array.
	public static byte[] getBytesFromFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);

	    // Get the size of the file
	    long length = file.length();

	    // You cannot create an array using a long type.
	    // It needs to be an int type.
	    // Before converting to an int type, check
	    // to ensure that file is not larger than Integer.MAX_VALUE.
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }

	    // Create the byte array to hold the data
	    byte[] bytes = new byte[(int)length];

	    // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    // Ensure all the bytes have been read in
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }

	    // Close the input stream and return bytes
	    is.close();
	    return bytes;
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
		client.connect();
		client.sendMessage("SEND MESSAGE!");
	}
}
