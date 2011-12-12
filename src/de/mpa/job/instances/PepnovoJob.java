package de.mpa.job.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import de.mpa.job.Job;

public class PepnovoJob extends Job {	
	
	// PARAMETERS
	// Model to choose
	private final static String MODEL = "CID_IT_TRYP";
	
	// Modifications
	private final static String MODS = "C+57:M+16";
	
	/**
	 * The pepnovo file.
	 */
	private File pepnovoFile;
	
	/**
	 * The MGF file.
	 */
	private File mgfFile;
    
    /**
     * The fragment ion tolerance.
     */
	private double fragmentTol;    
	
	/**
	 * Number of suggestions.
	 */
	private int numSuggestions;
	
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
	public PepnovoJob(File mgfFile, double fragmentTol, int numSuggestions) {
		this.mgfFile = mgfFile;
		this.fragmentTol = fragmentTol;
		this.numSuggestions = numSuggestions;
		this.pepnovoFile = new File(JobConstants.PEPNOVO_PATH);		
		initJob();
		run();
	}

	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		// full path to executable
		procCommands.add(pepnovoFile.getAbsolutePath() + "/" + JobConstants.PEPNOVO_EXE);
		procCommands.trimToSize();
		
		// Link to the MGF file
		procCommands.add("-file");
		procCommands.add(mgfFile.getAbsolutePath());
		
		// Add Model
		procCommands.add("-model");
		procCommands.add(MODEL);
		
		// Add modifications
		procCommands.add("-PTMs");
		procCommands.add(MODS);
		
		// Add fragment tolerance
		procCommands.add("-fragment_tolerance");
		procCommands.add(String.valueOf(fragmentTol));
		
		// Add solution number
		procCommands.add("-num_solutions");
		procCommands.add(String.valueOf(numSuggestions));
		
		// Peptide intensity threshold
		procCommands.add("-num_solutions");
		procCommands.add(String.valueOf(numSuggestions));
		
		// Add output path
		outputFile = new File(JobConstants.PEPNOVO_OUTPUT_PATH + mgfFile.getName() + ".out");
		procCommands.trimToSize();
		
		setDescription("PEPNOVO");
		procBuilder = new ProcessBuilder(procCommands);
		
		procBuilder.directory(pepnovoFile);
		  
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
	
	/**
	 * Executes a job.
	 */
	public void run() {
		proc = null;
		try {
			proc = procBuilder.start();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		}

		// Retrieve inputstream from process
		Scanner scan = new Scanner(proc.getInputStream());
		scan.useDelimiter(System.getProperty("line.separator"));
		
		// Temporary string variable
		String temp;
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(outputFile));
			// Get input from scanner and send to stdout
			while (scan.hasNext()) {
				temp = scan.next();								
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
			proc.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			if (proc != null) {
				proc.destroy();
			}
		}
	}
	
	/**
	 * Returns the name of the pepnovo output file.
	 * @return
	 */
	public String getFilename(){
		return outputFile.getAbsolutePath();		
	}
}

