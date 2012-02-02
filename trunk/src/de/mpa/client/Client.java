package de.mpa.client;

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.log4j.Logger;

import de.mpa.algorithms.CrossCorrelation;
import de.mpa.algorithms.LibrarySpectrum;
import de.mpa.algorithms.Protein;
import de.mpa.algorithms.RankedLibrarySpectrum;
import de.mpa.client.model.DbSearchResult;
import de.mpa.client.model.DenovoSearchResult;
import de.mpa.client.model.PeptideHit;
import de.mpa.client.model.ProteinHit;
import de.mpa.client.model.ProteinHitSet;
import de.mpa.db.DBConfiguration;
import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Libspectrum;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.Pep2prot;
import de.mpa.db.accessor.Pepnovohit;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.Project;
import de.mpa.db.accessor.Property;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spec2pep;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class Client<fk_projectid> {

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
	
	public final DbConnectionSettings dbSettings = new DbConnectionSettings();
	
		//
	/**
     *  Property change support for notifying the gui about new messages.
     */
    private PropertyChangeSupport pSupport;

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
	
	/**
	 * Sets the database connection.
	 * @throws SQLException 
	 */
//	public void initDBConnection(DbConnectionSettings dbSettings) throws SQLException {
	public void initDBConnection() throws SQLException {
		// Connection conn
		if (conn == null) {
			// connect to database
			DBConfiguration dbconfig = new DBConfiguration("metaprot", false, this.dbSettings);
			this.conn = dbconfig.getConnection();
		}
	}
	
	/**
	 * Clears the database connection.
	 */
	public void clearDBConnection() {
		try {
			this.conn.close();
			this.conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connects the client to the web service.
	 */
	public void connect() {
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
	public void sendFiles(File[] files) throws FileNotFoundException, IOException {		
		// Send files iteratively
		for (int i = 0; i < files.length; i++){			
			server.uploadFile(files[i].getName(), getBytesFromFile(files[i]));
		}
	}
	
	// Returns the contents of the file in a byte array.
	public static byte[] getBytesFromFile(File file) throws IOException {
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
	
	/**
	 * Runs the database search.
	 * @param file
	 */
	public void runDbSearch(File file, DbSearchSettings settings){
		server.runDbSearch(file.getName(), settings);
	}
	
	/**
	 * gets properties from database
	 */
	
// modifiy project
	public void modifyProject (long projectid, String projectName) throws SQLException{
		Project tempProject = Project.findFromProjectID(projectid, conn);
		tempProject.setTitle(projectName);
		tempProject.setModificationdate(new Timestamp((new Date()).getTime()));
		tempProject.update(conn);
		}
//modify project property	
	public void modifyProjectProperty (long propertyid, String propertyName, String propertyValue) throws SQLException{
		Property tempProperty = Property.findPropertyFromPropertyID(propertyid, conn);
		tempProperty.setName(propertyName);
		tempProperty.setValue(propertyValue);
		tempProperty.update(conn);
		}
	
	//modify experimentsname	
		public void modifyExperimentsName (long experimentid, String experimentsName) throws SQLException{
			Experiment tempExperiment = Experiment.findExperimentByID(experimentid, conn);
			tempExperiment.setTitle(experimentsName);
			tempExperiment.setModificationdate(new Timestamp((new Date()).getTime()));
			tempExperiment.update(conn);
			}	
		//modify experimentproperty	
		public void modifyExperimentsProperties (long exppropertyid, String expProperty,String expPropertyValue) throws SQLException{
			ExpProperty tempExperimentProperty = ExpProperty.findExpPropertyFromID(exppropertyid,conn);
			tempExperimentProperty.setName(expProperty);
			tempExperimentProperty.setValue(expPropertyValue);
			tempExperimentProperty.update(conn);
		}			
		// create new Project
		public void createNewProject (String pTitle,Timestamp pCreationdate, Timestamp pModificationdate) throws SQLException{
			HashMap<Object, Object> data = new HashMap<Object, Object>(11);
			data.put(Project.TITLE, pTitle);
			data.put(Project.CREATIONDATE, pCreationdate);
			data.put(Project.MODIFICATIONDATE, pModificationdate);
			Project project = new Project(data);
			project.persist(conn);
			project.getGeneratedKeys();
		}
		
		
		

	//remove projects
	public void removeProjects(Long projectid)throws SQLException{
		//delete the entries experiment
//		List<Experiment> tempExperimentListe = Experiment.findAllExperimentsOfProject(projectid, conn);
//		if (tempExperimentListe.isEmpty()==false){
//		for (int i = 0; i < tempExperimentListe.size(); i++) {
//			Experiment tempExperiment=new Experiment();
//			tempExperiment=(Experiment)tempExperimentListe(i);
//			tempExperiment.delete(conn);
//		}
//		}
//		//delete the entries for project property
//		
//		List<Property> tempPropertyList = Property.findAllPropertiesOfProject(projectid, conn);
//		if (tempPropertyList.isEmpty()==false){
//		for (int i = 0; i < tempPropertyList.size(); i++) {
//			Property tempProperty = new Property();
//			tempProperty= (Property)tempExperimentListe(i);
//			tempProperty.delete(conn);
//		}}
		//delete entries for project
		Project tempproject = Project.findFromProjectID(projectid, conn);
		tempproject.delete(conn);

		
		
		//project.
	}
	

	public List<Property> getProjectProperties(long fk_projectid) throws SQLException{
		return Property.findAllPropertiesOfProject(fk_projectid, conn);
	}
	/**
	 *  get experiments from database 
	 */

	public List<Experiment> getProjectExperiments(long fk_projectid) throws SQLException{
		return Experiment.findAllExperimentsOfProject(fk_projectid, conn);
	}

	
	/**
	 *  get experiment property from database 
	 */
	public List<ExpProperty> getExperimentProperties(long experimentid) throws SQLException{
		return ExpProperty.findAllPropertiesOfExperiment(experimentid, conn);
	}
	
	/**
	 * gets projects from Database
	 */
	public List<Project> getProjects() throws SQLException{

		return Project.findAllProjects(conn);
	}
	
	/**
	 * Runs the denovo search.
	 * @param file
	 */
	public void runDenovoSearch(File file, DenovoSearchSettings settings){
		server.runDenovoSearch(file.getName(), settings);
	}
	
	/**
	 * Returns the result from the de-novo search.
	 * @param file The query file.
	 * @return DenovoSearchResult
	 */
	public DenovoSearchResult getDenovoSearchResult(File file){
		// Initialize the connection.
		try {
			initDBConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		DenovoSearchResult result = null;
		
		MascotGenericFileReader mgfReader;
		List<MascotGenericFile> mgfFiles = null;
		try {
			// Get the query spectra.
			mgfReader = new MascotGenericFileReader(file);
			mgfFiles = mgfReader.getSpectrumFiles();
			
			// Initialize the result set.
			result = new DenovoSearchResult();
			List<Searchspectrum> querySpectra = new ArrayList<Searchspectrum>();
			Map<String, List<Pepnovohit>> pepnovoResults = new HashMap<String, List<Pepnovohit>>();
			
			// Iterate over query spectra and get the different identification result sets
			for (MascotGenericFile mgf : mgfFiles) {
				Searchspectrum spectrum = Searchspectrum.findFromFilename(mgf.getFilename(), conn);
				querySpectra.add(spectrum);
				long spectrumID = spectrum.getSpectrumid();
				String spectrumname = spectrum.getSpectrumname();
				
				// Pepnovo
				List<Pepnovohit> pepnovoList = Pepnovohit.getHitsFromSpectrumID(spectrumID, conn);
				if(pepnovoList.size() > 0) {
					pepnovoResults.put(spectrumname, pepnovoList);
				}
				
			}
			
			// Set the results.
			result.setQuerySpectra(querySpectra);
			result.setPepnovoResults(pepnovoResults);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Returns the result(s) from the database search performed on the server.
	 * @param file The query file.
	 * @return DbSearchResult
	 */
	public DbSearchResult getDbSearchResult(File file){
		// Init the database connection.
		try {
			initDBConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		DbSearchResult result = null;
		
		MascotGenericFileReader mgfReader;
		List<MascotGenericFile> mgfFiles = null;
		try {
			// Get the query spectra.
			mgfReader = new MascotGenericFileReader(file);
			mgfFiles = mgfReader.getSpectrumFiles();
			
			// Initialize the result set.
			result = new DbSearchResult();
			List<Searchspectrum> querySpectra = new ArrayList<Searchspectrum>();
			Map<String, List<XTandemhit>> xTandemResults = new HashMap<String, List<XTandemhit>>();
			Map<String, List<Omssahit>> omssaResults = new HashMap<String, List<Omssahit>>();
			Map<String, List<Cruxhit>> cruxResults = new HashMap<String, List<Cruxhit>>();
			Map<String, List<Inspecthit>> inspectResults = new HashMap<String, List<Inspecthit>>();
			Map<String, Integer> voteMap = new HashMap<String, Integer>();
			List<ProteinHit> proteins = new ArrayList<ProteinHit>();
			
			// Iterate over query spectra and get the different identification result sets
			for (MascotGenericFile mgf : mgfFiles) {
				Searchspectrum spectrum = Searchspectrum.findFromFilename(mgf.getFilename(), conn);
				querySpectra.add(spectrum);
				long spectrumID = spectrum.getSpectrumid();
				
				String spectrumname = spectrum.getSpectrumname();
				int votes = 0;
				// X!Tandem
				List<XTandemhit> xtandemList = XTandemhit.getHitsFromSpectrumID(spectrumID, conn);
				if(xtandemList.size() > 0) {
					xTandemResults.put(spectrumname, xtandemList);
					for (XTandemhit hit : xtandemList) {
						ProteinHit protHit = new ProteinHit(hit.getAccession());
						List<PeptideAccessor> peptides = PeptideAccessor.findFromID(hit.getFk_peptideid(), conn);
						for (PeptideAccessor peptide : peptides) {
							PeptideHit pepHit = new PeptideHit(peptide.getSequence(), (int) hit.getStart(), (int) hit.getEnd());
							protHit.setPeptideHit(pepHit);
							proteins.add(protHit);
						}
					}
					votes++;
				}
				// Omssa
				List<Omssahit> omssaList = Omssahit.getHitsFromSpectrumID(spectrumID, conn);
				if(omssaList.size() > 0) {
					omssaResults.put(spectrumname, omssaList);
					for (Omssahit hit : omssaList) {
						ProteinHit protHit = new ProteinHit(hit.getAccession());
						List<PeptideAccessor> peptides = PeptideAccessor.findFromID(hit.getFk_peptideid(), conn);
						for (PeptideAccessor peptide : peptides) {
							PeptideHit pepHit = new PeptideHit(peptide.getSequence());
							protHit.setPeptideHit(pepHit);
							proteins.add(protHit);
						}
					}
					votes++;
				}
				// Crux
				List<Cruxhit> cruxList = Cruxhit.getHitsFromSpectrumID(spectrumID, conn);				
				if(cruxList.size() > 0) {
					cruxResults.put(spectrumname, cruxList);
					for (Cruxhit hit : cruxList) {
						ProteinHit protHit = new ProteinHit(hit.getAccession());
						List<PeptideAccessor> peptides = PeptideAccessor.findFromID(hit.getFk_peptideid(), conn);
						for (PeptideAccessor peptide : peptides) {
							PeptideHit pepHit = new PeptideHit(peptide.getSequence());
							protHit.setPeptideHit(pepHit);
							proteins.add(protHit);
						}
					}
					votes++;
				}
				// Inspect
				List<Inspecthit> inspectList = Inspecthit.getHitsFromSpectrumID(spectrumID, conn);				
				if(inspectList.size() > 0) {
					inspectResults.put(spectrumname, inspectList);
					for (Inspecthit hit : inspectList) {
						ProteinHit protHit = new ProteinHit(hit.getAccession());
						List<PeptideAccessor> peptides = PeptideAccessor.findFromID(hit.getFk_peptideid(), conn);
						for (PeptideAccessor peptide : peptides) {
							PeptideHit pepHit = new PeptideHit(peptide.getSequence());
							protHit.setPeptideHit(pepHit);
							proteins.add(protHit);
						}
					}
					votes++;
				}
				voteMap.put(spectrumname, votes);
			}
			
			// Set the results.
			result.setQuerySpectra(querySpectra);
			result.setxTandemResults(xTandemResults);
			result.setOmssaResults(omssaResults);
			result.setCruxResults(cruxResults);
			result.setInspectResults(inspectResults);
			result.setVoteMap(voteMap);
			result.setProteins(getAnnotatedProteins(proteins));
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Get the annotated protein list.
	 * @param proteinHits
	 * @return
	 * @throws SQLException
	 */
	private ProteinHitSet getAnnotatedProteins(List<ProteinHit> proteinHits) throws SQLException{
		ProteinHitSet proteins = new ProteinHitSet();
		for (ProteinHit proteinHit : proteinHits) {
			ProteinAccessor protein = ProteinAccessor.findFromAttributes(proteinHit.getAccession(), conn);
			proteinHit.setDescription(protein.getDescription());
			proteins.addProtein(proteinHit);
		}
		return proteins;
		
	}
	/**
	 * Process
	 * @param file
	 * @param procSet
	 * @return resultMap
	 */
	public HashMap<String, ArrayList<RankedLibrarySpectrum>> process(File file, ProcessSettings procSet) {
		// init result map
		HashMap<String, ArrayList<RankedLibrarySpectrum>> resultMap = null;
		
		// parse query file
		try {
			MascotGenericFileReader mgfReader = new MascotGenericFileReader(file);
			List<MascotGenericFile> mgfFiles = mgfReader.getSpectrumFiles();
			
			// store list of results in HashMap
			resultMap = new HashMap<String, ArrayList<RankedLibrarySpectrum>>(mgfFiles.size());

//			NormalizedDotProduct method = new NormalizedDotProduct(procSet.getThreshMz());
			CrossCorrelation method = new CrossCorrelation();

			SpectrumExtractor specEx = new SpectrumExtractor(conn);
			
			// iterate over query spectra
			for (MascotGenericFile mgfQuery : mgfFiles) {
				double precursorMz = mgfQuery.getPrecursorMZ();
				
				// store results in list of ranked library spectra objects
				ArrayList<RankedLibrarySpectrum> resultList = new ArrayList<RankedLibrarySpectrum>();
				
				if (procSet.getAnnotatedOnly()) {
					// grab appropriate library spectra (candidates for similarity scoring)
					// XXX
					List<Spec2pep> entries;
					if (procSet.getExpID() != 0L) {
						entries = Spec2pep.getEntriesWithinPrecursorRangeFromExperimentID(precursorMz, procSet.getTolMz(), procSet.getExpID(), conn);
					} else {
						entries = Spec2pep.getEntriesWithinPrecursorRange(precursorMz, procSet.getTolMz(), conn);
					}
					// iterate candidates, score & store
					Map<Long, ArrayList<RankedLibrarySpectrum>> pep2spec = new HashMap<Long, ArrayList<RankedLibrarySpectrum>>();
					
					// determine auto-correlation
					method.compare(mgfQuery.getPeaks(), mgfQuery.getPeaks());
					double autoScore = method.getSimilarity();
					
					for (Spec2pep entry : entries) {
						long spectrumID = entry.getFk_spectrumid();
						MascotGenericFile mgfLib = specEx.getUnzippedFile(spectrumID);
						
						// scoring
//						int k = procSet.getK();		// score k highest peaks
//						k = Math.min(k, mgfQuery.getPeakList().size());
//						k = Math.min(k, mgfLib.getPeakList().size());
//						method.compare(mgfQuery.getHighestPeaks(k), mgfLib.getHighestPeaks(k));
						method.compare(mgfQuery.getPeaks(), mgfLib.getPeaks());
//						method.compare(mgfQuery.getPeakList(), mgfLib.getPeakList());	// score everything
//						double score = method.getSimilarity();
						double score = method.getSimilarity() / autoScore;	// normalize using auto-correlation
						
						// score threshold
						if (score >= procSet.getThreshSc()) {
							long peptideID = entry.getFk_peptideid();
							ArrayList<RankedLibrarySpectrum> spectra = pep2spec.get(peptideID);
							if (spectra == null) {
								spectra = new ArrayList<RankedLibrarySpectrum>();
							}
							// append ranked spectrum with preliminary null values, which are to be replaced later on
							spectra.add(new RankedLibrarySpectrum(mgfLib, mgfLib.getPrecursorMZ(), null, null, score));
							pep2spec.put(peptideID, spectra);
						}
					}
					// iterate over found peptides to gather protein annotations
					for (long peptideID : pep2spec.keySet()) {
						// get list of proteins from list of peptides
						List<PeptideAccessor> peptides = PeptideAccessor.findFromID(peptideID, conn);
						for (PeptideAccessor peptide : peptides) {	// list should contain only a single peptide due to unique IDs
							ArrayList<Long> proteinIDs = (ArrayList<Long>) Pep2prot.findProteinIDsFromPeptideID(peptide.getPeptideid(), conn);
							List<Protein> annotations = new ArrayList<Protein>();
							// gather annotations
							for (Long proteinID : proteinIDs) {
								ProteinAccessor protein = ProteinAccessor.findFromID(proteinID, conn);
								annotations.add(new Protein(protein.getAccession(), protein.getDescription()));
							}
							// replace placeholder null values with actual data
							ArrayList<RankedLibrarySpectrum> spectra = pep2spec.get(peptideID);
							for (RankedLibrarySpectrum spectrum : spectra) {
								spectrum.setSequence(peptide.getSequence());
								spectrum.setAnnotations(annotations);
								resultList.add(spectrum);
							}
						}
					}
				} else {
					// grab appropriate library spectra (candidates for similarity scoring)
					List<Libspectrum> entries = Libspectrum.getEntriesWithinPrecursorRange(precursorMz, procSet.getTolMz(), conn);
					// iterate candidates, score & store
					Map<Long, ArrayList<RankedLibrarySpectrum>> pep2spec = new HashMap<Long, ArrayList<RankedLibrarySpectrum>>();
					for (Libspectrum entry : entries) {
						long spectrumID = entry.getLibspectrumid();
						MascotGenericFile mgfLib = specEx.getUnzippedFile(spectrumID);
						
						// dot prod
						int k = procSet.getK();
						k = Math.min(k, mgfQuery.getPeakList().size());
						k = Math.min(k, mgfLib.getPeakList().size());
						method.compare(mgfQuery.getHighestPeaksList(k), mgfLib.getHighestPeaksList(k));
						double score = method.getSimilarity();
//						if (score < 0.0) { score = 0.0; }
						
						// score threshold
						if (score >= procSet.getThreshSc()) {
							// check whether annotations exist, find peptides first
							List<PeptideAccessor> peptides = PeptideAccessor.findFromSpectrumID(spectrumID, conn);
							if (!peptides.isEmpty()) {
								for (PeptideAccessor peptide : peptides) {	// possibly multiple peptides per single spectrum
									long peptideID = peptide.getPeptideid();
									ArrayList<RankedLibrarySpectrum> spectra = pep2spec.get(peptideID);
									if (spectra == null) {
										spectra = new ArrayList<RankedLibrarySpectrum>();
									}
									// append ranked spectrum with preliminary null values, which are to be replaced later on
									spectra.add(new RankedLibrarySpectrum(mgfLib, mgfLib.getPrecursorMZ(), null, null, score));
									pep2spec.put(peptideID, spectra);
								}
							} else {
								// directly append ranked spectrum with null values to result list
								resultList.add(new RankedLibrarySpectrum(mgfLib, mgfLib.getPrecursorMZ(), null, null, score));
							}
						}
					}
					// iterate over found peptides to gather protein annotations
					for (Long peptideID : pep2spec.keySet()) {
						// get list of proteins from list of peptides
						List<PeptideAccessor> peptides = PeptideAccessor.findFromID(peptideID, conn);
						for (PeptideAccessor peptide : peptides) {	// list should contain only a single peptide due to unique IDs
							ArrayList<Long> proteinIDs = (ArrayList<Long>) Pep2prot.findProteinIDsFromPeptideID(peptide.getPeptideid(), conn);
							List<Protein> annotations = new ArrayList<Protein>();
							// gather annotations
							for (Long proteinID : proteinIDs) {
								ProteinAccessor protein = ProteinAccessor.findFromID(proteinID, conn);
								annotations.add(new Protein(protein.getAccession(), protein.getDescription()));
							}
							// replace placeholder null values with actual data
							ArrayList<RankedLibrarySpectrum> spectra = pep2spec.get(peptideID);
							for (RankedLibrarySpectrum spectrum : spectra) {
								spectrum.setSequence(peptide.getSequence());
								spectrum.setAnnotations(annotations);
								resultList.add(spectrum);
							}
						}
					}
					// TODO: analyze score distribution of selected spectra, e.g. KopievonTest:76
				}
				resultMap.put(mgfQuery.getTitle(), resultList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	// TBD
	public List<MascotGenericFile> downloadSpectra(long experimentID) throws Exception {
		List<MascotGenericFile> mgfList = new ArrayList<MascotGenericFile>();
		SpectrumExtractor specEx = new SpectrumExtractor(conn);
		List<LibrarySpectrum> libSpectra = specEx.getLibrarySpectra(experimentID);
		for (LibrarySpectrum libSpec : libSpectra) {
			MascotGenericFile mgf = libSpec.getSpectrumFile();
			mgf.setTitle(libSpec.getSequence() + " " + mgf.getTitle());
			mgfList.add(mgf);
		}
		return mgfList;
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
     * Adds the property change listener.
     * @param l
     */
    public void addPropertyChangeListener(PropertyChangeListener l ) { 
    	pSupport.addPropertyChangeListener(l); 
    }
}
