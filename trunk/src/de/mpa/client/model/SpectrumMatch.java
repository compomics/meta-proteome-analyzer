package de.mpa.client.model;

public class SpectrumMatch {
	
	/**
	 * Flag denoting whether this match is selected for export.
	 */
	private boolean selected = true;
	
	/**
	 * The search spectrum id;
	 */
	protected long searchSpectrumID;

	/**
	 * Default empty constructor.
	 */
	public SpectrumMatch() { }
	
	/**
	 * Returns the spectrum specific id.
	 * @return The spectrum id.
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
	
	@Override
	public String toString() {
		return "[ssID = " + searchSpectrumID + "]"; 
	}
}
