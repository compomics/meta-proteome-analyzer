package de.mpa.client.settings;

public class Parameter {
	
	/**
	 * Type of the parameter, e.g. Boolean.class, String.class
	 */
	private Class type;
	
	/**
	 * Section for the parameter.
	 */
	private String section;
	
	/**
	 * Parameter value.
	 */
	private Object value;
	
	/**
	 * Name of the parameter.
	 */
	private String name;
	
	/**
	 * Description of the parameter.
	 */
	private String description;
	
	public Parameter(String name, Object value, Class type, String section, String description) {
		this.section = section;
		this.value = value;
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public String getSection() {
		return section;
	}

	public Object getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public Class getType() {
		return type;
	}
	
	

}
