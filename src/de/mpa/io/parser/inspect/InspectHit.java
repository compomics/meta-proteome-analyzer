package de.mpa.io.parser.inspect;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 07.09.2010
 * Time: 15:36:05
 * To change this template use File | Settings | File Templates.
 */
public class InspectHit {

    private long scanNumber;
    private String annotation;
    private String protein;
    private int charge;
    private double mqScore;
    private int length;
    private double totalPRMScore;
    private double medianPRMScore;
    private double fractionY;
    private double fractionB;
    private double intensity;
    private double ntt;
    private double pValue;
    private double fScore;
    private double deltaScore;
    private double deltaScoreOther;
    private double recordNumber;
    private long dbFilePos;
    private long specFilePos;
    private double precursorMZ;
    private double precursorMZError;

    public long getScanNumber() {
        return this.scanNumber;
    }

    public void setScanNumber(long scanNumber) {
        this.scanNumber = scanNumber;
    }

    public String getAnnotation() {
        return this.annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getProtein() {
        return this.protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public int getCharge() {
        return this.charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public double getMqScore() {
        return this.mqScore;
    }

    public void setMqScore(double mqScore) {
        this.mqScore = mqScore;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getTotalPRMScore() {
        return this.totalPRMScore;
    }

    public void setTotalPRMScore(double totalPRMScore) {
        this.totalPRMScore = totalPRMScore;
    }

    public double getMedianPRMScore() {
        return this.medianPRMScore;
    }

    public void setMedianPRMScore(double medianPRMScore) {
        this.medianPRMScore = medianPRMScore;
    }

    public double getFractionY() {
        return this.fractionY;
    }

    public void setFractionY(double fractionY) {
        this.fractionY = fractionY;
    }

    public double getFractionB() {
        return this.fractionB;
    }

    public void setFractionB(double fractionB) {
        this.fractionB = fractionB;
    }

    public double getIntensity() {
        return this.intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getNtt() {
        return this.ntt;
    }

    public void setNtt(double ntt) {
        this.ntt = ntt;
    }

    public double getpValue() {
        return this.pValue;
    }

    public void setpValue(double pValue) {
        this.pValue = pValue;
    }

    public double getfScore() {
        return this.fScore;
    }

    public void setfScore(double fScore) {
        this.fScore = fScore;
    }

    public double getDeltaScore() {
        return this.deltaScore;
    }

    public void setDeltaScore(double deltaScore) {
        this.deltaScore = deltaScore;
    }

    public double getDeltaScoreOther() {
        return this.deltaScoreOther;
    }

    public void setDeltaScoreOther(double deltaScoreOther) {
        this.deltaScoreOther = deltaScoreOther;
    }

    public double getRecordNumber() {
        return this.recordNumber;
    }

    public void setRecordNumber(double recordNumber) {
        this.recordNumber = recordNumber;
    }

    public long getDbFilePos() {
        return this.dbFilePos;
    }

    public void setDbFilePos(long dbFilePos) {
        this.dbFilePos = dbFilePos;
    }

    public long getSpecFilePos() {
        return this.specFilePos;
    }

    public void setSpecFilePos(long specFilePos) {
        this.specFilePos = specFilePos;
    }

    public double getPrecursorMZ() {
        return this.precursorMZ;
    }

    public void setPrecursorMZ(double precursorMZ) {
        this.precursorMZ = precursorMZ;
    }

    public double getPrecursorMZError() {
        return this.precursorMZError;
    }

    public void setPrecursorMZError(double precursorMZError) {
        this.precursorMZError = precursorMZError;
    }
}
