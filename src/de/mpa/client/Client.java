package de.mpa.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import de.mpa.client.model.FileExperiment;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.settings.PostProcessingParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.setup.GraphDatabase;
import de.mpa.io.GenericContainer;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.task.ResourceProperties;
import de.mpa.task.SearchTask;
import de.mpa.task.TaskManager;

public class Client {

	/**
	 * Client instance.
	 */
	private static Client instance = null;
	
	/**
	 * Parameter map containing result processing-related settings.
	 */
	private PostProcessingParameters resultParams = new PostProcessingParameters();

	/**
	 * Property change support for notifying the GUI about new messages.
	 */
	private PropertyChangeSupport pSupport;
	
	/**
	 * Database search result.
	 */
	private DbSearchResult dbSearchResult;

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
		this(false);
	}
	
	/**
	 * Creates the singleton client instance using the specified viewer and debug mode flags.
	 * @param viewer <code>true</code> if the application is to be launched in viewer mode
	 * @param debug <code>true</code> if the application is to be launched in debug mode
	 */
	private Client(boolean debug) {
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
	public static void init(boolean debug) {
		if (instance == null) {
			instance = new Client(debug);
		}
	}	

	/**
	 * Runs the searches by retrieving a bunch of spectrum file names and the global search settings.
	 * @param filenames The spectrum file names
	 * @param settings Global search settings
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void runSearches(DbSearchSettings settings) throws IOException, ClassNotFoundException  {
		// The FASTA loader
		FastaLoader fastaLoader = FastaLoader.getInstance();
		File fastaFile = new File(settings.getFastaFilePath());
		fastaLoader.setFastaFile(fastaFile);
		
		// Add fasta file to the Sequence factory
		GenericContainer.SeqFactory.loadFastaFile(fastaFile);
		File indexFile = new File(settings.getFastaFilePath() + ".fb");
		if (indexFile.exists()) {
			fastaLoader.setIndexFile(indexFile);
			fastaLoader.readIndexFile();
		} else {
			throw new IOException("Index file does not exist: " + settings.getFastaFilePath() + ".fb");
		}
		
		GenericContainer.FastaLoader = fastaLoader;
		
		if (mgfFiles != null) {
			TaskManager taskManager = TaskManager.getInstance();
			// Clear the task manager to account for unfinished jobs in the queue.
			taskManager.clear();
			
			Client.getInstance().firePropertyChange("indeterminate", true, false);
			Client.getInstance().firePropertyChange("new message", null, "DATABASE SEARCH RUNNING");
			
			for (File mgfFile : mgfFiles) {
				new SearchTask(mgfFile, settings);
				taskManager.run();
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
	 * Writes the current database search result object to a the specified file.
	 * @param filename The String representing the desired file path and name.
	 */
	/**
	 * Copies the backup raw database search result dump to the specified file
	 * path, fetches the spectra referenced by the result object and stores them
	 * alongside the raw result.
	 * @param pathname the string representing the desired file path and name for the result object
	 */
	public void exportDatabaseSearchResult(String pathname) {
		DbSearchResult dbSearchResult = restoreBackupDatabaseSearchResult();
		
		Set<SpectrumMatch> spectrumMatches = ((ProteinHitList) dbSearchResult.getProteinHitList()).getMatchSet();
	
		// Dump referenced spectra to separate MGF
		this.firePropertyChange("new message", null, "WRITING REFERENCED SPECTRA");
		this.firePropertyChange("resetall", -1L, (long) spectrumMatches.size());
		this.firePropertyChange("resetcur", -1L, (long) spectrumMatches.size());
		String status = "FINISHED";
		// TODO: clean up mix of Java IO and NIO APIs
		try {
			String prefix = pathname.substring(0, pathname.indexOf('.'));
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
		this.firePropertyChange("new message", null, "WRITING REFERENCED SPECTRA" + status);
	
		// Dump results object to file
		this.firePropertyChange("new message", null, "WRITING RESULT OBJECT TO DISK");
		status = "FINISHED";
		this.firePropertyChange("indeterminate", false, true);
		String path = ResourceProperties.getInstance().getProperty("path.base");
		try {
			this.dumpDatabaseSearchResult(dbSearchResult, path + Constants.BACKUP_RESULT);

			// Copy backup file to target location
			Files.copy(Paths.get(path + Constants.BACKUP_RESULT), Paths.get(pathname), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			status = "FAILED";
		}
		this.firePropertyChange("indeterminate", true, false);
		this.firePropertyChange("new message", null, "WRITING RESULT OBJECT TO DISK " + status);
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
	public PostProcessingParameters getResultParameters() {
		return this.resultParams;
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
	 * @return dbSearchResult The current database search result.
	 */
	public DbSearchResult getDatabaseSearchResult() {
		dbSearchResult = ClientFrame.getInstance().getProjectPanel().getSearchResult();
		return dbSearchResult;
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
//		if (instance.graphDatabaseHandler != null) {
//			instance.graphDatabaseHandler.shutDown();
//		}
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
	
	/**
	 * Dumps the specified search result object as a binary file identified by the specified path name.
	 * @param result the result object to dump
	 * @param pathname the path name string
	 * @throws IOException if an I/O error occurs
	 */
	public void dumpDatabaseSearchResult(DbSearchResult result, String pathname) throws IOException {
		// store as compressed binary object
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
				new GZIPOutputStream(new FileOutputStream(new File(pathname)))));
		oos.writeObject(result);
		oos.flush();
		oos.close();
	}
	
	/**
	 * Dumps the current database search result object to a temporary file for
	 * result restoration/export purposes.
	 */
	public void dumpBackupDatabaseSearchResult() {
		String path = ResourceProperties.getInstance().getProperty("path.base");
		try {
			this.dumpDatabaseSearchResult(dbSearchResult, path + Constants.BACKUP_RESULT);
		} catch (IOException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), e.getMessage(), null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	/**
	 * Restores the current database search result object from the dumped temporary file.
	 * @return the restored result object or <code>null</code> if an error occurred
	 */
	public DbSearchResult restoreBackupDatabaseSearchResult() {
		FileExperiment currentExperiment = ClientFrame.getInstance().getProjectPanel().getCurrentExperiment();
		String path = ResourceProperties.getInstance().getProperty("path.base");
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(new File(path + Constants.BACKUP_RESULT)))))) {
			DbSearchResult dbSearchResult = (DbSearchResult) ois.readObject();
			currentExperiment.setSearchResult(dbSearchResult);
		} catch (Exception e) {
			new File(path + Constants.BACKUP_RESULT);
			e.printStackTrace();
			JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			currentExperiment.clearSearchResult();
		}
		return currentExperiment.getSearchResult();
	}
}
