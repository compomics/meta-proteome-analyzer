package de.mpa.job.instances;

import java.io.File;

import de.mpa.job.Job;
import de.mpa.job.SearchType;

public class OmssaJob extends Job {
    
    // EValue of 10
    private final static double E_VALUE_CUTOFF = 1000; 
    
    // Number of missed cleavage allowed
    private final static int MISSED_CLEAVAGES = 2;
    
    // Hit list length for each
    private final static int HITLIST_LENGTH = 25;
    
    // Minimum charge to start looking for multiply charged ions
    private final static int MINIMUM_CHARGE_MULTIPLE_CHARGED = 2;
    
    // Fixed modifications 
    // ID 3 == Carbamidomethyl of C
    private final static String FIXED_MODS = "3";
    
    // Fixed modifications
    // ID 1 == Oxidation of M; 
    private final static String VAR_MODS = "1";
    
    private File omssaFile;
	private File mgfFile;
	private String searchDB;
	
	// Mass tolerance for fragement
    private double fragmentTol;
    
    // Mass tolerance for precursor
    private double precursorTol;
    

    private String filename;
    
    private SearchType searchType;
    
    // String of precursor ion tolerance unit ( ppm versus Da)
    private boolean isPrecursorIonTolPpm;	
	/**
	 * Constructor for the Ommsa retrieving the MGF file as the only parameter.
	 * http://proteomicsresource.washington.edu/omssa.php
	 * @param mgfFile
	 */
	public OmssaJob(File mgfFile, String searchDB, double fragmentTol, double precursorTol, boolean isPrecursorIonTolPpm, SearchType searchType) {
		this.mgfFile = mgfFile;
		this.searchDB = searchDB;
		this.fragmentTol = fragmentTol;
		this.precursorTol = precursorTol;
		this.isPrecursorIonTolPpm = isPrecursorIonTolPpm;
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

        // full path to executable
        procCommands.add(omssaFile.getAbsolutePath() + "/" + JobConstants.OMSSA_EXE);
        
        // always ask for spectra included in results file
        procCommands.add("-w");

        // FragmentTolerance
        //http://pubchem.ncbi.nlm.nih.gov/omssa/run.htm
        procCommands.add("-to");
        procCommands.add(Double.toString(fragmentTol));
        
        // PrecursorTolerance 
        procCommands.add("-te");
        procCommands.add(Double.toString(precursorTol));
       
       // Add flag if unit of precursors is in PPM:
       if(isPrecursorIonTolPpm){
    	   procCommands.add("-teppm");        
       }

        // Minimum charge of multiple charged ions
        procCommands.add("-zt");
        procCommands.add(Integer.toString(MINIMUM_CHARGE_MULTIPLE_CHARGED));

        // Missed cleavages
        procCommands.add("-v");
       	procCommands.add(Integer.toString(MISSED_CLEAVAGES));

       	// HitList number        
        procCommands.add("-hl");
        procCommands.add(Integer.toString(HITLIST_LENGTH));

        // eValue
        procCommands.add("-he");
        procCommands.add(Double.toString(E_VALUE_CUTOFF));   

        // look for monoisotopic peaks
        procCommands.add("-tom");
        procCommands.add("0");
        procCommands.add("-tem");
        procCommands.add("0");
        // look for b and y ions
        procCommands.add("-i");
        procCommands.add(1 + "," + 4);

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
        
        // set error out and std out to same stream
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
