package de.mpa.job.evaluation;

public class ProtResultEntry {

	
	private String accession;
	private String desc;
	private int anzPeptide = 0;
	private String sequence; 
	private int inpectHit = 0;
	private int xtandemHit = 0;
	private int omssaHit = 0;
	private int mascotHit = 0;
	private int subOrdinaryHit =0;
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getAnzPeptide() {
		return anzPeptide;
	}
	public void setAnzPeptide(int anzPeptide) {
		this.anzPeptide = anzPeptide;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public int getInpectHit() {
		return inpectHit;
	}
	public void setInpectHit(int inpectHit) {
		this.inpectHit = inpectHit;
	}
	public int getXtandemHit() {
		return xtandemHit;
	}
	public void setXtandemHit(int xtandemHit) {
		this.xtandemHit = xtandemHit;
	}
	public int getOmssaHit() {
		return omssaHit;
	}
	public void setOmssaHit(int omssaHit) {
		this.omssaHit = omssaHit;
	}
	public int getMascotHit() {
		return mascotHit;
	}
	public void setMascotHit(int mascotHit) {
		this.mascotHit = mascotHit;
	}
	public int getSubOrdinaryHit() {
		return subOrdinaryHit;
	}
	public void setSubOrdinaryHit(int subOrdinaryHit) {
		this.subOrdinaryHit = subOrdinaryHit;
	}
	
	
}
