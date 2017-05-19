package de.mpa.db.mysql.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
    
	/**
     * This method will find the hits from the current connection, based on the specified proteinid.
     *
     * @param proteinid long with the proteinid.
     * @param conn DB connection.
     * @return List of xtandem hits.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<XtandemhitTableAccessor> getHitsFromProteinID(long proteinid, Connection conn) throws SQLException {
    	List<XtandemhitTableAccessor> temp = new ArrayList<XtandemhitTableAccessor>();
    	PreparedStatement ps = conn.prepareStatement("SELECT x.* FROM xtandemhit x WHERE x.fk_proteinid = ?");
        ps.setLong(1, proteinid);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new XtandemhitTableAccessor(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    
	/**
     * This method create a new xtandemhit from the data of another xtandemhit
     *
     * @param proteinid 	the proteinid from the new protein (why we copy in the first place)
     * @param hit  	the old omssahit that is copied
     * @param conn DB connection.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static void copyxtandemhit(long proteinid, XtandemhitTableAccessor hit, Connection conn) throws SQLException {
	    HashMap<Object, Object> hitdata = new HashMap<Object, Object>(17);
		hitdata.put(XtandemhitTableAccessor.FK_SEARCHSPECTRUMID, hit.getFk_searchspectrumid());  
        hitdata.put(XtandemhitTableAccessor.DOMAINID, hit.getDomainid());
        hitdata.put(XtandemhitTableAccessor.START, hit.getStart());
        hitdata.put(XtandemhitTableAccessor.END, hit.getEnd());
        hitdata.put(XtandemhitTableAccessor.EVALUE, hit.getEvalue());
        hitdata.put(XtandemhitTableAccessor.DELTA, hit.getDelta());
        hitdata.put(XtandemhitTableAccessor.HYPERSCORE, hit.getHyperscore());                
        hitdata.put(XtandemhitTableAccessor.PRE, hit.getPre());
        hitdata.put(XtandemhitTableAccessor.POST, hit.getPost());                
        hitdata.put(XtandemhitTableAccessor.MISSCLEAVAGES, hit.getMisscleavages());
        hitdata.put(XtandemhitTableAccessor.PEP, hit.getPep());
        hitdata.put(XtandemhitTableAccessor.QVALUE, hit.getQvalue());
    	hitdata.put(XtandemhitTableAccessor.FK_PEPTIDEID, hit.getFk_peptideid());
    	hitdata.put(XtandemhitTableAccessor.FK_PROTEINID, proteinid);
   	    XtandemhitTableAccessor xtandemhit = new XtandemhitTableAccessor(hitdata);        
        xtandemhit.persist(conn);
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
