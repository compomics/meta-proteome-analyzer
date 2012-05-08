package de.mpa.client.model.dbsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private List<SpectrumMatch> spectrumMatches;
	
	/**
	 * 
	 */
	private Map<Long, Integer> id2index = new HashMap<Long, Integer>();
	
	/**
	 * PeptideHit constructor, taking the sequence as only parameter.
	 * @param sequence The String sequence.
	 * @param spectrumMatches The list of peptide spectrum matches.
	 */
	public PeptideHit(String sequence, SpectrumMatch spectrumMatch) {
		this.sequence = sequence;
		this.spectrumMatches = new ArrayList<SpectrumMatch>();
		addSpectrumMatch(spectrumMatch);
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
	 * Sets the peptide sequence
	 * @param the peptide sequence
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * Convenience method to retrieve a unique PSM.
	 * @return The list of PSMs.
	 */
	public SpectrumMatch getSingleSpectrumMatch() {
		return spectrumMatches.get(0);
	}
	
	
	public List<SpectrumMatch> getSpectrumMatches() {
		return spectrumMatches;
	}

	public void setSpectrumMatches(List<SpectrumMatch> spectrumMatches) {
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
	 * Adds a spectrum match to the PeptideHit. 
	 * @param sm The spectrum match.
	 */
	public void addSpectrumMatch(SpectrumMatch sm) {
		id2index.put(sm.getSearchSpectrumID(), spectrumMatches.size());
		spectrumMatches.add(sm);
	}

	/**
	 * Replaces an existing spectrum match which contains the same search
	 * spectrum ID with the specified spectrum match or appends it if no such ID
	 * exists yet.
	 * @param sm The spectrum match to be inserted.
	 */
	public void replaceSpectrumMatch(SpectrumMatch sm) {
		Integer index = id2index.get(sm.getSearchSpectrumID());
		if (index == null) {
			addSpectrumMatch(sm);
		} else {
			spectrumMatches.set(index, sm);
		}
	}
	
	/**
	 * Returns the spectrum match that is mapped to the specified search spectrum ID.
	 * @param id The search spectrum ID.
	 * @return The mapped spectrum match.
	 */
	public SpectrumMatch getSpectrumMatch(long id) {
		Integer index = id2index.get(id);
		return (index != null) ? spectrumMatches.get(id2index.get(id)) : null;
	}
}
