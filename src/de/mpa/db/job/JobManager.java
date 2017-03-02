package de.mpa.db.job;

import java.util.ArrayDeque;
import java.util.Queue;


/**
 * The JobManager handles the execution of the various jobs.
 * @author Thilo Muth
 *
 */
public class JobManager implements Runnable {
	
	/**
	 * JobManager instance.
	 */
	private static JobManager instance;

	/**
	 * JobQueue instance.
	 */
	private Queue<Job> jobQueue;
	
	
	/**
	 * Constructor for the job manager.
	 */
	private JobManager() {
		this.jobQueue = new ArrayDeque<Job>();
	}
	
	/**
	 * Returns the JobManager instance.
	 *
	 * @return the JobManager instance
	 */
	public static JobManager getInstance() {
		if (instance == null) {
			instance = new JobManager();
		}
		return instance;
	}
	
	/**
	 * Adds a job to the job queue.
	 * @param job
	 */
	public void addJob(Job job) {
		jobQueue.add(job);
	}
	
	/**
	 * Removes a job from the job queue.
	 * @param job
	 */
	public void deleteJob(Job job){
		jobQueue.remove(job);
	}
	
	/**
	 * Executes the jobs from the queue.
	 */
	public void run() {
		// Iterate the job queue
		for (Job job : jobQueue) {			
			job.run();
			// Remove job from the queue after successful execution.
			jobQueue.remove(job);
		}
	}
    
	/**
	 * This method deletes all the jobs from the queue.	
	 */	
	public void clear() {		
		jobQueue.clear();
	}
	
	/**
	 * Returns the number of jobs that have yet to be processed.
	 * @return The number of remaining jobs.
	 */
	public int getRemainingJobs() {
		return jobQueue.size();
	}
	
}
