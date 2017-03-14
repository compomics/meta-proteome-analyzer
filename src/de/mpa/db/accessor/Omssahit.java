package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.mpa.client.model.dbsearch.SearchEngineType;

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
     * @param aSpectrumID long with the spectrumid of the spectrum file to find.
     * @param aConn           Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<Omssahit> getHitsFromSpectrumID(long aSpectrumID, Connection aConn) throws SQLException {
    	List<Omssahit> temp = new ArrayList<Omssahit>();
        PreparedStatement ps = aConn.prepareStatement("select o.*, p.sequence, pr.accession from omssahit o, peptide p, protein pr where o.fk_peptideid = p.peptideid and o.fk_proteinid = pr.proteinid and o.fk_searchspectrumid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Omssahit(rs));
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
    
	/**
     * This method will find the hits from the current connection, based on the specified proteinid.
     *
     * @param proteinid long with the proteinID.
     * @param conn DB connection.
     * @return List of OMSSA hits.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<OmssahitTableAccessor> getHitsFromProteinid(long proteinid, Connection conn) throws SQLException {
    	List<OmssahitTableAccessor> temp = new ArrayList<OmssahitTableAccessor>();
    	PreparedStatement ps = conn.prepareStatement("select o.* from omssahit o where o.fk_proteinid = ?");
        ps.setLong(1, proteinid);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new OmssahitTableAccessor(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    
	/**
     * This method create a new omssahit from the data of another omssahit
     *
     * @param proteinid 	the proteinid from the new protein (why we copy in the first place)
     * @param hit  	the old omssahit that is copied
     * @param conn DB connection.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static void copyomssahit(long proteinid, OmssahitTableAccessor hit, Connection conn) throws SQLException {
    	HashMap<Object, Object> hitdata = new HashMap<Object, Object>(16); 
    	hitdata.put(OmssahitTableAccessor.FK_SEARCHSPECTRUMID, hit.getFk_searchspectrumid());
		hitdata.put(OmssahitTableAccessor.HITSETNUMBER,	hit.getHitsetnumber());
		hitdata.put(OmssahitTableAccessor.EVALUE, hit.getEvalue());
		hitdata.put(OmssahitTableAccessor.PVALUE, hit.getPvalue());
		hitdata.put(OmssahitTableAccessor.CHARGE, hit.getCharge());
		hitdata.put(OmssahitTableAccessor.MASS,	hit.getMass());
		hitdata.put(OmssahitTableAccessor.THEOMASS,	hit.getTheomass());
		hitdata.put(OmssahitTableAccessor.START, hit.getStart());
		hitdata.put(OmssahitTableAccessor.END, hit.getEnd());
		hitdata.put(OmssahitTableAccessor.PEP, hit.getPep());
		hitdata.put(OmssahitTableAccessor.QVALUE, hit.getQvalue());
		hitdata.put(OmssahitTableAccessor.FK_PEPTIDEID,	hit.getFk_peptideid());
		hitdata.put(OmssahitTableAccessor.FK_PROTEINID,	proteinid);
		OmssahitTableAccessor omssahit = new OmssahitTableAccessor(hitdata);
		omssahit.persist(conn);
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
