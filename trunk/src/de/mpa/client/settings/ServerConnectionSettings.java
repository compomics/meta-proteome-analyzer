package de.mpa.client.settings;

/**
 * Class to hold server connection settings.
 * @author Alexander Behne, Fabian Kohrs
 */
public class ServerConnectionSettings {
	
	/**
	 * String containing server URL.
	 */
	private String host;
	
	/**
	 * String containing server port.
	 */
	private String port;
	
	/**
	 * Default class constructor using default values.
	 */
	public ServerConnectionSettings() {
		// TODO: Remove hard-coded values.
		this.host = "0.0.0.0";
		this.port = "8080";
	}
	
	/**
	 * Returns server host URL.
	 * @return
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Sets server host URL.
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Returns server port.
	 * @return
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * Sets server port.
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}

}
