package de.mpa.io.parser.crux;

public class CruxHit {
	
    private int scanNumber;
    private int charge;    
    private double precursorMZ;
    private double neutralMass;
    private double peptideMass;
    private double deltaCN;
    private double xCorrScore;
    private int xCorrRank;
    private double percolatorScore;
    private int percolatorRank;
    private double qValue;
    private double pepValue;
    
    private int matchesSpectrum;    
    private String peptide;
    private String cleavageType;
    private String proteinid;
    private String flankingAA;
	public int getScanNumber() {
		return this.scanNumber;
	}
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	public int getCharge() {
		return this.charge;
	}
	public void setCharge(int charge) {
		this.charge = charge;
	}
	public double getPrecursorMZ() {
		return this.precursorMZ;
	}
	public void setPrecursorMZ(double precursorMZ) {
		this.precursorMZ = precursorMZ;
	}
	public double getNeutralMass() {
		return this.neutralMass;
	}
	public void setNeutralMass(double neutralMass) {
		this.neutralMass = neutralMass;
	}
	public double getPeptideMass() {
		return this.peptideMass;
	}
	public void setPeptideMass(double peptideMass) {
		this.peptideMass = peptideMass;
	}
	public double getDeltaCN() {
		return this.deltaCN;
	}
	public void setDeltaCN(double deltaCN) {
		this.deltaCN = deltaCN;
	}
	public double getxCorrScore() {
		return this.xCorrScore;
	}
	public void setxCorrScore(double xCorrScore) {
		this.xCorrScore = xCorrScore;
	}
	public int getxCorrRank() {
		return this.xCorrRank;
	}
	public void setxCorrRank(int xCorrRank) {
		this.xCorrRank = xCorrRank;
	}
	public double getPercolatorScore() {
		return this.percolatorScore;
	}
	public void setPercolatorScore(double percolatorScore) {
		this.percolatorScore = percolatorScore;
	}
	public int getPercolatorRank() {
		return this.percolatorRank;
	}
	public void setPercolatorRank(int percolatorRank) {
		this.percolatorRank = percolatorRank;
	}
	public double getqValue() {
		return this.qValue;
	}
	public void setqValue(double qValue) {
		this.qValue = qValue;
	}
	public int getMatchesSpectrum() {
		return this.matchesSpectrum;
	}
	public void setMatchesSpectrum(int matchesSpectrum) {
		this.matchesSpectrum = matchesSpectrum;
	}
	public String getPeptide() {
		return this.peptide;
	}
	public void setPeptide(String peptide) {
		this.peptide = peptide;
	}
	public String getCleavageType() {
		return this.cleavageType;
	}
	public void setCleavageType(String cleavageType) {
		this.cleavageType = cleavageType;
	}
	public String getProteinid() {
		return this.proteinid;
	}
	public void setProteinid(String proteinid) {
		this.proteinid = proteinid;
	}
	public String getFlankingAA() {
		return this.flankingAA;
	}
	public void setFlankingAA(String flankingAA) {
		this.flankingAA = flankingAA;
	}
	public double getPepValue() {
		return this.pepValue;
	}
	public void setPepValue(double pepValue) {
		this.pepValue = pepValue;
	}
    
    
    }
