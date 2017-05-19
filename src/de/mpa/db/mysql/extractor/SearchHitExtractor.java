package de.mpa.db.mysql.extractor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.db.mysql.accessor.Mascothit;
import de.mpa.db.mysql.accessor.Omssahit;
import de.mpa.db.mysql.accessor.SearchHit;
import de.mpa.db.mysql.accessor.XTandemhit;

/**
 * Class to extract all searchhits
 * @author R. Heyer
 *
 */
public class SearchHitExtractor {
	
	/**
	 * Map with all searchhits
	 */
	public static Map<Long, Boolean> MAP = new HashMap<Long, Boolean>();
	
	/**
	 * Finds all searchhits for a certain experiment
	 * @param experimentID. The experimentID
	 * @param conn. The connection
	 * @return the list with all searchhits
	 * @throws SQLException
	 */
	public static List<SearchHit> findSearchHitsFromExperimentID(long experimentID, Connection conn) throws SQLException{
		List<SearchHit> searchHits = new ArrayList<SearchHit>();
		searchHits.addAll(XTandemhit.getHitsFromExperimentID(experimentID, conn));
		searchHits.addAll(Omssahit.getHitsFromExperimentID(experimentID, conn));
		searchHits.addAll(Mascothit.getHitsFromExperimentID(experimentID, conn));
		
		for (SearchHit searchHit : searchHits) {
            SearchHitExtractor.MAP.put(searchHit.getFk_searchspectrumid(), true);
		}
		return searchHits;
		
	}
}
