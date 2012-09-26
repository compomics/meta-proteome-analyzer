package de.mpa.main;

import java.net.URISyntaxException;
import java.util.Map;

import de.mpa.analysis.KeggMaps;
import de.mpa.client.ui.SortableTreeNode;
import de.mpa.parser.ec.ECEntry;
import de.mpa.parser.ec.ECReader;

/**
 * This class initialize the parameters
 * @author heyer
 */
public class Parameters {

	public static Parameters instance;
	
	private Map<String, ECEntry> ecMap;

	private Map<String, Character> keggTaxonomyMap;
	private Map<String, Character> keggPathwayMap;
	
	private SortableTreeNode keggOrganismTreeRoot;
	private SortableTreeNode keggPathwayTreeRoot;

	private Parameters() {
		// TODO: Loading parameter dialog.
		try {
			initializeParameters();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
		ecMap = ECReader.readEC(
				getClass().getResourceAsStream("/de/mpa/resources/conf/ecReduced.xml"));
		
		// Initialize the KEGG pathway map
		keggPathwayMap = KeggMaps.readKeggPathways(
				getClass().getResourceAsStream("/de/mpa/resources/conf/keggPathways.txt"));
		// Initialize the KEGG taxonomy map
		keggTaxonomyMap = KeggMaps.readKeggOrganisms(
				getClass().getResourceAsStream("/de/mpa/resources/conf/keggTaxonomies.txt"));
	}
	
	/**
	 * Returns the array of enabled panels for the Viewer
	 * @return Array of enabled panels
	 */
	public String[] getEnabledItemsForViewer() {
		return new String[] { "View Results" };
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
