package de.mpa.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This helper class contains all the features to setup a project from the user interface.
 * @author R. Heyer, T. Muth, A. Behne
 */
public abstract class AbstractProject implements ProjectExperiment {
	
	/**
	 * The project ID.
	 */
	private Long id;
	
	/**
	 * The project title.
	 */
	private String title;
	
	/**
	 * The project's creation date.
	 */
	private Date creationDate;
	
	/**
	 * The experiment properties.
	 */
	private final Map<String, String> properties;
	
	/**
	 * The project's list of child experiments.
	 */
	private final List<AbstractExperiment> experiments;
	
	/**
	 * Creates a project from the specified ID, title, creation date, properties
	 * and child experiments.
	 * @param id the project ID
	 * @param title the project title
	 * @param creationDate the project's creation date
	 * @param properties the project properties
	 * @param experiments the child experiments
	 */
	public AbstractProject(Long id, String title, Date creationDate,
			Map<String, String> properties, List<AbstractExperiment> experiments) {
		this.id = id;
		this.title = title;
		this.creationDate = creationDate;
		this.properties = (properties != null) ? properties : new LinkedHashMap<String, String>();
		this.experiments = (experiments != null) ? experiments : new ArrayList<AbstractExperiment>();
	}
	
	/**
	 * Returns the project ID.
	 * @return the project ID
	 */
	@Override
	public Long getID() {
		return this.id;
	}
	
	/**
	 * Sets the project ID.
	 * @param id the ID to set
	 */
	public void setID(Long id) {
		this.id = id;
	}
	
	/**
	 * Returns the project properties.
	 * @return the project properties
	 */
	@Override
	public Map<String, String> getProperties() {
		return this.properties;
	}
	
	/**
	 * Returns the list of child experiments of this project.
	 * @return the experiments
	 */
	public List<AbstractExperiment> getExperiments() {
		return this.experiments;
	}
	
	/** 
	 * Returns the project title.
	 * @return the project title
	 */
	@Override
	public String getTitle() {
		return this.title;
	}

	/** 
	 * Sets the project title.
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the project's creation date.
	 * @return the creation date
	 */
	@Override
	public Date getCreationDate() {
		return this.creationDate;
	}
	
	/**
	 * Sets the project's creation date.
	 * @param creationDate the date to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractProject) {
			AbstractProject that = (AbstractProject) obj;
			if ((getID() != null) && (that.getID() != null)) {
				return getID().equals(that.getID());
			} else {
				return getCreationDate().equals(that.getCreationDate());
			}
		}
		return false;
	}

}
