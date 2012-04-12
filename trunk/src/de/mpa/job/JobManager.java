package de.mpa.job;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import de.mpa.job.instances.DeleteJob;
import de.mpa.job.instances.MS2FormatJob;
import de.mpa.webservice.Message;
import de.mpa.webservice.MessageQueue;


/**
 * The JobManager handles the execution of the various jobs.
 * @author Thilo Muth
 *
 */
public class JobManager {
	
	/**
	 * JobManager instance.
	 */
	private static JobManager instance;

	/**
	 * Logger instance.
	 */
	private Logger log = Logger.getLogger(JobManager.class);
	
	/**
	 * JobQueue instance.
	 */
	private Queue<Job> jobQueue;
	
	/**
	 * List of objects from the various
	 */
	private List<Object> objects;
	
	/**
	 * MessageQueue instance.
	 */
	private Queue<Message> msgQueue;
	
	/**
	 * Helper map instance for mapping job descriptions to job filenames.
	 */
	private Map<String, String> filenameMap; 
	
	/**
	 * Constructor for the job manager.
	 */
	private JobManager() {
		this.jobQueue = new ArrayDeque<Job>();
		this.msgQueue = MessageQueue.getInstance();
		this.filenameMap = new HashMap<String, String>();
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
	public void execute() {
		// Iterate the job queue
		for(Job job : jobQueue) {
			if(job instanceof MS2FormatJob) {
				MS2FormatJob ms2formatjob = (MS2FormatJob) job;
				ms2formatjob.run();
			} else if (job instanceof DeleteJob) {
				DeleteJob deletejob = (DeleteJob) job;
				deletejob.execute();
			} else {
				// Set the job status to RUNNING and put the message in the queue
				msgQueue.add(new Message(job, JobStatus.RUNNING.toString(), new Date()));
				filenameMap.put(job.getDescription(), job.getFilename());
				log.info("Executing job: " + job.getDescription());
				
				// Worker thread, currently only intended for serial execution, thus not really necessary.
//				Worker worker = new Worker(job);
//				worker.run();
				
				job.execute();
			}
			// Error logging.
			if (job.getStatus() == JobStatus.ERROR) {
				log.error(job.getError());
			}		
			
			// Remove job from the queue after successful execution.
			jobQueue.remove(job);
		}
	}
	
//    /**
//     *  Worker thread.
//     */
//    private class Worker implements Runnable {
//    	
//        /**
//         * The runLock is acquired and released surrounding each task
//         * execution. Mainly protects against interrupts that are
//         * intended to cancel the worker thread from instead
//         * interrupting the task being run.
//         */
//        private final ReentrantLock runLock = new ReentrantLock();
//
//        /**
//         * Initial job to run before entering run loop
//         */
//        private Job firstJob;
//
//
//        public Worker(Job firstJob) {
//            this.firstJob = firstJob;
//        }
//
//         /**
//         * Run a single task between before/after methods.
//         */
//        private void runJob(Job job) {
//            final ReentrantLock runLock = this.runLock;
//            runLock.lock();
//            try {
//                Thread.interrupted(); // clear interrupt status on entry
//                try {
//                    job.execute();
//                } catch(RuntimeException ex) {
//                	ex.printStackTrace();
//                }
//            } finally {
//                runLock.unlock();
//            }
//        }
//
//        /**
//         * Main run loop.
//         */
//        public void run() {
//			Job job = firstJob;
//			firstJob = null;
//			while (job != null) {
//				runJob(job);
//				job = null; // unnecessary, but may help GC. :-)
//			}
//        }
//    }
    
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
