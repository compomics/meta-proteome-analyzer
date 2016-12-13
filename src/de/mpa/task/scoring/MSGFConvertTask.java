package de.mpa.task.scoring;

import java.io.File;

import de.mpa.task.Task;

public class MSGFConvertTask extends Task {
	
	private File msgfExecutable;
	
	private String mzidFilePath;
	
	/**
	 * Constructor for converting MG-GF+'s output to human readable TSV file.
	 * @param mzidFilePath MS-GF+ output (*.mzid) file path.
	 * @param searchSettings Database search settings
	 */
	public MSGFConvertTask(String mzidFilePath) {
		this.mzidFilePath = mzidFilePath;
		this.msgfExecutable = new File(algorithmProperties.getProperty("path.msgf"));
		this.filename = mzidFilePath.substring(0, mzidFilePath.indexOf(".mzid")) + ".tsv";
		initJob();
	}
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob() {
		String maxHeapSize = "-Xmx3500m";
		
        // Full path to executable
		procCommands.add("java");
		procCommands.add(maxHeapSize);
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
