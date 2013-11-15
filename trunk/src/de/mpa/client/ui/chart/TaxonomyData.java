package de.mpa.client.ui.chart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import de.mpa.analysis.UniprotAccessor;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.taxonomy.Taxonomic;
import de.mpa.taxonomy.TaxonomyNode;

// TODO: merge with OntologyData since both classes share the same methods but differ in the type of occurrence maps they use
public class TaxonomyData implements ChartData {
	
	/**
	 * The hierarchy-level map reference.
	 */
	private Map<HierarchyLevel, Collection<? extends Taxonomic>> hierarchyMap;

	/**
	 * Occurrence map reference.
	 */
	private Map<String, ProteinHitList> occMap;

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
	private HierarchyLevel hierarchyLevel = HierarchyLevel.PROTEIN_LEVEL;
	
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
	 * Empty default constructor.
	 */
	public TaxonomyData() {
	}

	/**
	 * Constructs taxonomy data from a database search result object using the
	 * default protein hierarchy level.
	 * @param dbSearchResult The database search result object.
	 */
	public TaxonomyData(DbSearchResult dbSearchResult) {
		this(dbSearchResult, HierarchyLevel.PROTEIN_LEVEL);
	}

	/**
	 * Constructs taxonomy data from a database search result object and the 
	 * specified hierarchy level.
	 * @param dbSearchResult The database search result object.
	 * @param hierarchyLevel The hierarchy level, one of <code>PROTEIN_LEVEL</code>, 
	 * <code>PEPTIDE_LEVEL</code> or <code>SPECTRUM_LEVEL</code>.
	 */
	public TaxonomyData(DbSearchResult dbSearchResult, HierarchyLevel hierarchyLevel) {
		this.dbSearchResult = dbSearchResult;
		this.hierarchyLevel = hierarchyLevel;
		init();
	}

	/**
	 * This method sets up the taxonomies.
	 * @param dbSearchResult Database search result data.
	 */
	public void init() {
		hierarchyMap = new HashMap<HierarchyLevel, Collection<? extends Taxonomic>>();
		ProteinHitList metaProteins = dbSearchResult.getMetaProteins();
		hierarchyMap.put(HierarchyLevel.META_PROTEIN_LEVEL, metaProteins);
		hierarchyMap.put(HierarchyLevel.PROTEIN_LEVEL, metaProteins.getProteinSet());
		hierarchyMap.put(HierarchyLevel.PEPTIDE_LEVEL, metaProteins.getPeptideSet());
		hierarchyMap.put(HierarchyLevel.SPECTRUM_LEVEL, metaProteins.getMatchSet());
	}

	/**
	 * Utility method to append a protein hit list (a.k.a. a meta-protein) to
	 * the specified occurrence map.
	 * @param keyword the occurrence map key
	 * @param map the occurrence map
	 * @param metaProtein the protein hit list to append
	 */
	protected void appendHit(String keyword, Map<String, ProteinHitList> map, MetaProteinHit metaProtein) {
		ProteinHitList metaProteins = map.get(keyword);
		if (metaProteins == null) {
			metaProteins = new ProteinHitList();
		}
		if (!metaProteins.contains(metaProtein)) {
			metaProteins.add(metaProtein);
		}
		map.put(keyword, metaProteins);
	}

