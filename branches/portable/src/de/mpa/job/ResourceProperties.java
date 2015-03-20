package de.mpa.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.mpa.client.Constants;
import de.mpa.main.Starter;

public class ResourceProperties {
	
	/**
	 * Properties instance.
	 */
	private Properties prop;
	
	/**
	 * JobProperties instance.
	 */
	private static ResourceProperties instance;
    
	/**
	 * Constructor for the database manager.
	 * @throws IOException 
	 */
    private ResourceProperties() throws IOException  {
    	loadResourcesSettings();
	}
    
    /**
     * Returns an instance of the JobProperties.
     * @return JobProperties instance.
     * @throws IOException 
     */
    public static ResourceProperties getInstance() {
    	if (instance == null) {
    		try {
				instance = new ResourceProperties();
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
		InputStream inputStream = null;
		String path = getBasePath(this.getClass().getResource("ResourceProperties.class").getPath(), "/bin/de/mpa");
		if (Starter.isJarExport()) {
			inputStream = new FileInputStream(Constants.CONFIGURATION_PATH_JAR + File.separator + "algorithm-properties.txt");
		} else {
			inputStream = this.getClass().getResourceAsStream(Constants.CONFIGURATION_PATH + "algorithm-properties.txt");
		}
		prop.load(inputStream);
		prop.setProperty("path.fasta", path + "/built/fasta");
		prop.setProperty("path.taxonomy", path + "/built/taxonomy/");
		prop.setProperty("path.base", path);
		
		if (Starter.isWindows()) {
			prop.setProperty("path.xtandem", path + "/built/X!Tandem/windows/windows_64bit");
			prop.setProperty("path.omssa", path + "/built/OMSSA/windows");
			prop.setProperty("app.omssa", "omssacl.exe");
			prop.setProperty("path.qvality", path + "/built/QVality/windows/");
			prop.setProperty("app.qvality", "qvality.exe");
		}
		inputStream.close();
	}
	
	/**
	 * Returns the job property for a given parameter.
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return prop.getProperty(key);
	}
	
    public static String getBasePath(String classPath, String toolName) {
        String path = classPath;
        if (path.lastIndexOf(toolName) != -1) {
            path = path.substring(1, path.lastIndexOf(toolName));
            path = path.replace("%20", " ");
            path = path.replace("%5b", "[");
            path = path.replace("%5d", "]");

            if (System.getProperty("os.name").lastIndexOf("Windows") != -1) {
                path = path.replace("/", "\\");
            }
        } else {
            path = ".";
        }
        
        return path;
    }
	

}
