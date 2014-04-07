package de.mpa.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class represents the settings for the database connection.
 * @author T.Muth
 *
 */
public class DbConnectionSettings {
	
	/**
	 * JDBC driver.
	 */
	private String jdbcDriver = "com.mysql.jdbc.Driver";
	
	/**
	 * Locale URL.
	 */
	private String urlLocale = "jdbc:mysql://";
	
	/**
	 * Remote URL.
	 */
	private String urlRemote = "jdbc:mysql://";
	
	/**
	 * Connection port.
	 */
	private String port = ":";
	
	/**
	 * Database username.
	 */
	private String username;
	
	/**
	 * Database password.
	 */
	private String password;
	
	/**
	 * Loads the database connection settings from the settings file.
	 */
	public DbConnectionSettings() {
		try {
			readSettingsFromFile(new File("password/DbConnectionSettings.txt"));
		} catch (IOException e) {
			e.printStackTrace();
//			JXErrorPane.showDialog(ClientFrame.getInstance(),
//					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	/**
	 * Method to read database connection setting parameters from an external file.
	 * @param settingsFile the File to be read from
	 * @throws IOException
	 */
	public void readSettingsFromFile(File settingsFile) throws IOException {
		FileReader fr = new FileReader(settingsFile);
		BufferedReader textReader = new BufferedReader(fr);
		urlLocale += textReader.readLine();
		urlRemote += textReader.readLine();
		port += textReader.readLine() + "/";
		username = textReader.readLine();
		password = textReader.readLine();
		textReader.close();
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}
	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getUrlLocale() {
		return urlLocale;
	}
	public void setUrlLocale(String urlLocale) {
		this.urlLocale = urlLocale;
	}

	public String getUrlRemote() {
		return urlRemote;
	}
	public void setUrlRemote(String urlRemote) {
		this.urlRemote = urlRemote;
	}

	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
