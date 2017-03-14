package de.mpa.db.job.instances;

import java.sql.SQLException;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.db.DBManager;
import de.mpa.db.job.Job;

public class StoreJob extends Job {
	
	/**
	 * The database manager object.
	 */
	private DBManager dbManager;
	
	/**
	 * The QVality results filename.
	 */
	private final String qValueFilename;
	
	/**
	 * Search engine type.
	 */
	private final SearchEngineType searchEngineType;

	/**
	 * The search engine result filename.
	 */
	private final String resultFilename;

	
	/**
	 * Constructs an results storing job (without q-value file).
	 * @param searchEngineType
	 * @param resultsFileName
	 */
	public StoreJob(SearchEngineType searchEngineType, String resultsFileName) {
		this(searchEngineType, resultsFileName, null);
	}
	
	/**
	 * Constructs an results storing job.
	 * @param searchEngineType The search engine type.
	 * @param resultFilename The results filename.
	 * @param qValueFilename The q-value results filename.
	 */
	public StoreJob(SearchEngineType searchEngineType, String resultFilename, String qValueFilename) {
		try {
            dbManager = DBManager.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.resultFilename = resultFilename;
		this.qValueFilename = qValueFilename;
		this.searchEngineType = searchEngineType;
		// Set the description
        this.setFilename(resultFilename);
        this.setDescription(searchEngineType.name().toUpperCase() + " RESULTS STORING");
	}
	
	@Override
	public void run() {
		try {
            this.dbManager.storeDatabaseSearchResults(this.searchEngineType, this.resultFilename, this.qValueFilename);
		} catch (Exception e) {
            this.setError(e);
		}
	}

}
