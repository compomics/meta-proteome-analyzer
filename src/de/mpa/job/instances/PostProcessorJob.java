package de.mpa.job.instances;

import java.io.File;
import java.util.ArrayList;

import de.mpa.job.Job;

public class PostProcessorJob extends Job {
	
	private File inspectFile;
	private String filename;

	/**
	 * Constructor for the InspectJob.
	 * 
	 * @param mgfFile
	 * @param searchDB
	 * @param decoy
	 */
	public PostProcessorJob(File mgfFile, String searchDB) {
		this.inspectFile = new File(JobConstants.INSPECT_PATH);
		filename = JobConstants.INSPECT_PVALUED_OUTPUT_PATH + mgfFile.getName() + ".out";
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
		//procCommands.add("sudo");
		procCommands.add("python");
		procCommands.add(JobConstants.INSPECT_PATH + "PValue.py");
		procCommands.add("-r");
		procCommands.add(JobConstants.INSPECT_RAW_OUTPUT_PATH);
		procCommands.add("-w");
		procCommands.add(JobConstants.INSPECT_PVALUED_OUTPUT_PATH);
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
