package de.mpa.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.Hit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.taxonomy.Taxonomic;
import de.mpa.taxonomy.TaxonomyNode;

/**
 * TODO: API
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
	 * The taxonomy node reference.
	 */
	private TaxonomyNode taxonNode;

	/**
	 * Default empty constructor.
	 */
	public SpectrumMatch() { }

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

	@Override
	public TaxonomyNode getTaxonomyNode() {
		return taxonNode;
	}

	@Override
	public void setTaxonomyNode(TaxonomyNode taxonNode) {
		this.taxonNode = taxonNode;
	}
	
	@Override
	public String toString() {
		return "[ssID = " + searchSpectrumID + "]"; 
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
	public int getCount(Object x, Object y) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<Object> getProperties(ChartType type) {
			Set<Object> res = new HashSet<Object>();
			DbSearchResult dbresObj = Client.getInstance().getDbSearchResult(); // TODO use back mapping
			if (dbresObj != null) {
				Set<PeptideHit> pepSet = ((ProteinHitList)dbresObj.getProteinHitList()).getPeptideSet();
				for (PeptideHit pepHit : pepSet) {
					if (pepHit.getSpectrumMatches().contains(this)) {
						res.addAll(pepHit.getProperties(type));
						break;
					}
				}
			}
			return res;
		}
}
