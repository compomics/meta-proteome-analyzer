package de.mpa.db.job.instances;

import java.io.File;
import java.util.ArrayList;

import de.mpa.db.job.Job;

public class PostProcessorJob extends Job {
	
	private final File inspectFile;
	private final String filename;

	/**
	 * Constructor for the InspectJob.
	 * 
	 * @param mgfFile
	 * @param searchDB
	 * @param decoy
	 */
	public PostProcessorJob(File mgfFile, String searchDB) {
        inspectFile = new File(JobConstants.INSPECT_PATH);
        this.filename = JobConstants.INSPECT_PVALUED_OUTPUT_PATH + mgfFile.getName() + ".out";
        this.initJob();
	}
	
	/**
	 * In this step the INSPECT post-processing is done. 
	 * Statistically insignificant results are weeded out by the python script. 
	 */
	private void initJob() {
        this.setDescription("POST-PROCESSING JOB");
        this.procCommands = new ArrayList<String>();
		// Link to the output file.
		//procCommands.add("sudo");
        this.procCommands.add("python");
        this.procCommands.add(JobConstants.INSPECT_PATH + "PValue.py");
        this.procCommands.add("-r");
        this.procCommands.add(JobConstants.INSPECT_RAW_OUTPUT_PATH);
        this.procCommands.add("-w");
        this.procCommands.add(JobConstants.INSPECT_PVALUED_OUTPUT_PATH);
        this.procCommands.add("-S");
        this.procCommands.add("0.5");
        this.procCommands.trimToSize();
        this.procBuilder = new ProcessBuilder(this.procCommands);
        this.procBuilder.directory(this.inspectFile);
		// set error out and std out to same stream
        this.procBuilder.redirectErrorStream(true);
	}
	
	/**
	 * Returns the path to the Inspect PValued output file.
	 */
	public String getFilename(){
		return this.filename;
	}
}
