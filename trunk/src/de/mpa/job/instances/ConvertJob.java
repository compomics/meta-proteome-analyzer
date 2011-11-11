package de.mpa.job.instances;

import java.io.File;
import de.mpa.job.Job;


/**
 * This class is used for converting MGF files into the MS2 format.
 * @author Thilo Muth
 *
 */
public class ConvertJob extends Job {
	
	private final static String MSCONVERT_PATH = "/scratch/metaprot/software/crux/tide/";
	private final static String JOB_EXE =  "msconvert";


	/** 
	 * The msConvertFile.
	 */
	final private File msConvertFile;

	/** 
	 * The mgf file.
	 */
	private File mgfFile;
	
	/**
	 * Constructor for the ConvertJob retrieving the MGF file as the only parameter.
	 * 
	 * @param mgfFile
	 */
	public ConvertJob(File mgfFile) {
		this.mgfFile = mgfFile;		
		this.msConvertFile = new File(MSCONVERT_PATH);
		initJob();
		super.execute();		
	}
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		// Full path to executable.
		procCommands.add(msConvertFile.getAbsolutePath() + "/" + JOB_EXE);
		
		// Link output dir.
		procCommands.add("-o");
		procCommands.add(JobConstants.DATASET_PATH);
		
		// Link to the mgf input file.
		procCommands.add("--ms2");
		procCommands.add(mgfFile.getAbsolutePath());
		
		procCommands.trimToSize();
		
		log.info(procCommands);
		procBuilder = new ProcessBuilder(procCommands);

		procBuilder.directory(msConvertFile);
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}

	/**
	 * Returns the path to the converted MS2 file.
	 */
	public String getMs2Filename(){
		return mgfFile.getAbsolutePath().substring(0, mgfFile.getAbsolutePath().indexOf(".mgf")) + ".ms2";		
	}
}

