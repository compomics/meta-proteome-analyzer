package de.mpa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    public DBConfiguration(ParameterMap connectionParams) throws SQLException {
    	this.connectionParams = connectionParams;
        this.initConnection();
    }
    
	/**
	 * Initializes the database connection.
	 * @throws SQLException 
	 */
	private void initConnection() throws SQLException {
		try {
			// Register the JDBC driver for MySQL
			Class.forName("com.mysql.jdbc.Driver");

			// Extract connection parameters
			String dbAddress = (String) connectionParams.get("dbAddress").getValue();
			Integer dbPort = (Integer) connectionParams.get("dbPort").getValue();
			String dbName = (String) connectionParams.get("dbName").getValue();
			String dbUser = (String) connectionParams.get("dbUsername").getValue();
			String dbPass = (String) connectionParams.get("dbPass").getValue();
			// Establish connection to the DB
			conn = DriverManager.getConnection("jdbc:mysql://" + dbAddress + ":" + dbPort + "/" + dbName, dbUser, dbPass);
			
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