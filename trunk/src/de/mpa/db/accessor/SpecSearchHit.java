package de.mpa.db.accessor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	 * Method to query the search spectra from the database.
	 * @return list containing ResultSet objects.
	 */
	public static SpecSimResult getAnnotations(long experimentID, Connection conn) throws SQLException {
		SpecSimResult res = new SpecSimResult();
        PreparedStatement ps = conn.prepareStatement("SELECT ssh.fk_searchspectrumid, ssh.fk_libspectrumid, similarity, p.sequence, accession, description, pr.sequence FROM specsearchhit ssh" +
        		" INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid" +
        		" INNER JOIN libspectrum ls ON ssh.fk_libspectrumid = ls.libspectrumid" +
        		" INNER JOIN spectrum s ON ls.fk_spectrumid = s.spectrumid" +
        		" INNER JOIN spec2pep s2p ON s.spectrumid = s2p.fk_spectrumid" +
        		" INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid" +
        		" INNER JOIN pep2prot p2p ON p.peptideid = p2p.fk_peptideid" +
        		" INNER JOIN protein pr ON p2p.fk_proteinid = pr.proteinid" +
        		" WHERE ss.fk_experimentid = ?");
        ps.setLong(1, experimentID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
        	SpectrumSpectrumMatch ssm = new SpectrumSpectrumMatch(
        			rs.getLong("ssh.fk_searchspectrumid"),
        			rs.getLong("ssh.fk_libspectrumid"),
        			rs.getDouble("similarity"));
        	PeptideHit peptideHit = new PeptideHit(
        			rs.getString("p.sequence"),
        			ssm);
        	ProteinHit proteinHit = new ProteinHit(
        			rs.getString("accession"),
        			rs.getString("description"),
        			rs.getString("pr.sequence"), peptideHit);
        	res.addProtein(proteinHit);
        }
        rs.close();
        ps.close();
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
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			int width = rs.getInt(1) + 1;
			int height = rs.getInt(2) + 1;

			res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			
			ps = conn.prepareStatement(
					"SELECT fk_libspectrumid, fk_searchspectrumid, similarity FROM specsearchhit ssh " +
					"INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid " +
					"WHERE ss.fk_experimentid = ? " +
					"ORDER BY fk_libspectrumid");
			ps.setLong(1, experimentID);
			rs = ps.executeQuery();
			
			Map<Long, Integer> lsID2x = new TreeMap<Long, Integer>();
			Map<Long, Integer> ssID2y = new TreeMap<Long, Integer>();
			int curX = 1, curY = 1;
			
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			// iterate result set, paint pixels
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
				// truncate score to 32-bit float precision, convert bytes to integer
				byte[] bytes = new byte[4];
		        ByteBuffer buf = ByteBuffer.wrap(bytes);
		        buf.putFloat(score.floatValue());
				// set pixel color
				img.setRGB(mapX, mapY, buf.getInt(0));
			}

			// reorder pixel rows w.r.t. search spectrum ID ordering
			int[] srcBuf = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
			int[] dstBuf = ((DataBufferInt) res.getRaster().getDataBuffer()).getData();
			curY = 1;
			for (int mapY : ssID2y.values()) {
				System.arraycopy(srcBuf, mapY * width, dstBuf, curY++ * width, width);
			}
		}
		return res;
	}
	
}
