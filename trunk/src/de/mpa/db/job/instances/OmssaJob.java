package de.mpa.db.job.instances;

import java.io.File;

import de.mpa.db.job.Job;
import de.mpa.db.job.SearchType;

public class OmssaJob extends Job {
    
	/**
	 * Search parameters string.
	 */
	private String params;	
    
	/**
	 * Filename.
	 */
    private String filename;
    
    /**
     * Search type: Target or decoy.
     */
    private SearchType searchType;
    
    // Fixed modifications 
    // ID 3 == Carbamidomethyl of C
    private final static String FIXED_MODS = "3";
    
    // Fixed modifications
    // ID 1 == Oxidation of M; 
    private final static String VAR_MODS = "1";
    
    private File omssaFile;
	private File mgfFile;
	private String searchDB;
	
	// Mass tolerance for fragment ions
    private double fragmentTol;
    
    // Mass tolerance for precursor ions
    private double precursorTol;
    
    // Maximum number of missed cleavages.
    private int nMissedCleavages;
    
    // String of precursor ion tolerance unit ( ppm versus Da)
    private boolean isPrecursorTolerancePpm;

    
	/**
	 * Constructor for starting the OMSSA search engine.
	 * @param mgfFile Spectrum file
	 * @param searchDB Search database
	 * @param params Search parameters
	 * @param fragmentTol Fragment ion tolerance
	 * @param precursorTol Precursor ion tolerance
	 * @param nMissedCleavages Number of maximum missed cleavages
	 * @param isPrecursorTolerancePpm Condition whether the precursor tolerance unit is PPM (true) or Dalton (false)
	 * @param searchType Target or decoy search type
	 */
	public OmssaJob(File mgfFile, String searchDB, String params, double fragmentTol, double precursorTol, int nMissedCleavages, boolean isPrecursorTolerancePpm, SearchType searchType) {
		this.mgfFile = mgfFile;
		this.searchDB = searchDB;
		this.params = params;
		this.fragmentTol = fragmentTol;
		this.precursorTol = precursorTol;
		this.nMissedCleavages = nMissedCleavages;
		this.isPrecursorTolerancePpm = isPrecursorTolerancePpm;
		this.searchType = searchType;
		
		if(searchType == SearchType.DECOY){
			this.filename = JobConstants.OMSSA_OUTPUT_PATH + mgfFile.getName() + "_decoy.omx";
		} else {
			this.filename = JobConstants.OMSSA_OUTPUT_PATH + mgfFile.getName() + "_target.omx";
		}
		this.omssaFile = new File(JobConstants.OMSSA_PATH);
		initJob();
	}
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob(){

        // Full path to executable
        procCommands.add(omssaFile.getAbsolutePath() + "/" + JobConstants.OMSSA_EXE);
        
        // Always ask for spectra included in results file
        procCommands.add("-w");

        // Fragment Ion Tolerance
        procCommands.add("-to");
        procCommands.add(Double.toString(fragmentTol));
        
        // Precursor Ion Tolerance 
        procCommands.add("-te");
        procCommands.add(Double.toString(precursorTol));
       
       // Add flag if unit of precursors is in PPM:
       if(isPrecursorTolerancePpm){
    	   procCommands.add("-teppm");        
       }

        // Missed cleavages
        procCommands.add("-v");
       	procCommands.add(Integer.toString(nMissedCleavages));
       	
       	// Adding all external parameters.
       	String[] split = params.split("\\s");
       	for (String string : split) {
       		procCommands.add(string);
		}

        // Fixed modifications
        procCommands.add("-mf");
        procCommands.add(FIXED_MODS);
       
        // Variable modifications
        procCommands.add("-mv");
        procCommands.add(VAR_MODS);
        
        // Database
        procCommands.add("-d");
        procCommands.add(JobConstants.FASTA_PATH + searchDB + ".fasta");
        
        // Input MGF file
        procCommands.add("-fm");
        procCommands.add(mgfFile.getAbsolutePath());
        
        // Output file
        procCommands.add("-ox");
        procCommands.add(filename);
        procCommands.trimToSize();

        setDescription("OMSSA " + searchType.name() + " SEARCH");
        procBuilder = new ProcessBuilder(procCommands);
        procBuilder.directory(omssaFile);
        
        
        // Set error out and std out to same stream
        procBuilder.redirectErrorStream(true);
	}
	
	/**
	 * Returns the absolute path to the Omssa output file.
	 * @return
	 */
	public String getFilename(){
		return filename;		
	}
}
