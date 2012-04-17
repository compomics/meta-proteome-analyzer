package de.mpa.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

import org.apache.log4j.Logger;

import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.client.settings.DbSearchSettings;
import de.mpa.client.settings.DenovoSearchSettings;
import de.mpa.client.settings.SearchSettings;
import de.mpa.client.settings.SpecSimSettings;
import de.mpa.db.DBManager;
import de.mpa.db.MapContainer;
import de.mpa.db.storager.SpectrumStorager;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.job.JobManager;
import de.mpa.job.SearchType;
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
    private Queue<Message> msgQueue = new ArrayDeque<Message>();

	/**
	 * The DBManager instance.
	 */
	private DBManager dbManager;

	/**
	 * The JobManager instance.
	 */
	private JobManager jobManager;
    
	@Override
	public void receiveMessage(String msg) {
		log.info("Received message: " + msg);
	}

    // TODO: rewrite message system using listeners or somesuch
	@Override
	public String sendMessage() {
		Message msg = msgQueue.poll();
		if(msg != null){
			String composedMessage;
			if(msg.getJob() != null){
				composedMessage = msg.getJob().getDescription() + " " + msg.getData();
			} else {
				composedMessage = msg.getData();
			}
			
			log.info(composedMessage);
			return composedMessage;
		}
		return "";
	}

	@Override
	public File downloadFile(String filename) { 
		File file = new File(filename);		
		return file; 
	}

	/**
	 * Run the database search file. 
	 * @param filename The spectrum filename.
	 * @param dbSearchSettings The database search settings.
	 * @throws Exception
	 */
	private void runDbSearch(String filename, DbSearchSettings dbSearchSettings) {	
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
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		MapContainer.FastaLoader = fastaLoader;
		
		jobManager = JobManager.getInstance();
		// X!Tandem job
		if(dbSearchSettings.isXTandem()){
			XTandemJob xtandemJob = new XTandemJob(file, searchDB, fragIonTol, precIonTol, false, SearchType.TARGET);
			jobManager.addJob(xtandemJob);
			// Decoy search only
			if(dbSearchSettings.isDecoy()){
				// The X!Tandem decoy search is done here.
				XTandemJob xtandemDecoyJob = new XTandemJob(file, searchDB, fragIonTol, precIonTol, false, SearchType.DECOY);
				jobManager.addJob(xtandemDecoyJob);

				// The score job evaluates X!Tandem target + decoy results.
				XTandemScoreJob xtandemScoreJob = new XTandemScoreJob(xtandemJob.getFilename(), xtandemDecoyJob.getFilename());
				jobManager.addJob(xtandemScoreJob);
			}
		}
		
		// Omssa job
		if(dbSearchSettings.isOmssa()){
			OmssaJob omssaJob = new OmssaJob(file, searchDB, fragIonTol, precIonTol, false, SearchType.TARGET);
			jobManager.addJob(omssaJob);
			
			// Condition if decoy search is done here.
			if(dbSearchSettings.isDecoy()){
				
				// The Omssa decoy search is done here.
				OmssaJob omssaDecoyJob = new OmssaJob(file, searchDB + JobConstants.SUFFIX_DECOY, fragIonTol, precIonTol, false, SearchType.DECOY);
				jobManager.addJob(omssaDecoyJob);
				
				// The score job evaluates Omssa target + decoy results.
				OmssaScoreJob omssaScoreJob = new OmssaScoreJob(omssaJob.getFilename(), omssaDecoyJob.getFilename());
				jobManager.addJob(omssaScoreJob);
			}
		}
		
		// Crux job
		if(dbSearchSettings.isCrux()){
			MS2FormatJob ms2FormatJob = new MS2FormatJob(file);
			jobManager.addJob(ms2FormatJob);
			CruxJob cruxJob = new CruxJob(file, searchDB);
			jobManager.addJob(cruxJob);
			PercolatorJob percolatorJob = new PercolatorJob(file, searchDB);
			jobManager.addJob(percolatorJob);
			String percolatorfile = JobConstants.CRUX_OUTPUT_PATH + file.getName().substring(0, file.getName().length() - 4) + "_percolated.txt";
			RenameJob renameJob = new RenameJob(JobConstants.CRUX_OUTPUT_PATH + "percolator.target.txt", percolatorfile);
			jobManager.addJob(renameJob);
		}
		
		// Inspect job
		if(dbSearchSettings.isInspect()){
			InspectJob inspectJob = new InspectJob(file, searchDB);			
			jobManager.addJob(inspectJob);			
			PostProcessorJob postProcessorJob = new PostProcessorJob(file, searchDB);			
			jobManager.addJob(postProcessorJob);			
		}
		
		// Execute the search engine jobs.
		jobManager.execute();
		
		msgQueue.add(new Message(null, "DBSEARCH FINISHED", new Date()));
		
		// Clear the folders
		// FIXME: Thilo, make this working!
//		jobManager.addJob(new DeleteJob());
//		jobManager.execute();
//		jobManager.clear();
//		msgQueue.add(new Message(null, "CLEARED FOLDERS", new Date()));
	}

	private List<SpectrumSpectrumMatch> runSpecSimSearch(List<MascotGenericFile> mgfList, SpecSimSettings sss) {
		SpecSimJob specSimJob = new SpecSimJob(mgfList, sss);
		jobManager.addJob(specSimJob);
		jobManager.execute();
		return specSimJob.getResults();
	}
	
	/**
	 * This method runs the denovo search on the server.
	 * @param filename The spectrum filename.
	 * @param dnSettings The denovo search settings.
	 * @throws Exception
	 */
	private void runDenovoSearch(String filename, DenovoSearchSettings dnSettings) {	
		// The denovo file
		File file = new File(ServerSettings.TRANSFER_PATH + filename);
		
		// Start the de-novo job with the de-novo search settings.
		PepnovoJob denovoJob = new PepnovoJob(file, dnSettings.getModel(), dnSettings.getPrecursorTol(), dnSettings.getFragMassTol(), dnSettings.getNumSolutions());
		jobManager.addJob(denovoJob);
		jobManager.execute();
	}
	
	/**
	 * This method transfers a file to the server webservice.
	 */
	@Override
	public String uploadFile(String filename,  @XmlMimeType("application/octet-stream") DataHandler data) {
 
		if(data != null){	
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
				
				log.info("Upload Successful: " + file.getName());
				return file.getAbsolutePath();
			} catch (IOException e) {
				e.printStackTrace();
			}      
		} 
		throw new WebServiceException("Upload Failed!"); 
	}

	@Override
	public void runSearches(String filename, SearchSettings settings) {
		try {
			// 	DB Manager instance
			dbManager = DBManager.getInstance();
			
			// Store the spectra.
			File file = new File(JobConstants.TRANSFER_PATH + filename);
			SpectrumStorager storager = dbManager.storeSpectra(file, settings.getExpID());
			
			// Initialize the job manager.
			jobManager = JobManager.getInstance();
			
			// Get the filename map.
			Map<String, String> filenames = jobManager.getFilenames();

			// Run searches and store results
			if (settings.isDatabase()) {
				DbSearchSettings dbss = settings.getDbss();
				
				// Run
				runDbSearch(filename, dbss);
				
				// Store
				if (dbss.isXTandem()) dbManager.storeXTandemResults(filenames.get("X!TANDEM TARGET SEARCH"), filenames.get("X!TANDEM QVALUES"));
				if (dbss.isOmssa()) dbManager.storeOmssaResults(filenames.get("OMSSA TARGET SEARCH"), filenames.get("OMSSA QVALUES"));
				if (dbss.isCrux()) dbManager.storeCruxResults(filenames.get("CRUX"));
				if (dbss.isInspect()) dbManager.storeInspectResults(filenames.get("POST-PROCESSING JOB"));
			}
			
			if (settings.isSpecSim()) {
				SpecSimSettings sss = settings.getSss();
				
				// Run
				List<SpectrumSpectrumMatch> results = runSpecSimSearch(storager.getSpectra(), sss);
				
				// Store
				dbManager.storeSpecSimResults(results);
				results = null;
			}
			
			if (settings.isDeNovo()) {
				DenovoSearchSettings dnss = settings.getDnss();
				
				// Run
				runDenovoSearch(filename, dnss);
				
				// Store
				dbManager.storePepnovoResults(filenames.get("PEPNOVO"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
		
	}
}
