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
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import de.mpa.client.model.FileExperiment;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.settings.PostProcessingParameters;
import de.mpa.client.ui.ClientFrame;
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
	 * @param debug <code>true</code> if the application is to be launched in debug mode
	 */
	public static void init(boolean debug) {
		if (instance == null) {
			instance = new Client(debug);
		}
	}	

	/**
	 * Runs the searches by retrieving a bunch of spectrum file names and the global search settings.
	 * 	 * @param settings Global search settings
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void runSearches(DbSearchSettings settings) throws IOException, ClassNotFoundException  {
		// The FASTA loader
		FastaLoader fastaLoader = FastaLoader.getInstance();
		File fastaFile = new File(settings.getFastaFilePath());
		fastaLoader.setFastaFile(fastaFile);
		
		// Add fasta file to the Sequence factory
//		GenericContainer.SeqFactory.loadFastaFile(fastaFile);
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
	 * Copies the backup raw database search result dump to the specified file path, fetches the spectra referenced by the result object and stores them
	 * alongside the raw result.
	 * @param pathname the string representing the desired file path and name for the result object
	 */
	public void exportDatabaseSearchResult(String pathname) {
		// Dump referenced spectra to separate MGF
		this.firePropertyChange("new message", null, "WRITING REFERENCED SPECTRA");
		String status = "FINISHED";
		try {
			for (String spectrumFilePath : dbSearchResult.getSpectrumFilePaths()) {
				File spectrumFile = new File(spectrumFilePath);
				File parentFile = new File(pathname).getParentFile();
				File createdSpectrumFile = new File(parentFile.getAbsolutePath() + File.separator + spectrumFile.getName());
				// Check whether spectrum file is not already existing
				if (!createdSpectrumFile.exists()) {
					Files.copy(spectrumFile.toPath(), createdSpectrumFile.toPath());
				}
			}
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
	 * Returns a list of paths for the MGF input files.
	 * @return MGF file path list
	 */
	public List<String> getMgfFilePaths() {
		List<String> filePathList = new ArrayList<>();
		
		for (File file : mgfFiles) {
			filePathList.add(file.getAbsolutePath());
		}
		return filePathList;
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
		// Store as compressed binary object.
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(new File(pathname)))));
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
	
	/**
	 * Loads a database search result from an experimental file.
	 * @param filePath Path to MPA experiment
	 * @return DbSearchResult instance
	 */
	public DbSearchResult loadDatabaseSearchResultFromFile(String filePath) {
		DbSearchResult dbSearchResult = null;
		File experimentFile = new File(filePath);
		// Check whether file exists and is actually a file.
		if (experimentFile.exists() && experimentFile.isFile()) {
			try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(experimentFile))))) {
				dbSearchResult = (DbSearchResult) ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
		}
		return dbSearchResult;
	}
}
