package de.mpa.db.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.client.Client;
import de.mpa.client.ui.projectpanel.dialogs.GeneralDialog;
import de.mpa.db.mysql.accessor.ExpProperty;
import de.mpa.db.mysql.accessor.ExperimentTableAccessor;
import de.mpa.db.mysql.accessor.ProjectTableAccessor;
import de.mpa.db.mysql.accessor.Property;
import de.mpa.model.MPAProject;

/**
 * This class handles project management by accessing project and experiment
 * tables in the remote database.
 * 
 * @author T.Muth, A.Behne
 */
public class ProjectManager {
	
	/**
	 * The singleton instance of the project manager.
	 */
	private static ProjectManager instance;
	
	/**
	 * Database connection.
	 */
	private Connection conn;
		
	/**
	 * Sets up the project manager with an existing database connection.
	 * @param conn Database connection.
	 */
	private ProjectManager(Connection conn) {
        updateConnection(conn);
	}
	
	/**
	 * Returns the singleton instance of the project manager.
	 * @return
	 */
	public static ProjectManager getInstance() {
		Connection conn = null;
		try {
			// try getting database connection, may be null
			conn = Client.getInstance().getDatabaseConnection();
		} catch (SQLException e) {
			// do nothing
			e.printStackTrace();
		}
		return ProjectManager.getInstance(conn);
	}

	/**
	 * Returns the singleton instance of the project manager and validates the
	 * provided database connection.
	 * 
	 * @param conn
	 * @return
	 */
	public static ProjectManager getInstance(Connection conn) {
		if (ProjectManager.instance == null) {
            ProjectManager.instance = new ProjectManager(conn);
		} else {
            ProjectManager.instance.updateConnection(conn);
		}
		return ProjectManager.instance;
	}
	
	/**
	 * Returns whether the project manager is currently connected to a remote
	 * database.
	 * @return <code>true</code> if connected, <code>false</code> otherwise
	 * @throws SQLException if a database error occurs
	 */
	public boolean isConnected() throws SQLException {
		return (this.conn != null) && this.conn.isValid(0);
	}

	/**
	 * Checks whether the provided connection is consistent with the manager's 
	 * own and updates the latter if needed.
	 * @param conn the database connection to validate
	 * @return <code>true</code> if the connection has been updated, 
	 * 	<code>false</code> otherwise
	 */
	public boolean updateConnection(Connection conn) {
		boolean res = (this.conn != conn);
		if (res) {
			this.conn = conn;
		}
		return res;
	}

	/**
	 * Creates a new project with the specified title.
	 * @param title the project title
	 * @return Generated project Accessor
	 * @throws SQLException if a database error occurs
	 */
	public ProjectTableAccessor createNewProject(String title) throws SQLException {
		HashMap<Object, Object> data = new HashMap<Object, Object>(4);
		data.put(ProjectTableAccessor.TITLE, title);
		ProjectTableAccessor projectAcc = new ProjectTableAccessor(data);
		projectAcc.persist(this.conn);
        this.conn.commit();
		return projectAcc;
	}
	
	/**
	 * Changes the title of a project associated with the specified database
	 * id to the specified string.
	 * @param projectId the database id
	 * @param projectName the new project title
	 * @throws SQLException if a database error occurs
	 */
	public void modifyProjectName(long projectId, String projectName) throws SQLException {
		ProjectTableAccessor tempProject = ProjectTableAccessor.findFromProjectID(projectId, this.conn);
		tempProject.setTitle(projectName);
		tempProject.setModificationdate(new Timestamp((new Date()).getTime()));
		tempProject.update(this.conn);
        this.conn.commit();
	}

	/**
	 * Adds the specified properties to the project associated with the
	 * specified database id.
	 * @param projectId the database id
	 * @param properties the project properties
	 * @throws SQLException if a database error occurs
	 */
	public void addProjectProperties(Long projectId, Map<String, String> properties) throws SQLException {
		// Iterate the given project properties and add them to the database.
		for(Map.Entry<String, String> entry : properties.entrySet()){
			HashMap<Object, Object> data = new HashMap<Object, Object>(6);
			data.put(Property.FK_PROJECTID, projectId);
			data.put(Property.NAME, entry.getKey());
			data.put(Property.VALUE, entry.getValue());
			Property projProperty = new Property(data);
			projProperty.persist(this.conn);
		}
        this.conn.commit();
	}

	/**
	 * Modifies the properties of a specific project guided by a list of
	 * operations (change, delete, add).
	 * @param projectId the database id of the project
	 * @param newProperties the map of properties to modify
	 * @param operations the list of operations to apply
	 * @throws SQLException if a database error occurs
	 */
	public void modifyProjectProperties(Long projectId,
			Map<String, String> newProperties,
			List<GeneralDialog.Operation> operations) throws SQLException {

		List<Property> properties = Property.findAllPropertiesOfProject(projectId, conn);

		int i = 0;
		for (Map.Entry<String, String> newProperty : newProperties.entrySet()) {
			switch (operations.get(i)) {
			case NONE:
				break;
			case CHANGE:
				properties.get(i).setName(newProperty.getKey());
				properties.get(i).setValue(newProperty.getValue());
				properties.get(i).update(conn);
				break;
			case DELETE:
				System.out.println("Delete action");
				properties.get(i).delete(conn);
				break;
			case ADD:
				Property property = new Property(projectId, newProperty.getKey(), newProperty.getValue());
				property.setFk_projectid(projectId);
				property.persist(conn);
				break;
			}
			i++;
		}
		for (int j = i; j < operations.size(); j++) {
			if (operations.get(j) == GeneralDialog.Operation.DELETE) {
				properties.get(j).delete(conn);
			}
		}
		conn.commit();
	}

