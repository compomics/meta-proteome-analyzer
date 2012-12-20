package de.mpa.client.settings;

/**
 * Container class for storing search engine parameter-related variables.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
public class Parameter {
	
	/**
	 * The name of the parameter.
	 */
	private String name;
	
	/**
	 * The value of the parameter.
	 */
	private Object value;

	/**
	 * The section identifier of the parameter.
	 */
	private String section;
	
	/**
	 * The description string of the parameter. Used for tooltips.
	 */
	private String description;
	
	/**
	 * Constructs a configuration parameter object instance from the specified variables.
	 * 
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @param section the section identifier of the parameter
	 * @param description the description string of the parameter
	 */
	public Parameter(String name, Object value, String section, String description) {
		this.section = section;
		this.value = value;
		this.name = name;
		this.description = description;
	}

	/**
	 * Returns the name of the parameter.
	 * @return the name of the parameter
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of the parameter.
	 * @return the value of the parameter
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Sets the value of the parameter.
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Returns the section identifier of the parameter.
	 * @return the section identifier of the parameter
	 */
	public String getSection() {
		return section;
	}

	/**
	 * Returns the description string of the parameter.
	 * @return the description string of the parameter
	 */
	public String getDescription() {
		return description;
	}

}
