package de.mpa.db.extractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.mpa.client.Client;
import de.mpa.db.DBConfiguration;
import de.mpa.io.MascotGenericFile;

/**
 * SpectrumUtilities class is mainly used to access the database and retrieve unidentified and identified spectra.
 * 
 * @author T. Muth
 */
public class SpectrumUtilities {
	
	/**
	 * The database connection instance.
	 */
	private Connection conn;
	
	/**
	 * Sets the database connection.
	 * @throws SQLException 
	 */
	public void initDBConnection() throws SQLException {
		// Connection conn
		if (conn == null || !conn.isValid(0)) {
			// connect to database
			DBConfiguration dbconfig = new DBConfiguration(Client.getInstance().getConnectionParameters());
			this.conn = dbconfig.getConnection();
		}
	}
	
	/**
	 * Writes a list of MascoGenericFile spectra to a file.
	 * @param spectra List of MascotGenericFile spectra.
	 * @param filePath File path.
	 */
	public static void writeToFile(List<MascotGenericFile> spectra, String filePath) {
		try {
			File file = new File(filePath);
			FileOutputStream fos = new FileOutputStream(file);
			for (MascotGenericFile mgf : spectra) {
				mgf.writeToStream(fos);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to download database spectra belonging to a specific experiment.
	 * @param experimentID
	 * @return
	 * @throws SQLException
	 */
	public List<MascotGenericFile> getAllSpectra(long experimentID) throws SQLException {
		List<MascotGenericFile> res = new ArrayList<MascotGenericFile>();
		
		PreparedStatement ps = conn.prepareStatement("SELECT spectrumid, title, precursor_mz, precursor_int, " +
				"precursor_charge, mzarray, intarray, chargearray FROM spectrum " +
				"INNER JOIN searchspectrum on spectrum.spectrumid = searchspectrum.fk_spectrumid " +
				"WHERE searchspectrum.fk_experimentid = ?");
		ps.setLong(1, experimentID);
		ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	MascotGenericFile mgf = new MascotGenericFile(rs);
        	mgf.setFilename("Spectrum" + rs.getInt("spectrumid") + ".mgf");
        	mgf.setTitle(mgf.getTitle());
            res.add(mgf);
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
	public List<MascotGenericFile> getIdentifiedSpectra(long experimentID, long limit, long length) throws SQLException {
		List<MascotGenericFile> res = new ArrayList<MascotGenericFile>();
		
		PreparedStatement ps = conn.prepareStatement("SELECT searchspectrum.searchspectrumid, spectrumid, title, precursor_mz, precursor_int, " +
				"precursor_charge, mzarray, intarray, chargearray FROM spectrum " +
				"INNER JOIN searchspectrum on spectrum.spectrumid = searchspectrum.fk_spectrumid " +
				"WHERE searchspectrum.fk_experimentid = ? " + 
				"LIMIT ?, ?");
		ps.setLong(1, experimentID);
		ps.setLong(2, limit);
		ps.setLong(3, length);
		SearchHitExtractor.findSearchHitsFromExperimentID(experimentID, conn);
		
		ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	MascotGenericFile mgf = new MascotGenericFile(rs);
        	if(SearchHitExtractor.MAP.get(rs.getLong("searchspectrumid")) != null) {
        		mgf.setFilename("Spectrum" + rs.getInt("spectrumid") + ".mgf");
            	mgf.setTitle(mgf.getTitle());
                res.add(mgf);
        	}
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
	public List<MascotGenericFile> getUnIdentifiedSpectra(long experimentID, long start, long length) throws SQLException {
		List<MascotGenericFile> res = new ArrayList<MascotGenericFile>();
		
		PreparedStatement ps = conn.prepareStatement("SELECT searchspectrum.searchspectrumid, spectrumid, title, precursor_mz, precursor_int, " +
				"precursor_charge, mzarray, intarray, chargearray FROM spectrum " +
				"INNER JOIN searchspectrum on spectrum.spectrumid = searchspectrum.fk_spectrumid " +
				"WHERE searchspectrum.fk_experimentid = ? " + 
				"LIMIT ?, ?");
		
		ps.setLong(1, experimentID);
		ps.setLong(2, start);
		ps.setLong(3, length);
		SearchHitExtractor.findSearchHitsFromExperimentID(experimentID, conn);
		
		ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	MascotGenericFile mgf = new MascotGenericFile(rs);
        	if(SearchHitExtractor.MAP.get(rs.getLong("searchspectrumid")) == null) {
        		mgf.setFilename("Spectrum" + rs.getInt("spectrumid") + ".mgf");
            	mgf.setTitle(mgf.getTitle());
                res.add(mgf);
        	}
        }
        rs.close();
        ps.close();
		
		return res;
	}
	
	public static void main(String[] args) {
		SpectrumUtilities spectrumUtils = new SpectrumUtilities();
		try {
			spectrumUtils.initDBConnection();
			//SpectrumUtilities.writeToFile(spectrumUtils.getAllSpectra(21), "/home/muth/PersonalFolder/Metaproteomics/Review/all");
			SpectrumUtilities.writeToFile(spectrumUtils.getIdentifiedSpectra(33, 160000, 10000), "/home/muth/PersonalFolder/Metaproteomics/Review/id");
			SpectrumUtilities.writeToFile(spectrumUtils.getUnIdentifiedSpectra(33, 160000, 10000), "/home/muth/PersonalFolder/Metaproteomics/Review/nonid");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
