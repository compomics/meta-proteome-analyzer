package de.mpa.job.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import de.mpa.job.Job;

public class PepnovoJob extends Job {
	
	private final static String DATASET_PATH_WIN = "C:/workspace/MetaProteomeAnalyzer/";
	private final static String OUTPUT_PATH_WIN = "C:/metaproteomics/output/";
	private final static String PEPNOVO_PATH_WIN = "C:/metaproteomics/pepnovo/";
	
	// Model to choose
	private final static String MODEL = "CID_IT_TRYP";
	// Modifications
	private final static String MODS = "C+57:M+16";
	private final static String SOLUTION_NUMBER = "10";
	private File pepnovoFile;
	private File mgfFile;
    /**
     * The output file if specified.
     */
    protected File outputFile;
	private double fragmentTol;    
    
	/**
	 * Constructor for the XTandemJob retrieving the MGF file as the only
	 * parameter.
	 * 
	 * @param mgfFile
	 */
	public PepnovoJob(File mgfFile, double fragmentTol) {
		this.mgfFile = mgfFile;
		this.fragmentTol = fragmentTol;
		this.pepnovoFile = new File(PEPNOVO_PATH_WIN);		
		initJob();
		execute();
	}

	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		// full path to executable
		procCommands.add(pepnovoFile.getAbsolutePath() + "/" + "PepNovo.exe");
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
		procCommands.add(SOLUTION_NUMBER);
		
		// Add output path
		outputFile = new File(OUTPUT_PATH_WIN + mgfFile.getName() + ".out");
		procCommands.trimToSize();

		log.info("====== PEPNOVO JOB started ======");
		log.info(procCommands);
		procBuilder = new ProcessBuilder(procCommands);
		
		procBuilder.directory(pepnovoFile);
		  
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
	
	/**
	 * Executes a job.
	 */
	public void execute() {
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

