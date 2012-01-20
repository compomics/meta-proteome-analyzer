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

    private Connection conn;
	private static String JDBCDRIVER = "com.mysql.jdbc.Driver";
    private static String URL_LOCALE = "jdbc:mysql://192.168.30.102";
    private static String URL_REMOTE = "jdbc:mysql://metaprot";
    private static String URL_2 = ":3306/";
    private static String USER = "metaprot";
    private static String PASS = "test";
    private String dbName;
	private boolean locale;
    /**
     * Constructor of the DatabaseStarter.
     * @param awsCredentials
     */
    public DBConfiguration(String dbName, boolean locale){
    	this.dbName = dbName;
    	this.locale = locale;
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
			if(locale) conn = DriverManager.getConnection(URL_LOCALE + URL_2 + dbName, USER, PASS);
			else conn = DriverManager.getConnection(URL_REMOTE + URL_2 + dbName, USER, PASS);
			
		} catch (SQLException e) {
			System.out.println("SQL state: " + e.getSQLState());
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
        return conn;
    }
}
