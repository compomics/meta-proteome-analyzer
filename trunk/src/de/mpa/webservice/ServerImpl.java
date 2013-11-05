package de.mpa.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

import org.apache.log4j.Logger;

import de.mpa.client.DbSearchSettings;
import de.mpa.client.DenovoSearchSettings;
import de.mpa.client.SearchSettings;
import de.mpa.client.SpecSimSettings;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.db.DBManager;
import de.mpa.db.MapContainer;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.db.extractor.SpectrumUtilities;
import de.mpa.db.storager.SpectrumStorager;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.job.Job;
import de.mpa.job.JobManager;
import de.mpa.job.JobStatus;
import de.mpa.job.SearchType;
import de.mpa.job.blast.BlastJob;
import de.mpa.job.instances.CommonJob;
import de.mpa.job.instances.CruxJob;
import de.mpa.job.instances.InspectJob;
import de.mpa.job.instances.JobConstants;
import de.mpa.job.instances.MS2FormatJob;
import de.mpa.job.instances.OmssaJob;
import de.mpa.job.instances.PepnovoJob;
import de.mpa.job.instances.PercolatorJob;
import de.mpa.job.instances.PostProcessorJob;
import de.mpa.job.instances.RenameJob;
import de.mpa.job.instances.SpecSimJob;
import de.mpa.job.instances.SpecSimStoreJob;
import de.mpa.job.instances.StoreJob;
import de.mpa.job.instances.UniProtJob;
import de.mpa.job.instances.XTandemJob;
import de.mpa.job.scoring.OmssaScoreJob;
import de.mpa.job.scoring.XTandemScoreJob;


//Service Implementation Bean
@MTOM
@WebService(endpointInterface = "de.mpa.webservice.Server")
public class ServerImpl implements Server {	
		
	/**
     * Init the job logger.
     */
    protected static Logger log = Logger.getLogger(ServerImpl.class);
    
    /**
     * Message queue instance for communication between server and client.
     */
    private MessageQueue msgQueue = MessageQueue.getInstance();

	/**
	 * The DBManager instance.
	 */
	private DBManager dbManager;

	/**
	 * The JobManager instance.
	 */
	private JobManager jobManager;

	/**
	 * The RunOptions instance.
	 */
	private RunOptions runOptions;
    
	@Override
	public void receiveMessage(String msg) {
		log.info("Received message: " + msg);
	}

    // TODO: rewrite message system using listeners or somesuch
	@Override
	public String sendMessage() {
		String msg = msgQueue.poll();
		return (msg == null) ? "" : msg;
	}

	@Override
	public File downloadFile(String filename) { 
		File file = new File(filename);		
		return file; 
	}

