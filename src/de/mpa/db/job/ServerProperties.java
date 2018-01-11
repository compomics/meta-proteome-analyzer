package de.mpa.db.job;

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
    	loadResourcesSettings();
	}
    
    /**
     * Returns an instance of the JobProperties.
     * @return JobProperties instance.
     * @throws IOException 
     */
    public static ServerProperties getInstance() {
    	if (instance == null) {
    		try {
				instance = new ServerProperties();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		return instance;
    } 
    
    /**
     * Loads the resources settings from an external text file.
     * @throws IOException
     */
	private void loadResourcesSettings() throws IOException {
		prop = new Properties();
		
		// Load the resources settings via input stream.
		
		// adjusted path to src
		InputStream input = new FileInputStream("src\\"  + Constants.CONFIGURATION_PATH_JAR + File.separator + "server-settings.txt");
		prop.load(input);
	}
	
	/**
	 * Returns the job property for a given parameter.
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return prop.getProperty(key);
	}

}
