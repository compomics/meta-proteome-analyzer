package de.mpa.client.model.specsim;

import de.mpa.client.model.SpectrumMatch;

/**
 * Data structure for a spectrum-to-spectrum match.
 * 
 * @author behne
 */
@SuppressWarnings("serial")
public class SpectrumSpectrumMatch extends SpectrumMatch {
	
	/**
	 * The libspectrum ID.
	 */
	private final long libspectrumID;
	
	/**
	 * The similarity score.
	 */
	private final double similarity;
	
	/**
	 * Class constructor specifying seachspectrumID, libspectrumID and a score.
	 * 
	 * @param searchspectrumID
	 * @param libspectrumID
	 * @param similarity
	 */
	public SpectrumSpectrumMatch(long searchspectrumID, long libspectrumID,
			double similarity) {
        searchSpectrumID = searchspectrumID;
		this.libspectrumID = libspectrumID;
		this.similarity = similarity;
	}

	/**
	 * @return the libspectrumID
	 */
	public long getLibSpectrumID() {
		return this.libspectrumID;
	}

	/**
	 * @return the similarity
	 */
	public double getSimilarity() {
		return this.similarity;
	}

}
