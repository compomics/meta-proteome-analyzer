package de.mpa.client.model.dbsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mpa.analysis.Masses;
import de.mpa.analysis.taxonomy.Taxonomic;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.panels.ComparePanel.CompareData;
import de.mpa.util.Formatter;

/**
 * This class represents a peptide hit.
 * @author T.Muth
 *
 */
public class PeptideHit implements Serializable, Comparable<PeptideHit>, Taxonomic, Hit {
	
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 1L;
	
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
	 * The peptide spectrum match(es) for this peptide hit.
	 */
	private Map<Long, SpectrumMatch> spectrumMatches;

	/**
	 * Visible spectrum matches for this peptide hit.
	 */
	private Map<Long, SpectrumMatch> visSpectrumMatches;

	/**
	 * The NCBI taxonomy node of the peptide.
	 */
	private TaxonomyNode taxonNode;
	
	/**
	 * The database IDs of the experiments which contain the protein hit.
	 */
	private Set<Long> experimentIDs;
	
	/**
	 * PeptideHit constructor, taking the sequence as only parameter.
	 * @param sequence The String sequence.
	 * @param spectrumMatches The list of peptide spectrum matches.
	 */
	public PeptideHit(String sequence, SpectrumMatch spectrumMatch) {
		this.sequence = sequence;
		this.proteinHits = new ArrayList<ProteinHit>();
		this.spectrumMatches = new HashMap<Long, SpectrumMatch>();
		this.experimentIDs = new HashSet<Long>();
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
		this.experimentIDs = new HashSet<Long>();
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
		return spectrumMatches.values().iterator().next();
	}

	/**
	 * Returns the list of spectrum matches.
	 * @return the list of spectrum matches.
	 */
	public List<SpectrumMatch> getSpectrumMatches() {
		if (visSpectrumMatches == null) {
			return new ArrayList<>(spectrumMatches.values());
		}
		return new ArrayList<>(visSpectrumMatches.values());
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
		spectrumMatches.put(sm.getSearchSpectrumID(), sm);
		sm.addPeptideHit(this);
	}

	/**
	 * Returns the spectrum match that is mapped to the specified search spectrum ID.
	 * @param id The search spectrum ID.
	 * @return The mapped spectrum match.
	 */
	public SpectrumMatch getSpectrumMatch(long id) {
		if (visSpectrumMatches != null) {
			return visSpectrumMatches.get(id);
		} else {
			return spectrumMatches.get(id);
		}
	}

	/**
	 * Returns spectral count.
	 * @return the spectral count.
	 */
	public int getSpectralCount() {
		return getSpectrumMatches().size();
	}

	/**
	 * Adds the specified protein hit to the list of proteins associated with
	 * this peptide.
	 * @param proteinHit the protein to add
	 */
	public void addProteinHit(ProteinHit proteinHit) {
		// to replace a protein hit we need to remove it first,
		// does nothing if provided hit is new anyway
		proteinHits.remove(proteinHit);
		proteinHits.add(proteinHit);
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
	public List<ProteinHit> getProteinHits() {
		return proteinHits;
	}

	/**
	 * Gets the experiments IDs in which the peptide was identified
	 * @return experiment IDs.
	 */
	public Set<Long> getExperimentIDs() {
		return experimentIDs;
	}
	
	/**
	 * Adds the IDs of the experiments which contain this peptide.
	 */
	public void addExperimentIDs(Set<Long> experimentIDs) {
		this.experimentIDs.addAll(experimentIDs);
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
	}
	
	/**
	 * Sets the false discovery rate. 
	 * @param fdr talse discovery rate
	 */
	@Override
	public void setFDR(double fdr) {
		this.visSpectrumMatches = new HashMap<Long, SpectrumMatch>();
		for (SpectrumMatch match : spectrumMatches.values()) {
			match.setFDR(fdr);
			if (match.isVisible()) {
				visSpectrumMatches.put(match.getSearchSpectrumID(), match);
			}
		}
	}

	@Override
	public boolean isVisible() {
		return !visSpectrumMatches.isEmpty();
	}

	@Override
	public TaxonomyNode getTaxonomyNode() {
		return taxonNode;
	}

	@Override
	public void setTaxonomyNode(TaxonomyNode taxonNode) {
		this.taxonNode = taxonNode;
	}

	@Override
	public List<? extends Taxonomic> getTaxonomicChildren() {
		return this.getSpectrumMatches();
	}

	
	@Override
	public String toString() {
		return getSequence();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PeptideHit) {
			return getSequence().equals(((PeptideHit) obj).getSequence());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (this.getSequence() == null) return super.hashCode();
		else return this.getSequence().hashCode();
	}

	/**
	 * Function to use as TreeSet
	 */
	public int compareTo(PeptideHit that) {
		return this.getSequence().compareTo(that.getSequence());
	}

	@Override
	public Set<Object> getProperties(ChartType type) {
		Set<Object> res = new HashSet<Object>();
		
		// Only for experiments takes the experimentIDs of the peptide
		if (type != CompareData.EXPERIMENT) {
			// Gets properties from the protein hit.
			for (ProteinHit protHit : this.getProteinHits()) {
				res.addAll(protHit.getProperties(type));
			}
		}else {
			// Gets experiemtIDs from itself
			res.addAll(this.getExperimentIDs());
		}
		
		return res;
	}
	
	/**
	 * Returns the molecular weight in kDa for the peptide.
	 * @return Molecular peptide weight.
	 */
	public double getMolecularWeight() {
		// Get the masses with the amino acid masses.
		Map<String, Double> masses = Masses.getInstance();

		// Start with the N-terminal mass
		double molWeight = Masses.N_term;

		// Get the protein sequence.

		// Iterate the protein sequence and add the molecular masses.
		for (char letter : sequence.toCharArray()) {
			Double aaWeight = masses.get(String.valueOf(letter));
			if (aaWeight != null) {
				molWeight += aaWeight;
			}
		}

		// Add the C-terminal mass.
		molWeight += Masses.C_term;

		// Get the weight in kDa
		return Formatter.roundDouble((molWeight / 1000.0), 3);
	}
	
}
