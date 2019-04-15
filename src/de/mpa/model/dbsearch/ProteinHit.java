package de.mpa.model.dbsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.mpa.client.ui.resultspanel.ComparePanel.CompareData;
import de.mpa.client.ui.sharedelements.chart.ChartType;
import de.mpa.client.ui.sharedelements.chart.HierarchyLevel;
import de.mpa.client.ui.sharedelements.chart.OntologyChart.OntologyChartType;
import de.mpa.client.ui.sharedelements.chart.TaxonomyChart.TaxonomyChartType;
import de.mpa.client.ui.sharedelements.tables.PhylogenyTreeTableNode;
import de.mpa.io.fasta.DigFASTAEntry;
import de.mpa.model.algorithms.quantification.ExponentiallyModifiedProteinAbundanceIndex;
import de.mpa.model.analysis.ProteinAnalysis;
import de.mpa.model.analysis.UniProtUtilities;
import de.mpa.model.analysis.UniProtUtilities.Keyword;
import de.mpa.model.analysis.UniProtUtilities.KeywordCategory;
import de.mpa.model.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.model.taxonomy.Taxonomic;
import de.mpa.model.taxonomy.TaxonomyNode;


/**
 * This class represents an identified protein hit.
 * It contains information about accession, description and a list of associated peptide hits.
 * 
 * @author T. Muth, R. Heyer, A. Behne, F. Kohrs, kay
 */
public class ProteinHit implements Serializable, Comparable<ProteinHit>, Taxonomic, Hit {

	/*
	 * FIELDS
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *  Flag denoting whether this protein is selected for export.
	 */
	private boolean selected = true;
	
	/**
	 * Protein accession.
	 */
	private String accession;
	
	/**
	 * Protein description.
	 */
	private String description;
	
	/**
	 * The amino acid identity between protein in percent
	 */
	private double identity = 100.0;
	
	/**
	 * Molecular protein weight in kDa.
	 */
	private double molWeight = -1.0;
	
	/**
	 * The isoelectric point of the protein.
	 */
	private double pI = -1.0;
	
	/**
	 * The meta-protein this protein hit is associated with.
	 */
	private MetaProteinHit metaProteinHit;

	/**
	 * Peptide hits for the protein.
	 */
	private HashMap<String, PeptideHit> peptideHits;

	/**
	 * The amino acid sequence of the protein.
	 */
	private String sequence;
	
	/**
	 * The sequence coverage ratio of the protein hit.
	 */
	private double coverage = -1.0;
	
	/**
	 * The normalized spectral abundance factor of the protein hit.
	 */
	private double nsaf = -1.0;
	
	/**
	 * The Exponentially Modified Protein Abundance Index of the protein hit.
	 */
	private double empai = -1.0;
	
	/**
	 * The type of database this protein originated in.
	 */
	private DigFASTAEntry.Type databaseType; 

	/**
	 * The UniProt entry hit object containing additional meta-data.
	 */
	private UniProtEntryMPA uniProtEntry = null;
	
	/**
	 * The common taxonomy node.
	 */
//	private TaxonomyNode taxonomyNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root");
	private TaxonomyNode taxonomyNode;
	
	/**
	 * The database IDs of the experiments which contain the protein hit.
	 */
	private Set<Long> experimentIDs;
	
	private boolean isoleucineReplaced = false;

	/*
	 * FIELDS
	 * 
	 * COUSIN LIST -> CROSS-TABLE-SELECTION
	 */
	
	private List<PhylogenyTreeTableNode> cousin_list = new ArrayList<PhylogenyTreeTableNode>();
	
	public List<PhylogenyTreeTableNode> getCousin_list() {
		return cousin_list;
	}

	public void setCousin_list(List<PhylogenyTreeTableNode> cousin_list) {
		this.cousin_list = cousin_list;
	}
	
	public void addCousinNode(PhylogenyTreeTableNode node) {
		this.cousin_list.add(node);
	}

	/*
	 * CONSTRUCTORS
	 */
	
	/**
	 * Constructs a protein hit from the specified accession, description and
	 * sequence string as well as a single peptide hit.
	 * @param accession the protein accession
	 * @param description the protein description
	 * @param sequence the protein sequence
	 * @param peptideHit the peptide hit
	 * @param uniprotEntry the UniProtEntry hit
	 */
	public ProteinHit(String accession, String description, String sequence, PeptideHit peptideHit, UniProtEntryMPA uniProtEntry, TaxonomyNode taxonomyNode, long experimentID) {
		this.accession = accession;
		this.description = description;
		this.sequence = sequence;
		this.peptideHits = new HashMap<String, PeptideHit>();
		if (peptideHit != null) {
			this.peptideHits.put(peptideHit.getSequence(), peptideHit);
		}
		this.taxonomyNode = taxonomyNode;
		this.uniProtEntry = uniProtEntry;
		this.experimentIDs = new HashSet<Long>();
		this.experimentIDs.add(experimentID);
	}
	

