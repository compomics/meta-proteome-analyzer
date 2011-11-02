package de.mpa.parser.mascot.xml;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Name:				RobbiesDomParser
 * Last changed:		02.11.2011
 * Author:				Robbie
 * Description:			object representing Mascot xml file
 */

public class MascotRecord {
	
	// class variables
	private String xmlFilename = "";
	private String uri = "";
	private String mascotFilename = "";
	private List<Proteinhit> proteinHits;
	private Map<String,PeptideHit> pepMap = new HashMap<String, PeptideHit>();
	
	private int numQueries = 0;

	// class methods
	public void addEntry(String key,PeptideHit value){
		pepMap.put(key, value);			
	}

	public String getXmlFilename() {
		return xmlFilename;
	}
	public void setXmlFilename(String xmlFilename) {
		this.xmlFilename = xmlFilename;
	}

	public String getURI() {
		return uri;
	}
	public void setURI(String uri) {
		this.uri = uri;
	}

	public String getMascotFilename() {
		return mascotFilename;
	}
	public void setMascotFilename(String mascotFilename) {
		this.mascotFilename = mascotFilename;
	}

	public List<Proteinhit> getProteinHits() {
		return proteinHits;
	}
	public void setProteinHits(List<Proteinhit> proteinHits) {
		this.proteinHits = proteinHits;
	}
	
	public Map<String, PeptideHit> getPepMap() {
		return pepMap;
	}
	public void setPepMap(Map<String, PeptideHit> pepMap) {
		this.pepMap = pepMap;
	}

	public int getNumQueries() {
		return numQueries;
	}
	public void setNumQueries(int numQueries) {
		this.numQueries = numQueries;
	}
}
