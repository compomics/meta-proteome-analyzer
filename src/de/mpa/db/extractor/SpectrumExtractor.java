package de.mpa.db.extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.algorithms.Interval;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.specsim.SpectralSearchCandidate;
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
	 * Constructs a MascotGenericFile directly from the database.
	 * @param spectrumID The spectrum id
	 * @return The MascotGenericFile 
	 * @throws SQLException when the retrieval did not succeed.
	 * @throws IOException when the file could not be built.
	 */
	public static MascotGenericFile getMascotGenericFile(long spectrumID, Connection conn) throws SQLException, IOException{
		Spectrum spectrum = Spectrum.findFromSpectrumID(spectrumID, conn);
		MascotGenericFile res = null;
		
		if (spectrum != null){
			res = new MascotGenericFile(spectrum);
		}
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
	
	public List<SpectralSearchCandidate> getCandidates(List<Interval> precIntervals) throws SQLException {
		return getCandidatesFromExperiment(precIntervals, 0L);
	}

	/**
	 * Returns the list of spectral search candidates that belong to a specific experiment.
	 * @param experimentID The ID of the experiment to be queried.
	 * @return
	 * @throws SQLException
	 */
	public List<SpectralSearchCandidate> getCandidatesFromExperiment(long experimentID) throws SQLException {
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
	public List<SpectralSearchCandidate> getCandidatesFromExperiment(List<Interval> precIntervals, long experimentID) throws SQLException {
		ArrayList<SpectralSearchCandidate> res = new ArrayList<SpectralSearchCandidate>(precIntervals.size());
		
		// construct SQL statement
		StringBuilder sb = new StringBuilder("SELECT libspectrumid, title, precursor_mz, precursor_charge, mzarray, intarray, fk_peptideid, sequence FROM spectrum " +
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
	 * @param saveToFile 
	 * @return
	 * @throws SQLException
	 */
	public List<MascotGenericFile> downloadSpectra(long experimentID, boolean saveToFile) throws SQLException {
		List<MascotGenericFile> res = new ArrayList<MascotGenericFile>();
		
//		PreparedStatement ps = conn.prepareStatement("SELECT title, precursor_mz, precursor_int, " +
//				"precursor_charge, mzarray, intarray, chargearray, sequence FROM spectrum s " +
//				"INNER JOIN spec2pep s2p ON s.spectrumid = s2p.fk_spectrumid " +
//				"INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid " +
//				"INNER JOIN libspectrum ls ON s.spectrumid = ls.fk_spectrumid " +
//				"WHERE libspectrum.fk_experimentid = ?");
		PreparedStatement ps = conn.prepareStatement("SELECT title, precursor_mz, precursor_int, " +
				"precursor_charge, mzarray, intarray, chargearray, spectrumid FROM spectrum s " +
				"INNER JOIN libspectrum ls ON s.spectrumid = ls.fk_spectrumid " +
				"WHERE ls.fk_experimentid = ?");
		ps.setLong(1, experimentID);
		ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	MascotGenericFile mgf;
        	if (saveToFile) {
				mgf = new MascotGenericFile(rs);
        	} else {
        		mgf = new MascotGenericFile(null, null, new HashMap<Double, Double>(), 0.0, 0);
        	}
//        	mgf.setTitle(rs.getString("sequence") + " " + mgf.getTitle());	// prepend peptide sequence
        	String comments = mgf.getComments();
        	if (comments == null) {
        		comments = "";
        	}
        	comments += "#sid " + rs.getLong("spectrumid") + "\n";
        	mgf.setComments(comments);
            res.add(mgf);
        }
        rs.close();
        ps.close();
		
		return res;
	}

	/**
	 * Method to extract spectrum titles belonging to a list of specified searchspectrum IDs. 
	 * @param matches List of searchspectrum IDs.
	 * @return List of spectrum title strings.
	 * @throws SQLException
	 */
	public Map<Long, String> getSpectrumTitlesFromMatches(List<SpectrumMatch> matches) throws SQLException {
		if (matches != null && !matches.isEmpty()) {
			Map<Long, String> res = new HashMap<Long, String>(matches.size());
			
			StringBuilder sb = new StringBuilder("SELECT searchspectrumid, title FROM searchspectrum " +
					"INNER JOIN spectrum ON searchspectrum.fk_spectrumid = spectrum.spectrumid " +
					"WHERE ");
			for (SpectrumMatch match : matches) {
				if (!res.containsKey(match.getSearchSpectrumID())) {
					sb.append("searchspectrum.searchspectrumid = " + match.getSearchSpectrumID());
					sb.append(" OR ");
					res.put(match.getSearchSpectrumID(), null);
				}
			}
			for (int i = 0; i < 3; i++, sb.deleteCharAt(sb.length()-1)) {}	// remove last "OR "
			
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            res.put(rs.getLong(1), rs.getString(2));
	        }
	        rs.close();
	        ps.close();
			
			return res;
		}
		return null;
	}
	
	/**
	 * Method to extract a spectrum belonging to a specified spectrum ID.
	 * @param spectrumID The spectrum ID.
	 * @return MascotGenericFile containing the desired spectrum.
	 * @throws SQLException
	 */
	public MascotGenericFile getSpectrumBySpectrumID(long spectrumID) throws SQLException {
		MascotGenericFile res = null;
		
		PreparedStatement ps = conn.prepareStatement("SELECT title, precursor_mz, precursor_int, " +
				"precursor_charge, mzarray, intarray, chargearray FROM spectrum " +
				"WHERE spectrum.spectrumid = ?");
		ps.setLong(1, spectrumID);
		ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	res = new MascotGenericFile(rs);
        }
        rs.close();
        ps.close();
		return res;
	}
	
	/**
	 * Method to extract a spectrum belonging to a specified searchspectrum ID.
	 * @param searchspectrumID The searchspectrum ID.
	 * @return MascotGenericFile containing the desired spectrum.
	 * @throws SQLException
	 */
	public MascotGenericFile getSpectrumBySearchSpectrumID(long searchspectrumID) throws SQLException {
		MascotGenericFile res = null;
		
		PreparedStatement ps = conn.prepareStatement("SELECT title, precursor_mz, precursor_int, " +
				"precursor_charge, mzarray, intarray, chargearray FROM searchspectrum " +
				"INNER JOIN spectrum ON searchspectrum.fk_spectrumid = spectrum.spectrumid " +
				"WHERE searchspectrum.searchspectrumid = ?");
		ps.setLong(1, searchspectrumID);
		ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	res = new MascotGenericFile(rs);
        }
        rs.close();
        ps.close();
		return res;
	}
	
	/**
	 * Method to extract a spectrum belonging to a specified libspectrum ID.
	 * @param libspectrumID The libspectrum ID.
	 * @return MascotGenericFile containing the desired spectrum.
	 * @throws SQLException
	 */
	public MascotGenericFile getSpectrumByLibSpectrumID(long libspectrumID) throws SQLException {
		MascotGenericFile res = null;
		
		PreparedStatement ps = conn.prepareStatement("SELECT title, precursor_mz, precursor_int, " +
				"precursor_charge, mzarray, intarray, chargearray FROM libspectrum ls " +
				"INNER JOIN spectrum s on ls.fk_spectrumid = s.spectrumid " +
				"WHERE ls.libspectrumid = ?");
		ps.setLong(1, libspectrumID);
		ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	res = new MascotGenericFile(rs);
        }
        rs.close();
        ps.close();
		
		return res;
	}
	
}
