package de.mpa.client.ui.sharedelements.chart;

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

import de.mpa.client.Client;
import de.mpa.model.analysis.UniProtUtilities;
import de.mpa.model.analysis.UniProtUtilities.Keyword;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.MetaProteinHit;
import de.mpa.model.dbsearch.PeptideHit;
import de.mpa.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.model.dbsearch.ProteinHit;
import de.mpa.model.dbsearch.UniProtEntryMPA;

/**
 * Container class for protein keyword ontology data.
 * 
 * @author T. Muth, A. Behne
 * @date 25-07-2012
 */
public class OntologyData implements ChartData {

	/**
	 * The collection of keyword-specific occurrence maps.
	 */
	private Map<UniProtUtilities.KeywordCategory, Map<String, ArrayList<ProteinHit>>> occMaps;

	/**
	 * The chart type.
	 */
	private ChartType chartType;

	/**
	 * The hierarchy level (protein, peptide or spectrum).
	 */
	private HierarchyLevel hierarchyLevel;

	/**
	 * The relative threshold below which chart segments get grouped into a
	 * category labeled 'Others'.
	 */
	private double limit = 0.01;

	/**
	 * Flag to determine whether proteins grouped under the 'Unknown' tag shall
	 * be excluded in dataset generation and therefore will subsequently not be
	 * displayed in any associated plots.
	 */
	private boolean hideUnknown;

	/**
	 * Flag determining whether the underlying data shall be displayed in a pie
	 * chart or in a bar chart.
	 */
	private boolean showAsPie = true;

	/**
	 * Constructs ontology data from a database search result object using the
	 * default protein hierarchy level.
	 *
	 * @param dbSearchResult
	 *            the database search result object
	 */
	public OntologyData() {
		this(HierarchyLevel.META_PROTEIN_LEVEL);
//		this.init();
	}

	/**
	 * Constructs ontology data from a database search result object and the
	 * specified hierarchy level.
	 *
	 * @param dbSearchResult
	 *            the database search result object
	 * @param hierarchyLevel
	 *            the hierarchy level, one of <code>PROTEIN_LEVEL</code>,
	 *            <code>PEPTIDE_LEVEL</code> or <code>SPECTRUM_LEVEL</code>
	 */
	public OntologyData(HierarchyLevel hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
	}

	/**
	 * Sets up the ontologies.
	 */
	public void init() {
		// Get the ontology map
		Map<String, Keyword> ontologyMap = UniProtUtilities.ONTOLOGY_MAP;
		this.occMaps = new HashMap<UniProtUtilities.KeywordCategory, Map<String, ArrayList<ProteinHit>>>();
		OntologyChart.OntologyChartType[] chartTypes = OntologyChart.OntologyChartType.values();
		List<UniProtUtilities.KeywordCategory> ontologyTypes = new ArrayList<UniProtUtilities.KeywordCategory>();
		for (OntologyChart.OntologyChartType chartType : chartTypes) {
			ontologyTypes.add(chartType.getOntology());
		}
		for (OntologyChart.OntologyChartType type : chartTypes) {
			this.occMaps.put(type.getOntology(), new HashMap<String, ArrayList<ProteinHit>>());
		}

		// Go through DB search result object and add taxonomy information to
		// taxanomy maps --> this is the ontology map ????????
		DbSearchResult data = Client.getInstance().getDatabaseSearchResult();
		for (MetaProteinHit metaProtein : data.getMetaProteins()) {
			for (ProteinHit proteinHit : metaProtein.getProteinHitList()) {
				if (proteinHit.isSelected()) {
					// Get UniProt Entry
					UniProtEntryMPA rupe = proteinHit.getUniProtEntry();
					// booleans for ontology types found
					boolean[] found = new boolean[ontologyTypes.size()];

					// Entry must be provided
					if (rupe != null) {
						List<String> keywords = rupe.getKeywords();
						for (String keyword : keywords) {
							if (ontologyMap.containsKey(keyword)) {
								UniProtUtilities.KeywordCategory ontology = UniProtUtilities.KeywordCategory
																			.valueOf(ontologyMap.get(keyword)
																			.getCategory());
								found[ontologyTypes.indexOf(ontology)] = true;
								this.appendHit(keyword, this.occMaps.get(ontology), metaProtein);
							}
						}
					}
					for (int i = 0; i < ontologyTypes.size(); i++) {
						UniProtUtilities.KeywordCategory ontology = ontologyTypes.get(i);
						if (!found[i]) {
							this.appendHit("Unknown",
									this.occMaps.get(ontology), metaProtein);
						}
					}
				}
			}
		}
		// TODO: maybe merge pairs whose values are identical but differ in
		// their key(word)s?
	}

