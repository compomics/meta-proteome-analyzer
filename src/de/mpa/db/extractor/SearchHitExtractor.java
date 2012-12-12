package de.mpa.db.extractor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.XTandemhit;

public class SearchHitExtractor {
	
	public static Map<Long, Boolean> MAP = new HashMap<Long, Boolean>();
	
	public static List<SearchHit> findSearchHitsFromExperimentID(long experimentID, Connection conn) throws SQLException{
		List<SearchHit> searchHits = new ArrayList<SearchHit>();
		searchHits.addAll(XTandemhit.getHitsFromExperimentID(experimentID, conn));
		searchHits.addAll(Omssahit.getHitsFromExperimentID(experimentID, conn));
		searchHits.addAll(Cruxhit.getHitsFromExperimentID(experimentID, conn));
		searchHits.addAll(Inspecthit.getHitsFromExperimentID(experimentID, conn));
		
		for (SearchHit searchHit : searchHits) {			
			MAP.put(searchHit.getFk_searchspectrumid(), true);
		}
		return searchHits;
		
	}
}
