package de.mpa.db.accessor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.specsim.SpecSimResult;
import de.mpa.client.model.specsim.SpectrumSpectrumMatch;

public class SpecSearchHit extends SpecsearchhitTableAccessor {
	
	/**
	 * Class constructor using a spectrum-to-spectrum match object.
	 * @param ssm
	 */
	public SpecSearchHit(SpectrumSpectrumMatch ssm) {
		iFk_searchspectrumid = ssm.getSearchSpectrumID();
		iFk_libspectrumid = ssm.getLibSpectrumID();
		iSimilarity = ssm.getSimilarity();
	}

	/**
	 * Builds spectral similarity search results object from SQL query.
	 * @param experimentID The experiment ID.
	 * @param conn The database connection.
	 * @return Spectral similarity search results object.
	 * @throws SQLException
	 */
	public static SpecSimResult getAnnotations(long experimentID, Connection conn) throws SQLException {
		return getAnnotations(experimentID, conn, new PropertyChangeSupport(null));
	}

	/**
	 * Builds spectral similarity search results object from SQL query.
	 * @param experimentID The experiment ID.
	 * @param conn The database connection.
	 * @param pSupport The client's property change support to listen for progress updates.
	 * @return Spectral similarity search results object.
	 * @throws SQLException
	 */
	public static SpecSimResult getAnnotations(long experimentID, Connection conn, PropertyChangeSupport pSupport) throws SQLException {
		SpecSimResult res = new SpecSimResult();

		pSupport.firePropertyChange("new message", null, "PREPARING TO FETCH RESULTS");
		pSupport.firePropertyChange("resetall", 0L, 100L);
		pSupport.firePropertyChange("indeterminate", false, true);
		// Determine total row count
		long rowCount = 0L;
		PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM specsearchhit ssh " +
        		"INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid " +
        		"INNER JOIN libspectrum ls ON ssh.fk_libspectrumid = ls.libspectrumid " +
        		"INNER JOIN spectrum s2 ON ls.fk_spectrumid = s2.spectrumid " +
        		"INNER JOIN spec2pep s2p ON s2.spectrumid = s2p.fk_spectrumid " +
				"INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid " +
				"INNER JOIN pep2prot p2p ON p.peptideid = p2p.fk_peptideid " +
        		"INNER JOIN protein pr ON p2p.fk_proteinid = pr.proteinid " +
        		"WHERE ss.fk_experimentid = ?");
		ps.setLong(1, experimentID);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		rowCount += rs.getLong(1);
		int hundredth = (int) Math.ceil(rowCount / 100.0);
		rs.close();
		ps.close();
		
		// Abort prematurely if result set was empty
		if (rowCount == 0L) {
			pSupport.firePropertyChange("indeterminate", true, false);
			pSupport.firePropertyChange("progress", 0L, rowCount);
			pSupport.firePropertyChange("new message", null, "FETCHING RESULTS FAILED (EMPTY RESULTSET)");
			return res;
		}
		
		// determine image dimensions
		ps = conn.prepareStatement(
				"SELECT COUNT(DISTINCT ssh.fk_libspectrumid), COUNT(DISTINCT ssh.fk_searchspectrumid) FROM specsearchhit ssh " +
				"INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid " +
				"WHERE ss.fk_experimentid = ?");
		ps.setLong(1, experimentID);
		rs = ps.executeQuery();
		int width = 0, height = 0;
		if (rs.next()) {
			width = rs.getInt(1) + 1;
			height = rs.getInt(2) + 1;
	        rs.close();
	        ps.close();
		}
		
		// Query annotations
		pSupport.firePropertyChange("resetall", 0L, rowCount);
		pSupport.firePropertyChange("resetcur", 0L, rowCount);

