package de.mpa.parser.ec;
/**
 * This class represents a EC-entry
 * @author R. Heyer
 *
 */
public class ECEntry {
	private String number;	
	private String name;	
	private String description;
	
	// Getters and Setters
	/**
	 * This method returns the EC -number
	 * @return number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * This method sets the EC-number
	 * @param number
	 */
	public void setNumber(String eCNumber) {
		this.number = eCNumber;
	}

	/**
	 * This method returns the name of the ECEntry.
	 * @return name The name of the ECEntry.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method sets the ECName
	 * @param name Name of the ECEntry
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method gets the EC-description
	 * @return EC-description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * This method sets the EC-description
	 * @param description EC-description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
