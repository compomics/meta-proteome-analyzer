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
		return scanNumber;
	}
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	public int getCharge() {
		return charge;
	}
	public void setCharge(int charge) {
		this.charge = charge;
	}
	public double getPrecursorMZ() {
		return precursorMZ;
	}
	public void setPrecursorMZ(double precursorMZ) {
		this.precursorMZ = precursorMZ;
	}
	public double getNeutralMass() {
		return neutralMass;
	}
	public void setNeutralMass(double neutralMass) {
		this.neutralMass = neutralMass;
	}
	public double getPeptideMass() {
		return peptideMass;
	}
	public void setPeptideMass(double peptideMass) {
		this.peptideMass = peptideMass;
	}
	public double getDeltaCN() {
		return deltaCN;
	}
	public void setDeltaCN(double deltaCN) {
		this.deltaCN = deltaCN;
	}
	public double getxCorrScore() {
		return xCorrScore;
	}
	public void setxCorrScore(double xCorrScore) {
		this.xCorrScore = xCorrScore;
	}
	public int getxCorrRank() {
		return xCorrRank;
	}
	public void setxCorrRank(int xCorrRank) {
		this.xCorrRank = xCorrRank;
	}
	public double getPercolatorScore() {
		return percolatorScore;
	}
	public void setPercolatorScore(double percolatorScore) {
		this.percolatorScore = percolatorScore;
	}
	public int getPercolatorRank() {
		return percolatorRank;
	}
	public void setPercolatorRank(int percolatorRank) {
		this.percolatorRank = percolatorRank;
	}
	public double getqValue() {
		return qValue;
	}
	public void setqValue(double qValue) {
		this.qValue = qValue;
	}
	public int getMatchesSpectrum() {
		return matchesSpectrum;
	}
	public void setMatchesSpectrum(int matchesSpectrum) {
		this.matchesSpectrum = matchesSpectrum;
	}
	public String getPeptide() {
		return peptide;
	}
	public void setPeptide(String peptide) {
		this.peptide = peptide;
	}
	public String getCleavageType() {
		return cleavageType;
	}
	public void setCleavageType(String cleavageType) {
		this.cleavageType = cleavageType;
	}
	public String getProteinid() {
		return proteinid;
	}
	public void setProteinid(String proteinid) {
		this.proteinid = proteinid;
	}
	public String getFlankingAA() {
		return flankingAA;
	}
	public void setFlankingAA(String flankingAA) {
		this.flankingAA = flankingAA;
	}
	public double getPepValue() {
		return pepValue;
	}
	public void setPepValue(double pepValue) {
		this.pepValue = pepValue;
	}
    
    
    }
