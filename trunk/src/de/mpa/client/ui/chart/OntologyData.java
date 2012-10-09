package de.mpa.client.ui.chart;

import java.util.ArrayList;
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
	private Map<String, List<ProteinHit>> molFunctionOccMap;
	
	/**
	 * Biological process occurrences map.
	 */
	private Map<String, List<ProteinHit>> biolProcessOccMap;
	
	/**
	 * Cellular component occurrences map.
	 */
	private Map<String, List<ProteinHit>> cellCompOccMap;
	
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
		if (ontologyMap == null) {
			ontologyMap = UniprotAccessor.getOntologyMap();
		}
		
		// Maps to count the occurrences of each molecular function.
//		clear();
		biolProcessOccMap = new HashMap<String, List<ProteinHit>>();
		cellCompOccMap = new HashMap<String, List<ProteinHit>>();
		molFunctionOccMap = new HashMap<String, List<ProteinHit>>();
		
		for (ProteinHit proteinHit : dbSearchResult.getProteinHitList()) {
			UniProtEntry entry = proteinHit.getUniprotEntry();
			
			// Entry must be provided
			if (entry != null) {
				List<Keyword> keywords = entry.getKeywords();
				for (Keyword kw : keywords) {
					String keyword = kw.getValue();
					if (ontologyMap.containsKey(keyword)) {
						//TODO: Do the calculation on spectral level.
						KeywordOntology kwOntology = ontologyMap.get(keyword);
						switch (kwOntology) {
						case BIOLOGICAL_PROCESS:
							appendHit(keyword, biolProcessOccMap, proteinHit);
							break;
						case CELLULAR_COMPONENT:
							appendHit(keyword, cellCompOccMap, proteinHit);
							break;
						case MOLECULAR_FUNCTION:
							appendHit(keyword, molFunctionOccMap, proteinHit);
							break;
						}
					}
				}
			}
		}
		// TODO: maybe merge pairs whose values are identical but differ in their key(word)s?
	}
	
	/**
	 * Utility method to append a protein hit to the specified occurrence map.
	 * @param keyword The key for an entry in the occurrence map.
	 * @param map The occurrence map.
	 * @param proteinHit The protein hit to append.
	 */
	protected void appendHit(String keyword, Map<String, List<ProteinHit>> map, ProteinHit proteinHit) {
		List<ProteinHit> protHits = map.get(keyword);
		if (protHits == null) {
			protHits = new ArrayList<ProteinHit>();
		}
		protHits.add(proteinHit);
		map.put(keyword, protHits);
	}
	
	/**
	 * Returns the pieDataset.
	 * @return
	 */
	@Override
	public PieDataset getDataset() {
		// TODO: pre-process dataset generation and return only cached variables
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		
		Map<String, List<ProteinHit>> map = null;
		switch ((OntologyChartType) chartType) {
		case BIOLOGICAL_PROCESS:
			map = biolProcessOccMap;
			break;
		case CELLULAR_COMPONENT:
			map = cellCompOccMap;
			break;
		case MOLECULAR_FUNCTION:
			map = molFunctionOccMap;
			break;
		}
		Set<Entry<String, List<ProteinHit>>> entrySet = map.entrySet();
		
		int sumValues = 0;
		for (Entry<String, List<ProteinHit>> e : entrySet) {
			sumValues += e.getValue().size();
		}
		
		List<ProteinHit> others = new ArrayList<ProteinHit>();
		double limit = 0.01;
		for (Entry<String, List<ProteinHit>> e : entrySet) {
			Integer absVal = e.getValue().size();
			double relVal = absVal * 1.0 / sumValues * 1.0;
			Comparable key;
			if (relVal >= limit) {
				key = e.getKey();
			} else {
				key = "Others";
				// add grouped hits to list and store it in map they originate from
				// TODO: do this in pre-processing step, e.g. inside init()
				others.addAll(e.getValue());
				
				absVal = others.size();
			}
			pieDataset.setValue(key, absVal);
		}
		map.put("Others", others);
		
		return pieDataset;
	}
	
	@Override
	public List<ProteinHit> getProteinHits(String key) {
		switch ((OntologyChartType) chartType) {
		case BIOLOGICAL_PROCESS:
			return biolProcessOccMap.get(key);
		case CELLULAR_COMPONENT:
			return cellCompOccMap.get(key);
		case MOLECULAR_FUNCTION:
			return molFunctionOccMap.get(key);
		default:
			return null;
		}
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
		if (biolProcessOccMap != null) biolProcessOccMap.clear();
		if (molFunctionOccMap != null) molFunctionOccMap.clear();
		if (cellCompOccMap != null) cellCompOccMap.clear();
	}
	
	/**
	 * Sets up the default maps.
	 * @param defaultMap
	 */
	public void setDefaultMapping(Map<String, List<ProteinHit>> defaultMap) {
		this.biolProcessOccMap = defaultMap;
		this.cellCompOccMap = defaultMap;
		this.molFunctionOccMap = defaultMap;
	}
}
