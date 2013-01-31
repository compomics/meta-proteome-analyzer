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
			return null;
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

//		ps = conn.prepareStatement("SELECT ssh.fk_searchspectrumid, ssh.fk_libspectrumid, ssh.similarity, " +
//				"s1.title, s1.precursor_charge, p.sequence, s2.precursor_charge, " +
//				"pr.accession, pr.description, pr.sequence FROM specsearchhit ssh " +
//				"INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid " +
//				"INNER JOIN spectrum s1 ON ss.fk_spectrumid = s1.spectrumid " +
//				"INNER JOIN libspectrum ls ON ssh.fk_libspectrumid = ls.libspectrumid " +
//				"INNER JOIN spectrum s2 ON ls.fk_spectrumid = s2.spectrumid " +
//				"INNER JOIN spec2pep s2p ON s2.spectrumid = s2p.fk_spectrumid " +
//				"INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid " +
//				"INNER JOIN pep2prot p2p ON p.peptideid = p2p.fk_peptideid " +
//				"INNER JOIN protein pr ON p2p.fk_proteinid = pr.proteinid " +
//				"WHERE ss.fk_experimentid = ? " +
//				"ORDER BY ssh.fk_libspectrumid",
//				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		ps = conn.prepareStatement("SELECT ssh.*, s1.*, p1.*, s2.*, p2.*, pr.* FROM specsearchhit ssh " +
				"INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid " +
				"INNER JOIN spectrum s1 ON ss.fk_spectrumid = s1.spectrumid " +
				"INNER JOIN spec2pep s2p1 ON s1.spectrumid = s2p1.fk_spectrumid " +
				"INNER JOIN peptide p1 ON s2p1.fk_peptideid = p1.peptideid " +
				"INNER JOIN libspectrum ls ON ssh.fk_libspectrumid = ls.libspectrumid " +
				"INNER JOIN spectrum s2 ON ls.fk_spectrumid = s2.spectrumid " +
				"INNER JOIN spec2pep s2p2 ON s2.spectrumid = s2p2.fk_spectrumid " +
				"INNER JOIN peptide p2 ON s2p2.fk_peptideid = p2.peptideid " +
				"INNER JOIN pep2prot p2p ON p2.peptideid = p2p.fk_peptideid " +
				"INNER JOIN protein pr ON p2p.fk_proteinid = pr.proteinid " +
				"WHERE ss.fk_experimentid = ?",
//				"ORDER BY ssh.fk_libspectrumid",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		ps.setLong(1, experimentID);
		ps.setFetchSize(Integer.MIN_VALUE);

		pSupport.firePropertyChange("new message", null, "FETCHING SPECTRAL SIMILARITY RESULTS");
		rs = ps.executeQuery();
		pSupport.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT");
		pSupport.firePropertyChange("indeterminate", true, false);
		pSupport.firePropertyChange("resetcur", 0L, rowCount);
		
//		int capacity = (int) Math.ceil(width / 0.75);
		Map<Long, Integer> lsID2x = new TreeMap<Long, Integer>();
		Map<Long, Integer> ssID2y = new TreeMap<Long, Integer>();
		Map<String, Integer> seq2ID = new HashMap<String, Integer>();
		int curX = 1, curY = 1, curID = 0;
		Integer rgb;

		BufferedImage tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		WritableRaster wr = tmp.getRaster();

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
					rs.getString("p2.sequence"),
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
			
			// truncate score to 8-bit integer
//			rgb = (int) (score * Integer.MAX_VALUE);
			byte[] argb = new byte[4];
			argb[1] = (byte) (score * 255);
			rgb = 0;
		    for (int i=0; i<4; i++) {
		      rgb = ( rgb << 8 ) + (int) argb[i];
		    }
			tmp.setRGB(mapX, mapY, rgb);
			
			// searchspectrum sequence
//			String ssSeq = rs.getString("s1.title");
//			ssSeq = ssSeq.substring(0, ssSeq.indexOf(" ")) + rs.getString("s1.precursor_charge");
			String ssSeq = rs.getString("p1.sequence") + rs.getString("s1.precursor_charge");
			rgb = seq2ID.get(ssSeq);
			if (rgb == null) {
				seq2ID.put(ssSeq, rgb = curID++);
			}
			tmp.setRGB(0, mapY, rgb);
			
			
			// libspectrum sequence
			String lsSeq = rs.getString("p2.sequence") + rs.getString("s2.precursor_charge");
			rgb = seq2ID.get(lsSeq);
			if (rgb == null) {
				seq2ID.put(lsSeq, rgb = curID++);
			}
			tmp.setRGB(mapX, 0, rgb);
			
//			if (score > 0.9) {
//				System.out.println(ssSeq + " " + lsSeq + " " + score);
//			}

			row++;
		}
		rs.close();
		ps.close();
		
		pSupport.firePropertyChange("progress", 0L, rowCount);
		pSupport.firePropertyChange("new message", null, "DATABASE TRANSACTION FINISHED");
		pSupport.firePropertyChange("new message", null, "REFINING SCORE MATRIX IMAGE");

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// reorder pixel rows w.r.t. search spectrum ID ordering
		int[] srcBuf = ((DataBufferInt) tmp.getRaster().getDataBuffer()).getData();
		int[] dstBuf = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		
		curY = 1;
		int[] tmpSrcBuf = new int[width];
		int[] tmpDstBuf = new int[width];
		for (int mapY : ssID2y.values()) {
			System.arraycopy(srcBuf, mapY * width, tmpSrcBuf, 0, width);
			tmpDstBuf[0] = tmpSrcBuf[0];
			// reorder pixel columns w.r.t. library spectrum ID ordering
			curX = 1;
			for (int mapX : lsID2x.values()) {
				tmpDstBuf[curX++] = tmpSrcBuf[mapX];
			}
			System.arraycopy(tmpDstBuf, 0, dstBuf, curY++ * width, width);
			pSupport.firePropertyChange("progressmade", true, false);
		}
		// don't forget top row
		curX = 1;
		for (int mapX : lsID2x.values()) {
			tmpDstBuf[curX++] = srcBuf[mapX];
		}
		System.arraycopy(tmpDstBuf, 0, dstBuf, 0, width);
		pSupport.firePropertyChange("progressmade", true, false);
		
		res.setScoreMatrixImage(img);
		
		pSupport.firePropertyChange("progress", 0L, rowCount);
		pSupport.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");
        return res;
	}
	
}