	/**
	 * Returns the pieDataset.
	 * @return
	 */
	@Override
	public PieDataset getDataset() {
		// TODO: pre-process dataset generation and return only cached variables
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		
		// Add empty categories so they always show up first (to keep colors consistent)
		String unknownKey = "Unknown";
		pieDataset.setValue(unknownKey, new Integer(0));
		String othersKey = "Others";
		pieDataset.setValue(othersKey, new Integer(0));
		
		List<String> targetRanks = this.getTargetRanks();

		Collection<? extends Taxonomic> coll = this.hierarchyMap.get(hierarchyLevel);
		List<Taxonomic> knownList = new ArrayList<Taxonomic>();
		List<Taxonomic> unknownTaxa = new ArrayList<Taxonomic>();
		for (Taxonomic taxonomic : coll) {
			TaxonomyNode taxonNode = taxonomic.getTaxonomyNode();
			String rank = taxonNode.getRank().toString().toLowerCase();
			if (targetRanks.contains(rank)) {
				knownList.add(taxonomic);
			} else {
				unknownTaxa.add(taxonomic);
			}
		}
		
		occMap = new HashMap<String, ProteinHitList>();
		final Map<String, Integer> valMap = new HashMap<String, Integer>();
		for (Taxonomic taxonomic : knownList) {
			String topRank = chartType.getTitle().toLowerCase();
			
			String key = taxonomic.getTaxonomyNode().getParentNode(UniprotAccessor.TAXONOMY_RANKS_MAP.get(topRank)).getName();
			
			ProteinHitList hitList = occMap.get(key);
			if (hitList == null) {
				hitList = new ProteinHitList();
			}
			hitList.addAll(this.getProteinHits(taxonomic));
			occMap.put(key, hitList);
			
			Integer value = valMap.get(key);
			value = (value == null) ? 1 : value + 1;
			valMap.put(key, value);
		}
		ProteinHitList unknownHits = new ProteinHitList();
		for (Taxonomic taxonomic : unknownTaxa) {
			unknownHits.addAll(getProteinHits(taxonomic));
		}
		occMap.put(unknownKey, unknownHits);
		valMap.put(unknownKey, unknownTaxa.size());
		
		
		if (hierarchyLevel != HierarchyLevel.META_PROTEIN_LEVEL) {
			// trim redundant elements from protein hit lists
			for (ProteinHitList phl : occMap.values()) {
				Set<ProteinHit> proteinSet = phl.getProteinSet();
				
				phl.clear();
				// FIXME: NullPointer Exception thrown
				phl.addAll(proteinSet);
			}
		}
		
		double total = knownList.size() + unknownTaxa.size();
		ProteinHitList others = new ProteinHitList();
		int othersVal = 0;
		for (Entry<String, Integer> entry : valMap.entrySet()) {
			Integer absVal = entry.getValue();
			double relVal = absVal / total;
			Comparable key;
			if (relVal >= this.limit) {
				key = entry.getKey();
			} else {
				key = othersKey;
				// add grouped hits to list and store it in map they originate from
				// TODO: do this in pre-processing step, e.g. inside init()
				others.addAll(occMap.get(entry.getKey()));
				
				othersVal += absVal;
				absVal = othersVal;
			}
			pieDataset.setValue(key, absVal);
		}
		if (!others.isEmpty()) {
			occMap.put(othersKey, others);
		} else {
			occMap.remove(othersKey);
		}
		
		if (hideUnknown) {
			pieDataset.setValue(unknownKey, 0);
		}
		
		return pieDataset;
	}

	/**
	 * Convenience method to find a protein hit corresponding to the specified
	 * Taxonomic instance.
	 * @param taxonomic the Taxonomic instance
	 * @return a corresponding protein hit
	 */
	private List<ProteinHit> getProteinHits(Taxonomic taxonomic) {
		List<ProteinHit> res = new ArrayList<ProteinHit>();
		if (taxonomic instanceof ProteinHit) {
			// proteins and meta-proteins can be added as-is
			ProteinHit proteinHit = (ProteinHit) taxonomic;
			if (proteinHit.isSelected()) {
				res.add(proteinHit);
			}
		} else if (taxonomic instanceof PeptideHit) {
			// peptides add their parent proteins
			PeptideHit peptideHit = (PeptideHit) taxonomic;
			for (ProteinHit proteinHit : peptideHit.getProteinHits()) {
				if (proteinHit.isSelected()) {
					res.add(proteinHit);
				}
			}
//			res.addAll(peptideHit.getProteinHits());
		} else if (taxonomic instanceof SpectrumMatch) {
			// find match inside all peptides, add corresponding parent proteins
			Collection<? extends Taxonomic> coll = hierarchyMap.get(HierarchyLevel.PEPTIDE_LEVEL);
			for (Taxonomic tax : coll) {
				PeptideHit peptideHit = (PeptideHit) tax;
				for (SpectrumMatch match : peptideHit.getSpectrumMatches()) {
					if (match.equals(taxonomic)) {
						res.addAll(getProteinHits(peptideHit));
						break;
					}
				}
			}
		} else {
			// if we got here something went terribly wrong!
			System.err.println("Unknown Taxonomic instance: " + taxonomic);
		}
		return res;
	}

	/**
	 * Returns the rank type corresponding to the current chart type and all
	 * ranks below it.
	 * @return all target rank types
	 */
	private List<String> getTargetRanks() {
		List<String> targetRanks = new ArrayList<String>(UniprotAccessor.TAXONOMY_RANKS_MAP.keySet());
		String topRank = chartType.getTitle().toLowerCase();
		targetRanks = targetRanks.subList(targetRanks.indexOf(topRank), targetRanks.size());
		return targetRanks;
	}

	@Override
	public ProteinHitList getProteinHits(String key) {
		return occMap.get(key);
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
	 * Clears the occurrences map. 
	 */
	public void clear() {
		if (hierarchyMap != null) {
			hierarchyMap.clear();
		}
	}
	
	/**
	 * Sets the relative size limit for pie segments.
	 * @param limit the limit
	 */
	public void setLimit(double limit) {
		this.limit = limit;
	}

	/**
	 * Excludes proteins grouped in 'Unknown' category from dataset creation.
	 * @param hideUnknown <code>true</code> if 'Unknown' proteins shall be excluded, 
	 * 					  <code>false</code> otherwise
	 */
	public void setHideUnknown(boolean hideUnknown) {
		this.hideUnknown = hideUnknown;
	}
	
}
