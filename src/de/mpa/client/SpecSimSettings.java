
package de.mpa.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for specSimSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="specSimSettings">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tolMz" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="annotatedOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="experimentID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="pickCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="threshScore" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="trafoIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="vectIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="compIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="binWidth" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="binShift" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="profileIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="baseWidth" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="xCorrOffset" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "specSimSettings", propOrder = {
    "tolMz",
    "annotatedOnly",
    "experimentID",
    "pickCount",
    "threshScore",
    "trafoIndex",
    "vectIndex",
    "compIndex",
    "binWidth",
    "binShift",
    "profileIndex",
    "baseWidth",
    "xCorrOffset"
})
public class SpecSimSettings {

    protected double tolMz;
    protected boolean annotatedOnly;
    protected long experimentID;
    protected int pickCount;
    protected double threshScore;
    protected int trafoIndex;
    protected int vectIndex;
    protected int compIndex;
    protected double binWidth;
    protected double binShift;
    protected int profileIndex;
    protected double baseWidth;
    protected int xCorrOffset;

    /**
     * Gets the value of the tolMz property.
     * 
     */
    public double getTolMz() {
        return this.tolMz;
    }

    /**
     * Sets the value of the tolMz property.
     * 
     */
    public void setTolMz(double value) {
        tolMz = value;
    }

    /**
     * Gets the value of the annotatedOnly property.
     * 
     */
    public boolean isAnnotatedOnly() {
        return this.annotatedOnly;
    }

    /**
     * Sets the value of the annotatedOnly property.
     * 
     */
    public void setAnnotatedOnly(boolean value) {
        annotatedOnly = value;
    }

    /**
     * Gets the value of the experimentID property.
     * 
     */
    public long getExperimentID() {
        return this.experimentID;
    }

    /**
     * Sets the value of the experimentID property.
     * 
     */
    public void setExperimentID(long value) {
        experimentID = value;
    }

    /**
     * Gets the value of the pickCount property.
     * 
     */
    public int getPickCount() {
        return this.pickCount;
    }

    /**
     * Sets the value of the pickCount property.
     * 
     */
    public void setPickCount(int value) {
        pickCount = value;
    }

    /**
     * Gets the value of the threshScore property.
     * 
     */
    public double getThreshScore() {
        return this.threshScore;
    }

    /**
     * Sets the value of the threshScore property.
     * 
     */
    public void setThreshScore(double value) {
        threshScore = value;
    }

    /**
     * Gets the value of the trafoIndex property.
     * 
     */
    public int getTrafoIndex() {
        return this.trafoIndex;
    }

    /**
     * Sets the value of the trafoIndex property.
     * 
     */
    public void setTrafoIndex(int value) {
        trafoIndex = value;
    }

    /**
     * Gets the value of the vectIndex property.
     * 
     */
    public int getVectIndex() {
        return this.vectIndex;
    }

    /**
     * Sets the value of the vectIndex property.
     * 
     */
    public void setVectIndex(int value) {
        vectIndex = value;
    }

    /**
     * Gets the value of the compIndex property.
     * 
     */
    public int getCompIndex() {
        return this.compIndex;
    }

    /**
     * Sets the value of the compIndex property.
     * 
     */
    public void setCompIndex(int value) {
        compIndex = value;
    }

    /**
     * Gets the value of the binWidth property.
     * 
     */
    public double getBinWidth() {
        return this.binWidth;
    }

    /**
     * Sets the value of the binWidth property.
     * 
     */
    public void setBinWidth(double value) {
        binWidth = value;
    }

    /**
     * Gets the value of the binShift property.
     * 
     */
    public double getBinShift() {
        return this.binShift;
    }

    /**
     * Sets the value of the binShift property.
     * 
     */
    public void setBinShift(double value) {
        binShift = value;
    }

    /**
     * Gets the value of the profileIndex property.
     * 
     */
    public int getProfileIndex() {
        return this.profileIndex;
    }

    /**
     * Sets the value of the profileIndex property.
     * 
     */
    public void setProfileIndex(int value) {
        profileIndex = value;
    }

    /**
     * Gets the value of the baseWidth property.
     * 
     */
    public double getBaseWidth() {
        return this.baseWidth;
    }

    /**
     * Sets the value of the baseWidth property.
     * 
     */
    public void setBaseWidth(double value) {
        baseWidth = value;
    }

    /**
     * Gets the value of the xCorrOffset property.
     * 
     */
    public int getXCorrOffset() {
        return this.xCorrOffset;
    }

    /**
     * Sets the value of the xCorrOffset property.
     * 
     */
    public void setXCorrOffset(int value) {
        xCorrOffset = value;
    }

}
