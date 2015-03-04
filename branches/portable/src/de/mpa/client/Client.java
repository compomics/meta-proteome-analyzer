package de.mpa.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.ResultParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.setup.GraphDatabase;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.MascotGenericFileReader.LoadMode;
import de.mpa.job.SearchTask;

public class Client {

	/**
	 * Client instance.
	 */
	private static Client instance = null;
	
	/**
	 * Parameter map containing result fetching-related settings.
	 */
	private ParameterMap metaProtParams = new ResultParameters();

	/**
	 * Property change support for notifying the GUI about new messages.
	 */
	private PropertyChangeSupport pSupport;
	
	/**
	 * Database search result.
	 */
	private DbSearchResult dbSearchResult;

	/**
	 * Flag denoting whether client is in viewer mode.
	 */
	private boolean viewer;
	
	/**
	 * Flag for debugging options.
	 */
	private boolean debug;
	
	/**
	 * GraphDatabaseHandler.
	 */
	private GraphDatabaseHandler graphDatabaseHandler;
	
	/**
	 * List of MGF files
	 */
	private List<File> mgfFiles;

	/**
	 * Creates the singleton client instance in non-viewer, non-debug mode.
	 */
	private Client() {
		this(false, false);
	}
	
	/**
	 * Creates the singleton client instance using the specified viewer and debug mode flags.
	 * @param viewer <code>true</code> if the application is to be launched in viewer mode
	 * @param debug <code>true</code> if the application is to be launched in debug mode
	 */
	private Client(boolean viewer, boolean debug) {
		this.viewer = viewer;
		this.debug = debug;
		this.pSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Returns the client singleton instance.
	 * @return the client singleton instance
	 */
	public static Client getInstance() {
		return instance;
	}
	
	/**
	 * Returns the client singleton instance using the specified viewer and debug mode flags.
	 * @param viewer <code>true</code> if the application is to be launched in viewer mode
	 * @param debug <code>true</code> if the application is to be launched in debug mode
	 */
	public static void init(boolean viewer, boolean debug) {
		if (instance == null) {
			instance = new Client(viewer, debug);
		}
	}	

	/**
	 * Runs the searches by retrieving a bunch of spectrum file names and the global search settings.
	 * @param filenames The spectrum file names
	 * @param settings Global search settings
	 */
	public void runSearches(DbSearchSettings settings) {
		if (mgfFiles != null) {
			try {
				new SearchTask(mgfFiles, settings, new File("/out"), new File(""));
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
		}
	}

	/**
	 * Returns the GraphDatabaseHandler object.
	 * @return The GraphDatabaseHandler object.
	 */
	synchronized public void setupGraphDatabaseContent() {
		// If graph database is already in use.
		if (graphDatabaseHandler != null) {
			// Shutdown old graph database.
			graphDatabaseHandler.shutDown();
		}
		
		// Create a new graph database.
		GraphDatabase graphDb = new GraphDatabase("target/graphdb", true);
		
		// Setup the graph database handler. 
		graphDatabaseHandler = new GraphDatabaseHandler(graphDb);
		graphDatabaseHandler.setData(dbSearchResult);
	}		

	/**
	 * Convenience method to read a spectrum from the MGF file in the specified
	 * path between the specified start and end byte positions.
	 * @param pathname The pathname string pointing to the desired file.
	 * @param startPos The start byte position of the spectrum in the desired file.
	 * @param endPos The end byte position of the spectrum in the desired file.
	 * @return the desired spectrum or <code>null</code> if no such spectrum could be found
	 */
	public MascotGenericFile readSpectrumFromFile(String pathname, long startPos, long endPos) {
		MascotGenericFile mgf = null;
		try {
			// TODO: maybe use only one single reader instance for all MGF parsing needs (file panel, results panel, etc.)
			MascotGenericFileReader reader = new MascotGenericFileReader(new File(pathname), LoadMode.NONE);
			mgf = reader.loadSpectrum(0, startPos, endPos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mgf;
	}

	/**
	 * Writes the current database search result object to a the specified file.
	 * @param filename The String representing the desired file path and name.
	 */
	public void writeDbSearchResultToFile(String filename) {
		Set<SpectrumMatch> spectrumMatches = ((ProteinHitList) dbSearchResult.getProteinHitList()).getMatchSet();
		// Dump referenced spectra to separate MGF
		firePropertyChange("new message", null, "WRITING REFERENCED SPECTRA");
		firePropertyChange("resetall", -1L, (long) spectrumMatches.size());
		firePropertyChange("resetcur", -1L, (long) spectrumMatches.size());
		String status = "FINISHED";
		try {
			String prefix = filename.substring(0, filename.indexOf('.'));
			File mgfFile = new File(prefix + ".mgf");
			FileOutputStream fos = new FileOutputStream(mgfFile);
			long index = 0L;
			for (SpectrumMatch spectrumMatch : spectrumMatches) {
				spectrumMatch.setStartIndex(index);
				// TODO: Write MGF to file. 
//				MascotGenericFile mgf = Client.getInstance().getSpectrumBySearchSpectrumID(spectrumMatch.getSearchSpectrumID());
//				mgf.writeToStream(fos);
				index = mgfFile.length();
				spectrumMatch.setEndIndex(index);
				firePropertyChange("progressmade", false, true);
			}
			
			fos.flush();
			fos.close();
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			status = "FAILED";
		}
		firePropertyChange("new message", null, "WRITING REFERENCED SPECTRA" + status);
	
		// Dump results object to file
		firePropertyChange("new message", null, "WRITING RESULT OBJECT TO DISK");
		status = "FINISHED";
		try {
			firePropertyChange("indeterminate", false, true);
			// store as compressed binary object
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
					new GZIPOutputStream(new FileOutputStream(new File(filename)))));
			oos.writeObject(dbSearchResult);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			status = "FAILED";
		}
		firePropertyChange("indeterminate", true, false);
		firePropertyChange("new message", null, "WRITING RESULT OBJECT TO DISK " + status);
	}

	/**
	 * Adds a property change listener.
	 * @param pcl
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) { 
		pSupport.addPropertyChangeListener(pcl); 
	}

	/**
	 * Removes a property change listener.
	 * @param pcl
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) { 
		pSupport.removePropertyChangeListener(pcl);
	}

	/**
	 * Forwards a bound property update to any registered listeners. 
	 * No event is fired if old and new are equal and non-null. 
	 * @param propertyName The programmatic name of the property that was changed.
	 * @param oldValue The old value of the property.
	 * @param newValue The new value of the property.
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		pSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	/**
	 * Returns the parameter map containing result fetching-related settings.
	 * @return the result parameters
	 */
	public ParameterMap getResultParameters() {
		return this.metaProtParams;
	}

	/**
	 * Returns the {@link GraphDatabaseHandler} object.
	 * @return {@link GraphDatabaseHandler}
	 */
	public GraphDatabaseHandler getGraphDatabaseHandler() {
		return graphDatabaseHandler;
	}

	/**
	 * Returns the current database search result.
	 * @param experiment The experiment content.
	 * @return The current database search result.
	 */
	public DbSearchResult getDatabaseSearchResult(AbstractExperiment experiment) {
		if (dbSearchResult == null) {
			dbSearchResult = experiment.getSearchResult();
		}
		return dbSearchResult;
	}

	/**
	 * Returns the current database search result.
	 * @return dbSearchResult The current database search result.
	 */
	public DbSearchResult getDatabaseSearchResult() {
		return this.getDatabaseSearchResult(ClientFrame.getInstance().getProjectPanel().getSelectedExperiment());
	}

	/**
	 * Sets the current database search result
	 * @param dbSearchResult
	 */
	public void setDatabaseSearchResult(DbSearchResult dbSearchResult) {
		this.dbSearchResult = dbSearchResult;
	}
	
	/**
	 * Returns whether the client is in viewer mode.
	 * @return <code>true</code> if in viewer mode, <code>false</code> otherwise.
	 */
	public static boolean isViewer() {
		return instance.viewer;
	}
	
	/**
	 * Returns whether the client is in debug mode
	 * @return <code>true</code> if in debug mode, <code>false</code> otherwise.
	 */
	public static boolean isDebug() {
		return instance.debug;
	}

	/**
	 * Shuts down the JVM.
	 */
	public static void exit() {
		// Shutdown the graph database
		if (instance.graphDatabaseHandler != null) {
			instance.graphDatabaseHandler.shutDown();
		}
		System.exit(0);
	}
	
	/**
	 * Returns the list of input files.
	 * @return
	 */
	public List<File> getMgfFiles() {
		return mgfFiles;
	}
	
	/**
	 * Sets the list of input files used for the processing.
	 * @param files List of input files
	 */
	public void setMgfFiles(List<File> files) {
		this.mgfFiles = files;
	}
}
