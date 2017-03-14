package de.mpa.client.model.dbsearch;

import java.io.Serializable;
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
	 * Serialization ID set to default == 1L;
	 */
	private static final long serialVersionUID = 1L; 
	
	/**
	 * The set of protein hits with distinct accessions.
	 */
	private Set<ProteinHit> proteinSet;

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
        hasChanged = size() > 0;
		super.clear();
	}
	
	@Override
	public boolean addAll(Collection<? extends ProteinHit> c) {
		if (c == null) {
			return false;
		}
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
	 * 
	 * @return
	 */
	public Set<ProteinHit> getProteinSet() {
		if (hasChanged) {
            this.regenerateSets();
		}
		return this.proteinSet;
	}

	/**
	 * Utility method to return a set of peptide hits with distinct sequence
	 * strings.
	 * @return the peptide hit set
	 */
	public Set<PeptideHit> getPeptideSet() {
		if (hasChanged) {
            this.regenerateSets();
		}
		return this.peptideSet;
	}

	/**
	 * Utility method to return a set of spectrum matches with distinct search
	 * spectrum IDs.
	 * @return the spectrum match set
	 */
	public Set<SpectrumMatch> getMatchSet() {
		if (hasChanged) {
            this.regenerateSets();
		}
		return this.matchSet;
	}

	/**
	 * Caches sets of distinct peptides and spectrum matches contained in this protein list.
	 */
	private void regenerateSets() {
        this.proteinSet = new TreeSet<ProteinHit>();
        this.peptideSet = new TreeSet<PeptideHit>();
        this.matchSet = new TreeSet<SpectrumMatch>();
		if (!isEmpty()) {
			// this list contains meta-protein hits
			for (ProteinHit proteinHit : this) {
				if (proteinHit instanceof MetaProteinHit) {
					MetaProteinHit metaProteinHit = (MetaProteinHit) proteinHit;
                    this.proteinSet.addAll(metaProteinHit.getProteinSet());
                    this.peptideSet.addAll(metaProteinHit.getPeptideSet());
                    this.matchSet.addAll(metaProteinHit.getMatchSet());
				} else {
					List<PeptideHit> peptideHitList = proteinHit.getPeptideHitList();
					for (PeptideHit peptideHit : peptideHitList) {
                        this.matchSet.addAll(peptideHit.getSpectrumMatches());
					}
                    this.peptideSet.addAll(peptideHitList);
                    this.proteinSet.add(proteinHit);
				}
			}
		}
        hasChanged = false;
	}

}