		ps = conn.prepareStatement("SELECT ssh.fk_searchspectrumid, ssh.fk_libspectrumid, ssh.similarity, p.sequence, " +
				"pr.accession, pr.description, pr.sequence FROM specsearchhit ssh " +
				"INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid " +
				"INNER JOIN spectrum s1 ON ss.fk_spectrumid = s1.spectrumid " +
				"INNER JOIN libspectrum ls ON ssh.fk_libspectrumid = ls.libspectrumid " +
				"INNER JOIN spectrum s2 ON ls.fk_spectrumid = s2.spectrumid " +
				"INNER JOIN spec2pep s2p ON s2.spectrumid = s2p.fk_spectrumid " +
				"INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid " +
				"INNER JOIN pep2prot p2p ON p.peptideid = p2p.fk_peptideid " +
				"INNER JOIN protein pr ON p2p.fk_proteinid = pr.proteinid " +
				"WHERE ss.fk_experimentid = ? " +
				"ORDER BY ssh.fk_libspectrumid",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		ps.setLong(1, experimentID);
		ps.setFetchSize(Integer.MIN_VALUE);

		pSupport.firePropertyChange("new message", null, "FETCHING SPECTRAL SIMILARITY RESULTS");
		rs = ps.executeQuery();
		pSupport.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT");
		pSupport.firePropertyChange("indeterminate", true, false);
		pSupport.firePropertyChange("resetcur", 0L, rowCount);
		
		Map<Long, Integer> lsID2x = new HashMap<Long, Integer>(width - 1);
		Map<Long, Integer> ssID2y = new TreeMap<Long, Integer>();
		Map<String, Integer> seq2ID = new HashMap<String, Integer>();
		int curX = 1, curY = 1, curID = 0;
		Integer rgb;

		BufferedImage tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		System.out.println("" + width + " " + height);
		long row = 1;
		while (rs.next()) {
			if ((row % hundredth) == 0) {
				pSupport.firePropertyChange("progress", 0L, row);
			}
			Long lsID = rs.getLong("ssh.fk_libspectrumid");
			Long ssID = rs.getLong("ssh.fk_searchspectrumid");
			Double score = rs.getDouble("ssh.similarity");
		// --- result object part ---
			SpectrumSpectrumMatch ssm = new SpectrumSpectrumMatch(
					ssID,
					lsID,
					score);
			PeptideHit peptideHit = new PeptideHit(
					rs.getString("p.sequence"),
					ssm);
			ProteinHit proteinHit = new ProteinHit(
					rs.getString("pr.accession"),
					rs.getString("pr.description"),
					rs.getString("pr.sequence"), peptideHit);
			res.addProtein(proteinHit);
			
		// --- score matrix image part ---
			// x coordinate lookup
			Integer mapX = lsID2x.get(lsID);
			if (mapX == null) {
				lsID2x.put(lsID, mapX = curX++);
			}
			
			// y coordinate lookup
			Integer mapY = ssID2y.get(ssID);
			if (mapY == null) {
				ssID2y.put(ssID, mapY = curY++);
			}
			
			// truncate score to 32-bit integer
			rgb = (int) (score * Integer.MAX_VALUE);
			tmp.setRGB(mapX, mapY, rgb);
			
			// search spectrum sequence
			String ssSeq = rs.getString(4);
			ssSeq = ssSeq.substring(0, ssSeq.indexOf(" ")) + rs.getString(5);
			rgb = seq2ID.get(ssSeq);
			if (rgb == null) {
				seq2ID.put(ssSeq, rgb = curID++);
			}
			tmp.setRGB(0, mapY, rgb);
			
			// lib spectrum sequence
			String lsSeq = rs.getString(6) + rs.getString(7);
			rgb = seq2ID.get(lsSeq);
			if (rgb == null) {
				seq2ID.put(lsSeq, rgb = curID++);
			}
			tmp.setRGB(mapX, 0, rgb);

			row++;
		}
		rs.close();
		ps.close();

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// reorder pixel rows w.r.t. search spectrum ID ordering
		int[] srcBuf = ((DataBufferInt) tmp.getRaster().getDataBuffer()).getData();
		int[] dstBuf = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		curY = 1;
		for (int mapY : ssID2y.values()) {
			System.arraycopy(srcBuf, mapY * width, dstBuf, curY++ * width, width);
		}
		// don't forget top row
		System.arraycopy(srcBuf, 0, dstBuf, 0, width);
		
		res.setScoreMatrixImage(img);
		
		pSupport.firePropertyChange("progress", 0L, rowCount);
		pSupport.firePropertyChange("new message", null, "DATABASE TRANSACTION FINISHED");
        return res;
	}
	
