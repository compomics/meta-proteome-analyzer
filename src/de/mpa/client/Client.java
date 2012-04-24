package de.mpa.client;

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreePath;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import de.mpa.algorithms.denovo.DenovoTag;
import de.mpa.algorithms.denovo.GappedPeptide;
import de.mpa.algorithms.denovo.GappedPeptideCombiner;
import de.mpa.client.model.ExperimentContent;
import de.mpa.client.model.ProjectContent;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.client.model.denovo.DenovoSearchResult;
import de.mpa.client.model.denovo.SpectrumHit;
import de.mpa.client.model.denovo.Tag;
import de.mpa.client.model.denovo.TagHit;
import de.mpa.client.model.specsim.SpecSimResult;
import de.mpa.client.model.specsim.SpectralSearchCandidate;
import de.mpa.client.settings.DbSearchSettings;
import de.mpa.client.settings.SearchSettings;
import de.mpa.client.settings.ServerConnectionSettings;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.panels.FilePanel;
import de.mpa.db.ConnectionType;
import de.mpa.db.DBConfiguration;
import de.mpa.db.DbConnectionSettings;
import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.Pepnovohit;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.SpecSearchHit;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.webservice.WSPublisher;

public class Client {

	// Client instance
	private static Client client = null;
	
	// Server service
	private ServerImplService service;
	
	// Logger
	private Logger log = Logger.getLogger(getClass());
	
	// Server instance
	private Server server;
	
	// Connection
	private Connection conn;

	private DbConnectionSettings dbSettings = new DbConnectionSettings();
	private ServerConnectionSettings srvSettings = new ServerConnectionSettings();

	// TODO: move methods
	public DbConnectionSettings getDbSettings() {
		return dbSettings;
	}

	public void setDbSettings(DbConnectionSettings dbSettings) {
		this.dbSettings = dbSettings;
	}
	
	public ServerConnectionSettings getServerSettings() {
		return srvSettings;
	}

	public void setServerSettings(ServerConnectionSettings srvSettings) {
		this.srvSettings = srvSettings;
	}

	//
	/**
     *  Property change support for notifying the gui about new messages.
     */
    private PropertyChangeSupport pSupport;

	private DbSearchResult dbSearchResult;

	private DenovoSearchResult denovoSearchResult;

	private SpecSimResult specSimResult;
	
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
		if (conn == null) {
			// connect to database
			DBConfiguration dbconfig = new DBConfiguration("metaprot", ConnectionType.REMOTE, this.dbSettings);
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
		SOAPBinding binding = (SOAPBinding) bp.getBinding();
		binding.setMTOMEnabled(true);
		
		// Start requesting
		RequestThread thread = new RequestThread();
		thread.start();
	}
	
	/**
	 * Requests the server for response.
	 */
	public void request(){
		final String message = receiveMessage();
		if(message != null && !message.equals("")){
			log.info(message);
			EventQueue.invokeLater(new Runnable() {                                                 
				public void run() {
					pSupport.firePropertyChange("New Message", null, message);                                                    
				}
			});
		}
	}
	
	/**
	 * Send the message. 
	 * @param msg
	 */
	public String receiveMessage(){
		return server.sendMessage();
	}
	
	/**
	 * Send multiple files to the server.
	 * @param files
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void sendFiles(List<File> files) throws FileNotFoundException, IOException {		
		// Send files iteratively
		for (int i = 0; i < files.size(); i++){			
			server.uploadFile(files.get(i).getName(), getBytesFromFile(files.get(i)));
		}
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
	
	public void runSearches(List<File> files, SearchSettings settings) {
		for (int i = 0; i < files.size(); i++) {
			try {
				server.runSearches(files.get(i).getName(), settings);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Runs the database search.
	 * @param file
	 */
	public void runDbSearch(List<File> files, DbSearchSettings settings){
		// Iterate the files
		for (int i = 0; i < files.size(); i++) {
//			server.runDbSearch(files.get(i).getName(), settings);
		}
	}
	
//	/**
//	 * Runs the de-novo search.
//	 * @param file
//	 */
//	public void runDenovoSearch(List<File> files, DenovoSearchSettings settings){
//		// Iterate the files
//		for (int i = 0; i < files.size(); i++){				
//			server.runDenovoSearch(files.get(i).getName(), settings);
//		}
//	}
	