	/**
	 * Constructor for a simple protein hit with accession only.
	 * @param accession the protein accession
	 */
	public ProteinHit(String accession) {
		this(accession, "", "", null, null, null, 0L);
	}
	
	/*
	 * METHODS
	 * 
	 * FDR
	 */
	
	/**
	 * Sets the false discovery rate
	 * @param fdr. The false discovery rate.
	 */
	@Override
	public void setFDR(double fdr) {
		ArrayList<PeptideHit> newPeptideHits = new ArrayList<PeptideHit>();
		for (PeptideHit hit : peptideHits.values()) {
			hit.setFDR(fdr);
			if (hit.isVisible()) {
				newPeptideHits.add(hit);
			}
		}
		peptideHits.clear();
		for (PeptideHit pep : newPeptideHits) {
			peptideHits.put(pep.getSequence(), pep);
		}
	}
	
	@Override
	public boolean isVisible() {
		return !peptideHits.isEmpty();
	}
	
	/*
	 * METHODS
	 * 
	 * METAPROTEIN
	 */
	
	/**
	 * Returns the meta-protein this protein hit is associated with.
	 * @return the associated meta-protein
	 */
	public MetaProteinHit getMetaProteinHit() {
		return metaProteinHit;
	}

	/**
	 * Associates this protein hit with the specified meta-protein.
	 * @param metaProteinHit the meta-protein to be associated with
	 */
	public void setMetaProteinHit(MetaProteinHit metaProteinHit) {
		this.metaProteinHit = metaProteinHit;
	}
	
	/*
	 * METHODS
	 * 
	 * PEPTIDES
	 */
	
	/**
	 * Returns the peptide count for the protein hit.
	 * @return the number of peptides found in the protein hit
	 */
	public int getPeptideCount() {
		return peptideHits.size();
	}
	
	/**
	 * Convenience method to get a peptide hit by its sequence identifier.
	 * @param sequence The identifier string.
	 * @return the desired peptide hit or <code>null</code> if the retrieval failed.
	 */
	public PeptideHit getPeptideHit(String sequence) {
		return peptideHits.get(sequence);
	}
	
	/**
	 * Returns the peptide hits as list.
	 * @return The peptide hits as list.
	 */
	public List<PeptideHit> getPeptideHitList() {
		return new ArrayList<PeptideHit> (peptideHits.values());
	}
	
	/**
	 * Returns the map containing sequence-to-peptide hit pairs.
	 * @return the map of peptide hits
	 */
	public Map<String, PeptideHit> getPeptideHits() {
		return peptideHits;
	}
	
	/**
	 * Adds one peptide to the protein hit.
	 * @param peptidehit
	 */
	public void addPeptideHit(PeptideHit peptidehit) {
		if (!peptideHits.containsKey(peptidehit.getSequence())) {
			this.peptideHits.put(peptidehit.getSequence(), peptidehit);
		}
	}
	
	public void replaceIsoleucine(String new_seq, PeptideHit peptideHit) {
		if (!isoleucineReplaced) {
			peptideHits.remove(peptideHit.getSequence());
			// do not replace sequence for performance reasons
			// XXX: this will result in errors in the sequence tab
			//		this.setSequence(sequence.replaceAll("I", "L"));
			peptideHits.put(new_seq, peptideHit);
			this.isoleucineReplaced = true;
		}
	}

	public void replacePeptide(PeptideHit pep2) {
		this.peptideHits.put(pep2.getSequence(), pep2);
	}

	
	/*
	 * METHODS
	 * 
	 * SPECTRA
	 */
	
	/**
	 * Calculates and returns the spectral count.
	 * @return the spectral count
	 */
	public int getSpectralCount() {
			HashSet<Long> matches = new HashSet<Long>();
			// Iterate the found peptide results
			for (Entry<String, PeptideHit> entry : peptideHits.entrySet()) {
				// Store the spectrum matches
				for (PeptideSpectrumMatch match : entry.getValue().getPeptideSpectrumMatches()) {
					matches.add(match.getSpectrumID());
				}
			}
		return matches.size();
	}

	public ArrayList<PeptideSpectrumMatch> getPSMs() {
		HashSet<PeptideSpectrumMatch> psmset = new HashSet<PeptideSpectrumMatch>();
		for (PeptideHit pep : this.peptideHits.values()) {
			psmset.addAll(pep.getPeptideSpectrumMatches());
		}
		ArrayList<PeptideSpectrumMatch> psms = new ArrayList<PeptideSpectrumMatch>();
		psms.addAll(psmset);
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
		return taxonomyNode;
	}

	@Override
	public void setTaxonomyNode(TaxonomyNode taxonNode) {
		this.taxonomyNode = taxonNode;
	}

