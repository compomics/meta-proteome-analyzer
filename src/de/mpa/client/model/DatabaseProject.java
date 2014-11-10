package de.mpa.client.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.dialogs.GeneralDialog.Operation;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.ProjectAccessor;
import de.mpa.db.accessor.Property;

/**
 * Project implementation for database-linked projects.
 * 
 * @author A. Behne
 */
public class DatabaseProject extends AbstractProject {
	
	/**
	 * Creates an empty project.
	 */
	public DatabaseProject() {
		super(null, null, null, null, null);
	}
	
	/**
	 * Creates a project from the specified project database accessor object,
	 * list of properties, and list of child experiments.
	 * @param projectAcc the project accessor
	 * @param experiments the child experiments
	 */
	public DatabaseProject(ProjectAccessor projectAcc, List<Property> properties, List<AbstractExperiment> experiments) {
		super(projectAcc.getProjectid(), projectAcc.getTitle(),
				projectAcc.getCreationdate(), null, experiments);
		// add properties
		if (properties != null) {
			for (Property property : properties) {
				this.getProperties().put(property.getName(), property.getValue());
			}
		}
	}

	@Override
	public void persist(String title, Map<String, String> properties, Object... params) {
		try {
			this.setTitle(title);
			this.getProperties().putAll(properties);

			ProjectManager manager = ProjectManager.getInstance();
			
			// create new project in the remote database
			ProjectAccessor projectAcc = manager.createNewProject(this.getTitle());
			this.setID(projectAcc.getProjectid());
			this.setCreationDate(projectAcc.getCreationdate());
			
			// store project properties in the remote database
			manager.addProjectProperties(this.getID(), this.getProperties());
			
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void update(String title, Map<String, String> properties, Object... params) {
		try {
			this.setTitle(title);
			this.getProperties().clear();
			this.getProperties().putAll(properties);

			ProjectManager manager = ProjectManager.getInstance();

			// modify the project name
			manager.modifyProjectName(this.getID(), this.getTitle());

			// modify the project properties
			manager.modifyProjectProperties(this.getID(), this.getProperties(), (List<Operation>) params[0]);

		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	@Override
	public void delete() {
		try {
			ProjectManager manager = ProjectManager.getInstance();

			// delete project and all of its properties and experiments
			manager.deleteProject(this.getID());

		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

}
