package de.mpa.client.model;

import java.util.Date;
import java.util.Map;

/**
 * Helper interface for the content of a project or an experiment.
 * @author R. Heyer, T. Muth, A. Behne
 */
public interface ProjectExperiment {
	
	/**
	 * Returns the experiment ID.
	 * @return the experiment ID
	 */
	public Long getID();
	
	/**
	 * Returns the experiment title.
	 * @return the experiment title
	 */
	public String getTitle();

	/**
	 * Returns the experiment's creation date.
	 * @return the creation date
	 */
	public Date getCreationDate();

	/**
	 * Returns the experiment properties.
	 * @return the experiment properties
	 */
	public Map<String, String> getProperties();
	
	/**
	 * Creates a new content object using the specified title, properties and optional additional parameters.
	 * @param title the content object title
	 * @param properties the content properties
	 * @param params optional additional parameters
	 */
	public abstract void persist(String title, Map<String, String> properties, Object... params);
	
	/**
	 * Modifies the content object using the specified title, properties and optional additional parameters.
	 * @param title the new title
	 * @param properties the new properties
	 * @param params optional additional parameters
	 */
	public abstract void update(String title, Map<String, String> properties, Object... params);
	
	/**
	 * Deletes the content object from persistent storage.
	 */
	public abstract void delete();
	
}
