package de.mpa.model.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.mpa.client.ui.sharedelements.chart.ChartType;
import de.mpa.client.ui.sharedelements.chart.HierarchyLevel;
import de.mpa.client.ui.sharedelements.chart.OntologyChart;
import de.mpa.client.ui.sharedelements.chart.TaxonomyChart;
import de.mpa.model.MPAExperiment;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.MetaProteinHit;
import de.mpa.model.dbsearch.PeptideHit;
import de.mpa.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.model.dbsearch.ProteinHit;

public class CompareExperiments {

	private ChartType typeLevel;
	private Map<String, Long[]> results;
	private ArrayList<MPAExperiment> experiments;
	private HierarchyLevel countLevel;
	private DbSearchResult dbSearchResult;

	public CompareExperiments(ArrayList<MPAExperiment> experiments, DbSearchResult dbSearchResult, ChartType typeLevel,
			HierarchyLevel countLevel) {
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
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);

		this.results = new HashMap<String, Long[]>();
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
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
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);
		this.results = new HashMap<String, Long[]>();
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
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
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);

		// init the result map
		this.results = new HashMap<String, Long[]>();

		HashSet<PeptideHit> set = new HashSet<>();
		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			for (PeptideHit peptide : metaprotein.getPeptides()) {
				set.add(peptide);
			}
			for (PeptideHit peptide : set) {
				CompareUtil.countTaxonomyNodes(experimentIndexMap, peptide.getTaxonomyNode(),
						peptide.getExperimentIDs(), this.results, this.typeLevel);
			}
			set.clear();
		}
	}

	private void compareTaxonomyCountSpectra() {

		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);

		// init the result map
		this.results = new HashMap<String, Long[]>();

		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			// loop all psms
			for (PeptideSpectrumMatch psm : metaprotein.getPSMS()) {
				// counts the nodex with the searched taxonomyrank
				CompareUtil.countTaxonomyNodes(experimentIndexMap, psm.getTaxonomyNode(), psm.getExperimentIDs(),
						results, this.typeLevel);
			}
		}
	}

	private void compareMolFuncCountPeptides() {
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);

		// init the result map
		this.results = new HashMap<String, Long[]>();

		// hashset to get rid of dublicates
		HashSet<PeptideHit> set = new HashSet<>();

		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			// loop peptide list, but it contains dublicates
			for (PeptideHit peptide : metaprotein.getPeptides()) {
				// if not already processed
				if (!set.contains(peptide)) {
					// count the value
					CompareUtil.countFunctionElements(experimentIndexMap, peptide.getExperimentIDs(),
							peptide.getProperties(OntologyChart.OntologyChartType.MOLECULAR_FUNCTION), results);
					// add to already processed set of peptides
					set.add(peptide);
				}

			}
			// clear the set
			set.clear();
		}
	}

	private void compareMolFuncCountSpectra() {
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);

		// init the result map
		this.results = new HashMap<String, Long[]>();

		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			// loop every psm
			for (PeptideSpectrumMatch psm : metaprotein.getPSMS()) {
				// increase the result
				CompareUtil.countFunctionElements(experimentIndexMap, psm.getExperimentIDs(),
						psm.getPeptideHit().getProperties(OntologyChart.OntologyChartType.MOLECULAR_FUNCTION), results);
			}
		}
	}

	private void compareCellCompCountPeptides() {
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);

		// init the result map
		this.results = new HashMap<String, Long[]>();

		// hashset to get rid of dublicates
		HashSet<PeptideHit> set = new HashSet<>();

		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			// loop peptide list, but it contains dublicates
			for (PeptideHit peptide : metaprotein.getPeptides()) {
				// if not already processed
				if (!set.contains(peptide)) {
					// count the value
					CompareUtil.countFunctionElements(experimentIndexMap, peptide.getExperimentIDs(),
							peptide.getProperties(OntologyChart.OntologyChartType.CELLULAR_COMPONENT), results);
					// add to already processed set of peptides
					set.add(peptide);
				}
			}
			// clear the set
			set.clear();
		}
	}

	private void compareCellCompCountSpectra() {
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);
		// init the result map
		this.results = new HashMap<String, Long[]>();
		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			// loop every psm
			for (PeptideSpectrumMatch psm : metaprotein.getPSMS()) {
				// increase the result
				CompareUtil.countFunctionElements(experimentIndexMap, psm.getExperimentIDs(),
						psm.getPeptideHit().getProperties(OntologyChart.OntologyChartType.CELLULAR_COMPONENT), results);
			}
		}
	}

	private void compareBioProcessCountPeptides() {
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);

		// init the result map
		this.results = new HashMap<String, Long[]>();

		// hashset to get rid of dublicates
		HashSet<PeptideHit> set = new HashSet<>();

		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			// loop peptide list, but it contains dublicates
			for (PeptideHit peptide : metaprotein.getPeptides()) {
				// if not already processed
				if (!set.contains(peptide)) {
					// count the value
					CompareUtil.countFunctionElements(experimentIndexMap, peptide.getExperimentIDs(),
							peptide.getProperties(OntologyChart.OntologyChartType.BIOLOGICAL_PROCESS), results);
					// add to already processed set of peptides
					set.add(peptide);
				}
			}
			// clear the set
			set.clear();
		}
	}

	private void compareBioProcessCountSpectra() {
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);
		// init the result map
		this.results = new HashMap<String, Long[]>();
		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			// loop every psm
			for (PeptideSpectrumMatch psm : metaprotein.getPSMS()) {
				// increase the result
				CompareUtil.countFunctionElements(experimentIndexMap, psm.getExperimentIDs(),
						psm.getPeptideHit().getProperties(OntologyChart.OntologyChartType.BIOLOGICAL_PROCESS), results);
			}
		}
	}

	private void comparePeptidesCountPeptides() {
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);

		// init the result map
		this.results = new HashMap<String, Long[]>();

		// hashset to get rid of dublicates
		HashSet<PeptideHit> set = new HashSet<>();

		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			// loop peptide list, but it contains dublicates
			for (PeptideHit peptide : metaprotein.getPeptides()) {
				// if not already processed
				if (!set.contains(peptide)) {
					for (long psLong : peptide.getExperimentIDs()) {
						// is already inside just increase

						if (results.containsKey(peptide.getSequence())) {
							results.get(peptide.getSequence())[experimentIndexMap.get(psLong)]++;
						} else {
							// else put new row and increase
							results.put(peptide.getSequence(),
									CompareUtil.cleanLongArray(new Long[experimentIndexMap.size()]));
							results.get(peptide.getSequence())[(int) experimentIndexMap.get(psLong)]++;
						}
					}
					// add to already processed set of peptides
					set.add(peptide);
				}
			}
			// clear the set
			set.clear();
		}
	}

	private void comparePeptidesCountSpectra() {
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);
		// init the result map
		this.results = new HashMap<String, Long[]>();
		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			// loop every psm
			for (PeptideSpectrumMatch psm : metaprotein.getPSMS()) {
				// increase the result
				for (long psLong : psm.getExperimentIDs()) {
					// is already inside just increase
					if (results.containsKey(psm.getTitle())) {
						results.get(psm.getTitle())[experimentIndexMap.get(psLong)]++;
					} else {
						// else put new row and increase
						results.put(psm.getTitle(), CompareUtil.cleanLongArray(new Long[experimentIndexMap.size()]));
						results.get(psm.getTitle())[(int) experimentIndexMap.get(psLong)]++;
					}
				}
			}
		}
	}

	private void compareProteinsCountPeptides() {
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);

		// init the result map
		this.results = new HashMap<String, Long[]>();

		// hashset to get rid of dublicates of proteins
		HashSet<ProteinHit> proteinSet = new HashSet<>();

		// hashset to get rid of dublicates of peptides
		HashSet<PeptideHit> peptideSet = new HashSet<>();

		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			for (ProteinHit protein : metaprotein.getProteinHitList()) {
				// if not already processed protein
				if (!proteinSet.contains(protein)) {
					// loop every peptide of the protein
					for (PeptideHit peptide : protein.getPeptideHitList()) {
						// if not already processed peptide
						if (!peptideSet.contains(peptide)) {
							// increase the result
							CompareUtil.countProteinElements(experimentIndexMap, protein, results, peptide.getExperimentIDs());
							// add to already processed set of peptides
							peptideSet.add(peptide);
						}
					}
					// clear the peptide Set
					peptideSet.clear();
					// add to already processed set of proteins
					proteinSet.add(protein);
				}
			}
			// clear the protein Set
			proteinSet.clear();
		}
	}

	private void compareProteinsCountSpectra() {
		// create index experimentIDs to Index of the column map
		HashMap<Long, Integer> experimentIndexMap = CompareUtil.createIndexHashMapForExperiments(experiments);

		// init the result map
		this.results = new HashMap<String, Long[]>();

		// hashset to get rid of dublicates of proteins
		HashSet<ProteinHit> proteinSet = new HashSet<>();

		// hashset to get rid of dublicates of peptides
		HashSet<PeptideSpectrumMatch> psmSet = new HashSet<>();

		// loop every metaproteine
		for (MetaProteinHit metaprotein : dbSearchResult.getVisibleMetaProteins()) {
			for (ProteinHit protein : metaprotein.getProteinHitList()) {
				// if not already processed protein
				if (!proteinSet.contains(protein)) {
					// loop every peptide of the protein
					for (PeptideSpectrumMatch psm : protein.getPSMs()) {
						// if not already processed peptide
						if (!psmSet.contains(psm)) {
							// increase the result
							CompareUtil.countProteinElements(experimentIndexMap, protein, results, psm.getPeptideHit().getExperimentIDs());
							// add to already processed set of peptides
							psmSet.add(psm);
						}
					}
					// clear the peptide Set
					psmSet.clear();
					// add to already processed set of proteins
					proteinSet.add(protein);
				}
			}
			// clear the protein Set
			proteinSet.clear();
		}
	}

	public Map<String, Long[]> getResults() {
		return results;
	}
}
