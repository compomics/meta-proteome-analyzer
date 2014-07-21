package de.mpa.db.job.instances;

import java.io.File;
import java.util.ArrayList;

import de.mpa.db.job.Job;

public class InspectProcessingJob extends Job {
	
	private File inspectFile;
	private String filename;
	
	/**
	 * Constructor for the InspectProcessingJob.
	 * 
	 * @param mgfFile Spectrum file
	 * @param searchDB
	 */
	public InspectProcessingJob(File mgfFile) {
		this.inspectFile = new File(jobProperties.getProperty("path.inspect"));
		filename = jobProperties.getProperty("path.inspect.output.pvalued") + mgfFile.getName() + ".out";
		initJob();
	}
	
	/**
	 * In this step the INSPECT post-processing is done. 
	 * Statistically insignificant results are weeded out by the python script. 
	 */
	private void initJob() {
		setDescription("POST-PROCESSING JOB");
		procCommands = new ArrayList<String>();
		// Link to the output file.
		procCommands.add("python");
		procCommands.add(jobProperties.getProperty("path.inspect") + "PValue.py");
		procCommands.add("-r");
		procCommands.add(jobProperties.getProperty("path.inspect.output.raw"));
		procCommands.add("-w");
		procCommands.add(jobProperties.getProperty("path.inspect.output.pvalued"));
		procCommands.add("-S");
		procCommands.add("0.5");
		procCommands.trimToSize();
		procBuilder = new ProcessBuilder(procCommands);
		procBuilder.directory(inspectFile);
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
	
	/**
	 * Returns the path to the Inspect PValued output file.
	 */
	public String getFilename(){
		return filename;
	}
}
