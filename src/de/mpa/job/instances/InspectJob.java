package de.mpa.job.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.mpa.job.Job;

/**
 * This job executes an Inspect database search.
 * @author Thilo Muth
 *
 */
public class InspectJob extends Job {
	
	// PARAMETERS
	// Instrument type (QTOF or ESI-ION-TRAP)
	private final static String INSTRUMENT = "ESI-ION-TRAP";
	
	/* Protease - nonstandard digests are penalized:Options are trypsin, chymotrypsin, lysc, aspn, gluc */
	private final static String PROTEASE = "trypsin";
	
	//Modifications: mod,mass,residues,fix/opt,name
	private final static String MODS = "mod,+57,C,fix" + "\n" + 
									   "mod,+16,M,opt" + "\n";
	
	// Blindsearch --> Modification per peptide
	private final static String MODS_BLIND = "mods,1";
	
	private File inspectFile;
	private File inputFile;
	private File mgfFile;
	private String searchDB;
	private String filename;

	/**
	 * Constructor for the InspectJob.
	 * 
	 * @param mgfFile
	 * @param searchDB
	 * @param decoy
	 */
	public InspectJob(File mgfFile, String searchDB) {
		this.mgfFile = mgfFile;
		this.searchDB = searchDB + ".RS.trie";
		this.inspectFile = new File(JobConstants.INSPECT_PATH);
		filename = JobConstants.INSPECT_PVALUED_OUTPUT_PATH + mgfFile.getName() + ".out";
		buildInputFile();
		initJob();
		super.execute();
		postProcess();
	}

	
	/**
	 * In this step the postprocessing is down. 
	 * Statistically insignificant results are weeded out by the python script. 
	 */
	private void postProcess() {
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
		log.info("\n" + procCommands);
		procBuilder = new ProcessBuilder(procCommands);
		procBuilder.directory(inspectFile);
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
		super.execute();
	}
	
	/**
	 * Constructs the input.xml file needed for the Inspect process. 
	 */
	private void buildInputFile(){		
	        inputFile = new File(inspectFile, JobConstants.INSPECT_INPUT_FILE);
	        try {
	            BufferedWriter bw = new BufferedWriter(new FileWriter(inputFile));
	            bw.write("spectra," + mgfFile.getAbsolutePath() + "\n"
		                    + "instrument," + INSTRUMENT + "\n"
		                    + "protease," + PROTEASE + "\n"	                    
		                    + "db," + JobConstants.FASTA_PATH + searchDB + "\n"
		                    + MODS_BLIND + "\n"
		                    + MODS);
	            bw.flush();
	            bw.close();
	        } catch (IOException ioe) {
	           ioe.printStackTrace();
	        }
	    }
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		// Full path to executable.
		procCommands.add(inspectFile.getAbsolutePath() + "/" + JobConstants.INSPECT_EXE);
		
		// Link to the input file.
		procCommands.add("-i");
		procCommands.add(inputFile.getAbsolutePath());
			
		// Link to the output file.
		procCommands.add("-o");
		procCommands.add(JobConstants.INSPECT_RAW_OUTPUT_PATH + mgfFile.getName() + ".out");
		
		procCommands.trimToSize();

		log.info("====== INSPECT JOB started ======");
		log.info(procCommands);
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
