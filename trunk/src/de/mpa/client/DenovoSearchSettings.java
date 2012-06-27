
package de.mpa.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for denovoSearchSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="denovoSearchSettings">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="enzyme" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fragMassTol" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="precursorTol" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="model" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numSolutions" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="mods" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="experimentid" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "denovoSearchSettings", propOrder = {
    "enzyme",
    "fragMassTol",
    "precursorTol",
    "model",
    "numSolutions",
    "mods",
    "experimentid"
})
public class DenovoSearchSettings {

    protected String enzyme;
    protected double fragMassTol;
    protected double precursorTol;
    protected String model;
    protected int numSolutions;
    protected String mods;
    protected long experimentid;

    /**
     * Gets the value of the enzyme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnzyme() {
        return enzyme;
    }

    /**
     * Sets the value of the enzyme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnzyme(String value) {
        this.enzyme = value;
    }

    /**
     * Gets the value of the fragMassTol property.
     * 
     */
    public double getFragMassTol() {
        return fragMassTol;
    }

    /**
     * Sets the value of the fragMassTol property.
     * 
     */
    public void setFragMassTol(double value) {
        this.fragMassTol = value;
    }

    /**
     * Gets the value of the precursorTol property.
     * 
     */
    public double getPrecursorTol() {
        return precursorTol;
    }

    /**
     * Sets the value of the precursorTol property.
     * 
     */
    public void setPrecursorTol(double value) {
        this.precursorTol = value;
    }

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModel(String value) {
        this.model = value;
    }

    /**
     * Gets the value of the numSolutions property.
     * 
     */
    public int getNumSolutions() {
        return numSolutions;
    }

    /**
     * Sets the value of the numSolutions property.
     * 
     */
    public void setNumSolutions(int value) {
        this.numSolutions = value;
    }

    /**
     * Gets the value of the mods property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMods() {
        return mods;
    }

    /**
     * Sets the value of the mods property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMods(String value) {
        this.mods = value;
    }

    /**
     * Gets the value of the experimentid property.
     * 
     */
    public long getExperimentid() {
        return experimentid;
    }

    /**
     * Sets the value of the experimentid property.
     * 
     */
    public void setExperimentid(long value) {
        this.experimentid = value;
    }

}
