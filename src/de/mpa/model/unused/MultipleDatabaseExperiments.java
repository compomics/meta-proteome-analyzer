package de.mpa.model.unused;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import de.mpa.client.Client;
import de.mpa.client.ui.ClientFrame;
import de.mpa.db.mysql.ProjectManager;
import de.mpa.db.mysql.accessor.Searchspectrum;
import de.mpa.model.dbsearch.DbSearchResult;


/**
 * Implementation of the experiment interface for multiple fused database-linked experiments.
 * @author R. Heyer
 */
@Deprecated
public class MultipleDatabaseExperiments extends AbstractExperiment {

//	/**
//	 * The search result object.
//	 */
//	private DbSearchResult searchResult;
//
//
//	/**
//	 * The list of all experiments, which should be fused
//	 */
//	private ArrayList<DatabaseExperiment> experimentList;
//
//
//	/**
//	 * Constructor
//	 * @param experimentList
//	 * @param title
//	 * @param creationDate
//	 * @param project
//	 */
//	public MultipleDatabaseExperiments(ArrayList<DatabaseExperiment> experimentList, String title,
//		Date creationDate, AbstractProject project) {
//		super(0L, title, creationDate, project);
//		this.experimentList = experimentList;
//		// init taxonomy map
////		this.taxonomyMap = new HashMap<>();
//	}
//
//	@Override
//	public void persist(String title, Map<String, String> properties, Object... params) {
//	}
//
//	@Override
//	public void update(String title, Map<String, String> properties,
//			Object... params) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public void delete() {
//		try {
//			ProjectManager manager = ProjectManager.getInstance();
//
//			// remove experiment and all its properties
//			for (DatabaseExperiment experiment : experimentList) {
//				manager.deleteExperiment(experiment.getID());
//			}
//		} catch (SQLException e) {
//			JXErrorPane.showDialog(ClientFrame.getInstance(),
//					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//		}
//	}
//
//	@Override
//	public boolean hasSearchResult() {
//		try {
//			for (DatabaseExperiment experiment : experimentList) {
//				return Searchspectrum.hasSearchSpectra(experiment.getID(), Client.getInstance().getConnection());
//			}
//		} catch (SQLException e) {
//			JXErrorPane.showDialog(ClientFrame.getInstance(),new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
//		};
//		return false;
//	}
//
//	
//	
//	/**
//	 * Implements abstract method, actual result-data is retrieved by 'getSearchResultByView()' and passed along.
//	 * 
//	 * @return searchResult - this objects search result object
//	 * @author K. Schallert
//	 *  
//	 */ 
//	@Override
//	public DbSearchResult getSearchResult() {
//		// only retrieve result if there is none so far
//		if (searchResult == null) {
//			try {
//				// initialize stuff
//				this.searchResult = new DbSearchResult(this.getProject().getTitle(), this.experimentList, "some database");
//				this.searchResult.getSearchResultByView();
//			} catch (SQLException errSQL) {
//				errSQL.printStackTrace();
//				return null;
//			}
//			// setting the FDR to 0.2 to set all proteins/peptides/spectra to "Visible" 
//			searchResult.setFDR(0.2);
//			Client.getInstance().firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");
//		}
//		return searchResult;
//	}
//
//	@Override
//	@Deprecated
//	public void setSearchResult(DbSearchResult result) {
//		// TODO Auto-generated method stub
//	}
//	
}
