package de.mpa.client.model;

public class SpectrumMatch {

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
	 * Sets the spectrum id.
	 * @param spectrumid The spectrum id
	 */
	public void setSpectrumId(long spectrumid) {
		this.searchSpectrumID = spectrumid;
	}
}
