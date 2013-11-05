package de.mpa.job.instances;

import java.io.File;

import de.mpa.job.Job;


/**
 * The job for execution of the CRUX search engine.
 * @author Thilo Muth
 *
 */
public class CruxJob extends Job {	
	
	private final File cruxFile;
	private final File mgfFile;	
	private final String searchDB;
	private final String filename;
	private final double precIonTol;
	private final boolean isPrecIonTolPpm;
	 
	/**
	 * Constructor for the CruxJob retrieving the MGF file as the only
	 * parameter.
	 * http://noble.gs.washington.edu/proj/crux/crux-search-for-matches.html
	 * 
	 * @param mgfFile
	 * @param isPrecIonTolPpm 
	 * @param precIonTol 
	 */
	public CruxJob(File mgfFile, final String searchDB, double precIonTol, boolean isPrecIonTolPpm ) {
		this.searchDB = searchDB;
		this.mgfFile = mgfFile;
		this.cruxFile = new File(JobConstants.CRUX_PATH);	
		this.precIonTol = precIonTol;
		this.isPrecIonTolPpm = isPrecIonTolPpm;
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
		procCommands.add(mgfFile.getAbsolutePath());
		
		// Link to database index directory
		procCommands.add(JobConstants.FASTA_PATH  + searchDB + "-index");
		
		// Parameter-file
		procCommands.add("--parameter-file");
		procCommands.add(JobConstants.CRUX_PATH + "default.params");
		// Add Precursor tolerance TODO Check this override of parameters
//		procCommands.add("precursor-window=" + precIonTol);
//		if (isPrecIonTolPpm) {
//			procCommands.add("precursor-window-type=ppm"); 
//		} else {
//			procCommands.add("precursor-window-type=mass");
//		}
		// Link to outputfolder path.
		procCommands.add("--output-dir");
		procCommands.add(JobConstants.CRUX_OUTPUT_PATH);
		
		// Overwrite existing files (if any searches before)
		procCommands.add("--overwrite");
		procCommands.add("T");	
		
		procCommands.trimToSize();

		procBuilder = new ProcessBuilder(procCommands);
		System.out.println(procCommands);
		setDescription("CRUX");
		
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
	