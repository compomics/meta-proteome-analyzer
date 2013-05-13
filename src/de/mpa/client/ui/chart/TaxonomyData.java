package de.mpa.client.ui.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.ui.chart.TaxonomyPieChart.TaxonomyChartType;
import de.mpa.taxonomy.NcbiTaxonomy;

// TODO: merge with OntologyData since both classes share the same methods but differ in the type of occurrence maps they use
public class TaxonomyData implements ChartData {

	/**
	 * Taxonomy kingdom occurrences map.
	 */
	private Map<String, ProteinHitList> kingdomOccMap;

	/**
	 * Taxonomy phylum occurrences map.
	 */
	private Map<String, ProteinHitList> phylumOccMap;

	/**
	 * Taxonomy class occurrences map.
	 */
	private Map<String, ProteinHitList> classOccMap;

	/**
	 * Taxonomy order occurrences map.
	 */
	private Map<String, ProteinHitList> orderOccMap;

	/**
	 * Taxonomy family occurrences map.
	 */
	private Map<String, ProteinHitList> familyOccMap;

	/**
	 * Taxonomy genus occurrences map.
	 */
	private Map<String, ProteinHitList> genusOccMap;

	/**
	 * Taxonomy species occurrences map.
	 */
	private Map<String, ProteinHitList> speciesOccMap;

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
	 * Flag to determine whether proteins grouped under the 'Unknown' tag shall
	 * be excluded in dataset generation and therefore will subsequently not be
	 * displayed in any associated plots.
	 */
	private boolean hideUnknown;

	/**
	 * Enumeration holding taxonomic ranks.
	 */
	private enum RankList {
		KINGDOM, PHYLUM, CLASS,	ORDER, FAMILY, GENUS, SPECIES
	}


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

		kingdomOccMap = new HashMap<String, ProteinHitList>();
		phylumOccMap = new HashMap<String, ProteinHitList>();
		classOccMap = new HashMap<String, ProteinHitList>();
		orderOccMap = new HashMap<String, ProteinHitList>();
		familyOccMap = new HashMap<String, ProteinHitList>();
		genusOccMap = new HashMap<String, ProteinHitList>();
		speciesOccMap = new HashMap<String, ProteinHitList>();

		

		//TODO Robbie new Taxonomy
		// Map to match string rank to ENUM ranks for the switch
		Map<String, Enum> rankMap = new HashMap<String, Enum>();
		rankMap.put("kingdom",	RankList.KINGDOM);
		rankMap.put("phylum",	RankList.PHYLUM);
		rankMap.put("class",	RankList.CLASS);
		rankMap.put("order",	RankList.ORDER);
		rankMap.put("family",	RankList.FAMILY);
		rankMap.put("genus",	RankList.GENUS);
		rankMap.put("species",	RankList.SPECIES);

		NcbiTaxonomy ncbiTax = NcbiTaxonomy.getInstance();
		
