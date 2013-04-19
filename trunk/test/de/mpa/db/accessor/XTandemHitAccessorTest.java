package de.mpa.db.accessor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import de.mpa.db.DBManager;

public class XTandemHitAccessorTest {
	
	private Connection conn;
	
	@Before
	public void setUp() throws SQLException {
		conn = DBManager.getInstance().getConnection();
	}
	
	@Test
	public void testNonUniquePSMs() throws SQLException {
        List<SearchHit> searchHits = new ArrayList<SearchHit>();
        
	    // Iterate the hits.
	    searchHits.addAll(XTandemhit.getHitsFromExperimentID(214, conn));
	    searchHits.addAll(Omssahit.getHitsFromExperimentID(214, conn));
		
	    Map<String, SearchHit> nonDuplicates = new HashMap<String, SearchHit>();
	    
	    // Filter out duplicates (i.e. same PSM with same peptide) from both search engines.
	    for (SearchHit searchHit : searchHits) {
	    	String key = searchHit.getFk_searchspectrumid() + "_" + searchHit.getSequence();
			if (nonDuplicates.get(key) == null) {
				if(searchHit.getQvalue().doubleValue() < 0.05) {
					nonDuplicates.put(key, searchHit);
				}
	    	} 	
		}
	   
	    Map<Long, List<SearchHit>> map = new HashMap<Long, List<SearchHit>>();
	    
	    // Get non unique PSMs
	    List<SearchHit> nonDupList = new ArrayList<SearchHit>(nonDuplicates.values());
	    for (SearchHit hit : nonDupList) {
	    	long id = hit.getFk_searchspectrumid();
	    	List<SearchHit> list = null;
			if (map.get(id) != null) {
	    		list = map.get(id);
	    	} else {
	    		list = new ArrayList<SearchHit>();
	    	}
			list.add(hit);
    		map.put(id, list);
		}
	   
	    
	    final String SEP = ";";
		BufferedWriter bWriter = null;
		BufferedWriter bWriter2 = null;
		try {
			bWriter = new BufferedWriter(new FileWriter("/home/muth/Metaproteomics/Results/NonUniquePSMs/P34_Non-Unique_PSMs.txt"));
			bWriter.append("Spectrum ID" + SEP + "Peptide Sequence" + SEP + "Accession" + SEP + "Search Engine");
			bWriter.newLine();
			bWriter2 = new BufferedWriter(new FileWriter("/home/muth/Metaproteomics/Results/UniquePSMs/P34_Unique_PSMs.txt"));
			bWriter2.append("Spectrum ID" + SEP + "Peptide Sequence" + SEP + "Accession" + SEP + "Search Engine");
			bWriter2.newLine();
			for (Entry<Long, List<SearchHit>> entry : map.entrySet()) {
			    	List<SearchHit> values = entry.getValue();
			    	if (values.size() > 1) {
			    		for (SearchHit hit : values) {
		    				bWriter.append(hit.getFk_searchspectrumid() + SEP + hit.getSequence() + SEP + hit.getAccession() + SEP + hit.getType().name());
							bWriter.newLine();
						}
			    	} else if (values.size() == 1) {
			    		SearchHit hit = values.get(0);
			    		bWriter2.append(hit.getFk_searchspectrumid() + SEP + hit.getSequence() + SEP + hit.getAccession() + SEP + hit.getType().name());
						bWriter2.newLine();
			    	}
				}
			bWriter.close();
			bWriter2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
