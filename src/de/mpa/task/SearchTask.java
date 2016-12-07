package de.mpa.task;

import java.io.File;

import de.mpa.client.DbSearchSettings;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.task.instances.CometTask;
import de.mpa.task.instances.CreateDatabaseTask;
import de.mpa.task.instances.DeleteTask;
import de.mpa.task.instances.MSGFTask;
import de.mpa.task.instances.ParseTask;
import de.mpa.task.instances.UniProtTask;
import de.mpa.task.instances.XTandemTask;
import de.mpa.task.scoring.CometScoreTask;
import de.mpa.task.scoring.MSGFConvertTask;
import de.mpa.task.scoring.XTandemScoreTask;

/**
 * This class handles the search task.
 * 
 * @author T. Muth
 *
 */
public class SearchTask {
    
	/**
	 * Single MGF file.
	 */
	private File mgfFile;
	
	/**
	 * Database search settings.
	 */
	private DbSearchSettings searchSettings;
	
    /**
     * Runs the searches in one task.
     * @param mgfFile
     * @param dbSearchSettings
     */
	public SearchTask(File mgfFile, DbSearchSettings dbSearchSettings) {
		this.mgfFile = mgfFile;
		this.searchSettings = dbSearchSettings;
		init();
	}
	
	/**
	 * Initializes the task.
	 */
	private void init() {
		TaskManager jobManager = TaskManager.getInstance();
		
		/*
		 *  OPTIONAL (aka. FIRST) SEARCH ROUND
		 */
		// Ask whether iterative searching is being used.
		if (searchSettings.useIterativeSearch()) {
			searchSettings.setSearchType(SearchType.FIRSTROUND);
			
			// First search job.
			Task firstSearchJob = new XTandemTask(mgfFile, searchSettings);
			jobManager.addJob(firstSearchJob);
			
			// Parse the results and create new protein database.
			ParseTask xTandemParseJob = new ParseTask(SearchEngineType.FIRSTROUND, firstSearchJob.getFilename());
			jobManager.addJob(xTandemParseJob);
			jobManager.addJob(new DeleteTask(firstSearchJob.getFilename()));
			
			// Create a new (reduced) protein FASTA database.
			
			CreateDatabaseTask createDbJob = new CreateDatabaseTask(searchSettings);
			jobManager.addJob(createDbJob);
			jobManager.run();
			
			// Assign newly created (reduced) FASTA database file to the search settings --> used in the second search!
			searchSettings.setFastaFilePath(createDbJob.getFilename());
		}
		
		/*
		 * OBLIGATORY (aka. SECOND) SEARCH ROUND
		 */
		// X!Tandem search setup
		if (searchSettings.useXTandem()) {
			searchSettings.setSearchType(SearchType.TARGET);
			Task xtandemTargetJob = new XTandemTask(mgfFile, searchSettings);
			jobManager.addJob(xtandemTargetJob);
			
			searchSettings.setSearchType(SearchType.DECOY);
			Task xtandemDecoyJob = new XTandemTask(mgfFile, searchSettings);
			jobManager.addJob(xtandemDecoyJob);
			
			// The score job evaluates X!Tandem target + decoy results
			Task xTandemScoreJob = new XTandemScoreTask(xtandemTargetJob.getFilename(), xtandemDecoyJob.getFilename());
			jobManager.addJob(xTandemScoreJob);
			
			// Parse the results.
			ParseTask xTandemParseJob = new ParseTask(SearchEngineType.XTANDEM, xtandemTargetJob.getFilename());
			jobManager.addJob(xTandemParseJob);
			jobManager.addJob(new DeleteTask(xtandemTargetJob.getFilename()));
		}
		
		// Comet search setup
		if (searchSettings.useComet()) {
			searchSettings.setSearchType(SearchType.TARGET_DECOY);
			Task cometJob = new CometTask(mgfFile, searchSettings);
			jobManager.addJob(cometJob);
			
			// The score job evaluates Comet target + decoy results
			Task cometScoreJob = new CometScoreTask(cometJob.getFilename(), ((CometTask)cometJob).getDecoyFilename());
			jobManager.addJob(cometScoreJob);
			
			// Parse the results.
			ParseTask cometParseJob = new ParseTask(SearchEngineType.COMET, cometJob.getFilename());
			jobManager.addJob(cometParseJob);
			jobManager.addJob(new DeleteTask(cometParseJob.getFilename()));
		}
		
		// MS-GF+ search setup
		if (searchSettings.useMSGF()) {
			searchSettings.setSearchType(SearchType.TARGET_DECOY);
			Task msgfJob = new MSGFTask(mgfFile, searchSettings);
			jobManager.addJob(msgfJob);
			
			// This job converts the final results of MG-GF+ from MZID to TSV format.
			Task msgfConvertJob = new MSGFConvertTask(msgfJob.getFilename());
			jobManager.addJob(msgfConvertJob);
			
			// Parse the results.
			ParseTask msgfParseJob = new ParseTask(SearchEngineType.MSGF, msgfConvertJob.getFilename());
			jobManager.addJob(msgfParseJob);
			jobManager.addJob(new DeleteTask(msgfParseJob.getFilename()));
		}
		jobManager.addJob(new UniProtTask());
	}
}
