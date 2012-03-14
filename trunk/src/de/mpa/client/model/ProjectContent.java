package de.mpa.client.model;

import java.util.List;

import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Property;

/**
 * This helper class contains all the features to setup a project from the user interface.
 * @author R. Heyer, T. Muth
 *
 */
public class ProjectContent {
	/**
	 * The project id.
	 */
	private Long projectid;
	/**
	 * The project Title
	 */
	private String projectTitle;
	
	
	/**
	 * The project properties.
	 */
	private List<Property> projectProperties;
	
	/**
	 * The list of experiments.
	 */
	private List<Experiment> experiments;
	
	/**
	 * Constructor for the Project settings
	 * @param projectid
	 * @param projectProperties
	 */
	public ProjectContent(Long projectid, List<Property> projectProperties, List<Experiment> experiments) {
		this.projectid = projectid;
		this.projectProperties = projectProperties;
		this.experiments = experiments;
	}
	
	/**
	 * Returns the project id.
	 *  @return The project id.
	 */
	public Long getProjectid() {
		return projectid;
	}
	
	/**
	 * Returns the project properties.
	 * @return The project properties.
	 */
	public List<Property> getProjectProperties() {
		return projectProperties;
	}
	
	/**
	 * Returns the experiments.
	 * @return The project experiments.
	 */
	public List<Experiment> getExperiments() {
		return experiments;
	}
	
	/** 
	 * Returns the project title.
	 * @return The project title
	 */
	public String getProjectTitle() {
		return projectTitle;
	}

	/** 
	 * Sets the project title.
	 * @param projectTitle The project title
	 */
	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

}
