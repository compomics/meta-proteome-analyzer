package de.mpa.webservice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

import org.apache.log4j.Logger;

import de.mpa.client.DbSearchSettings;
import de.mpa.client.SearchSettings;
import de.mpa.client.model.dbsearch.SearchEngineType;
import de.mpa.db.DBManager;
import de.mpa.db.MapContainer;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.db.extractor.SpectrumUtilities;
import de.mpa.db.job.Job;
import de.mpa.db.job.JobManager;
import de.mpa.db.job.JobStatus;
import de.mpa.db.job.SearchType;
import de.mpa.db.job.ServerProperties;
import de.mpa.db.job.instances.CommonJob;
import de.mpa.db.job.instances.DeleteJob;
import de.mpa.db.job.instances.OmssaJob;
import de.mpa.db.job.instances.StoreJob;
import de.mpa.db.job.instances.XTandemJob;
import de.mpa.db.job.scoring.OmssaScoreJob;
import de.mpa.db.job.scoring.XTandemScoreJob;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.io.fasta.PeptideDigester;
import de.mpa.util.PropertyLoader;


//Service Implementation Bean
@MTOM(enabled = true)
@WebService(endpointInterface = "de.mpa.webservice.Server")
public class ServerImpl implements Server {	
	/**
     * Init the job logger.
     */
    protected static Logger log = Logger.getLogger(ServerImpl.class);
    
    /**
     * Message queue instance for communication between server and client.
     */
    private final MessageQueue msgQueue = MessageQueue.getInstance();

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
	
	/**
	 * The webservice context.
	 */
	@Resource
    WebServiceContext wsctx;

    
	@Override
	public void receiveMessage(String msg) {
        ServerImpl.log.info("Received message: " + msg);
	}

