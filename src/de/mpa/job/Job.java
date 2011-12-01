package de.mpa.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * Abstract class of a job to be executed.
 * Implements the interface Executable which provides the specification of a Job.
 * @author Thilo Muth
 *
 */
public abstract class Job implements Executable{
	
	// Default setting for the error --> NULL
	protected String error = null;
	
	// Default setting on JobStatus.WAITING
	private JobStatus status = JobStatus.WAITING;

	// Default description is an empty string
	private String description = "";
	
	// Filename representing the file.
	private String filename;
	
	/**
	 * The ProcessBuilder object
	 */
    protected ProcessBuilder procBuilder;
    
    /**
     * The Process object.
     */
    protected Process proc;
    
    /**
     * List of process commands.
     */
    protected ArrayList<String> procCommands = new ArrayList<String>();
    
    /**
     * Init the job logger.
     */
    protected static Logger log = Logger.getLogger(Job.class);
    
	/**
	 * Executes a job.
	 */
	public void execute() {
		proc = null;
		try {
			proc = procBuilder.start();
			setStatus(JobStatus.RUNNING);
		} catch (IOException ioe) {
			setStatus(JobStatus.ERROR);
			setError(ioe.getMessage());
			ioe.printStackTrace();
		}

		// Retrieve inputstream from process
		Scanner scan = new Scanner(proc.getInputStream());
		scan.useDelimiter(System.getProperty("line.separator"));
		
		// Temporary string variable
		String temp;

		// Get input from scanner and send to stdout
		while (scan.hasNext()) {
			temp = scan.next();

			if (temp.endsWith("done.") || temp.endsWith("loaded.") || temp.endsWith("started.") || temp.endsWith("loaded.")) {
				temp += "\n";
			} else {
				temp += " ";
			}
			System.out.print(temp);
		}
		scan.close();

		try {
			proc.waitFor();
			setStatus(JobStatus.FINISHED);
		} catch (InterruptedException e) {
			setError(e.getMessage());
			setStatus(JobStatus.ERROR);
			e.printStackTrace();
			if (proc != null) {
				proc.destroy();
			}
		}		
	}
	
	/**
	 * Returns the error message of the job. 
	 */
	public String getError() {		
		return error;
	}
	
	/**
	 * Returns the error message of the job. 
	 */
	public void setError(String error) {		
		this.error = error;
	}

	/**
	 * Returns the status of the job. 
	 * @return The status of this job
	 */
	public final JobStatus getStatus() {
		return this.status;
	}
	
	/**
	 * This method sets the status.
	 * @param status
	 */
	public void setStatus(JobStatus status) {
		this.status = status;
	}

	/**
	 * Returns the description of the job.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the job.
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the filename for an job specific file.
	 * @return
	 */
	public String getFilename(){
		return filename;
	}
}

