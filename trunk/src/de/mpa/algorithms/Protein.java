package de.mpa.algorithms;

public class Protein {
	
	private String accession;
	private String description;
	private String sequence;
	
	public Protein(String accession) {
		this(accession, null, null);
	}
	
	public Protein(String accession, String description) {
		this(accession, description, null);
	}
	
	public Protein(String accession, String description, String sequence) {
		this.accession = accession;
		this.description = description;
		this.sequence = sequence;
	}

	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

}
