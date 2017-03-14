package de.mpa.client.model.dbsearch;

import java.util.Set;

import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.OntologyChart;
import de.mpa.client.ui.chart.TaxonomyChart;

/**
 * Interface for entries of spectrum, peptide, protein and metaprotein hits.
 */
public interface Hit {
	
	/**
	 * Returns whether this hit is selected for exporting. 
	 * @return <code>true</code> if hit is selected for export, 
	 * <code>false</code> otherwise.
	 */
    boolean isSelected();
	
	/**
	 * Sets whether this hit is selected for exporting. 
	 * @param selected <code>true</code> if hit is selected for export, 
	 * <code>false</code> otherwise.
	 */
    void setSelected(boolean selected);
	
	/**
	 * This gets the property of the entry, supporting taxonomy, hierachies (protein, peptide..) and ontologies
	 * @param type one of {@link TaxonomyChart.TaxonomyChartType}, {@link OntologyChart.OntologyChartType} or {@link HierarchyLevel}
	 * @return
	 */
    Set<Object> getProperties(ChartType type);
	
	/**
	 * Sets the false discovery rate.
	 */
    void setFDR(double fdr);
	
	/**
	 * Returns true if hit should be visible else false.
	 * @return
	 */
    boolean isVisible();
	
}
