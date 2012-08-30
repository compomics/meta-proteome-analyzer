package de.mpa.client.ui.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import de.mpa.analysis.UniprotAccessor;
import de.mpa.analysis.UniprotAccessor.KeywordOntology;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.chart.OntologyPieChart.OntologyChartType;

/**
 * This class structure is used to describe the ontology data set.
 *  
 * @author T.Muth
 * @date 25-07-2012
 *
 */
public class OntologyData implements ChartData {
	
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
	 * The database search result.
	 */
	private DbSearchResult dbSearchResult;
	
	/**
	 * The chart type.
	 */
	private ChartType chartType;
	
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
		this.dbSearchResult = dbSearchResult;
		init();
	}

	/**
	 * This method sets up the ontologies.
	 * @param dbSearchResult Database search result data.
	 */
	public void init() {
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
	public PieDataset getDataset() {
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		Set<Entry<String, Integer>> entrySet = null;
		OntologyChartType pieChartType = (OntologyChartType) chartType;
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
		
		int sumValues = 0;
		for (Entry<String, Integer> e : entrySet) {
			sumValues += e.getValue();
		}
		
		double limit = 0.01;
		for (Entry<String, Integer> e : entrySet) {
			if((e.getValue() * 1.0 / sumValues * 1.0) >= limit) {
				pieDataset.setValue(e.getKey(), e.getValue());
			} else {
				pieDataset.setValue("Others", e.getValue());
			}
		}
		return pieDataset;
	}
	
	/**
	 * Sets the chart type.
	 * @param chartType The chart type.
	 */
	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
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
