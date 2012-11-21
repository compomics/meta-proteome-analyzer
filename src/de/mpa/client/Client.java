package de.mpa.client;

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.swing.tree.TreePath;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import de.mpa.client.model.ExperimentContent;
import de.mpa.client.model.ProjectContent;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.model.denovo.DenovoSearchResult;
import de.mpa.client.model.specsim.SpecSimResult;
import de.mpa.client.model.specsim.SpectralSearchCandidate;
import de.mpa.client.settings.ServerConnectionSettings;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.ClientFrame;
import de.mpa.db.ConnectionType;
import de.mpa.db.DBConfiguration;
import de.mpa.db.DbConnectionSettings;
import de.mpa.db.accessor.Pepnovohit;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.SpecSearchHit;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.extractor.SearchHitExtractor;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.MascotGenericFileReader.LoadMode;
import de.mpa.webservice.WSPublisher;

public class Client {

	// Client instance
	private static Client client = null;

	// Server service
	private ServerImplService service;

	// Server instance
	private Server server;

	// Connection
	private Connection conn;

	private DbConnectionSettings dbSettings;
	private ServerConnectionSettings srvSettings = new ServerConnectionSettings();

	/**
	 *  Property change support for notifying the gui about new messages.
	 */
	private PropertyChangeSupport pSupport;

	private DbSearchResult dbSearchResult;

	private DenovoSearchResult denovoSearchResult;

	private SpecSimResult specSimResult;

	/**
	 * Flag denoting whether client is in viewer mode.
	 */
	private boolean viewer = false;

