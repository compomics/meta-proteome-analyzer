package de.mpa.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

import org.apache.log4j.Logger;

import de.mpa.client.DBSearchSettings;
import de.mpa.db.DBManager;
import de.mpa.job.JobManager;
import de.mpa.job.instances.CruxJob;
import de.mpa.job.instances.DeleteJob;
import de.mpa.job.instances.InspectJob;
import de.mpa.job.instances.OmssaJob;
import de.mpa.job.instances.PepnovoJob;
import de.mpa.job.instances.XTandemJob;


//Service Implementation Bean
@MTOM
@WebService(endpointInterface = "de.mpa.webservice.Server")
public class ServerImpl implements Server {	
	
	
	/**
     * Init the job logger.
     */
    protected static Logger log = Logger.getLogger(ServerImpl.class);

	@Override
	public void receiveMessage(String msg) {
		log.info("Received message: " + msg);
		
	}
	
	@Override
	public String sendMessage(String msg) {
		log.info(msg);
		return "Hello, sending the message: " + msg;
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
	public void process(String filename, DBSearchSettings params) {	
		File file = new File("/scratch/metaprot/data/transfer/" + filename);
		DBManager dbManager = null;
		
		// Upload the spectra to the file server
//		try {
//			// 	STORE JOB					
//			dbManager = new DBManager();
//			dbManager.storeSpectra(file, "test", "uniprot", 0.5, 0.5, "Da");
//			
//		} catch (IOException e) {
//			log.error(e.getMessage());
//			e.printStackTrace();
//		} catch (SQLException ex) {
//			log.error(ex.getMessage());
//			ex.printStackTrace();
//		}
		
		// Init the job manager
		final JobManager jobManager = new JobManager();
		
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
			CruxJob cruxJob = new CruxJob(file, searchDB);
			jobManager.addJob(cruxJob);
		}
		
		// Inspect job
		if(params.isInspect()){
			InspectJob inspectJob = new InspectJob(file, searchDB);
			jobManager.addJob(inspectJob);
		}
		
		// Pepnovo job		
//		PepnovoJob pepnovoJob = new PepnovoJob(file, 0.5); 
//		jobManager.addJob(pepnovoJob);
//		
//		jobManager.addJob(new DeleteJob(file.getAbsolutePath()));
//		
//		try {
//			dbManager.storePepnovoResults(pepnovoJob.getFilename());
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		jobManager.execute();
		jobManager.clear();
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
	
	/**
	 * This method copies the file
	 * @param srFile
	 * @param dtFile
	 */
	private static void copyfile(File f1, File f2) {
		try {
			InputStream in = new FileInputStream(f1);
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
