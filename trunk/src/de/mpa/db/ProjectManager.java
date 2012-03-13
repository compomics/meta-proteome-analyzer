package de.mpa.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Project;
import de.mpa.db.accessor.Property;

public class ProjectManager {
	
	
	private Connection conn;
	
	/**
	 * 
	 * @param conn
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
		HashMap<Object, Object> data = new HashMap<Object, Object>(11);
		data.put(Project.TITLE, title);
		Project project = new Project(data);
		project.persist(conn);
		return (Long) project.getGeneratedKeys()[0];
	}
	
	/**
	 * This method adds specific properties to the project to the database..
	 * @param projectID The project ID.
	 * @param projectProperties Map containing the project properties.
	 */
	public void addProjectProperties(Long projectID, Map<String, String> projectProperties){
		//TODO: @heyer: make this working!
	}
	
	/**
	 * This method adds experiment(s) for a specified project.
	 * @param projectID The project ID.
	 * @param experiments List of experiments.
	 */
	public void addExperimentsForProject(Long projectID, List<String> experiments){
		//TODO: @heyer: make this working!
	}
	
	/**
	 * This method adds specific properties to the experiment.
	 * @param experimentID The experiment ID.
	 * @param experimentProperties Map containing experiment properties.
	 */
	public void addExperimentProperties(Long experimentID, Map<String, String> experimentProperties){
		//TODO: @heyer: make this working!
	}
	
	/**
	 * gets properties from database
	 */
	// modifiy project
	public void modifyProject(long projectid, String projectName) throws SQLException {
		Project tempProject = Project.findFromProjectID(projectid, conn);
		tempProject.setTitle(projectName);
		tempProject.setModificationdate(new Timestamp((new Date()).getTime()));
		tempProject.update(conn);
	}

	// modify project property
	public void modifyProjectProperty(long propertyid, String propertyName,
			String propertyValue) throws SQLException {
		Property tempProperty = Property.findPropertyFromPropertyID(propertyid,
				conn);
		tempProperty.setName(propertyName);
		tempProperty.setValue(propertyValue);
		tempProperty.update(conn);
	}

	// modify experimentsname
	public void modifyExperimentsName(long experimentid, String experimentsName)
			throws SQLException {
		Experiment tempExperiment = Experiment.findExperimentByID(experimentid, conn);
		tempExperiment.setTitle(experimentsName);
		tempExperiment.setModificationdate(new Timestamp((new Date()).getTime()));
		tempExperiment.update(conn);
	}

	// modify experimentproperty
	public void modifyExperimentsProperties(long exppropertyid,	String expProperty, String expPropertyValue) throws SQLException {
		ExpProperty tempExperimentProperty = ExpProperty.findExpPropertyFromID(exppropertyid, conn);
		tempExperimentProperty.setName(expProperty);
		tempExperimentProperty.setValue(expPropertyValue);
		tempExperimentProperty.update(conn);
	}


	/**
	 * This method removes the project for a specified project ID.
	 * @param projectid The project ID.
	 * @throws SQLException
	 */
	public void removeProject(Long projectid) throws SQLException {		
		Project project = Project.findFromProjectID(projectid, conn);
		project.delete(conn);
	}

	public List<Property> getProjectProperties(long fk_projectid) throws SQLException {
		return Property.findAllPropertiesOfProject(fk_projectid, conn);
	}

	/**
	 * get experiments from database
	 */

	public List<Experiment> getProjectExperiments(long fk_projectid)
			throws SQLException {
		return Experiment.findAllExperimentsOfProject(fk_projectid, conn);
	}

	/**
	 * get experiment property from database
	 */
	public List<ExpProperty> getExperimentProperties(long experimentid)
			throws SQLException {
		return ExpProperty.findAllPropertiesOfExperiment(experimentid, conn);
	}

	/**
	 * This method returns all projects from the database.
	 */
	public List<Project> getProjects() throws SQLException {
		return Project.findAllProjects(conn);
	}
	// create new Project
	// public void createNewProject (String pTitle,Timestamp pCreationdate,
	// Timestamp pModificationdate) throws SQLException{
	// HashMap<Object, Object> data = new HashMap<Object, Object>(11);
	// data.put(Project.TITLE, pTitle);
	// data.put(Project.CREATIONDATE, pCreationdate);
	// data.put(Project.MODIFICATIONDATE, pModificationdate);
	// Project project = new Project(data);
	// project.persist(conn);
	// project.getGeneratedKeys();
	// }
}
