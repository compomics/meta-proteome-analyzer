package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.mpa.client.model.dbsearch.SearchEngineType;

public class Cruxhit extends CruxhitTableAccessor implements SearchHit {
	
	private String sequence;
	private String accession;
	private long proteinid;
	
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Cruxhit(ResultSet aRS) throws SQLException {
        super(aRS);
        this.sequence = (String) aRS.getObject("sequence");
        this.accession = (String) aRS.getObject("accession");
        this.proteinid = aRS.getLong("proteinid");
    }
    
	public Cruxhit(HashMap<Object, Object> hitdata) {
		super(hitdata);
	}

	/**
     * This method will find the hits from the current connection, based on the specified spectrumid.
     *
     * @param aSpectrumID long with the spectrumid of the spectrum file to find.
     * @param conn DB connection.
     * @return List of Crux hits.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<Cruxhit> getHitsFromSpectrumID(long aSpectrumID, Connection conn) throws SQLException {
    	List<Cruxhit> temp = new ArrayList<Cruxhit>();
    	PreparedStatement ps = conn.prepareStatement("select c.*, p.sequence, pr.accession, pr.proteinid from cruxhit c, peptide p, protein pr, cruxhit2prot c2p where c.fk_peptideid = p.peptideid and c.cruxhitid = c2p.fk_cruxhitid and c2p.fk_proteinid = pr.proteinid and c.fk_searchspectrumid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Cruxhit(rs));
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
    public static List<Cruxhit> getHitsFromExperimentID(long experimentID, Connection conn) throws SQLException {
    	List<Cruxhit> temp = new ArrayList<Cruxhit>();
    	PreparedStatement ps = conn.prepareStatement("select c.*, p.sequence, pr.accession, pr.proteinid from cruxhit c, searchspectrum s, peptide p, protein pr, cruxhit2prot c2p where c.fk_peptideid = p.peptideid and c.cruxhitid = c2p.fk_cruxhitid and c2p.fk_proteinid = pr.proteinid and s.searchspectrumid = c.fk_searchspectrumid and s.fk_experimentid = ?");
        ps.setLong(1, experimentID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Cruxhit(rs));
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
	
	@Override
	public long getFk_proteinid() {
		return proteinid;
	}

	@Override
	public SearchEngineType getType() {
		return SearchEngineType.CRUX;
	}
	
	@Override
	public double getScore() {		
		return iPercolator_score.doubleValue();
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


}
