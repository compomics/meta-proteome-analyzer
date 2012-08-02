package de.mpa.client.ui.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import de.mpa.analysis.UniprotAccessor;
import de.mpa.analysis.UniprotAccessor.TaxonomyRank;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.chart.TaxonomyPieChart.TaxonomyChartType;

public class TaxonomyData implements ChartData {
	
	/**
	 * Taxonomy class occurrences map.
	 */
	private Map<String, Integer> classTaxonomyMap;
	
	/**
	 * Taxonomy phylum occurrences map.
	 */
	private Map<String, Integer> phylumTaxonomyMap;
	
	/**
	 * Taxonomy kingdom occurrences map.
	 */
	private Map<String, Integer> kingdomTaxonomyMap;
	
	/**
	 * Taxonomy species occurrences map.
	 */
	private Map<String, Integer> speciesTaxonomyMap;
	
	/**
	 * The ontology map.
	 */
	private Map<String, TaxonomyRank> taxonomyMap;
	
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
	public TaxonomyData() {
	}
	
	/**
	 * TaxonomyData constructor taking the database search result 
	 * @param dbSearchResult The database search result.
	 */
	public TaxonomyData(DbSearchResult dbSearchResult) {
		this.dbSearchResult = dbSearchResult;
		init();
	}

	/**
	 * This method sets up the taxonomies.
	 * @param dbSearchResult Database search result data.
	 */
	public void init() {
		// Get the taxonomy map.
		if(taxonomyMap == null) {
			taxonomyMap = UniprotAccessor.getTaxonomyMap();
		}
		
		// Map to count the occurrences of each molecular function.
		clear();
		
		classTaxonomyMap = new HashMap<String, Integer>();
		phylumTaxonomyMap = new HashMap<String, Integer>();
		kingdomTaxonomyMap = new HashMap<String, Integer>();
		speciesTaxonomyMap = new HashMap<String, Integer>();
		
		for (ProteinHit proteinHit : dbSearchResult.getProteinHits().values()) {
			UniProtEntry entry = proteinHit.getUniprotEntry();
			
			// Entry must be provided
			if(entry != null){
				
				List<NcbiTaxon> taxonomies = entry.getTaxonomy();
				for (NcbiTaxon taxon : taxonomies) {
					String taxonName = taxon.getValue();
					if(taxonomyMap.containsKey(taxonName)) {
						//TODO: Do the calculation on spectral level.
						TaxonomyRank taxonomyRank = taxonomyMap.get(taxonName);
						switch (taxonomyRank) {
						case KINGDOM:
							if(kingdomTaxonomyMap.containsKey(taxonName)){
								kingdomTaxonomyMap.put(taxonName, kingdomTaxonomyMap.get(taxonName)+1);
							} else {
								kingdomTaxonomyMap.put(taxonName, 1);
							}
							break;
						case PHYLUM:
							if(phylumTaxonomyMap.containsKey(taxonName)){
								phylumTaxonomyMap.put(taxonName, phylumTaxonomyMap.get(taxonName)+1);
							} else {
								phylumTaxonomyMap.put(taxonName, 1);
							}
							break;
						case CLASS:
							if(classTaxonomyMap.containsKey(taxonName)){
								classTaxonomyMap.put(taxonName, classTaxonomyMap.get(taxonName)+1);
							} else {
								classTaxonomyMap.put(taxonName, 1);
							}
							break;
						}
					}
				}
				String species = entry.getOrganism().getScientificName().getValue();
				if(speciesTaxonomyMap.containsKey(species)){
					speciesTaxonomyMap.put(species, speciesTaxonomyMap.get(species)+1);
				} else {
					speciesTaxonomyMap.put(species, 1);
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
		TaxonomyChartType pieChartType = (TaxonomyChartType) chartType;
		switch (pieChartType) {
		case KINGDOM:
			entrySet = kingdomTaxonomyMap.entrySet();
			break;
		case PHYLUM:
			entrySet = phylumTaxonomyMap.entrySet();
			break;
		case CLASS:
			entrySet = classTaxonomyMap.entrySet();
			break;
		case SPECIES:
			entrySet = speciesTaxonomyMap.entrySet();
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
		if(kingdomTaxonomyMap != null) kingdomTaxonomyMap.clear();
		if(phylumTaxonomyMap != null) phylumTaxonomyMap.clear();
		if(classTaxonomyMap != null) classTaxonomyMap.clear();
		if(speciesTaxonomyMap != null) speciesTaxonomyMap.clear();
	}

}
