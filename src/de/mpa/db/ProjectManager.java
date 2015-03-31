package de.mpa.db;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.model.AbstractProject;
import de.mpa.client.model.DatabaseExperiment;
import de.mpa.client.model.DatabaseProject;
import de.mpa.client.ui.dialogs.GeneralDialog.Operation;
import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.Cruxhit2prot;
import de.mpa.db.accessor.Cruxhit2protTableAccessor;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.ExperimentAccessor;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Mascothit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.ProjectAccessor;
import de.mpa.db.accessor.Property;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spec2pep;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.XTandemhit;

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
		this.updateConnection(conn);
	}
	
	/**
	 * Returns the singleton instance of the project manager.
	 * @return
	 */
	public static ProjectManager getInstance() {
		Connection conn = null;
		try {
			// try getting database connection, may be null
			if (!Client.isViewer()) {
				conn = Client.getInstance().getDatabaseConnection();
			}
		} catch (SQLException e) {
			// do nothing
			e.printStackTrace();
		}
		return getInstance(conn);
	}

	/**
	 * Returns the singleton instance of the project manager and validates the
	 * provided database connection.
	 * 
	 * @param conn
	 * @return
	 */
	public static ProjectManager getInstance(Connection conn) {
		if (instance == null) {
			instance = new ProjectManager(conn);
		} else {
			instance.updateConnection(conn);
		}
		return instance;
	}
	
	/**
	 * Returns whether the project manager is currently connected to a remote
	 * database.
	 * @return <code>true</code> if connected, <code>false</code> otherwise
	 * @throws SQLException if a database error occurs
	 */
	public boolean isConnected() throws SQLException {
		return (conn != null) && conn.isValid(0);
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
	 * @return Generated project ID.
	 * @throws SQLException if a database error occurs
	 */
	public ProjectAccessor createNewProject(String title) throws SQLException {
		HashMap<Object, Object> data = new HashMap<Object, Object>(4);
		data.put(ProjectAccessor.TITLE, title);
		ProjectAccessor projectAcc = new ProjectAccessor(data);
		projectAcc.persist(conn);
		conn.commit();
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
		ProjectAccessor tempProject = ProjectAccessor.findFromProjectID(projectId, conn);
		tempProject.setTitle(projectName);
		tempProject.setModificationdate(new Timestamp((new Date()).getTime()));
		tempProject.update(conn);
		conn.commit();
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
		for(Entry<String, String> entry : properties.entrySet()){
			HashMap<Object, Object> data = new HashMap<Object, Object>(6);
			data.put(Property.FK_PROJECTID, projectId);
			data.put(Property.NAME, entry.getKey());
			data.put(Property.VALUE, entry.getValue());
			Property projProperty = new Property(data);
			projProperty.persist(conn);
		}
		conn.commit();
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
			List<Operation> operations) throws SQLException {
		
		List<Property> properties = Property.findAllPropertiesOfProject(projectId, conn);
		
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
				Property property = new Property(projectId, newProperty.getKey(), newProperty.getValue());
				property.setFk_projectid(projectId);
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
		conn.commit();
	}

	/**
	 * Deletes a project associated with the specified database id as well
	 * as all associated properties and experiments.
	 * @param projectId the database id
	 * @throws SQLException if a database error occurs
	 */
	public void deleteProject(Long projectId) throws SQLException {
		ProjectAccessor project = ProjectAccessor.findFromProjectID(projectId, conn);
		List<ExperimentAccessor> experimentList =  ExperimentAccessor.findAllExperimentsOfProject(projectId, conn);
		
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
	public ExperimentAccessor createNewExperiment(Long projectId, String title) throws SQLException {
		HashMap<Object, Object> data = new HashMap<Object, Object>(5);
		data.put(ExperimentAccessor.FK_PROJECTID, projectId);
		data.put(ExperimentAccessor.TITLE, title);
		ExperimentAccessor experimentAcc = new ExperimentAccessor(data);
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
		ExperimentAccessor experiment = ExperimentAccessor.findExperimentByID(experimentId, conn);
		// only modify title if it actually differs from the provided string
		if (!title.equals(experiment.getTitle())) {
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
		for (Entry entry : expProperties.entrySet()) {
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
			List<Operation> operations) throws SQLException {
		
		List<ExpProperty> expProperties = ExpProperty.findAllPropertiesOfExperiment(experimentId, conn);
		
		// iterate properties
		int i = 0;
		for (Entry<String, String> newProperty : newProperties.entrySet()) {
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
			if (operations.get(j) == Operation.DELETE) {
				expProperties.get(j).delete(conn);
			}
		}
		conn.commit();
	}
	
	/**
	 * Deletes an experiment associated with the specified database id as well
	 * as all associated properties and search spectra.
	 * @param experimentId the database id
	 * @throws SQLException if a database error occurs
	 */
	public void deleteExperiment(Long experimentId) throws SQLException {
		ExperimentAccessor experiment = ExperimentAccessor.findExperimentByID(experimentId, conn);
		
		// Delete all properties of the experiment
		List<ExpProperty> expPropList = ExpProperty.findAllPropertiesOfExperiment(experimentId, conn);
		for (ExpProperty expProperty : expPropList) {
			expProperty.delete(conn);
		}
		
		// Delete the X!Tandem hits.
		List<XTandemhit> xTandemHits = XTandemhit.getHitsFromExperimentID(experimentId, conn);
		if (xTandemHits.size() > 0) {
			for (XTandemhit xTandemhit : xTandemHits) {
				xTandemhit.delete(conn);
			}
		}
		
		// Delete the OMSSA hits.
		List<Omssahit> omssaHits = Omssahit.getHitsFromExperimentID(experimentId, conn);
		if (omssaHits.size() > 0) {
			for (Omssahit omssaHit : omssaHits) {
				omssaHit.delete(conn);
			}
		}
		
		// Delete the MASCOT hits.
		List<Mascothit> mascotHits = Mascothit.getHitsFromExperimentID(experimentId, conn);
		if (mascotHits.size() > 0) {
			for (Mascothit mascotHit : mascotHits) {
				mascotHit.delete(conn);
			}
		}
		
		// Delete the Crux hits.
		List<Cruxhit> cruxHits = Cruxhit.getHitsFromExperimentID(experimentId, conn);
		if (cruxHits.size() > 0) {
			for (Cruxhit cruxHit : cruxHits) {
				List<Cruxhit2protTableAccessor> hits = Cruxhit2prot.getHitsFromCruxHitID(cruxHit.getCruxhitid(), conn);
				for (Cruxhit2protTableAccessor c2pHit : hits) {
					c2pHit.delete(conn);
				}
				cruxHit.delete(conn);
			}
		}
		
		// Delete the InSpect hits.
		List<Inspecthit> inspectHits = Inspecthit.getHitsFromExperimentID(experimentId, conn);
		if (inspectHits.size() > 0) {
			for (Inspecthit inspectHit : inspectHits) {
				inspectHit.delete(conn);
			}
		}
		
		// Delete all search spectrum entries linked to the experiment
		List<Searchspectrum> searchSpectra = Searchspectrum.findFromExperimentID(experimentId, conn);
		for (Searchspectrum searchSpectrum : searchSpectra) {
			long spectrumid = searchSpectrum.getFk_spectrumid();
			searchSpectrum.delete(conn);
			
			Spectrum spectrum = Spectrum.findFromSpectrumID(spectrumid, conn);
			
			// Are spectra mapping from one spectrum to multiple search spectra?
			List<Searchspectrum> spectraList = Searchspectrum.findFromSpectrumID(spectrumid, conn);
			
			if (spectraList.size() == 0 && spectrum != null) {
				List<Spec2pep> spec2PepEntries = Spec2pep.findFromID(spectrum.getSpectrumid(), conn);
				for (Spec2pep spec2pep : spec2PepEntries) {
					spec2pep.delete(conn);
				}
				spectrum.delete(conn);
			}
		}
		
		// Delete the experiment itself
		experiment.delete(conn);
		conn.commit();
	}
	
	
	
	/* convenience getters below this point */
	
	/**
	 * Returns the list of all projects currently stored in the targeted database.
	 * @return all stored projects
	 * @throws SQLException if a database error or I/O error occurs
	 */
	@SuppressWarnings("unchecked")
	public List<AbstractProject> getProjects() throws Exception {
		List<AbstractProject> projects = new ArrayList<>();
		
		if (Client.isViewer()) {
			File projectsFile = Constants.getProjectsFile();
			projects = (List<AbstractProject>) new XStream().fromXML(projectsFile);
		} else {
			List<ProjectAccessor> projectAccs = ProjectAccessor.findAllProjects(conn);
			for (ProjectAccessor projectAcc : projectAccs) {
				List<Property> projProps = Property.findAllPropertiesOfProject(projectAcc.getProjectid(), conn);
				
				List<AbstractExperiment> experiments = new ArrayList<>();

				AbstractProject project = new DatabaseProject(projectAcc, projProps, experiments);
				
				List<ExperimentAccessor> experimentAccs =
						ExperimentAccessor.findAllExperimentsOfProject(projectAcc.getProjectid(), conn);
				for (ExperimentAccessor experimentAcc : experimentAccs) {
					List<ExpProperty> expProps =
							ExpProperty.findAllPropertiesOfExperiment(experimentAcc.getExperimentid(), conn);
					experiments.add(new DatabaseExperiment(experimentAcc, expProps, project));
				}
				
				projects.add(project);
			}
		}
		return projects;
	}

}
