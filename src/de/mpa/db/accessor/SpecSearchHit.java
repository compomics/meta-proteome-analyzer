package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		iFk_searchspectrumid = ssm.getSpectrumId();
		iFk_libspectrumid = ssm.getLibspectrumID();
		iSimilarity = ssm.getSimilarity();
	}

//	/**
//	 * Method to query the search spectra from the database.
//	 * @return list containing the spectral similarity search hit IDs belonging to the specified experiment ID.
//	 */
//	public static List<Long> getSpecSearchHitIDsFromExperimentID(long experimentID, Connection conn) throws SQLException {
//		List<Long> res = new ArrayList<Long>();
//        PreparedStatement ps = conn.prepareStatement("SELECT specsearchhitid FROM specsearchhit ssh" +
//        		" INNER JOIN searchspectrum ss ON ssh.fk_searchspectrumid = ss.searchspectrumid" +
//        		" WHERE ss.fk_experimentid = ?");
//        ps.setLong(1, experimentID);
//        ResultSet rs = ps.executeQuery();
//        while (rs.next()) {
//            res.add(rs.getLong("specsearchhitid"));
//        }
//        rs.close();
//        ps.close();
//        return res;
//	}

	/**
	 * Method to query the search spectra from the database.
	 * @return list containing ResultSet objects.
	 */
	public static SpecSimResult getAnnotations(long experimentID, Connection conn) throws SQLException {
		SpecSimResult res = new SpecSimResult();
        PreparedStatement ps = conn.prepareStatement("SELECT specsearchhitid, ssh.fk_searchspectrumid, ssh.fk_libspectrumid, similarity, p.sequence, accession, description, pr.sequence FROM specsearchhit ssh" +
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
        			rs.getLong("fk_searchspectrumid"),
        			rs.getLong("fk_libspectrumid"),
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
	
}
