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
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

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
		File file = new File("test/de/mpa/resources/DumpTest.mpa".replaceAll("/", File.separator));
		
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
	
}
