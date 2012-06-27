package de.mpa.job.instances;

import de.mpa.job.Job;

public class RenameJob extends Job {
	protected String oldname; 
	protected String newname;	
	
	public RenameJob(){
		
	}
	
	/**
	 * Constructor for the RenameJob.
	 * 
	 * @param oldname
	 * @param newname
	 */
	public RenameJob(String oldname, String newname) {
		this.oldname = oldname;
		this.newname = newname;
		initJob();
	}	

	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	protected void initJob() {	
		setDescription("RENAME JOB: " + oldname + " TO " +newname);
		// Java commands
		procCommands.add("mv");
		procCommands.add(oldname);	
		procCommands.add(newname);	
		procCommands.trimToSize();
		procBuilder = new ProcessBuilder(procCommands);

		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}

}
