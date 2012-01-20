package de.mpa.db.extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.mpa.algorithms.LibrarySpectrum;
import de.mpa.algorithms.Protein;
import de.mpa.db.accessor.Libspectrum;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Spec2pep;
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
		List<Spec2pep> entries = Spec2pep.getEntriesWithinPrecursorRange(precursorMz, tolMz, conn);
		
		// Iterate the spectral library entries.
		for (Spec2pep entry : entries) {
			long spectrumID = entry.getFk_spectrumid();
			MascotGenericFile mgf = getUnzippedFile(spectrumID);
			// get list of proteins from list of peptides and gather annotations
			long peptideID = entry.getFk_peptideid();
			List<PeptideAccessor> peptides = PeptideAccessor.findFromID(peptideID, conn);
			for (PeptideAccessor peptide : peptides) {
				ArrayList<Long> proteinIDs = (ArrayList<Long>) Pep2prot.findProteinIDsFromPeptideID(peptide.getPeptideid(), conn);
				LibrarySpectrum libSpec = new LibrarySpectrum(mgf, mgf.getPrecursorMZ(), peptide.getSequence());
				for (Long proteinID : proteinIDs) {
					ProteinAccessor protein = ProteinAccessor.findFromID(proteinID, conn);
					libSpec.addAnnotation(new Protein(protein.getAccession(), protein.getDescription()));
				}
				libSpectra.add(libSpec);
			}
		}
		
		return libSpectra;
	}
	
	/**
	 * Returns the spectra which are taken for the spectral comparison.
	 * Condition is to be within a certain precursor mass range.
	 * @param precursorMz The precursor mass
     * @param tolMz The precursor mass tolerance
	 * @return
	 * @throws SQLException 
	 * @throws IOException 
	 */
	// TODO: create class for unannotated spectrum and make LibrarySpectrum a subclass of it
	public List<LibrarySpectrum> getSpectra(double precursorMz, double tolMz) throws SQLException, IOException{
		List<LibrarySpectrum> spectra = new ArrayList<LibrarySpectrum>();
		
		// Get the spectral library entries with similar precursor mass.
		List<Libspectrum> entries = Libspectrum.getEntriesWithinPrecursorRange(precursorMz, tolMz, conn);
		
		// Iterate the spectral library entries.
		for (Libspectrum entry : entries) {
			MascotGenericFile mgf = getUnzippedFile(entry.getLibspectrumid());
			// check whether annotations exist
			long libSpecID = entry.getLibspectrumid();
			// find peptides first
			List<PeptideAccessor> peptides = PeptideAccessor.findFromSpectrumID(libSpecID, conn);
			if (!peptides.isEmpty()) {
				for (PeptideAccessor peptide : peptides) {
					// find protein annotations next
					ArrayList<Long> proteinIDs = (ArrayList<Long>) Pep2prot.findProteinIDsFromPeptideID(peptide.getPeptideid(), conn);
					LibrarySpectrum libSpec = new LibrarySpectrum(mgf, mgf.getPrecursorMZ(), peptide.getSequence());
					for (Long proteinID : proteinIDs) {
						ProteinAccessor protein = ProteinAccessor.findFromID(proteinID, conn);
						libSpec.addAnnotation(new Protein(protein.getAccession(), protein.getDescription()));
					}
					spectra.add(libSpec);
				}
			} else {
				spectra.add(new LibrarySpectrum(mgf, entry.getPrecursor_mz().doubleValue(), null));
			}
		}
		
		return spectra;
	}
	
	/**
	 * Returns the unzipped file from the database.
	 * @param spectrumID
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public MascotGenericFile getUnzippedFile(long spectrumID) throws SQLException, IOException{		
		// Get the spectrum + spectrum file.
		Spectrumfile spectrumFile = Spectrumfile.findFromID(spectrumID, conn);
		Libspectrum spectrum = Libspectrum.findFromID(spectrumID, conn);
		
		// Get the resultant bytes
		byte[] result = spectrumFile.getUnzippedFile();
		
		return new MascotGenericFile(spectrum.getFilename(), new String(result));
	}
}
