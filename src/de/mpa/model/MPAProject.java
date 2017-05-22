package de.mpa.model;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.client.Client;
import de.mpa.client.ui.projectpanel.dialogs.GeneralDialog;
import de.mpa.db.mysql.ProjectManager;
import de.mpa.db.mysql.accessor.ExperimentTableAccessor;
import de.mpa.db.mysql.accessor.ProjectTableAccessor;
import de.mpa.db.mysql.accessor.Property;

public class MPAProject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	private Map<String, String> properties;
	
	/**
	 * The project's list of child experiments.
	 */
	private final ArrayList<MPAExperiment> experiments;
	
	/**
	 * Constructor for Project from an SQL-ID
	 * 
	 * @param projectID
	 * @throws SQLException 
	 */
	public MPAProject(Long projectID) throws SQLException {
		this.id = projectID;
		this.experiments = new ArrayList<MPAExperiment>();
		this.properties = new HashMap<String, String>();
		List<ExperimentTableAccessor> expAccList = ExperimentTableAccessor.findAllExperimentsOfProject(projectID, Client.getInstance().getConnection());
		for (ExperimentTableAccessor expAcc : expAccList) {
			MPAExperiment experiment = new MPAExperiment(expAcc, this);
			experiment.setProject(this);
			this.experiments.add(experiment);
		}
		List<Property> projProps = Property.findAllPropertiesOfProject(id, Client.getInstance().getConnection());
		for (Property prop : projProps) {
			properties.put(prop.getName(), prop.getValue());
		}
	}
	
	/**
	 * Constructor for Project from an SQL-ID
	 * 
	 * @param projectID
	 * @throws SQLException 
	 */
	public MPAProject(ProjectTableAccessor projectAcc) throws SQLException {
		this.id = projectAcc.getProjectid();
		this.title = projectAcc.getTitle();
		this.creationDate = projectAcc.getCreationdate();
		this.experiments = new ArrayList<MPAExperiment>();
		this.properties = new HashMap<String, String>();
		List<ExperimentTableAccessor> expAccList = ExperimentTableAccessor.findAllExperimentsOfProject(id, Client.getInstance().getConnection());
		for (ExperimentTableAccessor expAcc : expAccList) {
			MPAExperiment experiment = new MPAExperiment(expAcc, this);
			experiment.setProject(this);
			this.experiments.add(experiment);
		}
		List<Property> projProps = Property.findAllPropertiesOfProject(id, Client.getInstance().getConnection());
		for (Property prop : projProps) {
			properties.put(prop.getName(), prop.getValue());
		}
	}
	
	/**
	 * Constructor used when creating a new Project through the UI
	 * 
	 * @param title
	 */
	public MPAProject(String title) {
		this.experiments = new ArrayList<MPAExperiment>();
		this.properties = new HashMap<String, String>();
	}

	public List<MPAExperiment> getExperiments() {
		return experiments;
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

	public void persist(String contentName, Map<String, String> projProp) throws SQLException {
		ProjectManager pm = ProjectManager.getInstance();
		ProjectTableAccessor projAcc = pm.createNewProject(contentName);
		pm.addProjectProperties(projAcc.getProjectid(), projProp);
	}

	
	public void update(String title, Map<String, String> properties, List<GeneralDialog.Operation> params) throws SQLException {
		this.setTitle(title);
		this.getProperties().clear();
		this.getProperties().putAll(properties);
		ProjectManager pm = ProjectManager.getInstance();
		// modify the project name
		pm.modifyProjectName(this.getID(), title);
		// modify the project properties
		pm.modifyProjectProperties(getID(), properties, params);
	}

	public void delete() throws SQLException {
		ProjectManager pm = ProjectManager.getInstance();
		pm.deleteProject(this.getID());
	}

	public void addExperiment(MPAExperiment mpaExperiment) {
		this.experiments.add(mpaExperiment);
	}
	
}
