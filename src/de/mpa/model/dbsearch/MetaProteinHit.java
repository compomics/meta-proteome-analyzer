package de.mpa.model.dbsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.mpa.client.ui.resultspanel.ComparePanel.CompareData;
import de.mpa.client.ui.sharedelements.chart.ChartType;
import de.mpa.client.ui.sharedelements.chart.HierarchyLevel;
import de.mpa.client.ui.sharedelements.chart.OntologyChart.OntologyChartType;
import de.mpa.client.ui.sharedelements.chart.TaxonomyChart.TaxonomyChartType;
import de.mpa.model.analysis.UniProtUtilities;
import de.mpa.model.analysis.UniProtUtilities.Keyword;
import de.mpa.model.analysis.UniProtUtilities.KeywordCategory;
import de.mpa.model.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.model.taxonomy.Taxonomic;
import de.mpa.model.taxonomy.TaxonomyNode;

/**
 * Wrapper class for meta-proteins.
 * 
 * @author A. Behne, kay
 */
public class MetaProteinHit implements Serializable, Comparable<MetaProteinHit>, Taxonomic, Hit  {

	/*
	 * FIELDS
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The database IDs of the experiments which contain the protein hit.
	 */
	private Set<Long> experimentIDs;
	
	/**
	 *  Flag denoting whether this protein is selected for export.
	 */
	private boolean selected = true;
	
	/**
	 * The protein hit list of this meta-protein.
	 */
	private final ArrayList<ProteinHit> proteinHits;

	/**
	 * The UniProt entry hit object containing additional meta-data.
	 */
	private UniProtEntryMPA uniProtEntry = null;

	/**
	 * Protein accession.
	 */
	private String accession;
	
	/**
	 * Protein description.
	 */
	private String description;

	/**
	 * The common taxonomy node.
	 */
//	private TaxonomyNode taxonomyNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root");
	private TaxonomyNode taxonomyNode;

	/*
	 * CONSTRUCTOR 
	 */

	/**
	 * Constructs a meta-protein from the specified identifier string and
	 * protein hit list.
	 * @param identifier the identifier string
	 * @param phl the protein hit list
	 * @param upa. Uniprotentry to initialize this metaprotein with.
	 */
	public MetaProteinHit(String identifier, ProteinHit ph, UniProtEntryMPA upa) {
		this.setAccession(identifier);
		this.proteinHits = new ArrayList<ProteinHit>();
		this.proteinHits.add(ph);
		this.experimentIDs = new HashSet<Long>();
	}

	/*
	 * METHODS
	 * 
	 * FDR METHOD
	 */

	/**
	 * This method filter by FDR threshold for all visible protein hits.
	 * @param fdr the FDR threshold
	 */
	@Override
	public void setFDR(double fdr) {
		ArrayList<ProteinHit> newProteinHits = new ArrayList<ProteinHit>();
		for (ProteinHit ph : proteinHits) {
			ph.setFDR(fdr);
			if (ph.isVisible()) {
				newProteinHits.add(ph);
			}
		}
		proteinHits.clear();
		proteinHits.addAll(newProteinHits);
	}
	
	@Override
	public boolean isVisible() {
		return !proteinHits.isEmpty();
	}

	/*
	 * METHODS
	 * 
	 * PROTEIN HITS
	 */

	/**
	 * Returns the list of proteins associated with this meta-protein.
	 * @return the protein list
	 */
	public ArrayList<ProteinHit> getProteinHitList() {
		return this.proteinHits;
	}
	
	/**
	 * Returns a protein hit by its accession.
	 * @param accession the protein accession
	 * @return the protein hit
	 */
	public ProteinHit getProteinHit(String accession) {
		for (ProteinHit ph : this.proteinHits) {
			if (ph.getAccession().equals(accession)) {
				return ph;
			}
		}
		return null;
	}
	
	public void addProteinHit(ProteinHit prot) {
		this.proteinHits.add(prot);
	}

	/*
	 * METHODS
	 *
	 * PEPTIDE HITS 
	 */

	public ArrayList<PeptideHit> getPeptides() {
		HashSet<PeptideHit> pepset = new HashSet<PeptideHit>();
		for (ProteinHit ph : proteinHits) {
			pepset.addAll(ph.getPeptideHitList());
		}
		ArrayList<PeptideHit> peptides = new ArrayList<PeptideHit>();
		peptides.addAll(pepset);
		return peptides;
	}
	
	public PeptideHit getPeptideHit(String sequence) {
		// Check all peptides whether they are visible (FDR alright or not)
		PeptideHit peptide = null;
		for (ProteinHit protein : proteinHits) {
			peptide = protein.getPeptideHit(sequence);
			if (peptide != null) {
				break;
			}
		}
		return peptide;
	}
	
	/*
	 * METHODS
	 *
	 * PSMS
	 */
	
