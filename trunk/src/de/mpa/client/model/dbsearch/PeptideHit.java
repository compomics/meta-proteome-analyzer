package de.mpa.client.model.dbsearch;

import java.util.TreeMap;

import de.mpa.client.model.SpectrumMatch;

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
	private TreeMap<Long, SpectrumMatch> spectrumMatches;
	
	/**
	 * PeptideHit constructor, taking the sequence as only parameter.
	 * @param sequence The String sequence.
	 * @param spectrumMatches The list of peptide spectrum matches.
	 */
	public PeptideHit(String sequence, SpectrumMatch spectrumMatch) {
		this.sequence = sequence;
		this.spectrumMatches = new TreeMap<Long, SpectrumMatch>();
		this.spectrumMatches.put(spectrumMatch.getSpectrumId(), spectrumMatch);
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
	public SpectrumMatch getSingleSpectrumMatch() {
		return spectrumMatches.firstEntry().getValue();
	}
	
	
	public TreeMap<Long, SpectrumMatch> getSpectrumMatches() {
		return spectrumMatches;
	}

	public void setSpectrumMatches(TreeMap<Long, SpectrumMatch> spectrumMatches) {
		this.spectrumMatches = spectrumMatches;
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
	 * Sets the start position of the peptide.	
	 * @param start The start position of the peptide.
	 */
	public void setStart(int start) {
		this.start = start;
	}
	
	/**
	 * Sets the end position of the peptide.
	 * @param end The end position of the peptide.
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * Adds a peptide spectrum match to the PeptideHit.
	 * @param psm The peptide spectrum map.
	 */
	public void addSpectrumMatch(SpectrumMatch psm){
		spectrumMatches.put(psm.getSpectrumId(), psm);
	}
}
