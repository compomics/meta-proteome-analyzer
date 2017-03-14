package de.mpa.db.job.instances;

import java.io.File;
import java.util.ArrayList;

import de.mpa.db.job.Job;
import de.mpa.util.PropertyLoader;

public class InspectProcessingJob extends Job {
	
	private final File inspectFile;
	private final String filename;
	
	/**
	 * Constructor for the InspectProcessingJob.
	 * 
	 * @param mgfFile Spectrum file
	 * @param searchDB
	 */
	public InspectProcessingJob(File mgfFile) {
		
		String pathInspect = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_INSPECT);
		String pathInspectOutputPValued = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_INSPECT_OUTPUT_PVALUED);

        inspectFile = new File(pathInspect);
        this.filename = pathInspectOutputPValued + mgfFile.getName() + ".out";
        this.initJob();
	}
	
	/**
	 * In this step the INSPECT post-processing is done. 
	 * Statistically insignificant results are weeded out by the python script. 
	 */
	private void initJob() {
        this.setDescription("POST-PROCESSING JOB");
        this.procCommands = new ArrayList<String>();
		
		String pathInspect = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_INSPECT);
		String pathInspectOutputPValued = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_INSPECT_OUTPUT_PVALUED);
		String pathInspectOutputRaw = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_INSPECT_OUTPUT_RAW);
		
		
		// Link to the output file.
        this.procCommands.add("python");
        this.procCommands.add(pathInspect + "PValue.py");
        this.procCommands.add("-r");
        this.procCommands.add(pathInspectOutputRaw);
        this.procCommands.add("-w");
        this.procCommands.add(pathInspectOutputPValued);
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
