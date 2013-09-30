package de.mpa.db.storager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Basic storage functionality: Loading and storing of data.
 * 
 * @author Thilo Muth
 *
 */
public abstract class BasicStorager implements Storager {
	
	/**
	 * Logger object for the storagers.
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
    
	@Override
	public void run() {
		// FIXME: Refactor run-methods in all subclasses!
		this.load();
		try {
			this.store();
		} catch (Exception e) {
			log.error("Error message: " + e.getMessage());
			e.printStackTrace();
		}
		log.info("Data stored to the DB.");
	}

	@Override
	public void load() {		
	}

	@Override
	public void store() throws IOException, SQLException {
	}
	
}
