package de.mpa.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.model.specsim.SpecSimResult;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.ResultParameters;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.ClientFrame;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.setup.GraphDatabase;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.MascotGenericFileReader.LoadMode;

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
	 * Spectral similarity search result.
	 */
	private SpecSimResult specSimResult;

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
	 * Returns the contents of the file in a byte array.
	 * @param file File object
	 * @return Byte array for file
	 * @throws IOException
	 */
	public byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// Before converting to an int type, check to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
			is.close();
			throw new IOException("File size too long: " + length);
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file " + file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	/**
	 * Runs the searches by retrieving a bunch of spectrum file names and the global search settings.
	 * @param filenames The spectrum file names
	 * @param settings Global search settings
	 */
	public void runSearches(List<String> filenames, SearchSettings settings) {
		if (filenames != null) {
			for (int i = 0; i < filenames.size(); i++) {
				settings.getFilenames().add(filenames.get(i));
			}
			try {
//				server.runSearches(settings);
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
		}
	}



	/**
	 * Resets the current spectral similarity search result reference.
	 */
	public void clearSpecSimResult() {
		specSimResult = null;
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
	 * Method to consolidate spectra which are selected in a specified checkbox tree into spectrum packages of defined size.
	 * @param packageSize The amount of spectra per package.
	 * @param checkBoxTree The checkbox tree.
	 * @param listener An optional property change listener used to monitor progress.
	 * @return A list of files.
	 * @throws IOException if reading a spectrum file fails
	 * @throws SQLException if fetching spectrum data from the database fails
	 */
	public List<String> packAndSend(long packageSize, CheckBoxTreeTable checkBoxTree, String filename) throws IOException, SQLException {
		File file = null;
		List<String> filenames = new ArrayList<String>();
		FileOutputStream fos = null;
		CheckBoxTreeSelectionModel selectionModel = checkBoxTree.getCheckBoxTreeSelectionModel();
		if (checkBoxTree.getTreeTableModel().getRoot() != null) {
			CheckBoxTreeTableNode fileRoot = (CheckBoxTreeTableNode) ((DefaultTreeTableModel) checkBoxTree.getTreeTableModel()).getRoot();
			long numSpectra = 0;
			long maxSpectra = selectionModel.getSelectionCount();
			CheckBoxTreeTableNode spectrumNode = fileRoot.getFirstLeaf();
			if (spectrumNode != fileRoot) {
				this.firePropertyChange("resetall", 0L, maxSpectra);
				// iterate over all leaves
				while (spectrumNode != null) {
					// generate tree path and consult selection model whether path is explicitly or implicitly selected
					TreePath spectrumPath = spectrumNode.getPath();
					if (selectionModel.isPathSelected(spectrumPath, true)) {
						if ((numSpectra % packageSize) == 0) {			// create a new package every x files
							if (fos != null) {
								fos.close();
//								this.uploadFile(file.getName(), this.getBytesFromFile(file));
								file.delete();
							}

							file = new File(filename + (numSpectra/packageSize) + ".mgf");
							filenames.add(file.getName());
							fos = new FileOutputStream(file);
							long remaining = maxSpectra - numSpectra;
							this.firePropertyChange("resetcur", 0L, (remaining > packageSize) ? packageSize : remaining);
						}
						MascotGenericFile mgf = ClientFrame.getInstance().getFilePanel().getSpectrumForNode(spectrumNode);
						mgf.writeToStream(fos);
						fos.flush();
						this.firePropertyChange("progressmade", 0L, ++numSpectra);
					}
					spectrumNode = spectrumNode.getNextLeaf();
				}
				if (fos != null) {
					fos.close();
//					this.uploadFile(file.getName(), this.getBytesFromFile(file));
					file.delete();
				}
			} else {
				IOException e = new IOException("No files selected.");
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				throw e;
			}	
		}
		file.delete();
		return filenames;
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

}
