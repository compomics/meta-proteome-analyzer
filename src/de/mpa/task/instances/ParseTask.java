package de.mpa.task.instances;

import java.io.File;

import org.apache.log4j.Logger;

import de.mpa.analysis.TargetDecoyAnalysis;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.GenericContainer;
import de.mpa.io.parser.comet.CometParser;
import de.mpa.io.parser.msgf.MSGFParser;
import de.mpa.io.parser.xtandem.XTandemParser;
import de.mpa.io.parser.xtandem.XTandemProteinParser;
import de.mpa.task.Task;

public class ParseTask extends Task {
	
	/**
	 * Search engine type.
	 */
	private SearchEngineType searchEngineType;

	/**
	 * The search engine result filename.
	 */
	private String resultFilename;

	/**
	 * Constructs an results parsing job.
	 * @param searchEngineType The search engine type.
	 * @param resultFilename The results filename.
	 */
	public ParseTask(SearchEngineType searchEngineType, String resultFilename) {
		this.resultFilename = resultFilename;
		this.searchEngineType = searchEngineType;
		// Set the description
		setFilename(resultFilename);
	}
	
	@Override
	public void run() {
		setDescription(searchEngineType.toString().toUpperCase() + " RESULTS PARSING");
		client.firePropertyChange("new message", null, this.getDescription());
		log = Logger.getLogger(getClass());
		TargetDecoyAnalysis targetDecoyAnalysis = GenericContainer.currentTDA;
		
		GenericContainer parser = null;
		if (searchEngineType == SearchEngineType.XTANDEM && targetDecoyAnalysis != null) {
			parser = new XTandemParser(new File(resultFilename), targetDecoyAnalysis);
		} else if (searchEngineType == SearchEngineType.COMET && targetDecoyAnalysis != null) {
			parser = new CometParser(new File(resultFilename), targetDecoyAnalysis);
		} else if (searchEngineType == SearchEngineType.MSGF) {
			parser = new MSGFParser(new File(resultFilename));
		} else if (searchEngineType == SearchEngineType.FIRSTROUND) {
			parser = new XTandemProteinParser(new File(resultFilename));
		} 
		parser.parse();
		log.info("Number of " + searchEngineType.toString() + " hits parsed: " + parser.getNumberOfHits());
	}
}