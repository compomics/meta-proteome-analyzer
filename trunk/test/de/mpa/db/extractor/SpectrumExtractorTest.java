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
import de.mpa.io.MascotGenericFile;

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
	
	
	@Test
	public void testGetMascotGenericFile() throws SQLException, IOException {
		MascotGenericFile mgf = SpectrumExtractor.getMascotGenericFile(1001, conn);		
		assertEquals("Cmpd 1, +MSn(732.2648), 11.7 min", mgf.getTitle());		
		assertEquals(2, mgf.getCharge());
		
//		File outFile = new File("out.mgf");
//		outFile.createNewFile();
//		FileOutputStream fos = new FileOutputStream(outFile);
//		
//		mgf = SpectrumExtractor.getMascotGenericFile(157140, conn);
//		mgf.writeToStream(fos);
//		mgf = SpectrumExtractor.getMascotGenericFile(157170, conn);
//		mgf.writeToStream(fos);
//		mgf = SpectrumExtractor.getMascotGenericFile(157201, conn);
//		mgf.writeToStream(fos);
//		
//		mgf = SpectrumExtractor.getMascotGenericFile(182583, conn);
//		mgf.writeToStream(fos);
//		mgf = SpectrumExtractor.getMascotGenericFile(182613, conn);
//		mgf.writeToStream(fos);
//		mgf = SpectrumExtractor.getMascotGenericFile(182708, conn);
//		mgf.writeToStream(fos);
//		mgf = SpectrumExtractor.getMascotGenericFile(204979, conn);
//		mgf.writeToStream(fos);
//		mgf = SpectrumExtractor.getMascotGenericFile(205009, conn);
//		mgf.writeToStream(fos);
//		mgf = SpectrumExtractor.getMascotGenericFile(227424, conn);
//		mgf.writeToStream(fos);
//		mgf = SpectrumExtractor.getMascotGenericFile(227467, conn);
//		mgf.writeToStream(fos);
//		mgf = SpectrumExtractor.getMascotGenericFile(250094, conn);
//		mgf.writeToStream(fos);
//		mgf = SpectrumExtractor.getMascotGenericFile(250123, conn);
//		mgf.writeToStream(fos);
//		
//		fos.close();
	}
	
	
}
