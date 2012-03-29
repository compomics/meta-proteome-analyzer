package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Omssahit extends OmssahitTableAccessor implements SearchHit{
	
	private String sequence;
	private String accession;
	
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Omssahit(ResultSet aRS) throws SQLException {
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
    public static List<Omssahit> getHitsFromSpectrumID(long aSpectrumID, Connection aConn) throws SQLException {
    	List<Omssahit> temp = new ArrayList<Omssahit>();
        PreparedStatement ps = aConn.prepareStatement("select o.*, p.sequence, pr.accession from omssahit o, peptide p, protein pr, pep2prot p2p where o.fk_peptideid = p.peptideid and o.fk_peptideid = p2p.fk_peptideid and p2p.fk_proteinid = pr.proteinid and o.fk_spectrumid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Omssahit(rs));
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
}
