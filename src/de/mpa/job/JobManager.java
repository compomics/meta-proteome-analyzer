package de.mpa.job;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import de.mpa.job.instances.MS2FormatJob;
import de.mpa.webservice.Message;


/**
 * The JobManager handles the execution of the various jobs.
 * @author Thilo Muth
 *
 */
public class JobManager {
	
	private Logger log = Logger.getLogger(JobManager.class);
	
	private Queue<Job> jobQueue;
	
	private List<Object> objects;
	
	private Queue<Message> msgQueue;
	
	private Map<String, String> filenameMap; 
	
	/**
	 * Constructor for the job manager.
	 */
	public JobManager(Queue<Message> msgQueue){
		this.jobQueue = new ArrayDeque<Job>();
		this.msgQueue = msgQueue;
		this.filenameMap = new HashMap<String, String>();
	}
	
	/**
	 * Adds a job to the job queue.
	 * @param job
	 */
	public void addJob(Job job){
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
	public void execute(){
		// Iterate the job queue
		for(Job job : jobQueue){		
			if(job instanceof MS2FormatJob){
				MS2FormatJob ms2formatjob = (MS2FormatJob) job;
				ms2formatjob.run();
			} else {
				// Set the job status to RUNNING and put the message in the queue
				msgQueue.add(new Message(job, JobStatus.RUNNING.toString(), new Date()));
				filenameMap.put(job.getDescription(), job.getFilename());
				job.execute();
			}
			if (job.getStatus() == JobStatus.ERROR){
				log.error(job.getError());
			}		
			// Set the job status to FINISHED and put the message in the queue
			msgQueue.add(new Message(job, JobStatus.FINISHED.toString(), new Date()));
			jobQueue.remove(job);
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
	
	/**
	 * Returns the filenames.
	 * @return filename map The Map<String, String>
	 */
	public Map<String, String> getFilenames(){
		return filenameMap;
	}
}
