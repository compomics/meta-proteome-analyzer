package de.mpa.client.settings;

import java.util.List;

/**
 * Interface defining parameter storage capabilities.
 * 
 * @author T. Muth, F. Kohrs
 */
public interface ParameterSet {
	
	/**
	 * Returns a list of the stored parameters.
	 * @return a list of parameters
	 */
	public List<Parameter> getParameters();
	
	/**
	 * Initializes default values for the parameters.
	 */
	public void setDefaults();
}
