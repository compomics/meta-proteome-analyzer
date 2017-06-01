package de.mpa.model.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.mpa.client.ui.sharedelements.chart.ChartType;
import de.mpa.client.ui.sharedelements.chart.TaxonomyChart.TaxonomyChartType;
import de.mpa.model.MPAExperiment;
import de.mpa.model.dbsearch.ProteinHit;
import de.mpa.model.taxonomy.TaxonomyNode;

public class CompareUtil {

	protected static void countFunctionElements(HashMap<Long, Integer> experimentIndexMap, Set<Long> experiments,
			Set<Object> props, Map<String, Long[]> results) {
		for (Object property : props) {
			for (long psLong : experiments) {
				// is already inside just increase
				if (results.containsKey(property.toString())) {
					results.get(property.toString())[experimentIndexMap.get(psLong)]++;
				} else {
					// else put new row and increase
					results.put(property.toString(), cleanLongArray(new Long[experimentIndexMap.size()]));
					results.get(property.toString())[(int) experimentIndexMap.get(psLong)]++;
				}
			}
		}
	}

	protected static void countProteinElements(HashMap<Long, Integer> experimentIndexMap, ProteinHit protein,
			Map<String, Long[]> results, Set<Long> experimentIDs) {
		for (long psLong : experimentIDs) {
			// is already inside just increase
			if (results.containsKey(protein.getAccession())) {
				results.get(protein.getAccession())[experimentIndexMap.get(psLong)]++;
			} else {
				// else put new row and increase
				results.put(protein.getAccession(), CompareUtil.cleanLongArray(new Long[experimentIndexMap.size()]));
				results.get(protein.getAccession())[(int) experimentIndexMap.get(psLong)]++;
			}
		}
	}

	protected static void countTaxonomyNodes(HashMap<Long, Integer> experimentIndexMap, TaxonomyNode givenNode,
			Set<Long> experimentIDs, Map<String, Long[]> results, ChartType typeLevel) {
		TaxonomyNode node;
		// init start node
		node = givenNode;
		while (!isTaxonomyHierarchyLevel(node, (TaxonomyChartType) typeLevel)) {
			if (!node.isRoot()) {
				node = node.getParentNode();
			} else {
				// break condition
				break;
			}
		}
		// last test for the taxonomy rank
		if (isTaxonomyHierarchyLevel(node, (TaxonomyChartType) typeLevel)) {
			// for each experiment
			for (long psLong : experimentIDs) {
				// is already inside just increase
				if (results.containsKey(node.getName())) {
					results.get(node.getName())[experimentIndexMap.get(psLong)]++;
				} else {
					// else put new row and increase
					results.put(node.getName(), cleanLongArray(new Long[experimentIndexMap.size()]));
					results.get(node.getName())[(int) experimentIndexMap.get(psLong)]++;
				}
			}
		}
	}

	private static boolean isTaxonomyHierarchyLevel(TaxonomyNode node, TaxonomyChartType type) {
		if (node.getRank() == type.getRank())
			return true;
		return false;
	}

	protected static Long[] cleanLongArray(Long[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] = (long) 0;
		}
		return array;
	}

	protected static HashMap<Long, Integer> createIndexHashMapForExperiments(ArrayList<MPAExperiment> experiments) {
		HashMap<Long, Integer> experimentIndexMap = new HashMap<Long, Integer>();
		int i = 0;
		for (MPAExperiment exper : experiments) {
			experimentIndexMap.put(exper.getID(), i);
			i++;
		}
		return experimentIndexMap;
	}

}
