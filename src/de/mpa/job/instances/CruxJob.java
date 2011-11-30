package de.mpa.job.instances;

import java.io.File;

import de.mpa.job.Job;


/**
 * The job for execution of the CRUX search engine.
 * @author Thilo Muth
 *
 */
public class CruxJob extends Job{	
	
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
	public CruxJob(File mgfFile, final String searchDB) {
		this.searchDB = searchDB;
		this.cruxFile = new File(JobConstants.CRUX_PATH);	
		this.ms2File = JobConstants.DATASET_PATH + mgfFile.getName().substring(0, mgfFile.getName().length() - 4) + "_format.ms2";;
		initJob();
		filename = JobConstants.CRUX_OUTPUT_PATH + mgfFile.getName().substring(0, mgfFile.getName().length() - 4) + "_percolated.txt";
	}	

	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		
		// Java commands
		procCommands.add(JobConstants.CRUX_PATH + JobConstants.CRUX_EXE);
		procCommands.add("search-for-matches");					
		
		// Link to spectrum file.
		procCommands.add(ms2File);
		
		// Link to database index directory
		procCommands.add(JobConstants.FASTA_PATH  + searchDB + "-index");
		
		// Link to outputfolder path.
		procCommands.add("--output-dir");
		procCommands.add(JobConstants.CRUX_OUTPUT_PATH);
		
		// Overwrite existing files (if any searches before)
		procCommands.add("--overwrite");
		procCommands.add("T");	
		
		procCommands.trimToSize();

		procBuilder = new ProcessBuilder(procCommands);
		setDescription("CRUX JOB");
		
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
	