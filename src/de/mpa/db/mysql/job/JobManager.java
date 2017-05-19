package de.mpa.db.mysql.job;

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
	private final Queue<Job> jobQueue;
	
	
	/**
	 * Constructor for the job manager.
	 */
	private JobManager() {
        jobQueue = new ArrayDeque<Job>();
	}
	
	/**
	 * Returns the JobManager instance.
	 *
	 * @return the JobManager instance
	 */
	public static JobManager getInstance() {
		if (JobManager.instance == null) {
            JobManager.instance = new JobManager();
		}
		return JobManager.instance;
	}
	
	/**
	 * Adds a job to the job queue.
	 * @param job
	 */
	public void addJob(Job job) {
        this.jobQueue.add(job);
	}
	
	/**
	 * Removes a job from the job queue.
	 * @param job
	 */
	public void deleteJob(Job job){
        this.jobQueue.remove(job);
	}
	
	/**
	 * Executes the jobs from the queue.
	 */
	public void run() {
		// Iterate the job queue
		for (Job job : this.jobQueue) {
			job.run();
			// Remove job from the queue after successful execution.
            this.jobQueue.remove(job);
		}
	}
    
	/**
	 * This method deletes all the jobs from the queue.	
	 */	
	public void clear() {
        this.jobQueue.clear();
	}
	
	/**
	 * Returns the number of jobs that have yet to be processed.
	 * @return The number of remaining jobs.
	 */
	public int getRemainingJobs() {
		return this.jobQueue.size();
	}
	
}
