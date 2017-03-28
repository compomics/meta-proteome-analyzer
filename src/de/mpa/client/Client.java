package de.mpa.client;

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.tree.TreePath;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;

import de.mpa.client.settings.SpectrumFetchParameters;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.model.AbstractProject;
import de.mpa.client.model.DatabaseExperiment;
import de.mpa.client.model.MultipleDatabaseExperiments;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.model.specsim.SpecSimResult;
import de.mpa.client.settings.ConnectionParameters;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.ResultParameters;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.panels.ProjectPanel;
import de.mpa.db.DBConfiguration;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.ExperimentAccessor;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.SpecSearchHit;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.setup.GraphDatabase;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.main.Starter;

public class Client {

	/** 
	 * Client instance.
	 */
	private static Client instance;

	/**
	 * Server implementation service.
	 */
	private ServerImplService service;

	/**
	 * Webservice server instance.
	 */
	private Server server;

	/**
	 * SQL database connection.
	 */
	private Connection conn;
		
	/**
	 * Parameter map containing result processing-related settings.
	 */
	private final ResultParameters resultParams = new ResultParameters();
	
	/**
	 * Parameter map containing connection settings.
	 */
	private ConnectionParameters connectionParams;

	/**
	 * Property change support for notifying the GUI about new messages.
	 */
	private final PropertyChangeSupport pSupport;
	
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
	private final boolean viewer;
	
	/**
	 * Flag for debugging options.
	 */
	private final boolean debug;
	
	/**
	 * GraphDatabaseHandler.
	 */
	private GraphDatabaseHandler graphDatabaseHandler;

	private Client.RequestThread requestThread;

	/**
	 *  if flag is set false, calculations of empai/nsaf/coverage are omitted during FetchResults
	 */
	private boolean fast_results = true;


	public boolean isfast_results() {
		return this.fast_results;
	}

	public void setfast_results(boolean nsaf_empai_coverage_flag) {
		fast_results = nsaf_empai_coverage_flag;
	}

	/**
	 * Creates the singleton client instance in non-viewer, non-debug mode.
	 */
	private Client() {
		this(false, false, false);
	}

