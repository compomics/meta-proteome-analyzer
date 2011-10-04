package de.mpa.webservice;

import java.io.File;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface Server {
	
	 @WebMethod void receiveMessage(String msg);
	 
	 @WebMethod String sendMessage(String msg);
	 
	 // Download a file from the server
	 @WebMethod File downloadFile(String filename);
	 
	 // Upload file to server
	 @WebMethod String uploadFile(File file);
}
