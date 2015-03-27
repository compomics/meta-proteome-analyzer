package de.mpa.job.instances;

import java.io.File;

import org.apache.log4j.Logger;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.io.GenericContainer;
import de.mpa.io.parser.omssa.OmssaParser;
import de.mpa.io.parser.xtandem.XTandemParser;
import de.mpa.job.Job;

public class ParseJob extends Job {
	
	/**
	 * The QVality results filename.
	 */
	private String qValueFilename;
	
	/**
	 * Search engine type.
	 */
	private SearchEngineType searchEngineType;

	/**
	 * The search engine result filename.
	 */
	private String resultFilename;

	/**
	 * Constructs an results storing job (without q-value file).
	 * @param searchEngineType
	 * @param resultsFileName
	 */
	public ParseJob(SearchEngineType searchEngineType, String resultsFileName) {
		this(searchEngineType, resultsFileName, null);
	}
	
	/**
	 * Constructs an results parsing job.
	 * @param searchEngineType The search engine type.
	 * @param resultFilename The results filename.
	 * @param qValueFilename The q-value results filename.
	 */
	public ParseJob(SearchEngineType searchEngineType, String resultFilename, String qValueFilename) {
		this.resultFilename = resultFilename;
		this.qValueFilename = qValueFilename;
		this.searchEngineType = searchEngineType;
		// Set the description
		setFilename(resultFilename);
	}
	
	@Override
	public void run() {
		setDescription(searchEngineType.name().toUpperCase() + " RESULTS PARSING");
		client.firePropertyChange("new message", null, this.getDescription());
		log = Logger.getLogger(getClass());
		String targetScoreFilename = qValueFilename.substring(0, qValueFilename.lastIndexOf("_qvalued")) + "_target.out";;
		GenericContainer parser = null;
		if (searchEngineType == SearchEngineType.XTANDEM && qValueFilename != null) {
			parser = new XTandemParser(new File(resultFilename), new File(targetScoreFilename), new File(qValueFilename));
		} else if (searchEngineType == SearchEngineType.OMSSA && qValueFilename != null) {
			parser = new OmssaParser(new File(resultFilename), new File(targetScoreFilename), new File(qValueFilename));
		}
		parser.parse();
		log.info("Number of " + searchEngineType.name() + " hits parsed: " + parser.getNumberOfHits());
	}
}