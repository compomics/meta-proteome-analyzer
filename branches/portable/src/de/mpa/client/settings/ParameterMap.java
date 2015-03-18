package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Interface defining parameter storage capabilities.
 * 
 * @author T. Muth, F. Kohrs
 */
public abstract class ParameterMap extends LinkedHashMap<String, Parameter> {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public ParameterMap() {
		this.initDefaults();
	}
	
	/**
	 * Sets the value of the parameter associated with the specified key.
	 * @param key the parameter key
	 * @param value the value to set
	 */
	public void setValue(String key, Object value) {
		Parameter parameter = this.get(key);
		if (parameter != null) {
			parameter.setValue(value);
		}
	}
	
	/**
	 * Initializes default values for the parameters.
	 */
	public abstract void initDefaults();
	
	
	/**
	 * Consolidates suitable parameters into a parameter file required by certain processes.
	 * @return a file containing a representation of all suitable parameters
	 */
	public abstract File toFile(String path) throws IOException;
	
}
