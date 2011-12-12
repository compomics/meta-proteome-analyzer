package de.mpa.db.accessor;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Pepnovohit extends PepnovohitTableAccessor {

	private String sequence;
	
    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Pepnovohit(ResultSet aRS) throws SQLException {
        super(aRS);
        this.sequence = (String) aRS.getObject("sequence");
    }
    
    public static List<Pepnovohit> getHitsFromSpectrumID(long aSpectrumID, Connection aConn) throws SQLException {
    	List<Pepnovohit> temp = new ArrayList<Pepnovohit>();
    	PreparedStatement ps = aConn.prepareStatement("select d.*, p.sequence from pepnovohit d, peptide p where d.fk_peptideid = p.peptideid and d.fk_spectrumid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp.add(new Pepnovohit(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }
    

	public String getSequence() {
		return sequence;
	}
}

