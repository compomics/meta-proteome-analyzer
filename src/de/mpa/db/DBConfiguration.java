package de.mpa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
     * Database name.
     */
    private String dbName;
    
    /**
     * Database connection type.
     */
	private ConnectionType connType;
	
	/**
	 * Database connection settings.
	 */
	private DbConnectionSettings dbSettings;
	
    /**
     * Constructor of the DatabaseStarter.
     * @param awsCredentials
     * @throws SQLException 
     */
    public DBConfiguration(String dbName, ConnectionType connType, DbConnectionSettings dbSettings) throws SQLException{
    	this.dbSettings = dbSettings;
    	this.dbName = dbName;
    	this.connType = connType;
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
			Class.forName(dbSettings.getJdbcDriver());

			// Do the connection to the DB
			if (connType == ConnectionType.LOCAL) {
				conn = DriverManager.getConnection(dbSettings.getUrlLocale()+ dbSettings.getPort() + dbName, dbSettings.getUsername(), dbSettings.getPassword());
			} else {
				conn = DriverManager.getConnection(dbSettings.getUrlRemote()+ dbSettings.getPort() + dbName, dbSettings.getUsername(), dbSettings.getPassword());
			}
			
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