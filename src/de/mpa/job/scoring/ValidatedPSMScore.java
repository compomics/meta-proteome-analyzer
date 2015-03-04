package de.mpa.job.scoring;

/**
 * Model class for the validated PSM scores, used for X!Tandem and OMSSA hit validation. 
 * @author T. Muth
 *
 */
public class ValidatedPSMScore {
	
	/**
	 * The original search engine score;
	 */
	private double score;
	
	/**
	 * The posterior error probability.
	 */
	private double pep;
	
	/**
	 * The q-value (FDR threshold) value.
	 */
	private double qvalue;
	
	/**
	 * Constructor for a validated PSM score.
	 * @param score The original search engine score.
	 * @param pep The posterior error probability.
	 * @param qvalue The q-value (FDR threshold) value.
	 */
	public ValidatedPSMScore(double score, double pep, double qvalue) {
		this.score = score;
		this.pep = pep;
		this.qvalue = qvalue;
	}

	/**
	 * Returns the search engine score.
	 * @return The search engine score.
	 */
	public double getScore() {
		return score;
	}
	
	/**
	 * Returns the posterior error probability.
	 * @return The posterior error probability.
	 */
	public double getPep() {
		return pep;
	}
	
	/**
	 * Returns q-value (FDR threshold) value.
	 * @return The q-value (FDR threshold) value.
	 */
	public double getQvalue() {
		return qvalue;
	}

}
