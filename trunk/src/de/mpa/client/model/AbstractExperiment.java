package de.mpa.client.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import de.mpa.client.model.dbsearch.DbSearchResult;

/**
 * Abstract implementation of the experiment interface.
 * @author A. Behne
 */
public abstract class AbstractExperiment implements ProjectExperiment {
	
	/**
	 * The experiment ID.
	 */
	protected Long id;

	/**
	 * The experiment title.
	 */
	protected String title;

	/**
	 * The experiment's creation date.
	 */
	protected Date creationDate;
	
	/**
	 * The experiment properties.
	 */
	protected Map<String, String> properties;
	
	/**
	 * The parent project.
	 */
	protected AbstractProject project;
	
	/**
	 * Creates an experiment using the specified title, id, creation date and
	 * parent project.
	 * @param id the experiment ID
	 * @param title the experiment title
	 * @param creationDate the experiment's creation date
	 * @param project the parent project
	 */
	public AbstractExperiment(Long id, String title, Date creationDate, AbstractProject project) {
		this(id, title, creationDate, new LinkedHashMap<String, String>(), project);
	}
	
	/**
	 * Creates an experiment using the specified title, id, creation date,
	 * property map and parent project.
	 * @param id the experiment ID
	 * @param title the experiment title
	 * @param creationDate the experiment's creation date
	 * @param properties the experiment properties
	 * @param project the parent project
	 */
	public AbstractExperiment(Long id, String title, Date creationDate, Map<String, String> properties, AbstractProject project) {
		this.id = id;
		this.title = title;
		this.creationDate = creationDate;
		this.properties = properties;
		this.project = project;
	}
	
	/**
	 * Adds a property to the map of experiment properties.
	 * @param name the property name
	 * @param value the property value
	 */
	public void addProperty(String name, String value) {
		properties.put(name, value);
	}

	@Override
	public Long getID() {
		return id;
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the experiment title.
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public Map<String, String> getProperties() {
		return properties;
	}
	
	/**
	 * Returns the parent project.
	 * @return the parent project
	 */
	public AbstractProject getProject() {
		return project;
	}
	
	/**
	 * Sets the parent project.
	 * @param project the project to set
	 */
	public void setProject(AbstractProject project) {
		this.project = project;
	}
	
	/**
	 * Returns whether the experiment features displayable results.
	 * @return <code>true</code> if results exist, <code>false</code> otherwise
	 */
	public abstract boolean hasSearchResult();
	
	/**
	 * Returns the search result object.
	 * @return the search result object
	 */
	public abstract DbSearchResult getSearchResult();

	/**
	 * Clears the search result object.
	 */
	public abstract void clearSearchResult();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProjectExperiment) {
			ProjectExperiment that = (ProjectExperiment) obj;
			if (this.getClass().equals(that.getClass())) {
				if ((this.getID() != null) && (that.getID() != null)) {
					return this.getID().equals(that.getID());
				} else {
					return this.getCreationDate().equals(that.getCreationDate());
				}
			}
		}
		return false;
	}

}
