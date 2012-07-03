package de.mpa.webservice;

public class RunOptions {
	
	
	
	private int runCount = 0;
	
	private static final int runLimit = 1;
	
	private static RunOptions instance;
	
	public static RunOptions getInstance(){
		if(instance == null){
			instance = new RunOptions();
		}
		return instance;
	}
	
	private RunOptions() {
		
	}
	
	public boolean hasRunAlready(){
		return runCount == runLimit;
	}
	
	public int getRunCount() {
		return runCount;
	}

	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

	public int getRunLimit() {
		return runLimit;
	}
	
	
}