	/**
	 * Adds database search jobs. 
	 * @param filename The spectrum filename.
	 * @param dbSearchSettings The database search settings.
	 * @throws Exception
	 */
	private void addDbSearchJobs(String filename, DbSearchSettings dbSearchSettings) {	
		File file = new File(ServerSettings.TRANSFER_PATH + filename);
		
		// Get general parameters .
		String searchDB = dbSearchSettings.getFastaFile();
		double fragIonTol = dbSearchSettings.getFragmentIonTol();
		double precIonTol = dbSearchSettings.getPrecursorIonTol();
		int nMissedCleavages = dbSearchSettings.getNumMissedCleavages();
		boolean isPrecIonTolPpm = dbSearchSettings.isPrecursorIonUnitPpm();
		
		// The FASTA loader
		FastaLoader fastaLoader = FastaLoader.getInstance();
		fastaLoader.setFastaFile(new File(JobConstants.FASTA_PATH + searchDB  + ".fasta"));

		try {
			File indexFile = new File(JobConstants.FASTA_PATH + searchDB  + ".fasta.fb");
			if(indexFile.exists()) {
				fastaLoader.setIndexFile(indexFile);
				fastaLoader.readIndexFile();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e.getCause());
			e.printStackTrace();
		}
		MapContainer.FastaLoader = fastaLoader;
		
		// Init protein map for UniProt entry retrieval.
		MapContainer.UniprotQueryProteins = new HashMap<String, Long>();
		
		// X!Tandem job
		if (dbSearchSettings.isXTandem()) {
			Job xTandemJob = new XTandemJob(file, searchDB, fragIonTol, precIonTol, nMissedCleavages, isPrecIonTolPpm, SearchType.TARGET);
			jobManager.addJob(xTandemJob);
			// Decoy search only
			if (dbSearchSettings.isDecoy()) {
				// The X!Tandem decoy search is added here
				Job xTandemDecoyJob = new XTandemJob(file, searchDB, fragIonTol, precIonTol, nMissedCleavages, isPrecIonTolPpm, SearchType.DECOY);
				jobManager.addJob(xTandemDecoyJob);

				// The score job evaluates X!Tandem target + decoy results
				Job xTandemScoreJob = new XTandemScoreJob(xTandemJob.getFilename(), xTandemDecoyJob.getFilename());
				jobManager.addJob(xTandemScoreJob);
				
				// Add store job
				jobManager.addJob(new StoreJob(SearchEngineType.XTANDEM, xTandemJob.getFilename(), xTandemScoreJob.getFilename()));
			} else {
				// Add store job
				jobManager.addJob(new StoreJob(SearchEngineType.XTANDEM, xTandemJob.getFilename()));
			}
		}
		
		// OMSSA job
		if (dbSearchSettings.isOmssa()) {
			Job omssaJob = new OmssaJob(file, searchDB, fragIonTol, precIonTol, nMissedCleavages, isPrecIonTolPpm, SearchType.TARGET);
			jobManager.addJob(omssaJob);
			
			// Condition if decoy search is done here
			if (dbSearchSettings.isDecoy()) {
				// The Omssa decoy search is added here
				Job omssaDecoyJob = new OmssaJob(file, searchDB + JobConstants.SUFFIX_DECOY, fragIonTol, precIonTol, nMissedCleavages, isPrecIonTolPpm, SearchType.DECOY);
				jobManager.addJob(omssaDecoyJob);
				
				// The score job evaluates Omssa target + decoy results
				Job omssaScoreJob = new OmssaScoreJob(omssaJob.getFilename(), omssaDecoyJob.getFilename());
				jobManager.addJob(omssaScoreJob);
				
				// Add store job
				jobManager.addJob(new StoreJob(SearchEngineType.OMSSA, omssaJob.getFilename(), omssaScoreJob.getFilename()));
			} else {
				// Add store job
				jobManager.addJob(new StoreJob(SearchEngineType.OMSSA, omssaJob.getFilename()));
			}
		}
		
		// Crux job
		if (dbSearchSettings.isCrux()) {
			Job cruxJob = new CruxJob(file, searchDB, precIonTol, isPrecIonTolPpm);
			jobManager.addJob(cruxJob);
			Job percolatorJob = new PercolatorJob(file);
			jobManager.addJob(percolatorJob);
			String percolatorfile = JobConstants.CRUX_OUTPUT_PATH + file.getName().substring(0, file.getName().length() - 4) + "_percolated.txt";
			Job renameJob = new RenameJob(JobConstants.CRUX_OUTPUT_PATH + "percolator.target.psms.txt", percolatorfile);
			jobManager.addJob(renameJob);
			jobManager.addJob(new StoreJob(SearchEngineType.CRUX, cruxJob.getFilename()));
		}
		
		// InsPecT job
		if (dbSearchSettings.isInspect()) {
			Job inspectJob = new InspectJob(file, searchDB, precIonTol, isPrecIonTolPpm, fragIonTol);			
			jobManager.addJob(inspectJob);			
			Job postProcessorJob = new PostProcessorJob(file, searchDB);			
			jobManager.addJob(postProcessorJob);			
			jobManager.addJob(new StoreJob(SearchEngineType.INSPECT, postProcessorJob.getFilename()));
		}
		jobManager.addJob(new UniProtJob());
	}

	private void addSpecSimSearchJob(List<MascotGenericFile> mgfList, SpecSimSettings sss) {
		SpecSimJob specSimJob = new SpecSimJob(mgfList, sss);
		jobManager.addJob(specSimJob);
		jobManager.addJob(new SpecSimStoreJob(specSimJob));
	}
	
	/**
	 * This method runs the de novo search on the server.
	 * @param filename The spectrum filename.
	 * @param dnSettings The de novo search settings.
	 * @throws Exception
	 */
	private void addDeNovoSearchJob(String filename, DenovoSearchSettings dnSettings) {	
		// The de novo file
		File file = new File(ServerSettings.TRANSFER_PATH + filename);

		// Add a de novo search job with the de novo search settings
		Job pepNovoJob = new PepnovoJob(file, dnSettings.getModel(), dnSettings.getPrecursorTol(), dnSettings.getFragMassTol(), dnSettings.getNumSolutions());
		jobManager.addJob(pepNovoJob);
		
		// Add BLAST search job
		Job blastJob = new BlastJob(file, "uniprot_sprot.fasta");
		jobManager.addJob(blastJob);
	}
	
