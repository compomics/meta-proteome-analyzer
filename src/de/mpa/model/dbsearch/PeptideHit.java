package de.mpa.model.dbsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mpa.client.ui.resultspanel.ComparePanel.CompareData;
import de.mpa.client.ui.sharedelements.chart.ChartType;
import de.mpa.model.analysis.Masses;
import de.mpa.model.taxonomy.Taxonomic;
import de.mpa.model.taxonomy.TaxonomyNode;
import de.mpa.util.Formatter;

/**
 * This class represents a peptide hit.
 * @author T.Muth
 *
 */
public class PeptideHit implements Serializable, Comparable<PeptideHit>, Taxonomic, Hit {
	
	/*
	 * FIELDS
	 */
	
	/**
	 * 
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
	private ArrayList<ProteinHit> proteinHits;
	
	/**
	 * The peptide spectrum match(es) for this peptide hit.
	 */
	private ArrayList<PeptideSpectrumMatch> peptideSpectrumMatches;

	/**
	 * Visible spectrum matches for this peptide hit.
	 */
	private ArrayList<PeptideSpectrumMatch> visPeptideSpectrumMatches;

	/**
	 * The database IDs of the experiments which contain the protein hit.
	 */
	private HashSet<Long> experimentIDs;
	
	/**
	 * Taxonomy of this Peptide
	 */
	private TaxonomyNode taxonomyNode;
	
	/*
	 * CONSTRUCTORS
	 */
	
	/**
	 * PeptideHit constructor, taking the sequence as only parameter.
	 * @param sequence The String sequence.
	 * @param spectrumMatches The list of peptide spectrum matches.
	 */
	public PeptideHit(String sequence, PeptideSpectrumMatch psm) {
		this.sequence = sequence;
		this.proteinHits = new ArrayList<ProteinHit>();
		this.peptideSpectrumMatches = new ArrayList<PeptideSpectrumMatch>();
		this.visPeptideSpectrumMatches = new ArrayList<PeptideSpectrumMatch>();
		this.addPeptideSpectrumMatch(psm);
		this.experimentIDs = new HashSet<Long>();
		this.proteinHits = new ArrayList<ProteinHit>();
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
		this.proteinHits = new ArrayList<ProteinHit>();
	}

	/*
	 * METHODS
	 * 
	 * FDR
	 */
	
	/**
	 * Sets the false discovery rate. 
	 * @param fdr false discovery rate
	 */
	@Override
	public void setFDR(double fdr) {
		this.visPeptideSpectrumMatches = new ArrayList<PeptideSpectrumMatch>();
		for (PeptideSpectrumMatch entry : peptideSpectrumMatches) {
			PeptideSpectrumMatch match = entry;
			match.setFDR(fdr);
			if (match.isVisible()) {
				visPeptideSpectrumMatches.add(match);
			}
		}
	}
	
	@Override
	public boolean isVisible() {
		return !visPeptideSpectrumMatches.isEmpty();
	}
	
	/*
	 * METHODS
	 * 
	 * PROTEIN
	 */
	
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
	public ArrayList<ProteinHit> getProteinHits() {
		return proteinHits;
	}
	
	/*
	 * METHODS
	 * 
	 * PSMS
	 */
	
	/**
	 * Returns the list of spectrum matches.
	 * @return the list of spectrum matches.
	 */
	public ArrayList<PeptideSpectrumMatch> getPeptideSpectrumMatches() {
		return visPeptideSpectrumMatches;
	}
	
	/**
	 * Adds a spectrum match to the PeptideHit. 
	 * @param sm The spectrum match.
	 */
	public void addPeptideSpectrumMatch(PeptideSpectrumMatch new_psm) {
		boolean addThisPSM = true;
		for (PeptideSpectrumMatch old_psm : peptideSpectrumMatches) {
			if (old_psm.equals(new_psm)) {
				addThisPSM = false;
				break;
			}
		} 
		if (addThisPSM) {
			this.peptideSpectrumMatches.add(new_psm);
			this.visPeptideSpectrumMatches.add(new_psm);
		}
	}
	
	/**
	 * Returns the spectrum match that is mapped to the specified search spectrum ID.
	 * @param id The search spectrum ID.
	 * @return The mapped spectrum match.
	 */
	public PeptideSpectrumMatch getPeptideSpectrumMatch(Long spectrumID) {
		for (PeptideSpectrumMatch psm : peptideSpectrumMatches) {
			if (psm.getSpectrumID() == spectrumID) {
				return psm;
			}
		}
		return null;
	}
	
	/**
	 * Returns spectral count.
	 * @return the spectral count.
	 */
	public int getSpectralCount() {
		HashSet<Long> specids = new HashSet<Long>();
		for (PeptideSpectrumMatch psm : this.visPeptideSpectrumMatches) {
			specids.add(psm.getSpectrumID());
		}
		return specids.size();
	}
	
	/*
	 * METHODS 
	 * 
	 * TAXONOMY
	 */

	@Override
	public TaxonomyNode getTaxonomyNode() {
		return this.taxonomyNode;
	}

	@Override
	public void setTaxonomyNode(TaxonomyNode taxonNode) {
		this.taxonomyNode = taxonNode;
	}

	@Override
	public List<? extends Taxonomic> getTaxonomicChildren() {
		return null;
	}
	
	/*
	 * METHODS
	 * 
	 * GENERAL
	 */
	
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
	 * Adds the specified protein hit to the list of proteins associated with
	 * this peptide.
	 * @param proteinHit the protein to add
	 */
	public void addProteinHit(ProteinHit proteinHit) {
		boolean add = true;
		for (ProteinHit ph : this.proteinHits) {
			if (proteinHit.equals(ph)) {
				add = false;
				break;
			}
		}
		if (add) {
			this.proteinHits.add(proteinHit);
		}
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
	 * Adds a single ID of an experiment which contain this peptide.
	 */
	public void addExperimentID(Long experimentID) {
		this.experimentIDs.add(experimentID);
	}
	
	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
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
	@Override
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