	/**
	 * Deletes a project associated with the specified database id as well
	 * as all associated properties and experiments.
	 * @param projectId the database id
	 * @throws SQLException if a database error occurs
	 */
	public void deleteProject(Long projectId) throws SQLException {
		ProjectTableAccessor project = ProjectTableAccessor.findFromProjectID(projectId, conn);
		List<ExperimentTableAccessor> experimentList =  ExperimentTableAccessor.findAllExperimentsOfProject(projectId, conn);

		// Delete all experiment
		for (int i = 0; i < experimentList.size(); i++) {
			this.deleteExperiment(experimentList.get(i).getExperimentid());
		}

		// Delete all project properties
		List<Property> projectPropertyList = Property.findAllPropertiesOfProject(projectId, conn);
		for (Property property : projectPropertyList) {
			property.delete(conn);
		}

		// Delete project
		project.delete(conn);
		conn.commit();
	}

	/**
	 * Creates a new experiment with the specified title and associates it with
	 * the project associated with the specified database id.
	 * @param projectId the database id of the parent project
	 * @param title the experiment title
	 * @return the database id of the stored experiment
	 * @throws SQLException if a database error occurs
	 */
	public ExperimentTableAccessor createNewExperiment(Long projectId, String title) throws SQLException {
		HashMap<Object, Object> data = new HashMap<Object, Object>(5);
		data.put(ExperimentTableAccessor.FK_PROJECTID, projectId);
		data.put(ExperimentTableAccessor.TITLE, title);
		ExperimentTableAccessor experimentAcc = new ExperimentTableAccessor(data);
		experimentAcc.persist(conn);
		conn.commit();
		return experimentAcc;
	}

	/**
	 * Changes the title of an experiment associated with the specified database
	 * id to the specified string.
	 * @param experimentId the database id
	 * @param title the new experiment title
	 * @throws SQLException if a database error occurs
	 */
	public void modifyExperimentName(Long experimentId, String title) throws SQLException {
		ExperimentTableAccessor experiment = ExperimentTableAccessor.findExperimentByID(experimentId, conn);
		// only modify title if it actually differs from the provided string
		if (!title.equals(experiment.getTitle())) {
			System.out.println("updating title");
			experiment.setTitle(title);
			experiment.update(conn);
			conn.commit();
		}
	}

	/**
	 * Adds the specified properties to the experiment associated with the
	 * specified database id.
	 * @param experimentId the database id
	 * @param expProperties the experiment properties
	 * @throws SQLException if a database error occurs
	 */
	public void addExperimentProperties(Long experimentId, Map<String, String> expProperties) throws SQLException {
		// iterate properties and store them
		for (Map.Entry<String, String> entry : expProperties.entrySet()) {
			HashMap<Object, Object> data = new HashMap<Object, Object>(6);
			data.put(ExpProperty.FK_EXPERIMENTID, experimentId);
			data.put(ExpProperty.NAME, entry.getKey());
			ExpProperty expProperty = new ExpProperty(data);
			expProperty.persist(conn);
		}
		conn.commit();
	}

	/**
	 * Modifies the properties of a specific experiment guided by a list of
	 * operations (change, delete, add).
	 * @param experimentId the database id of the experiment
	 * @param newProperties the map of properties to modify
	 * @param operations the list of operations to apply
	 * @throws SQLException if a database error occurs
	 */
	public void modifyExperimentProperties(Long experimentId,
			Map<String, String> newProperties,
			List<GeneralDialog.Operation> operations) throws SQLException {

		List<ExpProperty> expProperties = ExpProperty.findAllPropertiesOfExperiment(experimentId, conn);

		// iterate properties
		int i = 0;
		for (Map.Entry<String, String> newProperty : newProperties.entrySet()) {
			// determine operation
			switch (operations.get(i)) {
			case NONE:
				// do nothing
				break;
			case CHANGE:
				// modify property
				expProperties.get(i).setName(newProperty.getKey());
				expProperties.get(i).setValue(newProperty.getValue());
				expProperties.get(i).update(conn);
				break;
			case DELETE:
				// remove property
				expProperties.get(i).delete(conn);
				break;
			case ADD:
				// add new property
				ExpProperty expProperty = new ExpProperty(experimentId, newProperty.getKey(), newProperty.getValue());
				expProperty.setFk_experimentid(experimentId);
				expProperty.persist(conn);
				break;
			}
			i++;
		}
		// iterate remaining operations (usually deletes)
		for (int j = i; j < operations.size(); j++) {
			if (operations.get(j) == GeneralDialog.Operation.DELETE) {
				expProperties.get(j).delete(this.conn);
			}
		}
        this.conn.commit();
	}
	
