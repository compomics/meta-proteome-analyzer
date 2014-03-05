package de.mpa.client.model.dbsearch;

import java.util.Set;

import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.OntologyChart.OntologyChartType;
import de.mpa.client.ui.chart.TaxonomyChart.TaxonomyChartType;

/**
 * Interface for a certain entry of a spectrum, peptide, protein and metaprotein.
 */
public interface Hit {
	
	/**
	 * This gets the property of the entry, supporting taxonomy, hierachies (protein, peptide..) and ontologies
	 * @param type one of {@link TaxonomyChartType}, {@link OntologyChartType} or {@link HierarchyLevel}
	 * @return
	 */
	public Set<Object> getProperties(ChartType type);
	
	/**
	 * Sets the false discovery rate.
	 */
	public void setFDR(double fdr);
	
	/**
	 * Returns true if hit should be visible else false.
	 * @return
	 */
	public boolean isVisible();
	
}
