package de.mpa.db.job.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.mpa.db.job.Job;

/**
 * This job executes an Inspect database search.
 * http://proteomics.ucsd.edu/InspectDocs/Searching.html
 * @author Thilo Muth
 *
 */
public class InspectJob extends Job {

	
	/* Protease - nonstandard digests are penalized:Options are trypsin, chymotrypsin, lysc, aspn, gluc */

	
	private File inspectFile;
	private File inputFile;
	private File mgfFile;
	private String searchDB;
	private String params;
	private final double precIonTol;
	private final double fragIonTol;
	private final boolean isPrecIonTolPpm;
	

	/**
	 * Constructor for the InspectJob.
	 * 
	 * @param mgfFile Spectrum file
	 * @param searchDB Search database
	 * @param params Search parameters string
	 * @param fragmentTol Fragment ion tolerance
	 * @param precursorTol Precursor ion tolerance
	 */
	public InspectJob(File mgfFile, String searchDB, String params, double precIonTol, boolean isPrecIonTolPpm, double fragIonTol) {
		this.mgfFile = mgfFile;
		this.searchDB = searchDB + ".RS.trie";
		this.params = params;
		this.inspectFile = new File(JobConstants.INSPECT_PATH);
		this.precIonTol = precIonTol;
		this.isPrecIonTolPpm = isPrecIonTolPpm;
		this.fragIonTol = fragIonTol;
		buildInputFile();
		initJob();
	}

	/**
	 * Constructs the input.xml file needed for the Inspect process. 
	 */
	private void buildInputFile(){		
	        inputFile = new File(inspectFile, JobConstants.INSPECT_INPUT_FILE);
	        try {
	            BufferedWriter bw = new BufferedWriter(new FileWriter(inputFile));
	            
	            String precursorIonTol;
	        	if (isPrecIonTolPpm) {
//	        		precursorIonTol = "PMTolerance,"+ Double.toString(precIonTol);
	        		precursorIonTol = "PMTolerance," + Double.toString(0.1);
	        	}else {
	        		precursorIonTol = "ParentPPM," + Integer.toString((int) precIonTol);
	        	}
	            bw.write("spectra," + mgfFile.getAbsolutePath() + "\n"
		                    + "db," + JobConstants.FASTA_PATH + searchDB + "\n" + 
		                    precursorIonTol + "\n" + 
		                    "IonTolerance," + Double.toString(fragIonTol) + "\n" + 
		                    params); 
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

		setDescription("INSPECT");
		procBuilder = new ProcessBuilder(procCommands);

		procBuilder.directory(inspectFile);
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
}