	/**
	 * Deletes an experiment associated with the specified database id as well
	 * as all associated properties and search spectra.
	 * @param experimentId the database id
	 * @throws SQLException if a database error occurs
	 * @author K. Schallert
	 */
	public void deleteExperiment(Long experimentId) throws SQLException {
				
		Client client = Client.getInstance();
        this.conn.setAutoCommit(false);
        ExperimentTableAccessor experiment = ExperimentTableAccessor.findExperimentByID(experimentId, this.conn);
		
		// TODO: client property change isn't working properly, likely due to messy initialization
		client.firePropertyChange("indeterminate", false, true);
		client.firePropertyChange("new message", null, "DELETE EXPERIMENT");    	
		client.firePropertyChange("indeterminate", true, false);
		client.firePropertyChange("resetall", 0L, 7);
		client.firePropertyChange("resetcur", 0L, 7);
		
		// delete all properties of the experiment
		List<ExpProperty> expPropList = ExpProperty.findAllPropertiesOfExperiment(experimentId, this.conn);
		for (ExpProperty expProperty : expPropList) {
			expProperty.delete(this.conn);
            this.conn.commit();
		}
		
		
		client.firePropertyChange("new message", null, "DELETE MASCOTHITS");
		client.firePropertyChange("progressmade", true, false);
		
		// delete mascothits
		Statement stmt = this.conn.createStatement();
		stmt.executeUpdate("DELETE m.* " +
				  "FROM mascothit m, searchspectrum s " +
				  "WHERE m.fk_searchspectrumid = s.searchspectrumid " +
				  "AND s.fk_experimentid = "+ experimentId);
        this.conn.commit();
		
		client.firePropertyChange("new message", null, "DELETE OMSSAHITS");
		client.firePropertyChange("progressmade", true, false);

    	// delete omssahits
		stmt.executeUpdate("DELETE o.* " +
				  "FROM omssahit o, searchspectrum s " +
				  "WHERE o.fk_searchspectrumid = s.searchspectrumid " +
				  "AND s.fk_experimentid = " + experimentId);
        this.conn.commit();

		client.firePropertyChange("new message", null, "DELETE XTANDEMHITS");
		client.firePropertyChange("progressmade", true, false);
		
    	// delete xtandemhits
		stmt.executeUpdate("DELETE x.* " +
				  "FROM xtandemhit x, searchspectrum s " +
				  "WHERE x.fk_searchspectrumid = s.searchspectrumid " +
				  "AND s.fk_experimentid = " + experimentId);
        this.conn.commit();
		
		client.firePropertyChange("new message", null, "DELETE REFERENCES");
		client.firePropertyChange("progressmade", true, false);

//		// delete spec2pep
//		stmt.executeUpdate("DELETE s2p.* FROM spec2pep s2p " +
//							"WHERE s2p.fk_spectrumid IN " +
//							"(SELECT searchspectrum.fk_spectrumid FROM searchspectrum " +
//							"WHERE searchspectrum.fk_experimentid = " + experimentId + ")");
//		conn.commit();
		
    	// delete searchspectra     	
		stmt.executeUpdate("DELETE FROM searchspectrum " +
				  			"WHERE searchspectrum.fk_experimentid = " + experimentId);
        this.conn.commit();
		
		client.firePropertyChange("progressmade", true, false);
		
		client.firePropertyChange("new message", null, "DELETE MASSSPECTRA");
		client.firePropertyChange("progressmade", true, false);
		
		// Delete all rows from spectrum which do not occur in searchspectrum.fk_spectrumid 
		stmt.executeUpdate("DELETE spec2pep.*, spectrum.* FROM spectrum " +
							"INNER JOIN spec2pep ON spec2pep.fk_spectrumid = spectrum.spectrumid " +
							"WHERE spectrum.spectrumid NOT IN " +
							"(SELECT searchspectrum.fk_spectrumid FROM searchspectrum)");
        this.conn.commit();
		client.firePropertyChange("progressmade", true, false);
				
		// delete the experiment itself
		experiment.delete(this.conn);
        this.conn.commit();
		
		client.firePropertyChange("progressmade", true, false);
		client.firePropertyChange("new message", null, "FINISHED DELETING");
	}
	
	/**
	 * Returns the list of all projects currently stored in the targeted database.
	 * @return all stored projects
	 * @throws SQLException if a database error or I/O error occurs
	 */
	public ArrayList<MPAProject> getProjects() throws Exception {
		ArrayList<MPAProject> projects = new ArrayList<>();
		List<ProjectTableAccessor> projectAccs = ProjectTableAccessor.findAllProjects(this.conn);
		for (ProjectTableAccessor projectAcc : projectAccs) {
			MPAProject project = new MPAProject(projectAcc);
			projects.add(project);
		}
		return projects;
	}

}
