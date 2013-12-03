package de.mpa.client.model.dbsearch;

import java.util.Set;

import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.OntologyChart.OntologyChartType;
import de.mpa.client.ui.chart.TaxonomyChart.TaxonomyChartType;

/**
 * TODO: API
 */
public interface Hit {
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getCount(Object x, Object y);
	
	/**
	 * This gets the property of the entry, supporting taxonomy, hierachies (protein, peptide..) and ontologies
	 * @param type one of {@link TaxonomyChartType}, {@link OntologyChartType} or {@link HierarchyLevel}
	 * @return
	 */
	public Set<Object> getProperties(ChartType type);
	
}
