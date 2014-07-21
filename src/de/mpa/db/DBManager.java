package de.mpa.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import de.mpa.analysis.ReducedProteinData;
import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.client.settings.ConnectionParameters;
import de.mpa.client.settings.ParameterMap;
import de.mpa.db.accessor.Uniprotentry;
import de.mpa.db.storager.CruxStorager;
import de.mpa.db.storager.InspectStorager;
import de.mpa.db.storager.OmssaStorager;
import de.mpa.db.storager.SpecSimStorager;
import de.mpa.db.storager.SpectrumStorager;
import de.mpa.db.storager.Storager;
import de.mpa.db.storager.XTandemStorager;
import de.mpa.util.Formatter;

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
		SpectrumStorager specStorager = new SpectrumStorager(conn, spectrumFile, experimentid, false);
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
	 */
	public void storeDatabaseSearchResults(SearchEngineType searchEngineType, String resultFilename, String qValueFilename) throws InterruptedException {
		// Wait for spectra to be stored to the database.
		spectraThread.join();
		Storager storager = null;
		
		if (searchEngineType == SearchEngineType.XTANDEM && qValueFilename != null) {
			String targetScoreFilename = qValueFilename.substring(0, qValueFilename.lastIndexOf("_qvalued")) + "_target.out";;
			storager = new XTandemStorager(conn, new File(resultFilename), new File(targetScoreFilename), new File(qValueFilename));
		}
		else if (searchEngineType == SearchEngineType.XTANDEM && qValueFilename == null) storager = new XTandemStorager(conn, new File(resultFilename));
		else if (searchEngineType == SearchEngineType.OMSSA && qValueFilename != null) {
			String targetScoreFilename = qValueFilename.substring(0, qValueFilename.lastIndexOf("_qvalued")) + "_target.out";;
			storager = new OmssaStorager(conn, new File(resultFilename), new File (targetScoreFilename), new File(qValueFilename));
		}
		else if (searchEngineType == SearchEngineType.OMSSA && qValueFilename == null) storager = new XTandemStorager(conn, new File(resultFilename));
		else if (searchEngineType == SearchEngineType.CRUX ) storager = new CruxStorager(conn, new File(resultFilename));
		else if (searchEngineType == SearchEngineType.INSPECT) storager = new InspectStorager(conn, new File(resultFilename));
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
	
	/**
	 * Method to query and store the UniProt entries. 
	 * Retrieval is based on the UniProt JAPI.
	 * @throws SQLException
	 */
	public void queryAndStoreUniprotEntries(boolean doUniRefRetrieval) throws SQLException {
		Map<String, ReducedProteinData> proteinDataMap = null;
		
		// Retrieve the UniProt entries.
		Map<String, Long> proteinHits = MapContainer.UniprotQueryProteins;
		Set<String> keySet = proteinHits.keySet();
		List<String> accessions = new ArrayList<String>();
		for (String string : keySet) {			
			// UniProt accession
			Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(proteinHits.get(string), conn);
			if (uniprotentry == null) {
				if (string.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
					accessions.add(string);		
				}
			}
		}
		if (accessions.size() > 0) {
			proteinDataMap = UniProtUtilities.retrieveProteinData(accessions, doUniRefRetrieval);
			Set<Entry<String, ReducedProteinData>> entrySet = proteinDataMap.entrySet();
			int counter = 0;
			for (Entry<String, ReducedProteinData> e : entrySet) {
				ReducedProteinData proteinData = e.getValue();
				if (proteinData != null && proteinData.getUniProtEntry() != null) {
					UniProtEntry uniProtEntry = proteinData.getUniProtEntry();
					// Get the corresponding protein accessor.
					long proteinid = 0L;

					// Check for secondary protein accessions.
					if (proteinHits.get(e.getKey()) == null) {
						List<SecondaryUniProtAccession> secondaryUniProtAccessions = uniProtEntry.getSecondaryUniProtAccessions();
						for (SecondaryUniProtAccession acc : secondaryUniProtAccessions) {
							if (proteinHits.get(acc.getValue()) != null) {
								proteinid = proteinHits.get(acc.getValue());
							}
						}
					} else {
						proteinid = proteinHits.get(e.getKey());
					}
					// Get taxonomy id
					Long taxID = Long.valueOf(uniProtEntry.getNcbiTaxonomyIds().get(0).getValue());

					// Get EC Numbers.
					String ecNumbers = "";
					List<String> ecNumberList = uniProtEntry.getProteinDescription().getEcNumbers();
					if (ecNumberList.size() > 0) {
						for (String ecNumber : ecNumberList) {
							ecNumbers += ecNumber + ";";
						}
						ecNumbers = Formatter.removeLastChar(ecNumbers);
					}

					// Get ontology keywords.
					String keywords = "";
					List<Keyword> keywordsList = uniProtEntry.getKeywords();

					if (keywordsList.size() > 0) {
						for (Keyword kw : keywordsList) {
							keywords += kw.getValue() + ";";
						}
						keywords = Formatter.removeLastChar(keywords);
					}

					// Get KO numbers.
					String koNumbers = "";
					List<DatabaseCrossReference> xRefs = uniProtEntry.getDatabaseCrossReferences(DatabaseType.KO);
					if (xRefs.size() > 0) {
						for (DatabaseCrossReference xRef : xRefs) {
							koNumbers += xRef.getPrimaryId().getValue() + ";";
						}
						koNumbers = Formatter.removeLastChar(koNumbers);
					}
					
					
					
					String uniref100 = proteinData.getUniRef100EntryId();
					String uniref90 = proteinData.getUniRef90EntryId();
					String uniref50 = proteinData.getUniRef50EntryId();
					Uniprotentry.addUniProtEntryWithProteinID(
							(Long) proteinid, taxID, ecNumbers, koNumbers, keywords,
							uniref100, uniref90, uniref50, conn);
					
					counter++;

					if (counter % 500 == 0) {
						conn.commit();
					}
				}
			}
			// Final commit and clearing of map.
			conn.commit();
			MapContainer.UniprotQueryProteins.clear();
		}
	}
	
	/**
	 * Returns the connection.
	 * @return
	 */
	public Connection getConnection() {
		return conn;
	}
}