	/**
	 * The constructor for the client (private for singleton object).
	 * 
	 * @param name
	 */
	private Client() {
		pSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Returns a client singleton object.
	 * 
	 * @return client Client singleton object
	 */
	public static Client getInstance() {
		if (client == null) {
			client = new Client();
		}

		return client;
	}

	//End SQL for protein Databases****************************************************
	/**
	 * Sets the database connection.
	 * @throws SQLException 
	 */
	public void initDBConnection() throws SQLException {
		// Connection conn
		if (conn == null || !conn.isValid(0)) {
			// connect to database
			DBConfiguration dbconfig = new DBConfiguration("metaprot", ConnectionType.REMOTE, getDbSettings());
			this.conn = dbconfig.getConnection();
		}
	}

	/**
	 * Clears the database connection.
	 * @throws SQLException 
	 */
	public void closeDBConnection() throws SQLException {
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}

	/**
	 * Connects the client to the web service.
	 */
	public void connect() {
		WSPublisher.start(srvSettings.getHost(), srvSettings.getPort());

		service = new ServerImplService();
		server = service.getServerImplPort();

		// enable MTOM in client
		BindingProvider bp = (BindingProvider) server;

		// Connection timeout: 12 hours
		bp.getRequestContext().put("com.sun.xml.ws.connect.timeout", 12 * 60 * 1000);

		// Request timeout: 24 hours
		bp.getRequestContext().put("com.sun.xml.ws.request.timeout", 24 * 60 * 60 * 1000);

		SOAPBinding binding = (SOAPBinding) bp.getBinding();
		binding.setMTOMEnabled(true);

		// Start requesting
		RequestThread thread = new RequestThread();
		thread.start();
	}

	/**
	 * Requests the server for response.
	 */
	public void request() {
		final String message = receiveMessage();
		if (message != null && !message.isEmpty()) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					firePropertyChange("New Message", null, message);
				}
			});
		}
	}

	/**
	 * Send the message. 
	 * @param msg
	 */
	public String receiveMessage() {
		return server.sendMessage();
	}

	/**
	 * Returns the contents of the file in a byte array.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// Before converting to an int type, check to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
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
			throw new IOException("Could not completely read file " + file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	public void runSearches(List<String> filenames, SearchSettings settings) {
		for (int i = 0; i < filenames.size(); i++) {
			settings.getFilenames().add(filenames.get(i));
		}
		try {
			server.runSearches(settings);
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	/**
	 * Returns the current spectral similarity search result.
	 * @param expContent The experiment content.
	 * @return The current spectral similarity search result.
	 */
	public SpecSimResult getSpecSimResult(ExperimentContent expContent) {
		if (specSimResult == null) {
			retrieveSpecSimResult(expContent.getExperimentID());
		}
		return specSimResult;
	}

	/**
	 * Returns the result(s) of a spectral similarity search belonging to a particular experiment.
	 * @param experimentID The experiment's primary key.
	 */
	private void retrieveSpecSimResult(Long experimentID) {
		try {
			initDBConnection();
			
			specSimResult = SpecSearchHit.getAnnotations(experimentID, conn, pSupport);
			
			if (specSimResult.getScoreMatrixImage() != null) {
				File outputfile = new File("saved.png");
				ImageIO.write(specSimResult.getScoreMatrixImage(), "png", outputfile);
			}
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	/**
	 * Resets the current spectral similarity search result reference.
	 */
	public void clearSpecSimResult() {
		specSimResult = null;
	}

	/**
	 * Returns the result from the de-novo search.
	 * @param file The query file.
	 * @return DenovoSearchResult
	 * @throws SQLException 
	 */
	public DenovoSearchResult getDenovoSearchResult(ProjectContent projContent, ExperimentContent expContent) {
		if (denovoSearchResult == null) {
			retrieveDeNovoSearchResult(projContent, expContent);
		}
		return denovoSearchResult;
	}

	/**
	 * This method retrieves the de novo result from the database for a specific project and experiment.
	 * @param projContent The project content.
	 * @param expContent The experiment content.
	 */
	private void retrieveDeNovoSearchResult(ProjectContent projContent, ExperimentContent expContent) {
		try {
			// Initialize the connection.
			initDBConnection();

			// The protein hit set, containing all information about found proteins.
			denovoSearchResult = new DenovoSearchResult(projContent.getProjectTitle(), expContent.getExperimentTitle());

			// Set up progress monitoring
			firePropertyChange("new message", null, "QUERYING DE NOVO SEARCH HITS");
			firePropertyChange("resetall", 0L, 100L);
			firePropertyChange("indeterminate", false, true);

			// Iterate over query spectra and get the different identification result sets
			List<Searchspectrum> searchSpectra = Searchspectrum.findFromExperimentID(expContent.getExperimentID(), conn);

			long maxProgress = searchSpectra.size();
			long curProgress = 0;
			firePropertyChange("new message", null, "BUILDING RESULTS OBJECT");
			firePropertyChange("indeterminate", true, false);
			firePropertyChange("resetall", 0L, maxProgress);
			firePropertyChange("resetcur", 0L, maxProgress);


			// Iterate the search spectra.
			for (Searchspectrum searchSpectrum : searchSpectra) {

				long spectrumId = searchSpectrum.getSearchspectrumid();
				// List for the Pepnovo hits.
				List<Pepnovohit> hits = Pepnovohit.getHitsFromSpectrumID(spectrumId, conn);

				Spectrum spectrum = Spectrum.findFromSpectrumID(searchSpectrum.getFk_spectrumid(), conn);
				denovoSearchResult.addHitSet(spectrum.getTitle(), Long.valueOf(spectrumId), hits);
				firePropertyChange("progress", 0L, ++curProgress);

			}
			firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	/**
	 * Returns the current database search result.
	 * @return dbSearchResult The current database search result.
	 */
	public DbSearchResult getDbSearchResult() {
		return dbSearchResult;
	}
	/**
	 * Sets the current database search result
	 * @param dbSearchResult
	 */
	public void setDbSearchResult(DbSearchResult dbSearchResult) {
		this.dbSearchResult = dbSearchResult;
	}

	/**
	 * Returns the current database search result.
	 * @param projContent The project content.
	 * @param expContent The experiment content.
	 * @return The current database search result.
	 */
	public DbSearchResult getDbSearchResult(ProjectContent projContent, ExperimentContent expContent) {
		if (dbSearchResult == null) {
			retrieveDbSearchResult(projContent, expContent);
		}
		return dbSearchResult;
	}




	/**
	 * Returns the result(s) from the database search for a particular experiment.
	 * @param ProjectContent, ExperimentConten
	 * @return DbSearchResult
	 */
	private void retrieveDbSearchResult(ProjectContent projContent, ExperimentContent expContent) {
		retrieveDbSearchResult(projContent.getProjectTitle(), expContent.getExperimentTitle(), expContent.getExperimentID());
	}

	/**
	 * Returns the result(s) from the database search for a particular experiment.
	 * @param experimentName 
	 * @param projectname 
	 * @param ProjectID, ExperimentID
	 * @return DbSearchResult
	 */
	public void retrieveDbSearchResult(String projectName, String experimentName, long experimentID) {
		// Init the database connection.
		try {
			initDBConnection();

			// The protein hit set, containing all information about found proteins.
			// TODO: use fastaDB parameter properly
			dbSearchResult = new DbSearchResult(projectName, experimentName, "TODO");

			// Set up progress monitoring
			firePropertyChange("new message", null, "QUERYING DB SEARCH HITS");
			firePropertyChange("resetall", 0L, 100L);
			firePropertyChange("indeterminate", false, true);

			// Query database search hits and them to result object
			List<SearchHit> searchHits = SearchHitExtractor.findSearchHitsFromExperimentID(experimentID, conn);

			dbSearchResult.setTotalSpectrumCount(
					Searchspectrum.getSpectralCountFromExperimentID(experimentID, conn));

			Set<Long> searchSpectrumIDs = new TreeSet<Long>();
			Set<String> peptideSequences = new TreeSet<String>();
			int totalPeptides = 0;
//			int modifiedPeptides = 0;
			
			long maxProgress = searchHits.size();
			long curProgress = 0;
			firePropertyChange("new message", null, "BUILDING RESULTS OBJECT");
			firePropertyChange("indeterminate", true, false);
			firePropertyChange("resetall", 0L, maxProgress);
			firePropertyChange("resetcur", 0L, maxProgress);
			for (SearchHit searchHit : searchHits) {
				addProteinSearchHit(searchHit);
				
				searchSpectrumIDs.add(searchHit.getFk_searchspectrumid());
				String pepSeq = searchHit.getSequence();
//				if (!pepSeq.matches("^[A-Z]*$")) {
//					modifiedPeptides++;
//				}
				peptideSequences.add(pepSeq);
				
				firePropertyChange("progress", 0L, ++curProgress);
			}
			for (ProteinHit ph : dbSearchResult.getProteinHitList()) {
				totalPeptides += ph.getPeptideCount();
			}
			
			dbSearchResult.setIdentifiedSpectrumCount(searchSpectrumIDs.size());
			dbSearchResult.setTotalPeptideCount(totalPeptides);
			dbSearchResult.setUniquePeptideCount(peptideSequences.size());
			
			firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets the current database search result reference.
	 */
	public void clearDbSearchResult() {
		dbSearchResult = null;
	}

	/**
	 * This method converts a search hit into a protein hit and adds it to the current protein hit set.
	 * @param hit The search hit implementation.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	private void addProteinSearchHit(SearchHit hit) throws SQLException {

		// Create the PeptideSpectrumMatch
		PeptideSpectrumMatch psm = new PeptideSpectrumMatch(hit.getFk_searchspectrumid(), hit);

		// Get the peptide hit.
		PeptideAccessor peptide = PeptideAccessor.findFromID(hit.getFk_peptideid(), conn);
		PeptideHit peptideHit = new PeptideHit(peptide.getSequence(), psm);

		// Get the protein accessor.
		ProteinAccessor protein = ProteinAccessor.findFromID(hit.getFk_proteinid(), conn);

		// Add a new protein to the protein hit set.
		dbSearchResult.addProtein(new ProteinHit(protein.getAccession(), protein.getDescription(), protein.getSequence(), peptideHit));
	}

	/**
	 * Queries the database to retrieve a list of all possible candidates for 
	 * spectral comparison belonging to a specified experiment ID.
	 * @deprecated Not used anymore, server handles spectral comparison now. Feel free to delete.
	 * @param experimentID The experiment's ID.
	 * @return A list of candidates for spectral comparison.
	 * @throws SQLException
	 */
	@Deprecated
	public List<SpectralSearchCandidate> getCandidatesFromExperiment(long experimentID) throws SQLException {
		initDBConnection();
		return new SpectrumExtractor(conn).getCandidatesFromExperiment(experimentID);
	}

	/**
	 * Queries the database to retrieve a mapping of search spectrum IDs 
	 * to their respective spectrum file titles.
	 * @param matches A list of SpectrumMatch objects
	 * @return A map containing containing ID-title pairs.
	 * @throws SQLException
	 */
	public Map<Long, String> getSpectrumTitlesFromMatches(List<SpectrumMatch> matches) throws SQLException {
		initDBConnection();
		return new SpectrumExtractor(conn).getSpectrumTitlesFromMatches(matches);
	}

	/**
	 * Queries the database to retrieve a spectrum file belonging to a specific searchspectrum entry.
	 * @param searchspectrumID The primary key of the searchspectrum entry.
	 * @return The corresponding spectrum file object.
	 * @throws SQLException
	 */
	public MascotGenericFile getSpectrumBySearchSpectrumID(long searchspectrumID) throws SQLException {
		initDBConnection();
		return new SpectrumExtractor(conn).getSpectrumFromSearchSpectrumID(searchspectrumID);
	}

	/**
	 * Queries the database to retrieve a spectrum file belonging to a specific spectrum title.
	 * @param title The spectrum title.
	 * @return The corresponding spectrum file object.
	 * @throws SQLException
	 */
	public MascotGenericFile getSpectrumByTitle(String title) throws SQLException {
		initDBConnection();
		return Spectrum.getSpectrumFileFromTitle(title, conn);
	}

	/**
	 * Queries the database to retrieve a spectrum file belonging to a specific libspectrum entry.
	 * @param libspectrumID The primary key of the libspectrum entry.
	 * @return The corresponding spectrum file object.
	 * @throws SQLException
	 */
	public MascotGenericFile getSpectrumByLibSpectrumID(long libspectrumID) throws SQLException {
		initDBConnection();
		return new SpectrumExtractor(conn).getSpectrumFromLibSpectrumID(libspectrumID);
	}

	/**
	 * Convenience method to read a spectrum from the MGF file in the specified
	 * path at the specified byte position.
	 * @param pathname The pathname string pointing to the desired file.
	 * @param indexPos The byte position of the spectrum in the desired file.
	 * @return the desired spectrum
	 */
	public MascotGenericFile readSpectrumFromFile(String pathname, Long indexPos) {
		MascotGenericFile mgf = null;
		try {
			// TODO: maybe use only one single reader instance for all MGF parsing needs (file panel, results panel, etc.)
			MascotGenericFileReader reader = new MascotGenericFileReader(new File(pathname), LoadMode.NONE);
			mgf = reader.loadNthSpectrum(0, indexPos);
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
	 * @throws IOException 
	 */
	public List<String> packAndSend(long packageSize, CheckBoxTreeTable checkBoxTree, String filename) throws IOException {
		File file = null;
		List<String> filenames = new ArrayList<String>();
		FileOutputStream fos = null;
		CheckBoxTreeSelectionModel selectionModel = checkBoxTree.getCheckBoxTreeSelectionModel();
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
							server.uploadFile(file.getName(), getBytesFromFile(file));
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
			fos.close();
			server.uploadFile(file.getName(), getBytesFromFile(file));
		} else {
			throw new IOException("ERROR: No files selected.");
		}
		return filenames;
	}

	/**
	 * Queries the database to retrieve a list of spectrum files belonging to a specified experiment.
	 * @deprecated For spectral similarity tuning use only. Will be deleted at some point in the future. 
	 * @param experimentID The primary key of the experiment.
	 * @return A list of spectrum files.
	 * @throws Exception
	 */
	@Deprecated
	public List<MascotGenericFile> downloadSpectra(long experimentID) throws Exception {
		return new SpectrumExtractor(conn).downloadSpectra(experimentID);
	} 

	// Thread polling the server each second.
	class RequestThread extends Thread {		
		public void run() {
			while(true){
				try {
					Thread.sleep(1000);
					request();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
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
	 * Returns the current connection to the database.
	 * @return
	 * @throws SQLException 
	 */
	public Connection getConnection() throws SQLException {
		if (conn == null)
			initDBConnection();
		return conn;
	}

	/**
	 * Shuts down the JVM.
	 */
	public void exit() {
		try {
			closeDBConnection();
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
		System.exit(0);
	}

	/**
	 * Returns the DbConnectionSettings.
	 * @return dbSettings The DBConnectionSettings object.
	 */
	public DbConnectionSettings getDbSettings() {
		if (dbSettings == null) {
			dbSettings = new DbConnectionSettings();
		}
		return dbSettings;
	}

	/**
	 * Sets the DbConnectionSettings.
	 * @param dbSettings The DBConnectionSettings object.
	 */
	public void setDbSettings(DbConnectionSettings dbSettings) {
		this.dbSettings = dbSettings;
	}

	/**
	 * Returns the ServerConnectionSettings.
	 * @return dbSettings The ServerConnectionSettings object.
	 */
	public ServerConnectionSettings getServerSettings() {
		return srvSettings;
	}

	/**
	 * Sets the ServerConnectionSettings.
	 * @param srvSettings The ServerConnectionSettings object.
	 */
	public void setServerSettings(ServerConnectionSettings srvSettings) {
		this.srvSettings = srvSettings;
	}

	/**
	 * Returns whether the client is in viewer mode.
	 * @return <code>true</code> if in viewer mode, <code>false</code> otherwise.
	 */
	public boolean isViewer() {
		return this.viewer;
	}

	/**
	 * Sets the client's viewer mode property.
	 * @param viewer <code>true</code> if in viewer mode, <code>false</code> otherwise.
	 */
	public void setViewer(boolean viewer) {
		this.viewer = viewer;
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
			for (SpectrumMatch spectrumMatch : spectrumMatches) {
				spectrumMatch.setIndexPosition(mgfFile.length());
				MascotGenericFile mgf = Client.getInstance().getSpectrumBySearchSpectrumID(
						spectrumMatch.getSearchSpectrumID());
				mgf.writeToStream(fos);
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

}
