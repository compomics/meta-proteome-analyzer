package de.mpa.task;

import java.util.ArrayDeque;
import java.util.Queue;


/**
 * The JobManager handles the execution of the various tasks.
 * @author Thilo Muth
 *
 */
public class TaskManager implements Runnable {
	
	/**
	 * JobManager instance.
	 */
	private static TaskManager instance;

	/**
	 * JobQueue instance.
	 */
	private Queue<Task> jobQueue;
	
	
	/**
	 * Constructor for the job manager.
	 */
	private TaskManager() {
		this.jobQueue = new ArrayDeque<Task>();
	}
	
	/**
	 * Returns the JobManager instance.
	 *
	 * @return the JobManager instance
	 */
	public static TaskManager getInstance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}
	
	/**
	 * Adds a job to the job queue.
	 * @param job
	 */
	public void addJob(Task job){
		jobQueue.add(job);
	}
	
	/**
	 * Removes a job from the job queue.
	 * @param job
	 */
	public void deleteJob(Task job){
		jobQueue.remove(job);
	}
	
	/**
	 * Executes the jobs from the queue.
	 */
	public void run() {
		// Iterate the job queue
		for (Task job : jobQueue) {		
			job.run();
			// Remove job from the queue after successful execution.
			jobQueue.remove(job);
		}
	}
    
	/**
	 * This method deletes all the jobs from the queue.	
	 */	
	public void clear(){		
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
