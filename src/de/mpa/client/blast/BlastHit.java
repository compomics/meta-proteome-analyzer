package de.mpa.client.blast;

/**
 * This class contains one BlastHit
 * @author R. Heyer
 */
public class BlastHit {

	/**
	 * Accession of the BLAST hit.
	 */
	String accession;
	
	/**
	 * Name of the BLAST hit.
	 */
	String name;
	
	/**
	 * Length of the BLAST hit.
	 */
	int length;
	
	/**
	 * Score of the BLAST hit.
	 */
	double bitScore;
	
	/**
	 * Type of BLAST
	 */
	String method;
	
	/**
	 * E-value of the BLAST
	 */
	double eValue;
	
	/**
	 * Identities of the BLAST hit.
	 */
	double identities;
	
	/**
	 * Posititves of the BLAST hit.
	 */
	String positives;
	
	/**
	 * Gaps of the BLAST hit.
	 */
	String gaps;
	
	/**
	 * Query sequence for the BLAST search.
	 */
	String query;
	
	/**
	 * Sequence of the BLAST hit.
	 */
	String Sbjct  ;
	
	
	/**
	 * Constructor for a BLASThit
	 * @param Accession. The accession of the BLAST hit.
	 * @param Name. The name for the BLAST hit.
	 */
	public BlastHit(String accession, String name) {
		this.accession 	= accession;
		this.name		= name;
	}


	/**
	 * Gets the accession of the BLAST hit.
	 * @return accession.
	 */
	public String getAccession() {
		return accession;
	}


	/**
	 * Gets the name of the BLAST hit.
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the name of the BLAST hit.
	 * @return sequence length.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Sets the sequence length of a BLAST.
	 * @param length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Gets the score of a BLAST hit.
	 * @return score
	 */
	public double getScore() {
		return bitScore;
	}

	/**
	 * Sets the score of a BLAST hit.
	 * @param score
	 */
	public void setScore(double score) {
		bitScore = score;
	}

	/**
	 * Gets the method for the BLAST
	 * @return BLAST metho
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets  the method for the BLAST.
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Gets the e-value of the BLAST hit.
	 * @return e-value
	 */
	public double geteValue() {
		return eValue;
	}

	/**
	 * Sets the e-value of the BLAST hit.
	 * @param eValue
	 */
	public void seteValue(double eValue) {
		this.eValue = eValue;
	}

	/**
	 * Gets the identities of a BLAST hit as percentage.
	 * @return identities
	 */
	public double getIdentities() {
		return identities;
	}

	/**
	 * Sets the identities of a BLAST hit as percentage.
	 * @param identities
	 */
	public void setIdentities(double identities) {
		this.identities = identities;
	}

	/**
	 * Gets the positives of a BLAST hit.
	 * @return positives.
	 */
	public String getPositives() {
		return positives;
	}

	/**
	 * Sets the positives of a BLAST hit.
	 * @param positives
	 */
	public void setPositives(String positives) {
		this.positives = positives;
	}

	/**
	 * Gets the gaps of a BLAST hit.
	 * @return gaps.
	 */
	public String getGaps() {
		return gaps;
	}

	/**
	 * Sets the gaps of a BLAST hit.
	 * @param gaps
	 */
	public void setGaps(String gaps) {
		this.gaps = gaps;
	}

	/**
	 * Gets the query sequence of a BLAST hit.
	 * @return query sequence.
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Sets the query sequence of a BLAST hit.
	 * @param query.
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Gets the sequence of a the BLAST hit.
	 * @return BLAST sequence.
	 */
	public String getSbjct() {
		return Sbjct;
	}

	/**
	 * Sets the sequence of a BLAST hit.
	 * @param sbjct. Sequence of a BLAST hit.
	 */
	public void setSbjct(String sbjct) {
		Sbjct = sbjct;
	}
}
