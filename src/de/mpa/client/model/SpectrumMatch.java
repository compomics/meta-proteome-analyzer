package de.mpa.client.model;

import java.io.Serializable;

public class SpectrumMatch implements Serializable, Comparable<SpectrumMatch> {
	
	/**
	 * Flag denoting whether this match is selected for export.
	 */
	private boolean selected = true;
	
	/**
	 * The search spectrum id;
	 */
	protected long searchSpectrumID;
	
	/**
	 * The start index byte position of the associated spectrum.
	 */
	private long startIndex;
	
	/**
	 * The end index byte position of the associated spectrum.
	 */
	private long endIndex;

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
	
}
