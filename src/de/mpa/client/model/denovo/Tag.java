package de.mpa.client.model.denovo;

public class Tag {
	
	/**
	 * This variable holds the sequence containing the mass gaps.
	 */
	private String gappedSeq;
	
	/**
	 * This variable holds the formatted sequence without mass gaps.
	 */
	private String formattedSeq;
		
	/**
	 * The total mass
	 */
	private double totalMass;
	
	
	public Tag(String gappedSeq, String formattedSeq, double totalMass) {
		this.gappedSeq = gappedSeq;
		this.formattedSeq = formattedSeq;
		this.totalMass = totalMass;
	}

	public String getGappedSeq() {
		return gappedSeq;
	}

	public String getFormattedSeq() {
		return formattedSeq;
	}

	public double getTotalMass() {
		return totalMass;
	}
	
	
}
