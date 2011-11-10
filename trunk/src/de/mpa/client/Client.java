package de.mpa.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import de.mpa.algorithms.LibrarySpectrum;
import de.mpa.algorithms.NormalizedDotProduct;
import de.mpa.algorithms.RankedLibrarySpectrum;
import de.mpa.db.DBConfiguration;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class Client {

	// Client instance
	private static Client client = null;
	
	// Server service
	private ServerImplService service;

	// Server instance
	private Server server;

	/**
	 * The constructor for the client.
	 * 
	 * @param name
	 */
	private Client() {

	}

	/**
	 * Returns a client singleton object.
	 * 
	 * @return client Client singleton object
	 */
	public static Client getInstance() {
		if (client == null) {
			client = new Client();
		}
		return client;
	}
	
	/**
	 * Connects the client to the web service.
	 */
	public void connect(){
		service = new ServerImplService();
		server = service.getServerImplPort();
		
		// enable MTOM in client
		BindingProvider bp = (BindingProvider) server;
		SOAPBinding binding = (SOAPBinding) bp.getBinding();
		binding.setMTOMEnabled(true);
	}
	
	/**
	 * Send the message. 
	 * @param msg
	 */
	public void sendMessage(String msg){
		server.sendMessage(msg);
	}
	
	/**
	 * Send multiple files to the server.
	 * @param files
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void sendFiles(File[] files) throws FileNotFoundException, IOException {
		
		// Number of files to send.
		int nFiles = files.length;
		
		for (int i = 0; i < nFiles; i++){			
			server.uploadFile(files[i].getName(), getBytesFromFile(files[i]));
		}
	}
	
	// Returns the contents of the file in a byte array.
	public static byte[] getBytesFromFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);

	    // Get the size of the file
	    long length = file.length();

	    // You cannot create an array using a long type.
	    // It needs to be an int type.
	    // Before converting to an int type, check
	    // to ensure that file is not larger than Integer.MAX_VALUE.
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }

	    // Create the byte array to hold the data
	    byte[] bytes = new byte[(int)length];

	    // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    // Ensure all the bytes have been read in
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }

	    // Close the input stream and return bytes
	    is.close();
	    return bytes;
	}
	
	/**
	 * Runs the database search.
	 * @param file
	 */
	public void runDbSearch(File file){
		server.process(file.getName(), "uniprot_sprot", 0.3, 0.5);
	}
	
	public HashMap<String, ArrayList<RankedLibrarySpectrum>> process(File file, ProcessSettings procSet){
		// init result map
		HashMap<String, ArrayList<RankedLibrarySpectrum>> resultMap = null;
		
		// connect to database
		DBConfiguration dbconfig = new DBConfiguration("metaprot");
		Connection conn = dbconfig.getConnection();
		
		// parse query file
		try {
			MascotGenericFileReader mgfReader = new MascotGenericFileReader(file);
			List<MascotGenericFile> mgfFiles = mgfReader.getSpectrumFiles();
			
			// store list of results in HashMap
			resultMap = new HashMap<String, ArrayList<RankedLibrarySpectrum>>(mgfFiles.size());

			// iterate over query spectra
			for (MascotGenericFile mgfQuery : mgfFiles) {
				double precursorMz = mgfQuery.getPrecursorMZ();
				
				// grab appropriate library spectra
				SpectrumExtractor specEx = new SpectrumExtractor(conn);
				List<LibrarySpectrum> libSpectra;
				if (procSet.getAnnotatedOnly()) {
					libSpectra = specEx.getLibrarySpectra(precursorMz, procSet.getTolMz());
				} else {
					libSpectra = specEx.getSpectra(precursorMz, procSet.getTolMz());
					// TODO: analyze score distribution of selected spectra, e.g. KopievonTest:76
				}
				
				// store results in list of Pairs
				ArrayList<RankedLibrarySpectrum> resultList = new ArrayList<RankedLibrarySpectrum>();
				
				// extract data from library spectrum objects
				for (LibrarySpectrum libSpec : libSpectra) {
					MascotGenericFile mgfLib = libSpec.getSpectrumFile();
					
					// dot prod
					int k = procSet.getK();
					k = Math.min(k, mgfQuery.getPeakList().size());
					k = Math.min(k, mgfLib.getPeakList().size());
					NormalizedDotProduct method = new NormalizedDotProduct(procSet.getThreshMz());
					method.compare(mgfQuery.getHighestPeaks(k), mgfLib.getHighestPeaks(k));
					double score = method.getSimilarity();
					
					// score threshold
					if (score >= procSet.getThreshSc()) {
						resultList.add(new RankedLibrarySpectrum(libSpec, score));
					}
				}
				resultMap.put(mgfQuery.getTitle(), resultList);
			}
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return resultMap;
				
//		server.process(filename);
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		// Get instance of the client.
		Client client = Client.getInstance();		
//		client.connect();
//		client.sendMessage("SEND MESSAGE!");
	}
}
