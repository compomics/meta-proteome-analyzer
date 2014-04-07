package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
	 * Returns a list of the stored parameters.
	 * @return a list of parameters
	 */
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>(values());
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
