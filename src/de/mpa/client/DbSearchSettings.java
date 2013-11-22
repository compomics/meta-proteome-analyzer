
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
        return crux;
    }

    /**
     * Sets the value of the crux property.
     * 
     */
    public void setCrux(boolean crux) {
        this.crux = crux;
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
        return cruxParams;
    }

    /**
     * Sets the value of the cruxParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCruxParams(String cruxParams) {
        this.cruxParams = cruxParams;
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
    public void setFastaFile(String fastaFile) {
        this.fastaFile = fastaFile;
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
    public void setFragmentIonTol(double fragmentIonTol) {
        this.fragmentIonTol = fragmentIonTol;
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
    public void setInspect(boolean inspect) {
        this.inspect = inspect;
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
        return inspectParams;
    }

    /**
     * Sets the value of the inspectParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInspectParams(String inspectParams) {
        this.inspectParams = inspectParams;
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
    public void setNumMissedCleavages(int numMissedCleavages) {
        this.numMissedCleavages = numMissedCleavages;
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
    public void setOmssa(boolean omssa) {
        this.omssa = omssa;
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
        return omssaParams;
    }

    /**
     * Sets the value of the omssaParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOmssaParams(String omssaParams) {
        this.omssaParams = omssaParams;
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
    public void setPrecursorIonTol(double precursorIonTol) {
        this.precursorIonTol = precursorIonTol;
    }

    /**
     * Gets the value of the precursorIonUnit property.
     * 
     */
    public boolean isPrecursorIonUnitPpm() {
        return precursorIonUnit;
    }

    /**
     * Sets the value of the precursorIonUnit property.
     * 
     */
    public void setPrecursorIonUnitPpm(boolean precursorIonUnit) {
        this.precursorIonUnit = precursorIonUnit;
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
    public void setXTandem(boolean xTandem) {
        this.xTandem = xTandem;
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
        return xtandemParams;
    }

    /**
     * Sets the value of the xtandemParams property.
     * 
     * @param xtandemParams
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXtandemParams(String xtandemParams) {
        this.xtandemParams = xtandemParams;
    }

    /**
     * Gets the value of the decoy property.
     * 
     */
    public boolean isDecoy() {
        return decoy;
    }

    /**
     * Sets the value of the decoy property.
     * 
     */
    public void setDecoy(boolean decoy) {
        this.decoy = decoy;
    }

    /**
     * Gets the value of the mascot property.
     * 
     */
    public boolean isMascot() {
        return mascot;
    }

    /**
     * Sets the value of the mascot property.
     * 
     */
    public void setMascot(boolean mascot) {
        this.mascot = mascot;
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
    public void setExperimentid(long experimentid) {
        this.experimentid = experimentid;
    }

}
