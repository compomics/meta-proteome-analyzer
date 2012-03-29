package de.mpa.db.accessor;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inspecthit extends InspecthitTableAccessor implements SearchHit{
	
	private String sequence;
	private String accession;
	
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Inspecthit(ResultSet aRS) throws SQLException {
        super(aRS);
        this.sequence = (String) aRS.getObject("sequence");
        this.accession = (String) aRS.getObject("accession");
    }
    
	public Inspecthit(HashMap<Object, Object> hitdata) {
		super(hitdata);
	}

	/**
     * This method will find the hits from the current connection, based on the specified spectrumid.
     *
     * @param aSpectrumID long with the spectrumid of the spectrum file to find.
     * @param aConn           Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<Inspecthit> getHitsFromSpectrumID(long aSpectrumID, Connection aConn) throws SQLException {
    	List<Inspecthit> temp = new ArrayList<Inspecthit>();
    	PreparedStatement ps = aConn.prepareStatement("select i.*, p.sequence, pr.accession from inspecthit i, peptide p, protein pr, pep2prot p2p where i.fk_peptideid = p.peptideid and i.fk_peptideid = p2p.fk_peptideid and p2p.fk_proteinid = pr.proteinid and i.fk_spectrumid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Inspecthit(rs));
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

	public Number getQvalue() {
		return iP_value;
	}
}
