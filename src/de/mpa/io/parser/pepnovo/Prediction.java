package de.mpa.io.parser.pepnovo;

/**
 * This class represents the prediction information of one line in each spectrum from the pepnovo output.
 * 
 * @author Thilo Muth
 *
 */
public class Prediction {
	
	/**
	 * The sequence/tag rank (starts at 0)
	 */
	private int index;
	
	/**
	 * The ranking score (the major score that is used) 
	 */
	private double rankScore;
	
	/**
	 * The PepNovo score of the sequence
	 */
	private double pepNovoScore;
	
	/**
	 * The mass gap from the N-terminal to the start of the de novo sequence.
	 */
	private double nTermGap;
	
	/**
	 * The mass gap from the C-terminal to the end of the de novo sequence.
	 */
	private double cTermGap;
	
	/**
	 * M+H of the precursor.
	 */
	private double precursorMh;
	
	/**
	 * The used charge.
	 */
	private int charge;
	
	/**
	 * The predicted amino acid sequence.
	 */
	private String sequence;
	
	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getRankScore() {
		return this.rankScore;
	}

	public void setRankScore(double rankScore) {
		this.rankScore = rankScore;
	}

	public double getPepNovoScore() {
		return this.pepNovoScore;
	}

	public void setPepNovoScore(double pepNovoScore) {
		this.pepNovoScore = pepNovoScore;
	}

	public double getnTermGap() {
		return this.nTermGap;
	}

	public void setnTermGap(double nTermGap) {
		this.nTermGap = nTermGap;
	}

	public double getcTermGap() {
		return this.cTermGap;
	}

	public void setcTermGap(double cTermGap) {
		this.cTermGap = cTermGap;
	}

	public double getPrecursorMh() {
		return this.precursorMh;
	}

	public void setPrecursorMh(double precursorMh) {
		this.precursorMh = precursorMh;
	}

	public int getCharge() {
		return this.charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public String getSequence() {
		return this.sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
}
