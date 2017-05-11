package de.mpa.io.fasta.index;

import java.util.Set;

import gnu.trove.set.hash.THashSet;

public class PeptideDigest {
	/**
	 * Peptide string sequence.
	 */
	private final String peptide;
	
	/**
	 * Set of protein accessions.
	 */
	private final THashSet<String> proteinAccessions = new THashSet<>();
	
	/**
	 * Set of protein entries.
	 */
	private final THashSet<ProteinEntry> proteinEntries = new THashSet<>();
	
	/**
	 * Constructs a PeptideDigest containing a peptide.
	 * 
	 * @param peptide	the peptide for the PeptideDigest
	 */
	public PeptideDigest(String peptide) {
        this.peptide = peptide;
    }
	
	/**
	 * Constructs a PeptideDigest containing a peptide.
	 * 
	 * @param peptide	the peptide for the PeptideDigest
	 */
	public PeptideDigest(String peptide, String proteinAccession) {
        this(peptide);
        proteinAccessions.add(proteinAccession);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeptideDigest digestedPeptide = (PeptideDigest) o;
        return peptide.equals(digestedPeptide.peptide) && proteinAccessions.equals(digestedPeptide.proteinAccessions);
    }
    
    
    /**
     * Returns the set of assigned protein accessions. 
     * @return the set of assigned protein accessions.
     */
	public Set<String> getProteinAccessions() {
		return proteinAccessions;
	}
	
	/**
	 * Returns the set of protein entries.
	 * @return the of protein entries
	 */
	public THashSet<ProteinEntry> getProteinEntries() {
		return proteinEntries;
	}

	/**
	 * Returns the peptide of the digest.
	 * @return the peptide of the digest
	 */
	public String getPeptide() {
		return peptide;
	}
	
	/**
	 * Adds a protein accession to the digest.
	 * @param accession	the protein accession
	 */
	public void addProteinAccession(String accession) {
		proteinAccessions.add(accession);
	}
	
	/**
	 * Adds a protein entry to the digest.
	 * @param entry	the protein entry
	 */
	public void addProteinEntry(ProteinEntry entry) {
		proteinEntries.add(entry);
	}
}
