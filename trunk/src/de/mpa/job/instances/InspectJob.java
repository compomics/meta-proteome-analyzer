package de.mpa.job.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.mpa.job.Job;

/**
 * This job executes an Inspect database search.
 * http://proteomics.ucsd.edu/InspectDocs/Searching.html
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
	private final double precIonTol;
	private final double fragIonTol;
	private final boolean isPrecIonTolPpm;

	/**
	 * Constructor for the InspectJob.
	 * 
	 * @param mgfFile
	 * @param searchDB
	 * @param isPrecIonTolPpm 
	 * @param precIonTol 
	 * @param fragIonTol 
	 * @param decoy
	 */
	public InspectJob(File mgfFile, String searchDB, double precIonTol, boolean isPrecIonTolPpm, double fragIonTol) {
		this.mgfFile = mgfFile;
		this.searchDB = searchDB + ".RS.trie";
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
		
		// Set precursor and fragment tolerance
//		if (isPrecIonTolPpm) {
//			procCommands.add("PMTolerance,"+ precIonTol);
//		}else {
//			procCommands.add("ParentPPM," + precIonTol);
//		}
//		procCommands.add("IonTolerance," + fragIonTol );
		
		procCommands.trimToSize();

		setDescription("INSPECT");
		procBuilder = new ProcessBuilder(procCommands);

		procBuilder.directory(inspectFile);
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
}
