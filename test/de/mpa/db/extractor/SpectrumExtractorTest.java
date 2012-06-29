package de.mpa.db.extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import junit.framework.TestCase;
import de.mpa.algorithms.Interval;
import de.mpa.client.model.specsim.SpectralSearchCandidate;
import de.mpa.db.DBManager;

public class SpectrumExtractorTest extends TestCase {
	
	private Connection conn;
	public SpectrumExtractorTest() {
		try {
			DBManager dbManager = DBManager.getInstance();
			conn = dbManager.getConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests the extraction of spectral search candidates from the database.
	 */
	public void testGetLibrarySpectra() throws SQLException, IOException {
		SpectrumExtractor specExtractor = new SpectrumExtractor(conn);
		
		List<Interval> intervals = new ArrayList<Interval>(1);
		intervals.add(new Interval(1098.4841, 1098.4841));
		
		List<SpectralSearchCandidate> candidates = specExtractor.getCandidates(intervals);
		
		SpectralSearchCandidate candidate = candidates.get(0);
		
		// Test library spectrum
		assertEquals("TDGAEMSK", candidate.getSequence());
		assertEquals(1098.4841, candidate.getPrecursorMz());
		
		// Test spectrum file extraction
		TreeMap<Double, Double> peaks = new TreeMap<Double, Double>(candidate.getPeaks());
		
		// Test the m/z value
		assertEquals(55.708918, peaks.firstEntry().getKey(), 0.0001);
		
		// Test the intensity
		assertEquals(14.505132, peaks.firstEntry().getValue(), 0.0001);
	}
}
