
package de.mpa.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dbSearchSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dbSearchSettings">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="crux" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="enzyme" type="{http://webservice.mpa.de/}protease" minOccurs="0"/>
 *         &lt;element name="fastaFile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fragmentIonTol" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="inspect" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="numMissedCleavages" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="omssa" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="precursorIonTol" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="XTandem" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dbSearchSettings", propOrder = {
    "crux",
    "enzyme",
    "fastaFile",
    "fragmentIonTol",
    "inspect",
    "numMissedCleavages",
    "omssa",
    "precursorIonTol",
    "xTandem"
})
public class DbSearchSettings {

    protected boolean crux;
    protected Protease enzyme;
    protected String fastaFile;
    protected double fragmentIonTol;
    protected boolean inspect;
    protected int numMissedCleavages;
    protected boolean omssa;
    protected double precursorIonTol;
    @XmlElement(name = "XTandem")
    protected boolean xTandem;

    /**
     * Gets the value of the crux property.
     * 
     */
    public boolean isCrux() {
        return crux;
    }

    /**
     * Sets the value of the crux property.
     * 
     */
    public void setCrux(boolean value) {
        this.crux = value;
    }

    /**
     * Gets the value of the enzyme property.
     * 
     * @return
     *     possible object is
     *     {@link Protease }
     *     
     */
    public Protease getEnzyme() {
        return enzyme;
    }

    /**
     * Sets the value of the enzyme property.
     * 
     * @param value
     *     allowed object is
     *     {@link Protease }
     *     
     */
    public void setEnzyme(Protease value) {
        this.enzyme = value;
    }

    /**
     * Gets the value of the fastaFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFastaFile() {
        return fastaFile;
    }

    /**
     * Sets the value of the fastaFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFastaFile(String value) {
        this.fastaFile = value;
    }

    /**
     * Gets the value of the fragmentIonTol property.
     * 
     */
    public double getFragmentIonTol() {
        return fragmentIonTol;
    }

    /**
     * Sets the value of the fragmentIonTol property.
     * 
     */
    public void setFragmentIonTol(double value) {
        this.fragmentIonTol = value;
    }

    /**
     * Gets the value of the inspect property.
     * 
     */
    public boolean isInspect() {
        return inspect;
    }

    /**
     * Sets the value of the inspect property.
     * 
     */
    public void setInspect(boolean value) {
        this.inspect = value;
    }

    /**
     * Gets the value of the numMissedCleavages property.
     * 
     */
    public int getNumMissedCleavages() {
        return numMissedCleavages;
    }

    /**
     * Sets the value of the numMissedCleavages property.
     * 
     */
    public void setNumMissedCleavages(int value) {
        this.numMissedCleavages = value;
    }

    /**
     * Gets the value of the omssa property.
     * 
     */
    public boolean isOmssa() {
        return omssa;
    }

    /**
     * Sets the value of the omssa property.
     * 
     */
    public void setOmssa(boolean value) {
        this.omssa = value;
    }

    /**
     * Gets the value of the precursorIonTol property.
     * 
     */
    public double getPrecursorIonTol() {
        return precursorIonTol;
    }

    /**
     * Sets the value of the precursorIonTol property.
     * 
     */
    public void setPrecursorIonTol(double value) {
        this.precursorIonTol = value;
    }

    /**
     * Gets the value of the xTandem property.
     * 
     */
    public boolean isXTandem() {
        return xTandem;
    }

    /**
     * Sets the value of the xTandem property.
     * 
     */
    public void setXTandem(boolean value) {
        this.xTandem = value;
    }

}
