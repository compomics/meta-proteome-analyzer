package de.mpa.client.ui.chart;

import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import de.mpa.client.model.dbsearch.ProteinHit;

public class IdentificationData implements ChartData {
	
	/**
	 * IdentificationData constructor. 
	 * @param TODO: Add parameters!
	 */
	public IdentificationData() {
		//this.occurrencesMap = occurrencesMap;
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Returns the category dataset.
	 * @return Category dataset.
	 */
	public CategoryDataset getDataset() {
		DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
		// TODO: Add a default category dataset.
		return categoryDataset;
	}

	@Override
	public List<ProteinHit> getProteinHits(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void clear() {
		
	}

}
