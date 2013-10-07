package de.mpa.db.storager;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import de.mpa.client.model.dbsearch.SearchEngineType;

/**
 * Basic storage functionality: Loading and storing of data.
 * 
 * @author Thilo Muth
 *
 */
public abstract class BasicStorager implements Storager {
	
	/**
	 * Logger object for the storage classes.
	 */
	protected Logger log = Logger.getLogger(getClass());
	
	/**
	 * Connection instance.
	 */
	protected Connection conn;
	
    /**
     * The file instance.
     */
    protected File file;
    
    /**
     * The search engine type.
     */
    protected SearchEngineType searchEngineType;
    
	@Override
	public void run() {
		this.load();
		try {
			this.store();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error("Could not perform rollback. Error message: " + e.getMessage());
				e1.printStackTrace();
			}
			log.error(searchEngineType.name() + " storing error message: " + e.getMessage());
			e.printStackTrace();
		}
		log.info(searchEngineType.name() + " results stored to the DB.");
	}

	@Override
	public void load() {		
	}

	@Override
	public void store() throws Exception {
	}
	
}
