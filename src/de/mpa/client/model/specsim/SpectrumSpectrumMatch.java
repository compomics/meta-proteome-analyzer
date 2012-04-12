package de.mpa.client.model.specsim;

/**
 * Data structure for a spectrum-to-spectrum match.
 * 
 * @author behne
 */
public class SpectrumSpectrumMatch {
	
	/**
	 * The searchspectrum ID.
	 */
	private long searchspectrumID;
	
	/**
	 * The libspectrum ID.
	 */
	private long libspectrumID;
	
	/**
	 * The similarity score.
	 */
	private double similarity;
	
	/**
	 * Class constructor specifying seachspectrumID, libspectrumID and a score.
	 * 
	 * @param searchspectrumID
	 * @param libspectrumID
	 * @param similarity
	 */
	public SpectrumSpectrumMatch(long searchspectrumID, long libspectrumID,
			double similarity) {
		this.searchspectrumID = searchspectrumID;
		this.libspectrumID = libspectrumID;
		this.similarity = similarity;
	}

	/**
	 * @return the searchspectrumID
	 */
	public long getSearchspectrumID() {
		return searchspectrumID;
	}

	/**
	 * @return the libspectrumID
	 */
	public long getLibspectrumID() {
		return libspectrumID;
	}

	/**
	 * @return the similarity
	 */
	public double getSimilarity() {
		return similarity;
	}

}
