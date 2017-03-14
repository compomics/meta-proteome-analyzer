package de.mpa.client.model.dbsearch;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
	private final Map<String, ProteinHit> proteinHits;
	
	/**
	 * The visible protein hit list of this meta-protein.
	 */
	private Map<String, ProteinHit> visProteinHits = new LinkedHashMap<String, ProteinHit>();

	/**
	 * Constructs a meta-protein from the specified identifier string and
	 * protein hit list.
	 * @param identifier the identifier string
	 * @param phl the protein hit list
	 * @param upa. Uniprotentry to initialize this metaprotein with.
	 */
	public MetaProteinHit(String identifier, ProteinHit ph, UniProtEntryMPA upa) {
		super(identifier);
        setUniprotEntry(upa);
        setTaxonomyNode(upa.getTaxonomyNode());
        proteinHits = new LinkedHashMap<String, ProteinHit>();
        proteinHits.put(ph.getAccession(), ph);
		for (PeptideHit pep : ph.getPeptideHitList()) {
            addPeptideHit(pep);
		}
	}

	/**
	 * Returns the list of proteins associated with this meta-protein.
	 * @return the protein list
	 */
	public ProteinHitList getProteinHitList() {
		if (this.visProteinHits.isEmpty()) {
			return new ProteinHitList(this.proteinHits.values());
		}
		return new ProteinHitList(this.visProteinHits.values());
	}
	
	
	// TODO: why do we have to methods here?
	public Map<String, ProteinHit> getProteinHits() {
		if (this.visProteinHits.isEmpty()) {
			return proteinHits;
		}
		return this.visProteinHits;
	}
	
	/**
	 * Returns a protein hit by its accession.
	 * @param accession the protein accession
	 * @return the protein hit
	 */
	public ProteinHit getProteinHit(String accession) {
		if (this.visProteinHits.isEmpty()) {
			return this.proteinHits.get(accession);
		}
		return this.visProteinHits.get(accession);
	}
	
	/**
	 * Returns the set of proteins associated with this meta-protein.
	 * @return the protein set
	 */
	public Set<ProteinHit> getProteinSet() {
		if (this.visProteinHits.isEmpty()) {
			return new HashSet<ProteinHit>(this.proteinHits.values());
		}
		return new HashSet<ProteinHit>(this.visProteinHits.values());
	}

	/**
	 * Utility method to return a set of peptide hits with distinct sequence
	 * strings.
	 * @return the peptide hit set
	 */
	public Set<PeptideHit> getPeptideSet() {

		// Define new peptide set
		Set<PeptideHit> outSet = new HashSet<PeptideHit>();
		
		// Check all peptides whether they are visible (FDR alright or not) 
		for (PeptideHit pep : this.getProteinHitList().getPeptideSet()) {
			List<SpectrumMatch> matches = pep.getSpectrumMatches();
			for (SpectrumMatch match : matches) {
				if (match.isVisible()) {
					outSet.add(pep);
					break;
				}
			}
		}
	return outSet;
	}

	/**
	 * Utility method to return a set of spectrum matches with distinct search
	 * spectrum IDs.
	 * @return the spectrum match set
	 */
	public Set<SpectrumMatch> getMatchSet() {
		return this.getProteinHitList().getMatchSet();
	}

	/**
	 * Associates the specified list of protein hits with this meta-protein.
	 * @param proteinHits
	 */
	public void addAll(List<ProteinHit> proteinHits) {
		for (ProteinHit ph : proteinHits) {
            visProteinHits.put(ph.getAccession(), ph);
			ph.setMetaProteinHit(this);
		}
	}

	/**
	 * Returns whether any proteins are associated with this meta-protein.
	 * @return <code>true</code> if not empty, <code>false</code> otherwise
	 */
	// TODO: unused method, remove?
	public boolean isEmpty() {
		return this.proteinHits.isEmpty();
	}
	
	/**
	 * This method filter by FDR threshold for all visible protein hits.
	 * @param fdr the FDR threshold
	 */
	@Override
	public void setFDR(double fdr) {
        visProteinHits = new LinkedHashMap<String, ProteinHit>();
		for (Map.Entry<String, ProteinHit> entry : proteinHits.entrySet()) {
			ProteinHit hit = entry.getValue();
			hit.setFDR(fdr);
			if (hit.isVisible()) {
                visProteinHits.put(entry.getKey(), hit);
			}
		}
	}

	@Override
	public boolean isVisible() {
		return !visProteinHits.isEmpty();
	}
	
	@Override
	public List<? extends Taxonomic> getTaxonomicChildren() {
		return getProteinHitList();
	}
	
	@Override
	public boolean isSelected() {
		boolean res = true;
		for (ProteinHit ph : this.proteinHits.values()) {
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
		for (ProteinHit ph : this.proteinHits.values()) {
			ph.setSelected(selected);
		}
	}
	
	@Override
	public Set<Object> getProperties(ChartType type) {
		// Aggregate properties of associated proteins
		Set<Object> res = new HashSet<Object>();
		for (ProteinHit protHit : getProteinHitList()) {
			res.addAll(protHit.getProperties(type));
		}
		return res;
	}
	
	@Override
	public TaxonomyNode getTaxonomyNode() {
		if (super.getTaxonomyNode() == null) {
			return this.getProteinHitList().get(0).getTaxonomyNode();
		}
		return super.getTaxonomyNode();
	}
}