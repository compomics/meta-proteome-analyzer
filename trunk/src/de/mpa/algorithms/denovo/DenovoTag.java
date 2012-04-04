package de.mpa.algorithms.denovo;

import de.mpa.db.accessor.Pepnovohit;

/**
 * This class represents a denovo tag, with the N-terminal gap and C-terminal gap masses.
 * @author T. Muth
 *
 */
public class DenovoTag {
	
	/**
	 * This variable holds the de-novo tag sequence.
	 */
	private String sequence;
	
	/**
	 * This variable holds the N-terminal gap.
	 */
	private double nTermGap;
	
	/**
	 * This variable holds the C-terminal gap.
	 */
	private double cTermGap;
	
	/**
	 * Constructs the de-novo sequence tag object.
	 * @param sequence The de-novo tag sequence.
	 * @param nTermGap The N-terminal gap.
	 * @param cTermGap The C-terminal gap.
	 */
	public DenovoTag(String sequence, double nTermGap, double cTermGap) {
		this.sequence = sequence;
		this.nTermGap = nTermGap;
		this.cTermGap = cTermGap;
	}
	
	/**
	 * Constructs the de-novo sequence tag object.
	 * @param sequence The de-novo tag sequence.
	 * @param nTermGap The N-terminal gap.
	 * @param cTermGap The C-terminal gap.
	 */
	public DenovoTag(Pepnovohit pepnovoHit) {
		this.sequence = pepnovoHit.getSequence();
		this.nTermGap = pepnovoHit.getN_gap().doubleValue();
		this.cTermGap = pepnovoHit.getC_gap().doubleValue();
	}
	
	/**
	 * Returns the de-novo tag sequence.
	 * @return
	 */
	public String getSequence() {
		return sequence;
	}
	
	/**
	 * This method converts the tag to the gapped peptide format.
	 * @return The gapped peptide format String.
	 */
	public String convertToGappedPeptideFormat(){
		String newSequence = "";
		if(nTermGap > 0){
			newSequence += "<" + nTermGap + ">,";
		}
		for (int i = 0; i < sequence.length() - 1; i++){			
			if(i+4 < sequence.length()&& sequence.substring(i, i+4).equals("M+16")){
				newSequence += sequence.substring(i, i+4) + ",";
				i = i+3;
			} else {
				newSequence += sequence.substring(i, i+1) + ",";
			}			
		}
		newSequence += sequence.charAt(sequence.length()-1);
		
		if(cTermGap > 0){
			newSequence += ",<" + cTermGap + ">";
		}
	    return newSequence;
	}
	
	public double getnTermGap() {
		return nTermGap;
	}
	
	public double getcTermGap() {
		return cTermGap;
	}
}
