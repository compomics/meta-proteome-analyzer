package de.mpa.job.instances;

import java.io.File;

import de.mpa.job.Job;

public class PepnovoJob extends Job {	
	
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
	 * This variable holds the model, e.g. CID_IT_TRYP.
	 */
	private String model;
    
	/**
	 * The precursor mass tolerance.
	 */
	private double precursorTol;
	
    /**
     * The fragment ion tolerance.
     */
	private double fragmentTol;    
	
	/**
	 * Number of solutions, default = 20.
	 */
	private int nSolutions;
	
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
		this.pepnovoFile = new File(JobConstants.PEPNOVO_PATH);		
		initJob();
	}

	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		// Full path to executable.
		procCommands.add(pepnovoFile.getAbsolutePath() + "/" + JobConstants.PEPNOVO_EXE);
		procCommands.trimToSize();
		
		// Link to the MGF file.
		procCommands.add("-file");
		procCommands.add(mgfFile.getAbsolutePath());
		
		// Model name.
		procCommands.add("-model");
		procCommands.add(model);
		
		// Add modifications
		procCommands.add("-PTMs");
		procCommands.add(MODS);
		
		// Fragment mass tolerance (each model has a default setting).
		procCommands.add("-fragment_tolerance");
		procCommands.add(String.valueOf(fragmentTol));
		
		// Precursor mass tolerance (each model has a default setting).
		procCommands.add("-pm_tolerance");
		procCommands.add(String.valueOf(precursorTol));
		
		// Number of possible solutions, default == 20
		procCommands.add("-num_solutions");
		procCommands.add(String.valueOf(nSolutions));
		
		// Add output path.
		outputFile = new File(JobConstants.PEPNOVO_OUTPUT_PATH + mgfFile.getName() + ".out");
		procCommands.trimToSize();
		
		// Set the description for the job.
		setDescription("PEPNOVO");
		procBuilder = new ProcessBuilder(procCommands);
		procBuilder.directory(pepnovoFile);
		  
		// Set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
	
//	/**
//	 * Executes a job.
//	 */
//	public void execute() {
//		proc = null;
//		try {
//			proc = procBuilder.start();
//		} catch (IOException ioe) {
//			System.out.println(ioe.getMessage());
//			ioe.printStackTrace();
//		}
//
//		// Retrieve inputstream from process
//		Scanner scan = new Scanner(proc.getInputStream());
//		scan.useDelimiter(System.getProperty("line.separator"));
//		
//		// Temporary string variable
//		String temp;
//		
//		BufferedWriter writer = null;
//		try {
//			writer = new BufferedWriter(new FileWriter(outputFile));
//			// Get input from scanner and send to stdout
//			while (scan.hasNext()) {
//				temp = scan.next();								
//				writer.write(temp);	
//				writer.newLine();
//			}
//			writer.flush();
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		scan.close();
//
//		try {
//			proc.waitFor();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			if (proc != null) {
//				proc.destroy();
//			}
//		}
//	}
	
	/**
	 * Returns the name of the pepnovo output file.
	 * @return
	 */
	public String getFilename(){
		return outputFile.getAbsolutePath();		
	}
}

