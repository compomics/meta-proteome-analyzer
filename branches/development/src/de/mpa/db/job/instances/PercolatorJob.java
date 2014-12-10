package de.mpa.db.job.instances;

import java.io.File;
import java.util.ArrayList;

import de.mpa.db.job.Job;

public class PercolatorJob extends Job{
	
	private final File cruxFile;
	private final String filename;
	 
	/**
	 * Constructor for the XTandemJob retrieving the MGF file as the only
	 * parameter.
	 * 
	 * @param mgfFile
	 */
	public PercolatorJob(File mgfFile) {
		this.cruxFile = new File(jobProperties.getProperty("path.crux"));
		filename = jobProperties.getProperty("path.crux.output") + mgfFile.getName().substring(0, mgfFile.getName().length() - 4) + "_percolated.txt";
		initJob();
	}	
	
	/**
	 * In this step Percolator is used to re-rank the obtained results + assign q-values to them.
	 */
	private void initJob() {
		setDescription("PERCOLATOR JOB");
		
		procCommands = new ArrayList<String>();
		// Link to the output file.
		procCommands.add(jobProperties.getProperty("path.crux") + jobProperties.getProperty("app.crux"));
		procCommands.add("percolator");
		
		// Link to crux sourcefolder path.		
		procCommands.add(jobProperties.getProperty("path.crux.output") + "search.target.txt");
		
		// Link to outputfolder path.
		procCommands.add("--output-dir");
		procCommands.add(jobProperties.getProperty("path.crux.output"));
		
		// Overwrite existing files (if any searches before)
		procCommands.add("--overwrite");
		procCommands.add("T");
		
		// FDR < 5%
		procCommands.add("--train-fdr");
		procCommands.add("0.05");
		
		procCommands.add("--test-fdr");
		procCommands.add("0.05");
		
		procCommands.trimToSize();
		log.info("PERCOLATOR" + procCommands);
		
		procBuilder = new ProcessBuilder(procCommands);
		procBuilder.directory(cruxFile);
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
	
	/**
	 * Returns the path to the crux percolated output file.
	 */
	public String getFilename(){
		return filename;
	}
}
