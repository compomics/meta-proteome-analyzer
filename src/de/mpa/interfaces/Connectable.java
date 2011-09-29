package de.mpa.interfaces;

import java.io.IOException;
import java.net.UnknownHostException;

public interface Connectable {
	
	// Connecting a host
	public void connect(String host, int port) throws UnknownHostException, IOException ;
	
	// Disconneting from the host
	public void disconnect() throws IOException ;
	
}
