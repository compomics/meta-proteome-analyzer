package de.mpa.io.fasta;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.mpa.client.Constants;
import de.mpa.util.PropertyLoader;

/**
 * Class to run the fastaformat script
 * @author Robert
 *
 */
public class RunFastaFormater {
	/**
	 * The MPA log.
	 */
	private final Logger log = Logger.getLogger(this.getClass());
	/**
	 * Default Constructor 
	 */
	public RunFastaFormater(){
	}

	public void formatFastaDatabase(String fastaPath){
		// Construct fasta formater query query
		ArrayList<String> fastaFormatQuery = new ArrayList<String>();
		
		// Add commands to start the fasta formater script
		fastaFormatQuery.add(Constants.FASTA_FORMATER_PATH);

		// May not include ".fasta"
		fastaFormatQuery.add(fastaPath.split("[.]")[0]);
		fastaFormatQuery.add(PropertyLoader.getProperty(PropertyLoader.BASE_PATH));
		fastaFormatQuery.add(PropertyLoader.getProperty(PropertyLoader.BASE_PATH));
		//third path to xampp installation
		fastaFormatQuery.add(PropertyLoader.getProperty(PropertyLoader.XAMPP_PATH));

		// Add database 
		fastaFormatQuery.trimToSize();

		// Get Logging Panel
        this.log.info("FASTAFORMATER:  Starts the fastaformter");
        this.log.info("FASTAFORMATER:  Database:");
        this.log.info("FASTAFORMATER:  " + fastaPath);
        
		System.out.println(fastaFormatQuery.toString());
		
        
		// Construct Process
		Process process = null;
		try {
			ProcessBuilder builder = new ProcessBuilder(fastaFormatQuery);
			builder.redirectErrorStream(true);
			process = builder.start();
			
			// Get output of the fastaformater script
			BufferedReader reader =  new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ( (line = reader.readLine()) != null) {
                this.log.info("FASTAFORMATER:  " + line);
			}
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			process.destroy();
			// Define permissions for the new *fasta-files
			File fastaFolderFile = new File(Constants.FASTA_FORMATER_PATH);
			fastaFolderFile.setExecutable(true);
			fastaFolderFile.setReadable(true); 
			fastaFolderFile.setWritable(true);
		}
	}
}
