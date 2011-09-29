package de.mpa.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class represents the server class.
 * @author Thilo Muth
 *
 */
public class Server {
	
	private final static int port = 8080;
	
	// The ServerSocket instance
	private ServerSocket server;
	
	// The data input (from the client)
	BufferedReader input;
	
	// The data output (to the client)
	PrintStream output;
	
	// The client socket
	Socket clientSocket;
	
	/**
	 * The constructor for the server.
	 */
	public Server() {
	}
	
	/**
	 * Accepting the connection.
	 * @throws IOException
	 */
	public void accept() throws IOException{
		clientSocket = server.accept();
		output = new PrintStream(clientSocket.getOutputStream());		
		input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	/**
	 * Starts the server.
	 * @param port
	 * @throws IOException
	 */
	public void start(int port) throws IOException{
		server = new ServerSocket(port);
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
	public void listen() throws IOException, ClassNotFoundException{			
			String inputLine;
			while(true){
				if ((inputLine = input.readLine()) != null) {   
					System.out.println("Received: " + inputLine);
				}			
			}
	}
	
	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.start(port);
			server.accept();
			server.listen();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
