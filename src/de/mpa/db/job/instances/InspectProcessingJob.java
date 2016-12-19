package de.mpa.db.job.instances;

import java.io.File;
import java.util.ArrayList;

import de.mpa.db.job.Job;
import de.mpa.util.PropertyLoader;

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
		
		String pathInspect = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_INSPECT);
		String pathInspectOutputPValued = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_INSPECT_OUTPUT_PVALUED);
		
		this.inspectFile = new File(pathInspect);
		filename = pathInspectOutputPValued + mgfFile.getName() + ".out";
		initJob();
	}
	
	/**
	 * In this step the INSPECT post-processing is done. 
	 * Statistically insignificant results are weeded out by the python script. 
	 */
	private void initJob() {
		setDescription("POST-PROCESSING JOB");
		procCommands = new ArrayList<String>();
		
		String pathInspect = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_INSPECT);
		String pathInspectOutputPValued = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_INSPECT_OUTPUT_PVALUED);
		String pathInspectOutputRaw = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_INSPECT_OUTPUT_RAW);
		
		
		// Link to the output file.
		procCommands.add("python");
		procCommands.add(pathInspect + "PValue.py");
		procCommands.add("-r");
		procCommands.add(pathInspectOutputRaw);
		procCommands.add("-w");
		procCommands.add(pathInspectOutputPValued);
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
