package de.mpa.client.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.xml.ws.WebServiceException;

import de.mpa.client.Client;
import de.mpa.client.Constants;
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
	
	public static final String DEFAULT_PORT = "8080";

	
	public ConnectionParameters() {
		initDefaults();
	}

	@Override
	public void initDefaults() {
		// database connection settings
		try {
			readUserParamsFromFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		this.put("dbPort", new Parameter("Database Port", new Integer[] { 3306, 0, 65535 }, "Database Connection", "The network port number for communicating with the database."));
		
		
		JButton dbTestButton = new JButton("Test Connection", IconConstants.DATABASE_CONNECT_ICON);
		dbTestButton.addActionListener(new ActionListener() {
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
		});
		this.put("dbTest", new Parameter(null, dbTestButton, "Database Connection", "Test the validity of the database connection settings."));

		// Webservice server settings.
		this.put("srvPort", new Parameter("Server Port", new Integer[] {8080, 0, 65535 }, "Server Connection", "The network port number for communicating with the server application."));
		
		JButton srvTestButton = new JButton("Test Connection", IconConstants.SERVER_CONNECT_ICON);
		srvTestButton.addActionListener(new ActionListener() {
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
		});
		this.put("srvTest", new Parameter(null, srvTestButton, "Server Connection", "Test the validity of the server connection settings."));
	}
	
	/**
	 * This method updates the parameters within the client.
	 */
	public void updateParams() {
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
			if (split[0].equals("dbAddress")) {
				this.put("dbAddress", new Parameter("Database Address", split[1], "Database Connection", "The network address of the database. May be an URL or IP address."));
			} else if (split[0].equals("dbName")) {
				this.put("dbName", new Parameter("Database Name", split[1], "Database Connection", "The database name."));
			} else if (split[0].equals("dbUsername")) {
				this.put("dbUsername", new Parameter("Username", split[1], "Database Connection", "The username for connecting to the database."));
			} else if (split[0].equals("dbPass")) {
				this.put("dbPass", new Parameter("Password", new JPasswordField(split[1]), "Database Connection", "The password for connecting to the database."));
			} else if (split[0].equals("srvAddress")) {
				// server connection settings
				this.put("srvAddress", new Parameter("Server Address", split[1], "Server Connection", "The network address of the server application. May be an URL or IP address."));
			}
		}
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
