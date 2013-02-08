package de.mpa.client.ui.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.util.MapUtil;

/**
 * This class structure is used to describe the top/best-of data set.
 *  
 * @author T.Muth
 * @date 26-07-2012
 *
 */
public class TopData implements ChartData {
	
	/**
	 * The database search result.
	 */
	private DbSearchResult dbSearchResult;
	
	/**
	 * The TOP_NUMBER is the amount of best-of hits, which is taken for the data.
	 */
	private final static int TOP_NUMBER = 10;
	
	/**
	 * The top proteins map.
	 */
	private HashMap<ProteinHit, Integer> topProteinsMap;

	/**
	 * TopData constructor
	 * @param dbSearchResult The database search result.
	 */
	public TopData(DbSearchResult dbSearchResult) {
		this.dbSearchResult = dbSearchResult;
		init();
	}

	@Override
	public void init() {
		HashMap<ProteinHit, Integer> tempMap = new HashMap<ProteinHit, Integer>();
		
		// Iterate protein hits and fill proteins map.
		for (ProteinHit proteinHit : dbSearchResult.getProteinHits().values()) {
			tempMap.put(proteinHit, proteinHit.getSpectralCount());
		}
		
		// Get list of map keys sorted by map value 
		List<ProteinHit> topProteins = MapUtil.getKeysSortedByValue(tempMap, true);
		
		topProteinsMap = new LinkedHashMap<ProteinHit, Integer>(TOP_NUMBER);
		for (int i = 0; i < TOP_NUMBER; i++) {
			ProteinHit proteinHit = topProteins.get(i);
			topProteinsMap.put(proteinHit, proteinHit.getSpectralCount());
		}
		tempMap = null;
	}
	
	@Override
	public CategoryDataset getDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		// Iterate the best ranked (highest spectral count) proteins.
		for (Entry<ProteinHit, Integer> entry : topProteinsMap.entrySet()) {
			ProteinHit proteinHit = entry.getKey();
			String[] split = proteinHit.getDescription().split("\\s+");
			dataset.setValue(proteinHit.getSpectralCount(), "No. Spectra", split[0]);
		}
		return dataset;
	}

	@Override
	public List<ProteinHit> getProteinHits(String key) {
		return new ArrayList<ProteinHit>(topProteinsMap.keySet());
	}

}
