package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Cruxhit2prot extends Cruxhit2protTableAccessor {
	
	/**
     * This method will find the hits from the current connection, based on the specified spectrumid.
     *
     * @param aCruxHitId long with the cruxhit id
     * @param conn DB connection.
     * @return List of Crux hits.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static List<Cruxhit2protTableAccessor> getHitsFromCruxHitID(long aCruxHitId, Connection conn) throws SQLException {
    	List<Cruxhit2protTableAccessor> temp = new ArrayList<Cruxhit2protTableAccessor>();
    	PreparedStatement ps = conn.prepareStatement("select c2p.* from cruxhit2prot c2p where c2p.fk_cruxhitid = ?");
        ps.setLong(1, aCruxHitId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Cruxhit2protTableAccessor(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }

}
