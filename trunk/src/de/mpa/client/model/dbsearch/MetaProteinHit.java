package de.mpa.client.model.dbsearch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HierarchyLevel;

/**
 * Wrapper class for meta-proteins.
 * 
 * @author A. Behne
 */
public class MetaProteinHit extends ProteinHit {
	
	/**
	 * The protein hit list of this meta-protein.
	 */
	private ProteinHitList phl;

	/**
	 * Constructs a meta-protein hit from the specified accession string.
	 * @param accession the meta-protein accession
	 * @throws Exception 
	 */
	public MetaProteinHit(String accession, ProteinHitList phl) throws Exception {
		super(accession);
		this.phl = phl;
	}

	/**
	 * Returns the protein hit list.
	 * @return the protein hits
	 */
	public ProteinHitList getProteinHits() {
		return phl;
	}
	
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
	 * 
	 * @param proteinHits
	 */
	public void addAll(List<ProteinHit> proteinHits) {
		phl.addAll(proteinHits);
	}

	/**
	 * 
	 * @param metaProtein
	 * @return
	 */
	public boolean contains(MetaProteinHit metaProtein) {
		return phl.contains(metaProtein);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return phl.isEmpty();
	}
	
	@Override
	public Set<Object> getProperties(ChartType type) {
//		if (type.equals(HierarchyLevel.META_PROTEIN_LEVEL)) {
//			this.get
//		} else {
			Set<Object> res = new HashSet<Object>();
			for (ProteinHit protHit : this.getProteinHits()) {
				res.addAll(protHit.getProperties(type));
			}
//		}
		return res;
	}
}