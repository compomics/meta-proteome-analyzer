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
	 * The tooltip string of the parameter.
	 */
	private String tooltip;
	
	/**
	 * Constructs a configuration parameter object instance from the specified variables.
	 * 
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @param section the section identifier of the parameter
	 * @param tooltip the tooltip string of the parameter
	 */
	public Parameter(String name, Object value, String section, String tooltip) {
		this.section = section;
		this.value = value;
		this.name = name;
		this.tooltip = tooltip;
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
	 * Returns the section identifier of the parameter.
	 * @return the section identifier of the parameter
	 */
	public String getSection() {
		return section;
	}

	/**
	 * Returns the tooltip string of the parameter.
	 * @return the tooltip string of the parameter
	 */
	public String getTooltip() {
		return tooltip;
	}

}
