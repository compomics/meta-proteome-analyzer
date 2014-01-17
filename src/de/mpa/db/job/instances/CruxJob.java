package de.mpa.db.job.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.mpa.db.job.Job;


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
	private final static String PARAMETER_FILE = "default.params"; 
	
	/**
	 * Crux parameters.
	 */
	private String params;
	private int nMissedCleavages;
	private File parameterFile;
	private double fragmentTol;
	 
	/**
	 * Constructor for the CruxJob retrieving the MGF file as the only
	 * parameter.
	 * http://noble.gs.washington.edu/proj/crux/crux-search-for-matches.html
	 * 
	 * @param mgfFile
	 * @param isPrecIonTolPpm 
	 * @param nMissedCleavages
	 * @param precIonTol 
	 * @param nMissedCleavages 
	 */
	public CruxJob(File mgfFile, final String searchDB, String params, double fragmentTol, double precIonTol, int nMissedCleavages, boolean isPrecIonTolPpm ) {
		this.mgfFile = mgfFile;
		this.searchDB = searchDB;
		this.params = params;
		this.cruxFile = new File(JobConstants.CRUX_PATH);	
		this.fragmentTol = fragmentTol;
		this.precIonTol = precIonTol;
		this.nMissedCleavages = nMissedCleavages;
		this.isPrecIonTolPpm = isPrecIonTolPpm;
		buildParameterFile();
		initJob();
		filename = JobConstants.CRUX_OUTPUT_PATH + mgfFile.getName().substring(0, mgfFile.getName().length() - 4) + "_percolated.txt";
	}	
	
    /**
     * Constructs the parameters file.
     */
    private void buildParameterFile() {

        parameterFile = new File(cruxFile, PARAMETER_FILE);
        
        String precursorTolType = "mass";
        if(isPrecIonTolPpm) precursorTolType = "ppm";
        		
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(parameterFile));
            bw.write(params);
            
            bw.write("stats=false\n" +
            		"comparison=eq\n" + 
            		"min-weibull-points=4000" + 
            		"isotope=0\n" + 
            		"primary-ions=by\n" + 
            		"isotopic-mass=average\n" + 
            		"missed-cleavages=" + nMissedCleavages + "\n" +
            		"precursor-window-type=" + precursorTolType + "\n" + 
            		"precursor-window=" + precIonTol + "\n" +
            		"mz-bin-width=" + fragmentTol + "\n" + 
            		"C=57.021464\n" + 
            		"version=false\n" +
            		"digestion=full-digest\n" + 
            		"spectrum-parser=pwiz\n" + 
            		"parameter-file=__NULL_STR\n" + 
            		"max-rank-preliminary=500\n" + 
            		"max-mods=255\n" + 
            		"spectrum-charge=all\n" + 
            		"train-fdr=0.050000\n" + 
            		"pi-zero=1.000000\n" + 
            		"mz-bin-offset=0.680000\n" + 
            		"spectrum-min-mass=0.000000\n" + 
            		"spectrum-min-mass=0.000000\n" + 
            		"nmod=NO MODS\n" + 
            		"cmod=NO MODS\n"
            		);
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
		
		// Java commands
		procCommands.add(JobConstants.CRUX_PATH + JobConstants.CRUX_EXE);
		procCommands.add("search-for-matches");					
		
		// Link to spectrum file.
		procCommands.add(mgfFile.getAbsolutePath());
		
		// Link to database index directory
		procCommands.add(JobConstants.FASTA_PATH  + searchDB + "-index");
		
		// Parameter-file
		procCommands.add("--parameter-file");
		procCommands.add(parameterFile.getAbsolutePath());

		// Link to outputfolder path.
		procCommands.add("--output-dir");
		procCommands.add(JobConstants.CRUX_OUTPUT_PATH);
		
		// Overwrite existing files (if any searches before)
		procCommands.add("--overwrite");
		procCommands.add("T");	
		
		procCommands.trimToSize();

		procBuilder = new ProcessBuilder(procCommands);
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
	