package de.mpa.model;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.client.Client;
import de.mpa.client.ui.projectpanel.dialogs.GeneralDialog.Operation;
import de.mpa.db.mysql.ProjectManager;
import de.mpa.db.mysql.accessor.ExpProperty;
import de.mpa.db.mysql.accessor.ExperimentTableAccessor;
import de.mpa.db.mysql.accessor.Searchspectrum;

public class MPAExperiment implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	private Map<String, String> properties;
	
	/**
	 * The parent project.
	 */
	private MPAProject project;
	
	/**
	 * Constructor for Experiment from an SQL-ID
	 * 
	 * @param expID
	 * @throws SQLException 
	 */
	public MPAExperiment(Long expID, MPAProject project) throws SQLException {
		this(ExperimentTableAccessor.findExperimentByID(expID, Client.getInstance().getConnection()), project);
	}
	
	/**
	 * Constructor for Experiment from an SQL-TableAccessor-Object
	 * 
	 * @param expID
	 * @throws SQLException 
	 */
	public MPAExperiment(ExperimentTableAccessor experimentTableAccessor, MPAProject project) throws SQLException {
		this.id = experimentTableAccessor.getExperimentid();
		this.title = experimentTableAccessor.getTitle();
		this.creationDate = experimentTableAccessor.getCreationdate();
		this.project = project;
		this.properties = new HashMap<String, String>();
		List<ExpProperty> props = ExpProperty.findAllPropertiesOfExperiment(this.id, Client.getInstance().getConnection());
		for (ExpProperty prop : props) {
			this.properties.put(prop.getName(), prop.getValue());
		}
	}
	
	/**
	 * Constructor used when creating a new Experiment through the UI
	 * 
	 * @param title
	 */
	public MPAExperiment(String title, MPAProject project) {
		this.title = title;
		this.project = project;
		this.properties = new HashMap<String, String>();
	}

	public MPAProject getProject() {
		return project;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getID() {
		return id;
	}

	public void persist(String contentName, Map<String, String> expProperties, MPAProject mpaProject) throws SQLException {
		ProjectManager pm = ProjectManager.getInstance();
		ExperimentTableAccessor expAcc = pm.createNewExperiment(mpaProject.getID(), contentName);
		pm.addExperimentProperties(expAcc.getExperimentid(), expProperties);
		this.id = expAcc.getExperimentid();
		this.title = expAcc.getTitle();
		this.properties = expProperties;
		this.project = mpaProject;
		this.project.addExperiment(this);
	}

	public void update(String contentName, Map<String, String> properties2, List<Operation> operations) throws SQLException {
		this.setTitle(title);
		this.getProperties().clear();
		this.getProperties().putAll(properties);
		ProjectManager manager = ProjectManager.getInstance();
		// modify the experiment name
		manager.modifyExperimentName(this.getID(), contentName);
		// modify the experiment properties
		manager.modifyExperimentProperties(this.getID(), properties2, operations);
		System.out.println("Updated");
	}

	public boolean hasSearchResult() throws SQLException {
		boolean return_value = Searchspectrum.hasSearchSpectra(this.getID(), Client.getInstance().getConnection());
		return return_value;
	}

	public void setProject(MPAProject selectedProject) {
		this.project = selectedProject;
	}

	public void delete() throws SQLException {
		ProjectManager.getInstance().deleteExperiment(this.getID());
	}
	
}
