package de.mpa.job;

import java.io.File;
import java.util.List;

import de.mpa.client.DbSearchSettings;
import de.mpa.job.instances.XTandemJob;
import de.mpa.job.scoring.XTandemScoreJob;

public class SearchTask {
    
	private List<File> mgfFiles;
	private DbSearchSettings searchSettings;
	private File outputFolder;
	private File parametersFile;
    
    /**
     * 
     * @param mgfFiles
     * @param dbSearchSettings
     * @param outputFolder
     * @param parametersFile
     */
	public SearchTask(List<File> mgfFiles, DbSearchSettings dbSearchSettings, File outputFolder, File parametersFile) {
		this.mgfFiles = mgfFiles;
		this.searchSettings = dbSearchSettings;
		this.outputFolder = outputFolder;
		this.parametersFile = parametersFile;
		init();
	}
	
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void init() {		
		JobManager jobManager = JobManager.getInstance();
		// Iterate the MGF files.
		for (File mgfFile : mgfFiles) {
			// X!Tandem job
			if (searchSettings.isXTandem()) {
				searchSettings.setSearchType(SearchType.TARGET);
				Job xtandemTargetJob = new XTandemJob(mgfFile, searchSettings);
				jobManager.addJob(xtandemTargetJob);
				
				searchSettings.setSearchType(SearchType.DECOY);
				Job xtandemDecoyJob = new XTandemJob(mgfFile, searchSettings);
				jobManager.addJob(xtandemDecoyJob);
				
				// The score job evaluates X!Tandem target + decoy results
				Job xTandemScoreJob = new XTandemScoreJob(xtandemTargetJob.getFilename(), xtandemDecoyJob.getFilename());
				jobManager.addJob(xTandemScoreJob);
			}
		}
		
		System.out.println("Run the searches...");
		jobManager.run();
		
		
	}

}
