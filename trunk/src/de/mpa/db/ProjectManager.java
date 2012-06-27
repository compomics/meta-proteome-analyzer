package de.mpa.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mpa.client.ui.dialogs.Operation;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Project;
import de.mpa.db.accessor.Property;

/**
 * This class handles the project management by accessing project and experiments in the database.
 * @author T.Muth, A.Behne
 *
 */
public class ProjectManager {
	
	/**
	 * Database connection.
	 */
	private Connection conn;
	
	/**
	 * Sets up the project manager with an existing database connection.
	 * @param conn Database connection.
	 */
	public ProjectManager(Connection conn) {
		this.conn = conn;
	}

	/**
	 * This method creates a new project.
	 * @param title The project title.
	 * @return Generated project ID.
	 * @throws SQLException
	 */
	public Long createNewProject(String title) throws SQLException {
		HashMap<Object, Object> data = new HashMap<Object, Object>(4);
		data.put(Project.TITLE, title);
		Project project = new Project(data);
		project.persist(conn);
		return (Long) project.getGeneratedKeys()[0];
	}
	
	/**
	 * This method creates a new experiment.
	 * @param title The experiment title.
	 * @return Generated experiment ID.
	 * @throws SQLException
	 */
	public Long createNewExperiment(Long projectID, String title) throws SQLException {
		HashMap<Object, Object> data = new HashMap<Object, Object>(5);
		data.put(Experiment.FK_PROJECTID, projectID);
		data.put(Experiment.TITLE, title);
		Experiment experiment = new Experiment(data);
		experiment.persist(conn);
		return (Long) experiment.getGeneratedKeys()[0];
	}
	
	/**
	 * This method adds specific properties to the project to the database..
	 * @param projectID The project ID.
	 * @param projectProperties Map containing the project properties.
	 * @throws SQLException 
	 */
	public void addProjectProperties(Long projectID, Map<String, String> projectProperties) throws SQLException{
		// Iterate the given project properties and add them to the database.
		for(Entry entry : projectProperties.entrySet()){
			HashMap<Object, Object> data = new HashMap<Object, Object>(6);
			data.put(Property.FK_PROJECTID, projectID);
			data.put(Property.NAME, entry.getKey());
			data.put(Property.VALUE, entry.getValue());
			Property projProperty = new Property(data);
			projProperty.persist(conn);
		}
	}
	
	/**
	 * This method adds specific properties to the experiment.
	 * @param experimentID The experiment ID.
	 * @param experimentProperties Map containing experiment properties.
	 * @throws SQLException 
	 */
	public void addExperimentProperties(Long experimentID, Map<String, String> experimentProperties) throws SQLException{
		// Iterate the given experiment properties and add them to the database.
		for(Entry entry : experimentProperties.entrySet()){
			HashMap<Object, Object> data = new HashMap<Object, Object>(6);
			data.put(ExpProperty.FK_EXPERIMENTID, experimentID);
			data.put(ExpProperty.NAME, entry.getKey());
			data.put(ExpProperty.VALUE, entry.getValue());
			ExpProperty expProperty = new ExpProperty(data);
			expProperty.persist(conn);
		}
	}
	
	
	/**
	 * This method modifies a project.
	 * @param projectid The project id.
	 * @param projectName The project name.
	 * @throws SQLException
	 */
	public void modifyProject(long projectid, String projectName) throws SQLException {
		Project tempProject = Project.findFromProjectID(projectid, conn);
		tempProject.setTitle(projectName);
		tempProject.setModificationdate(new Timestamp((new Date()).getTime()));
		tempProject.update(conn);
	}


	/**
	 * This method modifies the experiment name.
	 * @param experimentid The experiment id.
	 * @param experimentName The experiment name.
	 * @throws SQLException
	 */
	public void modifyExperimentName(long experimentid, String experimentName)
			throws SQLException {
		Experiment tempExperiment = Experiment.findExperimentByID(experimentid, conn);
		tempExperiment.setTitle(experimentName);
		tempExperiment.setModificationdate(new Timestamp((new Date()).getTime()));
		tempExperiment.update(conn);
	}

	/**
	 * This method modifies the experiment properties.
	 * @param exppropertyid The experiment property id.
	 * @param expProperty The experiment property.
	 * @param expPropertyValue The experiment property value.
	 * @throws SQLException
	 */
	public void modifyExperimentProperties(long exppropertyid,	String expProperty, String expPropertyValue) throws SQLException {
		ExpProperty tempExperimentProperty = ExpProperty.findExpPropertyFromID(exppropertyid, conn);
		tempExperimentProperty.setName(expProperty);
		tempExperimentProperty.setValue(expPropertyValue);
		tempExperimentProperty.update(conn);
	}


	/**
	 * This method removes the project for a specified project ID.
	 * @param projectId The project ID.
	 * @throws SQLException
	 */
	public void removeProject(Long projectId) throws SQLException {		
		Project project = Project.findFromProjectID(projectId, conn);
		project.delete(conn);
	}
	
	/**
	 * This method returns the project title for a specified projectID
	 * @param projectId The project ID.
	 * @return The project title.
	 * @throws SQLException
	 */
	public String getProjectTitle(long projectId) throws SQLException{
		return Project.findFromProjectID(projectId, conn).getTitle();
	}

	/**
	 * This method returns all project properties for a specified projectID
	 * @param projectId
	 * @return ProjectProperties The project properties
	 * @throws SQLException
	 */
	public List<Property> getProjectProperties(long projectId) throws SQLException {
		return Property.findAllPropertiesOfProject(projectId, conn);
	}

	/**
	 * Returns a list experiments from the database.
	 * @return The list of experiments.
	 */
	public List<Experiment> getProjectExperiments(long fk_projectid) throws SQLException {
		return Experiment.findAllExperimentsOfProject(fk_projectid, conn);
	}
	
