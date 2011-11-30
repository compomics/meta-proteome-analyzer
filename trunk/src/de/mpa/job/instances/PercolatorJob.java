package de.mpa.job.instances;

import java.io.File;
import java.util.ArrayList;

import de.mpa.job.Job;

public class PercolatorJob extends Job{
	
	private final File cruxFile;
	private String ms2File;	
	private final String searchDB;
	private final String filename;
	 
	/**
	 * Constructor for the XTandemJob retrieving the MGF file as the only
	 * parameter.
	 * 
	 * @param mgfFile
	 */
	public PercolatorJob(File mgfFile, final String searchDB) {
		this.searchDB = searchDB;
		this.cruxFile = new File(JobConstants.CRUX_PATH);
		filename = JobConstants.CRUX_OUTPUT_PATH + mgfFile.getName().substring(0, mgfFile.getName().length() - 4) + "_percolated.txt";
		initJob();
	}	
	
	/**
	 * In this step Percolator is used to re-rank the obtained results + assign q-values to them.
	 */
	private void initJob() {
		setDescription("PERCOLATOR JOB");
		
		procCommands = new ArrayList<String>();
		// Link to the output file.
		procCommands.add(JobConstants.CRUX_PATH + JobConstants.CRUX_EXE);
		procCommands.add("percolator");
		
		// Database index directory
		procCommands.add(JobConstants.FASTA_PATH  + searchDB + "-index");		
		
		// Link to crux sourcefolder path.		
		procCommands.add(JobConstants.CRUX_OUTPUT_PATH);
		
		// Link to outputfolder path.
		procCommands.add("--output-dir");
		procCommands.add(JobConstants.CRUX_OUTPUT_PATH);
		
		// Overwrite existing files (if any searches before)
		procCommands.add("--overwrite");
		procCommands.add("T");
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
