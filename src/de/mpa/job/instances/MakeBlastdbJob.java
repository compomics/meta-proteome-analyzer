package de.mpa.job.instances;

import java.io.File;

import org.apache.log4j.Logger;

import de.mpa.job.Job;
import de.mpa.util.FormatUtilities;

public class MakeBlastdbJob extends Job {
	
	private File fastaFile;

	/**
	 * Constructor for the MakeBlastdbJob
	 * @param fastaFile
	 */		 
	public MakeBlastdbJob(File fastaFile) {
		log = Logger.getLogger(getClass());
		this.fastaFile = fastaFile;
		initJob();
	}
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		// set the description
		setDescription("MAKEBLASTDB JOB");
		
		// full path to executable
		procCommands.add(algorithmProperties.getProperty("path.makeblastdb") + algorithmProperties.getProperty("app.makeblastdb"));
		
		// Link to output file
		procCommands.add("-in");
		
		// Use makeblastdb naming convention for escaping whitespace characters
		if (FormatUtilities.containsWhiteSpace(fastaFile.getAbsolutePath())) {
			log.error("Please remove white spaces from the path directory of the FASTA file: " + fastaFile.getAbsolutePath());
		}
		procCommands.add(fastaFile.getAbsolutePath());
		procCommands.add("-dbtype");
		procCommands.add("prot");
		procCommands.trimToSize();
		log.info(procCommands);
		procBuilder = new ProcessBuilder(procCommands);
		
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}

}
