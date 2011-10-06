package de.mpa.db.extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.mpa.algorithms.LibrarySpectrum;
import de.mpa.db.accessor.Speclibentry;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.Spectrumfile;
import de.mpa.io.MascotGenericFile;

public class SpectrumExtractor {
	
	/**
	 * Connection instance.
	 */
	private Connection conn;
	
	/**
	 * Constructor for the SpectrumExtractor.
	 * @param conn
	 */
	public SpectrumExtractor(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * Returns the library spectra which are taken for the spectral comparison.
	 * Condition is to be within a certain precursor mass range.
	 * @param precursorMz The precursor mass
     * @param tolMz The precursor mass tolerance
	 * @return
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public List<LibrarySpectrum> getLibrarySpectra(double precursorMz, double tolMz) throws SQLException, IOException{
		List<LibrarySpectrum> libSpectra = new ArrayList<LibrarySpectrum>();
		
		// Get the spectral library entries with similar precursor mass.
		List<Speclibentry> entries = Speclibentry.getEntriesWithinPrecursorRange(precursorMz, tolMz, conn);
		
		// Iterate the spectral library entries.
		for (Speclibentry entry : entries) {
			MascotGenericFile mgf = getUnzippedFile(entry.getL_spectrumid());
			libSpectra.add(new LibrarySpectrum(mgf, entry.getPrecursor_mz().doubleValue(), entry.getSequence(), entry.getAnnotation()));			
		}
		
		return libSpectra;
	}
	
	/**
	 * Returns the unzipped file from the database.
	 * @param spectrumID
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	private MascotGenericFile getUnzippedFile(long spectrumID) throws SQLException, IOException{		
		// Get the spectrum + spectrum file.
		Spectrumfile spectrumFile = Spectrumfile.findFromID(spectrumID, conn);
		Spectrum spectrum = Spectrum.findFromID(spectrumID, conn);
		
		// Get the resultant bytes
		byte[] result = spectrumFile.getUnzippedFile();
		
		return new MascotGenericFile(spectrum.getFilename(), new String(result));
	}
}
