package de.mpa.client.model.dbsearch;

import java.io.Serializable;
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
public class PeptideHit implements Serializable{
	
	/**
	 * Flag denoting whether this peptide is selected for export.
	 */
	private boolean selected = true;
	
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
	 *  The list of protein hits associated with this peptide hit.
	 */
	private List<ProteinHit> proteinHits;
	
	/**
	 * The peptide spectrum match for this peptide hit.
	 */
	private List<SpectrumMatch> spectrumMatches;
	
	/**
	 * Map linking search spectrum IDs of matches to their position in this 
	 * peptide hit's list of matches.
	 */
	private Map<Long, Integer> id2index = new HashMap<Long, Integer>();
	
	/**
	 * PeptideHit constructor, taking the sequence as only parameter.
	 * @param sequence The String sequence.
	 * @param spectrumMatches The list of peptide spectrum matches.
	 */
	public PeptideHit(String sequence, SpectrumMatch spectrumMatch) {
		this.sequence = sequence;
		this.proteinHits = new ArrayList<ProteinHit>();
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
	
	/**
	 * Returns the list of spectrum matches.
	 * @return the list of spectrum matches.
	 */
	public List<SpectrumMatch> getSpectrumMatches() {
		return spectrumMatches;
	}

	/**
	 * Sets the list of spectrum matches.
	 * @param spectrumMatches the list of spectrum matches to set.
	 */
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
	
	/**
	 * Returns spectral count.
	 * @return the spectral count.
	 */
	public int getSpectralCount() {
		return getSpectrumMatches().size();
	}

	/**
	 * Adds the specified ProteinHit to the list of proteins associated with this peptide.  
	 * @param proteinHit The hit to add.
	 */
	public void addProteinHit(ProteinHit proteinHit) {
		if (!proteinHits.contains(proteinHit)) {
			proteinHits.add(proteinHit);
		}
	}
	
	/**
	 * Returns the number of proteins associated with this peptide.
	 * @return The protein count.
	 */
	public int getProteinCount() {
		return proteinHits.size();
	}
	
	/**
	 * Returns the list of proteins associated with this peptide.
	 * @return The protein list.
	 */
	public List<ProteinHit> getProteinList() {
		return proteinHits;
	}
	
	/**
	 * Returns whether this peptide hit is selected for exporting. 
	 * @return <code>true</code> if peptide is selected for export, 
	 * <code>false</code> otherwise.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets whether this peptide hit is selected for exporting. 
	 * @param selected <code>true</code> if peptide is selected for export, 
	 * <code>false</code> otherwise.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		// make child matches inherit selection state
//		for (SpectrumMatch sm : spectrumMatches) {
//			sm.setSelected(selected);
//		}
	}
	
	@Override
	public String toString() {
		return getSequence();
	}

	// TODO: investigate whether this kind of equality check causes problems
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PeptideHit) {
			return getSequence().equals(((PeptideHit) obj).getSequence());
		}
		return false;
	}
}
