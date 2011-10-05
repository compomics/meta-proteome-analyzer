package de.mpa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class manages the configuration for the database.
 * @author Thilo Muth
 * 
 */
public class DBConfiguration {

    private Connection connection;
	private static String JDBCDRIVER = "com.mysql.jdbc.Driver";
    private static String URL_1 = "jdbc:mysql://localhost";
    private static String URL_2 = ":3306/";
    private static String USER = "";
    private static String PASS = "";
    private String dbName;
    /**
     * Constructor of the DatabaseStarter.
     * @param awsCredentials
     */
    public DBConfiguration(String dbName){
    	this.dbName = dbName;
        initConnection();
    }
    
	/**
	 * Initializes the database connection.
	 */
	private void initConnection() {
		// Get a connection to the database
		try {
			// Register the JDBC driver for MySQL
			Class.forName(JDBCDRIVER);

			// Do the connection to the DB

			connection = DriverManager.getConnection(URL_1 + URL_2 + dbName,
					USER, PASS);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}
    
    /**
     * Returns the connection.
     * @return connection Connection instance
     */
    public Connection getConnection() {
        return connection;
    }
}