		// Go through dbsearch result object and add taxonomy information to taxanomy maps.
		for (ProteinHit proteinHit : dbSearchResult.getProteinHitList()) {

			// Get UniProt Entry
			UniProtEntry entry = proteinHit.getUniprotEntry();

			// booleans for type of found taxonomy
			boolean kingFound, phylFound, clasFound, ordFound, famFound, genFound, specFound;
			kingFound = phylFound = clasFound = specFound = ordFound = famFound = genFound = false;

			if (entry != null) {
				// Get taxonomy ID
				int taxID = Integer.valueOf(entry.getNcbiTaxonomyIds().get(0).getValue());
				while (taxID != 1) {
					try {
						String rank = ncbiTax.getRank(taxID);
						String name = ncbiTax.getTaxonName(taxID);
						RankList rankType = (RankList) rankMap.get(rank);
						
						if (rankType != null) {
							//ADD to taxonomy File
							switch (rankType) {
							case KINGDOM:
								appendHit(name, kingdomOccMap, proteinHit);
								kingFound = true;
								break;
							case PHYLUM:
								appendHit(name, phylumOccMap, proteinHit);
								phylFound = true;
								break;
							case CLASS:
								appendHit(name, classOccMap, proteinHit);
								clasFound = true;
								break;
							case ORDER:
								appendHit(name, orderOccMap, proteinHit);
								ordFound = true;
								break;
							case FAMILY:
								appendHit(name, familyOccMap, proteinHit);
								famFound = true;
								break;
							case GENUS:
								appendHit(name, genusOccMap, proteinHit);
								genFound = true;
								break;
							case SPECIES:
								appendHit(name, speciesOccMap, proteinHit);
								specFound = true;
								break;
							}
						}
						//  Get parent tax ID
						taxID = ncbiTax.getParentTaxID(taxID);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// ADD unknown
				if (!kingFound) {
					appendHit("Unknown", kingdomOccMap, proteinHit);
				}
				if (!phylFound) {
					appendHit("Unknown", phylumOccMap, proteinHit);
				}
				if (!clasFound) {
					appendHit("Unknown", classOccMap, proteinHit);
				}
				if (!ordFound) {
					appendHit("Unknown", orderOccMap, proteinHit);
				}
				if (!famFound) {
					appendHit("Unknown", familyOccMap, proteinHit);
				}
				if (!genFound) {
					appendHit("Unknown", genusOccMap, proteinHit);
				}
				if (!specFound) {
					appendHit("Unknown", speciesOccMap, proteinHit);
				}
			}
		}
	}

	/**
	 * Utility method to append a protein hit to the specified occurrence map.
	 * @param keyword The key for an entry in the occurrence map.
	 * @param map The occurrence map.
	 * @param proteinHit The protein hit to append.
	 */
	protected void appendHit(String keyword, Map<String, ProteinHitList> map, ProteinHit proteinHit) {
		ProteinHitList protHits = map.get(keyword);
		if (protHits == null) {
			protHits = new ProteinHitList();
		}
		protHits.add(proteinHit);
		map.put(keyword, protHits);
	}

	/**
	 * This mapping has to be done due to inconsistent Uniprot data entries.
	 * @param taxonName The taxon name.
	 * @return The rule based taxon name.
	 */
	private String getRuleBasedMapping(String taxonName) {
		// Baciallales and Lactobaciallales do not provide sufficient information.
		if (taxonName.equals("Bacillales")) {
			taxonName = "Bacilli";
		} else if (taxonName.equals("Lactobacillales")) {
			taxonName = "Bacilli";
		}
		return taxonName;
	}

	/**
	 * Returns the pieDataset.
	 * @return
	 */
	public PieDataset getDataset() {
		// TODO: pre-process dataset generation and return only cached variables
		DefaultPieDataset pieDataset = new DefaultPieDataset();

		Map<String, ProteinHitList> map = null;
		switch ((TaxonomyChartType) chartType) {
		case KINGDOM:
			map = kingdomOccMap;
			break;
		case PHYLUM:
			map = phylumOccMap;
			break;
		case CLASS:
			map = classOccMap;
			break;
		case ORDER:
			map = orderOccMap;
			break;
		case FAMILY:
			map = familyOccMap;
			break;
		case GENUS:
			map = genusOccMap;
			break;
		case SPECIES:
			map = speciesOccMap;
			break;
		}
		Set<Entry<String, ProteinHitList>> entrySet = map.entrySet();

		int sumValues = 0;
		for (Entry<String, ProteinHitList> entry : entrySet) {
			sumValues += getSizeByHierarchy(entry.getValue(), hierarchyLevel);
		}

		ProteinHitList others = new ProteinHitList();
		double limit = 0.01;
		for (Entry<String, ProteinHitList> entry : entrySet) {
			Integer absVal = getSizeByHierarchy(entry.getValue(), hierarchyLevel);
			double relVal = absVal * 1.0 / sumValues * 1.0;
			Comparable key;
			if (relVal >= limit) {
				key = entry.getKey();
			} else {
				key = "Others";
				// add grouped hits to list and store it in map they originate from
				// TODO: do this in pre-processing step, e.g. inside init()
				others.addAll(entry.getValue());

				absVal = getSizeByHierarchy(others, hierarchyLevel);
			}
			pieDataset.setValue(key, absVal);
		}
		if (!others.isEmpty() && !map.containsKey("Others")) {
			map.put("Others", others);
		}
		if (hideUnknown) {
			pieDataset.setValue("Unknown", new Integer(0));
		}

		return pieDataset;
	}

	/**
	 * Utility method to return size of hierarchically grouped data in a protein
	 * hit list.
	 * @param phl the protein hit list
	 * @param hl the hierarchy level, one of <code>PROTEIN_LEVEL</code>, 
	 * <code>PEPTIDE_LEVEL</code> or <code>SPECTRUM_LEVEL</code>
	 * @return the data size
	 */
	private int getSizeByHierarchy(ProteinHitList phl, HierarchyLevel hl) {
		switch (hl) {
		case PROTEIN_LEVEL:
			return phl.size();
		case PEPTIDE_LEVEL:
			return phl.getPeptideSet().size();
		case SPECTRUM_LEVEL:
			return phl.getMatchSet().size();
		default:
			return 0;
		}
	}

	@Override
	public List<ProteinHit> getProteinHits(String key) {
		switch ((TaxonomyChartType) chartType) {
		case KINGDOM:
			return kingdomOccMap.get(key);
		case PHYLUM:
			return phylumOccMap.get(key);
		case CLASS:
			return classOccMap.get(key);
		case ORDER:
			return orderOccMap.get(key);
		case FAMILY:
			return familyOccMap.get(key);
		case GENUS:
			return genusOccMap.get(key);
		case SPECIES:
			return speciesOccMap.get(key);
		default:
			return null;
		}
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
		if (kingdomOccMap != null)	kingdomOccMap.clear();
		if (phylumOccMap != null)	phylumOccMap.clear();
		if (classOccMap != null)	classOccMap.clear();
		if (orderOccMap != null)	orderOccMap.clear();
		if (familyOccMap != null)	familyOccMap.clear();
		if (genusOccMap != null)	genusOccMap.clear();
		if (speciesOccMap != null)	speciesOccMap.clear();
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