	/**
	 * This method transfers a file to the server webservice.
	 */
	@Override
	public synchronized String uploadFile(String filename,  @XmlMimeType("application/octet-stream") DataHandler data) {
		if (data != null) {
			runOptions = RunOptions.getInstance();
			
		    InputStream io;
			try {
				
				io = data.getInputStream();
				byte b[] = new byte[io.available()];  
			    io.read(b);
			    
			    // The file will be put on the transfer file path
			    File file = new File(ServerSettings.TRANSFER_PATH + filename);
				FileOutputStream fos = new FileOutputStream(file);
				
				fos.write(b);
				
				// Close the streams.
				io.close();
				fos.close();
				runOptions.setRunCount(0);
				log.info("Upload Successful: " + file.getName());
				return file.getAbsolutePath();
			} catch (IOException e) {
				log.error(e.getMessage(), e.getCause());
				e.printStackTrace();
			}      
		} 
		throw new WebServiceException("Upload Failed!"); 
	}

	@Override
	public synchronized void runSearches(SearchSettings settings) {
		try {
			
			runOptions = RunOptions.getInstance();
			
			if (!runOptions.hasRunAlready()) {
				// DB Manager instance
				dbManager = DBManager.getInstance();
				
				// Initialize the job manager
				jobManager = JobManager.getInstance();
				List<String> filenames = settings.getFilenames();
				
				// Iterate uploaded files
				int i = 1;
				for (String filename : filenames) {
					// Store uploaded spectrum files to DB
					File file = new File(JobConstants.TRANSFER_PATH + filename);
					
					// Repair spectra
//					repairSpectra(file, dbManager.getConnection());
					
					SpectrumStorager storager = dbManager.storeSpectra(file, settings.getExpID());

					// Add search jobs to job manager queue
					if (settings.isDatabase()) {
						addDbSearchJobs(filename, settings.getDbss());
					}
					if (settings.isSpecSim()) {
						addSpecSimSearchJob(storager.getSpectra(), settings.getSss());
					}
					if (settings.isDeNovo()) {
						addDeNovoSearchJob(filename, settings.getDnss());
					}

					msgQueue.add(new Message(new CommonJob(JobStatus.RUNNING, "BATCH SEARCH " + i + "/" + filenames.size()), new Date()), log);
					jobManager.run();
					
					msgQueue.add(new Message(new CommonJob(JobStatus.FINISHED, "BATCH SEARCH " + i + "/" + filenames.size()), new Date()), log);
					i++;
					runOptions.setRunCount(1);
				}
				// Clear the folders
//				jobManager.addJob(new DeleteJob());
				jobManager.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Scans the specified spectrum file for dummy entries and replaces them 
	 * with contents fetched from the remote database.
	 * @param file The spectrum file
	 * @param conn The database connection
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void repairSpectra(File file, Connection conn) throws IOException, SQLException {
		repairSpectra(file, conn, file.getPath());
	}

	/**
	 * Scans the specified spectrum file for dummy entries, replaces them with
	 * contents fetched from the remote database and writes the result out to a
	 * file with the specified path.
	 * @param file The spectrum file.
	 * @param conn The database connection
	 * @param path The new file path
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void repairSpectra(File file, Connection conn, String path) throws IOException, SQLException {
		MascotGenericFileReader reader = new MascotGenericFileReader(file);
		List<MascotGenericFile> spectra = reader.getSpectrumFiles(true);
		
		List<Long> spectrumIDs = new ArrayList<Long>();
		Iterator<MascotGenericFile> iterator = spectra.iterator();
		while (iterator.hasNext()) {
			MascotGenericFile mgf = (MascotGenericFile) iterator.next();
			if (mgf.getSpectrumID() != null) {
				spectrumIDs.add(mgf.getSpectrumID());
				iterator.remove();
			}
		}
		if (!spectrumIDs.isEmpty()) {
			if (path.equals(file.getPath())) {
				// remove old mgf file
				file.delete();
			}
			file = new File(path);
			
			// download whole spectra for identified dummies
			SpectrumExtractor specEx = new SpectrumExtractor(conn);
			List<MascotGenericFile> newSpectra = specEx.getSpectraBySpectrumIDs(spectrumIDs);
			
			// write all spectra to new mgf file
			spectra.addAll(newSpectra);
			SpectrumUtilities.writeToFile(spectra, path);
		}
		reader.close();
	}
	
}
