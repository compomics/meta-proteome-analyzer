package de.mpa.job;

public class DeleteJob extends Job {	
	
	/**
	 * The filename String.
	 */
	private String filename;
	
	/**
	 * Constructor for the DeleteJob
	 * 
	 * @param filename
	 */
	public DeleteJob(String filename) {
		this.filename = filename;
		initJob();
	}	
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {		
		// Java commands
		procCommands.add("rm");
		procCommands.add(filename);		
		procCommands.trimToSize();		
		procBuilder = new ProcessBuilder(procCommands);
		setDescription("DELETE JOB --- " + procCommands);
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
}
