package de.mpa.client.ui.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.SortOrder;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.UniProtUtilities.Keyword;
import de.mpa.analysis.UniProtUtilities.KeywordCategory;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
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
	 * The protein hit list backing this data object.
	 */
	private ProteinHitList data;

	/**
	 * The collection of keyword-specific occurrence maps.
	 */
	private Map<KeywordCategory, Map<String, ProteinHitList>> occMaps;
	
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
	 * Sets the data link to the meta-protein list of the specified search
	 * result object and refreshes the underlying dataset.
	 * @param dbSearchResult the database search result object
	 */
	public void setResult(DbSearchResult dbSearchResult) {
		this.setData(dbSearchResult.getMetaProteins());
	}
	
	/**
	 * Sets the data link to the provided protein hit list and refreshes the
	 * underlying dataset.
	 * @param data the protein hit list that will back this data object
	 */
	public void setData(ProteinHitList data) {
		this.data = data;
		this.init();
	}

	/**
	 * Sets up the ontologies.
	 */
	public void init() {
		// Get the ontology map
		Map<String, Keyword> ontologyMap = UniProtUtilities.ONTOLOGY_MAP;
		
		this.occMaps = new HashMap<KeywordCategory, Map<String, ProteinHitList>>();
		OntologyChartType[] chartTypes = OntologyChartType.values();
		List<KeywordCategory> ontologyTypes = new ArrayList<KeywordCategory>();
		for (OntologyChartType chartType : chartTypes) {
			ontologyTypes.add(chartType.getOntology());
		}
		for (OntologyChartType type : chartTypes) {
			this.occMaps.put(type.getOntology(), new HashMap<String, ProteinHitList>());
		}
		
		// Go through DB search result object and add taxonomy information to taxanomy maps
		for (ProteinHit mp : this.data) {
			MetaProteinHit metaProtein = (MetaProteinHit) mp;
			for (ProteinHit proteinHit : metaProtein.getProteinHitList()) {
				if (proteinHit.isSelected()) {
					// Get UniProt Entry
					ReducedUniProtEntry rupe = proteinHit.getUniProtEntry();
					// booleans for ontology types found
					boolean[] found = new boolean[ontologyTypes.size()];
					
					// Entry must be provided
					if (rupe != null) {
						List<String> keywords = rupe.getKeywords();
						for (String keyword : keywords) {
							if (ontologyMap.containsKey(keyword)) {
								KeywordCategory ontology = KeywordCategory.valueOf(
										ontologyMap.get(keyword).getCategory());
								found[ontologyTypes.indexOf(ontology)] = true;
								this.appendHit(keyword, this.occMaps.get(ontology), metaProtein);
							}
						}
					}
					for (int i = 0; i < ontologyTypes.size(); i++) {
						KeywordCategory ontology = ontologyTypes.get(i);
						if (!found[i]) {
							this.appendHit("Unknown", this.occMaps.get(ontology), metaProtein);
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

			// add empty "Unknown" category so it always shows up first (to keep colors consistent)
			pieDataset.setValue(unknownKey, unknownVal);
			
			// retrieve occurrence map
			Map<String, ProteinHitList> occMap =
					this.occMaps.get(((OntologyChartType) this.chartType).getOntology());
			
			// remove cached 'Others' category
			occMap.remove(othersKey);
			Set<Entry<String, ProteinHitList>> entrySet = occMap.entrySet();

			for (Entry<String, ProteinHitList> entry : entrySet) {
				ProteinHitList metaProteins = entry.getValue();
				Integer absVal = this.getSizeByHierarchy(metaProteins, hierarchyLevel);
				Comparable key = entry.getKey();
				pieDataset.setValue(key, absVal);
			}
			

			ProteinHitList others = new ProteinHitList();
			// add empty 'Others' category so it always shows up last (to keep colors consistent)
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
		case PROTEIN_LEVEL: {
			Set<ProteinHit> proteinSet = mphl.getProteinSet();
			int count = 0;
			for (ProteinHit prot : proteinSet) {
				if (prot.isSelected()) {
					count++;
				}
			}
			return count;
//			return proteinSet.size();
		}
		case PEPTIDE_LEVEL: {
			Set<ProteinHit> proteinSet = mphl.getProteinSet();
			Set<PeptideHit> peptideSet = new HashSet<PeptideHit>();
			for (ProteinHit prot : proteinSet) {
				if (prot.isSelected()) {
					for (PeptideHit pep : prot.getPeptideHitList()) {
						if (pep.isSelected()) {
							peptideSet.add(pep);
						}
					}
				}
			}
			return peptideSet.size();
		}
		case SPECTRUM_LEVEL: {
			Set<ProteinHit> proteinSet = mphl.getProteinSet();
			Set<SpectrumMatch> matchSet = new HashSet<SpectrumMatch>();
			for (ProteinHit prot : proteinSet) {
				if (prot.isSelected()) {
					for (PeptideHit pep : prot.getPeptideHitList()) {
						if (pep.isSelected()) {
							for (SpectrumMatch match : pep.getSpectrumMatches()) {
								if (match.isSelected()) {
									matchSet.add(match);
								}
							}
						}
					}
				}
			}
			return matchSet.size();
		}
		default:
			return 0;
		}
	}
	
	@Override
	public ProteinHitList getProteinHits(String key) {
		Map<String, ProteinHitList> occMap =
				this.occMaps.get(((OntologyChartType) this.chartType).getOntology());
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
