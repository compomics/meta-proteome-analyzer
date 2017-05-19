package de.mpa.db.mysql.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.mpa.client.Constants;

public class ServerProperties {
	
	/**
	 * Properties instance.
	 */
	private Properties prop;
	
	/**
	 * JobProperties instance.
	 */
	private static ServerProperties instance;
    
	/**
	 * Constructor for the database manager.
	 * @throws IOException 
	 */
    private ServerProperties() throws IOException  {
        this.loadResourcesSettings();
	}
    
    /**
     * Returns an instance of the JobProperties.
     * @return JobProperties instance.
     * @throws IOException 
     */
    public static ServerProperties getInstance() {
    	if (ServerProperties.instance == null) {
    		try {
                ServerProperties.instance = new ServerProperties();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		return ServerProperties.instance;
    } 
    
    /**
     * Loads the resources settings from an external text file.
     * @throws IOException
     */
	private void loadResourcesSettings() throws IOException {
        this.prop = new Properties();
		
		// Load the resources settings via input stream.
		InputStream input = new FileInputStream(Constants.CONFIGURATION_PATH_JAR + File.separator + "server-settings.txt");
        this.prop.load(input);
	}
	
	/**
	 * Returns the job property for a given parameter.
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return this.prop.getProperty(key);
	}

}
