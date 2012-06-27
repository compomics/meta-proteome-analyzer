package de.mpa.webservice;

import java.io.File;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.bind.annotation.XmlMimeType;

import de.mpa.client.SearchSettings;

@WebService
@SOAPBinding(style = Style.RPC)
public interface Server {
	
	/**
	 * Receives a message from a client.
	 * @param msg The message string.
	 */
	 @WebMethod void receiveMessage(String msg);
	 
	 /**
	  * Sends a message to a requesting client.
	  * @return The message string.
	  */
	 @WebMethod String sendMessage();

	 /**
	  * Runs searches on the specified files with the specified settings.
	  * @param filenames Array of filenames associated with previously uploaded files.
	  * @param settings Settings object containing search parameters.
	  */
	 @WebMethod void runSearches(SearchSettings settings);
	 
	 /**
	  * Download a specified file from the server
	  * @param filename The name of the file to be downloaded.
	  * @return The file.
	  */
	 @WebMethod File downloadFile(String filename);
	 
	 /**
	  * Uploads a file to the server.
	  * @param filename The filename of the file to be uploaded.
	  * @param data The file's byte data.
	  * @return The file's absolute path on the server.
	  */
	 @WebMethod String uploadFile(String filename,  @XmlMimeType("application/octet-stream") DataHandler data);

}
