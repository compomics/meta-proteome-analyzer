
package de.mpa.client;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Java class for searchSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchSettings">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dbss" type="{http://webservice.mpa.de/}dbSearchSettings" minOccurs="0"/>
 *         &lt;element name="dnss" type="{http://webservice.mpa.de/}denovoSearchSettings" minOccurs="0"/>
 *         &lt;element name="expID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sss" type="{http://webservice.mpa.de/}specSimSettings" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchSettings", propOrder = {
    "dbss",
    "dnss",
    "expID",
    "sss",
    "filenames"
})
public class SearchSettings {

    protected DbSearchSettings dbss;
    protected DenovoSearchSettings dnss;
    protected long expID;
    protected SpecSimSettings sss;
    protected List<String> filenames;
    
    /**
	 * Class constructor to shut up web service!
	 */
	public SearchSettings() {}
	
	/**
	 * Class constructor.
	 * @param dbss
	 * @param sss
	 * @param dnss
	 */
	public SearchSettings(DbSearchSettings dbss, SpecSimSettings sss, DenovoSearchSettings dnss, long expID) {
		this.dbss = dbss;
		this.sss = sss;
		this.dnss = dnss;
		this.expID = expID;
	}
	
    /**
     * Gets the value of the dbss property.
     * 
     * @return
     *     possible object is
     *     {@link DbSearchSettings }
     *     
     */
    public DbSearchSettings getDbss() {
        return dbss;
    }

    /**
     * Sets the value of the dbss property.
     * 
     * @param value
     *     allowed object is
     *     {@link DbSearchSettings }
     *     
     */
    public void setDbss(DbSearchSettings value) {
        this.dbss = value;
    }

    /**
     * Gets the value of the dnss property.
     * 
     * @return
     *     possible object is
     *     {@link DenovoSearchSettings }
     *     
     */
    public DenovoSearchSettings getDnss() {
        return dnss;
    }

    /**
     * Sets the value of the dnss property.
     * 
     * @param value
     *     allowed object is
     *     {@link DenovoSearchSettings }
     *     
     */
    public void setDnss(DenovoSearchSettings value) {
        this.dnss = value;
    }

    /**
     * Gets the value of the expID property.
     * 
     */
    public long getExpID() {
        return expID;
    }

    /**
     * Sets the value of the expID property.
     * 
     */
    public void setExpID(long value) {
        this.expID = value;
    }

    /**
     * Gets the value of the sss property.
     * 
     * @return
     *     possible object is
     *     {@link SpecSimSettings }
     *     
     */
    public SpecSimSettings getSss() {
        return sss;
    }

    /**
     * Sets the value of the sss property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpecSimSettings }
     *     
     */
    public void setSss(SpecSimSettings value) {
        this.sss = value;
    }
    
    /**
	 * Returns whether database search shall be performed.
	 * @return
	 */
	public boolean isDatabase() {
		return (dbss != null); 
	}

	/**
	 * Returns whether spectral similarity search shall be performed.
	 * @return
	 */
	public boolean isSpecSim() {
		return (sss != null); 
	}

	/**
	 * Returns whether de novo search shall be performed.
	 * @return
	 */
	public boolean isDeNovo() {
		return (dnss != null); 
	}

	 /**
     * Gets the value of the filenames property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the filenames property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFilenames().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFilenames() {
        if (filenames == null) {
            filenames = new ArrayList<String>();
        }
        return this.filenames;
    }

}
