package de.mpa.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.db.storager.CruxStorager;
import de.mpa.db.storager.InspectStorager;
import de.mpa.db.storager.OmssaStorager;
import de.mpa.db.storager.PepnovoStorager;
import de.mpa.db.storager.SpecSimStorager;
import de.mpa.db.storager.SpectrumStorager;
import de.mpa.db.storager.XTandemStorager;

/**
 * This class serves for handling and managing the database.
 * 
 * @author T.Muth
 *
 */
public class DBManager {
	
	/**
	 * Database connection.
	 */
    private Connection conn;
    
    /**
     * Separate spectrum storage thread.
     */
	private Thread spectraThread;
	
	/**
	 * DBManager instance.
	 */
	private static DBManager instance;
    
	/**
	 * Constructor for the database manager.
	 * @throws SQLException
	 */
    private DBManager() throws SQLException {
		init();
	}
    
    /**
     * Returns an instance of the DBManager.
     * @return DBMananger instance.
     * @throws SQLException
     */
    public static DBManager getInstance() throws SQLException {
    	if (instance == null || !instance.getConnection().isValid(1)) {
    		instance = new DBManager();
    	}
		return instance;
    }
    
    /**
     * Initialize the database manager.
     * @throws SQLException
     */
	private void init() throws SQLException {	
		// The database configuration.
		DBConfiguration dbconfig = new DBConfiguration("metaprot", ConnectionType.LOCAL, new DbConnectionSettings());
		conn = dbconfig.getConnection();
    }
	
	/**
	 * This method stores the spectrum contents to the database.
	 * @param spectrumFile The spectrum file.
	 * @param experimentid The experiment id.
	 * @throws SQLException  
	 * @throws IOException 
	 */
	public SpectrumStorager storeSpectra(File spectrumFile, long experimentid) throws IOException, SQLException, InterruptedException {
		// Store the spectra from the spectrum file for a given experiment.	
		// TODO: No redudancy check uses for the spectrum storing... add flag option to client ?
		SpectrumStorager specStorager = new SpectrumStorager(conn, spectrumFile, experimentid, false);
		spectraThread = new Thread(specStorager);
		spectraThread.start();
		spectraThread.join();
		return specStorager;
	}
	
	/**
	 * This methods stores the XTandem results to the DB.
	 * @param xtandemFilename
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storeXTandemResults(String xtandemFilename, String xtandemQValued) throws IOException, SQLException, InterruptedException {
		spectraThread.join();
		if (xtandemQValued != null) {
			XTandemStorager job = new XTandemStorager(conn, new File(xtandemFilename), new File(xtandemQValued));
			job.run();
		} else {
			XTandemStorager job = new XTandemStorager(conn, new File(xtandemFilename));
			job.run();
		}
	}
	
	/**
	 * This methods stores the Omssa results to the DB.
	 * @param omssaFilename
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storeOmssaResults(String omssaFilename, String omssaQValued) throws IOException, SQLException, InterruptedException {
		spectraThread.join();
		if (omssaQValued != null) {
			OmssaStorager job = new OmssaStorager(conn, new File(omssaFilename), new File(omssaQValued)); 
			job.run();
		} else {
			OmssaStorager job = new OmssaStorager(conn, new File(omssaFilename));
			job.run();
		}
	}
	
	/**
	 * This methods stores the Crux results to the DB.
	 * @param cruxFilename
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storeCruxResults(String cruxFilename) throws IOException, SQLException, InterruptedException {
		spectraThread.join();
		CruxStorager job = new CruxStorager(conn, new File(cruxFilename));	
		job.run();
	}
	
	/**
	 * This methods stores the Inspect results to the DB.
	 * @param inspectFilename
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storeInspectResults(String inspectFilename) throws IOException, SQLException, InterruptedException {
		spectraThread.join();
		InspectStorager job = new InspectStorager(conn, new File(inspectFilename));
		job.run();
	}

	/**
	 * Stores found spectrum-spectrum matches to the DB.
	 * @param results List of SSMs.
	 * @throws InterruptedException 
	 */
	public void storeSpecSimResults(List<SpectrumSpectrumMatch> results) throws InterruptedException {
		SpecSimStorager storager = new SpecSimStorager(conn, results);
		Thread thread = new Thread(storager);
		thread.start();
		thread.join();
	}
	
	/**
	 * This methods stores the Pepnovo results to the DB.
	 * @param pepnovoFilename
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storePepnovoResults(String pepnovoFilename) throws IOException, SQLException, InterruptedException {
		spectraThread.join();
		PepnovoStorager job = new PepnovoStorager(conn, new File(pepnovoFilename));
		job.run();
	}
	
	/**
	 * Returns the connection.
	 * @return
	 */
	public Connection getConnection() {
		return conn;
	}
}
