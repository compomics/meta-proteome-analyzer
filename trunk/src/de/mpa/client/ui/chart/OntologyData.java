package de.mpa.client.ui.chart;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

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
	 * OntologyData constructor. 
	 * @param occurrencesMap The occurrences map.
	 */
	public OntologyData() {
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
	 * Sets the molecular function map.
	 * @param molFunctionOccMap
	 */
	public void setMolFunctionOccMap(Map<String, Integer> molFunctionOccMap) {
		this.molFunctionOccMap = molFunctionOccMap;
	}

	/**
	 * Sets the biological process map.
	 * @param biolProcessOccMap the biolProcessOccMap to set
	 */
	public void setBiolProcessOccMap(Map<String, Integer> biolProcessOccMap) {
		this.biolProcessOccMap = biolProcessOccMap;
	}

	/**
	 * Sets the cellular component map.
	 * @param cellCompOccMap the cellCompOccMap to set
	 */
	public void setCellCompOccMap(Map<String, Integer> cellCompOccMap) {
		this.cellCompOccMap = cellCompOccMap;
	}
}