	public static BufferedImage getScoreMatrixImage(long experimentID, Connection conn) throws SQLException {
		BufferedImage res = null;
		// determine image dimensions
		PreparedStatement ps = conn.prepareStatement(
				"SELECT COUNT(DISTINCT ssh.fk_libspectrumid), COUNT(DISTINCT ssh.fk_searchspectrumid) FROM specsearchhit ssh " +
				"INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid " +
				"WHERE ss.fk_experimentid = ?");
		ps.setLong(1, experimentID);
        long startTime = System.currentTimeMillis();
        System.out.print("Querying image dimensions ");
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			int width = rs.getInt(1) + 1;
			int height = rs.getInt(2) + 1;
	        System.out.println("[" + width + ", " + height + "] took " + (System.currentTimeMillis() - startTime) + "ms");
	        rs.close();
	        ps.close();

			res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			
			ps = conn.prepareStatement(
//					"SELECT ssh.fk_libspectrumid, ssh.fk_searchspectrumid, ssh.similarity, s1.title, s1.precursor_charge, p.sequence, s2.precursor_charge FROM specsearchhit ssh " +
					"SELECT ssh.fk_libspectrumid, ssh.fk_searchspectrumid, ssh.similarity FROM specsearchhit ssh " +
					"INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid " +
//					"INNER JOIN spectrum s1 ON ss.fk_spectrumid = s1.spectrumid " +
//					"INNER JOIN libspectrum ls ON ssh.fk_libspectrumid = ls.libspectrumid " +
//					"INNER JOIN spectrum s2 ON ls.fk_spectrumid = s2.spectrumid " +
//					"INNER JOIN spec2pep s2p ON s2.spectrumid = s2p.fk_spectrumid " +
//					"INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid " + 
					"WHERE ss.fk_experimentid = ? " +
//					"GROUP BY ssh.fk_libspectrumid, ssh.fk_searchspectrumid");
					"ORDER BY ssh.fk_libspectrumid",
	        		ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        ps.setLong(1, experimentID);
	        ps.setFetchSize(Integer.MIN_VALUE);
			startTime = System.currentTimeMillis();
	        System.out.print("Querying scores ");
			rs = ps.executeQuery();
	        System.out.println("took " + (System.currentTimeMillis() - startTime) + "ms");
			
			Map<Long, Integer> lsID2x = new HashMap<Long, Integer>(width - 1);
			Map<Long, Integer> ssID2y = new TreeMap<Long, Integer>();
			Map<String, Integer> seq2ID = new HashMap<String, Integer>();
			int curX = 1, curY = 1, curID = 0;
			Integer rgb;
			
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			// iterate result set, paint pixels
	        startTime = System.currentTimeMillis();
	        System.out.print("Building image ");
			while (rs.next()) {
				// x coordinate lookup
				Long lsID = rs.getLong(1);
				Integer mapX = lsID2x.get(lsID);
				if (mapX == null) {
					lsID2x.put(lsID, mapX = curX++);
				}
				
				// y coordinate lookup
				Long ssID = rs.getLong(2);
				Integer mapY = ssID2y.get(ssID);
				if (mapY == null) {
					ssID2y.put(ssID, mapY = curY++);
				}
				
				// score
				Double score = rs.getDouble(3);
				// truncate score to 32-bit integer
				rgb = (int) (score * Integer.MAX_VALUE);
				img.setRGB(mapX, mapY, rgb);
				
				// search spectrum sequence
				String ssSeq = rs.getString(4);
				ssSeq = ssSeq.substring(0, ssSeq.indexOf(" ")) + rs.getString(5);
				rgb = seq2ID.get(ssSeq);
				if (rgb == null) {
					seq2ID.put(ssSeq, rgb = curID++);
				}
				img.setRGB(0, mapY, rgb);
				
				// lib spectrum sequence
				String lsSeq = rs.getString(6) + rs.getString(7);
				rgb = seq2ID.get(lsSeq);
				if (rgb == null) {
					seq2ID.put(lsSeq, rgb = curID++);
				}
				img.setRGB(mapX, 0, rgb);
			}

			// reorder pixel rows w.r.t. search spectrum ID ordering
			int[] srcBuf = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
			int[] dstBuf = ((DataBufferInt) res.getRaster().getDataBuffer()).getData();
			curY = 1;
			for (int mapY : ssID2y.values()) {
				System.arraycopy(srcBuf, mapY * width, dstBuf, curY++ * width, width);
			}
			// don't forget top row
			System.arraycopy(srcBuf, 0, dstBuf, 0, width);

			System.out.println("took " + (System.currentTimeMillis() - startTime) + "ms");
		} else {
			System.out.println("failed");
		}
		return res;
	}
	
}
