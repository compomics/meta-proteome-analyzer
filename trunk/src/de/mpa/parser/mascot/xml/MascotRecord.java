package de.mpa.parser.mascot.xml;

import java.util.ArrayList;
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
	private List<ProteinHit> proteinHits;
	private Map<String, ArrayList<PeptideHit>> pepMap = new HashMap<String, ArrayList<PeptideHit>>();
	
	private int numQueries = 0;

	// class methods
	public void addPepMapEntry(String key, PeptideHit value) {
		ArrayList<PeptideHit> hitList;
		if (pepMap.containsKey(key)) {
			// append new hit to existing ones if key already exists
			hitList = pepMap.get(key);
			// remove old map entry, replace with new list
			pepMap.remove(key);
		} else {
			hitList = new ArrayList<PeptideHit>();
		}
		hitList.add(value);
		pepMap.put(key, hitList);
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

	public List<ProteinHit> getProteinHits() {
		return proteinHits;
	}
	public void setProteinHits(List<ProteinHit> proteinHits) {
		this.proteinHits = proteinHits;
	}
	
	public Map<String, ArrayList<PeptideHit>> getPepMap() {
		return pepMap;
	}
	public void setPepMap(Map<String, ArrayList<PeptideHit>> pepMap) {
		this.pepMap = pepMap;
	}

	public int getNumQueries() {
		return numQueries;
	}
	public void setNumQueries(int numQueries) {
		this.numQueries = numQueries;
	}
}
