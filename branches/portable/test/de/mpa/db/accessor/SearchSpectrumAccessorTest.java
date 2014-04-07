package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.db.DBManager;

public class SearchSpectrumAccessorTest extends TestCase {

	private Connection conn;
	
	@Before
	public void setUp() throws SQLException {
		conn = DBManager.getInstance().getConnection();
	}
	
	@Test
	public void testSpectralCountRetrieval() throws SQLException {
		int specCount = Searchspectrum.getSpectralCountFromExperimentID(42l, conn);
		assertEquals(11694, specCount);
	}
	
}
