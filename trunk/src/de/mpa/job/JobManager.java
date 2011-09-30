package de.mpa.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


/**
 * The JobManager handles the execution of the various jobs.
 * @author Thilo Muth
 *
 */
public class JobManager {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
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
	public void execute() throws Exception{
		for(Job job : jobQueue){
			log.info("Executing job: " + job.getDescription());
			job.execute();			
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
