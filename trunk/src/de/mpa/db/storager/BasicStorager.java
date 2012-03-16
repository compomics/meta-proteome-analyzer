package de.mpa.db.storager;

import java.io.IOException;
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
	
	@Override
	public void run() {
		this.load();
		try {
			this.store();
		} catch (IOException ioe) {
			log.error("Error message: " + ioe.getMessage());
			ioe.printStackTrace();
		} catch (SQLException e) {
			log.error("Error message: " + e.getMessage() + " - Error code: " + e.getErrorCode());
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
