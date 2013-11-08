package de.mpa.job.instances;

import java.io.File;

import de.mpa.job.Job;
import de.mpa.job.SearchType;

public class OmssaJob extends Job {
    
    // EValue of 10
    private final static double E_VALUE_CUTOFF = 1000;
    
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
	
	// Mass tolerance for fragment ions
    private double fragmentTol;
    
    // Mass tolerance for precursor ions
    private double precursorTol;
    
    // Maximum number of missed cleavages.
    private int nMissedCleavages;

    private String filename;
    
    private SearchType searchType;
    
    // String of precursor ion tolerance unit ( ppm versus Da)
    private boolean isPrecursorTolerancePpm;	
    
	/**
	 * Constructor for starting the OMSSA search engine.
	 * @param mgfFile Spectrum file
	 * @param searchDB Search database
	 * @param fragmentTol Fragment ion tolerance
	 * @param precursorTol Precursor ion tolerance
	 * @param nMissedCleavages Number of maximum missed cleavages
	 * @param isPrecursorTolerancePpm Condition whether the precursor tolerance unit is PPM (true) or Dalton (false)
	 * @param searchType Target or decoy search type
	 */
	public OmssaJob(File mgfFile, String searchDB, double fragmentTol, double precursorTol, int nMissedCleavages, boolean isPrecursorTolerancePpm, SearchType searchType) {
		this.mgfFile = mgfFile;
		this.searchDB = searchDB;
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
       if(isPrecursorTolerancePpm){
    	   procCommands.add("-teppm");        
       }

        // Minimum charge of multiple charged ions
        procCommands.add("-zt");
        procCommands.add(Integer.toString(MINIMUM_CHARGE_MULTIPLE_CHARGED));

        // Missed cleavages
        procCommands.add("-v");
       	procCommands.add(Integer.toString(nMissedCleavages));

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
        
        // Enzyme
        procCommands.add("-e");
        // 0 == Trypsin
        /*  rypsin (0), argc (1), cnbr (2), chymotrypsin (3), formicacid (4), lysc (5), lysc-p (6), pepsin-a (7),
        tryp-cnbr (8), tryp-chymo (9), trypsin-p (10), whole-protein (11), aspn (12), gluc (13), aspngluc (14),
        top-down (15), semi-tryptic (16), no-enzyme (17), chymotrypsin-p (18), aspn-de (19), gluc-de (20),
        lysn (21), thermolysin-p (22), semi-chymotrypsin (23), semi-gluc (24), max(25),*/
        procCommands.add("16");
        
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
