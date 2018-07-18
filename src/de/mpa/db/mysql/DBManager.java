package de.mpa.db.mysql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import de.mpa.client.Client;
import de.mpa.client.settings.ConnectionParameters;
import de.mpa.client.settings.ParameterMap;
import de.mpa.db.mysql.storager.OmssaStorager;
import de.mpa.db.mysql.storager.SpectrumStorager;
import de.mpa.db.mysql.storager.Storager;
import de.mpa.db.mysql.storager.XTandemStorager;
import de.mpa.model.dbsearch.SearchEngineType;

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
	 * Parameter map containing connection settings.
	 */
	private ParameterMap connectionParams;
	
	/**
	 * DBManager instance.
	 */
	private static DBManager instance;
    
	/**
	 * Constructor for the database manager.
	 * @throws SQLException
	 */
    private DBManager() throws SQLException {
        this.init();
	}
    
    /**
     * Returns an instance of the DBManager.
     * @return DBMananger instance.
     * @throws SQLException
     */
    public static DBManager getInstance() throws SQLException {
    	if (DBManager.instance == null) {
            DBManager.instance = new DBManager();
    	}
		return DBManager.instance;
    }
    
    /**
     * Initialize the database manager.
     * @throws SQLException
     */
	private void init() throws SQLException {
		// The database configuration.
		if (this.conn == null || !this.conn.isValid(0)) {
			// connect to database
			if (this.connectionParams == null) {
                this.connectionParams = new ConnectionParameters();
			}

			DBConfiguration dbconfig = new DBConfiguration(this.connectionParams);
            conn = dbconfig.getConnection();
		}
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
		SpectrumStorager specStorager = new SpectrumStorager(this.conn, spectrumFile, experimentid);
        this.spectraThread = new Thread(specStorager);
        this.spectraThread.start();
        this.spectraThread.join();
		return specStorager;
	}
	
	/**
	 * This method is called to store the database search results to the SQL database.
	 * @param searchEngineType SearchEngine type.
	 * @param resultFilename Search engine result filename
	 * @param qValueFile q-value result file
	 * @throws InterruptedException
	 * @throws SQLException 
	 */
	public void storeDatabaseSearchResults(SearchEngineType searchEngineType, String resultFilename, String qValueFilename) throws InterruptedException, SQLException {
		// Wait for spectra to be stored to the database.
        this.spectraThread.join();
		Storager storager = null;
		if (this.conn.isClosed()) {
            this.conn = Client.getInstance().getConnection();
		}
		
		if (searchEngineType == SearchEngineType.XTANDEM && qValueFilename != null) {
			String targetScoreFilename = qValueFilename.substring(0, qValueFilename.lastIndexOf("_qvalued")) + "_target.out";
			storager = new XTandemStorager(this.conn, new File(resultFilename), new File(targetScoreFilename), new File(qValueFilename));
		}
		else if (searchEngineType == SearchEngineType.XTANDEM && qValueFilename == null) storager = new XTandemStorager(this.conn, new File(resultFilename));
		else if (searchEngineType == SearchEngineType.OMSSA && qValueFilename != null) {
			String targetScoreFilename = qValueFilename.substring(0, qValueFilename.lastIndexOf("_qvalued")) + "_target.out";
            storager = new OmssaStorager(this.conn, new File(resultFilename), new File (targetScoreFilename), new File(qValueFilename));
		}
		else if (searchEngineType == SearchEngineType.OMSSA && qValueFilename == null) storager = new XTandemStorager(this.conn, new File(resultFilename));
		storager.run();
	}

	/**
	 * Returns the connection.
	 * @return
	 */
	public Connection getConnection() {
		return this.conn;
	}
	/**
	 * Returns the connection.
	 * @return
	 */
	public Connection reconnect() {
		try {
			this.conn.close();
			this.conn = Client.getInstance().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this.conn;
	}
}
