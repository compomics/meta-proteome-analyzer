package de.mpa.client.model.dbsearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.mpa.client.model.SpectrumMatch;

/**
 * An extended ArrayList for ProteinHits providing utility methods.
 * 
 * @author A. Behne
 */
public class ProteinHitList extends ArrayList<ProteinHit> {
	
	public ProteinHitList() {
		super();
	}
	
	public ProteinHitList(Collection<? extends ProteinHit> c) {
		super(c);
	}

	// TODO: maybe cache sets and recalculate only when the underlying list got changed (e.g. via add())
	/**
	 * Utility method returning a set of peptide hits with distinct sequence
	 * strings.
	 * @return the peptide hit set
	 */
	public Set<PeptideHit> getPeptideSet() {
		Set<PeptideHit> peptideSet = new HashSet<PeptideHit>();
		for (ProteinHit proteinHit : this) {
			peptideSet.addAll(proteinHit.getPeptideHitList());
		}
		return peptideSet;
	}

	/**
	 * Utility method returning a set of spectrum matches with distinct search
	 * spectrum IDs.
	 * @return the spectrum match set
	 */
	public Set<SpectrumMatch> getMatchSet() {
		Set<SpectrumMatch> matchSet = new HashSet<SpectrumMatch>();
		for (ProteinHit proteinHit : this) {
			for (PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
				matchSet.addAll(peptideHit.getSpectrumMatches());
			}
		}
		return matchSet;
	}

}
