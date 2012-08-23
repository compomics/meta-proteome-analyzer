package de.mpa.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class KeggMaps {
	/**
	 * Returns the KEGG pathway map.
	 * Source: http://www.genome.jp/kegg-bin/get_htext?org_name=br08901&query=&htext=br08901.keg...
	 * &filedir=&highlight=&option=&extend=&uploadfile=&format=&wrap=&length=&open=A1&close=&hier=#A1
	 * @return The Kegg pathwayList
	 */
	public static LinkedHashMap<String, String> getKeggPathwayList(String path) {

		LinkedHashMap<String, String> keggPathwayMap = new LinkedHashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String currentLine;
			while((currentLine = br.readLine()) != null){
				switch (currentLine.charAt(0)) {
				case 'A':
					keggPathwayMap.put("<html><i>" + currentLine.substring(1, currentLine.length()) + "</i></html>", "A");
					break;
				case 'B':
					keggPathwayMap.put(currentLine.substring(3, currentLine.length()), "B");
					break;
				case 'C':
					keggPathwayMap.put(currentLine.substring(5, currentLine.length()), "C");
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keggPathwayMap;
	}
	
	/**
	 * This method returns the map of all Kegg Taxons. Source: http://www.genome.jp/kegg-bin/get_htext?htext=...
	 * br08601.keg&filedir=files&format=-a&query=MJA+MFE+MVU+MFS+MIF+MIG+MMP+MMQ+MMX+MMZ+MMD+MAE+MVN+MVO+MOK+MAC+...
	 * MBA+MMA+MBU+MMH+MEV+MZH+MTP+MCJ+MHI+MHU+MLA+MEM+MBG+MPI+MBN+MPL+MPD+MEZ+MTH+MMG+MST+MSI+MRU+MEL+MEW+MFV+MKA+RCI&option=-a
	 * @param path of the text file (conf)
	 * @return linkedHashmap<String,String>keggTaxonomyMap
	 */
	public static LinkedHashMap<String, String> getKeggTaxonomie(String path){
		LinkedHashMap<String, String> keggTaxonomyMap = new LinkedHashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String currentLine;
			while((currentLine = br.readLine()) != null){
				switch (currentLine.charAt(0)) {
				case 'A':
					keggTaxonomyMap.put("<html><i>" + currentLine.substring(1, currentLine.length()) + "</i></html>", "A");
					break;
				case 'B':
					if (currentLine.length()>1) {// Check for empty rows
						keggTaxonomyMap.put("<html>" + currentLine.substring(3, currentLine.length()) + "</html>", "B");
					}
					break;
				case 'C':
					keggTaxonomyMap.put(currentLine.substring(5, currentLine.length()), "C");
					break;
				case 'D':
					keggTaxonomyMap.put(currentLine.substring(7, currentLine.length()), "D");
					break;
				case 'E':
					keggTaxonomyMap.put(currentLine.substring(9, currentLine.length()), "E");
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keggTaxonomyMap;
	}
}
