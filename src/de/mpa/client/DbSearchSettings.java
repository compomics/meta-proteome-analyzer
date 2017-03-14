
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
 *         &lt;element name="Crux" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="cruxParams" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fastaFile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fragmentIonTol" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="Inspect" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="inspectParams" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numMissedCleavages" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Omssa" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="omssaParams" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="precursorIonTol" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="precursorIonUnit" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="XTandem" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="xtandemParams" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decoy" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Mascot" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "dbSearchSettings", propOrder = {
    "crux",
    "cruxParams",
    "fastaFile",
    "fragmentIonTol",
    "inspect",
    "inspectParams",
    "numMissedCleavages",
    "omssa",
    "omssaParams",
    "precursorIonTol",
    "precursorIonUnit",
    "addProtHitUse",
    "xTandem",
    "xtandemParams",
    "decoy",
    "mascot",
    "experimentid"
})
public class DbSearchSettings {

    @XmlElement(name = "Crux")
    protected boolean crux;
    protected String cruxParams;
    protected String fastaFile;
    protected double fragmentIonTol;
    @XmlElement(name = "Inspect")
    protected boolean inspect;
    protected String inspectParams;
    protected int numMissedCleavages;
    @XmlElement(name = "Omssa")
    protected boolean omssa;
    protected String omssaParams;
    protected double precursorIonTol;
    protected boolean precursorIonUnit;
    protected boolean addProtHitUse;
    @XmlElement(name = "XTandem")
    protected boolean xTandem;
    protected String xtandemParams;
    protected boolean decoy;
    @XmlElement(name = "Mascot")
    protected boolean mascot;
    protected long experimentid;

    /**
     * Gets the value of the crux property.
     * 
     */
    public boolean isCrux() {
        return this.crux;
    }

    /**
     * Sets the value of the crux property.
     * 
     */
    public void setCrux(boolean value) {
        crux = value;
    }

    /**
     * Gets the value of the cruxParams property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCruxParams() {
        return this.cruxParams;
    }

    /**
     * Sets the value of the cruxParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCruxParams(String value) {
        cruxParams = value;
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
        return this.fastaFile;
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
        fastaFile = value;
    }

    /**
     * Gets the value of the fragmentIonTol property.
     * 
     */
    public double getFragmentIonTol() {
        return this.fragmentIonTol;
    }

    /**
     * Sets the value of the fragmentIonTol property.
     * 
     */
    public void setFragmentIonTol(double value) {
        fragmentIonTol = value;
    }

    /**
     * Gets the value of the inspect property.
     * 
     */
    public boolean isInspect() {
        return this.inspect;
    }

    /**
     * Sets the value of the inspect property.
     * 
     */
    public void setInspect(boolean value) {
        inspect = value;
    }

    /**
     * Gets the value of the inspectParams property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInspectParams() {
        return this.inspectParams;
    }

    /**
     * Sets the value of the inspectParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInspectParams(String value) {
        inspectParams = value;
    }

    /**
     * Gets the value of the numMissedCleavages property.
     * 
     */
    public int getNumMissedCleavages() {
        return this.numMissedCleavages;
    }

    /**
     * Sets the value of the numMissedCleavages property.
     * 
     */
    public void setNumMissedCleavages(int value) {
        numMissedCleavages = value;
    }
    
    /**
     * Sets the value for the peptide FASTA flag.
     * 
     */
    public void setpepFASTA(boolean value) {
        this.addProtHitUse = value;
    }

    /**
     * Gets the value for the peptide FASTA flag.
     * 
     */
    public boolean getPepDBFlag() {
    	return this.addProtHitUse;
    }
    
    /**
     * Gets the value of the omssa property.
     * 
     */
    public boolean isOmssa() {
        return this.omssa;
    }

    /**
     * Sets the value of the omssa property.
     * 
     */
    public void setOmssa(boolean value) {
        omssa = value;
    }

    /**
     * Gets the value of the omssaParams property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOmssaParams() {
        return this.omssaParams;
    }

    /**
     * Sets the value of the omssaParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOmssaParams(String value) {
        omssaParams = value;
    }

    /**
     * Gets the value of the precursorIonTol property.
     * 
     */
    public double getPrecursorIonTol() {
        return this.precursorIonTol;
    }

    /**
     * Sets the value of the precursorIonTol property.
     * 
     */
    public void setPrecursorIonTol(double value) {
        precursorIonTol = value;
    }

    /**
     * Gets the value of the precursorIonUnit property.
     * 
     */
    public boolean isPrecursorIonUnitPpm() {
        return this.precursorIonUnit;
    }

    /**
     * Sets the value of the precursorIonUnit property.
     * 
     */
    public void setPrecursorIonUnitPpm(boolean value) {
        precursorIonUnit = value;
    }

    /**
     * Gets the value of the xTandem property.
     * 
     */
    public boolean isXTandem() {
        return this.xTandem;
    }

    /**
     * Sets the value of the xTandem property.
     * 
     */
    public void setXTandem(boolean value) {
        xTandem = value;
    }

    /**
     * Gets the value of the xtandemParams property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXtandemParams() {
        return this.xtandemParams;
    }

    /**
     * Sets the value of the xtandemParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXtandemParams(String value) {
        xtandemParams = value;
    }

    /**
     * Gets the value of the decoy property.
     * 
     */
    public boolean isDecoy() {
        return this.decoy;
    }

    /**
     * Sets the value of the decoy property.
     * 
     */
    public void setDecoy(boolean value) {
        decoy = value;
    }

    /**
     * Gets the value of the mascot property.
     * 
     */
    public boolean isMascot() {
        return this.mascot;
    }

    /**
     * Sets the value of the mascot property.
     * 
     */
    public void setMascot(boolean value) {
        mascot = value;
    }

    /**
     * Gets the value of the experimentid property.
     * 
     */
    public long getExperimentid() {
        return this.experimentid;
    }

    /**
     * Sets the value of the experimentid property.
     * 
     */
    public void setExperimentid(long value) {
        experimentid = value;
    }

    
}
