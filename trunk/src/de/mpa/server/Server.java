package de.mpa.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.mpa.job.JobManager;
import de.mpa.job.instances.DeleteJob;
import de.mpa.job.instances.PepnovoJob;

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
	 * Process a derived file.
	 * @param filename
	 * @throws Exception
	 */
	private void process(String filename) throws Exception{		
		JobManager jobManager = new JobManager();	
		jobManager.addJob(new PepnovoJob(new File(filename), 0.5));
		jobManager.addJob(new DeleteJob(filename));		
		jobManager.execute();
	}
	
	
	/**
	 * Runs the thread.
	 */
	public void run() {
		
	}
	
	/**
	 * Main entry point for the server application.
	 * @param args
	 */
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

}
