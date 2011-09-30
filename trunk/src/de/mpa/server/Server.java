package de.mpa.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.mpa.client.ByteStream;

/**
 * This class represents the server class.
 * @author Thilo Muth
 *
 */
public class Server implements Runnable {	
	// Server port
	private final static int port = 8080;
	
	// Logger
	public final static Logger LOG = Logger.getLogger(Server.class);
	
	// The ServerSocket instance
	private static ServerSocket server;
	
	// The data input (from the client)
	BufferedReader input;
	
	// The data output (to the client)
	PrintWriter output;
	
	// The client socket
	Socket clientSocket;
	
	/**
	 * The constructor for the server.
	 */
	public Server() {
		BasicConfigurator.configure();
		Logger.getLogger("org.apache").setLevel(Level.INFO);
	}
	
	/**
	 * Accepting the connection.
	 * @throws IOException
	 */
	public void accept() throws IOException{
		clientSocket = server.accept();
		output = new PrintWriter(clientSocket.getOutputStream(), true);	
		input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	/**
	 * Stops the server.
	 * @throws IOException
	 */
	public void stop() throws IOException{
		output.close();
		input.close();
		server.close();
	}
	
	/**
	 * Start permanent listening.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void receiveMsg() throws IOException, ClassNotFoundException{			
			String inputLine;
			if ((inputLine = input.readLine()) != null) {   
				LOG.info("Received: " + inputLine);
			}			
	}	
	
	/**
	 * Send a message to the server.
	 * @param msg
	 */
	public void sendMessage(String msg) {
		output.println(msg);		
	}
	
	/**
	 * Receive the files at the server side.
	 */
	public void receiveFiles() {
		try {
			InputStream in = clientSocket.getInputStream();
			int nFiles = ByteStream.toInt(in);

			for (int i = 0; i < nFiles; i++) {
				String filename = ByteStream.toString(in);
				File file = new File(filename);
				ByteStream.toFile(in, file);
				LOG.info("Received file: " + filename);
				sendMessage("File ist da: " + filename);
			}
		} catch (java.lang.Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
	
	public static void main(String[] args) {
		try {
			server = new ServerSocket(port);
			while (true){
				Server server = new Server();
				server.accept();
				new Thread(server).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void run() {
			//receiveMsg();
			receiveFiles();
		
	}

}
