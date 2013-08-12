package de.mpa.db.extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.algorithms.Interval;
import de.mpa.client.model.specsim.SpectralSearchCandidate;
import de.mpa.db.DBManager;

public class SpectrumExtractorTest extends TestCase {

	private Connection conn;
	private SpectrumExtractor specEx;
	
	@Before
	public void setUp() {
		try {
			DBManager dbManager = DBManager.getInstance();
			conn = dbManager.getConnection();
			conn.setAutoCommit(false);
			specEx = new SpectrumExtractor(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests the extraction of spectral search candidates from the database.
	 */
	@Test
	public void testGetCandidates() throws SQLException, IOException {
		
		List<Interval> intervals = new ArrayList<Interval>(1);
		intervals.add(new Interval(352.2322, 352.2322));
		
		List<SpectralSearchCandidate> candidates = specEx.getCandidatesFromExperiment(intervals, 0L);
		
		SpectralSearchCandidate candidate = candidates.get(0);
		
		// Test library spectrum
		assertEquals("VTAVDAK", candidate.getSequence());
		assertEquals(352.2322, candidate.getPrecursorMz());
		
		// Test spectrum file extraction
		TreeMap<Double, Double> peaks = new TreeMap<Double, Double>(candidate.getPeaks());
		
		// Test the m/z value
		assertEquals(114.92747, peaks.firstEntry().getKey(), 0.0001);
		
		// Test the intensity
		assertEquals(902.0, peaks.firstEntry().getValue(), 0.0001);
	}

	
	
}
