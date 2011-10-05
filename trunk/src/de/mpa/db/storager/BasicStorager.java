package de.mpa.db.storager;

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
	
	private Connection conn;
	private Logger log = Logger.getLogger(getClass());
	
	@Override
	public void run() {
		this.load();
		try {
			this.store();
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
