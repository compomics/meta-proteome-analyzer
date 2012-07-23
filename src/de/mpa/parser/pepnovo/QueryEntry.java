package de.mpa.parser.pepnovo;

import java.util.List;

/**
 * This class represents one entry (line) of the PepNovo+ BLAST query file.
 * @author T.Muth
 *
 */
public class QueryEntry {
	
	/**
	 * The number of the spectrum (starting at 1)
	 */
	private int spectrumNumber;
	
	/**
	 * List of input queries.
	 */
	private List<String> queries;
	
	/**
	 * The score of the entry.
	 */
	private double score;
	
	/**
	 * QueryEntry default constructor.
	 */
	public QueryEntry() {}

	/**
	 * Returns the spectrum number.
	 * @return The spectrum number.
	 */
	public int getSpectrumNumber() {
		return spectrumNumber;
	}
	
	/**
	 * Returns the list of queries.
	 * @return The list of queries.
	 */
	public List<String> getQueries() {
		return queries;
	}
	
	/**
	 * Returns the number of sequences.
	 * @return The number of sequences.
	 */
	public int getNumSequences() {
		return queries.size();
	}

	/**
	 * Returns the query score.
	 * @return The query score.
	 */
	public double getScore() {	
		return score;
	}

	/**
	 * Sets the spectrum number. 
	 * @param spectrumNumber
	 */
	public void setSpectrumNumber(int spectrumNumber) {
		this.spectrumNumber = spectrumNumber;
	}
	
	/**
	 * Sets the queries.
	 * @param queries Query sequences.
	 */
	public void setQueries(List<String> queries) {
		this.queries = queries;
	}
	
	/**
	 * Sets the query score.
	 * @param score The query score. 
	 */
	public void setScore(double score) {
		this.score = score;
	}
}