	/**
	 * Return a specified experiment for a specific project.
	 * @return The specified experiment.
	 * @throws SQLException 
	 */
	public Experiment getProjectExperiment(long projectid, long experimentid) throws SQLException {
		return Experiment.findExperimentByIDandProjectID(experimentid, projectid, conn);
	}
	
	/**
	 * Returns the experiment property from database.
	 * @return The list of experimental properties.
	 */
	public List<ExpProperty> getExperimentProperties(long experimentid) throws SQLException {
		return ExpProperty.findAllPropertiesOfExperiment(experimentid, conn);
	}

	/**
	 * This method returns all projects from the database.
	 */
	public List<Project> getProjects() throws SQLException {
		return Project.findAllProjects(conn);
	}

	/**
	 * This method change the name of a project specified by the projectid
	 * @param projectid
	 * @param title
	 * @throws SQLException
	 */
	public void modifyProjectName(Long projectid, String title) throws SQLException {
		Project project = Project.findFromProjectID(projectid, conn);
		project.setTitle(title);
		project.update(conn);
	}

	/**
	 * This method modify ( change/delete/update) the project properties
	 * @param projectID
	 * @param newProperties
	 * @param operations
	 * @throws SQLException
	 */
	public void modifyProjectProperties(Long projectID,
			Map<String, String> newProperties,
			ArrayList<Operation> operations) throws SQLException {
		
		ArrayList<Property> properties = new ArrayList<Property>(Property.findAllPropertiesOfProject(projectID, conn));
		
		int i = 0;
		for (Entry<String, String> newProperty : newProperties.entrySet()) {
			switch (operations.get(i)) {
			case NONE:
				break;
			case CHANGE:
				properties.get(i).setName(newProperty.getKey());
				properties.get(i).setValue(newProperty.getValue());
				properties.get(i).update(conn);
				break;
			case DELETE:
				properties.get(i).delete(conn);
				break;
			case ADD:
				Property property = new Property(projectID, newProperty.getKey(), newProperty.getValue());
				property.setFk_projectid(projectID);
				property.persist(conn);
				break;
			}
			i++;
		}
		for (int j = i; j < operations.size(); j++) {
			if (operations.get(j) == Operation.DELETE) {
				properties.get(j).delete(conn);
			}
		}
	}
	
	/**
	 * This method changes the name of an experiment specified by the experiment ID.
	 * @param experimentid
	 * @param title
	 * @throws SQLException
	 */
	public void modifyExperimentName(Long experimentid, String title) throws SQLException {
		Experiment experiment = Experiment.findExperimentByID(experimentid, conn);
		experiment.setTitle(title);
		experiment.update(conn);
	}	
	
	/**
	 * This method modifies the properties of a specific experiment guided by a
	 * list of singular operations.
	 * @param experimentID
	 * @param newProperties
	 * @param operations
	 * @throws SQLException
	 */
	public void modifyExperimentProperties(Long experimentID, Map<String, String> newProperties, List<Operation> operations) throws SQLException {
		
		List<ExpProperty> expProperties = new ArrayList<ExpProperty>(ExpProperty.findAllPropertiesOfExperiment(experimentID, conn));
		
		int i = 0;
		for (Entry<String, String> newProperty : newProperties.entrySet()) {
			switch (operations.get(i)) {
			case NONE:
				break;
			case CHANGE:
				expProperties.get(i).setName(newProperty.getKey());
				expProperties.get(i).setValue(newProperty.getValue());
				expProperties.get(i).update(conn);
				break;
			case DELETE:
				expProperties.get(i).delete(conn);
				break;
			case ADD:
				ExpProperty expProperty = new ExpProperty(experimentID, newProperty.getKey(), newProperty.getValue());
				expProperty.setFk_experimentid(experimentID);
				expProperty.persist(conn);
				break;
			}
			i++;
		}
		for (int j = i; j < operations.size(); j++) {
			if (operations.get(j) == Operation.DELETE) {
				expProperties.get(j).delete(conn);
			}
		}
	}
	
	/**
	 * This method deletes an experiment and all its properties by its ID.
	 * @param experimentId
	 * @throws SQLException
	 */
	public void deleteExperiment(Long experimentId) throws SQLException {
		Experiment experiment = Experiment.findExperimentByID(experimentId, conn);
		List<ExpProperty> expPropList = ExpProperty.findAllPropertiesOfExperiment(experimentId, conn);

		for (ExpProperty expProperty : expPropList) {
			expProperty.delete(conn);
		}
		experiment.delete(conn);
	}

	/**
	 * This method deletes the project with its properties, experiment and experiment properties by the project ID.
	 * @param projectid The project id. 
	 * @throws SQLException
	 */
	public void deleteProject(Long projectid) throws SQLException {
		Project project = Project.findFromProjectID(projectid, conn);
		List<Experiment> experimentList =  Experiment.findAllExperimentsOfProject(projectid, conn);
		
		// Delete all experiment
		for (int i = 0; i < experimentList.size(); i++) {
			deleteExperiment(experimentList.get(i).getExperimentid());
		}
		
		// Delete all project properties
		List<Property> projectPropertyList = Property.findAllPropertiesOfProject(projectid, conn);
		for (Property property : projectPropertyList) {
			property.delete(conn);
		}

		// Delete project
		project.delete(conn);
	}
	
	/**
	 * This method checks whether the provided connection is consistent with the manager's own
	 * and updates the latter if needed.
	 * @param conn The database connection.
	 */
	public void revalidate(Connection conn) {
		if (this.conn != conn) {
			this.conn = conn;
		}
	}
	
}
