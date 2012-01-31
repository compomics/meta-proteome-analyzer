package de.mpa.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DbConnectionSettings {

	private String jdbcDriver = "com.mysql.jdbc.Driver";
	private String urlLocale = "jdbc:mysql://";
	private String urlRemote = "jdbc:mysql://";
	private String port = ":";
	private String username;
	private String password;

	public DbConnectionSettings() {
		// read settings from file
		try {
			readSettingsFromFile(new File("password/DbConnectionSettings.txt"));
		} catch (IOException e) {
			e.printStackTrace();
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
//		System.out.println(textReader.readLine());
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
