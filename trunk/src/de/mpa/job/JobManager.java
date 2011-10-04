package de.mpa.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * The JobManager handles the execution of the various jobs.
 * @author Thilo Muth
 *
 */
public class JobManager {
	
	private Logger log = Logger.getLogger(JobManager.class);
	
	private List<Job> jobQueue;
	
	private List<Object> objects;
	
	/**
	 * Constructor for the job manager.
	 */
	public JobManager(){
		jobQueue = new ArrayList<Job>();
	}
	
	/**
	 * Adds a job to the job queue.
	 * @param job
	 */
	public void addJob(Job job){
		jobQueue.add(job);
	}
	
	/**
	 * Executes the jobs from the queue.
	 */
	public void execute(){
		for(Job job : jobQueue){			
			job.execute();
			if (job.getStatus() == JobStatus.ERROR){
				log.error(job.getError());
			}			
		}
		
	}
	
	/**
	 * Returns a list of objects.
	 * @return
	 */
	public List<Object> getObjects(){
		return objects;
	}
	
	/**
	 * This method deletes all the jobs from the queue.	
	 */	
	public void clear(){		
		jobQueue.clear();
	}
}
