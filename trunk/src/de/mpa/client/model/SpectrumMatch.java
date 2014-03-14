package de.mpa.client.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.mpa.analysis.taxonomy.Taxonomic;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.client.model.dbsearch.Hit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.panels.ComparePanel.CompareData;

/**
 * Class holding spetrum match data.
 */
public class SpectrumMatch implements Serializable, Comparable<SpectrumMatch>, Taxonomic, Hit {
	
	/**
	 * Serialization ID set to default == 1L;
	 */
	private static final long serialVersionUID = 1L; 
	
	/**
	 * Flag denoting whether this match is selected for export.
	 */
	private boolean selected = true;
	
	/**
	 * The search spectrum id;
	 */
	protected long searchSpectrumID;
	
	/**
	 * The spectrum title.
	 */
	protected String title;
	
	/**
	 * The start index byte position of the associated spectrum.
	 */
	private long startIndex;
	
	/**
	 * The end index byte position of the associated spectrum.
	 */
	private long endIndex;
	
	/**
	 * The list of peptide hits this spectrum is associated with.
	 */
	private Collection<PeptideHit> peptideHits;

	/**
	 * The taxonomy node reference.
	 */
	private TaxonomyNode taxonNode;

	/**
	 * The database IDs of the experiments which contain the protein hit.
	 */
	private Set<Long> experimentIDs;
	
	/**
	 * Default empty constructor.
	 */
	public SpectrumMatch() {
//		this.peptideHits = new ArrayList<PeptideHit>();
		this.peptideHits = new HashSet<PeptideHit>();
		this.experimentIDs = new HashSet<Long>();
	}

	/**
	 * Returns whether this spectrum match is selected for exporting. 
	 * @return <code>true</code> if match is selected for export, 
	 * <code>false</code> otherwise.
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * Sets whether this spectrum match is selected for exporting. 
	 * @param selected <code>true</code> if match is selected for export, 
	 * <code>false</code> otherwise.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Returns the search spectrum ID.
	 * @return the search spectrum ID
	 */
	public long getSearchSpectrumID() {
		return searchSpectrumID;
	}
	
	/**
	 * Sets the search spectrum id.
	 * @param searchSpectrumID The search spectrum id
	 */
	public void setSearchSpectrumID(long searchSpectrumID) {
		this.searchSpectrumID = searchSpectrumID;
	}

	
	/**
	 * Gets the spectrum title.
	 * @return The spectrum title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the spectrum title.
	 * @param The spectrum title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the byte position of the beginning of the spectrum associated
	 * with this match inside an exported spectrum container file.
	 * @return the start index byte position
	 */
	public long getStartIndex() {
		return startIndex;
	}

	/**
	 * Sets the byte position of a spectrum to be associated with this match
	 * inside a spectrum container file which is to be exported.
	 * @param startIndex the start index byte position
	 */
	public void setStartIndex(long startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * Returns the byte position of the end of the spectrum associated with this
	 * match inside an exported spectrum container file.
	 * @return the end index byte position
	 */
	public long getEndIndex() {
		return endIndex;
	}

	/**
	 * Sets the byte position of the end of the spectrum associated with this
	 * match inside an exported spectrum container file.
	 * @param endIndex the end index byte position
	 */
	public void setEndIndex(long endIndex) {
		this.endIndex = endIndex;
	}
	
	/**
	 * Returns the list of peptide hits associated with this match.
	 * @return the peptide hits
	 */
	public Collection<PeptideHit> getPeptideHits() {
		return this.peptideHits;
	}
	
	/**
	 * Adds a peptide hit to the list of peptides associated with this match.
	 * @param peptideHit the peptide hit to add
	 */
	public void addPeptideHit(PeptideHit peptideHit) {
		// to replace a peptide hit we need to remove it first, does nothing if provided hit is new anyway
		this.peptideHits.remove(peptideHit);
		this.peptideHits.add(peptideHit);
	}
	
	/**
	 * Gets the experiments IDs in which the spectrum was identified
	 * @return experiment IDs.
	 */
	public Set<Long> getExperimentIDs() {
		return experimentIDs;
	}
	
	/**
	 * Adds the IDs of the experiments which contain this spectrum.
	 */
	public void addExperimentIDs(Set<Long> experimentIDs) {
		this.experimentIDs.addAll(experimentIDs);
	}
	
	@Override
	public void setFDR(double fdr) {
		// do nothing
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public TaxonomyNode getTaxonomyNode() {
		return this.taxonNode;
	}

	@Override
	public void setTaxonomyNode(TaxonomyNode taxonNode) {
		this.taxonNode = taxonNode;
	}
	
	@Override
	public List<? extends Taxonomic> getTaxonomicChildren() {
		return null;
	}

	@Override
	public String toString() {
//		return "[ssID = " + this.searchSpectrumID + "]";
		return "" + this.searchSpectrumID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpectrumMatch) {
			SpectrumMatch that = (SpectrumMatch) obj;
			return (this.getSearchSpectrumID() == that.getSearchSpectrumID());
		}
		return false;
	}

	@Override
	public int compareTo(SpectrumMatch that) {
		long delta = this.getSearchSpectrumID() - that.getSearchSpectrumID();
		return (delta < 0L) ? -1 : (delta > 0L) ? 1 : 0;
	}

	@Override
	public Set<Object> getProperties(ChartType type) {
			Set<Object> res = new HashSet<Object>();
			
			
			// Only for experiments takes the experimentIDs of the spectrum
			if (type != CompareData.EXPERIMENT) {
				// Gets properties from the protein hit.
				for (PeptideHit pepHit : this.peptideHits) {
					res.addAll(pepHit.getProperties(type));
				}
			}else {
				// Gets experimentIDs from itself
				res.addAll(this.getExperimentIDs());
			}
			
			return res;
		}
}
