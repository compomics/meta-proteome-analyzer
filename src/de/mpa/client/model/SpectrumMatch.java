package de.mpa.client.model;

import java.io.Serializable;

import de.mpa.io.MascotGenericFile;

public class SpectrumMatch implements Serializable {
	
	/**
	 * Flag denoting whether this match is selected for export.
	 */
	private boolean selected = true;
	
	/**
	 * The search spectrum id;
	 */
	protected long searchSpectrumID;
	
	/**
	 * The Mascot Generic File
	 */
	private MascotGenericFile mgf;

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
	 * Returns the spectrum file reference.
	 * @return the spectrum file reference.
	 */
	public MascotGenericFile getMgf() {
		return mgf;
	}
	
	/**
	 * Sets the spectrum file reference.
	 * @param mgf the spectrum file to set.
	 */
	public void setMgf(MascotGenericFile mgf) {
		this.mgf = mgf;
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpectrumMatch) {
			SpectrumMatch that = (SpectrumMatch) obj;
			return (this.getSearchSpectrumID() == that.getSearchSpectrumID());
		}
		return false;
	}
	
}
