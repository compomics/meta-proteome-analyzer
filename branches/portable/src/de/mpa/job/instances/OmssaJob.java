package de.mpa.job.instances;

import java.io.File;

import de.mpa.client.DbSearchSettings;
import de.mpa.job.Job;
import de.mpa.job.SearchType;

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
    
    /**
     * OMSSA file instance.
     */
    private File omssaFile;
    
    /**
     * MGF file instance.
     */
	private File mgfFile;
	
	/**
	 * Search
	 */
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
	 * @param searchSettings Search settings
	 */
	public OmssaJob(File mgfFile, DbSearchSettings searchSettings) {
		this.mgfFile = mgfFile;
		this.searchDB = searchSettings.getFastaFile();
		this.params = searchSettings.getOmssaParams();
		this.fragmentTol = searchSettings.getFragIonTol();
		this.precursorTol = searchSettings.getPrecIonTol();
		this.nMissedCleavages = searchSettings.getMissedCleavages();
		this.isPrecursorTolerancePpm = searchSettings.isPrecIonTolPpm();
		this.searchType = searchSettings.getSearchType();
		String basePath = algorithmProperties.getProperty("path.base");
		if (searchType == SearchType.DECOY) {
			this.filename = basePath + algorithmProperties.getProperty("path.omssa.output") + mgfFile.getName() + "_decoy.omx";
			searchDB = searchDB.substring(0, searchDB.indexOf(".fasta")) + "_decoy.fasta";
		} else {
			this.filename = basePath + algorithmProperties.getProperty("path.omssa.output") + mgfFile.getName() + "_target.omx";
		}
		this.omssaFile = new File(algorithmProperties.getProperty("path.omssa"));
		initJob();
	}
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob(){

        // Full path to executable
        procCommands.add(omssaFile.getAbsolutePath() + "/" + algorithmProperties.getProperty("app.omssa"));
        
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
        procCommands.add(searchDB);
        
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
