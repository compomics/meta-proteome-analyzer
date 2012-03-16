package de.mpa.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

import org.apache.log4j.Logger;

import de.mpa.client.DbSearchSettings;
import de.mpa.client.DenovoSearchSettings;
import de.mpa.db.DBManager;
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
    
    
	@Override
	public void receiveMessage(String msg) {
		log.info("Received message: " + msg);
	}
	
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
	public void runDbSearch(String filename, DbSearchSettings dbSearchSettings) {	
		File file = new File(ServerSettings.TRANSFER_PATH + filename);
		
		// Initialize the job manager.
		final JobManager jobManager = new JobManager(msgQueue);
		
		// Get general parameters .
		String searchDB = dbSearchSettings.getFastaFile();
		double fragIonTol = dbSearchSettings.getFragmentIonTol();
		double precIonTol = dbSearchSettings.getPrecursorIonTol();
		
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
		jobManager.clear();
		
		// Get the filename map.
		Map<String, String> filenames = jobManager.getFilenames();
		DBManager dbManager = null;
		
		try {
			// 	DB Manager instance					
			dbManager = new DBManager();
			
			// Store the spectra.
			dbManager.storeSpectra(file, dbSearchSettings.getExperimentid());
			
			// Store the results.
			if (dbSearchSettings.isXTandem()) dbManager.storeXTandemResults(filenames.get("X!TANDEM TARGET SEARCH"), filenames.get("X!TANDEM QVALUES"));
			if (dbSearchSettings.isOmssa()) dbManager.storeOmssaResults(filenames.get("OMSSA TARGET SEARCH"), filenames.get("OMSSA QVALUES"));
			if (dbSearchSettings.isCrux()) dbManager.storeCruxResults(filenames.get("CRUX"));
			if (dbSearchSettings.isInspect()) dbManager.storeInspectResults(filenames.get("POST-PROCESSING JOB"));
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}
		msgQueue.add(new Message(null, "DBSEARCH FINISHED", new Date()));
		
		// Clear the folders
//		jobManager.addJob(new DeleteJob());
//		jobManager.execute();
//		jobManager.clear();
//		msgQueue.add(new Message(null, "CLEARED FOLDERS", new Date()));
	}
	
	/**
	 * This method runs the denovo search on the server.
	 * @param filename The spectrum filename.
	 * @param denovoSearchSettings The denovo search settings.
	 * @throws Exception
	 */
	public void runDenovoSearch(String filename, DenovoSearchSettings denovoSearchSettings) {	
		// The denovo file
		File file = new File(ServerSettings.TRANSFER_PATH + filename);
		
		// Init the job manager
		final JobManager jobManager = new JobManager(msgQueue);
		
		// Get general parameters
		PepnovoJob denovoJob = new PepnovoJob(file, denovoSearchSettings.getDnFragmentTolerance(), denovoSearchSettings.getDnNumSolutions());
		jobManager.addJob(denovoJob);
		jobManager.execute();
		jobManager.clear();
		
		// Get the filename map.
		Map<String, String> filenames = jobManager.getFilenames();
		DBManager dbManager = null;
		
		try {
			// 	DB Manager instance					
			dbManager = new DBManager();
			dbManager.storeSpectra(file, denovoSearchSettings.getExperimentid());
			dbManager.storePepnovoResults(filenames.get("PEPNOVO"));
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}
		msgQueue.add(new Message(null, "DENOVOSEARCH FINISHED", new Date()));
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
}
