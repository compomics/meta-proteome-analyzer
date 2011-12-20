package de.mpa.client.model;

/**
 * This class represents a peptide hit.
 * @author Thilo Muth
 *
 */
public class PeptideHit {
	
	// The peptide sequence
	private String sequence;
	
	// The start of the peptide sequence in the protein.
	private int start;
	
	// The end of the peptide sequence in the protein.
	private int end;
	
	/**
	 * PeptideHit constructor, taking the sequence as only parameter.
	 * @param sequence The String sequence.
	 */
	public PeptideHit(String sequence) {
		this.sequence = sequence;
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
}
