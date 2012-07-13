package de.mpa.client.model;

public class SpectrumMatch {
	/**
	 * Marker, if Match is selected for export.
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
	
	@Override
	public String toString() {
		return "[ssID = " + searchSpectrumID + "]"; 
	}

	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
