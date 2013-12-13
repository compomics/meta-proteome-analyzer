package de.mpa.client.model.dbsearch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.taxonomy.Taxonomic;

/**
 * Wrapper class for meta-proteins.
 * 
 * @author A. Behne
 */
public class MetaProteinHit extends ProteinHit {
	
	/**
	 * Serialization ID set to default == 1L;
	 */
	private static final long serialVersionUID = 1L; 
	
	/**
	 * The protein hit list of this meta-protein.
	 */
	private ProteinHitList phl;

	/**
	 * Constructs a meta-protein from the specified identifier string and
	 * protein hit list.
	 * @param identifier the identifier string
	 * @param phl the protein hit list
	 */
	public MetaProteinHit(String identifier, ProteinHitList phl) {
		super(identifier);
		this.phl = phl;
	}

	/**
	 * Returns the list of proteins associated with this meta-protein.
	 * @return the protein list
	 */
	public ProteinHitList getProteinHits() {
		return phl;
	}
	
	/**
	 * Returns the set of proteins associated with this meta-protein.
	 * @return the protein set
	 */
	public Set<ProteinHit> getProteinSet() {
		return new HashSet<ProteinHit>(phl);
	}

	/**
	 * Utility method to return a set of peptide hits with distinct sequence
	 * strings.
	 * @return the peptide hit set
	 */
	public Set<PeptideHit> getPeptideSet() {
		return phl.getPeptideSet();
	}

	/**
	 * Utility method to return a set of spectrum matches with distinct search
	 * spectrum IDs.
	 * @return the spectrum match set
	 */
	public Set<SpectrumMatch> getMatchSet() {
		return phl.getMatchSet();
	}

	/**
	 * Associates the specified list of protein hits with this meta-protein.
	 * @param proteinHits
	 */
	public void addAll(List<ProteinHit> proteinHits) {
		phl.addAll(proteinHits);
		for (ProteinHit proteinHit : proteinHits) {
			proteinHit.setMetaProteinHit(this);
		}
	}

	/**
	 * Returns whether any proteins are associated with this meta-protein.
	 * @return <code>true</code> if not empty, <code>false</code> otherwise
	 */
	// TODO: unused method, remove?
	public boolean isEmpty() {
		return phl.isEmpty();
	}
	
	@Override
	public List<? extends Taxonomic> getTaxonomicChildren() {
		return this.getProteinHits();
	}
	
	@Override
	public boolean isSelected() {
		boolean res = true;
		for (ProteinHit ph : phl) {
			res &= ph.isSelected();
			if (!res) {
				break;
			}
		}
		return res;
	}
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		for (ProteinHit ph : phl) {
			ph.setSelected(selected);
		}
	}
	
	@Override
	public Set<Object> getProperties(ChartType type) {
		// Aggregate properties of associated proteins
		Set<Object> res = new HashSet<Object>();
		for (ProteinHit protHit : this.getProteinHits()) {
			res.addAll(protHit.getProperties(type));
		}
		return res;
	}
}