package de.mpa.db.mysql.job.instances;

import java.io.File;

import de.mpa.db.mysql.job.Job;
import de.mpa.db.mysql.job.SearchType;
import de.mpa.util.PropertyLoader;

public class OmssaJob extends Job {
    
	/**
	 * Search parameters string.
	 */
	private final String params;
    
	/**
	 * Filename.
	 */
    private final String filename;
    
    /**
     * Search type: Target or decoy.
     */
    private final SearchType searchType;
    
    // Fixed modifications 
    // ID 3 == Carbamidomethyl of C
    private static final String FIXED_MODS = "3";
    
    // Fixed modifications
    // ID 1 == Oxidation of M; 
    private static final String VAR_MODS = "1";
    
    private final File omssaFile;
	private final File mgfFile;
	private final String searchDB;
	
	// Mass tolerance for fragment ions
    private final double fragmentTol;
    
    // Mass tolerance for precursor ions
    private final double precursorTol;
    
    // Maximum number of missed cleavages.
    private final int nMissedCleavages;
    
    // String of precursor ion tolerance unit ( ppm versus Da)
    private final boolean isPrecursorTolerancePpm;

    
	/**
	 * Constructor for starting the OMSSA search engine.
	 * 
	 * @param mgfFile
	 *            Spectrum file
	 * @param searchDB
	 *            Search database
	 * @param params
	 *            Search parameters
	 * @param fragmentTol
	 *            Fragment ion tolerance
	 * @param precursorTol
	 *            Precursor ion tolerance
	 * @param nMissedCleavages
	 *            Number of maximum missed cleavages
	 * @param isPrecursorTolerancePpm
	 *            Condition whether the precursor tolerance unit is PPM (true)
	 *            or Dalton (false)
	 * @param searchType
	 *            Target or decoy search type
	 */
	public OmssaJob(File mgfFile, String searchDB, String params, double fragmentTol, double precursorTol,
			int nMissedCleavages, boolean isPrecursorTolerancePpm, SearchType searchType) {
		this.mgfFile = mgfFile;
		this.searchDB = searchDB;
		this.params = params;
		this.fragmentTol = fragmentTol;
		this.precursorTol = precursorTol;
		this.nMissedCleavages = nMissedCleavages;
		this.isPrecursorTolerancePpm = isPrecursorTolerancePpm;
		this.searchType = searchType;

		String pathOmssa = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_OMSSA);
		String pathOmssaOutput = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_OMSSA_OUTPUT);

		if (searchType == SearchType.DECOY) {
            filename = pathOmssaOutput + mgfFile.getName() + "_decoy.omx";
		} else {
            filename = pathOmssaOutput + mgfFile.getName() + "_target.omx";
		}
        omssaFile = new File(pathOmssa);
        this.initJob();
	}

	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {

		String appOmssa = this.omssaFile.getAbsolutePath() + File.separator + PropertyLoader.getProperty(PropertyLoader.APP_OMSSA);

		// Full path to executable
        this.procCommands.add(appOmssa);

		// Always ask for spectra included in results file
        this.procCommands.add("-w");

		// Fragment Ion Tolerance
        this.procCommands.add("-to");
        this.procCommands.add(Double.toString(this.fragmentTol));

		// Precursor Ion Tolerance
        this.procCommands.add("-te");
        this.procCommands.add(Double.toString(this.precursorTol));

		// Add flag if unit of precursors is in PPM:
		if (this.isPrecursorTolerancePpm) {
            this.procCommands.add("-teppm");
		}

		// Missed cleavages
        this.procCommands.add("-v");
        this.procCommands.add(Integer.toString(this.nMissedCleavages));

		// Adding all external parameters.
		String[] split = this.params.split("\\s");
		for (String string : split) {
            this.procCommands.add(string);
		}

		// Fixed modifications
        this.procCommands.add("-mf");
        this.procCommands.add(OmssaJob.FIXED_MODS);

		// Variable modifications
        this.procCommands.add("-mv");
        this.procCommands.add(OmssaJob.VAR_MODS);

		// Database
        this.procCommands.add("-d");
		String pathFasta = PropertyLoader.getProperty(PropertyLoader.BASE_PATH)
				+ PropertyLoader.getProperty(PropertyLoader.PATH_FASTA);
        this.procCommands.add(pathFasta + this.searchDB + ".fasta");

		// Input MGF file
        this.procCommands.add("-fm");
        this.procCommands.add(this.mgfFile.getAbsolutePath());

		// Output file
        this.procCommands.add("-ox");
        this.procCommands.add(this.filename);
        this.procCommands.trimToSize();

        this.setDescription("OMSSA " + this.searchType.name() + " SEARCH");
        this.procBuilder = new ProcessBuilder(this.procCommands);
        this.procBuilder.directory(this.omssaFile);

		// Set error out and std out to same stream
        this.procBuilder.redirectErrorStream(true);

	}

	/**
	 * Returns the absolute path to the Omssa output file.
	 * 
	 * @return
	 */
	public String getFilename() {
		return this.filename;
	}
}