	/**
	 * Creates the singleton client instance using the specified viewer and debug mode flags.
	 * @param viewer <code>true</code> if the application is to be launched in viewer mode
	 * @param debug <code>true</code> if the application is to be launched in debug mode
	 */
	private Client(boolean viewer, boolean debug, boolean fast) {
		this.viewer = viewer;
		this.debug = debug;
		fast_results = fast;
		pSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Returns the client singleton instance.
	 * @return the client singleton instance
	 */
	public static Client getInstance() {
		return Client.instance;
	}

	/**
	 * Returns the client singleton instance using the specified viewer and debug mode flags.
	 * @param viewer <code>true</code> if the application is to be launched in viewer mode
	 * @param debug <code>true</code> if the application is to be launched in debug mode
	 */
	public static void init(boolean viewer, boolean debug, boolean fast_results) {
		if (Client.instance == null) {
			Client.instance = new Client(viewer, debug, fast_results);
		}
	}

	/**
	 * Returns the connection to the remote SQL database.
	 * @return the database connection
	 * @throws SQLException if a connection error occurs
	 */
	public Connection getConnection() throws SQLException {
		// check whether connection is valid
		if (this.conn == null || !this.conn.isValid(0) || this.conn.isClosed()) {
			// connect to database
			if (this.connectionParams == null) {
				this.connectionParams = new ConnectionParameters();
			}

			DBConfiguration dbconfig = new DBConfiguration(this.connectionParams);
			conn = dbconfig.getConnection();
		}
		return this.conn;
	}

	/**
	 * Closes the database connection.
	 * @throws SQLException
	 */
	public void closeDBConnection() throws SQLException {
		if (this.conn != null) {
			this.conn.close();
			this.conn = null;
		}
	}

	/**
	 * Connects the client to the web service.
	 */
	public boolean connectToServer() throws WebServiceException {
		if (!hasConnectionToServer()) {
			this.service = new ServerImplService();

			// Enable MTOM
			this.server = this.service.getServerImplPort(new MTOMFeature());

			// Try to send client to server.
			this.sendMessage("Client connected.");

			// enable MTOM in client
			BindingProvider bp = (BindingProvider) this.server;

			// Connection timeout: 12 hours
			bp.getRequestContext().put("com.sun.xml.ws.connect.timeout", 12 * 60 * 1000);

			// Request timeout: 24 hours
			bp.getRequestContext().put("com.sun.xml.ws.request.timeout", 24 * 60 * 60 * 1000);

			// Start new request thread.
			this.requestThread = new Client.RequestThread();
			this.requestThread.start();
			return true;
		}
		return false;
	}
	
	/**
	 * Disconnects manually from the server.
	 */
	public void disconnectFromServer() {
		this.service = null;
		this.server = null;
		
		if (this.requestThread != null){
			try {
				this.requestThread.join();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.requestThread = null;

	}
	
	/**
	 * Checks whether the client is connected to the server - binding provider is working.
	 * @return true if client is connected to the server otherwise false.
	 */
	public boolean hasConnectionToServer() {
		// enable MTOM in client
		if (this.server == null) {
			return false;
		}
		else {
			BindingProvider bp = (BindingProvider) this.server;
			if (bp != null) {
				return true;
			}
		}
		return false;
	}
	
	// Thread polling the server each second.
	private class RequestThread extends Thread {
		@Override
		public void run() {
			while (Client.this.hasConnectionToServer()) {
				try {
					Client.this.request();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Requests the server for response.
	 */
	public void request() {
		String message = this.receiveMessage();
		if (message != null && !message.isEmpty()) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					Client.this.firePropertyChange("New Message", null, message);
				}
			});
		}
	}

	/**
	 * Receives a message from the server - forces the server to send a message.
	 * @return String Received Message
	 */
	public String receiveMessage() {
		return this.server.sendMessage();
	}
	
	/**
	 * Sends a message to the server.
	 * @param message Server message
	 */
	public void sendMessage(String message) {
		this.server.receiveMessage(message);
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
	 * @throws SQLException 
	 */
	public void runSearches(List<String> filenames, SearchSettings settings) throws SQLException {
		if (filenames != null) {
			settings.setFilenames(filenames);
//			for (int i = 0; i < filenames.size(); i++) {
//				settings.getFilenames().add(filenames.get(i));
//			}
			try {
				this.server.runSearches(settings);
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
		}
		
		// reestablish all connections
		closeDBConnection();
		connectToServer();
		getConnection();
		
		// restart the client frame
		ClientFrame.getInstance().restart();
	}
	
	/**
	 * Returns the current spectral similarity search result.
	 * @param expContent The experiment content.
	 * @return The current spectral similarity search result.
	 */
	public SpecSimResult getSpecSimResult(AbstractExperiment expContent) {
		if (this.specSimResult == null) {
			this.retrieveSpecSimResult(expContent.getID());
		}
		return this.specSimResult;
	}

	/**
	 * Returns the result(s) of a spectral similarity search belonging to a particular experiment.
	 * @param experimentID The experiment's primary key.
	 */
	private void retrieveSpecSimResult(Long experimentID) {
		try {
			this.getConnection();
			this.specSimResult = SpecSearchHit.getAnnotations(experimentID, this.conn, this.pSupport);
		} catch (Exception e) {
			this.pSupport.firePropertyChange("new message", null, "FETCHING RESULTS FAILED");
			this.pSupport.firePropertyChange("indeterminate", true, false);
			JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	/**
	 * Resets the current spectral similarity search result reference.
	 */
	public void clearSpecSimResult() {
		this.specSimResult = null;
	}

	/**
	 * Returns the GraphDatabaseHandler object.
	 * @return The GraphDatabaseHandler object.
	 */
	public synchronized void setupGraphDatabase(boolean singleDataResult) {
		// If graph database is already in use.
		if (this.graphDatabaseHandler != null) {
			// Shut down old graph database.
			this.graphDatabaseHandler.shutDown();
		}
		
		// Create a new graph database.
		// Setup the graph database handler. 
		GraphDatabase graphDb;
		if (Starter.isJarExport()) {
//			graphDb = new GraphDatabase("/scratch/metaprot/software/MPApackage/target/graphdb", true);
			graphDb = new GraphDatabase("target/graphdb", true);
		}else{
			graphDb = new GraphDatabase("target/graphdb", true);
		}
		
		// Setup the graph database handler. 
		this.graphDatabaseHandler = new GraphDatabaseHandler(graphDb);

		if (singleDataResult) {
			this.graphDatabaseHandler.setData(this.dbSearchResult);
		}
	}
	
	/**
	 * Queries the database to retrieve a spectrum file belonging to a specific searchspectrum entry.
	 * @param searchspectrumID The primary key of the searchspectrum entry.
	 * @return The corresponding spectrum file object.
	 * @throws SQLException
	 */
	public MascotGenericFile getSpectrumBySearchSpectrumID(long searchspectrumID) throws SQLException {
		// TODO: delegate to experiment implementation
		this.getConnection();
		return new SpectrumExtractor(this.conn).getSpectrumBySearchSpectrumID(searchspectrumID);
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
		// TODO: delegate to experiment implementation
		MascotGenericFile mgf = null;
		try {
			// TODO: maybe use only one single reader instance for all MGF parsing needs (file panel, results panel, etc.)
			MascotGenericFileReader reader = new MascotGenericFileReader(new File(pathname), MascotGenericFileReader.LoadMode.NONE);
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
		// TODO: offload tree table selection-based packing logic to file panel, there is currently too much mixing of UI code and non-UI code in this method
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
				firePropertyChange("resetall", 0L, maxSpectra);
				// iterate over all leaves
				while (spectrumNode != null) {
					// generate tree path and consult selection model whether path is explicitly or implicitly selected
					TreePath spectrumPath = spectrumNode.getPath();
					if (selectionModel.isPathSelected(spectrumPath, true)) {
						if ((numSpectra % packageSize) == 0) {			// create a new package every x files
							if (fos != null) {
								fos.close();
								uploadFile(file.getName(), getBytesFromFile(file));
								file.delete();
							}

							file = new File(filename + (numSpectra/packageSize) + ".mgf");
							filenames.add(file.getName());
							fos = new FileOutputStream(file);
							long remaining = maxSpectra - numSpectra;
							firePropertyChange("resetcur", 0L, (remaining > packageSize) ? packageSize : remaining);
						}
						MascotGenericFile mgf = ClientFrame.getInstance().getFilePanel().getSpectrumForNode(spectrumNode);
						mgf.writeToStream(fos);
						fos.flush();
						firePropertyChange("progressmade", 0L, ++numSpectra);
					}
					spectrumNode = spectrumNode.getNextLeaf();
				}
				if (fos != null) {
					fos.close();
					uploadFile(file.getName(), getBytesFromFile(file));
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
	 * Uploads a file with the specified data contents to the server instance.
	 * @param filename The name of the file to upload
	 * @param data The contents of the file to upload
	 * @return The path of the new file instance on the server
	 */
	public String uploadFile(String filename, byte[] data) {
		return this.server.uploadFile(filename, data);
	}

	/**
	 * Queries the database to retrieve a list of spectrum files belonging to a specified experiment.
	 * @param experimentID The primary key of the experiment.
	 * @param annotType the annotation-related fetch setting, either one of <code>AnnotationType.WITH_ANNOTATIONS</code>,
	 * 					 <code>WITHOUT_ANNOTATIONS</code> or <code>IGNORE_ANNOTATIONS</code>
	 * @param fromLibrary <code>true</code> if the spectra shall be pulled from the spectral library, 
	 * 					  <code>false</code> when they shall be pulled from previous searches. 
	 * @param saveToFile <code>true</code> if the spectra are to be written to a file, <code>false</code> otherwise
	 * @return A list of spectrum files.
	 * @throws Exception if an error occurs
	 */
	public List<MascotGenericFile> downloadSpectra(long experimentID, SpectrumFetchParameters.AnnotationType annotType, boolean fromLibrary, boolean saveToFile) throws Exception {
		return new SpectrumExtractor(this.conn).getSpectraByExperimentID(experimentID, annotType, fromLibrary, saveToFile);
	} 
	
	/**
	 * fetch requested spectra from the database and write them to a file
	 * @param path path to the file that will be written
	 * @param expID database ID of the experiment
	 * @param ident TRUE if identified spectra should be fetched
	 * @param uident TRUE if unidentified spectra should be fetched
	 * @param filter will only fetch spectra that contain the specified string
	 * @throws SQLException if an error with a database fetch occurs
	 */
	public void fetchAndExportSpectra(String path, 
										long expID, 
										boolean ident, 
										boolean uident, 
										String filter) throws SQLException {

		// gather all spectra for the experiment
		Set<Long> allSearchspectrumIds = new HashSet<Long>();
		for (Searchspectrum searchspectrum : Searchspectrum.findFromExperimentID(expID, this.conn)) {
			allSearchspectrumIds.add(searchspectrum.getSearchspectrumid());	
		}
		
		// gather all identified spectra for the experiment
		List<XTandemhit> xtandemHits = XTandemhit.getHitsFromExperimentID(expID, this.conn);
		List<Omssahit> omssaHits = Omssahit.getHitsFromExperimentID(expID, this.conn);
		List<SearchHit> hits = new ArrayList<SearchHit>();
		hits.addAll(xtandemHits);
		hits.addAll(omssaHits);
		Set<Long> identifiedSpectra = new HashSet<Long>();
		for (SearchHit hit : hits) {
			identifiedSpectra.add(hit.getFk_searchspectrumid());
		}
		
		if (!ident) {
			// remove identified spectra
			allSearchspectrumIds.removeAll(identifiedSpectra);
		}
		if (!uident) {
			// retain only identified spectra
			allSearchspectrumIds.retainAll(identifiedSpectra);
		}
		
		// set up the file stream
		firePropertyChange("new message", null, "WRITING FETCHED DATABASE SPECTRA");
		firePropertyChange("resetall", -1L, (long) allSearchspectrumIds.size());
		firePropertyChange("resetcur", -1L, (long) allSearchspectrumIds.size());
		String status = "FINISHED";

		try {
			String prefix = path.substring(0, path.indexOf('.'));
			File mgfFile = new File(prefix + ".mgf");
			FileOutputStream fos = new FileOutputStream(mgfFile);
			// write all relevant MGFs
			for (Long searchSpectrumId : allSearchspectrumIds) {
				long spectrumId = Searchspectrum.findFromSearchSpectrumID(searchSpectrumId, this.conn).getFk_spectrumid();
				MascotGenericFile mgf = Spectrum.getSpectrumFileFromIdAndTitle(spectrumId, filter, this.conn);
				if (mgf != null) {
					mgf.writeToStream(fos);
				}
				firePropertyChange("progressmade", false, true);
			}
			fos.flush();
			fos.close();
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			status = "FAILED";
		}
		firePropertyChange("new message", null, "WRITING SPECTRA " + status);
		
	}

	/**
	 * Copies the backup raw database search result dump to the specified file
	 * path, fetches the spectra referenced by the result object and stores them
	 * alongside the raw result.
	 * @param pathname the string representing the desired file path and name for the result object
	 */
	public void exportDatabaseSearchResult(String pathname) {
		DbSearchResult dbSearchResult = this.restoreBackupDatabaseSearchResult();
		
		Set<SpectrumMatch> spectrumMatches = ((ProteinHitList) dbSearchResult.getProteinHitList()).getMatchSet();
	
		// Dump referenced spectra to separate MGF
		firePropertyChange("new message", null, "WRITING REFERENCED SPECTRA");
		firePropertyChange("resetall", -1L, (long) spectrumMatches.size());
		firePropertyChange("resetcur", -1L, (long) spectrumMatches.size());
		String status = "FINISHED";
		// TODO: clean up mix of Java IO and NIO APIs
		try {
			String prefix = pathname.substring(0, pathname.indexOf('.'));
			File mgfFile = new File(prefix + ".mgf");
			FileOutputStream fos = new FileOutputStream(mgfFile);
			long index = 0L;
			for (SpectrumMatch spectrumMatch : spectrumMatches) {
				spectrumMatch.setStartIndex(index);
				MascotGenericFile mgf = getInstance().getSpectrumBySearchSpectrumID(
						spectrumMatch.getSearchSpectrumID());
				mgf.writeToStream(fos);
				index = mgfFile.length();
				spectrumMatch.setEndIndex(index);
				spectrumMatch.setTitle(mgf.getTitle());
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
		firePropertyChange("indeterminate", false, true);
		try {
//			File backupFile = new File(Constants.BACKUP_RESULT_PATH);
//			if (!backupFile.exists()) {
//				// technically this should never happen
//				System.err.println("No result file backup detected, creating new one...");
			dumpDatabaseSearchResult(dbSearchResult, Constants.BACKUP_RESULT_PATH);
//			}
			// Copy backup file to target location
			Files.copy(Paths.get(Constants.BACKUP_RESULT_PATH), Paths.get(pathname), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			status = "FAILED";
		}
		firePropertyChange("indeterminate", true, false);
		firePropertyChange("new message", null, "WRITING RESULT OBJECT TO DISK " + status);
	}
	
	/**
	 * Dumps the specified search result object as a binary file identified by the specified path name.
	 * @param result the result object to dump
	 * @param pathname the path name string
	 * @throws IOException if an I/O error occurs
	 */
	private void dumpDatabaseSearchResult(DbSearchResult result, String pathname) throws IOException {
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
		try {
			dumpDatabaseSearchResult(this.dbSearchResult, Constants.BACKUP_RESULT_PATH);
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
		AbstractExperiment currentExperiment = ClientFrame.getInstance().getProjectPanel().getCurrentExperiment();
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
				new GZIPInputStream(new FileInputStream(new File(Constants.BACKUP_RESULT_PATH)))))) {
			DbSearchResult restoredDbSearchResult = (DbSearchResult) ois.readObject();
//			currentExperiment.setSearchResult(restoredDbSearchResult);
			// Has to update the dbSearchResult object in the client, too
			this.dbSearchResult = restoredDbSearchResult;
			ClientFrame.getInstance().getResultsPanel().setDBSearchResultObj(this.dbSearchResult);
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			currentExperiment.clearSearchResult();
		} 
		return this.dbSearchResult;
	}

	/**
	 * Adds a property change listener.
	 * @param pcl the property change listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		this.pSupport.addPropertyChangeListener(pcl);
	}

	/**
	 * Removes a property change listener.
	 * @param pcl the property change listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		this.pSupport.removePropertyChangeListener(pcl);
	}

	/**
	 * Forwards a bound property update to any registered listeners. 
	 * No event is fired if old and new are equal and non-null. 
	 * @param propertyName The programmatic name of the property that was changed.
	 * @param oldValue The old value of the property.
	 * @param newValue The new value of the property.
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		this.pSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Returns the current connection to the database.
	 * @return
	 * @throws SQLException 
	 */
	public Connection getDatabaseConnection() throws SQLException {
		if ((this.conn == null) && !Client.isViewer()) {
			getConnection();
		}
		return this.conn;
	}

	/**
	 * Returns the parameter map containing connection settings.
	 * @return the connection settings
	 */
	public ParameterMap getConnectionParameters() {
		return connectionParams;
	}
	
	/**
	 * Sets the parameter map containing connection settings.
	 * @param connectionParams
	 */
	public void setConnectionParams(ConnectionParameters connectionParams) {
		this.connectionParams = connectionParams;
	}
	
	/**
	 * Returns the parameter map containing result fetching-related settings.
	 * @return the result parameters
	 */
	public ResultParameters getResultParameters() {
		return resultParams;
	}

	/**
	 * Returns the {@link GraphDatabaseHandler} object.
	 * @return {@link GraphDatabaseHandler}
	 */
	public GraphDatabaseHandler getGraphDatabaseHandler() {
		return this.graphDatabaseHandler;
	}
	
	/**
	 * Returns the current database search result.
	 * @return dbSearchResult The current database search result.
	 */
	public DbSearchResult getDatabaseSearchResult() {
		return this.dbSearchResult;
	}
	
	/**
	 * Returns searchresult constructed from the experiment currently selected in the 
	 * 
	 * @return
	 */
	public DbSearchResult getSingleSearchResult() {
		LinkedList<Long> experimentList = new LinkedList<Long>();
		AbstractExperiment selexp = ClientFrame.getInstance().getProjectPanel().getSelectedExperiment();
		experimentList.add(selexp.getID());
		MultipleDatabaseExperiments multipleDatabaseExperiments = new MultipleDatabaseExperiments(experimentList, selexp.getTitle(), new Timestamp(Calendar.getInstance().getTime().getTime()), ClientFrame.getInstance().getProjectPanel().getSelectedProject());
		DbSearchResult searchResult = multipleDatabaseExperiments.getSearchResult();
		dbSearchResult = searchResult;
		return this.dbSearchResult;
	}
	
	/**
	 * Returns the database search result of all experiments in the list.
	 * @return dbSearchResult The database search result of all listet experiments.
	 */
	public DbSearchResult getMultipleSearchResults(LinkedList<Long> experimentList) {
		MultipleDatabaseExperiments multipleDatabaseExperiments = new MultipleDatabaseExperiments(experimentList, "MultipleExperimentObject", new Timestamp(Calendar.getInstance().getTime().getTime()), ClientFrame.getInstance().getProjectPanel().getSelectedProject());
		DbSearchResult searchResult = multipleDatabaseExperiments.getSearchResult();
		dbSearchResult = searchResult;
		return this.dbSearchResult;
	}
	
	/**
	 * Returns the database search result independent of type of result
	 * @return dbSearchResult The database search result of single or all listed experiments.
	 */
	public DbSearchResult fetchResults() {
		return this.dbSearchResult;
	}
	
	/**
	 * Returns whether the client is in viewer mode.
	 * @return <code>true</code> if in viewer mode, <code>false</code> otherwise.
	 */
	public static boolean isViewer() {
		return Client.instance.viewer;
	}
	
	/**
	 * Returns whether the client is in debug mode
	 * @return <code>true</code> if in debug mode, <code>false</code> otherwise.
	 */
	public static boolean isDebug() {
		return Client.instance.debug;
	}

	/**
	 * Shuts down the application.
	 */
	public static void exit() {
		// Shutdown the graph database
		if (Client.instance.graphDatabaseHandler != null) {
			Client.instance.graphDatabaseHandler.shutDown();
		}
	
		try {
			// Close SQL DB connection
			Client.instance.closeDBConnection();
			// Delete backup result object
			Files.deleteIfExists(Paths.get(Constants.BACKUP_RESULT_PATH));
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		} finally {
			System.exit(0);
		}
	}
}
