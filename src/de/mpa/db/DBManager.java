package de.mpa.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.client.settings.ConnectionParameters;
import de.mpa.client.settings.ParameterMap;
import de.mpa.db.storager.OmssaStorager;
import de.mpa.db.storager.SpecSimStorager;
import de.mpa.db.storager.SpectrumStorager;
import de.mpa.db.storager.Storager;
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
		init();
	}
    
    /**
     * Returns an instance of the DBManager.
     * @return DBMananger instance.
     * @throws SQLException
     */
    public static DBManager getInstance() throws SQLException {
    	if (instance == null) {
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
		if (conn == null || !conn.isValid(0)) {
			// connect to database
			if (connectionParams == null) {
				connectionParams = new ConnectionParameters();
			}

			DBConfiguration dbconfig = new DBConfiguration(connectionParams);
			this.conn = dbconfig.getConnection();
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
		SpectrumStorager specStorager = new SpectrumStorager(conn, spectrumFile, experimentid);
		spectraThread = new Thread(specStorager);
		spectraThread.start();
		spectraThread.join();
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
		spectraThread.join();
		Storager storager = null;
		if (conn.isClosed()) {
			conn = Client.getInstance().getConnection();
		}
		
		if (searchEngineType == SearchEngineType.XTANDEM && qValueFilename != null) {
			String targetScoreFilename = qValueFilename.substring(0, qValueFilename.lastIndexOf("_qvalued")) + "_target.out";
			storager = new XTandemStorager(conn, new File(resultFilename), new File(targetScoreFilename), new File(qValueFilename));
		}
		else if (searchEngineType == SearchEngineType.XTANDEM && qValueFilename == null) storager = new XTandemStorager(conn, new File(resultFilename));
		else if (searchEngineType == SearchEngineType.OMSSA && qValueFilename != null) {
			String targetScoreFilename = qValueFilename.substring(0, qValueFilename.lastIndexOf("_qvalued")) + "_target.out";;
			storager = new OmssaStorager(conn, new File(resultFilename), new File (targetScoreFilename), new File(qValueFilename));
		}
		else if (searchEngineType == SearchEngineType.OMSSA && qValueFilename == null) storager = new XTandemStorager(conn, new File(resultFilename));
		storager.run();
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
	
//	/**
//	 * Method to query and store the UniProt entries. 
//	 * Retrieval is based on the UniProt JAPI.
//	 * @throws SQLException
//	 */
//	public void queryAndStoreUniprotEntries(boolean doUniRefRetrieval) throws SQLException {
//		System.out.println("Do the uniprot");
//		
//		// Retrieve the UniProt entries.
//		Map<String, Long> proteinHits = MapContainer.UniprotQueryProteins;
//		Set<String> keySet = proteinHits.keySet();
//		Map<String, List<Long>> accessionMap = new TreeMap<String, List<Long>>();
//		for (String string : keySet) {
//			System.out.println("ProteinsACC " +  string);
//			// check if uniprotentry exists
//			if (Uniprotentry.check_if_UniProtEntry_exists_from_proteinID(proteinHits.get(string), conn)) {
//				System.out.println("prooved check 1");
//				if (string.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
//					System.out.println("prooved check 2");
//					if (accessionMap.containsKey(string)) {
//						accessionMap.get(string).add(proteinHits.get(string));
//					} else {
//						List<Long> prot_id_list = new ArrayList<Long>();
//						accessionMap.put(string, prot_id_list);
//					}
//				}
//			}
//		}
//		System.out.println("Size " + accessionMap.keySet().size());
//		if (accessionMap.keySet().size() > 0) {
//			// instantiate UniProtUtilites class			
//			UniProtUtilities uniprotweb = new UniProtUtilities();
//			uniprotweb.make_uniprot_entries(accessionMap);
//			// clearing of map.
//			MapContainer.UniprotQueryProteins.clear();
//		}
//	}
	
	/**
	 * Returns the connection.
	 * @return
	 */
	public Connection getConnection() {
		return conn;
	}
}
