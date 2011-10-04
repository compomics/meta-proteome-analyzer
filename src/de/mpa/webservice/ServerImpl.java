package de.mpa.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;


//Service Implementation Bean
@MTOM
@WebService(endpointInterface = "de.mpa.webservice.Server")
public class ServerImpl implements Server {
	

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
 
	@Override
	public String uploadFile(File file) {
 
		if(file != null){		
			File newFile = new File("c:\\metaproteomics\\" + file.getName());
			copyfile(file, newFile);
			return newFile.getAbsolutePath();
			//return "Upload Successful: " + storedFile.getAbsolutePath();
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
