package de.mpa.client.model.dbsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import de.mpa.algorithms.quantification.ExponentiallyModifiedProteinAbundanceIndex;
import de.mpa.analysis.ProteinAnalysis;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.taxonomy.Taxonomic;
import de.mpa.taxonomy.TaxonomyNode;


/**
 * This class represents an identified protein hit.
 * It contains information about accession, description and a list of associated peptide hits.
 * 
 * @author T. Muth, R. Heyer, A. Behne, F. Kohrs
 */
public class ProteinHit implements Serializable, Comparable<ProteinHit>, Taxonomic {
	
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
	 * The species of the protein
	 */
	private String species;
	
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
	 * Peptide hits for the protein.
	 */
	private Map<String, PeptideHit> peptideHits;
	
	/**
	 * The spectral count, i.e. the number of spectra which relate to the protein.
	 */
	private int specCount = -1;
	
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
	 * The UniProt entry object containing additional meta-data.
	 */
	private UniProtEntry uniprotEntry = null;
	
	/**
	 * The taxonomy node reference.
	 */
	private TaxonomyNode taxonNode = new TaxonomyNode(1, "no rank", "root");

	/**
	 * Constructs a protein hit from the specified accession, description and
	 * sequence string as well as a single peptide hit.
	 * @param accession the protein accession
	 * @param description the protein description
	 * @param sequence the protein sequence
	 * @param peptideHit the peptide hit
	 */
	public ProteinHit(String accession, String description, String sequence, PeptideHit peptideHit){
		this.accession = accession;
		this.description = description;
		this.sequence = sequence;
		this.peptideHits = new LinkedHashMap<String, PeptideHit>();
		if (peptideHit != null) {
			this.peptideHits.put(peptideHit.getSequence(), peptideHit);
		}
	}

	/**
	 * Constructor for a simple protein hit with accession only.
	 * @param accession the protein accession
	 */
	public ProteinHit(String accession) {
		this(accession, "", "", null);
	}
	
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
	 * Returns the species of the protein
	 * @return species the species
	 */
	public String getSpecies() {
		// Extract species string from description
		String[] split = description.split(" OS=");
		if (split.length > 1) {
			description = split[0];
			species = (split[1].contains(" GN=")) ?
					split[1].substring(0, split[1].indexOf(" GN=")) : split[1];
		}
		return species;
	}
	
	/**
	 * Sets the species of the protein
	 * @param species the species to set
	 */
	public void setSpecies(String species) {
		this.species = species;
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
	 * Returns the peptide count for the protein hit.
	 * @return he number of peptides found in the protein hit
	 */
	public int getPeptideCount(){
		return peptideHits.size();
	}
	
	/**
	 * Calculates and returns the spectral count.
	 * @return the spectral count
	 */
	public int getSpectralCount() {
		if (specCount < 0) {
			Set<Long> matches = new HashSet<Long>();
			// Iterate the found peptide results
			for (Entry<String, PeptideHit> entry : peptideHits.entrySet()) {
				// Store the spectrum matches
				for (SpectrumMatch match : entry.getValue().getSpectrumMatches()) {
					matches.add(match.getSearchSpectrumID());
				}
			}
			this.specCount = matches.size();
		}
		return specCount;
	}

	/**
	 * Gets the emPAI of the protein
	 * @return the emPAI
	 */
	public double getEmPAI() {
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
//		if (nsaf < 0.0) {
//			Exception e = new Exception("NSAF has not been calculated yet. Call ProteinAnalysis.calculateLabelFree() first.");
//			JXErrorPane.showDialog(ClientFrame.getInstance(),
//					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//		}
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
		return new ArrayList<PeptideHit>(peptideHits.values());
	}
	
	/**
	 * Returns the map containing sequence-to-peptide hit pairs.
	 * @return the map of peptide hits
	 */
	public Map<String, PeptideHit> getPeptideHits() {
		return peptideHits;
	}
	
	/**
	 * Sets the map of peptide hits.
	 * @param peptideHits The map of peptide hits to set.
	 */
	public void setPeptideHits(Map<String, PeptideHit> peptideHits) {
		this.peptideHits = peptideHits;
	}
	
	/**
	 * Adds one peptide to the protein hit.
	 * @param peptidehit
	 */
	public void addPeptideHit(PeptideHit peptidehit) {
		peptideHits.put(peptidehit.getSequence(), peptidehit);
	}
	
	/**
	 * Add all peptides to the protein hit.
	 * @param peptidehit
	 */
	public void addPeptideHits(Map<String, PeptideHit> peptidehits) {
		peptideHits.putAll(peptidehits);
	}
	
	/**
	 * Convenience method to retrieve a unique peptide hit.
	 */
	public PeptideHit getSinglePeptideHit() {
		return peptideHits.values().iterator().next();
	}
	
	/**
	 * Returns a collection of non-redundant spectrum IDs belonging to this protein hit.
	 * @return A set of spectrum IDs.
	 */
	public Set<Long> getSpectrumIDs() {
		Set<Long> matches = new HashSet<Long>();
		for (Entry<String, PeptideHit> entry : peptideHits.entrySet()) {
			for (SpectrumMatch match : entry.getValue().getSpectrumMatches()) {
				matches.add(match.getSearchSpectrumID());
			}
		}
		return matches;
	}
	
	/**
	 * Sets an UniProt entry.
	 * @param uniprotEntry The UniProtEntry object
	 */
	public void setUniprotEntry(UniProtEntry uniprotEntry) {
		this.uniprotEntry = uniprotEntry;
	}
	
	/**
	 * Returns the UniProt entry.
	 * @return The UniProtEntry object.
	 */
	public UniProtEntry getUniprotEntry() {
		return uniprotEntry;
	}
	
	/**
	 * Returns whether this protein hit is selected for exporting. 
	 * @return <code>true</code> if protein is selected for export, 
	 * <code>false</code> otherwise.
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * Sets whether this protein hit is selected for exporting. 
	 * @param selected <code>true</code> if protein is selected for export, 
	 * <code>false</code> otherwise.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		// make child peptides inherit selection state
//		for (PeptideHit ph : peptideHits.values()) {
//			ph.setSelected(selected);
//		}
	}
	
	@Override
	public TaxonomyNode getTaxonomyNode() {
		return taxonNode;
	}

	@Override
	public void setTaxonomyNode(TaxonomyNode taxonNode) {
		this.taxonNode = taxonNode;
	}
	
	@Override
	public String toString() {
		return (getAccession() + " | " + getDescription());
	}
	
	// TODO: investigate whether this kind of equality check causes problems
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProteinHit) {
			return getAccession().equals(((ProteinHit) obj).getAccession());
		}
		return false;
	}

	@Override
	public int compareTo(ProteinHit that) {
		return this.getAccession().compareTo(that.getAccession());
	}
	
}
