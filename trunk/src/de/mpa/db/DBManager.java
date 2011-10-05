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

public class DBManager {
	
    private Connection conn;
	private Thread spectraThread;
	private ExecutorService executor;
    
    public DBManager() throws SQLException {
		init();
	}
	private void init() throws SQLException{
		DBConfiguration dbconfig = new DBConfiguration("metaproteomics");
		conn = dbconfig.getConnection();
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
		Spectrum.checkDuplicateFile(mgfFile.getName(), conn);
		
		ProjectStorager projectStorager = new ProjectStorager(conn, title, taxon, fragmentTol, precursorTol, precursorUnit);
		Thread thread = new Thread(projectStorager);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
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
}
