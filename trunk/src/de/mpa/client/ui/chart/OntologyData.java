package de.mpa.client.ui.chart;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.SortOrder;

import de.mpa.analysis.UniprotAccessor;
import de.mpa.analysis.UniprotAccessor.KeywordOntology;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
import de.mpa.client.ui.chart.OntologyChart.OntologyChartType;

/**
 * Container class for protein keyword ontology data.
 *  
 * @author T. Muth, A. Behne
 * @date 25-07-2012
 */
public class OntologyData implements ChartData {
	
	/**
	 * The database search result.
	 */
	private DbSearchResult dbSearchResult;
	
	/**
	 * The chart type.
	 */
	private ChartType chartType;
	
	/**
	 * The hierarchy level (protein, peptide or spectrum).
	 */
	private HierarchyLevel hierarchyLevel;

	/**
	 * The relative threshold below which chart segments get grouped into a category labeled 'Others'.
	 */
	private double limit = 0.01;
	
	/**
	 * Flag to determine whether proteins grouped under the 'Unknown' tag shall
	 * be excluded in dataset generation and therefore will subsequently not be
	 * displayed in any associated plots.
	 */
	private boolean hideUnknown;
	
	/**
	 * Flag determining whether the underlying data shall be displayed in a pie chart or in a bar chart.
	 */
	private boolean showAsPie = true;
	
	/**
	 * Empty default constructor.
	 */
	public OntologyData() {
	}

	/**
	 * Constructs ontology data from a database search result object using the
	 * default protein hierarchy level.
	 * @param dbSearchResult the database search result object
	 */
	public OntologyData(DbSearchResult dbSearchResult) {
		this(dbSearchResult, HierarchyLevel.META_PROTEIN_LEVEL);
	}
	
	/**
	 * Constructs ontology data from a database search result object and the 
	 * specified hierarchy level.
	 * @param dbSearchResult the database search result object
	 * @param hierarchyLevel the hierarchy level, one of <code>PROTEIN_LEVEL</code>, 
	 * <code>PEPTIDE_LEVEL</code> or <code>SPECTRUM_LEVEL</code>
	 */
	public OntologyData(DbSearchResult dbSearchResult, HierarchyLevel hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
		this.setResult(dbSearchResult);
	}
	
	/**
	 * Sets the database search result reference to the specified result and
	 * refreshes the underlying dataset.
	 * @param dbSearchResult the database search result object
	 */
	public void setResult(DbSearchResult dbSearchResult) {
		this.dbSearchResult = dbSearchResult;
		this.init();
	}

	/**
	 * Sets up the ontologies.
	 */
	public void init() {
		// Get the ontology map
		Map<String, KeywordOntology> ontologyMap = UniprotAccessor.ONTOLOGY_MAP;
		
		// Go through DB search result object and add taxonomy information to taxanomy maps
		for (ProteinHit mp : dbSearchResult.getMetaProteins()) {
			MetaProteinHit metaProtein = (MetaProteinHit) mp;
			for (ProteinHit proteinHit : metaProtein.getProteinHits()) {
				if (proteinHit.isSelected()) {
					// Get UniProt Entry
					ReducedUniProtEntry entry = proteinHit.getUniProtEntry();
					
					OntologyChartType[] types = OntologyChartType.values();
					// booleans for ontology types found
					boolean[] found = new boolean[types.length];
					
					// Entry must be provided
					if (entry != null) {
						List<String> keywords = entry.getKeywords();
						for (String keyword : keywords) {
							if (ontologyMap.containsKey(keyword)) {
								KeywordOntology kwOntology = ontologyMap.get(keyword);
								for (int i = 0; i < types.length; i++) {
									OntologyChartType oct = types[i];
									if (kwOntology.equals(oct.getOntology())) {
										found[i] = true;
										this.appendHit(keyword, oct.getOccurrenceMap(), metaProtein);
									}
								}
							}
						}
					}
					for (int i = 0; i < types.length; i++) {
						OntologyChartType oct = types[i];
						if (!found[i]) {
							this.appendHit("Unknown", oct.getOccurrenceMap(), metaProtein);
						}
					}
				}
			}
		}
		// TODO: maybe merge pairs whose values are identical but differ in their key(word)s?
	}
	
	/**
	 * Utility method to append a protein hit list (a.k.a. a meta-protein) to
	 * the specified occurrence map.
	 * @param keyword the occurrence map key
	 * @param occMap the occurrence map
	 * @param metaProtein the protein hit list to append
	 */
	protected void appendHit(String keyword, Map<String, ProteinHitList> occMap, MetaProteinHit metaProtein) {
		ProteinHitList metaProteins = occMap.get(keyword);
		if (metaProteins == null) {
			metaProteins = new ProteinHitList();
		}
		if (!metaProteins.contains(metaProtein)) {
			metaProteins.add(metaProtein);
		}
		occMap.put(keyword, metaProteins);
	}
	
