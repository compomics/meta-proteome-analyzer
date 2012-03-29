package de.mpa.client.model;

import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * This class represents an identified protein hit.
 * It contains information about accession, description and a list of containing peptide hits.
 * @author T. Muth, R. Heyer
 *
 */
public class ProteinHit {
	/**
	 * Protein accession.
	 */
	private String accession;
	
	/**
	 * Protein description.
	 */
	private String description;
	
	/**
	 * TODO: Molecular protein weight in kDa.
	 */
	private double molWeight;
	
	/**
	 * Peptide hits for the protein.
	 */
	private TreeMap<String, PeptideHit> peptideHits;
	
	/**
	 * The spectral count, i.e. the number of spectra which relate to the protein.
	 */
	private int specCount;
	
	/**
	 * TODO: Coverage!
	 */
	private double coverage;
	
	/**
	 * TODO: NSAF!
	 */
	private double nSAF;
	
	
	/**
	 * Constructor for a simple protein hit with accesion only.
	 * @param accession
	 */
	public ProteinHit(String accession, String description, PeptideHit peptideHit){
		this.accession = accession;
		this.description = description;
		this.peptideHits = new TreeMap<String, PeptideHit>();
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
	 * Returns the molecular weight.
	 * @return The molecular weight.
	 */
	public double getMolWeight() {
		return molWeight;
	}
	
	/**
	 * Returns the sequence coverage.
	 * @return The sequence coverage
	 */
	public double getCoverage() {
		return coverage;
	}
	/**
	 * Sets the sequence coverage.
	 * @param coverage The sequence coverage to set
	 */
	public void setCoverage(double coverage) {
		this.coverage = coverage;
	}
	
	/**
	 * Calculates and returns the spectral count.
	 * @return The spectral count.
	 */
	public int getSpecCount() {
		Set<Entry<String, PeptideHit>> entrySet = peptideHits.entrySet();
		
		// Counter variable
		int count = 0;
		
		// Iterate the found peptide results
		for (Entry<String, PeptideHit> entry : entrySet) {
				// Get the peptide hit.
				PeptideHit peptideHit = entry.getValue();
				count += peptideHit.getPeptideSpectrumMatches().size();
		}
		this.specCount = count;
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
	public TreeMap<String, PeptideHit> getPeptideHits() {
		return peptideHits;
	}
	
	/**
	 * Sets the list of peptide hits.
	 * @param peptideHits The list of peptide hits to set.
	 */
	public void setPeptideHits(TreeMap<String, PeptideHit> peptideHits) {
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
	 * Convenience method to retrieve a unique peptide hit.
	 */
	public PeptideHit getSinglePeptideHit(){
		return peptideHits.firstEntry().getValue();
	}
}
