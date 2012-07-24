package de.mpa.client.ui.chart;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;


public class OntologyData {
	
	/**
	 * Occurrences map used for the ontology data.
	 */
	private Map<String, Integer> occurrencesMap;
	
	/**
	 * OntologyData constructor. 
	 * @param occurrencesMap The occurrences map.
	 */
	public OntologyData(Map<String, Integer> occurrencesMap) {
		this.occurrencesMap = occurrencesMap;
	}
	
	/**
	 * Returns the pieDataset.
	 * @return
	 */
	public PieDataset getPieDataset() {
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		Set<Entry<String, Integer>> entrySet = occurrencesMap.entrySet();
		for (Entry<String, Integer> e : entrySet) {
			pieDataset.setValue(e.getKey(), e.getValue());
		}
		return pieDataset;
	}
	
	/**
	 * Clears the occurrences map. 
	 */
	public void clear() {
		occurrencesMap.clear();
	}
		
	/**
	 * Sets the occurrences map.
	 * @param occurrencyMap
	 */
	public void setOccurrencyMap(Map<String, Integer> occurrencyMap) {
		this.occurrencesMap = occurrencyMap;
	}
	
	
}