	/**
	 * Utility method to append a protein hit list (a.k.a. a meta-protein) to
	 * the specified occurrence map.
	 *
	 * @param keyword
	 *            the occurrence map key
	 * @param occMap
	 *            the occurrence map
	 * @param metaProtein
	 *            the protein hit list to append
	 */
	protected void appendHit(String keyword, Map<String, ArrayList<ProteinHit>> occMap, MetaProteinHit metaProtein) {
		ArrayList<ProteinHit> prots = new ArrayList<ProteinHit>(); 
		for (ProteinHit ph : metaProtein.getProteinHitList()) {
			prots.add(ph);
		}
		if (occMap.get(keyword) == null) {
			occMap.put(keyword, prots);
		} else {
			occMap.get(keyword).addAll(metaProtein.getProteinHitList());
		}
//		ArrayList<ProteinHit> metaProteins = occMap.get(keyword);
//		if (metaProteins == null) {
//			metaProteins = new ArrayList<MetaProteinHit>();
//		}
//		if (!metaProteins.contains(metaProtein)) {
//			metaProteins.add(metaProtein);
//		}
//		occMap.put(keyword, metaProteins);
	}

	/**
	 * Returns the pie dataset.
	 *
	 * @return the pie dataset.
	 */
	@Override
	public PieDataset getDataset() {
		// TODO: pre-process dataset generation and return only cached
		// variables
		
		// init
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		pieDataset.setGroup(new DatasetGroup(hierarchyLevel.getTitle()));

		String unknownKey = "Unknown";
		int unknownVal = 0;
		String othersKey = "Others";
		int othersVal = 0;

		// add empty "Unknown" category so it always shows up first (to keep
		// colors consistent)
		pieDataset.setValue(unknownKey, unknownVal);

		// retrieve occurrence map
		Map<String, ArrayList<ProteinHit>> occMap = this.occMaps
				.get(((OntologyChart.OntologyChartType) this.chartType).getOntology());

		// remove cached 'Others' category
		occMap.remove(othersKey);
		Set<Entry<String, ArrayList<ProteinHit>>> entrySet = occMap.entrySet();

		// Calculate for each subgroup of the pie its size
		for (Entry<String, ArrayList<ProteinHit>> entry : entrySet) {
			ArrayList<ProteinHit> metaProteins = entry.getValue();
			Integer absVal = this.getSizeByHierarchy(metaProteins,
					hierarchyLevel);
			Comparable key = entry.getKey();
			pieDataset.setValue(key, absVal);
		}

		ArrayList<ProteinHit> others = new ArrayList<ProteinHit>();
		// add empty 'Others' category so it always shows up last (to keep
		// colors consistent)
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
				ArrayList<ProteinHit> metaProteins = occMap.get(key);
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
			pieDataset.insertValue(pieDataset.getItemCount() - 1, othersKey,
					othersVal);
			occMap.put(othersKey, others);
		} else {
			pieDataset.remove(othersKey);
		}
		return pieDataset;
	}

	/**
	 * Utility method to return size of hierarchically grouped data in a protein
	 * hit list.
	 *
	 * @param proteinHits
	 *            the meta-protein hit list
	 * @param hl
	 *            the hierarchy level, one of <code>PROTEIN_LEVEL</code>,
	 *            <code>PEPTIDE_LEVEL</code> or <code>SPECTRUM_LEVEL</code>
	 * @return the data size
	 */
	private int getSizeByHierarchy(ArrayList<ProteinHit> proteinHits, HierarchyLevel hl) {
		switch (hl) {
		case META_PROTEIN_LEVEL:
			HashSet<String> mpSet = new HashSet<String>();
			for (ProteinHit prot : proteinHits) {
				if (prot.isSelected() && prot.isVisible()) {
					mpSet.add(prot.getMetaProteinHit().getAccession());
				}
			}
			return mpSet.size();
		case PROTEIN_LEVEL: {
			HashSet<ProteinHit> proteinSet = new HashSet<ProteinHit>();
			int count = 0;
			for (ProteinHit prot : proteinHits) {
				if (prot.isSelected() && prot.isVisible()) {
					proteinSet.add(prot);
				}
			}
			return proteinSet.size();
		}
		case PEPTIDE_LEVEL: {
//			Set<ProteinHit> proteinSet = mphl.getProteinSet();
			Set<PeptideHit> peptideSet = new HashSet<PeptideHit>();
			for (ProteinHit prot : proteinHits) {
				if (prot.isSelected() && prot.isVisible()) { 
					for (PeptideHit pep : prot.getPeptideHitList()) {
						if (pep.isSelected() && pep.isVisible()) {
							peptideSet.add(pep);
						}
					}
				}
			}
			return peptideSet.size();
		}
		case SPECTRUM_LEVEL: {
//			Set<ProteinHit> proteinSet = mphl.getProteinSet();
			Set<PeptideSpectrumMatch> matchSet = new HashSet<PeptideSpectrumMatch>();
			for (ProteinHit prot : proteinHits) {
				if (prot.isSelected() && prot.isVisible()) {
					for (PeptideHit pep : prot.getPeptideHitList()) {
						if (pep.isSelected() && pep.isVisible()) {
							for (PeptideSpectrumMatch match : pep.getPeptideSpectrumMatches()) {
								if (match.isSelected() && match.isVisible()) {
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
	public List<ProteinHit> getProteinHits(String key) {
		Map<String, ArrayList<ProteinHit>> occMap = this.occMaps
				.get(((OntologyChart.OntologyChartType) chartType).getOntology());
		if (occMap != null) {
			ArrayList<ProteinHit> phl = occMap.get(key);
			if (phl != null) {
				if (this.hierarchyLevel == HierarchyLevel.META_PROTEIN_LEVEL) {
					return phl;
				} else {
					System.out.println("other hierarchy level");
					// TODO: other hierarchy levels need to be properly processed?
					return phl;
				}
			}
		}
		return null;
	}

	/**
	 * Sets the chart type.
	 * 
	 * @param chartType
	 *            The chart type.
	 */
	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	/**
	 * Sets the hierarchy level of the occurrences to be returned. Either one of
	 * <code>PROTEIN_LEVEL</code>, <code>PEPTIDE_LEVEL</code> or
	 * <code>SPECTRUM_LEVEL</code>
	 * 
	 * @param hierarchyLevel
	 *            the hierarchy level
	 */
	public void setHierarchyLevel(HierarchyLevel hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
	}

	/**
	 * Sets the relative size limit for pie segments.
	 * 
	 * @param limit
	 *            the limit
	 */
	public void setMinorGroupingLimit(double limit) {
		this.limit = limit;
	}

	/**
	 * Sets whether proteins grouped in 'Unknown' category shall be excluded
	 * from dataset creation.
	 * 
	 * @param hideUnknown
	 *            <code>true</code> if 'Unknown' proteins shall be excluded,
	 *            <code>false</code> otherwise
	 */
	public void setHideUnknown(boolean hideUnknown) {
		this.hideUnknown = hideUnknown;
	}

	// TODO: maybe re-factor to allow other types of charts, e.g. getChartType()
	// returning enum members like PIE_CHART and BAR_CHART
	public void setShowAsPie(boolean showAsPie) {
		this.showAsPie = showAsPie;
	}

	public boolean getShowAsPie() {
		return showAsPie;
	}

}
