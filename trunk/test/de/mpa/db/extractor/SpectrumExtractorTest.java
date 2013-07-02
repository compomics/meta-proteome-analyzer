package de.mpa.db.extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.algorithms.Interval;
import de.mpa.client.model.specsim.SpectralSearchCandidate;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.SixtyFourBitStringSupport;

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
	
//	/**
//	 * Tests the extraction of spectral search candidates from the database.
//	 */
//	@Test
//	public void testGetCandidates() throws SQLException, IOException {
//		
//		List<Interval> intervals = new ArrayList<Interval>(1);
//		intervals.add(new Interval(352.2322, 352.2322));
//		
//		List<SpectralSearchCandidate> candidates = specEx.getCandidatesFromExperiment(intervals, 0L);
//		
//		SpectralSearchCandidate candidate = candidates.get(0);
//		
//		// Test library spectrum
//		assertEquals("VTAVDAK", candidate.getSequence());
//		assertEquals(352.2322, candidate.getPrecursorMz());
//		
//		// Test spectrum file extraction
//		TreeMap<Double, Double> peaks = new TreeMap<Double, Double>(candidate.getPeaks());
//		
//		// Test the m/z value
//		assertEquals(114.92747, peaks.firstEntry().getKey(), 0.0001);
//		
//		// Test the intensity
//		assertEquals(902.0, peaks.firstEntry().getValue(), 0.0001);
//	}
	
	
	@Test
	public void testGetMascotGenericFile() throws SQLException, IOException {
		MascotGenericFile mgf = SpectrumExtractor.getMascotGenericFile(1001, conn);		
		assertEquals("Cmpd 1, +MSn(732.2648), 11.7 min", mgf.getTitle());		
		assertEquals(2, mgf.getCharge());
		
        /* New spectrum section */
        HashMap<Object, Object> data = new HashMap<Object, Object>(12);
    
        // The spectrum title
        data.put(Spectrum.TITLE, mgf.getTitle());
        
        // The precursor mass.
        data.put(Spectrum.PRECURSOR_MZ, mgf.getPrecursorMZ());
        
        // The precursor intensity
        data.put(Spectrum.PRECURSOR_INT, mgf.getIntensity());
        
        // The precursor charge
        data.put(Spectrum.PRECURSOR_CHARGE, Long.valueOf(mgf.getCharge()));
        
        // The m/z array
        TreeMap<Double, Double> peakMap = new TreeMap<Double, Double>(mgf.getPeaks());
		Double[] mzDoubles = peakMap.keySet().toArray(new Double[0]);
        data.put(Spectrum.MZARRAY, SixtyFourBitStringSupport.encodeDoublesToBase64String(mzDoubles));
        
        // The intensity array
		Double[] inDoubles = peakMap.values().toArray(new Double[0]);
        data.put(Spectrum.INTARRAY, SixtyFourBitStringSupport.encodeDoublesToBase64String(inDoubles));
        
        // The charge array
        TreeMap<Double, Integer> chargeMap = new TreeMap<Double, Integer>(mgf.getCharges());
        for (Double mz : peakMap.keySet()) {
			if (!chargeMap.containsKey(mz)) {
				chargeMap.put(mz, 0);
			}
		}
		Integer[] chInts = chargeMap.values().toArray(new Integer[0]);
		data.put(Spectrum.CHARGEARRAY, SixtyFourBitStringSupport.encodeIntsToBase64String(chInts));
        
        // The total intensity.
        data.put(Spectrum.TOTAL_INT, mgf.getTotalIntensity());
        
        // The highest intensity.
        data.put(Spectrum.MAXIMUM_INT, mgf.getHighestIntensity());

        // Create the database object.
        Spectrum query = new Spectrum(data);
        query.persist(conn);
        
        // Get the spectrumid from the generated keys.
        Long spectrumid = (Long) query.getGeneratedKeys()[0];
        
        /* Searchspectrum storager*/
        HashMap<Object, Object> searchData = new HashMap<Object, Object>(5);

        searchData.put(Searchspectrum.FK_SPECTRUMID, spectrumid);
        searchData.put(Searchspectrum.FK_EXPERIMENTID, 1L);

        Searchspectrum searchSpectrum = new Searchspectrum(searchData);
        searchSpectrum.persist(conn);

        // Get the search spectrum id from the generated keys.
        Long searchspectrumid = (Long) searchSpectrum.getGeneratedKeys()[0];
        
        conn.commit();
	}
	
	
}
