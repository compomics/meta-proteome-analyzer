package de.mpa.client.model.dbsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a reduced model of an UniProtEntry.
 * @author T. Muth
 *
 */
public class ReducedUniProtEntry implements Serializable {
	
	/**
	 * Serialization ID set to default == 1L;
	 */
	private static final long serialVersionUID = 1L; 
	
	/**
	 * Taxonomy (NCBI) ID.
	 */
	private long taxID;
	
	/**
	 * List of ontology keywords.
	 */
	private List<String> keywords;
	
	/**
	 * List of E.C. numbers.
	 */
	private List<String> ecNumbers;
	
	/**
	 * List of KEGG ontology numbers.
	 */
	private List<String> koNumbers;
	
	/**
	 * Default constructor for an UniprotEntry hit.
	 * @param taxID Taxonomy ID.
	 * @param keywords List of ontology keywords.
	 * @param ecNumbers List of E.C. numbers.
	 * @param koNumbers List of KEGG ontology numbers.
	 */
	public ReducedUniProtEntry(long taxID, String keywords, String ecNumbers, String koNumbers) {
		this.taxID = taxID;
		this.keywords = formatToList(keywords);
		this.ecNumbers = formatToList(ecNumbers);
		this.koNumbers = formatToList(koNumbers);
	}
	
	/**
	 * Formats the concatenated string to list of string.
	 * @param concatenatedString
	 * @return List of strings.
	 */
	private List<String> formatToList(String concatenatedString) {
		List<String> stringList = new ArrayList<String>();
		String[] strings = concatenatedString.split(";");
		for (String string : strings) {
			if (string.length() > 0) stringList.add(string);
		}
		return stringList;
	}
	
	/**
	 * Returns the taxonomy ID.
	 * @return The taxonomy ID.
	 */
	public long getTaxID() {
		return taxID;
	}
		
	/**
	 * Returns the keywords.
	 * @return Keyword list.
	 */
	public List<String> getKeywords() {
		return keywords;
	}
	
	/**
	 * Returns the EC numbers.
	 * @return EC number list.
	 */
	public List<String> getEcNumbers() {
		return ecNumbers;
	}
	
	/**
	 * Returns the KO number list.
	 * @return KO number list.
	 */
	public List<String> getKNumbers() {
		return koNumbers;
	}
}
