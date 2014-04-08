package de.mpa.client.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import de.mpa.client.Client;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.icons.IconConstants;

/**
 * Parameter map holding database and server connection settings.
 * @author A. Behne
 */
public class ConnectionParameters extends ParameterMap {
	
	@Override
	public void initDefaults() {
		// database connection settings
		// TODO: read defaults from connection settings file
		this.put("dbAddress", new Parameter("Database Address", "metaprot", "Database Connection", "The network address of the database. May be an URL or IP address."));
		this.put("dbPort", new Parameter("Database Port", new Integer[] { 3306, 0, 65535 }, "Database Connection", "The network port number for communicating with the database."));
		this.put("dbName", new Parameter("Username", "metaroot", "Database Connection", "The username for connecting to the database."));
		this.put("dbPass", new Parameter("Password", new JPasswordField("test"), "Database Connection", "The password for connecting to the database."));
		
		JButton dbTestButton = new JButton("Test Connection", IconConstants.DATABASE_CONNECT_ICON);
		dbTestButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// TODO: implement test button, refactor client connection settings
//				DbConnectionSettings dbSettings = gatherDbSettings();
				Client client = Client.getInstance();
//				client.setDatabaseConnectionSettings(dbSettings);

				// method closes old connection
				try {
					client.closeDBConnection();				
				} catch (SQLException e) {
					e.printStackTrace();
				}
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
		
		// server connection settings
		this.put("srvAddress", new Parameter("Server Address", ServerConnectionSettings.DEFAULT_HOST, "Server Connection", "The network address of the server application. May be an URL or IP address."));
		this.put("srvPort", new Parameter("Server Port", new Integer[] { ServerConnectionSettings.DEFAULT_PORT, 0, 65535 }, "Server Connection", "The network port number for communicating with the server application."));
		
		JButton srvTestButton = new JButton("Test Connection", IconConstants.SERVER_CONNECT_ICON);
		srvTestButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// TODO: implement test button, refactor client connection settings
			}
		});
		this.put("srvTest", new Parameter(null, srvTestButton, "Server Connection", "Test the validity of the server connection settings."));
		
	}

	@Override
	public File toFile(String path) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
