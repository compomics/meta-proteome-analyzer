package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpa.analysis.ReducedProteinData;
import de.mpa.analysis.UniProtUtilities;

public class LinkUniProtToProteinTest {
	
	/**
	 * DB Connection.
	 */
	private Connection conn;
	private Map<String, ReducedProteinData> proteinDataMap;

	@Before
	public void setUp() throws SQLException {

	}
	
	@Test 
	@Ignore
	public void testUpdateUniProtEntries() throws SQLException {
		UniProtUtilities.repairMissingUniRefs();
	}
	
	@Test
	public void testRetrieveProteinsWithoutUniProtEntries() throws SQLException {
		UniProtUtilities.repairEmptyUniProtEntries();
			}
}