	@Override
	public List<? extends Taxonomic> getTaxonomicChildren() {
		return this.getPeptideHitList();
	}
	
	/*
	 * METHODS
	 * 
	 * GENERAL
	 */
	
	/**
	 * Returns the protein's amino acid sequence.
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}
	
	/**
	 * Sets the protein's amino acid sequence.
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * Returns the protein accession.
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}
	
	/**
	 * 	Return the database type
	 * 
	 * @return databaseType
	 */
	public DigFASTAEntry.Type getDatabaseType() {
		return databaseType;
	}
	
	/**
	 * Set the database type.
	 * 
	 * @param databaseType
	 */
	public void setDatabaseType(DigFASTAEntry.Type databaseType) {
		this.databaseType = databaseType;
	}

	/**
	 * Sets the protein accession.
	 * @param accession the accession to set
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}

	/**
	 * Returns the protein description.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the protein description.
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the sequence identity.
	 * @return identity the sequence identity
	 */
	public double getIdentity() {
		return identity;
	}

	/**
	 * Sets the sequence identity for this protein w.r.t. other proteins in a group
	 * @param identity the sequence identity to set
	 */
	public void setIdentity(double identity) {
		this.identity = identity;
	}

	/**
	 * Returns the sequence coverage.
	 * @return the sequence coverage
	 */
	public double getCoverage() {
		if (coverage < 0.0) {
			coverage = ProteinAnalysis.calculateSequenceCoverage(this);
		}
		return coverage;
	}
	/**
	 * Sets the sequence coverage.
	 * @param coverage the sequence coverage to set
	 */
	public void setCoverage(double coverage) {
		this.coverage = coverage;
	}
	
	/**
	 * Returns the molecular weight.
	 * @return the molecular weight
	 */
	public double getMolecularWeight() {
		if (molWeight < 0.0) {
			molWeight = ProteinAnalysis.calculateMolecularWeight(this);
		}
		return molWeight;
	}
	
	/**
	 * Sets the molecular weight of the protein.
	 * @param molWeight the molecular weight to set
	 */
	public void setMolecularWeight(double molWeight) {
		this.molWeight = molWeight;
	}
	
	/**
	 * Gets the isoelectric point of the protein. 
	 * @return the pI
	 */
	public double getIsoelectricPoint() {
		if (pI < 0.0) {
			pI = ProteinAnalysis.calculateIsoelectricPoint(this);
		}
		return pI;
	}

	/**	
	 * Sets the isoelectric point of the protein.
	 * @param pI the pI to set
	 */
	public void setIsoelectricPoint(double pI) {
		this.pI = pI;
	}
	
	/**
	 * Gets the emPAI of the protein
	 * @return the emPAI
	 */
	public double getEmPAI() {
//		empai = 0.0;
		if (empai < 0.0) {
			empai = ProteinAnalysis.calculateLabelFree(new ExponentiallyModifiedProteinAbundanceIndex(), this);
		}
		return empai;
	}
	
	/**
	 * Sets the emPAI of the protein.
	 * @param emPAI the emPAI to set
	 */
	public void setEmPAI(double emPAI) {
		this.empai = emPAI;
	}

	/**
	 * Returns the NSAF protein quantification value.
	 * @return the NSAF
	 */
	public double getNSAF() {
		return nsaf;
	}
	
	/**
	 * Sets the NSAF protein quantification value
	 * @param NSAF the NSAF to set
	 */
	public void setNSAF(double nsaf) {
		this.nsaf = nsaf;
	}
	
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
	 * Adds a single ID of an experiment which contain this protein.
	 */
	public void addExperimentID(Long experimentID) {
		this.experimentIDs.add(experimentID);
	}
	
	@Override
	public boolean isSelected() {
		return selected;
	}
	
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	@Override
	public String toString() {
		return (getAccession() + " | " + getDescription());
	}
	
	@Override
	public int compareTo(ProteinHit that) {
		return this.getAccession().compareTo(that.getAccession());
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
//						System.err.println("ERROR: unrecognized ontology \'" + kw + "\'");
					}
				}
			}
		} else if (type instanceof HierarchyLevel) {
			HierarchyLevel hl = (HierarchyLevel) type;
			switch (hl) {
			case META_PROTEIN_LEVEL:
				MetaProteinHit mph = this.getMetaProteinHit();
				// metaprotein accession 
				res.add(mph.getAccession());
				break;
			case PROTEIN_LEVEL:
				// these are the protein accessions
				res.add(this.getAccession());
				break;
			case PEPTIDE_LEVEL:
				// these are the peptide sequences
				res.addAll(this.getPeptideHits().keySet());
				break;
			case SPECTRUM_LEVEL:
				// 
				for (PeptideHit ph : this.getPeptideHitList()) {
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
	
}
