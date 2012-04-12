
package de.mpa.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import de.mpa.interfaces.SpectrumComparator;


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
 *         &lt;element name="specComp" type="{http://webservice.mpa.de/}spectrumComparator" minOccurs="0"/>
 *         &lt;element name="threshScore" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
    "specComp",
    "threshScore"
})
public class SpecSimSettings {

    protected double tolMz;
    protected boolean annotatedOnly;
    protected long experimentID;
    protected int pickCount;
    protected SpectrumComparator specComp;
    protected double threshScore;
    
    
    /**
     * Constructor takes all necessary parameters.
     * @param tolMz The precursor m/z tolerance. 
     * @param annotatedOnly Condition flag for using annotated spectra only.
     * @param experimentID The experiment ID.
     * @param pickCount The maximum number of peaks with the highest intensity.
     * @param specComp The spectrum comparator object.
     * @param threshScore The similarity threshold.
     */
    public SpecSimSettings(double tolMz, boolean annotatedOnly,	long experimentID, int pickCount, SpectrumComparator specComp, double threshScore) {
		this.tolMz = tolMz;
		this.annotatedOnly = annotatedOnly;
		this.experimentID = experimentID;
		this.pickCount = pickCount;
		this.specComp = specComp;
		this.threshScore = threshScore;
	}


	public SpecSimSettings() {
		// TODO Auto-generated constructor stub
	}

	/**
     * Gets the value of the tolMz property.
     * @return The precursor m/z tolerance.
     */
    public double getTolMz() {
        return tolMz;
    }

    /**
     * Sets the value of the tolMz property.
     */
    public void setTolMz(double value) {
        this.tolMz = value;
    }

    /**
     * Gets the value of the annotatedOnly property.
     * @return The value of the annotatedOnly property.
     */
    public boolean isAnnotatedOnly() {
        return annotatedOnly;
    }

    /**
     * Sets the value of the annotatedOnly property.
     * 
     */
    public void setAnnotatedOnly(boolean value) {
        this.annotatedOnly = value;
    }

    /**
     * Gets the value of the experimentID property.
     * @return The experiment ID.
     */
    public long getExperimentID() {
        return experimentID;
    }

    /**
     * Sets the value of the experimentID property.
     * 
     */
    public void setExperimentID(long value) {
        this.experimentID = value;
    }

    /**
     * Gets the value of the pickCount property.
     * @return The pick count.
     */
    public int getPickCount() {
        return pickCount;
    }

    /**
     * Sets the value of the pickCount property.
     * 
     */
    public void setPickCount(int value) {
        this.pickCount = value;
    }

    /**
     * Gets the value of the specComp property.
     * 
     * @return
     *     possible object is
     *     {@link SpectrumComparator }
     *     
     */
    public SpectrumComparator getSpecComp() {
        return specComp;
    }

    /**
     * Sets the value of the specComp property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpectrumComparator }
     *     
     */
    public void setSpecComp(SpectrumComparator value) {
        this.specComp = value;
    }

    /**
     * Gets the value of the threshScore property.
     * 
     */
    public double getThreshScore() {
        return threshScore;
    }

    /**
     * Sets the value of the threshScore property.
     * 
     */
    public void setThreshScore(double value) {
        this.threshScore = value;
    }

}
