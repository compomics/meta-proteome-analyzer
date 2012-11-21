package de.mpa.client.model.dbsearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.mpa.client.model.SpectrumMatch;

/**
 * An extended ArrayList for ProteinHits providing utility methods.
 * 
 * @author A. Behne
 */
public class ProteinHitList extends ArrayList<ProteinHit> {

	/**
	 * The set of peptide hits with distinct amino acid sequences.
	 */
	private Set<PeptideHit> peptideSet;
	
	/**
	 * The set of spectrum matches with distinct search spectrum IDs.
	 */
	private Set<SpectrumMatch> matchSet;
	
	/**
	 * Flag to indicate whether the cached sets need to be regenerated.
	 */
	private boolean hasChanged = true;
	
	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public ProteinHitList() {
		super();
	}

	/**
	 * Constructs a list containing the elements of the specified collection, in
	 * the order they are returned by the collection's iterator.
	 * @param c the collection whose elements are to be placed into this list
	 */
	public ProteinHitList(Collection<? extends ProteinHit> c) {
		super(c);
	}
	
	@Override
	public boolean add(ProteinHit e) {
		hasChanged = super.add(e);
		return hasChanged;
	}
	
	@Override
	public void add(int index, ProteinHit element) {
		super.add(index, element);
		hasChanged = true;
	}
	
	@Override
	public ProteinHit remove(int index) {
		ProteinHit res = super.remove(index);
		hasChanged = true;
		return res;
	}
	
	@Override
	public boolean remove(Object o) {
		hasChanged = super.remove(o);
		return hasChanged;
	}
	
	@Override
	public void clear() {
		hasChanged = this.size() > 0;
		super.clear();
	}
	
	@Override
	public boolean addAll(Collection<? extends ProteinHit> c) {
		hasChanged = super.addAll(c);
		return hasChanged;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends ProteinHit> c) {
		hasChanged = super.addAll(index, c);
		return hasChanged;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		hasChanged = super.removeAll(c);
		return hasChanged;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		hasChanged = super.retainAll(c);
		return hasChanged;
	}

	/**
	 * Utility method to return a set of peptide hits with distinct sequence
	 * strings.
	 * @return the peptide hit set
	 */
	public Set<PeptideHit> getPeptideSet() {
		if (hasChanged) {
			regenerateSets();
		}
		return peptideSet;
	}

	/**
	 * Utility method to return a set of spectrum matches with distinct search
	 * spectrum IDs.
	 * @return the spectrum match set
	 */
	public Set<SpectrumMatch> getMatchSet() {
		if (hasChanged) {
			regenerateSets();
		}
		return matchSet;
	}

	/**
	 * Caches sets of distinct peptides and spectrum matches contained in this protein list.
	 */
	private void regenerateSets() {
		peptideSet = new TreeSet<PeptideHit>();
		matchSet = new TreeSet<SpectrumMatch>();
		for (ProteinHit proteinHit : this) {
			List<PeptideHit> peptideHitList = proteinHit.getPeptideHitList();
			for (PeptideHit peptideHit : peptideHitList) {
				matchSet.addAll(peptideHit.getSpectrumMatches());
			}
			peptideSet.addAll(peptideHitList);
		}
		hasChanged = false;
	}

}
