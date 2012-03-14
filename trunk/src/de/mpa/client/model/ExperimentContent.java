package de.mpa.client.model;

import java.util.List;

import de.mpa.db.accessor.ExpProperty;

/**
 * Helper class for the content of an experiment.
 * @author R. Heyer, T. Muth
 *
 */
public class ExperimentContent {
	/**
	 * The project id.
	 */
	private Long projectid;
	
	/**
	 * The experiment id.
	 */
	private Long experimentid;
	
	/**
	 * The experiment title.
	 */
	private String experimentTitle;
	
	/**
	 * The experiment properties.
	 */
	private List<ExpProperty> experimentProperties;
	
	/**
	 * Constructor for the content of an experiment.
	 * @param projectid The project id.
	 * @param experimentid The experiment id.
	 * @param experimentProperties The experiment properties.
	 */
	public ExperimentContent(Long projectid, Long experimentid,	List<ExpProperty> experimentProperties) {
		this.projectid = projectid;
		this.experimentid = experimentid;
		this.experimentProperties = experimentProperties;
	}

	/**
	 * Returns the experiment title.
	 * @return The experiment title.
	 */
	public String getExperimentTitle() {
		return experimentTitle;
	}

	/**
	 * Sets the experiment title.
	 * @param experimentTitle The experiment title.
	 */
	public void setExperimentTitle(String experimentTitle) {
		this.experimentTitle = experimentTitle;
	}
	
	/**
	 * Returns the experiment id.
	 * @return The experiment id.
	 */
	public Long getExperimentid() {
		return experimentid;
	}

	/**
	 * Returns the experiment properties.
	 * @return The experiment properties.
	 */
	public List<ExpProperty> getExperimentProperties() {
		return experimentProperties;
	}
	
	/**
	 * Returns the project id.
	 * @return
	 */
	public Long getProjectid() {
		return projectid;
	}
	
	
}
