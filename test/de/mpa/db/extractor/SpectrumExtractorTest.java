package de.mpa.db.extractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpa.algorithms.Interval;
import de.mpa.client.model.specsim.SpectralSearchCandidate;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.io.MascotGenericFile;

public class SpectrumExtractorTest {

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
	@Ignore
	public void testGetCandidates() throws SQLException, IOException {
		
		List<Interval> intervals = new ArrayList<Interval>(1);
		intervals.add(new Interval(352.2322, 352.2322));
		
		List<SpectralSearchCandidate> candidates = specEx.getCandidatesFromExperiment(intervals, 0L);
		
		SpectralSearchCandidate candidate = candidates.get(0);
		
		// Test library spectrum
		TestCase.assertEquals("VTAVDAK", candidate.getSequence());
		TestCase.assertEquals(352.2322, candidate.getPrecursorMz());
		
		// Test spectrum file extraction
		TreeMap<Double, Double> peaks = new TreeMap<Double, Double>(candidate.getPeaks());
		
		// Test the m/z value
		TestCase.assertEquals(114.92747, peaks.firstEntry().getKey(), 0.0001);
		
		// Test the intensity
		TestCase.assertEquals(902.0, peaks.firstEntry().getValue(), 0.0001);
	}
	
	
	@Test
	public void testGetMascotGenericFile() throws SQLException, IOException {
		List<Searchspectrum> searchspectra = Searchspectrum.findFromExperimentID(203, conn);
		System.out.println("No. Spectra: " + searchspectra.size());
		
		for (int i = 10000; i < 20000; i++) {
			Searchspectrum s = searchspectra.get(i);
			boolean identified = false;
			List<XTandemhit> hits1 = XTandemhit.getHitsFromSpectrumID(s.getSearchspectrumid(), conn);
			List<Omssahit> hits2 = Omssahit.getHitsFromSpectrumID(s.getSearchspectrumid(), conn);
			
			// Check for available identifications.
			if (hits1.size() > 0 || hits2.size() > 0) identified = true;
			
			MascotGenericFile mgf = SpectrumExtractor.getMascotGenericFile(s.getFk_spectrumid(), conn);
			File outFile = null;
			if (identified) {
				outFile = new File("/scratch/SpectrumQuality/P23/id2/Spectrum" + s.getFk_spectrumid() + ".mgf");
			} else {
				outFile = new File("/scratch/SpectrumQuality/P23/nonid2/Spectrum" + s.getFk_spectrumid() + ".mgf");
			}
			outFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(outFile);
			mgf.writeToStream(fos);
			fos.close();
		}
	}
	
	
}
