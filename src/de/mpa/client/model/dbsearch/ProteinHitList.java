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
public class ProteinHitList extends ArrayList<ProteinHit> implements Serializable {
	
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
     * The global list of peptides found within the search result.
     */
	private List<PeptideHit> peptideList;
	
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
		this.hasChanged = super.add(e);
		return this.hasChanged;
	}
	
	@Override
	public void add(int index, ProteinHit element) {
		super.add(index, element);
		this.hasChanged = true;
	}
	
	@Override
	public ProteinHit remove(int index) {
		ProteinHit res = super.remove(index);
		this.hasChanged = true;
		return res;
	}
	
	@Override
	public boolean remove(Object o) {
		this.hasChanged = super.remove(o);
		return this.hasChanged;
	}
	
	@Override
	public void clear() {
		this.hasChanged = this.size() > 0;
		super.clear();
	}
	
	@Override
	public boolean addAll(Collection<? extends ProteinHit> c) {
		if (c == null) {
			return false;
		}
		this.hasChanged = super.addAll(c);
		return this.hasChanged;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends ProteinHit> c) {
		this.hasChanged = super.addAll(index, c);
		return this.hasChanged;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		this.hasChanged = super.removeAll(c);
		return this.hasChanged;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		this.hasChanged = super.retainAll(c);
		return this.hasChanged;
	}

	/**
	 * 
	 * @return
	 */
	public Set<ProteinHit> getProteinSet() {
		if (this.hasChanged) {
			regenerateSets();
		}
		return proteinSet;
	}

	/**
	 * Utility method to return a set of peptide hits with distinct sequence
	 * strings.
	 * @return the peptide hit set
	 */
	public Set<PeptideHit> getPeptideSet() {
		if (this.hasChanged) {
			regenerateSets();
		}
		return peptideSet;
	}

	/**
	 * Utility method to return a list of peptide hits with all sequence
	 * strings - including duplicated peptides
	 * @return the peptide hit list
	 */
	public List<PeptideHit> getPeptideList() {
		if (this.hasChanged) {
			regenerateSets();
		}

		return peptideList;
	}

	/**
	 * Utility method to return a set of spectrum matches with distinct search
	 * spectrum IDs.
	 * @return the spectrum match set
	 */
	public Set<SpectrumMatch> getMatchSet() {
		if (this.hasChanged) {
			regenerateSets();
		}
		return matchSet;
	}

	/**
	 * Caches sets of distinct peptides and spectrum matches contained in this protein list.
	 */
	private void regenerateSets() {
		proteinSet = new TreeSet<ProteinHit>();
		peptideSet = new TreeSet<PeptideHit>();
		peptideList = new ArrayList<PeptideHit>();
		matchSet = new TreeSet<SpectrumMatch>();
		if (!this.isEmpty()) {
			// check whether this list contains meta-proteins or proteins
			if (this.get(0) instanceof MetaProteinHit) {
				// this list contains meta-protein hits
				for (ProteinHit proteinHit : this) {
					MetaProteinHit metaProteinHit = (MetaProteinHit) proteinHit;
					proteinSet.addAll(metaProteinHit.getProteinSet());
					peptideSet.addAll(metaProteinHit.getPeptideSet());
					peptideList.addAll(metaProteinHit.getPeptideHitList());
					matchSet.addAll(metaProteinHit.getMatchSet());
					// TODO: please also check whether we need this for unipept at all?
                    // Case check what happens once the user hits the "Process results" button for the meta-protein generation: is the peptide list still the same or do need to have an update here?

				}
			} else {
				// this list contains protein hits
				for (ProteinHit proteinHit : this) {
					List<PeptideHit> peptideHitList = proteinHit.getPeptideHitList();
					for (PeptideHit peptideHit : peptideHitList) {
						matchSet.addAll(peptideHit.getSpectrumMatches());
						//peptideList.add(peptideHit);
					}
					peptideSet.addAll(peptideHitList);
					System.out.println(peptideSet.size());

					peptideList.addAll(peptideHitList);
					System.out.println(peptideList.size());
					proteinSet.add(proteinHit);

				}
			}
		}
		this.hasChanged = false;
	}

}
