package de.mpa.db.mysql.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.mpa.model.dbsearch.SearchEngineType;

public class XTandemhit extends XtandemhitTableAccessor implements SearchHit {
	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The peptide sequence.
	 */
	private String sequence;
	
	/**
	 * The protein accession.
	 */
	private String accession;
	
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public XTandemhit(ResultSet aRS) throws SQLException {
        super(aRS);
        sequence = (String) aRS.getObject("sequence");
        accession = (String) aRS.getObject("accession");
    }
    
    /**
     * This constructor works faster by reducing the included data
     *
     * @param not_view 	Get values from xtandem table directly = true,  Get values from view = false
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public XTandemhit(ResultSet aRS, boolean now_view) throws SQLException {    	
    	if (now_view) {
            iXtandemhitid = aRS.getLong("xtandemhit.xtandemhitid");
            iFk_searchspectrumid = aRS.getLong("xtandemhit.fk_searchspectrumid");
            iFk_peptideid = aRS.getLong("xtandemhit.fk_peptideid");
            iFk_proteinid = aRS.getLong("xtandemhit.fk_proteinid");
            sequence = (String) aRS.getObject("peptide.sequence");
            accession = (String) aRS.getObject("protein.accession");
            iQvalue = (Number) aRS.getObject("xtandemhit.qvalue");
    	} else {
            iXtandemhitid = aRS.getLong("xtandemhitid");
            iFk_searchspectrumid = aRS.getLong("fk_searchspectrumid");
            iFk_peptideid = aRS.getLong("fk_peptideid");
            iFk_proteinid = aRS.getLong("fk_proteinid");
            sequence = (String) aRS.getObject("pepseq");
            accession = (String) aRS.getObject("accession");
            iQvalue = (Number ) aRS.getObject("qvalue");
    	}
	}

	/**
     * This method will find the hits from the current connection, based on the specified spectrumid.
     *
     * @param experimentID long with the experimentID.
     * @param conn DB connection.
     * @return List of Crux hits.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<XTandemhit> getHitsFromExperimentID(long experimentID, Connection conn) throws SQLException {
    	List<XTandemhit> temp = new ArrayList<XTandemhit>();
    	PreparedStatement ps = conn.prepareStatement(
    			"SELECT x.*, p.sequence, pr.accession FROM xtandemhit x, searchspectrum s, peptide p, protein pr " +
    			"WHERE x.fk_peptideid = p.peptideid " +
    			"AND x.fk_proteinid = pr.proteinid " +
    			"AND s.searchspectrumid = x.fk_searchspectrumid " +
    			"AND s.fk_experimentid = ?");
        ps.setLong(1, experimentID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new XTandemhit(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    
    /**
     * Returns the peptide sequence.
     * @return the peptide sequence
     */
	public String getSequence() {
		return this.sequence;
	}
	
	/**
	 * Returns the protein accession.
	 * @return the protein accession
	 */
	public String getAccession() {
		return this.accession;
	}
	
	/**
	 * Returns the search spectrum id.
	 * @return the search spectrum id
	 */
	public long getFk_searchspectrumid() {
		return this.iFk_searchspectrumid;
	}
	
	/**
	 * Returns the search hit charge.
	 * @return the search hit charge
	 */
	public long getCharge(){
		//TODO: Include the appropriate charge in the DB.
		return 2;
	}
	
	@Override
	public SearchEngineType getType() {
		return SearchEngineType.XTANDEM;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SearchHit) {
			SearchHit hit = ((SearchHit) obj);
			if (hit.getType() == getType()) {
				if (hit.getFk_searchspectrumid() == getFk_searchspectrumid()) {
					if (hit.getFk_peptideid() == getFk_peptideid()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public double getScore() {		
		return this.iHyperscore.doubleValue();
	}
}
