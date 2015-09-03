package de.mpa.job;

import java.io.File;
import java.util.List;

import de.mpa.client.DbSearchSettings;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.job.instances.DeleteJob;
import de.mpa.job.instances.OmssaJob;
import de.mpa.job.instances.ParseJob;
import de.mpa.job.instances.SpectraJob;
import de.mpa.job.instances.UniProtJob;
import de.mpa.job.instances.XTandemJob;
import de.mpa.job.scoring.OmssaScoreJob;
import de.mpa.job.scoring.XTandemScoreJob;

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
		JobManager jobManager = JobManager.getInstance();
		
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
			
			// Parse the results.
			ParseJob xTandemParseJob = new ParseJob(SearchEngineType.XTANDEM, xtandemTargetJob.getFilename(), xTandemScoreJob.getFilename());
			jobManager.addJob(xTandemParseJob);
			jobManager.addJob(new DeleteJob(xtandemTargetJob.getFilename()));
		}
		
		// OMSSA job
		if (searchSettings.isOmssa()) {
			searchSettings.setSearchType(SearchType.TARGET);
			Job omssaTargetJob = new OmssaJob(mgfFile, searchSettings);
			jobManager.addJob(omssaTargetJob);
			
			searchSettings.setSearchType(SearchType.DECOY);
			Job omssaDecoyJob = new OmssaJob(mgfFile, searchSettings);
			jobManager.addJob(omssaDecoyJob);
			
			// The score job evaluates OMSSA target + decoy results
			Job omssaScoreJob = new OmssaScoreJob(omssaTargetJob.getFilename(), omssaDecoyJob.getFilename());
			jobManager.addJob(omssaScoreJob);
			
			// Parse the results.
			ParseJob omssaParseJob = new ParseJob(SearchEngineType.OMSSA, omssaTargetJob.getFilename(), omssaScoreJob.getFilename());
			jobManager.addJob(omssaParseJob);
			jobManager.addJob(new DeleteJob(omssaParseJob.getFilename()));
		}
		jobManager.addJob(new UniProtJob());
	}

}