	public HashSet<PeptideSpectrumMatch> getPSMS() {
		HashSet<PeptideSpectrumMatch> psms = new HashSet<PeptideSpectrumMatch>();
		for (ProteinHit ph : proteinHits) {
			for (PeptideHit pep : ph.getPeptideHitList()) {
				psms.addAll(pep.getPeptideSpectrumMatches());
			}
		}
		return psms;
	}

	/*
	 * METHODS
	 *
	 * UNIPROTENTRY
	 */

	/**
	 * Sets an UniProt entry.
	 * @param uniprotEntry The UniprotentryAccessor object
	 */
	public void setUniprotEntry(UniProtEntryMPA uniprotEntry) {
		this.uniProtEntry = uniprotEntry;
	}

	/**
	 * Returns the UniProt entry.
	 * @return The UniProtEntry object.
	 */
	public UniProtEntryMPA getUniProtEntry() {
		return uniProtEntry;
	}

	/*
	 * METHODS
	 * 
	 * TAXONOMY
	 */

	@Override
	public TaxonomyNode getTaxonomyNode() {
		return this.taxonomyNode;
	}

	@Override
	public void setTaxonomyNode(TaxonomyNode taxonNode) {
		this.taxonomyNode = taxonNode;
	}

	@Override
	public List<? extends Taxonomic> getTaxonomicChildren() {
		return this.getProteinHitList();
	}

	/*
	 * METHODS
	 * 
	 * GENERAL
	 */

	/**
	 * Gets the experiments IDs in which the protein was identified
	 * @return experiment IDs.
	 */
	public Set<Long> getExperimentIDs() {
		return this.experimentIDs;
	}
	
	/**
	 * Adds the IDs of the experiments which contain this protein.
	 */
	public void addExperimentIDs(Set<Long> experimentIDs) {
		this.experimentIDs.addAll(experimentIDs);
	}
	
	/**
	 * Adds the IDs of the experiments which contain this protein.
	 */
	public void addExperimentID(Long experimentID) {
		this.experimentIDs.add(experimentID);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof MetaProteinHit) {
			MetaProteinHit m = (MetaProteinHit) o;
			return this.getAccession().equals(m.getAccession());
		}
		return false;
	}
	
	
	@Override
	public int compareTo(MetaProteinHit that) {
		return this.getAccession().compareTo(that.getAccession());
	}
	
	@Override
	public boolean isSelected() {
		for (ProteinHit prot : this.proteinHits) {
			if (prot.isSelected()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public Set<Object> getProperties(ChartType type) {
		Set<Object> res = new HashSet<Object>();
		if (type instanceof TaxonomyChartType) {
			// Get node by taxonomy rank
			TaxonomyChartType taxChartType = (TaxonomyChartType) type;
			TaxonomyNode node = this.getTaxonomyNode().getParentNode(taxChartType.getRank());
			// Return name of node
			res.add(node.getName());
		} else if (type instanceof OntologyChartType) {
			OntologyChartType ontChartType = (OntologyChartType) type;
			UniProtEntryMPA redUniEntry = this.getUniProtEntry();
			if (redUniEntry != null) {
				List<String> keywords = redUniEntry.getKeywords();
				for (String kw : keywords) {
					Keyword keyword = UniProtUtilities.ONTOLOGY_MAP.get(kw);
					if (keyword != null) {
						KeywordCategory ontologyType = KeywordCategory.valueOf(
								keyword.getCategory());
						if (ontologyType.equals(ontChartType.getOntology())) {
							res.add(kw);
						}
					} else {
//						if (!kw.equals("")) {
//							System.err.println("ERROR: unrecognized ontology \'" + kw + "\'");
//						}
					}
				}
			}
		} else if (type instanceof HierarchyLevel) {
			HierarchyLevel hl = (HierarchyLevel) type;
			switch (hl) {
			case META_PROTEIN_LEVEL:
				res.add(this.getAccession());
				break;
			case PROTEIN_LEVEL:
				// get protein accessions
				for (ProteinHit ph : this.getProteinHitList()) {
					res.add(ph.getAccession());
				}
				break;
			case PEPTIDE_LEVEL:
				// get peptide sequences
				for (PeptideHit pep : this.getPeptides()) {
					res.add(pep.getSequence());
				}
				break;
			case SPECTRUM_LEVEL:
//				HashSet<Long> new_res = (HashSet<Long>) res;
				for (PeptideHit ph : this.getPeptides()) {
					for (PeptideSpectrumMatch sm : ph.getPeptideSpectrumMatches()) {
						// TODO: implement title caching for spectrum matches
						// XXX: is this working properly using spectrum IDs instead of seachspectrum ids??
						res.add(sm.getSpectrumID());
					}
				}
				break;
			default:
				// If we got here something went wrong - investigate!
				System.err.println("ERROR: Unknown hierarchy level!");
				break;
			}
		} else if (type == CompareData.EXPERIMENT) {
			res.addAll(this.getExperimentIDs());
		} else {
			// If we got here something went wrong - investigate!
			System.err.println("Error: Unknown chart type!");
		}
		return res;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	@Override
	public String toString() {
		return (getAccession() + " | " + getDescription());
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
