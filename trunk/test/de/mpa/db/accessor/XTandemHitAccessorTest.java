package de.mpa.db.accessor;

import jaligner.Alignment;
import jaligner.Sequence;
import jaligner.SmithWatermanGotoh;
import jaligner.matrix.Matrix;
import jaligner.matrix.MatrixLoader;
import jaligner.matrix.MatrixLoaderException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.db.DBManager;

public class XTandemHitAccessorTest {
	
	private Connection conn;
	
	@Before
	public void setUp() throws SQLException {
		conn = DBManager.getInstance().getConnection();
	}
	
	@Test
	public void testNonUniquePSMs() throws SQLException {
		
		
		Map<Integer, String> exp2FilenameMap = new HashMap<Integer, String>();
		exp2FilenameMap.put(150, "P1");
		exp2FilenameMap.put(152, "P3");
		exp2FilenameMap.put(192, "P8");
		exp2FilenameMap.put(195, "P11");
		exp2FilenameMap.put(200, "P17");
		exp2FilenameMap.put(203, "P23");
		exp2FilenameMap.put(205, "P27");
		exp2FilenameMap.put(208, "P28");
		exp2FilenameMap.put(211, "P31");
		exp2FilenameMap.put(214, "P34");
		int[] expArray = new int[] {150, 152, 192, 195, 200, 203, 205, 208, 211, 214};
		/* ALIGNMENT FLAG */
		final boolean DO_ALIGNMENT = true;
		
		/* FDR = qValue threshold */
		final double FDR = 0.01;
		
		// Iterate the experiments.
		for (int i = 0; i < expArray.length; i++) {
			Set<Long> psmSet = new HashSet<Long>();
	        List<SearchHit> searchHits = new ArrayList<SearchHit>();
	        
		    // Iterate the hits.
		    int experimentID = expArray[i];
			searchHits.addAll(XTandemhit.getHitsFromExperimentID(experimentID, conn));
		    searchHits.addAll(Omssahit.getHitsFromExperimentID(experimentID, conn));
			
		    Map<String, SearchHit> nonDuplicates = new HashMap<String, SearchHit>();
		    
		    int xtandemCounter = 0;
		    int omssaCounter = 0;
		    // Filter out duplicates (i.e. same PSM with same peptide) from both search engines.
		    for (SearchHit searchHit : searchHits) {
		    	if(searchHit.getQvalue().doubleValue() < FDR) {
		    		psmSet.add(searchHit.getFk_searchspectrumid());
		    		if(searchHit.getType() == SearchEngineType.XTANDEM) {
		    			xtandemCounter++;
		    		}
		    		
		    		if(searchHit.getType() == SearchEngineType.OMSSA) {
		    			omssaCounter++;
		    		}
		    		
		    		String key = searchHit.getFk_searchspectrumid() + "_" + searchHit.getSequence();
		    		if (nonDuplicates.get(key) == null) {
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
				bWriter = new BufferedWriter(new FileWriter("/home/muth/Metaproteomics/Results/NonUniquePSMs/" + exp2FilenameMap.get(experimentID) + "_Non-Unique_PSMs.txt"));
				bWriter.append("Spectrum ID" + SEP + "Peptide Sequence" + SEP + "Accession" + SEP + "Search Engine");
				bWriter.newLine();
				bWriter2 = new BufferedWriter(new FileWriter("/home/muth/Metaproteomics/Results/UniquePSMs/" + exp2FilenameMap.get(experimentID) + "_Unique_PSMs.txt"));
				bWriter2.append("Spectrum ID" + SEP + "Peptide Sequence" + SEP + "Accession" + SEP + "Search Engine");
				bWriter2.newLine();
				int nonUniquePSMs = 0;
				int uniquePSMs = 0;
				int noPeptides = 0;
				int nonIsoLeucineVsLeucineCounter = 0;
				Matrix matrix = null;
				if(DO_ALIGNMENT) {
					matrix = MatrixLoader.load("BLOSUM62");
				}
				for (Entry<Long, List<SearchHit>> entry : map.entrySet()) {
				    	List<SearchHit> values = entry.getValue();
				    	if (values.size() > 1) {
				    		boolean counted = false;
				    		if(values.size() == 2 && DO_ALIGNMENT) {
				    			List<SearchHit> hits = new ArrayList<SearchHit>();
				    			hits.addAll(values);
				    			
			    				String seq1 = hits.get(0).getSequence();
			    				String seq2 = hits.get(1).getSequence();
								Alignment align = SmithWatermanGotoh.align(
										new Sequence(seq1),
										new Sequence(seq2),											
										matrix, 10.0f, 0.5f);
			    				if(align.getSimilarity() < seq1.length()) {
			    					nonIsoLeucineVsLeucineCounter++;
			    					nonIsoLeucineVsLeucineCounter++;
			    					SearchHit hit = hits.get(0);			    					
				    				bWriter.append(hit.getFk_searchspectrumid() + SEP + hit.getSequence() + SEP + hit.getAccession() + SEP + hit.getType().name());
			    					SearchHit hit2 = hits.get(1);
			    					bWriter.newLine();
				    				bWriter.append(hit2.getFk_searchspectrumid() + SEP + hit2.getSequence() + SEP + hit2.getAccession() + SEP + hit2.getType().name());
									bWriter.newLine();
			    				} else if(align.getSimilarity() < seq2.length()) {
			    					nonIsoLeucineVsLeucineCounter++;
			    					nonIsoLeucineVsLeucineCounter++;
			    					SearchHit hit = hits.get(0);
				    				bWriter.append(hit.getFk_searchspectrumid() + SEP + hit.getSequence() + SEP + hit.getAccession() + SEP + hit.getType().name());
			    					SearchHit hit2 = hits.get(1);
			    					bWriter.newLine();
				    				bWriter.append(hit2.getFk_searchspectrumid() + SEP + hit2.getSequence() + SEP + hit2.getAccession() + SEP + hit2.getType().name());
									bWriter.newLine();
			    				}
						} else if (values.size() > 2) {
//							    for (SearchHit hit : values) {
//				    				bWriter.append(hit.getFk_searchspectrumid() + SEP + hit.getSequence() + SEP + hit.getAccession() + SEP + hit.getType().name());
//				    				bWriter.newLine();
//								}
//								nonIsoLeucineVsLeucineCounter++;
			    			}
				    		
				    		for (SearchHit hit : values) {
				    			if(!counted) {
				    				nonUniquePSMs++;
				    			}
				    			counted = true;
				    			noPeptides++;
	
							}
				    	} else if (values.size() == 1) {
				    		uniquePSMs++;
				    		SearchHit hit = values.get(0);
				    		bWriter2.append(hit.getFk_searchspectrumid() + SEP + hit.getSequence() + SEP + hit.getAccession() + SEP + hit.getType().name());
							bWriter2.newLine();
				    	}
					}
				 // Number of identified spectra.
			    System.out.println("Experiment No. " + experimentID);
			    System.out.println("No. identified spectra: " + psmSet.size());
			    System.out.println("XTandem hits: " + xtandemCounter);
				System.out.println("OMSSA hits: " + omssaCounter);
				
				System.out.println("No. Non-unique PSMs: " + nonUniquePSMs);
				System.out.println("No. Peptides from non-unique PSMs: " + noPeptides);
				System.out.println("No. Unique PSMs: " + uniquePSMs);
				System.out.println("Non Iso-Leucine vs. Leucine exchanges: " + nonIsoLeucineVsLeucineCounter++); 
				System.out.println();
				
				bWriter.close();
				bWriter2.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MatrixLoaderException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
}
