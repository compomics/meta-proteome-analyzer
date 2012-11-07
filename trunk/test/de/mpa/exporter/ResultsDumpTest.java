package de.mpa.exporter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.Client;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.db.ConnectionType;
import de.mpa.db.DBConfiguration;
import de.mpa.db.DbConnectionSettings;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.extractor.SearchHitExtractor;
import de.mpa.io.MascotGenericFile;

public class ResultsDumpTest extends TestCase {
	
	/**
	 * The database connection.
	 */
	private Connection conn;
	
	/**
	 * The database search result object.
	 */
	private DbSearchResult resultToExport;

	/**
	 * Establishes a connection to the database and fetches search result data from it.
	 */
	@Before
	public void setUp() {
		try {
			// init database connection
			DBConfiguration dbconfig = new DBConfiguration(
					"metaprot", ConnectionType.REMOTE, new DbConnectionSettings());
			this.conn = dbconfig.getConnection();
			
			// init search result object
			this.resultToExport = new DbSearchResult("Ecoli", "FewProteins", null);
			
			// fetch search hits from database and add them to the result object
			List<SearchHit> searchHits = SearchHitExtractor.findSearchHitsFromExperimentID(34L, conn);
			for (SearchHit hit : searchHits) {
				// create peptide-spectrum match
				PeptideSpectrumMatch psm = new PeptideSpectrumMatch(hit.getFk_searchspectrumid(), hit);
				
				// create peptide hit
				PeptideAccessor peptide = PeptideAccessor.findFromID(hit.getFk_peptideid(), conn);
				PeptideHit peptideHit = new PeptideHit(peptide.getSequence(), psm);
				
				// create protein hit
				ProteinAccessor protein = ProteinAccessor.findFromID(hit.getFk_proteinid(), conn);
				ProteinHit proteinHit = new ProteinHit(
						protein.getAccession(), protein.getDescription(), protein.getSequence(), peptideHit);
				
				// add protein hit to result object
				resultToExport.addProtein(proteinHit);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests result object integrity after serialization and deserialization.
	 * @throws IOException if any file operations fail.
	 * @throws ClassNotFoundException if class of serialized object cannot be found. 
	 */
	@Test
	public void testSerializeDeserialize() throws IOException, ClassNotFoundException {
		// init target file
		File file = new File("DumpTest.mpa");
		
		// dump to file
		FileOutputStream fos = new FileOutputStream(file);
		// store as compressed binary object
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(fos)));
		
		// write out result object
		oos.writeObject(resultToExport);
		oos.flush();
		oos.close();
		
		// read dumped file
		FileInputStream fis = new FileInputStream(file);
		// extract compressed binary object
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(fis)));
		
		// read in result object
		DbSearchResult importedResult = (DbSearchResult) ois.readObject();
		ois.close();
		
		// assert object equality
		assertEquals(resultToExport, importedResult);	// using equals() implementation of DBSearchResult class
		
		SearchHit expHit = ((PeptideSpectrumMatch) resultToExport.getProteinHitList().get(0).
				getPeptideHitList().get(0).getSpectrumMatches().get(0)).getSearchHits().get(0);
		SearchHit impHit = ((PeptideSpectrumMatch) importedResult.getProteinHitList().get(0).
				getPeptideHitList().get(0).getSpectrumMatches().get(0)).getSearchHits().get(0);
		assertEquals(expHit, impHit);	// using equals() implementation of SearchHit implementations
	}

	
	
	/**
	 * Test to write and read the mgf as extra File
	 */
	@Test
	public void testClientwriteDbSearchResultToFile(){
		Client.getInstance().writeDbSearchResultToFile("C:\\Documents and Settings\\heyer\\Desktop\\RobertTestet.mpa", resultToExport);
		MascotGenericFile mgfOriginal2385 = null; 
		MascotGenericFile mgfOriginal1081669 = null; 
		
		List<ProteinHit> proteinHitList 		= resultToExport.getProteinHitList();
	    List<PeptideHit> peptideHitList 		= proteinHitList.get(2).getPeptideHitList();
	    List<SpectrumMatch> spectrumMatches 	= peptideHitList.get(0).getSpectrumMatches();
	    // Get original mgf
	    try {
	    	mgfOriginal2385 		= Client.getInstance().getSpectrumFromSearchSpectrumID(spectrumMatches.get(0).getSearchSpectrumID());
	    	mgfOriginal1081669 		= Client.getInstance().getSpectrumFromSearchSpectrumID(1081669L);
	    } catch (SQLException e) {
			e.printStackTrace();
		};
		
		// Get original peaks
	    HashMap<Double, Double> peaksOrigin2385 	= mgfOriginal2385.getPeaks();
	    HashMap<Double, Double> peaksOrigin1081669 	= mgfOriginal1081669.getPeaks();
	    
	    // Reload Mgfs
	    MascotGenericFile mgfFile2385 				= Client.getInstance().readMgf("C:\\Documents and Settings\\heyer\\Desktop\\RobertTestet.mgf", 2385L);
	    HashMap<Double, Double> peaksReLoaded2385 	= mgfFile2385.getPeaks();
	    
	    MascotGenericFile mgfFile2683 				= Client.getInstance().readMgf("C:\\Documents and Settings\\heyer\\Desktop\\RobertTestet.mgf", 2683L);
	    HashMap<Double, Double> peaksReLoaded2683 	= mgfFile2683.getPeaks();
	    
	    //Tests
	    assertEquals(peaksOrigin2385, peaksReLoaded2385);
	    assertEquals(mgfOriginal2385.getTitle(), mgfFile2385.getTitle());
	    assertEquals(mgfOriginal2385.getCharge(), mgfFile2385.getCharge());
	    assertEquals(mgfOriginal2385.getPrecursorMZ(), mgfFile2385.getPrecursorMZ());
	    assertEquals(mgfOriginal2385.getIntensity(), mgfFile2385.getIntensity());
	    assertEquals(peaksOrigin1081669, peaksReLoaded2683);
	}
}
