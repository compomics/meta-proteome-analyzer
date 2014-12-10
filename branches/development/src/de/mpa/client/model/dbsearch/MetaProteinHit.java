package de.mpa.client.model.dbsearch;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.mpa.analysis.taxonomy.Taxonomic;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.ui.chart.ChartType;

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
	private Map<String, ProteinHit> proteinHits;
	
	/**
	 * The visible protein hit list of this meta-protein.
	 */
	private Map<String, ProteinHit> visProteinHits;

	/**
	 * Constructs a meta-protein from the specified identifier string and
	 * protein hit list.
	 * @param identifier the identifier string
	 * @param phl the protein hit list
	 */
	public MetaProteinHit(String identifier, ProteinHit ph) {
		super(identifier);
		this.proteinHits = new LinkedHashMap<String, ProteinHit>();
		this.proteinHits.put(ph.getAccession(), ph);
	}

	/**
	 * Returns the list of proteins associated with this meta-protein.
	 * @return the protein list
	 */
	public ProteinHitList getProteinHitList() {
		if (visProteinHits == null) {
			return new ProteinHitList(proteinHits.values());
		}
		return new ProteinHitList(visProteinHits.values());
	}
	
	public Map<String, ProteinHit> getProteinHits() {
		if (visProteinHits == null) {
			return this.proteinHits;
		}
		return visProteinHits;
	}
	
	/**
	 * Returns a protein hit by its accession.
	 * @param accession the protein accession
	 * @return the protein hit
	 */
	public ProteinHit getProteinHit(String accession) {
		if (visProteinHits == null) {
			return proteinHits.get(accession);
		}
		return visProteinHits.get(accession);
	}
	
	/**
	 * Returns the set of proteins associated with this meta-protein.
	 * @return the protein set
	 */
	public Set<ProteinHit> getProteinSet() {
		if (visProteinHits == null) {
			return new HashSet<ProteinHit>(proteinHits.values());
		}
		return new HashSet<ProteinHit>(visProteinHits.values());
	}

	/**
	 * Utility method to return a set of peptide hits with distinct sequence
	 * strings.
	 * @return the peptide hit set
	 */
	public Set<PeptideHit> getPeptideSet() {
		return getProteinHitList().getPeptideSet();
	}

	/**
	 * Utility method to return a set of spectrum matches with distinct search
	 * spectrum IDs.
	 * @return the spectrum match set
	 */
	public Set<SpectrumMatch> getMatchSet() {
		return getProteinHitList().getMatchSet();
	}

	/**
	 * Associates the specified list of protein hits with this meta-protein.
	 * @param proteinHits
	 */
	public void addAll(List<ProteinHit> proteinHits) {
		for (ProteinHit ph : proteinHits) {
			this.visProteinHits.put(ph.getAccession(), ph);
			ph.setMetaProteinHit(this);
		}
	}

	/**
	 * Returns whether any proteins are associated with this meta-protein.
	 * @return <code>true</code> if not empty, <code>false</code> otherwise
	 */
	// TODO: unused method, remove?
	public boolean isEmpty() {
		return proteinHits.isEmpty();
	}
	
	/**
	 * This method filter by FDR threshold for all visible protein hits.
	 * @param fdr the FDR threshold
	 */
	@Override
	public void setFDR(double fdr) {
		this.visProteinHits = new LinkedHashMap<String, ProteinHit>();
		for (Entry<String, ProteinHit> entry : this.proteinHits.entrySet()) {
			ProteinHit hit = entry.getValue();
			hit.setFDR(fdr);
			if (hit.isVisible()) {
				this.visProteinHits.put(entry.getKey(), hit);
			}
		}
	}

	@Override
	public boolean isVisible() {
		return !this.visProteinHits.isEmpty();
	}
	
	@Override
	public List<? extends Taxonomic> getTaxonomicChildren() {
		return this.getProteinHitList();
	}
	
	@Override
	public boolean isSelected() {
		boolean res = true;
		for (ProteinHit ph : proteinHits.values()) {
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
		for (ProteinHit ph : proteinHits.values()) {
			ph.setSelected(selected);
		}
	}
	
	@Override
	public Set<Object> getProperties(ChartType type) {
		// Aggregate properties of associated proteins
		Set<Object> res = new HashSet<Object>();
		for (ProteinHit protHit : this.getProteinHitList()) {
			res.addAll(protHit.getProperties(type));
		}
		return res;
	}
	
	@Override
	public TaxonomyNode getTaxonomyNode() {
		if (super.getTaxonomyNode() == null) {
			return getProteinHitList().get(0).getTaxonomyNode();
		}
		return super.getTaxonomyNode();
	}
}