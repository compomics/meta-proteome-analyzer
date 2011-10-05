package de.mpa.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

import org.apache.log4j.Logger;

import de.mpa.db.DBManager;
import de.mpa.job.JobManager;
import de.mpa.job.instances.PepnovoJob;


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
		System.out.println("Received message: " + msg);
		
	}
	
	@Override
	public String sendMessage(String msg) {
		System.out.println(msg);
		return "Hello, sending the message: " + msg;
	}

	@Override
	public File downloadFile(String filename) { 
		File file = new File("c:\\metaproteomics\\" + filename);		
		return file; 
	}
	
	/**
	 * Process a derived file.
	 * @param filename
	 * @throws Exception
	 */
	public void process(String filename) {	
		File file = new File("c:\\metaproteomics\\" + filename);
		DBManager dbManager = null;
		
		// Upload the spectra to the file server
		try {
			// 	STORE JOB					
			dbManager = new DBManager();
			dbManager.storeSpectra(file, "test", "uniprot", 0.5, 0.5, "Da");
			
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (SQLException ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		
		final JobManager jobManager = new JobManager();
		PepnovoJob pepnovoJob = new PepnovoJob(file, 0.5); 
		jobManager.addJob(pepnovoJob);
		
		//jobManager.addJob(new DeleteJob(file.getAbsolutePath()));
		
		try {
			dbManager.storePepnovoResults(pepnovoJob.getFilename());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		jobManager.execute();
	}
	
	@Override
	public String uploadFile(File file) {
 
		if(file != null){		
			File newFile = new File("c:\\metaproteomics\\" + file.getName());
			copyfile(file, newFile);
			log.info("Upload Successful: " + newFile.getAbsolutePath());
			return newFile.getAbsolutePath();
			
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
