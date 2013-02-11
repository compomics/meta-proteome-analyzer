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
import de.mpa.client.model.dbsearch.ProteinHitList;
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
	private Map<String, ProteinHitList> molFunctionOccMap;
	
	/**
	 * Biological process occurrences map.
	 */
	private Map<String, ProteinHitList> biolProcessOccMap;
	
	/**
	 * Cellular component occurrences map.
	 */
	private Map<String, ProteinHitList> cellCompOccMap;
	
	/**
	 * The database search result.
	 */
	private DbSearchResult dbSearchResult;
	
	/**
	 * The chart type.
	 */
	private ChartType chartType;
	
	/**
	 * The hierarchy level (protein, peptide or spectrum).
	 */
	private HierarchyLevel hierarchyLevel;
	
	/**
	 * Flag to determine whether proteins grouped under the 'Unknown' tag shall
	 * be excluded in dataset generation and therefore will subsequently not be
	 * displayed in any associated plots.
	 */
	private boolean hideUnknown;
	
	/**
	 * Empty default constructor.
	 */
	public OntologyData() {
	}

	/**
	 * Constructs ontology data from a database search result object using the
	 * default protein hierarchy level.
	 * @param dbSearchResult The database search result object.
	 */
	public OntologyData(DbSearchResult dbSearchResult) {
		this(dbSearchResult, HierarchyLevel.PROTEIN_LEVEL);
	}
	
	/**
	 * Constructs ontology data from a database search result object and the 
	 * specified hierarchy level.
	 * @param dbSearchResult The database search result object.
	 * @param hierarchyLevel The hierarchy level, one of <code>PROTEIN_LEVEL</code>, 
	 * <code>PEPTIDE_LEVEL</code> or <code>SPECTRUM_LEVEL</code>.
	 */
	public OntologyData(DbSearchResult dbSearchResult, HierarchyLevel hierarchyLevel) {
		this.dbSearchResult = dbSearchResult;
		this.hierarchyLevel = hierarchyLevel;
		init();
	}

	/**
	 * Sets up the ontologies.
	 */
	public void init() {
		// Get the ontology map.
		Map<String, KeywordOntology> ontologyMap = UniprotAccessor.ONTOLOGY_MAP;
		
		// Maps to count the occurrences of each molecular function.
		biolProcessOccMap = new HashMap<String, ProteinHitList>();
		cellCompOccMap = new HashMap<String, ProteinHitList>();
		molFunctionOccMap = new HashMap<String, ProteinHitList>();
		
		for (ProteinHit proteinHit : dbSearchResult.getProteinHitList()) {
			UniProtEntry entry = proteinHit.getUniprotEntry();
			
			boolean procFound, compFound, funcFound;
			procFound = compFound = funcFound = false;
			
			// Entry must be provided
			if (entry != null) {
				List<Keyword> keywords = entry.getKeywords();
				for (Keyword kw : keywords) {
					String keyword = kw.getValue();
					if (ontologyMap.containsKey(keyword)) {
						KeywordOntology kwOntology = ontologyMap.get(keyword);
						switch (kwOntology) {
						case BIOLOGICAL_PROCESS:
							appendHit(keyword, biolProcessOccMap, proteinHit);
							procFound = true;	// mark keyword type found
							break;
						case CELLULAR_COMPONENT:
							appendHit(keyword, cellCompOccMap, proteinHit);
							compFound = true;	// mark keyword type found
							break;
						case MOLECULAR_FUNCTION:
							appendHit(keyword, molFunctionOccMap, proteinHit);
							funcFound = true;	// mark keyword type found
							break;
						}
					}
				}
			}
			if (!procFound) {
				appendHit("Unknown", biolProcessOccMap, proteinHit);
			}
			if (!compFound) {
				appendHit("Unknown", cellCompOccMap, proteinHit);
			}
			if (!funcFound) {
				appendHit("Unknown", molFunctionOccMap, proteinHit);
			}
		}
		// TODO: maybe merge pairs whose values are identical but differ in their key(word)s? E.g. 
	}
	
	/**
	 * Utility method to append a protein hit to the specified occurrence map.
	 * @param keyword The key for an entry in the occurrence map.
	 * @param map The occurrence map.
	 * @param proteinHit The protein hit to append.
	 */
	protected void appendHit(String keyword, Map<String, ProteinHitList> map, ProteinHit proteinHit) {
		ProteinHitList protHits = map.get(keyword);
		if (protHits == null) {
			protHits = new ProteinHitList();
		}
		protHits.add(proteinHit);
		map.put(keyword, protHits);
	}
	
	/**
	 * Returns the pie dataset.
	 * @return the pie dataset.
	 */
	@Override
	public PieDataset getDataset() {
		// TODO: pre-process dataset generation and return only cached variables
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		pieDataset.setValue("Unknown", new Integer(0));
		
		Map<String, ProteinHitList> map = null;
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
		Set<Entry<String, ProteinHitList>> entrySet = map.entrySet();

		int sumValues = 0;
		for (Entry<String, ProteinHitList> entry : entrySet) {
			sumValues += getSizeByHierarchy(entry.getValue(), hierarchyLevel);
		}
		
		ProteinHitList others = new ProteinHitList();
		double limit = 0.01;
		for (Entry<String, ProteinHitList> entry : entrySet) {
			Integer absVal = getSizeByHierarchy(entry.getValue(), hierarchyLevel);
			double relVal = absVal * 1.0 / sumValues * 1.0;
			Comparable key;
			if (relVal >= limit) {
				key = entry.getKey();
			} else {
				key = "Others";
				// add grouped hits to list and store it in map they originate from
				// TODO: do this in pre-processing step, e.g. inside init()
				others.addAll(entry.getValue());
				
				absVal = getSizeByHierarchy(others, hierarchyLevel);
			}
			pieDataset.setValue(key, absVal);
		}
		if (!others.isEmpty()) {
			map.put("Others", others);
		}
		if (hideUnknown) {
			pieDataset.setValue("Unknown", new Integer(0));
		}
		
		return pieDataset;
	}

	/**
	 * Utility method to return size of hierarchically grouped data in a protein
	 * hit list.
	 * @param phl the protein hit list
	 * @param hl the hierarchy level, one of <code>PROTEIN_LEVEL</code>, 
	 * <code>PEPTIDE_LEVEL</code> or <code>SPECTRUM_LEVEL</code>
	 * @return the data size
	 */
	private int getSizeByHierarchy(ProteinHitList phl, HierarchyLevel hl) {
		switch (hl) {
		case PROTEIN_LEVEL:
			return phl.size();
		case PEPTIDE_LEVEL:
			return phl.getPeptideSet().size();
		case SPECTRUM_LEVEL:
			return phl.getMatchSet().size();
		default:
			return 0;
		}
	}
	
	@Override
	public ProteinHitList getProteinHits(String key) {
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
	 * Sets the hierarchy level of the occurrences to be returned. Either one of
	 * <code>PROTEIN_LEVEL</code>, <code>PEPTIDE_LEVEL</code> or
	 * <code>SPECTRUM_LEVEL</code>
	 * @param hierarchyLevel the hierarchy level
	 */
	public void setHierarchyLevel(HierarchyLevel hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
	}

	/**
	 * Sets up the default maps.
	 * @param defaultMap
	 */
	public void setDefaultMapping(Map<String, ProteinHitList> defaultMap) {
		this.biolProcessOccMap = defaultMap;
		this.cellCompOccMap = defaultMap;
		this.molFunctionOccMap = defaultMap;
	}

	/**
	 * Excludes proteins grouped in 'Unknown' category from dataset creation.
	 * @param hideUnknown <code>true</code> if 'Unknown' proteins shall be excluded, 
	 * 					  <code>false</code> otherwise
	 */
	public void setHideUnknown(boolean hideUnknown) {
		this.hideUnknown = hideUnknown;
	}
	
}
