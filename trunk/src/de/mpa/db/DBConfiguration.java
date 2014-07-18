package de.mpa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JPasswordField;

import de.mpa.client.settings.ParameterMap;

/**
 * This class manages the configuration for the database.
 * @author T.Muth
 * 
 */
public class DBConfiguration {
	
	/**
	 * Database connection.
	 */
    private Connection conn;
    
	/**
	 * Connection settings.
	 */
	private ParameterMap connectionParams;
	
    /**
     * Constructor of the DatabaseStarter.
     * @param dbName Database name
     * @throws SQLException 
     */
    public DBConfiguration(ParameterMap connectionParams) throws SQLException{
    	this.connectionParams = connectionParams;
        initConnection();
    }
    
	/**
	 * Initializes the database connection.
	 * @throws SQLException 
	 */
	private void initConnection() throws SQLException {
		// Get a connection to the database
		try {
			// Register the JDBC driver for MySQL
			Class.forName("com.mysql.jdbc.Driver");

			// Get the password from the JPasswordField
			String pwText = new String(((JPasswordField) connectionParams.get("dbPass").getValue()).getPassword());
			
			// Do the connection to the DB
			conn = DriverManager.getConnection("jdbc:mysql://" + connectionParams.get("dbAddress").getValue().toString().trim() + ":" + ((Integer[]) connectionParams.get("dbPort").getValue())[0] + "/" + connectionParams.get("dbName").getValue(), connectionParams.get("dbUsername").getValue().toString(), pwText);
			
			// Set auto commit == FALSE --> Manual commit & rollback.
			conn.setAutoCommit(false);
			
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}
    
    /**
     * Returns the connection.
     * @return connection Connection instance
     * @throws SQLException 
     */
    public Connection getConnection() throws SQLException {
        return conn;
    }
}