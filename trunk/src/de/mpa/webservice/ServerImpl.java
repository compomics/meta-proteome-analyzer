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
import de.mpa.job.instances.ConvertJob;
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


//Service Implementation Bean
@MTOM
@WebService(endpointInterface = "de.mpa.webservice.Server")
public class ServerImpl implements Server {	
	
	
	/**
     * Init the job logger.
     */
    protected static Logger log = Logger.getLogger(ServerImpl.class);
    
    /**
     * The message queue.
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
	 * Process a derived file.
	 * @param filename
	 * @throws Exception
	 */
	public void runDbSearch(String filename, DbSearchSettings params) {	
		File file = new File("/scratch/metaprot/data/transfer/" + filename);
		
		// Init the job manager
		final JobManager jobManager = new JobManager(msgQueue);
		
		// Get general parameters 
		String searchDB = params.getFastaFile();
		double fragIonTol = params.getFragmentIonTol();
		double precIonTol = params.getPrecursorIonTol();
		
		// X!Tandem job
		if(params.isXTandem()){
			XTandemJob xtandemJob = new XTandemJob(file, searchDB, fragIonTol, precIonTol, false, false);
			jobManager.addJob(xtandemJob);
		}
		
		// Omssa job
		if(params.isOmssa()){
			OmssaJob omssaJob = new OmssaJob(file, searchDB, fragIonTol, precIonTol, false, false);
			jobManager.addJob(omssaJob);
		}
		
		// Crux job
		if(params.isCrux()){
			ConvertJob convertJob = new ConvertJob(file);
			jobManager.addJob(convertJob);
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
		if(params.isInspect()){
			InspectJob inspectJob = new InspectJob(file, searchDB);			
			jobManager.addJob(inspectJob);			
			PostProcessorJob postProcessorJob = new PostProcessorJob(file, searchDB);			
			jobManager.addJob(postProcessorJob);			
		}
		jobManager.execute();
		jobManager.clear();
		
		// Get the filename map.
		Map<String, String> filenames = jobManager.getFilenames();
		DBManager dbManager = null;
		
		try {
			// 	DB Manager instance					
			dbManager = new DBManager();
			dbManager.storeSpectra(file);
			if(params.isXTandem()) dbManager.storeXTandemResults(filenames.get("X!TANDEM"));
			if(params.isOmssa()) dbManager.storeOmssaResults(filenames.get("OMSSA"));
			if(params.isCrux()) dbManager.storeCruxResults(filenames.get("CRUX"));
			if(params.isInspect()) dbManager.storeInspectResults(filenames.get("POST-PROCESSING JOB"));
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}
		msgQueue.add(new Message(null, "DBSEARCH FINISHED", new Date()));
	}
	
	/**
	 * Process the derived pepnovo file.
	 * @param filename
	 * @throws Exception
	 */
	public void runDenovoSearch(String filename, DenovoSearchSettings params) {	
		// The denovo file
		File file = new File("/scratch/metaprot/data/transfer/" + filename);
		
		// Init the job manager
		final JobManager jobManager = new JobManager(msgQueue);
		
		// Get general parameters
		PepnovoJob denovoJob = new PepnovoJob(file, params.getDnFragmentTolerance(), params.getDnNumSolutions());
		jobManager.addJob(denovoJob);
		jobManager.execute();
		jobManager.clear();
		
		// Get the filename map.
		Map<String, String> filenames = jobManager.getFilenames();
		DBManager dbManager = null;
		
		try {
			// 	DB Manager instance					
			dbManager = new DBManager();
			dbManager.storeSpectra(file);
			dbManager.storePepnovoResults(filenames.get("PEPNOVO"));
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (SQLException ex) {
			log.error(ex.getMessage());
		}
		msgQueue.add(new Message(null, "DENOVOSEARCH FINISHED", new Date()));
	}
	
	@Override		
	public String uploadFile(String filename,  @XmlMimeType("application/octet-stream") DataHandler data) {
 
		if(data != null){	
		    InputStream io;
			try {
				io = data.getInputStream();
				byte b[] = new byte[io.available()];  
			    io.read(b);
			    // TODO: Parameterize the path to the data folder!
			    File file = new File("/scratch/metaprot/data/transfer/" + filename);
			    
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
	
//	/**
//	 * This method copies the file
//	 * @param srFile
//	 * @param dtFile
//	 */
//	private static void copyfile(File f1, File f2) {
//		try {
//			InputStream in = new FileInputStream(f1);
//			OutputStream out = new FileOutputStream(f2);
//
//			byte[] buf = new byte[1024];
//			int len;
//			while ((len = in.read(buf)) > 0) {
//				out.write(buf, 0, len);
//			}
//			in.close();
//			out.close();
//		} catch (FileNotFoundException ex) {
//			System.exit(0);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

}
