
package de.mpa.client.settings;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.mpa.client.SpecSimSettings;


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
 *         &lt;element name="expID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="sss" type="{http://webservice.mpa.de/}specSimSettings" minOccurs="0"/>
 *         &lt;element name="filenames" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
    "expID",
    "sss",
    "filenames"
})
public class SearchSettings {

    protected DbSearchSettings dbss;
    protected long expID;
    protected SpecSimSettings sss;
    @XmlElement(nillable = true)
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
    public SearchSettings(DbSearchSettings dbss, SpecSimSettings sss, long expID) {
            this.dbss = dbss;
            this.sss = sss;
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
        return this.dbss;
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
        dbss = value;
    }

    /**
     * Gets the value of the expID property.
     * 
     */
    public long getExpID() {
        return this.expID;
    }

    /**
     * Sets the value of the expID property.
     * 
     */
    public void setExpID(long value) {
        expID = value;
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
        return this.sss;
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
        sss = value;
    }
    
    /**
     * Returns whether database search shall be performed.
     * @return
     */
    public boolean isDatabase() {
            return (this.dbss != null);
    }

    /**
     * Returns whether spectral similarity search shall be performed.
     * @return
     */
    public boolean isSpecSim() {
            return (this.sss != null);
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
        if (this.filenames == null) {
            this.filenames = new ArrayList<String>();
        }
        return filenames;
    }

    /**
     * Sets the value of the expID property.
     * 
     */
	public void setFilenames(List<String> filenames) {
		this.filenames = filenames;
	}

    
    
    
}
