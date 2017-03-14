package de.mpa.db.job.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import de.mpa.db.job.Job;

public class PepnovoJob extends Job {	
	
	// Modifications
    private static final String MODS = "C+57:M+16";
	
	/**
	 * The pepnovo file.
	 */
	private final File pepnovoFile;
	
	/**
	 * The MGF file.
	 */
	private final File mgfFile;
	
	/**
	 * This variable holds the model, e.g. CID_IT_TRYP.
	 */
	private final String model;
    
	/**
	 * The precursor mass tolerance.
	 */
	private final double precursorTol;
	
    /**
     * The fragment ion tolerance.
     */
	private final double fragmentTol;
	
	/**
	 * Number of solutions, default = 20.
	 */
	private final int nSolutions;
	
    /**
     * The output file if specified.
     */
    protected File outputFile;
    
	/**
	 * Constructor for the XTandemJob retrieving the MGF file as the only
	 * parameter.
	 * 
	 * @param mgfFile
	 */
    
    /**
     * Constructor for the PepnovoJob, retrieving MGF file, model, precursor mass tolerance, 
     * fragment mass tolerance and number of suggestions as parameters.
     * @param mgfFile The MGF input file.
     * @param model The input de-novo model.
     * @param precursorTol The precursor mass tolerance.
     * @param fragmentTol The fragment mass tolerance.
     * @param nSolutions The number of maximum solutions.
     */
	public PepnovoJob(File mgfFile, String model, double precursorTol, double fragmentTol, int nSolutions) {
		this.mgfFile = mgfFile;
		this.model = model;
		this.precursorTol = precursorTol;
		this.fragmentTol = fragmentTol;
		this.nSolutions = nSolutions;
        pepnovoFile = new File(JobConstants.PEPNOVO_PATH);
        this.initJob();
	}

	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		// Full path to executable.
        this.procCommands.add(this.pepnovoFile.getAbsolutePath() + "/" + JobConstants.PEPNOVO_EXE);
        this.procCommands.trimToSize();
		
		// Link to the MGF file.
        this.procCommands.add("-file");
        this.procCommands.add(this.mgfFile.getAbsolutePath());
		
		// Generate blast queries
        this.procCommands.add("-msb_generate_query");
		
		// Blast query name
        this.procCommands.add("-msb_query_name");
        this.procCommands.add(JobConstants.PEPNOVO_OUTPUT_PATH + this.mgfFile.getName());
		
		// Model name.
        this.procCommands.add("-model");
        this.procCommands.add(this.model);
		
		// Add modifications
        this.procCommands.add("-PTMs");
        this.procCommands.add(PepnovoJob.MODS);
		
		// Fragment mass tolerance (each model has a default setting).
        this.procCommands.add("-fragment_tolerance");
        this.procCommands.add(String.valueOf(this.fragmentTol));
		
		// Precursor mass tolerance (each model has a default setting).
        this.procCommands.add("-pm_tolerance");
        this.procCommands.add(String.valueOf(this.precursorTol));
		
		// Number of possible solutions, default == 20
        this.procCommands.add("-num_solutions");
        this.procCommands.add(String.valueOf(this.nSolutions));
		
		// Add output path.
        this.outputFile = new File(JobConstants.PEPNOVO_OUTPUT_PATH + this.mgfFile.getName() + ".out");
        this.procCommands.trimToSize();
		
		// Set the description for the job.
        this.setDescription("PEPNOVO");
        this.procBuilder = new ProcessBuilder(this.procCommands);
        this.procBuilder.directory(this.pepnovoFile);
		
		// Set error out and std out to same stream
        this.procBuilder.redirectErrorStream(true);
	}
	
	/**
	 * This method executes a job: 
	 * Overriding the run method is necessary due to the file writer ability!
	 */
	public void run() {
        this.proc = null;
		try {
            this.proc = this.procBuilder.start();
		} catch (IOException ioe) {
            Job.log.error(ioe.getMessage());
			ioe.printStackTrace();
		}

		// Retrieve inputstream from process
		Scanner scan = new Scanner(this.proc.getInputStream());
		scan.useDelimiter(System.getProperty("line.separator"));
		
		// Temporary string variable
		String temp;
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(this.outputFile));
			// Get input from scanner and send to stdout
			while (scan.hasNext()) {
				temp = scan.next();		
				System.out.println(temp);
				writer.write(temp);	
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		scan.close();

		try {
            this.proc.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			if (this.proc != null) {
                this.proc.destroy();
			}
		}
	}
	
	/**
	 * Returns the name of the pepnovo output file.
	 * @return
	 */
	public String getFilename(){
		return this.outputFile.getAbsolutePath();
	}
}

