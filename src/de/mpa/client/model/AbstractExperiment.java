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
	private Long id;

	/**
	 * The experiment title.
	 */
	private String title;

	/**
	 * The experiment's creation date.
	 */
	private Date creationDate;
	
	/**
	 * The experiment properties.
	 */
	private final Map<String, String> properties;
	
	/**
	 * The parent project.
	 */
	private AbstractProject project;
	
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
        this.properties.put(name, value);
	}

	@Override
	public Long getID() {
		return this.id;
	}
	
	/**
	 * Sets the experiment ID.
	 * @param id the ID to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String getTitle() {
		return this.title;
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
		return this.creationDate;
	}
	
	/**
	 * Sets the creation date.
	 * @param creationDate the date to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public Map<String, String> getProperties() {
		return this.properties;
	}
	
	/**
	 * Returns the parent project.
	 * @return the parent project
	 */
	public AbstractProject getProject() {
		return this.project;
	}
	
	/**
	 * Sets the parent project.
	 * @param project the project to set
	 */
	public void setProject(AbstractProject project) {
		if (project == null) {
			throw new IllegalArgumentException("Project must not be null.");
		}
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
	 * Sets the search result object.
	 * @param result the result object to set
	 */
	public abstract void setSearchResult(DbSearchResult result);

	/**
	 * Clears the search result object.
	 */
	public void clearSearchResult() {
        setSearchResult(null);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProjectExperiment) {
			ProjectExperiment that = (ProjectExperiment) obj;
			if (getClass().equals(that.getClass())) {
				if ((getID() != null) && (that.getID() != null)) {
					return getID().equals(that.getID());
				} else {
					return getCreationDate().equals(that.getCreationDate());
				}
			}
		}
		return false;
	}

}
