package de.mpa.client.settings;

/**
 * Class to hold server connection settings.
 * @author Alexander Behne, Fabian Kohrs
 */
public class ServerConnectionSettings {
	
	/**
	 * Default host URL.
	 */
	public static final String DEFAULT_HOST = "0.0.0.0";
	
	/**
	 * Default port.
	 */
	public static final String DEFAULT_PORT = "8080";

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
		this.host = DEFAULT_HOST;
		this.port = DEFAULT_PORT;
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
