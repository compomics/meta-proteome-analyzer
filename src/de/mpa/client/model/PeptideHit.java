package de.mpa.client.model;

import java.util.TreeMap;


/**
 * This class represents a peptide hit.
 * @author T.Muth
 *
 */
public class PeptideHit {
	
	/**
	 *  The peptide sequence
	 */
	private String sequence;
	
	/**
	 * The start of the peptide sequence in the protein.
	 */
	private int start;
	
	/**
	 *  The end of the peptide sequence in the protein.
	 */
	private int end;
	
	/**
	 * The peptide spectrum match for this peptide hit.
	 */
	private TreeMap<Long, PeptideSpectrumMatch> peptideSpectrumMatches;
	
	/**
	 * PeptideHit constructor, taking the sequence as only parameter.
	 * @param sequence The String sequence.
	 * @param peptideSpectrumMatches The list of peptide spectrum matches.
	 */
	public PeptideHit(String sequence, PeptideSpectrumMatch peptideSpectrumMatch) {
		this.sequence = sequence;
		this.peptideSpectrumMatches = new TreeMap<Long, PeptideSpectrumMatch>();
		this.peptideSpectrumMatches.put(peptideSpectrumMatch.getSpectrumId(), peptideSpectrumMatch);
	}
	
	/**
	 * PeptideHit constructor, taking sequence, start and end as parameters.
	 * @param sequence The String sequence.
	 * @param start The start of peptide in the protein.
	 * @param end The end of the peptide in the protein.
	 */
	public PeptideHit(String sequence, int start, int end) {
		this.sequence = sequence;
		this.start = start;
		this.end = end;
	}
	/**
	 * Returns the peptide sequence.
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}
	
	/**
	 * Convenience method to retrieve a unique PSM.
	 * @return The list of PSMs.
	 */
	public PeptideSpectrumMatch getSinglePeptideSpectrumMatch() {
		return peptideSpectrumMatches.firstEntry().getValue();
	}
	
	
	public TreeMap<Long, PeptideSpectrumMatch> getPeptideSpectrumMatches() {
		return peptideSpectrumMatches;
	}

	public void setPeptideSpectrumMatches(TreeMap<Long, PeptideSpectrumMatch> peptideSpectrumMatches) {
		this.peptideSpectrumMatches = peptideSpectrumMatches;
	}

	/**
	 * Returns the peptide start.
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * Returns the peptide end.
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}
	
	/**
	 * Adds a peptide spectrum match to the PeptideHit.
	 * @param psm The peptide spectrum map.
	 */
	public void addPeptideSpectrumMatch(PeptideSpectrumMatch psm){
		peptideSpectrumMatches.put(psm.getSpectrumId(), psm);
	}
}
