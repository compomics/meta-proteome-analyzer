package de.mpa.client.model.denovo;

import java.util.TreeMap;


public class DenovoTagHit {
	
	/**
	 * The tag sequence.
	 */
	private String tagSequence;
	
	/**
	 * The total mass
	 */
	private double totalMass;
	
	/**
	 * Spectrum hit for the de-novo tag.
	 */
	private TreeMap<Long, SpectrumHit> spectrumHits;
	
	/**
	 * Constructor building a de-novo tag hit. Parameters are the tag sequence, the total mass and the (first) spectrum hit.
	 * @param tagSequence The tag sequence.
	 * @param totalMass The total mass of the hit.
	 * @param spectrumHit The (first) spectrum hit.
	 */
	public DenovoTagHit(String tagSequence, double totalMass, SpectrumHit spectrumHit) {
		this.tagSequence = tagSequence;
		this.totalMass = totalMass;
		this.spectrumHits = new TreeMap<Long, SpectrumHit>();
		this.spectrumHits.put(spectrumHit.getSpectrumid(), spectrumHit);
	}

	/**
	 * Returns the tag sequence.
	 * @return The tag sequence.
	 */
	public String getTagSequence() {
		return tagSequence;
	}
	
	/**
	 * Returns the total mass.
	 * @return The total mass.
	 */
	public double getTotalMass() {
		return totalMass;
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
