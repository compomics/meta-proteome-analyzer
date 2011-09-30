package de.mpa.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import de.mpa.interfaces.Connectable;

/**
 * This class represents the client implementation.
 *  
 * @author Thilo Muth
 *
 */
public class Client implements Connectable {
	

	
	// Client instance
	private static Client client = null;
	
	// The name of the client 
	private String name;
	
	// The server instance
	private Socket server;
	
	private PrintWriter out;
	private BufferedReader in;
 	
	/**
	 * The constructor for the client.
	 * @param name
	 */
	private Client() {
		
	}
	
	/**
	 * Returns a client singleton object.
	 * @return client Client singleton object
	 */
	public static Client getInstance() {
		if (client == null) {
			client = new Client();
		}
		return client;
	}
	
	/**
	 * Connects the client to a given host.
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect(String host, int port) throws UnknownHostException, IOException {
		server = new Socket(host, port);
		out = new PrintWriter(server.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(server.getInputStream()));
	}	
	
	/**
	 * Disconnects the client.
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		out.close();		
		in.close();
		server.close();
	}
	
	/**
	 * Returns the name of the client
	 * @return String The name of the client.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Start permanent listening.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void receiveMsg() throws IOException, ClassNotFoundException {
		String inputLine = null;
		while(true){
			if ((inputLine = in.readLine()) != null) {
				System.out.println("Client received: " + inputLine);
			}			
		}
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

		// Get the output stream.
		OutputStream os = server.getOutputStream();
		ByteStream.toStream(os, nFiles);
		
		// Iterate the files.
		for (int i = 0; i < nFiles; i++) {
			ByteStream.toStream(os, files[i].getName());
			ByteStream.toStream(os, files[i]);
		}
	}
	
	/**
	 * Send a message to the server.
	 * @param msg
	 */
	public void sendMessage(String msg) {
		out.println(msg);
	}

	public static void main(String[] args) {		
		try {
			Client client = Client.getInstance();
			client.connect("localhost", 8080);			
			client.sendMessage("Testing2...");
			client.disconnect();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}
