package de.mpa.db.mysql;

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
	private final ParameterMap connectionParams;
	
    /**
     * Constructor of the DatabaseStarter.
     * @param dbName Database name
     * @throws SQLException 
     */
    public DBConfiguration(ParameterMap connectionParams) throws SQLException {
    	this.connectionParams = connectionParams;
        initConnection();
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
			String dbAddress = (String) this.connectionParams.get("dbAddress").getValue();
			Integer dbPort = (Integer) this.connectionParams.get("dbPort").getValue();
			String dbName = (String) this.connectionParams.get("dbName").getValue();
			String dbUser = (String) this.connectionParams.get("dbUsername").getValue();
			String dbPass = (String) this.connectionParams.get("dbPass").getValue();
			
			// Establish connection to the DB
            this.conn = DriverManager.getConnection("jdbc:mysql://" + dbAddress + ":" + dbPort + "/" + dbName, dbUser, dbPass);
			
			// Set auto commit == FALSE --> Manual commit & rollback.
            this.conn.setAutoCommit(false);
			
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
        return this.conn;
    }
}