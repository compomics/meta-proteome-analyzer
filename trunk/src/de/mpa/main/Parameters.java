package de.mpa.main;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import de.mpa.client.Constants;
import de.mpa.io.parser.ec.ECEntry;
import de.mpa.io.parser.ec.ECReader;

/**
 * This class initializes the parameters.
 * @author heyer
 */
public class Parameters {

	/**
	 * Parameters instance.
	 */
	public static Parameters instance;
	
	/**
	 *  Map of all EC.-numbers and descriptions.
	 */
	private Map<String, ECEntry> ecMap;
	
	/**
	 *  Map of all Uniprot peptides and the according accessions
	 */
	private Map<String, String[]> uniProtPeptideMap = null;
	
	/**
	 *  Map of all Uniprot accessions and the associated peptides
	 */
	private Map<String, String[]> uniProtAccMap = null;


	/**
	 * Private constructor for the parameters.
	 */
	private Parameters() {
		try {
			this.initializeParameters();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to create instance.
	 * @return the Parameters.
	 */
	public static Parameters getInstance() {
		if (instance == null) {
			instance = new Parameters();
		}
		return instance;
	}
	
	/**
	 * This method initialize the parameters,
	 * @throws URISyntaxException 
	 */
	private void initializeParameters() throws URISyntaxException {
		// Initialize the EC-number map
		ecMap = ECReader.readXML(getClass().getResourceAsStream(
				Constants.CONFIGURATION_PATH + "ecReduced.xml"));
	}
	
	/**
	 * Returns the UniProt peptide map with the associated accessions.
	 * @return Treemap of all UniProt peptides
	 */
	@SuppressWarnings("unchecked")
	public TreeMap<String, String[]> getUniProtPeptideMap() {
		if (uniProtPeptideMap == null) {
			// Initialize UniProt peptide map
			try {
				InputStream fosP = getClass().getResourceAsStream("/de/mpa/resources/conf/UniProtMapOfAllPeptide");
				ObjectInputStream oosP = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(fosP)));
				uniProtPeptideMap = (TreeMap<String, String[]>) oosP.readObject();
				oosP.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return (TreeMap<String, String[]>) uniProtPeptideMap ;
	}
	
	/**
	 * Returns the UniProt accession map with the associated peptides
	 * @return Treemap of all UniProt accessions
	 */
	@SuppressWarnings("unchecked")
	public TreeMap<String, String[]> getUniProtAccMap() {
		if (uniProtAccMap == null) {
			// Initialize Uniprot accession map
			try {
				InputStream fosAcc = getClass().getResourceAsStream("/de/mpa/resources/conf/UniProtMapOfAllAccessions");
				ObjectInputStream oosAcc = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(fosAcc)));
				uniProtAccMap = ((Map<String, String[]>)oosAcc.readObject());
				oosAcc.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return (TreeMap<String, String[]>)uniProtAccMap  ;
	}

	/**
	 * Returns the array of enabled panels for the Viewer
	 * @return Array of enabled panels
	 */
	public String[] getEnabledItemsForViewer() {
		return new String[] { "View Results", "Logging" };
	}
	
	/**
	 * This method returns the EC-map
	 * @return Map<String, ECEntry>. Returns the ECMap
	 */
	public Map<String, ECEntry> getEcMap() {
		return ecMap;
	}

}
