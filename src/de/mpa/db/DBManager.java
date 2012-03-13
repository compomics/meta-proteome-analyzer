package de.mpa.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.mpa.db.storager.CruxStorager;
import de.mpa.db.storager.InspectStorager;
import de.mpa.db.storager.OmssaStorager;
import de.mpa.db.storager.PepnovoStorager;
import de.mpa.db.storager.SpectrumStorager;
import de.mpa.db.storager.XTandemStorager;

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
		DBConfiguration dbconfig = new DBConfiguration("metaprot", ConnectionType.LOCAL, new DbConnectionSettings());
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
	public SpectrumStorager storeSpectra(File mgfFile) throws IOException, SQLException {
		// TODO: The Project storager is initialized here.
//		ProjectStorager projectStorager = new ProjectStorager(conn, title);
//		Thread thread = new Thread(projectStorager);
//		thread.start();
//		try {
//			thread.join();
//		} catch (InterruptedException e) {			
//			e.printStackTrace();
//		}
//		
//		// Returns the project id.
//		long projectid = projectStorager.getProjectid();		
		
		// Store the spectra	
		SpectrumStorager specStorager = new SpectrumStorager(conn, mgfFile);
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
	 * This methods stores the XTandem results to the DB.
	 * @param xtandemFilename
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storeXTandemResults(String xtandemFilename, String xtandemQValued) throws IOException, SQLException{
		try {
			spectraThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(xtandemQValued != null){
			executor.execute(new XTandemStorager(conn, new File(xtandemFilename), new File(xtandemQValued)));
		} else {
			executor.execute(new XTandemStorager(conn, new File(xtandemFilename)));
		}
		
	}
	
	/**
	 * This methods stores the Omssa results to the DB.
	 * @param omssaFilename
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storeOmssaResults(String omssaFilename, String omssaQValued) throws IOException, SQLException{
		try {
			spectraThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(omssaQValued != null){
			executor.execute(new OmssaStorager(conn, new File(omssaFilename), new File(omssaQValued)));
		} else {
			executor.execute(new OmssaStorager(conn, new File(omssaFilename)));
		}
	}
	
	/**
	 * This methods stores the Crux results to the DB.
	 * @param cruxFilename
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storeCruxResults(String cruxFilename) throws IOException, SQLException {
		try {
			spectraThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.execute(new CruxStorager(conn, new File(cruxFilename)));		
	}
	
	/**
	 * This methods stores the Inspect results to the DB.
	 * @param inspectFilename
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storeInspectResults(String inspectFilename) throws IOException, SQLException {
		
		try {
			spectraThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.execute(new InspectStorager(conn, new File(inspectFilename)));
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
