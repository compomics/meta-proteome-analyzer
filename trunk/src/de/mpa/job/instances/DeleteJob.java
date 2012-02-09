package de.mpa.job.instances;

import de.mpa.job.Job;

public class DeleteJob extends Job {	
	
	/**
	 * Constructor for the DeleteJob
	 * 
	 */
	public DeleteJob() {
		initJob();
	}	
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {		
		// Java commands
		procCommands.add("clearfolders.sh");
		procCommands.trimToSize();		
		procBuilder = new ProcessBuilder(procCommands);
		setDescription("CLEAR FOLDERS JOB --- " + procCommands);
		log.info(getDescription());		
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
}
