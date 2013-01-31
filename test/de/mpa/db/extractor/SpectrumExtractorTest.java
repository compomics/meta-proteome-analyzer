package de.mpa.db.extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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
	public void testExtractLibrarySpectra() throws SQLException {
		
		long experimentID = 8L;
		List<MascotGenericFile> spectra;
		
		// Get all library spectra associated with experiment
		spectra = specEx.getSpectraByExperimentID(experimentID, false, true, true);
		
		assertEquals(21519, spectra.size());
		
		// Get annotated library spectra associated with experiment
		spectra = specEx.getSpectraByExperimentID(experimentID, true, true, true);
		
		assertEquals(3838, spectra.size());
		
		// remove spectra outside retention time window from list
		double rtMin = 30.0, rtMax = 60.0;
		Iterator<MascotGenericFile> iter = spectra.iterator();
		while (iter.hasNext()) {
			MascotGenericFile mgf = (MascotGenericFile) iter.next();
			
			String title = mgf.getTitle();
			int lastSpace = title.lastIndexOf(" ");
			double rt = Double.parseDouble(title.substring(
					title.lastIndexOf(" ", lastSpace - 1) + 1, lastSpace));
			
			if (rt < rtMin || rt > rtMax) {
				iter.remove();
			}
		}
		assertEquals(1739, spectra.size());
		
		// Test spectrum ID retrieval
		assertEquals(155959L, spectra.get(0).getSpectrumID().longValue());
		assertEquals(160930L, spectra.get(spectra.size() - 1).getSpectrumID().longValue());
		
//		// Store in DB
//		long targetExpID = 55L;
//		for (MascotGenericFile mgf : spectra) {
//			Long spectrumID = mgf.getSpectrumID();
//			
//			/* libspectrum section */
//			HashMap<Object, Object> libdata = new HashMap<Object, Object>(6);
//			
//			libdata.put(Libspectrum.FK_SPECTRUMID, spectrumID);
//			libdata.put(Libspectrum.FK_EXPERIMENTID, targetExpID);
//
//			// Create the database object.
//			Libspectrum libspectrum = new Libspectrum(libdata);
//			libspectrum.persist(conn);
//		}
//		conn.commit();
		
//		// Write to file
//		try {
//			FileOutputStream fos = new FileOutputStream(new File("Ecoli_library_abridged.mgf"));
//			for (MascotGenericFile mgf : spectra) {
//				mgf.writeToStream(fos);
//			}
//			fos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
}
