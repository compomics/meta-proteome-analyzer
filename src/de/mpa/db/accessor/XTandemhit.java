package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.mpa.client.model.dbsearch.SearchEngineType;

public class XTandemhit extends XtandemhitTableAccessor implements SearchHit {
	private String sequence = null;
	private String accession = null;
	
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public XTandemhit(ResultSet aRS) throws SQLException {
        super(aRS);
        this.sequence = (String) aRS.getObject("sequence");
        this.accession = (String) aRS.getObject("accession");
    }
    
    /**
     * This method will find the hits from the current connection, based on the specified spectrumid.
     *
     * @param aSpectrumID long with the spectrumid of the spectrum file to find.
     * @param aConn           Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<XTandemhit> getHitsFromSpectrumID(long aSpectrumID, Connection aConn) throws SQLException {
    	List<XTandemhit> temp = new ArrayList<XTandemhit>();
        PreparedStatement ps = aConn.prepareStatement("select x.*, p.sequence, pr.accession from xtandemhit x, peptide p, protein pr where x.fk_peptideid = p.peptideid and x.fk_proteinid = pr.proteinid and x.fk_searchspectrumid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            XTandemhit hit = new XTandemhit(rs);
            temp.add(hit);	
        }
        rs.close();
        ps.close();
        return temp;
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

	public String getSequence() {
		return sequence;
	}

	public String getAccession() {
		return accession;
	}

	public long getFk_searchspectrumid() {
		return iFk_searchspectrumid;
	}
	
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
			if (hit.getType() == this.getType()) {
				if (hit.getFk_searchspectrumid() == this.getFk_searchspectrumid()) {
					if (hit.getFk_peptideid() == this.getFk_peptideid()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public double getScore() {		
		return iHyperscore.doubleValue();
	}
}
