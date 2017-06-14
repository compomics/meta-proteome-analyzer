package de.mpa.db.mysql.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.glassfish.hk2.internal.ConstantActiveDescriptor;

import de.mpa.client.Constants;
import de.mpa.webservice.Message;
import de.mpa.webservice.MessageQueue;

/**
 * Abstract class of a job to be executed. Implements the interface Executable
 * which provides the specification of a Job.
 * 
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
	protected String error;

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
	 * The job properties.
	 */
	protected ServerProperties jobProperties = ServerProperties.getInstance();

	/**
	 * Executes the job.
	 */
	public void run() {
		this.proc = null;
		try {
			this.proc = this.procBuilder.start();
			this.setStatus(JobStatus.RUNNING);
		} catch (IOException ioe) {
			this.setError(ioe);
			ioe.printStackTrace();
		}

		// Retrieve inputstream from process
		Scanner scan = new Scanner(this.proc.getInputStream());
		scan.useDelimiter(System.getProperty("line.separator"));

		// Temporary string variable
		String temp;

		// Get input from scanner and send to stdout
		while (scan.hasNext()) {
			temp = scan.next();

			if (temp.endsWith("done.") || temp.endsWith("loaded.") || temp.endsWith("started.")
					|| temp.endsWith("loaded.")) {
				temp += "\n";
			} else {
				temp += " ";
			}
		}
		scan.close();

		// try {
		// proc.waitFor();
		// done();
		// } catch (InterruptedException e) {
		// setError(e);
		// e.printStackTrace();
		// if (proc != null) {
		// log.error("SUBPROCESS KILLED!");
		// proc.destroy();
		// }
		// }
		this.done();
	}

	/**
	 * Finalizes the job and sets the status to finished.
	 */
	protected void done() {
		// Set the job status to FINISHED and put the message in the queue
		this.setStatus(JobStatus.FINISHED);
	}

	/**
	 * Returns the error message of the job.
	 */
	public String getError() {
		return this.error;
	}

	/**
	 * Sets the job's error message.
	 * 
	 * @param error
	 *            The error message string to be set.
	 */
	public void setError(String error) {
		this.setError(new Exception(error));
	}

	/**
	 * Sets the job's error message.
	 * 
	 * @param e
	 *            The exception to be logged.
	 */
	public void setError(Exception e) {
		Job.log.error(e.getMessage(), e.getCause());
		e.printStackTrace();
		error = e.getMessage();
		this.setStatus(JobStatus.ERROR);
	}

	/**
	 * Returns the status of the job.
	 * 
	 * @return The status of this job
	 */
	public final JobStatus getStatus() {
		return status;
	}

	/**
	 * This method sets the status.
	 * 
	 * @param status
	 */
	public void setStatus(JobStatus status) {
		this.status = status;
		if (Constants.VERBOSE_LOG_OUTPUT) {
			MessageQueue.getInstance().add(new Message(this, new Date()), Job.log);
		}
	}

	/**
	 * Returns the description of the job.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description of the job.
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the filename for a job specific file.
	 * 
	 * @return
	 */
	public String getFilename() {
		return this.filename;
	}

	/**
	 * Sets the filename for a job specific file.
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
