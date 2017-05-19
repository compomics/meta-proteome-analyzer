package de.mpa.client.ui.sharedelements.chart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import de.mpa.client.Client;
import de.mpa.client.ui.sharedelements.chart.TaxonomyChart.TaxonomyChartType;
import de.mpa.model.analysis.UniProtUtilities;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.Hit;
import de.mpa.model.dbsearch.MetaProteinHit;
import de.mpa.model.dbsearch.PeptideHit;
import de.mpa.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.model.dbsearch.ProteinHit;
import de.mpa.model.taxonomy.Taxonomic;
import de.mpa.model.taxonomy.TaxonomyNode;

/**
 * Container class for protein taxonomy data.
 * 
 * @author A. Behne
 */
public class TaxonomyData implements ChartData {

	/**
	 * The hierarchy-level map reference (spectra, peptides, proteins, metaproteins).
	 */
	private Map<HierarchyLevel, Collection<? extends Hit>> hierarchyMap;

	/**
	 * Occurrence map reference.
	 */
	private Map<String, ArrayList<ProteinHit>> occMap;

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
	 * Constructs taxonomy data from a database search result object using the
	 * default protein hierarchy level.
	 * @param dbSearchResult The database search result object.
	 */
	public TaxonomyData() {
		this(HierarchyLevel.PROTEIN_LEVEL);
//		this.init();
	}

	/**
	 * Constructs taxonomy data from a database search result object and the 
	 * specified hierarchy level.
	 * @param dbSearchResult The database search result object.
	 * @param hierarchyLevel The hierarchy level, one of <code>PROTEIN_LEVEL</code>, 
	 * <code>PEPTIDE_LEVEL</code> or <code>SPECTRUM_LEVEL</code>.
	 */
	public TaxonomyData(HierarchyLevel hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
	}

	/**
	 * Sets up the taxonomies.
	 */
	public void init() {
		DbSearchResult data = Client.getInstance().getDatabaseSearchResult();
        hierarchyMap = new HashMap<HierarchyLevel, Collection<? extends Hit>>();
        hierarchyMap.put(HierarchyLevel.META_PROTEIN_LEVEL, data.getMetaProteins());
        hierarchyMap.put(HierarchyLevel.PROTEIN_LEVEL, data.getAllProteinHits());
        hierarchyMap.put(HierarchyLevel.PEPTIDE_LEVEL, data.getAllPeptideHits());
        hierarchyMap.put(HierarchyLevel.SPECTRUM_LEVEL, data.getAllPSMS());
		
//		List<Collection<? extends Hit>> hitLists = new ArrayList<>();
//		hitLists.add(this.data);
//		hitLists.add(this.data.getProteinSet());
//		hitLists.add(this.data.getPeptideSet());
//		hitLists.add(this.data.getMatchSet());
//		
//		for (Collection<? extends Hit> hitList : hitLists) {
//			Iterator<? extends Hit> iter = hitList.iterator();
//			while (iter.hasNext()) {
//				Hit hit = iter.next();
//				if (!hit.isSelected()) {
//					iter.remove();
//				}
//			}
//		}
//		this.hierarchyMap.put(HierarchyLevel.META_PROTEIN_LEVEL, hitLists.get(0));
//		this.hierarchyMap.put(HierarchyLevel.PROTEIN_LEVEL, hitLists.get(1));
//		this.hierarchyMap.put(HierarchyLevel.PEPTIDE_LEVEL, hitLists.get(2));
//		this.hierarchyMap.put(HierarchyLevel.SPECTRUM_LEVEL, hitLists.get(3));
	}

