package de.mpa.client.settings;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.xml.ws.WebServiceException;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.settings.Parameter.ButtonParameter;
import de.mpa.client.settings.Parameter.NumberParameter;
import de.mpa.client.settings.Parameter.PasswordParameter;
import de.mpa.client.settings.Parameter.TextParameter;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.main.Starter;

/**
 * Parameter map holding database and server connection settings.
 * @author A. Behne
 */
public class ConnectionParameters extends ParameterMap {
	
	/**
	 * Default serialization ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The default database connection port.
	 */
	public static final int DEFAULT_DB_PORT = 3306;
	
	/**
	 * The default server connection port.
	 */
	public static final int DEFAULT_SRV_PORT = 8080;

	/**
	 * Initializes connection parameters from default values.
	 */
	public ConnectionParameters() {
		this.initDefaults();
	}

	@Override
	public void initDefaults() {
		
		// database settings
		this.put("dbAddress", new TextParameter("", "Database Address", "The network address of the database. May be an URL or IP address.", "Database Connection"));
		this.put("dbName", new TextParameter("", "Database Name", "The database name.", "Database Connection"));
		this.put("dbUsername", new TextParameter("", "Username", "The username for connecting to the database.", "Database Connection"));
		this.put("dbPass", new PasswordParameter("", "Password", "The password for connecting to the database.", "Database Connection"));
		this.put("dbPort", new NumberParameter(DEFAULT_DB_PORT, 0, 65535, "Database Port", "The network port number for communicating with the database.", "Database Connection"));
		
		Action testDbAction = new AbstractAction("Test Connection", IconConstants.DATABASE_CONNECT_ICON) {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Client client = Client.getInstance();
				
				// method closes old connection
				try {
					client.closeDBConnection();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				// Update connection parameters in the client
				updateParams();
				
				// try new connection				
				try {
					client.getConnection();
					JOptionPane.showMessageDialog(ClientFrame.getInstance(),
							"Connection to database is valid.", "Database Connection", JOptionPane.INFORMATION_MESSAGE);
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(ClientFrame.getInstance(),
							"Could not connect to database. Please verify your connection settings.",
							"Database Connection", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		testDbAction.putValue(Action.SHORT_DESCRIPTION, "Test the validity of the database connection settings.");
		this.put("dbTest", new ButtonParameter(testDbAction, "Database Connection"));

		// web service settings
		this.put("srvAddress", new TextParameter("", "Server Address", "The network address of the server application. May be an URL or IP address.", "Server Connection"));
		this.put("srvPort", new NumberParameter(DEFAULT_SRV_PORT, 0, 65535, "Server Port", "The network port number for communicating with the server application.", "Server Connection"));
		
		Action testSrvAction = new AbstractAction("Test Connection", IconConstants.SERVER_CONNECT_ICON) {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Try to connect to server.		
				try {
					updateParams();
					Client client = Client.getInstance();
					client.connectToServer();
					
					JOptionPane.showMessageDialog(ClientFrame.getInstance(), "Server connection is working.", "Database Connection", JOptionPane.INFORMATION_MESSAGE);
				} catch (WebServiceException e) {
					JOptionPane.showMessageDialog(ClientFrame.getInstance(),
							"Could not connect to server. Please verify your connection settings.",
							"Server Connection", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		this.put("srvTest", new ButtonParameter(testSrvAction, "Server Connection"));
		
		// parse settings file
		try {
			this.readUserParamsFromFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method updates the parameters within the client.
	 */
	private void updateParams() {
		Client.getInstance().setConnectionParams(this);
	}
	
	/**
	 * Reads the connection parameters from a file.
	 * @throws IOException 
	 */
	private void readUserParamsFromFile() throws IOException {
		BufferedReader br = null;
				
		if (Starter.isJarExport()) {
			br = new BufferedReader(new FileReader(new File(Constants.CONFIGURATION_PATH_JAR + File.separator + "connection-settings.txt")));
		} else {
			InputStream is = ClassLoader.getSystemResourceAsStream(Constants.CONFIGURATION_PATH_JAR + File.separator + "connection-settings.txt");
			br = new BufferedReader(new InputStreamReader(is));
		}
		
		String line;
		while ((line = br.readLine()) != null) {
			String[] split = line.split("=");
			if (split.length > 1) {
				this.setValue(split[0], split[1]);
			}
			
		}
		br.close();
	}
	
	
	@Override
	public File toFile(String path) throws IOException {
		// Append extension if it's missing
		if (!path.endsWith(".txt")) {
			path += ".txt";
		}
		
		// Create new file at specified path
		File file = new File(path);
		// Check for whether path does point to a file (and not a directory)
		if(!file.isFile()) {
			throw new IOException();
		}
		
		// Set up writer for parameter file
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		// Grab a set of default parameters for comparison purposes
		ConnectionParameters defaults = new ConnectionParameters();
		
		// Iterate stored parameter values and compare them to the defaults
		for (Entry<String, Parameter> entry : this.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue().getValue();
			Object defaultValue = defaults.get(key).getValue();
			if (value instanceof JPasswordField) {
				// special case for combobox models, compare selected items
				value = new String(((JPasswordField) value).getPassword());
				defaultValue = new String(((JPasswordField) defaultValue).getPassword());
			}
			// Compare values, if they differ add a line to the configuration file
			if (!value.equals(defaultValue)) {
				// Write line containing non-default value
				bw.append(key + "=" + value);
				bw.newLine();
			}
		}
		
		// Close writer
		bw.flush();
		bw.close();
		return file;
	}

}
