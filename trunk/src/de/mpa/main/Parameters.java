package de.mpa.main;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import de.mpa.analysis.KeggMaps;
import de.mpa.analysis.taxonomy.NcbiTaxonomy;
import de.mpa.client.ui.SortableTreeNode;
import de.mpa.io.parser.ec.ECEntry;
import de.mpa.io.parser.ec.ECReader;

/**
 * This class initialize the parameters
 * @author heyer
 */
public class Parameters {

	/**
	 * Instance
	 */
	public static Parameters instance;
	
	/**
	 *  Map of all EC.-numbers and descriptions
	 */
	private Map<String, ECEntry> ecMap;
	
	/**
	 * KEGG taxonomy map.
	 */
	private Map<String, Character> keggTaxonomyMap;
	
	/**
	 * KEGG pathway map.
	 */
	private Map<String, Character> keggPathwayMap;
	
	/**
	 * KEGG organism tree root node.
	 */
	private SortableTreeNode keggOrganismTreeRoot;
	
	/**
	 * KEGG pathway tree root node.
	 */
	private SortableTreeNode keggPathwayTreeRoot;

	/**
	 *  Map of all Uniprot peptides and the according accessions
	 */
	private Map<String, String[]> uniProtPeptideMap = null;
	
	/**
	 *  Map of all Uniprot accessions and the associated peptides
	 */
	private Map<String, String[]> uniProtAccMap = null;
	
	/**
	 * NcbiTaxonomy object. 
	 */
	private NcbiTaxonomy ncbiTaxonomy = null;
	
	/**
	 * Method gets the NCBI taxonomy maps.
	 * @return Ncbi tax maps.
	 */
	public NcbiTaxonomy getNcbiMaps() {
		if (ncbiTaxonomy == null) {
			try {
				InputStream fis = getClass().getResourceAsStream("/de/mpa/resources/conf/NcbiTaxonomy");
				ObjectInputStream ois  = new ObjectInputStream(fis);
				ncbiTaxonomy = (NcbiTaxonomy) ois.readObject();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return ncbiTaxonomy;
	}

	/**
	 * Private constructor for the parameters.
	 */
	private Parameters() {
		// TODO: Loading parameter dialog.
		try {
			initializeParameters();
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
	 * This method initialize the parameters
	 * @throws URISyntaxException 
	 */
	private void initializeParameters() throws URISyntaxException {
		// Initialize the EC-number map
		ecMap = ECReader.readEC(getClass().getResourceAsStream("/de/mpa/resources/conf/ecReduced.xml"));
		
		// Initialize the KEGG pathway map
		keggPathwayMap = KeggMaps.readKeggPathways(getClass().getResourceAsStream("/de/mpa/resources/conf/keggPathways.txt"));
		
		// Initialize the KEGG taxonomy map
		keggTaxonomyMap = KeggMaps.readKeggOrganisms(getClass().getResourceAsStream("/de/mpa/resources/conf/keggTaxonomies.txt"));
	}
	
	/**
	 * Returns the UniProt peptide map with the associated accessions
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

	/**
	 * This method returns the map of all KeggPathways
	 * @return keggPathwayMap. The map of all KeggPathways
	 */
	public Map<String, Character> getKeggPathwayMap() {
		return keggPathwayMap;
	}

	/**
	 * This method returns the map of all KeggTaxons
	 * @return keggTaxonomyMap
	 */
	public Map<String, Character> getKeggTaxonomyMap() {
		return keggTaxonomyMap;
	}
	
	/**
	 * Returns the KEGG organism tree root.
	 * @return the KEGG organism tree root.
	 */
	public SortableTreeNode getKeggOrganismTreeRoot() {
		return keggOrganismTreeRoot;
	}
	
	/**
	 * Returns the KEGG pathway tree root.
	 * @return the KEGG pathway tree root.
	 */
	public SortableTreeNode getKeggPathwayTreeRoot() {
		return keggPathwayTreeRoot;
	}
	
}
