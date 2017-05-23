package de.mpa.model.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.mpa.client.ui.sharedelements.chart.ChartType;
import de.mpa.client.ui.sharedelements.chart.HierarchyLevel;
import de.mpa.client.ui.sharedelements.chart.OntologyChart;
import de.mpa.client.ui.sharedelements.chart.TaxonomyChart;
import de.mpa.client.ui.sharedelements.chart.TaxonomyChart.TaxonomyChartType;
import de.mpa.model.MPAExperiment;
import de.mpa.model.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.MetaProteinHit;
import de.mpa.model.dbsearch.PeptideHit;
import de.mpa.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.model.taxonomy.TaxonomyNode;

public class CompareExperiments {

	private ChartType typeLevel;
	private Map<String, Long[]> results;
	private ArrayList<MPAExperiment> experiments;
	private HierarchyLevel countLevel;
	private DbSearchResult dbSearchResult;

	public CompareExperiments(ArrayList<MPAExperiment> experiments, DbSearchResult dbSearchResult,
			ChartType typeLevel, HierarchyLevel countLevel) {
		this.typeLevel = typeLevel;
		this.experiments = experiments;
		this.dbSearchResult = dbSearchResult;
		this.countLevel = countLevel;
		this.results = null;
		compare();
	}

	private void compare() {
		try {
			// determine which compare-method to use and apply it based on the
			// countlevel hashString
			if (this.typeLevel == HierarchyLevel.META_PROTEIN_LEVEL) {
				// compare metaproteins
				if (countLevel == HierarchyLevel.SPECTRUM_LEVEL) {
					compareMetaproteinsCountSpectra();
				} else if (countLevel == HierarchyLevel.PEPTIDE_LEVEL) {
					compareMetaproteinsCountPeptides();
				}
			} else if (this.typeLevel == HierarchyLevel.PROTEIN_LEVEL) {
				// compare proteins
				if (countLevel == HierarchyLevel.SPECTRUM_LEVEL) {
					compareProteinsCountSpectra();
				} else if (countLevel == HierarchyLevel.PEPTIDE_LEVEL) {
					compareProteinsCountPeptides();
				}
			} else if (this.typeLevel == HierarchyLevel.PEPTIDE_LEVEL) {
				// compare peptides
				if (countLevel == HierarchyLevel.SPECTRUM_LEVEL) {
					comparePeptidesCountSpectra();
				} else if (countLevel == HierarchyLevel.PEPTIDE_LEVEL) {
					comparePeptidesCountPeptides();
				}
			} else if (this.typeLevel == OntologyChart.OntologyChartType.BIOLOGICAL_PROCESS) {
				// compare biological process
				if (countLevel == HierarchyLevel.SPECTRUM_LEVEL) {
					compareBioProcessCountSpectra();
				} else if (countLevel == HierarchyLevel.PEPTIDE_LEVEL) {
					compareBioProcessCountPeptides();
				}
			} else if (typeLevel == OntologyChart.OntologyChartType.CELLULAR_COMPONENT) {
				// compare cellular component
				if (countLevel == HierarchyLevel.SPECTRUM_LEVEL) {
					compareCellCompCountSpectra();
				} else if (countLevel == HierarchyLevel.PEPTIDE_LEVEL) {
					compareCellCompCountPeptides();
				}
			} else if (typeLevel == OntologyChart.OntologyChartType.MOLECULAR_FUNCTION) {
				// compare molecular function
				if (countLevel == HierarchyLevel.SPECTRUM_LEVEL) {
					compareMolFuncCountSpectra();
				} else if (countLevel == HierarchyLevel.PEPTIDE_LEVEL) {
					compareMolFuncCountPeptides();
				}
			} else if (this.typeLevel instanceof TaxonomyChart.TaxonomyChartType) {
				// compare taxonomy of rank given
				if (countLevel == HierarchyLevel.SPECTRUM_LEVEL) {
					compareTaxonomyCountSpectra();
				} else if (countLevel == HierarchyLevel.PEPTIDE_LEVEL) {
					compareTaxonomyCountPeptides();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void compareMetaproteinsCountSpectra() {
		HashMap<Long, Integer> experimentIndexMap = new HashMap<Long, Integer>();
		int i = 0;
		for (MPAExperiment exper : this.experiments) {
			experimentIndexMap.put(exper.getID(), i);
			i++;
		}
		this.results = new HashMap<String, Long[]>();
		for (MetaProteinHit metaprotein : dbSearchResult.getMetaProteins()) {
			// save metaprotein name as string
			String mpString = metaprotein.getAccession() + metaprotein.getDescription();
			Long[] experiments = new Long[this.experiments.size()];
			for (int j = 0; j < this.experiments.size(); j++) {
				experiments[j] = 0L;
			}
			// new set of spectra for each metaprotein
			HashSet<Long> spectra_found_this_metaprotein = new HashSet<Long>();
			this.results.put(mpString, experiments);
			for (PeptideHit pep : metaprotein.getPeptides()) {
				for (PeptideSpectrumMatch psm : pep.getPeptideSpectrumMatches()) {
					if (!(spectra_found_this_metaprotein.contains(psm.getSpectrumID()))) {
						spectra_found_this_metaprotein.add(psm.getSpectrumID());
						for (Long exp : psm.getExperimentIDs()) {
							experiments[experimentIndexMap.get(exp)] += 1L;
						}
					}
				}
			}
		}
	}

	private void compareMetaproteinsCountPeptides() {
		HashMap<Long, Integer> experimentIndexMap = new HashMap<Long, Integer>();
		int i = 0;
		for (MPAExperiment exper : this.experiments) {
			experimentIndexMap.put(exper.getID(), i);
			i++;
		}
		this.results = new HashMap<String, Long[]>();
		for (MetaProteinHit metaprotein : dbSearchResult.getMetaProteins()) {
			// save metaprotein name as string
			String mpString = metaprotein.getAccession() + metaprotein.getDescription();
			Long[] experiments = new Long[this.experiments.size()];
			for (int j = 0; j < this.experiments.size(); j++) {
				experiments[j] = 0L;
			}
			this.results.put(mpString, experiments);
			for (PeptideHit pep : metaprotein.getPeptides()) {
				for (Long exp : pep.getExperimentIDs()) {
					experiments[experimentIndexMap.get(exp)] += 1L;
				}
			}
		}
	}

	private void compareTaxonomyCountPeptides() {
		// get individual metaproteins, get its taxonomy-node and determine its
		// taxonomy
		//

		for (MetaProteinHit metaprotein : dbSearchResult.getMetaProteins()) {
			//
			metaprotein.getTaxonomyNode();
		}
	}

	private void compareTaxonomyCountSpectra() {

		//create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = new HashMap<Long, Integer>();
		int i = 0;
		for (MPAExperiment exper : this.experiments) {
			experimentIndexMap.put(exper.getID(), i);
			i++;
		}
		
		//init the result map
		this.results = new HashMap<String, Long[]>();		

		//objects for the loop
		//store the column values for the result map
		Long[] resultValues = new Long[this.experiments.size()];
		resultValues = cleanLongArray(resultValues);
		//for ignoring the dublicates we use hashset
		HashSet<Long> spectra_found_this_metaprotein = new HashSet<Long>();
		//for iterating the taxonomy tree
		TaxonomyNode node = null;

		//loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getMetaProteins()) {
			//loop all psms 
			for (PeptideSpectrumMatch psm : metaprotein.getPSMS()) {
				//init start node
				node = psm.getTaxonomyNode();
				//while not the right rank, set it to parent and repeat
				while (!isTaxonomyHierarchyLevel(node, (TaxonomyChartType) this.typeLevel)) {
					if (!node.isRoot()) {
						node = node.getParentNode();
					} else {
						//break condition
						break;
					}
				}
				//last test for the taxonomy rank
				if (isTaxonomyHierarchyLevel(node, (TaxonomyChartType) this.typeLevel)) {
					//for each experiment
					for (long psLong : psm.getExperimentIDs()) {
						//is already inside just increase
						if(results.containsKey(node.getName())){
							results.get(node.getName())[experimentIndexMap.get(psLong)]++;
						}else{
							//else put new row and increase
							results.put(node.getName(), cleanLongArray(new Long[experimentIndexMap.size()]));
							System.out.println();
							results.get(node.getName())[(int)experimentIndexMap.get(psLong)]++;
						}
												
					}
				}
			}
		}
	}
	
	private static Long[] cleanLongArray(Long[] array){
		for (int i = 0; i < array.length; i++) {
			array[i]=(long) 0;
		}
		return array;
	}

	private static boolean isTaxonomyHierarchyLevel(TaxonomyNode node, TaxonomyChartType type) {

		if (node.getRank() == type.getRank())
			return true;
		return false;
	}

	private void compareMolFuncCountPeptides() {
		// TODO Auto-generated method stub

	}

	private void compareMolFuncCountSpectra() {
		// TODO Auto-generated method stub
		this.dbSearchResult.getMetaProteins().get(0).getUniProtEntry().getKeywords();
	}

	private void compareCellCompCountPeptides() {
		// TODO Auto-generated method stub

	}

	private void compareCellCompCountSpectra() {
		// TODO Auto-generated method stub

	}

	private void compareBioProcessCountPeptides() {
		// TODO Auto-generated method stub

	}

	private void compareBioProcessCountSpectra() {
		// TODO Auto-generated method stub

	}

	private void comparePeptidesCountPeptides() {
		// TODO Auto-generated method stub

	}

	private void comparePeptidesCountSpectra() {
		// TODO Auto-generated method stub

	}

	private void compareProteinsCountPeptides() {
		// TODO Auto-generated method stub

	}

	private void compareProteinsCountSpectra() {
		// TODO Auto-generated method stub

	}

	public Map<String, Long[]> getResults() {
		return results;
	}
}
