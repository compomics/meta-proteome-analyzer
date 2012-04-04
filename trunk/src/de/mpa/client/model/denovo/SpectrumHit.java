package de.mpa.client.model.denovo;

import java.util.ArrayList;
import java.util.List;

import de.mpa.db.accessor.Pepnovohit;


/**
 * The spectrum hit is a de-novo spectrum assignment containing multiple de-novo hits. 
 * @author T. Muth
 *
 */
public class SpectrumHit {
	
	/**
	 * The original (search) spectrum id.
	 */
	private long spectrumid;
	
	/**
	 * The spectrum title.
	 */
	private String spectrumTitle;
	
	/**
	 * The de-novo solution hits (for each spectrum).
	 */
	private List<Pepnovohit> denovoHits = new ArrayList<Pepnovohit>();
	
	/**
	 * The SpectrumHit instance.
	 * @param spectrumid The original spectrum id.
	 * @param spectrumTitle The spectrum title.
	 * @param denovoHits The de-novo solution hits for each spectrum.
	 */
	public SpectrumHit(long spectrumid, String spectrumTitle, List<Pepnovohit> denovoHits) {
		this.spectrumid = spectrumid;
		this.spectrumTitle = spectrumTitle;
		this.denovoHits = denovoHits;
	}
	
	/**
	 * Returns the spectrum id.
	 * @return The spectrum id.
	 */
	public long getSpectrumid() {
		return spectrumid;
	}
	
	/**
	 * Returns the spectrum title.
	 * @return The spectrum title.
	 */
	public String getSpectrumTitle() {
		return spectrumTitle;
	}

	/**
	 * Returns the de-novo hit for a spectrum hit.
	 * @return The list of de-novo hits.
	 */
	public List<Pepnovohit> getDenovoHits() {
		return denovoHits;
	}
	
	/**
	 * Returns the number of solutions for the spectrum.
	 * @return The number of solutions.
	 */
	public int getNumberOfSolutions(){
		return denovoHits.size();
	}
}
