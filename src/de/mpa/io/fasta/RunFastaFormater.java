package de.mpa.io.fasta;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.mpa.client.Constants;

/**
 * Class to run the fastaformat script
 * @author Robert
 *
 */
public class RunFastaFormater {
	/**
	 * The MPA log.
	 */
	private Logger log = Logger.getLogger(getClass());
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

		// Add database 
		fastaFormatQuery.trimToSize();

		// Get Logging Panel
		log.info("FASTAFORMATER:  Starts the fastaformter");
		log.info("FASTAFORMATER:  Database:");
		log.info("FASTAFORMATER:  " + fastaPath);
		
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
				log.info("FASTAFORMATER:  " + line);
			}
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}finally{
			process.destroy();
			// Define permissions for the new *fasta-files
			File fastaFolderFile = new File(Constants.FASTA_FORMATER_PATH);
			fastaFolderFile.setExecutable(true);
			fastaFolderFile.setReadable(true); 
			fastaFolderFile.setWritable(true);
		}
	}
}
