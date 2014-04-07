package de.mpa.client.model.dbsearch;

import java.util.Set;

import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.OntologyChart.OntologyChartType;
import de.mpa.client.ui.chart.TaxonomyChart.TaxonomyChartType;

/**
 * Interface for entries of spectrum, peptide, protein and metaprotein hits.
 */
public interface Hit {
	
	/**
	 * Returns whether this hit is selected for exporting. 
	 * @return <code>true</code> if hit is selected for export, 
	 * <code>false</code> otherwise.
	 */
	public boolean isSelected();
	
	/**
	 * Sets whether this hit is selected for exporting. 
	 * @param selected <code>true</code> if hit is selected for export, 
	 * <code>false</code> otherwise.
	 */
	public void setSelected(boolean selected);
	
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
