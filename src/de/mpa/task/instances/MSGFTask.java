package de.mpa.task.instances;

import java.io.File;

import de.mpa.client.DbSearchSettings;
import de.mpa.task.Task;

public class MSGFTask extends Task {
	
	/**
	 * Search parameters string.
	 */
	private String params;	
    
	/**
	 * Filename string.
	 */
    private String filename;
    
    /**
     * MS-GF+ executable.
     */
    private File msgfExecutable;
    
    /**
     * MGF file instance.
     */
	private File mgfFile;
	
	/**
	 * FASTA search database.
	 */
	private String searchDB;
	
    // Mass tolerance for precursor ions
    private double precursorTol;
    
    // String of precursor ion tolerance unit (ppm versus Da)
    private boolean isPrecursorTolerancePpm;
    
	/**
	 * Constructor for starting the MS-GF+ search engine.
	 * @param mgfFile Spectrum file
	 * @param searchSettings Database search settings
	 */
	public MSGFTask(File mgfFile, DbSearchSettings searchSettings) {
		this.mgfFile = mgfFile;
		this.searchDB = searchSettings.getFastaFilePath();
		this.params = searchSettings.getMsgfParams();
		this.precursorTol = searchSettings.getPrecIonTol();
		this.isPrecursorTolerancePpm = searchSettings.isPrecIonTolPpm();
		this.filename =  algorithmProperties.getProperty("path.msgf.output") + mgfFile.getName() + ".mzid";
		this.msgfExecutable = new File(algorithmProperties.getProperty("path.msgf"));
		initJob();
	}
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob(){
		
		String maxHeapSize = "-Xmx3500m";
		  
        // Full path to executable
        procCommands.add("java");
        procCommands.add("-jar");
        procCommands.add(maxHeapSize);
        procCommands.add("-XX:+UseConcMarkSweepGC");
        procCommands.add(msgfExecutable.getAbsolutePath() + File.separator + algorithmProperties.getProperty("app.msgf"));
        
        // Input MGF file
        procCommands.add("-s");
        procCommands.add(mgfFile.getAbsolutePath());
        
        // Database
        procCommands.add("-d");
        procCommands.add(searchDB);
        
        // Precursor Ion Tolerance 
        procCommands.add("-t");
        
        if(isPrecursorTolerancePpm){
     	   procCommands.add(Double.toString(precursorTol) + "ppm");        
        } else {
        	procCommands.add(Double.toString(precursorTol) + "Da");
        }
        
        // Default: target-decoy search
        procCommands.add("-tda");
        procCommands.add("1");
        
       	//Adding all external parameters.
       	String[] split = params.split("\\s");
       	for (String string : split) {
       		if (string.contains("(")) {
       			string = string.substring(0, string.indexOf("(") - 1);
       		}
       		procCommands.add(string);
		}
        
        // Output file
        procCommands.add("-o");
        procCommands.add(filename);
        procCommands.trimToSize();
        
        setDescription("MS-GF+ TARGET-DECOY SEARCH");
        procBuilder = new ProcessBuilder(procCommands);
        procBuilder.directory(msgfExecutable);
        
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
