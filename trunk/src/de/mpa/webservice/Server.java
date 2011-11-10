package de.mpa.webservice;

import java.io.File;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.bind.annotation.XmlMimeType;

@WebService
@SOAPBinding(style = Style.RPC)
public interface Server {
	
	 @WebMethod void receiveMessage(String msg);
	 
	 @WebMethod String sendMessage(String msg);
	 
	 @WebMethod void process(String filename, String searchDB, double fragmentIonTol, double precursorTol);
	 
	 // Download a file from the server
	 @WebMethod File downloadFile(String filename);
	 
	 // Upload file to server
	 @WebMethod String uploadFile(String filename,  @XmlMimeType("application/octet-stream") DataHandler data);
}
