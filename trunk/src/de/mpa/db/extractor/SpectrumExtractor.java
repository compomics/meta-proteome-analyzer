package de.mpa.db.extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.mpa.algorithms.Interval;
import de.mpa.algorithms.LibrarySpectrum;
import de.mpa.algorithms.Protein;
import de.mpa.db.accessor.Libspectrum;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Spec2pep;
import de.mpa.db.accessor.Spectrum;
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
				LibrarySpectrum libSpec = new LibrarySpectrum(mgf, spectrumID, peptide.getSequence());
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
					LibrarySpectrum libSpec = new LibrarySpectrum(mgf, libSpecID, peptide.getSequence());
					for (Long proteinID : proteinIDs) {
						ProteinAccessor protein = ProteinAccessor.findFromID(proteinID, conn);
						libSpec.addAnnotation(new Protein(protein.getAccession(), protein.getDescription()));
					}
					spectra.add(libSpec);
				}
			} else {
				spectra.add(new LibrarySpectrum(mgf, libSpecID, null));
			}
		}
		
		return spectra;
	}
	
	/**
	 * Returns the library spectra which are taken for spectral comparison.
	 * Condition is to belong to a certain experiment entry.
	 * @param precursorMz The precursor mass
     * @param tolMz The precursor mass tolerance
	 * @return
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public List<LibrarySpectrum> getLibrarySpectra(long experimentID) throws SQLException, IOException {
		List<LibrarySpectrum> libSpectra = new ArrayList<LibrarySpectrum>();
		
		// Get the spectral library entries with similar precursor mass.
		List<Spec2pep> entries = Spec2pep.getEntriesFromExperimentID(experimentID, conn);
		
		// Iterate the spectral library entries.
		for (Spec2pep entry : entries) {
			long spectrumID = entry.getFk_spectrumid();
			MascotGenericFile mgf = getUnzippedFile(spectrumID);
			// get list of proteins from list of peptides and gather annotations
			long peptideID = entry.getFk_peptideid();
			List<PeptideAccessor> peptides = PeptideAccessor.findFromID(peptideID, conn);
			for (PeptideAccessor peptide : peptides) {
				ArrayList<Long> proteinIDs = (ArrayList<Long>) Pep2prot.findProteinIDsFromPeptideID(peptide.getPeptideid(), conn);
				LibrarySpectrum libSpec = new LibrarySpectrum(mgf, spectrumID, peptide.getSequence());
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
	 * TODO: Modify for array spectrum functionality. 
	 * Returns the unzipped file from the database.
	 * @param spectrumID
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public MascotGenericFile getUnzippedFile(long spectrumID) throws SQLException, IOException{		
		MascotGenericFile res;
		
		PreparedStatement ps = conn.prepareStatement(Spectrum.getBasicSelect() +
				"WHERE " + Spectrum.SPECTRUMID + " = ?");
		ps.setLong(1, spectrumID);
		
		ResultSet rs = ps.executeQuery();
		res = new MascotGenericFile(rs);
		rs.close();
		ps.close();
		
		return res;
	}
	
	/**
	 * Returns the list of spectrum IDs belonging to spectral search candidates of a specific experiment.
	 * @param experimentID The ID of the experiment to be queried.
	 * @return ArrayList containing the spectrum IDs.
	 * @throws SQLException
	 */
	public ArrayList<Long> getCandidateIDsFromExperiment(long experimentID) throws SQLException {
		ArrayList<Long> res = new ArrayList<Long>();
		
		PreparedStatement ps = conn.prepareStatement("SELECT libspectrumid FROM libspectrum " +
					 								 "INNER JOIN spec2pep ON libspectrum.libspectrumid = spec2pep.fk_spectrumid " + 
					 								 "WHERE libspectrum.fk_experimentid = " + experimentID);
		ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(rs.getLong(1));
        }
        rs.close();
        ps.close();
		
		return res;
	}

	/**
	 * Returns the list of spectral search candidates that belong to a specific experiment.
	 * @param experimentID The ID of the experiment to be queried.
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<SpectralSearchCandidate> getCandidatesFromExperiment(long experimentID) throws SQLException {
		ArrayList<Interval> precIntervals = new ArrayList<Interval>();
		precIntervals.add(new Interval(0.0, Double.MAX_VALUE));
		return getCandidatesFromExperiment(precIntervals, experimentID);
	}
	
	/**
	 * Returns the list of spectral search candidates that belong to a specific experiment and are bounded by specified precursor mass intervals.
	 * @param precIntervals The list of precursor mass intervals.
	 * @param experimentID The ID of the experiment to be queried.
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<SpectralSearchCandidate> getCandidatesFromExperiment(ArrayList<Interval> precIntervals, long experimentID) throws SQLException {
		ArrayList<SpectralSearchCandidate> res = new ArrayList<SpectralSearchCandidate>(precIntervals.size());
		
		// construct SQL statement
		StringBuilder sb = new StringBuilder("SELECT spectrumid, title, precursor_mz, precursor_charge, mzarray, intarray, fk_peptideid, sequence FROM spectrum " +
				   							 "INNER JOIN spec2pep ON spectrum.spectrumid = spec2pep.fk_spectrumid " + 
				   							 "INNER JOIN peptide ON spec2pep.fk_peptideid = peptide.peptideid " +
				   							 "INNER JOIN libspectrum ON spectrum.spectrumid = libspectrum.fk_spectrumid " +
				   							 "WHERE (");
			for (Interval precInterval : precIntervals) {
				sb.append("spectrum.precursor_mz BETWEEN ");
				sb.append(precInterval.getLeftBorder());
				sb.append(" AND ");
				sb.append(precInterval.getRightBorder());
				sb.append(" OR ");
			}
			for (int i = 0; i < 3; i++, sb.deleteCharAt(sb.length()-1)) {}	// remove last "OR "
			sb.append(") ");
		if (experimentID != 0L) {
			sb.append("AND libspectrum.fk_experimentid = " + experimentID);
		}
		
		// execute SQL statement and build result list
		PreparedStatement ps = conn.prepareStatement(sb.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(new SpectralSearchCandidate(rs));
        }
        rs.close();
        ps.close();
		
		return res;
	}

	/**
	 * Method to download database spectra belonging to a specific experiment.
	 * @param experimentID
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<MascotGenericFile> downloadSpectra(long experimentID) throws SQLException {
		ArrayList<MascotGenericFile> res = new ArrayList<MascotGenericFile>();
		
		PreparedStatement ps = conn.prepareStatement("SELECT title, precursor_mz, precursor_int, " +
				"precursor_charge, mzarray, intarray, chargearray, sequence FROM spectrum " +
				"INNER JOIN spec2pep on spectrum.spectrumid = spec2pep.fk_spectrumid " +
				"INNER JOIN peptide on spec2pep.fk_peptideid = peptide.peptideid " +
				"INNER JOIN libspectrum on spectrum.spectrumid = libspectrum.fk_spectrumid " +
				"WHERE libspectrum.fk_experimentid = ?");
		ps.setLong(1, experimentID);
		ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	MascotGenericFile mgf = new MascotGenericFile(rs);
        	mgf.setTitle(rs.getString("sequence") + " " + mgf.getTitle());	// prepend peptide sequence
            res.add(mgf);
        }
        rs.close();
        ps.close();
		
		return res;
	}
}
