package de.mpa.client.model.dbsearch;

import java.io.Serializable;

/**
 * An Uniref entry
 * @author R. Heyer
 *
 */
public class UniRefEntryMPA implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The uniRef100.
	 */
	private String uniRef100;
	
	/**
	 * The uniRef90.
	 */
	private String uniRef90;
	
	/**
	 * The uniRef50.
	 */
	private String uniRef50;

	/**
	 * Default constructur
	 */
	public UniRefEntryMPA() {
	}
	
	/**
	 * Default constructur
	 */
	public UniRefEntryMPA(String uniRef100, String uniRef90, String uniRef50) {
		this.uniRef100 = uniRef100;
		this.uniRef90 = uniRef90;
		this.uniRef50 = uniRef50;
	}

	/**
	 * Gets the Uniref100
	 * @return. UniRef100. Gets the Uniref100 as string
	 */
	public String getUniRef100() {
		return uniRef100;
	}

	/**
	 * Sets the UniRef100.
	 * @param uniRef100. The UniRef100 as string
	 */
	public void setUniRef100(String uniRef100) {
		this.uniRef100 = uniRef100;
	}

	/**
	 * Gets the Uniref90
	 * @return. UniRef90. Gets the Uniref90 as string
	 */
	public String getUniRef90() {
		return uniRef90;
	}
	
	/**
	 * Sets the UniRef90.
	 * @param uniRef90. The UniRef90 as string
	 */
	public void setUniRef90(String uniRef90) {
		this.uniRef90 = uniRef90;
	}

	/**
	 * Gets the Uniref50
	 * @return. UniRef50. Gets the Uniref50 as string
	 */
	public String getUniRef50() {
		return uniRef50;
	}

	/**
	 * Sets the UniRef50.
	 * @param uniRef50. The UniRef50 as string
	 */
	public void setUniRef50(String uniRef50) {
		this.uniRef50 = uniRef50;
	}
}