	/**
	 * Returns the pie dataset.
	 * @return the pie dataset.
	 */
	@Override
	public PieDataset getDataset() {
		try {
			// TODO: pre-process dataset generation and return only cached variables
			DefaultPieDataset pieDataset = new DefaultPieDataset();
			pieDataset.setGroup(new DatasetGroup(hierarchyLevel.getTitle()));

			String unknownKey = "Unknown";
			int unknownVal = 0;
			String othersKey = "Others";
			int othersVal = 0;

			// Add empty "Unknown" category so it always shows up first (to keep colors consistent)
			pieDataset.setValue(unknownKey, unknownVal);
			
			Map<String, ProteinHitList> occMap = ((OntologyChartType) this.chartType).getOccurrenceMap();
			// remove cached 'Others' category
			occMap.remove(othersKey);
			Set<Entry<String, ProteinHitList>> entrySet = occMap.entrySet();

//			double total = 0.0;
//			for (Entry<String, ProteinHitList> entry : entrySet) {			
//				total += this.getSizeByHierarchy(entry.getValue(), hierarchyLevel);
//			}
			
			for (Entry<String, ProteinHitList> entry : entrySet) {
				ProteinHitList metaProteins = entry.getValue();
				Integer absVal = this.getSizeByHierarchy(metaProteins, hierarchyLevel);
//				double relVal = absVal / total;
//				Comparable key;
//				if (relVal >= this.limit) {
//					key = entry.getKey();
//				} else {
//					key = othersKey;
//					// add grouped hits to list and store it in map they originate from
//					// TODO: do this in pre-processing step, e.g. inside init()
//					others.addAll(metaProteins);
//					
//					absVal = getSizeByHierarchy(others, hierarchyLevel);
//				}
				Comparable key = entry.getKey();
				pieDataset.setValue(key, absVal);
			}
			

			ProteinHitList others = new ProteinHitList();
			// Add empty 'Others' category so it always shows up last (to keep colors consistent)
			pieDataset.setValue(othersKey, othersVal);
			
			double total = DatasetUtilities.calculatePieDatasetTotal(pieDataset);
			for (Object obj : pieDataset.getKeys()) {
				Comparable key = (Comparable) obj;
				if (othersKey.equals(key) || unknownKey.equals(key)) {
					continue;
				}
				int absVal = pieDataset.getValue(key).intValue();
				double relVal = absVal / total;
				if (relVal < this.limit) {
					pieDataset.remove(key);
					othersVal += absVal;
					ProteinHitList metaProteins = occMap.get(key);
					others.addAll(metaProteins);
				}
			}
			
			// sort dataset w.r.t. remaining values
			pieDataset.sortByValues(SortOrder.DESCENDING);
			
			unknownVal = pieDataset.getValue(unknownKey).intValue();
			if ((unknownVal > 0) && !this.hideUnknown) {
				// move 'Unknown' category to front
				pieDataset.insertValue(0, unknownKey, unknownVal);
			} else {
				pieDataset.remove(unknownKey);
			}
			if (othersVal > 0) {
				// move 'Others' category to end
				pieDataset.insertValue(pieDataset.getItemCount() - 1, othersKey, othersVal);
				occMap.put(othersKey, others);
			} else {
				pieDataset.remove(othersKey);
			}

//			if (!others.isEmpty()) {
//				occMap.put(othersKey, others);
//			} else {
//				occMap.remove(othersKey);
////				pieDataset.remove(othersKey);
//			}
//			
//			if (hideUnknown) {
//				pieDataset.setValue(unknownKey, 0);
////				pieDataset.remove(unknownKey);
//			}
			
			return pieDataset;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Utility method to return size of hierarchically grouped data in a protein
	 * hit list.
	 * @param mphl the meta-protein hit list
	 * @param hl the hierarchy level, one of <code>PROTEIN_LEVEL</code>, 
	 * <code>PEPTIDE_LEVEL</code> or <code>SPECTRUM_LEVEL</code>
	 * @return the data size
	 */
	private int getSizeByHierarchy(ProteinHitList mphl, HierarchyLevel hl) {
		if (mphl == null) {
			System.out.println("uh oh");
		}
		switch (hl) {
		case META_PROTEIN_LEVEL:
			return mphl.size();
		case PROTEIN_LEVEL:
			Set<ProteinHit> proteinSet = mphl.getProteinSet();
			return proteinSet.size();
		case PEPTIDE_LEVEL:
			return mphl.getPeptideSet().size();
		case SPECTRUM_LEVEL:
			return mphl.getMatchSet().size();
		default:
			return 0;
		}
	}
	
	@Override
	public ProteinHitList getProteinHits(String key) {
		Map<String, ProteinHitList> occMap = ((OntologyChartType) this.chartType).getOccurrenceMap();
		if (occMap != null) {
			ProteinHitList phl = occMap.get(key);
			if (phl != null) {
				if (hierarchyLevel == HierarchyLevel.META_PROTEIN_LEVEL) {
					return phl;
				} else {
					return new ProteinHitList(phl.getProteinSet());
				}
			}
		}
		return null;
	}
	
	/**
	 * Sets the chart type.
	 * @param chartType The chart type.
	 */
	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	/**
	 * Sets the hierarchy level of the occurrences to be returned. Either one of
	 * <code>PROTEIN_LEVEL</code>, <code>PEPTIDE_LEVEL</code> or
	 * <code>SPECTRUM_LEVEL</code>
	 * @param hierarchyLevel the hierarchy level
	 */
	public void setHierarchyLevel(HierarchyLevel hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
	}

	/**
	 * Sets the relative size limit for pie segments.
	 * @param limit the limit
	 */
	public void setMinorGroupingLimit(double limit) {
		this.limit = limit;
	}

	/**
	 * Sets whether proteins grouped in 'Unknown' category shall be excluded from dataset creation.
	 * @param hideUnknown <code>true</code> if 'Unknown' proteins shall be excluded, 
	 *  <code>false</code> otherwise
	 */
	public void setHideUnknown(boolean hideUnknown) {
		this.hideUnknown = hideUnknown;
	}
	
	// TODO: maybe re-factor to allow other types of charts, e.g. getChartType() returning enum members like PIE_CHART and BAR_CHART
	public void setShowAsPie(boolean showAsPie) {
		this.showAsPie = showAsPie;
	}
	
	public boolean getShowAsPie() {
		return this.showAsPie;
	}
	
}
