package de.mpa.client.model.denovo;

import java.util.TreeMap;


public class TagHit {
	
	/**
	 * The de-novo tag.
	 */
	private Tag tag;

	
	/**
	 * Spectrum hit for the de-novo tag.
	 */
	private TreeMap<Long, SpectrumHit> spectrumHits;
	
	/**
	 * Constructor building a de-novo tag hit. Parameters are the tag sequence, the total mass and the (first) spectrum hit.
	 * @param tag The de-novo tag.
	 * @param totalMass The total mass of the hit.
	 * @param spectrumHit The (first) spectrum hit.
	 */
	public TagHit(Tag tag, SpectrumHit spectrumHit) {
		this.tag = tag;
		this.spectrumHits = new TreeMap<Long, SpectrumHit>();
		this.spectrumHits.put(spectrumHit.getSpectrumid(), spectrumHit);
	}

	/**
	 * Returns the de-novo tag.
	 * @return
	 */
	public Tag getTag() {
		return tag;
	}

	/**
	 * Returns the spectrum hits.
	 * @return The spectrum hits.
	 */
	public TreeMap<Long, SpectrumHit> getSpectrumHits() {
		return spectrumHits;
	}
	
	/**
	 * Sets the spectrum hits.
	 * @param spectrumHits The spectrum hits.
	 */
	public void setSpectrumHits(TreeMap<Long, SpectrumHit> spectrumHits) {
		this.spectrumHits = spectrumHits;
	}

	/**
	 * Convenience method to retrieve the first spectrum hit.
	 * @return The the first spectrum hit.
	 */
	public SpectrumHit getFirstSpectrumHit() {
		return spectrumHits.firstEntry().getValue();
	}
	
	/**
	 * Returns the tag spectral count.
	 * @return The tag spectral count.
	 */
	public int getTagSpecCount() {
		return spectrumHits.size();
	}
}
