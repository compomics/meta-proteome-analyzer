package de.mpa.db.mysql.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.mpa.model.dbsearch.SearchEngineType;

public class Omssahit extends OmssahitTableAccessor implements SearchHit {
	
	private static final long serialVersionUID = 1L;
	private final String sequence;
	private final String accession;
	
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Omssahit(ResultSet aRS) throws SQLException {
        super(aRS);
        sequence = (String) aRS.getObject("sequence");
        accession = (String) aRS.getObject("accession");
    }
    
    /**
     * This constructor works faster by reducing the included data
     *
     * @param aRS ResultSet to read the data from.
     * @param not_view 	Get values from omssa table directly = true,  Get values from view = false 
     * @throws SQLException when reading the ResultSet failed.
     */
    public Omssahit(ResultSet aRS, boolean not_view) throws SQLException {
    	if (not_view) {
            iOmssahitid = aRS.getLong("omssahit.omssahitid");
            iFk_searchspectrumid = aRS.getLong("omssahit.fk_searchspectrumid");
            iFk_peptideid = aRS.getLong("omssahit.fk_peptideid");
            iFk_proteinid = aRS.getLong("omssahit.fk_proteinid");
            iCharge = aRS.getLong("omssahit.charge");
            sequence = (String) aRS.getObject("peptide.sequence");
            accession = (String) aRS.getObject("protein.accession");
            iQvalue = (Number ) aRS.getObject("omssahit.qvalue");
    	} else {
            iOmssahitid = aRS.getLong("omssahitid");
            iFk_searchspectrumid = aRS.getLong("fk_searchspectrumid");
            iFk_peptideid = aRS.getLong("fk_peptideid");
            iFk_proteinid = aRS.getLong("fk_proteinid");
            iCharge = aRS.getLong("charge");
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
     * @return List of OMSSA hits.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<Omssahit> getHitsFromExperimentID(long experimentID, Connection conn) throws SQLException {
    	List<Omssahit> temp = new ArrayList<Omssahit>();
    	PreparedStatement ps = conn.prepareStatement("select o.*, p.sequence, pr.accession " +
    												 "from omssahit o, searchspectrum s, peptide p, protein pr " +
    												 "where o.fk_peptideid = p.peptideid " +
    												 "and o.fk_proteinid = pr.proteinid " +
    												 "and s.searchspectrumid = o.fk_searchspectrumid " +
    												 "and s.fk_experimentid = ?");
    	
        ps.setLong(1, experimentID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Omssahit(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    
    

	public String getSequence() {
		return this.sequence;
	}

	public String getAccession() {
		return this.accession;
	}
	
	@Override
	public SearchEngineType getType() {
		return SearchEngineType.OMSSA;
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
		return this.iEvalue.doubleValue();
	}
}
