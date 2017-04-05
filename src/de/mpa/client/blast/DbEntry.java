package de.mpa.client.blast;

import java.util.List;

/**
 * Class which holds a database entry.
 * @author R. Heyer
 */
public class DbEntry {

	/**
	 * Identifier
	 */
	String identifier;
	
	/**
	 * Header
	 */
	String header;
	
	/**
	 * The amino acid sequence of a database entry
	 */
	String sequence;
	
	/**
	 * The type of DB_Type
	 */
	DbEntry.DB_Type type;

	/**
	 * A list with subheaders of a FASTA-file
	 */
	List<String> subHeaderList;

	/**
	 * Constructor for a database entry
	 * @param identifier. The identifier of the database entry.
	 * @param header. The header of a database entry.
	 * @param sequence. The amino acid sequence of the database entry.
	 */
	public DbEntry(String identifier, String header, DbEntry.DB_Type type2, List<String> subHeaderList){
		this.identifier 		= identifier;
		this.header 			= header;
		this.type 				= type2;
		this.subHeaderList 		= subHeaderList;
	}

	/**
	 * Gets the identifier of the database entry.
	 * @return The identifier of the database entry.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Gets the header of the database entry.
	 * @return The header of the database entry.
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Gets the sequence of the database entry.
	 * @return The sequence of the database entry.
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * Gets the list of subheaders (all Strings after the first resp. second "|")
	 * @return subheaders. The list of subheaders.
	 */
	public List<String> getSubHeader() {
		return subHeaderList;
	}

	/**
	 * Sets the protein sequence.
	 * @param sequence. The protein sequence
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * Gets the type of protein entry.
	 * @return. DB_Type. The type of the database entry.
	 */
	public DbEntry.DB_Type getType() {
		return this.type;
	}
	
	
	/**
	 * ENUM for DATABASE Types and Begins.
	 * @author R. Heyer
	 */
	public enum DB_Type {
		UNIPROTSPROT 	(">sp|"),
		UNIPROTTREMBL 	(">tr|"),
		NCBIGENBANK 	(">gi|"), 
		NCBIREFERENCE 	(">ref|"),
		METAGENOME1 	(">|"),
		METAGENOME2 	(">"),
		METAGENOME3 	(">mg|");

		/**
		 * String of header begin.
		 */
		final String dbStartFlag;

		/**
		 * Constructor for DB_TYPE
		 * @param dbStart
		 */
		DB_Type(String dbStart) {
            dbStartFlag = dbStart;
		}
	}

}
