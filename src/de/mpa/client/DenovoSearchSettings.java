
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
 *         &lt;element name="dnEnzyme" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dnFragmentTolerance" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="dnMS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dnNumSolutions" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dnPTMs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dnPrecursorTolerance" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dnRemoveAllPep" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "dnEnzyme",
    "dnFragmentTolerance",
    "dnMS",
    "dnNumSolutions",
    "dnPTMs",
    "dnPrecursorTolerance",
    "dnRemoveAllPep",
    "experimentid"
})
public class DenovoSearchSettings {

    protected String dnEnzyme;
    protected double dnFragmentTolerance;
    protected String dnMS;
    protected int dnNumSolutions;
    protected String dnPTMs;
    protected double dnPrecursorTolerance;
    protected boolean dnRemoveAllPep;
    protected long experimentid;

    /**
     * Gets the value of the dnEnzyme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDnEnzyme() {
        return dnEnzyme;
    }

    /**
     * Sets the value of the dnEnzyme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDnEnzyme(String value) {
        this.dnEnzyme = value;
    }

    /**
     * Gets the value of the dnFragmentTolerance property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDnFragmentTolerance() {
        return dnFragmentTolerance;
    }

    /**
     * Sets the value of the dnFragmentTolerance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDnFragmentTolerance(Double value) {
        this.dnFragmentTolerance = value;
    }

    /**
     * Gets the value of the dnMS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDnMS() {
        return dnMS;
    }

    /**
     * Sets the value of the dnMS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDnMS(String value) {
        this.dnMS = value;
    }

    /**
     * Gets the value of the dnNumSolutions property.
     * 
     */
    public int getDnNumSolutions() {
        return dnNumSolutions;
    }

    /**
     * Sets the value of the dnNumSolutions property.
     * 
     */
    public void setDnNumSolutions(int value) {
        this.dnNumSolutions = value;
    }

    /**
     * Gets the value of the dnPTMs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDnPTMs() {
        return dnPTMs;
    }

    /**
     * Sets the value of the dnPTMs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDnPTMs(String value) {
        this.dnPTMs = value;
    }

    /**
     * Gets the value of the dnPrecursorTolerance property.
     * 
     */
    public double getDnPrecursorTolerance() {
        return dnPrecursorTolerance;
    }

    /**
     * Sets the value of the dnPrecursorTolerance property.
     * 
     */
    public void setDnPrecursorTolerance(double value) {
        this.dnPrecursorTolerance = value;
    }

    /**
     * Gets the value of the dnRemoveAllPep property.
     * 
     */
    public boolean isDnRemoveAllPep() {
        return dnRemoveAllPep;
    }

    /**
     * Sets the value of the dnRemoveAllPep property.
     * 
     */
    public void setDnRemoveAllPep(boolean value) {
        this.dnRemoveAllPep = value;
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
