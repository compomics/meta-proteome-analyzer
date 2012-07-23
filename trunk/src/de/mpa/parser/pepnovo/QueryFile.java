package de.mpa.parser.pepnovo;

import java.util.List;

/**
 * This class represents PepNovo+ BLAST query file.
 * @author T.Muth
 *
 */
public class QueryFile {
	
	/**
	 * The name of the PepNovo query output file.
	 */
	private String filename;
	
	/**
	 * The list of query entries.
	 */
	private List<QueryEntry> queryEntries;

	/**
	 * QueryFile gets filename and queryEntries as parameters.
	 * @param filename Query filename.
	 * @param queryEntries List of query entries. 
	 */
	public QueryFile(String filename, List<QueryEntry> queryEntries) {
		this.filename = filename;
		this.queryEntries = queryEntries;
	}
	
	/**
	 * Returns the query filename.
	 * @return query filename.
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * Returns the query entries.
	 * @return Query entries.
	 */
	public List<QueryEntry> getQueryEntries() {
		return queryEntries;
	}
}
