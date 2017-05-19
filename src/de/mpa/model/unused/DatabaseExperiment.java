package de.mpa.model.unused;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import de.mpa.client.Client;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.projectpanel.dialogs.GeneralDialog.Operation;
import de.mpa.db.mysql.ProjectManager;
import de.mpa.db.mysql.accessor.ExpProperty;
import de.mpa.db.mysql.accessor.Searchspectrum;
import de.mpa.model.dbsearch.DbSearchResult;
// new imports
/**
 * Implementation of the experiment interface for database-linked experiments.
 * 
 * @author A. Behne
 */
public class DatabaseExperiment extends AbstractExperiment {
//
//	/**
//	 * The search result object.
//	 */
////	private DbSearchResult searchResult;
//
//
//	/**
//	 * Creates an empty database-linked experiment.
//	 */
//	public DatabaseExperiment() {
//		this(null, null, null, null, null);
//	}
//
//	/**
//	 * Creates a database-linked experiment using the specified database
//	 * accessor object, experiment properties and parent project.
//	 * @param experimentAcc the database accessor wrapping the experiment
//	 * @param properties the experiment property accessor objects
//	 * @param project the parent project
//	 */
//	public DatabaseExperiment(ExperimentAccessor experimentAcc, List<ExpProperty> properties, AbstractProject project) {
//		this(experimentAcc.getExperimentid(), experimentAcc.getTitle(),
//				experimentAcc.getCreationdate(), properties, project);
//	}
//
//	/**
//	 * Creates a database-linked experiment using the specified title, database
//	 * id, experiment properties and parent project.
//	 * @param title the experiment title
//	 * @param id the experiment's database id
//	 * @param creationDate the project's creation date
//	 * @param properties the experiment property accessor objects
//	 * @param project the parent project
//	 */
//	public DatabaseExperiment(Long id, String title, Date creationDate, List<ExpProperty> properties, AbstractProject project) {
//		super(id, title, creationDate, project);
//		// init properties
//		if (properties != null) {
//			for (ExpProperty property : properties) {
//				this.addProperty(property.getName(), property.getValue());
//			}
//		}
//	}
//
//	@Override
//	public boolean hasSearchResult() {
//		try {
//			return Searchspectrum.hasSearchSpectra(
//					this.getID(), Client.getInstance().getConnection());
//		} catch (SQLException e) {
//			JXErrorPane.showDialog(ClientFrame.getInstance(),
//					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//		};
//		return false;
//	}
//	
//
//	/**
//	 * Implements abstract method, actual result-data is retrieved by 'getSearchResultByView()' 
//	 * in the MultiDwatabaseExperiment class and passed along.
//	 * 
//	 * @return searchResult - this objects search result object
//	 * @author K. Schallert
//	 *  
//	 */ 
//	@Override
//	public DbSearchResult getSearchResult() {
//		// instantiate Multiexp and call viewbased method
//		ArrayList<DatabaseExperiment> experimentList = new ArrayList<DatabaseExperiment>();
//		experimentList.add(this);
//		MultipleDatabaseExperiments multipleDatabaseExperiments = new MultipleDatabaseExperiments(experimentList, this.getTitle(), new Timestamp(Calendar.getInstance().getTime().getTime()), ClientFrame.getInstance().getProjectPanel().getSelectedProject());
//		DbSearchResult dbSearchResult = multipleDatabaseExperiments.getSearchResult();
//		this.searchResult = dbSearchResult;
//		return searchResult;
//	}
//
//	@Override
//	public void setSearchResult(DbSearchResult searchResult) {
//		this.searchResult = searchResult;
//	}
//
//
//	@Override
//	public void persist(String title, Map<String, String> properties, Object... params) {
//		try {
//			this.setTitle(title);
//			this.getProperties().putAll(properties);
//
//			ProjectManager manager = ProjectManager.getInstance();
//
//			// create new experiment in the remote database
//			ExperimentAccessor experimentAcc = manager.createNewExperiment(this.getProject().getID(), this.getTitle());
//			this.setId(experimentAcc.getExperimentid());
//			this.setCreationDate(experimentAcc.getCreationdate());
//
//			// store experiment properties in the remote database
//			manager.addExperimentProperties(this.getID(), this.getProperties());
//
//		} catch (SQLException e) {
//			JXErrorPane.showDialog(ClientFrame.getInstance(),
//					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//		}
//	}
//
//	@Override
//	public void update(String title, Map<String, String> properties, Object... params) {
//		try {
//			this.setTitle(title);
//			this.getProperties().clear();
//			this.getProperties().putAll(properties);
//
//			ProjectManager manager = ProjectManager.getInstance();
//
//			// modify the experiment name
//			manager.modifyExperimentName(this.getID(), this.getTitle());
//
//			// modify the experiment properties
//			manager.modifyExperimentProperties(this.getID(), this.getProperties(), (List<Operation>) params[0]);
//
//		} catch (SQLException e) {
//			JXErrorPane.showDialog(ClientFrame.getInstance(),
//					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//		}
//	}
//
//	@Override
//	public void delete() {
//		try {			
//			ProjectManager manager = ProjectManager.getInstance();
//
//			// remove experiment and all its properties
//			manager.deleteExperiment(this.getID());
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			JXErrorPane.showDialog(ClientFrame.getInstance(),
//					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//		}
//	}

}