	/**
	 * Returns the pieDataset.
	 * @return
	 */
	@Override
	public PieDataset getDataset() {
		// TODO: pre-process dataset generation and return only cached variables
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		pieDataset.setGroup(new DatasetGroup(hierarchyLevel.getTitle()));
		
		// Add empty categories so they always show up first (to keep colors consistent)
		String unknownKey = "Unknown";
		pieDataset.setValue(unknownKey, new Integer(0));
		String othersKey = "Others";
		pieDataset.setValue(othersKey, new Integer(0));

		UniProtUtilities.TaxonomyRank topRank = ((TaxonomyChartType) this.chartType).getRank();
		List<UniProtUtilities.TaxonomyRank> targetRanks = this.getTargetRanks(topRank);

		Collection<? extends Hit> hits = this.hierarchyMap.get(this.hierarchyLevel);
		List<Taxonomic> knownList = new ArrayList<Taxonomic>();
		List<Taxonomic> unknownTaxa = new ArrayList<Taxonomic>();

		// this properly checks the selection status of peptides and spectra based on protein-selection
		for (Hit hit : hits) {
			boolean selected = false;
			if (hit instanceof PeptideHit) {
				PeptideHit pephit = (PeptideHit) hit;
				for (ProteinHit protein : pephit.getProteinHits()) {
					if (protein.isSelected()) {
						selected = true;
						break;
					}
				}
			} else if (hit instanceof PeptideSpectrumMatch) {
				PeptideSpectrumMatch spec = (PeptideSpectrumMatch) hit;
				PeptideHit pephit = spec.getPeptideHit();
				for (ProteinHit protein : pephit.getProteinHits()) {
					if (protein.isSelected()) {
						selected = true;
						break;
					}
				}
			} else {
				if (hit.isSelected()) {
					selected = true;
				}
			}
			if (selected) {
				Taxonomic taxonomic = (Taxonomic) hit;
				TaxonomyNode taxonNode = taxonomic.getTaxonomyNode();
				UniProtUtilities.TaxonomyRank rank = taxonNode.getRank();
				if (targetRanks.contains(rank)) {
					knownList.add(taxonomic);
				} else {
					unknownTaxa.add(taxonomic);
				}
			}
		}

		this.occMap = new HashMap<String, ArrayList<ProteinHit>>();
		final Map<String, Integer> valMap = new HashMap<String, Integer>();
		for (Taxonomic taxonomic : knownList) {
			// extract section key
			String key = taxonomic.getTaxonomyNode().getParentNode(topRank).getName();
			ArrayList<ProteinHit> hitList = this.occMap.get(key);
			if (hitList == null) {
				hitList = new ArrayList<ProteinHit>();
			}
			hitList.addAll(this.getProteinHits(taxonomic));
			this.occMap.put(key, hitList);

			Integer value = valMap.get(key);
			value = (value == null) ? 1 : value + 1;
			valMap.put(key, value);
		}
		// add unknown taxa
		ArrayList<ProteinHit> unknownHits = this.occMap.get(unknownKey);
		if (unknownHits == null) {
			unknownHits = new ArrayList<ProteinHit>();
		}
		for (Taxonomic taxonomic : unknownTaxa) {
			unknownHits.addAll(this.getProteinHits(taxonomic));
		}
		if (unknownHits.size() > 0) {
			this.occMap.put(unknownKey, unknownHits);
			valMap.put(unknownKey, unknownHits.size());
		}

		// XXX: should be non redundant anyways!
//		if (hierarchyLevel != HierarchyLevel.META_PROTEIN_LEVEL) {
//		
//			// trim redundant elements from protein hit lists
//			for (ArrayList<MetaProteinHit> phl : this.occMap.values()) {
//				for (MetProteinHit mph : phl) {
//					Set<MetaProteinHit> proteinSet = phl;
//				}
//
//				phl.clear();
//				// FIXME: NullPointer Exception thrown
//				phl.addAll(proteinSet);
//			}
//		}

		double total = knownList.size() + unknownTaxa.size();
		ArrayList<ProteinHit> others = new ArrayList<ProteinHit>();
		int othersVal = 0;
		for (Entry<String, Integer> entry : valMap.entrySet()) {
			Integer absVal = entry.getValue();
			double relVal = absVal / total;
			Comparable<String> key;
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

		if (this.hideUnknown || (pieDataset.getValue(unknownKey).intValue() == 0)) {
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
		if (taxonomic instanceof MetaProteinHit) {
			// proteins and meta-proteins can be added as-is
			MetaProteinHit mph = (MetaProteinHit) taxonomic;
			if (mph.isSelected()) {
				res.addAll(mph.getProteinHitList());
			}
	    } else if (taxonomic instanceof ProteinHit) {
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
		} else if (taxonomic instanceof PeptideSpectrumMatch) {
			// find match inside all peptides, add corresponding parent proteins
			Collection<? extends Hit> hits = this.hierarchyMap.get(HierarchyLevel.PEPTIDE_LEVEL);
			for (Hit hit : hits) {
				PeptideHit peptideHit = (PeptideHit) hit;
				for (PeptideSpectrumMatch match : peptideHit.getPeptideSpectrumMatches()) {
					if (match.equals(hit)) {
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
	private List<UniProtUtilities.TaxonomyRank> getTargetRanks(UniProtUtilities.TaxonomyRank topRank) {
		List<UniProtUtilities.TaxonomyRank> targetRanks =
				new ArrayList<UniProtUtilities.TaxonomyRank>(UniProtUtilities.TAXONOMY_RANKS_MAP.values());
		targetRanks = targetRanks.subList(targetRanks.indexOf(topRank), targetRanks.size());
		return targetRanks;
	}

	@Override
	public ArrayList<ProteinHit> getProteinHits(String key) {
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
		return showAsPie;
	}
	
}
