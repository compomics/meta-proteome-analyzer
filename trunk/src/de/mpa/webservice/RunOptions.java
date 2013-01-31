package de.mpa.webservice;

/**
 * This class describes the servers run options.
 * @author T. Muth
 *
 */
public class RunOptions {
	
	/**
	 * Number of runs.
	 */
	private int runCount = 0;
	
	/**
	 * Run limit set to 1
	 */
	private static final int runLimit = 1;
	
	/**
	 * Instance of the RunOptions.
	 */
	private static RunOptions instance;
	
	/**
	 * Returns instance of the RunOptions.
	 * @return RunOptions instance
	 */
	public static RunOptions getInstance(){
		if(instance == null){
			instance = new RunOptions();
		}
		return instance;
	}
	
	/**
	 * Private constructor: Access via instance only.
	 */
	private RunOptions() {}
	
	/**
	 * Has the server already been running ?
	 * @return Boolean flag if the server has already been running.
	 */
	public boolean hasRunAlready(){
		return runCount == runLimit;
	}
	
	/**
	 * Returns the run count.
	 * @return
	 */
	public int getRunCount() {
		return runCount;
	}
	
	/**
	 * Sets the run count.
	 * @param runCount Number of runs.
	 */
	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}
	
	/**
	 * Returns the run limit.
	 * @return Run limit.
	 */
	public int getRunLimit() {
		return runLimit;
	}
	
	
}
