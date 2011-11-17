package de.mpa.db.extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;
import de.mpa.algorithms.LibrarySpectrum;
import de.mpa.db.DBManager;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.Peak;

public class SpectrumExtractorTest extends TestCase {
	
	private Connection conn;
	public SpectrumExtractorTest() {
		try {
			DBManager dbManager = new DBManager();
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
		assertEquals(1098.4841, libSpectrum1.getPrecursorMz());
		
		// Test spectrum file extraction
		MascotGenericFile mgf = libSpectrum1.getSpectrumFile();
		assertEquals("1A1_1.mgf", mgf.getFilename());
		List<Peak> peaks = mgf.getPeakList();
		
		// Test the m/z value
		assertEquals(55.708918, peaks.get(0).getMz(), 0.0001);
		
		// Test the intensity
		assertEquals(14.505132, peaks.get(0).getIntensity(), 0.0001);
	}
}
