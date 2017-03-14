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
	private final long taxID;
	
	/**
	 * List of ontology keywords.
	 */
	private final List<String> keywords;
	
	/**
	 * List of E.C. numbers.
	 */
	private final List<String> ecNumbers;
	
	/**
	 * List of KEGG ontology numbers.
	 */
	private final List<String> koNumbers;
	
	/**
	 * UniRef100 Cluster ID.
	 */
	private final String uniRef100id;
	
	/**
	 * UniRef90 Cluster ID.
	 */
	private final String uniRef90id;
	
	/**
	 * UniRef50 Cluster ID.
	 */
	private final String uniRef50id;
	
	/**
	 * Default constructor for an UniprotEntry hit.
	 * @param taxID Taxonomy ID.
	 * @param keywords List of ontology keywords.
	 * @param ecNumbers List of E.C. numbers.
	 * @param koNumbers List of KEGG ontology numbers.
	 * @param uniRef100id UniRef100 cluster ID.
	 * @param uniRef90id UniRef900 cluster ID.
	 * @param uniRef50id UniRef50 cluster ID.
	 */
	public ReducedUniProtEntry(long taxID, String keywords, String ecNumbers, String koNumbers, String uniRef100id, String uniRef90id, String uniRef50id) {
		this.taxID = taxID;
		this.keywords = this.formatToList(keywords);
		this.ecNumbers = this.formatToList(ecNumbers);
		this.koNumbers = this.formatToList(koNumbers);
		this.uniRef100id = uniRef100id;
		this.uniRef90id = uniRef90id;
		this.uniRef50id = uniRef50id;
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
		return this.taxID;
	}
		
	/**
	 * Returns the keywords.
	 * @return Keyword list.
	 */
	public List<String> getKeywords() {
		return this.keywords;
	}
	
	/**
	 * Returns the EC numbers.
	 * @return EC number list.
	 */
	public List<String> getEcNumbers() {
		return this.ecNumbers;
	}
	
	/**
	 * Returns the KO number list.
	 * @return KO number list.
	 */
	public List<String> getKNumbers() {
		return this.koNumbers;
	}
	
	/**
	 * Returns the UniRef100 cluster ID.
	 * @return UniRef100 cluster ID.
	 */
	public String getUniRef100id() {
		return this.uniRef100id;
	}
	
	/**
	 * Returns the UniRef90 cluster ID.
	 * @return UniRef90 cluster ID.
	 */
	public String getUniRef90id() {
		return this.uniRef90id;
	}
	
	/**
	 * Returns the UniRef50 cluster ID.
	 * @return UniRef50 cluster ID.
	 */
	public String getUniRef50id() {
		return this.uniRef50id;
	}
}
