package de.mpa.job.instances;

import de.mpa.job.Job;

/**
 * Wrapper job class for executing a bash script to recursively delete files in a folder and its subfolders:
 * clearfolders.sh: find /folder/subfolder -type f -exec rm -f {} \;
 * 
 * @author T.Muth
 * @date 11/07/2012
 * 
 */
public class DeleteJob extends Job {	
	/**
	 * Constructor for the DeleteJob
	 */
	public DeleteJob() {
		initJob();
	}	
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {		
		// Java commands
		procCommands.add(JobConstants.MAIN_PATH + "clearfolders.sh");
		procCommands.trimToSize();		
		procBuilder = new ProcessBuilder(procCommands);
		setDescription("CLEAR FOLDERS");
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
}
