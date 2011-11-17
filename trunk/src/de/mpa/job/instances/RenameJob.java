package de.mpa.job.instances;

import de.mpa.job.Job;

public class RenameJob extends Job{
	private String oldname; 
	private String newname;	
	/**
	 * Constructor for the DeleteJob
	 * 
	 * @param oldname
	 * @param newname
	 */
	public RenameJob(String oldname, String newname) {
		this.oldname = oldname;
		this.newname = newname;
		initJob();
		super.execute();		
	}	
		
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {		
		// Java commands
		procCommands.add("mv");
		procCommands.add(oldname);	
		procCommands.add(newname);	
		procCommands.trimToSize();

		log.info("RENAME " + procCommands);
		procBuilder = new ProcessBuilder(procCommands);

		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
}