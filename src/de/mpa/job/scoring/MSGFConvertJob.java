package de.mpa.job.scoring;

import java.io.File;

import de.mpa.job.Job;

public class MSGFConvertJob extends Job {
	
	private File msgfExecutable;
	
	private String mzidFilePath;
	
	/**
	 * Constructor for converting MG-GF+'s output to human readable TSV file.
	 * @param mzidFilePath MS-GF+ output (*.mzid) file path.
	 * @param searchSettings Database search settings
	 */
	public MSGFConvertJob(String mzidFilePath) {
		this.mzidFilePath = mzidFilePath;
		this.msgfExecutable = new File(algorithmProperties.getProperty("path.msgf"));
		this.filename = mzidFilePath.substring(0, mzidFilePath.indexOf(".mzid")) + ".tsv";
		initJob();
	}
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		
        // Full path to executable
		procCommands.add("java");
		procCommands.add("-cp");
        procCommands.add(msgfExecutable.getAbsolutePath() + "/" + algorithmProperties.getProperty("app.msgf"));
        procCommands.add("edu.ucsd.msjava.ui.MzIDToTsv");
        
        // Input MGF file
        procCommands.add("-i");
        procCommands.add(mzidFilePath);
        
        procCommands.add("-o");
        procCommands.add(filename);

        procCommands.trimToSize();

        setDescription("MS-GF+ OUTPUT CONVERSION");
        procBuilder = new ProcessBuilder(procCommands);
        procBuilder.directory(msgfExecutable);
        
        // Set error out and std out to same stream
        procBuilder.redirectErrorStream(true);
	}
}
