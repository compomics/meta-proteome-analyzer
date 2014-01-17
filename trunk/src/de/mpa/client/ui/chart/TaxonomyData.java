package de.mpa.client.ui.chart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.analysis.taxonomy.Taxonomic;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.ui.chart.TaxonomyChart.TaxonomyChartType;

/**
 * Container class for protein taxonomy data.
 * 
 * @author A. Behne
 */
public class TaxonomyData implements ChartData {

	/**
	 * The protein hit list backing this data object.
	 */
	private ProteinHitList data;
	
	/**
	 * The hierarchy-level map reference.
	 */
	private Map<HierarchyLevel, Collection<? extends Taxonomic>> hierarchyMap;

	/**
	 * Occurrence map reference.
	 */
	private Map<String, ProteinHitList> occMap;

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
	 * Flag determining whether the underlying data shall be displayed in a pie chart or in a bar chart.
	 */
	private boolean showAsPie = true;

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
	 * Sets up the taxonomies.
	 */
	public void init() {
		this.hierarchyMap = new HashMap<HierarchyLevel, Collection<? extends Taxonomic>>();
		this.hierarchyMap.put(HierarchyLevel.META_PROTEIN_LEVEL, this.data);
		this.hierarchyMap.put(HierarchyLevel.PROTEIN_LEVEL, this.data.getProteinSet());
		this.hierarchyMap.put(HierarchyLevel.PEPTIDE_LEVEL, this.data.getPeptideSet());
		this.hierarchyMap.put(HierarchyLevel.SPECTRUM_LEVEL, this.data.getMatchSet());
	}

	/**
	 * Returns the pieDataset.
	 * @return
	 */
	@Override
	public PieDataset getDataset() {
		// TODO: pre-process dataset generation and return only cached variables
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		pieDataset.setGroup(new DatasetGroup(this.hierarchyLevel.getTitle()));
		
		// Add empty categories so they always show up first (to keep colors consistent)
		String unknownKey = "Unknown";
		pieDataset.setValue(unknownKey, new Integer(0));
		String othersKey = "Others";
		pieDataset.setValue(othersKey, new Integer(0));

		TaxonomyRank topRank = ((TaxonomyChartType) this.chartType).getRank();
		List<TaxonomyRank> targetRanks = this.getTargetRanks(topRank);

		Collection<? extends Taxonomic> coll = this.hierarchyMap.get(this.hierarchyLevel);
		List<Taxonomic> knownList = new ArrayList<Taxonomic>();
		List<Taxonomic> unknownTaxa = new ArrayList<Taxonomic>();
		for (Taxonomic taxonomic : coll) {
			TaxonomyNode taxonNode = taxonomic.getTaxonomyNode();
			TaxonomyRank rank = taxonNode.getRank();
			if (targetRanks.contains(rank)) {
				knownList.add(taxonomic);
			} else {
				unknownTaxa.add(taxonomic);
			}
		}
		
		this.occMap = new HashMap<String, ProteinHitList>();
		final Map<String, Integer> valMap = new HashMap<String, Integer>();
		for (Taxonomic taxonomic : knownList) {
			// extract section key
			String key = taxonomic.getTaxonomyNode().getParentNode(topRank).getName();
			
			ProteinHitList hitList = this.occMap.get(key);
			if (hitList == null) {
				hitList = new ProteinHitList();
			}
			hitList.addAll(this.getProteinHits(taxonomic));
			this.occMap.put(key, hitList);
			
			Integer value = valMap.get(key);
			value = (value == null) ? 1 : value + 1;
			valMap.put(key, value);
		}
		// add unknown taxa
		ProteinHitList unknownHits = this.occMap.get(unknownKey);
		if (unknownHits == null) {
			unknownHits = new ProteinHitList();
		}
		for (Taxonomic taxonomic : unknownTaxa) {
			unknownHits.addAll(this.getProteinHits(taxonomic));
		}
		if (unknownHits.size() > 0) {
			this.occMap.put(unknownKey, unknownHits);
			valMap.put(unknownKey, unknownHits.size());
		}
		
		
		if (hierarchyLevel != HierarchyLevel.META_PROTEIN_LEVEL) {
			// trim redundant elements from protein hit lists
			for (ProteinHitList phl : this.occMap.values()) {
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
				others.addAll(this.occMap.get(entry.getKey()));
				
				othersVal += absVal;
				absVal = othersVal;
			}
			pieDataset.setValue(key, absVal);
		}
		if (!others.isEmpty()) {
			this.occMap.put(othersKey, others);
		} else {
			this.occMap.remove(othersKey);
			pieDataset.remove(othersKey);
		}
		
		if (this.hideUnknown) {
//			pieDataset.setValue(unknownKey, 0);
			pieDataset.remove(unknownKey);
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
			Collection<? extends Taxonomic> coll = this.hierarchyMap.get(HierarchyLevel.PEPTIDE_LEVEL);
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
	private List<TaxonomyRank> getTargetRanks(TaxonomyRank topRank) {
		List<TaxonomyRank> targetRanks =
				new ArrayList<TaxonomyRank>(UniProtUtilities.TAXONOMY_RANKS_MAP.values());
		targetRanks = targetRanks.subList(targetRanks.indexOf(topRank), targetRanks.size());
		return targetRanks;
	}

	@Override
	public ProteinHitList getProteinHits(String key) {
		return this.occMap.get(key);
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
		if (this.hierarchyMap != null) {
			this.hierarchyMap.clear();
		}
	}
	
	/**
	 * Sets the relative size limit for pie segments.
	 * @param limit the limit
	 */
	public void setMinorGroupingLimit(double limit) {
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

	public void setShowAsPie(boolean showAsPie) {
		this.showAsPie = showAsPie;
	}
	
	public boolean getShowAsPie() {
		return this.showAsPie;
	}
	
}
