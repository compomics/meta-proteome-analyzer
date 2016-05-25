package de.mpa.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.log4j.Logger;

import de.mpa.client.Client;

/**
 * Abstract class of a job to be executed.
 * Implements the interface Executable which provides the specification of a Job.
 * @author Thilo Muth
 *
 */
public abstract class Job implements Executable {
	
	/**
	 * Job identifier.
	 */
	protected int id;
	
	/**
	 * Job error string.
	 */
	protected String error = null;
	
	/**
	 * JobStatus status.
	 */
	protected JobStatus status = JobStatus.WAITING;

	/**
	 * Job description.
	 */
	protected String description = "";
	
	/**
	 * Job filename.
	 */
	protected String filename;
	
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
     * Initialize the job logger.
     */
    protected static Logger log = Logger.getLogger(Job.class);
    
    /**
     * The algorithm properties.
     */
	protected ResourceProperties algorithmProperties = ResourceProperties.getInstance();
	
	/**
	 * Client instance.
	 */
	protected Client client = Client.getInstance();
    
	/**
	 * Executes the job.
	 */
	public void run() {
		log = Logger.getLogger(getClass());
		proc = null;
		try {
			proc = procBuilder.start();
			setStatus(JobStatus.RUNNING);
			if (client != null)
				client.firePropertyChange("new message", null, this.getDescription() + " " + this.getStatus());
		} catch (IOException ioe) {
			setError(ioe);
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
//			System.out.print(temp);
			log.info(temp.trim());
		}
		scan.close();

		try {
			proc.waitFor();
			done();
		} catch (InterruptedException e) {
			setError(e);
			e.printStackTrace();
			if (proc != null) {
				log.error("SUBPROCESS KILLED!");
				proc.destroy();
			}
		}		
	}
	
	/**
	 * Finalizes the job and sets the status to finished.
	 */
	protected void done() {
		// Set the job status to FINISHED and put the message in the queue
		setStatus(JobStatus.FINISHED);
		if (client != null)
			client.firePropertyChange("new message", null, this.getDescription() + " " + this.getStatus());
	}
	
	/**
	 * Returns the error message of the job. 
	 */
	public String getError() {		
		return error;
	}
	
	/**
	 * Sets the job's error message.
	 * @param error The error message string to be set.
	 */
	public void setError(String error) {
		setError(new Exception(error));
	}
	
	/**
	 * Sets the job's error message. 
	 * @param e The exception to be logged.
	 */
	public void setError(Exception e) {
		log.error(e.getMessage(), e.getCause());
		e.printStackTrace();
		this.error = e.getMessage();
		setStatus(JobStatus.ERROR);
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
	 * Returns the filename for a job specific file.
	 * @return
	 */
	public String getFilename(){
		return filename;
	}
	
	/**
	 * Sets the filename for a job specific file.
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
