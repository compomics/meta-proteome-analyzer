package de.mpa.io.parser.mascot.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a complete Mascot XML file
 * 
 * @author R. Heyer, A. Behne
 */
public class MascotRecord {
	
	/**
	 * The XML filename.
	 */
	private String xmlFilename = "";
	
	/**
	 * The link to the remote .dat file.
	 */
	private String uri = "";
	
	/**
	 * The filename of the input spectrum file.
	 */
	private String inputFilename = "";
	
	/**
	 * The list of protein hits.
	 */
	private List<MascotProteinHit> proteinHits;
	
	/**
	 * The map containing spectrum scan title-to-peptide list mappings
	 */
	private Map<String, List<MascotPeptideHit>> pepMap = new HashMap<String, List<MascotPeptideHit>>();
	
	/**
	 * The total number of annotated queries.
	 */
	private int numQueries;
	
	/**
	 * The global list of post-translational amino acid modifications.
	 */
	private List<MascotModification> modifications;

	/**
	 * Add peptide specified by spectrum scan title and peptide hit.
	 * @param scanTitle Spectrum scan title.
	 * @param peptideHit Mascot peptidehit
	 */
	public void addPeptide(String scanTitle, MascotPeptideHit peptideHit) {
		List<MascotPeptideHit> hitList = this.pepMap.get(scanTitle);
		if (hitList == null) {
			hitList = new ArrayList<MascotPeptideHit>();
		}
		hitList.add(peptideHit);
        this.pepMap.put(scanTitle, hitList);
	}

	public String getXmlFilename() {
		return this.xmlFilename;
	}
	public void setXmlFilename(String xmlFilename) {
		this.xmlFilename = xmlFilename;
	}

	public String getURI() {
		return this.uri;
	}
	public void setURI(String uri) {
		this.uri = uri;
	}

	public String getInputFilename() {
		return this.inputFilename;
	}
	public void setInputFilename(String mascotFilename) {
        inputFilename = mascotFilename;
	}

	public List<MascotProteinHit> getProteins() {
		return this.proteinHits;
	}
	public void setProteins(List<MascotProteinHit> proteinHits) {
		this.proteinHits = proteinHits;
	}
	
	public Map<String, List<MascotPeptideHit>> getPeptideMap() {
		return this.pepMap;
	}
	public void setPeptides(Map<String, List<MascotPeptideHit>> pepMap) {
		this.pepMap = pepMap;
	}

	public int getNumQueries() {
		return this.numQueries;
	}
	public void setNumQueries(int numQueries) {
		this.numQueries = numQueries;
	}

	public List<MascotModification> getModifications() {
		return this.modifications;
	}

	public void setModifications(List<MascotModification> modifications) {
		this.modifications = modifications;
	}
}
