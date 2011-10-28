package de.mpa.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.storager.PepnovoStorager;
import de.mpa.db.storager.ProjectStorager;
import de.mpa.db.storager.SpectrumStorager;

/**
 * This class serves for handling and managing the database.
 * 
 * @author Thilo Muth
 *
 */
public class DBManager {
	
	// Database connection
    private Connection conn;
    
    // Separate spectrum storage thread
	private Thread spectraThread;
	
	// Thread pool handler
	private ExecutorService executor;
    
	/**
	 * Constructor for the database manager.
	 * @throws SQLException
	 */
    public DBManager() throws SQLException {
		init();
	}
    
    /**
     * Initialize the database manager.
     * @throws SQLException
     */
	private void init() throws SQLException{		
		// The database configuration.
		DBConfiguration dbconfig = new DBConfiguration("metaprot");
		conn = dbconfig.getConnection();
		
		// Set auto commit == FALSE --> Manual commit & rollback.
		conn.setAutoCommit(false);
		
		// Start the cached thread pool.
		executor = Executors.newCachedThreadPool();
    }
	
	/**
	 * This method stores the spectra contents and files to the DB.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public SpectrumStorager storeSpectra(File mgfFile, String title, String taxon, double fragmentTol, double precursorTol, String precursorUnit) throws IOException, SQLException {
		// Check for duplicate file in the DB!
		//TODO: Spectrum.checkDuplicateFile(mgfFile.getName(), conn);
		
		// The Project storager is initialized here.
		ProjectStorager projectStorager = new ProjectStorager(conn, title, taxon, fragmentTol, precursorTol, precursorUnit);
		Thread thread = new Thread(projectStorager);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		
		// Returns the project id.
		long projectid = projectStorager.getProjectid();		
		
		// Store the spectra		
		SpectrumStorager specStorager = new SpectrumStorager(conn, mgfFile, projectid);
		spectraThread = new Thread(specStorager);
		spectraThread.start();
		
		try {
			spectraThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		return specStorager;
	}
	
	/**
	 * This methods stores the Pepnovo results to the DB.
	 * @param pepnovoFilename
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storePepnovoResults(String pepnovoFilename) throws IOException, SQLException{
		try {
			spectraThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.execute(new PepnovoStorager(conn, new File(pepnovoFilename)));
	}
	
	/**
	 * Returns the connection.
	 * @return
	 */
	public Connection getConnection() {
		return conn;
	}
}
