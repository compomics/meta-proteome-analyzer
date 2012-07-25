package de.mpa.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
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
import de.mpa.db.DBManager;
import de.mpa.db.MapContainer;
import de.mpa.db.storager.SpectrumStorager;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.job.Job;
import de.mpa.job.JobManager;
import de.mpa.job.JobStatus;
import de.mpa.job.SearchType;
import de.mpa.job.blast.BlastJob;
import de.mpa.job.instances.CommonJob;
import de.mpa.job.instances.CruxJob;
import de.mpa.job.instances.DeleteJob;
import de.mpa.job.instances.InspectJob;
import de.mpa.job.instances.JobConstants;
import de.mpa.job.instances.MS2FormatJob;
import de.mpa.job.instances.OmssaJob;
import de.mpa.job.instances.PepnovoJob;
import de.mpa.job.instances.PercolatorJob;
import de.mpa.job.instances.PostProcessorJob;
import de.mpa.job.instances.RenameJob;
import de.mpa.job.instances.SpecSimJob;
import de.mpa.job.instances.XTandemJob;
import de.mpa.job.scoring.OmssaScoreJob;
import de.mpa.job.scoring.XTandemScoreJob;
import de.mpa.job.storing.CruxStoreJob;
import de.mpa.job.storing.InspectStoreJob;
import de.mpa.job.storing.OmssaStoreJob;
import de.mpa.job.storing.SpecSimStoreJob;
import de.mpa.job.storing.XTandemStoreJob;


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
		
		// The FASTA loader
		FastaLoader fastaLoader = FastaLoader.getInstance();
		fastaLoader.setFastaFile(new File(JobConstants.FASTA_PATH + searchDB  + ".fasta"));

		try {
			fastaLoader.setIndexFile(new File(JobConstants.FASTA_PATH + searchDB  + ".fasta.fb"));
			fastaLoader.readIndexFile();
		} catch (Exception e) {
			log.error(e.getMessage(), e.getCause());
			e.printStackTrace();
		}
		MapContainer.FastaLoader = fastaLoader;
		
		// X!Tandem job
		if (dbSearchSettings.isXTandem()) {
			Job xTandemJob = new XTandemJob(file, searchDB, fragIonTol, precIonTol, false, SearchType.TARGET);
			jobManager.addJob(xTandemJob);
			// Decoy search only
			if (dbSearchSettings.isDecoy()) {
				// The X!Tandem decoy search is added here
				Job xTandemDecoyJob = new XTandemJob(file, searchDB, fragIonTol, precIonTol, false, SearchType.DECOY);
				jobManager.addJob(xTandemDecoyJob);

				// The score job evaluates X!Tandem target + decoy results
				Job xTandemScoreJob = new XTandemScoreJob(xTandemJob.getFilename(), xTandemDecoyJob.getFilename());
				jobManager.addJob(xTandemScoreJob);
				
				// Add store job
				jobManager.addJob(new XTandemStoreJob(xTandemJob.getFilename(), xTandemScoreJob.getFilename()));
			} else {
				// Add store job
				jobManager.addJob(new XTandemStoreJob(xTandemJob.getFilename(), null));
			}
		}
		
		// Omssa job
		if (dbSearchSettings.isOmssa()) {
			Job omssaJob = new OmssaJob(file, searchDB, fragIonTol, precIonTol, false, SearchType.TARGET);
			jobManager.addJob(omssaJob);
			
			// Condition if decoy search is done here
			if (dbSearchSettings.isDecoy()) {
				// The Omssa decoy search is added here
				Job omssaDecoyJob = new OmssaJob(file, searchDB + JobConstants.SUFFIX_DECOY, fragIonTol, precIonTol, false, SearchType.DECOY);
				jobManager.addJob(omssaDecoyJob);
				
				// The score job evaluates Omssa target + decoy results
				Job omssaScoreJob = new OmssaScoreJob(omssaJob.getFilename(), omssaDecoyJob.getFilename());
				jobManager.addJob(omssaScoreJob);
				
				// Add store job
				jobManager.addJob(new OmssaStoreJob(omssaJob.getFilename(), omssaScoreJob.getFilename()));
			} else {
				// Add store job
				jobManager.addJob(new OmssaStoreJob(omssaJob.getFilename(), null));
			}
		}
		
		// Crux job
		if (dbSearchSettings.isCrux()) {
			Job ms2FormatJob = new MS2FormatJob(file);
			jobManager.addJob(ms2FormatJob);
			Job cruxJob = new CruxJob(file, searchDB);
			jobManager.addJob(cruxJob);
			Job percolatorJob = new PercolatorJob(file, searchDB);
			jobManager.addJob(percolatorJob);
			String percolatorfile = JobConstants.CRUX_OUTPUT_PATH + file.getName().substring(0, file.getName().length() - 4) + "_percolated.txt";
			Job renameJob = new RenameJob(JobConstants.CRUX_OUTPUT_PATH + "percolator.target.txt", percolatorfile);
			jobManager.addJob(renameJob);
			jobManager.addJob(new CruxStoreJob(cruxJob.getFilename()));
		}
		
		// Inspect job
		if (dbSearchSettings.isInspect()) {
			Job inspectJob = new InspectJob(file, searchDB);			
			jobManager.addJob(inspectJob);			
			Job postProcessorJob = new PostProcessorJob(file, searchDB);			
			jobManager.addJob(postProcessorJob);			
			jobManager.addJob(new InspectStoreJob(postProcessorJob.getFilename()));
		}
		
	
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
		
		// Add a de novo search results storing job
		// TODO: Adapt this to new BLAST hit model.
		//jobManager.addJob(new PepnovoStoreJob(pepNovoJob.getFilename()));
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
			
			if(!runOptions.hasRunAlready()){
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
					
					// Batch-execute jobs
					Thread managerThread = new Thread(jobManager);
					managerThread.start();
					managerThread.join();
					
					msgQueue.add(new Message(new CommonJob(JobStatus.FINISHED, "BATCH SEARCH " + i + "/" + filenames.size()), new Date()), log);
					runOptions.setRunCount(1);
				}
				// Clear the folders
				jobManager.addJob(new DeleteJob());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e.getCause());
			e.printStackTrace();
		}
	}
	
}
