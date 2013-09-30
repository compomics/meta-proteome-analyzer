package de.mpa.job.instances;

import java.sql.SQLException;

import de.mpa.db.DBManager;
import de.mpa.job.Job;

public class UniProtJob extends Job {
	
	
	/**
	 * The database manager object.
	 */
	private DBManager dbManager;
	
	/**
	 * Constructs a job for the UniProt querying and storing.
	 */
	public UniProtJob() {
		try {
			this.dbManager = DBManager.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Set the description
		setDescription("UNIPROT RESULTS STORING");
	}
	
	@Override
	public void run() {
		try {
			dbManager.queryAndStoreUniprotEntries();
		} catch (Exception e) {
			setError(e);
		}
	}

}
