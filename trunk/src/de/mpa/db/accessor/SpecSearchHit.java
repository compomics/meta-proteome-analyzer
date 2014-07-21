package de.mpa.db.accessor;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

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
	 * @param experimentId The experiment ID.
	 * @param conn The database connection.
	 * @return Spectral similarity search results object.
	 * @throws Exception 
	 */
	public static SpecSimResult getAnnotations(long experimentId, Connection conn) throws Exception {
		return getAnnotations(experimentId, conn, new PropertyChangeSupport(null));
	}

	/**
	 * Builds spectral similarity search results object from SQL query.
	 * @param experimentId The experiment ID.
	 * @param conn The database connection.
	 * @param pSupport The client's property change support to listen for progress updates.
	 * @return Spectral similarity search results object.
	 * @throws Exception 
	 */
	public static SpecSimResult getAnnotations(long experimentId, Connection conn, PropertyChangeSupport pSupport) throws Exception {
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
		ps.setLong(1, experimentId);
		
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
				"SELECT ssh.fk_libspectrumid, ssh.fk_searchspectrumid FROM specsearchhit ssh " +
				"INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid " +
				"WHERE ss.fk_experimentid = ?",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		ps.setLong(1, experimentId);
		ps.setFetchSize(Integer.MIN_VALUE);
		
		rs = ps.executeQuery();
		Set<Long> lsIDset = new TreeSet<Long>(), ssIDset = new TreeSet<Long>();
		while (rs.next()) {
			Long lsID = rs.getLong("ssh.fk_libspectrumid");
			Long ssID = rs.getLong("ssh.fk_searchspectrumid");
			lsIDset.add(lsID);
			ssIDset.add(ssID);
		}
		ps.close();
		List<Long> lsIDs = new ArrayList<Long>(lsIDset);
		List<Long> ssIDs = new ArrayList<Long>(ssIDset);
		lsIDset = ssIDset = null;
		int width = lsIDs.size() + 1, height = ssIDs.size() + 1;
		
		Collections.sort(lsIDs);
		Collections.sort(ssIDs);
		
		// Query annotations
		pSupport.firePropertyChange("resetall", 0L, rowCount);
		pSupport.firePropertyChange("resetcur", 0L, rowCount);

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
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		ps.setLong(1, experimentId);
		ps.setFetchSize(Integer.MIN_VALUE);

		pSupport.firePropertyChange("new message", null, "FETCHING SPECTRAL SIMILARITY RESULTS");
		rs = ps.executeQuery();
		pSupport.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT");
		pSupport.firePropertyChange("indeterminate", true, false);
		pSupport.firePropertyChange("resetcur", 0L, rowCount);
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		int row = 1;
		int seqID = 0;
		Map<String, Integer> seqIDs = new HashMap<String, Integer>();
		Set<Long> lsIDs2 = new TreeSet<Long>();
		Set<Long> ssIDs2 = new TreeSet<Long>();
		while (rs.next()) {
			if ((row % hundredth) == 0) {
				pSupport.firePropertyChange("progress", 0L, row);
			}
			row++;
			
			Long lsID = rs.getLong("ssh.fk_libspectrumid");
			Long ssID = rs.getLong("ssh.fk_searchspectrumid");
			double score = rs.getDouble("ssh.similarity");
			
			lsIDs2.add(lsID);
			ssIDs2.add(ssID);
			
			/* result object part  */
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
					rs.getString("pr.sequence"), peptideHit, null, null, experimentId);
			res.addProtein(proteinHit);
			
			/* score matrix image part */
			int x = Collections.binarySearch(lsIDs, lsID);
			if (x < 0) {
				System.out.println("libspectrumid " + lsID.longValue() + " not found");
			}
			int y = Collections.binarySearch(ssIDs, ssID);
			if (y < 0) {
				System.out.println("libspectrumid " + ssID.longValue() + " not found");
			}
			// encode similarity score as 8-bit integer inside red color channel
			Integer rgb = ((int) (score * 255.0) << 16) + (255 << 24);
			img.setRGB(x + 1, y + 1, rgb);
			// encode peptide IDs using indexes
			String ssSeq = rs.getString("p1.sequence") + rs.getString("s1.precursor_charge");
			rgb = seqIDs.get(ssSeq);
			if (rgb == null) {
				seqIDs.put(ssSeq, rgb = seqID++);
			}
			img.setRGB(0, y + 1, rgb);
			String lsSeq = rs.getString("p2.sequence") + rs.getString("s2.precursor_charge");
			rgb = seqIDs.get(lsSeq);
			if (rgb == null) {
				seqIDs.put(lsSeq, rgb = seqID++);
			}
			img.setRGB(x + 1, 0, rgb);
		}
		
		res.setScoreMatrixImage(img);
		
		pSupport.firePropertyChange("progress", 0L, rowCount);
		pSupport.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");
        return res;
	}
	
	/**
	 * Inserts the specified list of spectrum-spectrum matches into the remote
	 * database in batches of the specified size.
	 * @param data the list of matches
	 * @param batchSize the batch size
	 * @param conn the database connection
	 * @return an array of update counts containing one element for each command in the batch
	 * @throws SQLException if a database access error occurs
	 */
	public static int[] batchPersist(List<SpectrumSpectrumMatch> data, int batchSize, Connection conn) throws SQLException {
		int[] res = new int[0];
		
		// cache auto-commit flag
		boolean auto = conn.getAutoCommit();
		// disable auto-commit for batch insertion
		conn.setAutoCommit(false);
		
		// prepare insert statement
		PreparedStatement ps = conn.prepareStatement(
				"INSERT INTO specsearchhit (specsearchhitid, fk_searchspectrumid, fk_libspectrumid, similarity, creationdate, modificationdate) "
				+ "values(?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

		// iterate match data
		int count = 0;
		for (SpectrumSpectrumMatch ssm: data) {
			// set parameters
			ps.setNull(1, 4);
			ps.setLong(2, ssm.getSearchSpectrumID());
			ps.setLong(3, ssm.getLibSpectrumID());
			ps.setObject(4, ssm.getSimilarity());
			// add set of parameters to batch
			ps.addBatch();
			// execute batch every [batchSize] elements
			if ((++count % batchSize) == 0) {
				int[] batchRes = ps.executeBatch();
				conn.commit();
				res = ArrayUtils.addAll(res, batchRes);
			}
		}
		// execute remaining batch commands
		int[] batchRes = ps.executeBatch();
		conn.commit();
		res = ArrayUtils.addAll(res, batchRes);
		ps.close();
		
		// restore auto-commit flag
		conn.setAutoCommit(auto);
		
		return res;
	}
	
}