	@Override
	public String sendMessage() {
		String msg = this.msgQueue.poll();
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
	@SuppressWarnings("static-access")
	private void addDbSearchJobs(String filename, DbSearchSettings dbSearchSettings) {	
		String pathTransfer = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + PropertyLoader.getProperty(PropertyLoader.PATH_TRANSFER);
		File file = new File(pathTransfer + filename);
		
		String searchDB = dbSearchSettings.getFastaFile();
		double fragIonTol = dbSearchSettings.getFragmentIonTol();
		double precIonTol = dbSearchSettings.getPrecursorIonTol();
		int nMissedCleavages = dbSearchSettings.getNumMissedCleavages();
		boolean isPrecIonTolPpm = dbSearchSettings.isPrecursorIonUnitPpm();
		
		// The FASTA loader
		FastaLoader fastaLoader = FastaLoader.getInstance();
		String pathFasta = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + PropertyLoader.getProperty(PropertyLoader.PATH_FASTA);
		fastaLoader.setFastaFile(new File(pathFasta + searchDB  + ".fasta"));
		
		// Check for additional peptide FASTA file and create if needed
		if (dbSearchSettings.isXTandem() || dbSearchSettings.isOmssa()) {
			if (dbSearchSettings.getPepDBFlag() && fastaLoader.getPepFile() == null) {
				PeptideDigester digester = new PeptideDigester();
				String fasta = fastaLoader.getFile().getAbsolutePath();
				String outFile = searchDB + ".pep";
				// If peptide FASTA is missing create it by Tryptic digestion of protein FASTA
				digester.createPeptidDB(fasta, outFile, 1, 5, 50);
				fastaLoader.setPepFile(new File(outFile));
			}
			// we need this file ...
			if (!dbSearchSettings.getPepDBFlag()) {
				fastaLoader.setPepFile(null);
			}
		}

		try {
			pathFasta = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + PropertyLoader.getProperty(PropertyLoader.PATH_FASTA);
			File indexFile = new File(pathFasta + searchDB  + ".fasta.fb");
			if(indexFile.exists()) {
				fastaLoader.setIndexFile(indexFile);
				fastaLoader.readIndexFile();
			}
		} catch (Exception e) {
            ServerImpl.log.error(e.getMessage(), e.getCause());
			e.printStackTrace();
		}
		MapContainer.FastaLoader = fastaLoader;
		
		// Setup the in-silico digested peptide database file
//		FastaLoader.getInstance().loadPepFile();
		
		// Init protein map for UniProt entry retrieval.
//		MapContainer.UniprotQueryProteins = new HashMap<String, Long>();
		
		// X!Tandem job
		if (dbSearchSettings.isXTandem()) {
			Job xTandemJob = new XTandemJob(file, searchDB, dbSearchSettings.getXtandemParams(), fragIonTol, precIonTol, nMissedCleavages, isPrecIonTolPpm, SearchType.TARGET);
            this.jobManager.addJob(xTandemJob);
			// Decoy search only
			if (dbSearchSettings.isDecoy()) {
				// The X!Tandem decoy search is added here
				Job xTandemDecoyJob = new XTandemJob(file, searchDB, dbSearchSettings.getXtandemParams(), fragIonTol, precIonTol, nMissedCleavages, isPrecIonTolPpm, SearchType.DECOY);
                this.jobManager.addJob(xTandemDecoyJob);

				// The score job evaluates X!Tandem target + decoy results
				Job xTandemScoreJob = new XTandemScoreJob(xTandemJob.getFilename(), xTandemDecoyJob.getFilename());
                this.jobManager.addJob(xTandemScoreJob);
				
				// Add store job
                this.jobManager.addJob(new StoreJob(SearchEngineType.XTANDEM, xTandemJob.getFilename(), xTandemScoreJob.getFilename()));
			} else {
				// Add store job
                this.jobManager.addJob(new StoreJob(SearchEngineType.XTANDEM, xTandemJob.getFilename()));
			}
			// Clear the folders
            this.jobManager.addJob(new DeleteJob(xTandemJob.getFilename()));
		}
		
		// OMSSA job
		if (dbSearchSettings.isOmssa()) {
			Job omssaJob = new OmssaJob(file, searchDB, dbSearchSettings.getOmssaParams(), fragIonTol, precIonTol, nMissedCleavages, isPrecIonTolPpm, SearchType.TARGET);
            this.jobManager.addJob(omssaJob);
			
			// Condition if decoy search is done here
			if (dbSearchSettings.isDecoy()) {
				// The Omssa decoy search is added here.
				Job omssaDecoyJob = new OmssaJob(file, searchDB + "_decoy", dbSearchSettings.getOmssaParams(), fragIonTol, precIonTol, nMissedCleavages, isPrecIonTolPpm, SearchType.DECOY);
                this.jobManager.addJob(omssaDecoyJob);
				
				// The score job evaluates Omssa target + decoy results.
				Job omssaScoreJob = new OmssaScoreJob(omssaJob.getFilename(), omssaDecoyJob.getFilename());
                this.jobManager.addJob(omssaScoreJob);
				
				// Add store job.
                this.jobManager.addJob(new StoreJob(SearchEngineType.OMSSA, omssaJob.getFilename(), omssaScoreJob.getFilename()));
			} else {
				// Add store job.
                this.jobManager.addJob(new StoreJob(SearchEngineType.OMSSA, omssaJob.getFilename()));
			}
			// Clear the folders
            this.jobManager.addJob(new DeleteJob(omssaJob.getFilename()));
		}
		
	}

//	/**
//	 * Adds spectral similarity searching and storing jobs to the job queue.
//	 * @param mgfList the list of spectrum files to search
//	 * @param sss the spectral similarity search settings
//	 */
//	private void addSpecSimSearchJob(List<MascotGenericFile> mgfList, SpecSimSettings sss) {
//		SpecSimJob specSimJob = new SpecSimJob(mgfList, sss);
//		jobManager.addJob(specSimJob);
//		jobManager.addJob(new SpecSimStoreJob(specSimJob));
//	}	

	
	/**
	 * This method transfers a file to the server webservice.
	 */
	@Override
	public synchronized String uploadFile(String filename,  byte[] bytes) {		
		String pathTransfer = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + PropertyLoader.getProperty(PropertyLoader.PATH_TRANSFER);  
		String filePath = pathTransfer + filename;
	        try {
	            FileOutputStream fos = new FileOutputStream(filePath);
	            BufferedOutputStream outputStream = new BufferedOutputStream(fos);
	            outputStream.write(bytes);
	            outputStream.close();
	        } catch (IOException ex) {
	            System.err.println(ex);
	            throw new WebServiceException(ex);
	        }
	        return filePath;
	}

	@Override
	public synchronized void runSearches(SearchSettings settings) {
		
		try {
            this.runOptions = RunOptions.getInstance();
			// TODO: has commenting this out any unforseen consequences?
//			if (!this.runOptions.hasRunAlready()) {
				// DB Manager instance
                this.dbManager = DBManager.getInstance();
				
				// Initialize the job manager
                this.jobManager = JobManager.getInstance();
				List<String> filenames = settings.getFilenames();
				
				// Iterate uploaded files
				int i = 1;
				for (String filename : filenames) {
					
					// Store uploaded spectrum files to DB
					String pathTransfer = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + PropertyLoader.getProperty(PropertyLoader.PATH_TRANSFER);
					File file = new File(pathTransfer + filename);
					
					// Repair spectra
//					repairSpectra(file, dbManager.getConnection());
					
                    this.dbManager.storeSpectra(file, settings.getExpID());

					// Add search jobs to job manager queue
					if (settings.isDatabase()) {
                        this.addDbSearchJobs(filename, settings.getDbss());
					}
//					if (settings.isSpecSim()) {
//						addSpecSimSearchJob(storager.getSpectra(), settings.getSss());
//					}

                    this.msgQueue.add(new Message(new CommonJob(JobStatus.RUNNING, "BATCH SEARCH " + i + "/" + filenames.size()), new Date()), ServerImpl.log);
                    this.jobManager.run();

                    this.msgQueue.add(new Message(new CommonJob(JobStatus.FINISHED, "BATCH SEARCH " + i + "/" + filenames.size()), new Date()), ServerImpl.log);
                    // hackish ...
                    this.runOptions.setRunCount(1);
                    i++;
				}
//                this.jobManager.run();
//			}
		} catch (Exception e) {
			e.printStackTrace();
            ServerImpl.log.error(e.getMessage(), e.getCause());
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
        ServerImpl.repairSpectra(file, conn, file.getPath());
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
			MascotGenericFile mgf = iterator.next();
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
