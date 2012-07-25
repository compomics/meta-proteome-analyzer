package de.mpa.client.model.dbsearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jdesktop.swingx.JXErrorPane;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import de.mpa.algorithms.quantification.ExponentiallyModifiedProteinAbundanceIndex;
import de.mpa.analysis.ProteinAnalysis;
import de.mpa.client.model.SpectrumMatch;


/**
 * This class represents an identified protein hit.
 * It contains information about accession, description and a list of containing peptide hits.
 * @author T. Muth, R. Heyer
 *
 */
public class ProteinHit {
	/**
	 * Marker, if Protein is selected for export.
	 */
	private boolean selected = true; //Math.random()<0.5;
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
	 * The Normalized Spectral Abundance Factor of the protein hit.
	 */
	private double nSAF = -1.0;
	
	/**
	 * The Exponentially Modified Protein Abundance Index of the protein hit.
	 */
	private double emPAI = -1.0;

	private UniProtEntry uniprotEntry;
	
	
	
	/**
	 * Constructor for a simple protein hit with accesion only.
	 * @param accession
	 */
	public ProteinHit(String accession, String description, String sequence, PeptideHit peptideHit){
		this.accession = accession;
		this.description = description;
		this.sequence = sequence;
		this.peptideHits = new LinkedHashMap<String, PeptideHit>();
		this.peptideHits.put(peptideHit.getSequence(), peptideHit);
	}
	/**
	 * Returns the accession.
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}

	/**
	 * Returns the description.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * Returns the species of the protein
	 * @return species
	 */
	public String getSpecies() {
		return species;
	}
	
	/**
	 * Sets the species of the protein
	 * @param species
	 */
	public void setSpecies(String species) {
		this.species = species;
	}
	/**
	 * Returns the molecular weight.
	 * @return The molecular weight.
	 */
	public double getMolecularWeight() {
		if (molWeight < 0.0) {
			molWeight = ProteinAnalysis.calculateMolecularWeight(this);
		}
		return molWeight;
	}
	
	/**
	 * Sets the molecular weight of the protein.
	 * @param molWeight The molecular weight of the protein.
	 */
	public void setMolecularWeight(double molWeight) {
		this.molWeight = molWeight;
	}
	
	/**
	 * Gets the isoelectric point of the protein. 
	 */
	public double getIsoelectricPoint() {
		if (pI < 0.0) {
			pI = ProteinAnalysis.calculateIsoelectricPoint(this);
		}
		return pI;
	}

	/**	
	 * Sets the isoelectric point of the protein.
	 * @param pI The isoelectric point of the protein.
	 */
	public void setIsoelectricPoint(double pI) {
		this.pI = pI;
	}
	
	/**
	 * Gets the protein's amino acid sequence.
	 * @return The protein sequence.
	 */
	public String getSequence() {
		return sequence;
	}
	
	/**
	 * Sets the protein's amino acid sequence
	 * @param sequence The sequence of the protein.
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	/**
	 * Returns the sequence coverage.
	 * @return The sequence coverage
	 */
	public double getCoverage() {
		if (coverage < 0.0) {
			coverage = ProteinAnalysis.calculateSequenceCoverage(this);
		}
		return coverage;
	}
	/**
	 * Sets the sequence coverage.
	 * @param coverage The sequence coverage to set
	 */
	public void setCoverage(double coverage) {
		this.coverage = coverage;
	}
	
//	/**
//	 * Calculates and returns the spectral count.
//	 * @return The spectral count.
//	 */
//	public int getSpectralCount() {
//		if (specCount < 0) {
//			Set<Entry<String, PeptideHit>> entrySet = peptideHits.entrySet();
//			
//			// Counter variable
//			int count = 0;
//			
//			// Iterate the found peptide results
//			for (Entry<String, PeptideHit> entry : entrySet) {
//					// Get the peptide hit.
//					PeptideHit peptideHit = entry.getValue();
//					count += peptideHit.getSpectrumMatches().size();
//			}
//			this.specCount = count;
//		}
//		return specCount;
//	}
	
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
	 * Calculates and returns the spectral count.
	 * @return The spectral count.
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
	 * Returns the peptide count for the protein hit.
	 * @return The number of peptides found in the protein hit.
	 */
	public int getPeptideCount(){
		return peptideHits.size();
	}

	/**
	 * Returns the NSAF protein quantification value.
	 * @return The NSAF protein quantification.
	 */
	public double getNSAF() {
		if (nSAF < 0.0) {
			JXErrorPane.showDialog(new Exception("NSAF has not been calculated yet. Call ProteinAnalysis.calculateLabelFree() first."));
		}
		return nSAF;
	}
	
	/**
	 * Sets the NSAF protein quantification value.
	 * @param nSAF The NSAF quantification value to set
	 */
	public void setNSAF(double nSAF) {
		this.nSAF = nSAF;
	}
	/**
	 * Returns a list of peptide hits.
	 * @return The list of peptide hits.
	 */
	public Map<String, PeptideHit> getPeptideHits() {
		return peptideHits;
	}
	
	/**
	 * Returns the peptide hits as list.
	 * @return The peptide hits as list.
	 */
	public List<PeptideHit> getPeptideHitList(){
		return new ArrayList<PeptideHit>(peptideHits.values());
	}
	
	/**
	 * Sets the list of peptide hits.
	 * @param peptideHits The list of peptide hits to set.
	 */
	public void setPeptideHits(Map<String, PeptideHit> peptideHits) {
		this.peptideHits = peptideHits;
	}

	/**
	 * Gets the emPAI of the protein
	 * @return emPAI
	 */
	public double getEmPAI() {
		if (emPAI < 0.0) {
			emPAI = ProteinAnalysis.calculateLabelFree(new ExponentiallyModifiedProteinAbundanceIndex(), this);
		}
		return emPAI;
	}
	
	/**
	 * Sets the emPAI of the protein
	 * @param emPAI
	 */
	public void setEmPAI(double emPAI) {
		this.emPAI = emPAI;
	}
	/**
	 * Adds one peptide to the protein hit.
	 * @param peptidehit
	 */
	public void addPeptideHit(PeptideHit peptidehit) {
		peptideHits.put(peptidehit.getSequence(), peptidehit);
	}
	
	/**
	 * Convenience method to retrieve a unique peptide hit.
	 */
	public PeptideHit getSinglePeptideHit() {
		return peptideHits.values().iterator().next();
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
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
//		for (PeptideHit ph : peptideHits.values()) {
//			ph.setSelected(selected);
//		}
	}
}