	/**
	 * Returns the result from the de-novo search.
	 * @param file The query file.
	 * @return DenovoSearchResult
	 * @throws SQLException 
	 */
	public DenovoSearchResult getDenovoSearchResult(ProjectContent projContent, ExperimentContent expContent) {
		
		try {
			// Initialize the connection.
			initDBConnection();
			
			// The protein hit set, containing all information about found proteins.
			denovoSearchResult = new DenovoSearchResult(projContent.getProjectTitle(), expContent.getExperimentTitle());
			
			// Iterate over query spectra and get the different identification result sets
			List<Searchspectrum> searchSpectra = Searchspectrum.findFromExperimentID(expContent.getExperimentID(), conn);
			
			// Iterate the search spectra.
			for (Searchspectrum searchSpectrum : searchSpectra) {
				
				long searchSpectrumId = searchSpectrum.getSearchspectrumid();
				// List for the Pepnovo hits.
				List<Pepnovohit> pepnovoList = Pepnovohit.getHitsFromSpectrumID(searchSpectrumId, conn);
				
				// Fill the map with spectrum IDs as keys and pepnovo hits as values.
				if(pepnovoList.size() > 0) {
					
					// Get the spectrum title
					String spectrumTitle = Spectrum.findFromSpectrumID(searchSpectrum.getFk_spectrumid(), conn).getTitle();
					
					// Reduced pepnovo list
					List<Pepnovohit> reducedList = new ArrayList<Pepnovohit>();
					
					// The list of gapped peptides.
					List<GappedPeptide> gappedPeptides = new ArrayList<GappedPeptide>();
					for (Pepnovohit pepnovoHit : pepnovoList) {
						// Get the denovo-tag
						
						//if(pepnovoHit.getPnvscore().doubleValue() > 30){
							reducedList.add(pepnovoHit);
							DenovoTag denovoTag = new DenovoTag(pepnovoHit);
							gappedPeptides.add(new GappedPeptide(denovoTag.convertToGappedPeptideFormat()));
						//}
						
					}
					
					// Get the spectrum hit
					SpectrumHit spectrumHit = new SpectrumHit(searchSpectrumId, spectrumTitle, reducedList);
					
					// Construct the gapped peptide combiner object.
					GappedPeptideCombiner combiner = new GappedPeptideCombiner(gappedPeptides, 0.5);
					GappedPeptide peptide = combiner.getCombinedGappedPeptide();
					Tag tag = new Tag(peptide.getGappedSequence(), peptide.getFormattedSequence(), peptide.getTotalMass());
					
					// Add the tag hit.
					denovoSearchResult.addTagHit(new TagHit(tag, spectrumHit));
						
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return denovoSearchResult;
	}
	
	/**
	 * Returns the result(s) from the database search for a particular experiment.
	 * @param experimentid The experiment id
	 * @return DbSearchResult
	 */
	private void retrieveDbSearchResult(ProjectContent projContent, ExperimentContent expContent) {
		// Init the database connection.
		try {
			initDBConnection();
			
			// The protein hit set, containing all information about found proteins.
			dbSearchResult = new DbSearchResult(projContent.getProjectTitle(), expContent.getExperimentTitle(),  "EASTER EGG");
			
			// Iterate over query spectra and get the different identification result sets
			List<Searchspectrum> searchSpectra = Searchspectrum.findFromExperimentID(expContent.getExperimentID(), conn);
			
			//TODO: Get search date from run table
			Date searchDate = null;
			for (Searchspectrum searchSpectrum : searchSpectra) {
				
				long searchSpectrumId = searchSpectrum.getSearchspectrumid();
				
				// X!Tandem
				List<XTandemhit> xtandemList = XTandemhit.getHitsFromSpectrumID(searchSpectrumId, conn);
				if(xtandemList.size() > 0) {
					for (XTandemhit hit : xtandemList) {
						addProteinSearchHit(hit, SearchEngineType.XTANDEM);
					}
					
					// Set creation date
					if(searchDate == null){
						dbSearchResult.setSearchDate(xtandemList.get(0).getCreationdate());
					}
				}
				
				// Omssa
				List<Omssahit> omssaList = Omssahit.getHitsFromSpectrumID(searchSpectrumId, conn);
				if(omssaList.size() > 0) {
					for (Omssahit hit : omssaList) {
						addProteinSearchHit(hit, SearchEngineType.OMSSA);
					}
				}
				// Crux
				List<Cruxhit> cruxList = Cruxhit.getHitsFromSpectrumID(searchSpectrumId, conn);				
				if(cruxList.size() > 0) {
					for (Cruxhit hit : cruxList) {
						hit.getAccession();
						//addProteinSearchHit(hit, SearchEngineType.CRUX);
					}
				}
				// Inspect
				List<Inspecthit> inspectList = Inspecthit.getHitsFromSpectrumID(searchSpectrumId, conn);		
				if(inspectList.size() > 0) {
					for (Inspecthit hit : inspectList) {
						addProteinSearchHit(hit, SearchEngineType.INSPECT);
					}
				}
			}
		}  catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the current database search result.
	 * @param projContent The project content.
	 * @param expContent The experiment content.
	 * @return The current database search result.
	 */
	public DbSearchResult getDbSearchResult(ProjectContent projContent,ExperimentContent expContent) {
		if(dbSearchResult == null) {
			retrieveDbSearchResult(projContent, expContent);
		}
		return dbSearchResult;
	}
	
	/**
	 * Returns the current database search result.
	 * @return dbSearchResult The current database search result.
	 */
	public DbSearchResult getDbSearchResult() {
		return dbSearchResult;
	}
	
	
	/**
	 * This method converts a search hit into a protein hit and adds it to the current protein hit set.
	 * @param hit The search hit implementation.
	 * @throws SQLException when the retrieval did not succeed.
	 */
	private void addProteinSearchHit(SearchHit hit, SearchEngineType type) throws SQLException {
		
		// Create the PeptideSpectrumMatch
		// TODO: add the hit here!
		PeptideSpectrumMatch psm = new PeptideSpectrumMatch(hit.getFk_searchspectrumid(), hit, type);
		
		// Get the peptide hit.
		PeptideAccessor peptide = PeptideAccessor.findFromID(hit.getFk_peptideid(), conn);
		PeptideHit peptideHit = new PeptideHit(peptide.getSequence(), psm);
		
		// Get the protein accessor.
		ProteinAccessor protein = ProteinAccessor.findFromID(hit.getFk_proteinid(), conn);
		
		// Add a new protein to the protein hit set.
		dbSearchResult.addProtein(new ProteinHit(protein.getAccession(), protein.getDescription(), protein.getSequence(), peptideHit));
	}

	/**
	 * Returns the current spectral similarity search result.
	 * @param projContent The project content.
	 * @param expContent The experiment content.
	 * @return The current database search result.
	 */
	public SpecSimResult getSpecSimResult(ExperimentContent expContent) {
		if (specSimResult == null) {
			try {
				initDBConnection();
				specSimResult = SpecSearchHit.getAnnotations(expContent.getExperimentID(), conn);
			} catch (Exception e) {
				JXErrorPane.showDialog(e);
			}
		}
		return specSimResult;
	}

	/**
	 * TODO: API!
	 * @param experimentID
	 * @return
	 * @throws SQLException
	 */
	public List<SpectralSearchCandidate> getCandidatesFromExperiment(long experimentID) throws SQLException {
		initDBConnection();
		return new SpectrumExtractor(conn).getCandidatesFromExperiment(experimentID);
	}

	/**
	 * TODO: API :)
	 * @param idSet
	 * @return
	 * @throws SQLException
	 */
	public List<String> getSpectrumTitlesFromIDs(Set<Long> idSet) throws SQLException {
		initDBConnection();
		return new SpectrumExtractor(conn).getSpectrumTitlesFromIDs(idSet);
	}
	
	public MascotGenericFile getSpectrumFromSearchSpectrumID(long searchspectrumID) throws SQLException {
		initDBConnection();
		return new SpectrumExtractor(conn).getSpectrumFromSearchSpectrumID(searchspectrumID);
	}
	
	public MascotGenericFile getSpectrumFromLibSpectrumID(long libspectrumID) throws SQLException {
		initDBConnection();
		return new SpectrumExtractor(conn).getSpectrumFromLibSpectrumID(libspectrumID);
	}
	
//	/**
//	 * TODO: API!
//	 * @param file
//	 * @param procSet
//	 * @param processWorker 
//	 * @return resultMap
//	 */
//	public HashMap<String, ArrayList<RankedLibrarySpectrum>> searchSpecLib(File file, SpecSimSettings procSet) {
//		// declare result map
//		HashMap<String, ArrayList<RankedLibrarySpectrum>> resultMap = null;
//		
//		try {
//			// parse query file
//			MascotGenericFileReader mgfReader = new MascotGenericFileReader(file);
//			List<MascotGenericFile> mgfFiles = mgfReader.getSpectrumFiles();
//			
//			// store list of results in HashMap (with spectrum title as key)
//			resultMap = new HashMap<String, ArrayList<RankedLibrarySpectrum>>(mgfFiles.size());
//			
//			// iterate query spectra to gather precursor m/z values
//			ArrayList<Double> precursorMZs = new ArrayList<Double>(mgfFiles.size());
//			for (MascotGenericFile mgf : mgfFiles) {
//				precursorMZs.add(mgf.getPrecursorMZ());
//			}
//			Collections.sort(precursorMZs);
//			// build list of precursor m/z intervals using sorted list
//			ArrayList<Interval> intervals = new ArrayList<Interval>();
//			Interval current = null;
//			for (double precursorMz : precursorMZs) {
//				if (current == null) {	// first interval
//					current = new Interval(((precursorMz - procSet.getTolMz()) < 0.0) ? 0.0 : precursorMz - procSet.getTolMz(), precursorMz + procSet.getTolMz());
//					intervals.add(current);
//				} else {
//					// if left border of new interval intersects current interval extend the latter
//					if ((precursorMz - procSet.getTolMz()) < current.getRightBorder()) {
//						current.setRightBorder(precursorMz + procSet.getTolMz());
//					} else {	// generate new interval
//						current = new Interval(precursorMz - procSet.getTolMz(), precursorMz + procSet.getTolMz());
//						intervals.add(current);
//					}
//				}
//			}
//
//			// extract list of candidates
//			SpectrumExtractor specEx = new SpectrumExtractor(conn);
//			ArrayList<SpectralSearchCandidate> candidates = 
//				specEx.getCandidatesFromExperiment(intervals, procSet.getExperimentID());
//			
//			// iterate query spectra to determine similarity scores
////			int progress = 0;
//			for (MascotGenericFile mgfQuery : mgfFiles) {
//				
//				// store results in list of ranked library spectra objects
//				ArrayList<RankedLibrarySpectrum> resultList = new ArrayList<RankedLibrarySpectrum>();
//				
//				// prepare query spectrum for similarity comparison with candidate spectra,
//				// e.g. vectorize peaks, calculate auto-correlation, etc.
//				procSet.getSpecComparator().prepare(mgfQuery.getHighestPeaks(procSet.getPickCount()));
//				
//				// iterate candidates
//				for (SpectralSearchCandidate candidate : candidates) {
//					// re-check precursor tolerance criterion to determine proper candidates
//					if (Math.abs(mgfQuery.getPrecursorMZ() - candidate.getPrecursorMz()) < procSet.getTolMz()) {
//						// TODO: redundancy check in candidates (e.g. same spectrum from multiple peptide associations)
//						// score query and library spectra
//						procSet.getSpecComparator().compareTo(candidate.getPeaks());
//						double score = procSet.getSpecComparator().getSimilarity();
//						
//						// store result if score is above specified threshold
//						if (score >= procSet.getThreshScore()) {
//							// TODO: finish storage in RankedLibrarySpectrum objects, map everything to peptides and proteins
//							
//							// store peptide ID in map for annotation gathering later on
//							
//							// create MascotGenericFile from SpectralSearchCandidate object
//							MascotGenericFile mgfLib = new MascotGenericFile(null, candidate.getSpectrumTitle(), candidate.getPeaks(), candidate.getPrecursorMz(), candidate.getPrecursorCharge());
//							
//							resultList.add(new RankedLibrarySpectrum(mgfLib, candidate.getLibpectrumID(), candidate.getSequence(), null, score));
//						}
//					}
//				}
//				procSet.getSpecComparator().cleanup();
//				resultMap.put(mgfQuery.getTitle(), resultList);
//				pSupport.firePropertyChange("progressmade", 0, 1);
////				pSupport.firePropertyChange("progress", progress++, progress);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return resultMap;
//	}
	
	/**
	 * Method to consolidate spectra which are selected in a specified checkbox tree into spectrum packages of defined size.
	 * @param packageSize The amount of spectra per package.
	 * @param checkBoxTree The checkbox tree.
	 * @param listener An optional property change listener used to monitor progress.
	 * @return A list of files.
	 * @throws IOException 
	 */
	public List<File> packSpectra(int packageSize, CheckBoxTreeTable checkBoxTree, String filename) throws IOException {
		List<File> files = new ArrayList<File>();
		FileOutputStream fos = null;
		CheckBoxTreeSelectionModel selectionModel = checkBoxTree.getCheckBoxTreeSelectionModel();
		CheckBoxTreeTableNode fileRoot = (CheckBoxTreeTableNode) ((DefaultTreeTableModel) checkBoxTree.getTreeTableModel()).getRoot();
		int numSpectra = 0;
		CheckBoxTreeTableNode spectrumNode = fileRoot.getFirstLeaf();
		if (spectrumNode != fileRoot) {
			// iterate over all leaves
			while (spectrumNode != null) {
				// generate tree path and consult selection model whether path is explicitly or implicitly selected
				TreePath spectrumPath = spectrumNode.getPath();
				if (selectionModel.isPathSelected(spectrumPath, true)) {
					if ((numSpectra % packageSize) == 0) {			// create a new package every x files
						if (fos != null) {
							fos.close();
						}
						File file = new File(filename + (numSpectra/packageSize) + ".mgf");
						files.add(file);
						fos = new FileOutputStream(file);
					}
//					MascotGenericFile mgf = ((SpectrumTree)checkBoxTree.getTree()).getSpectrumAt(spectrumNode);
					MascotGenericFile mgf = FilePanel.getSpectrumForNode(spectrumNode);
					mgf.writeToStream(fos);
					fos.flush();
					try {
						pSupport.firePropertyChange("progress", numSpectra++, numSpectra);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				spectrumNode = spectrumNode.getNextLeaf();
			}
			fos.close();
		} else {
			throw new IOException("ERROR: No files selected.");
		}
		return files;
	}

	// XXX: TBD
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
     * Returns the current connection to the database.
     * @return
     * @throws SQLException 
     */
    public Connection getConnection() throws SQLException{
    	if(conn == null) initDBConnection();
    	return conn;
    }
}
