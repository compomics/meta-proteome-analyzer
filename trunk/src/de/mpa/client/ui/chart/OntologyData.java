package de.mpa.client.ui.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

import de.mpa.analysis.UniprotAccessor;
import de.mpa.analysis.UniprotAccessor.KeywordOntology;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.chart.OntologyPieChart.PieChartType;

/**
 * This class structure is used to describe the ontology data set.
 *  
 * @author T.Muth
 * @date 25-07-2012
 *
 */
public class OntologyData {
	
	/**
	 * Molecular function occurrences map.
	 */
	private Map<String, Integer> molFunctionOccMap;
	
	/**
	 * Biological process occurrences map.
	 */
	private Map<String, Integer> biolProcessOccMap;
	
	/**
	 * Cellular component occurrences map.
	 */
	private Map<String, Integer> cellCompOccMap;
	
	/**
	 * The ontology map.
	 */
	private Map<String, KeywordOntology> ontologyMap;
	
	
	/**
	 * Empty default constructor.
	 */
	public OntologyData() {
	}
	
	/**
	 * OntologyData constructor taking the database seach result 
	 * @param dbSearchResult The database search result.
	 */
	public OntologyData(DbSearchResult dbSearchResult) {
		init(dbSearchResult);
	}

	/**
	 * This method sets up the ontologies.
	 * @param dbSearchResult Database search result data.
	 */
	public void init(DbSearchResult dbSearchResult) {
		// Get the ontology map.
		if(ontologyMap == null) {
			ontologyMap = UniprotAccessor.getOntologyMap();
		}
		
		// Map to count the occurrences of each molecular function.
		clear();
		
		biolProcessOccMap = new HashMap<String, Integer>();
		cellCompOccMap = new HashMap<String, Integer>();
		molFunctionOccMap = new HashMap<String, Integer>();
		
		for (ProteinHit proteinHit : dbSearchResult.getProteinHits().values()) {
			UniProtEntry entry = proteinHit.getUniprotEntry();
			
			// Entry must be provided
			if(entry != null){
				List<Keyword> keywords = entry.getKeywords();
				for (Keyword kw : keywords) {
					String keyword = kw.getValue();
					if(ontologyMap.containsKey(keyword)) {
						//TODO: Do the calculation on spectral level.
						KeywordOntology kwOntology = ontologyMap.get(keyword);
						switch (kwOntology) {
						case BIOLOGICAL_PROCESS:
							if(biolProcessOccMap.containsKey(keyword)){
								biolProcessOccMap.put(keyword, biolProcessOccMap.get(keyword)+1);
							} else {
								biolProcessOccMap.put(keyword, 1);
							}
							break;
						case CELLULAR_COMPONENT:
							if(cellCompOccMap.containsKey(keyword)){
								cellCompOccMap.put(keyword, cellCompOccMap.get(keyword)+1);
							} else {
								cellCompOccMap.put(keyword, 1);
							}
							break;
						case MOLECULAR_FUNCTION:
							if(molFunctionOccMap.containsKey(keyword)){
								molFunctionOccMap.put(keyword, molFunctionOccMap.get(keyword)+1);
							} else {
								molFunctionOccMap.put(keyword, 1);
							}
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns the pieDataset.
	 * @return
	 */
	public PieDataset getPieDataset(ChartType chartType) {
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		Set<Entry<String, Integer>> entrySet = null;
		PieChartType pieChartType = (PieChartType) chartType;
		switch (pieChartType) {
		case BIOLOGICAL_PROCESS:
			entrySet = biolProcessOccMap.entrySet();
			break;
		case CELLULAR_COMPONENT:
			entrySet = cellCompOccMap.entrySet();
			break;
		case MOLECULAR_FUNCTION:
			entrySet = molFunctionOccMap.entrySet();
			break;
		}
		for (Entry<String, Integer> e : entrySet) {
			pieDataset.setValue(e.getKey(), e.getValue());
		}
		return pieDataset;
	}
	
	/**
	 * Clears the occurrences map. 
	 */
	public void clear() {
		if(biolProcessOccMap != null) biolProcessOccMap.clear();
		if(molFunctionOccMap != null) molFunctionOccMap.clear();
		if(cellCompOccMap != null) cellCompOccMap.clear();
	}
	
	/**
	 * Sets up the default maps.
	 * @param defaultMap
	 */
	public void setDefaultMapping(Map<String, Integer> defaultMap) {
		this.biolProcessOccMap = defaultMap;
		this.cellCompOccMap = defaultMap;
		this.molFunctionOccMap = defaultMap;
	}
}
