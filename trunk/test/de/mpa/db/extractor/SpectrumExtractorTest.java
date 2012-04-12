package de.mpa.db.extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;

import junit.framework.TestCase;
import de.mpa.algorithms.LibrarySpectrum;
import de.mpa.db.DBManager;
import de.mpa.io.MascotGenericFile;

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
	
	// Testing the extraction of the library spectra from the database + the zipped spectrum files.
	public void testGetLibrarySpectra() throws SQLException, IOException{
		SpectrumExtractor specExtractor = new SpectrumExtractor(conn);
		List<LibrarySpectrum> libSpectra = specExtractor.getLibrarySpectra(1098.4841, 0.0);
		LibrarySpectrum libSpectrum1 = libSpectra.get(0);
		// Test library spectrum
		assertEquals("TDGAEMSK", libSpectrum1.getSequence());
		assertEquals(1098.4841, libSpectrum1.getSpectrumFile().getPrecursorMZ());
		
		// Test spectrum file extraction
		MascotGenericFile mgf = libSpectrum1.getSpectrumFile();
		assertEquals("1A1_1.mgf", mgf.getFilename());
		TreeMap<Double, Double> peaks = new TreeMap<Double, Double>(mgf.getPeaks());
		
		// Test the m/z value
		assertEquals(55.708918, peaks.firstEntry().getKey(), 0.0001);
		
		// Test the intensity
		assertEquals(14.505132, peaks.firstEntry().getValue(), 0.0001);
	}
}
