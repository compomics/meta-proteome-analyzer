package de.mpa.io.fasta;

import java.util.List;

/**
 * Representation of FASTA database entry.
 */
public class DigFASTAEntry {

	/**
	 * ENUM for DATABASE Types and Begins.
	 * @author R. Heyer
	 */
	public enum Type {
		UNIPROTSPROT 	(">sp|"),
		UNIPROTTREMBL 	(">tr|"),
		NCBIGENBANK 	(">gi|"), 
//		NCBIREFERENCE 	(">ref|"),
		Database		(">DB|"),
		METAGENOME1 	(">generic|"),
		SILICO_PEPTIDE	(">pep|"),
		METAGENOME2 	(">|"),
		METAGENOME3 	(">"),
		METAGENOME4 	(">mg|");

		/**
		 * String of header begin.
		 */
		public String dbStartFlag;

		/**
		 * Constructor for DB_TYPE
		 * @param dbStart
		 */
		Type(String dbStart) {
            dbStartFlag = dbStart;
		}
	}
	
	/**
	 * Identifier. Can be a multiples for in-silico peptide.
	 */
	private final String identifier;

	/**
	 * Description of the protein entry
	 */
	private final String description;
	
	/**
	 * Header
	 */
	private final String header;
	
	/**
	 * Subheaders
	 */
	private final List<String> subHeaderList;
	
	/**
	 * The amino acid sequence of a database entry
	 */
	private final String sequence;
	
	/**
	 * The type of DB_Type
	 */
	private DigFASTAEntry.Type type;

	/**
	 * The type of DB_Type
	 */
	private long uniProtID = -1;

	/**

	 * @param identifier. The identifier of the database entry.
	 * @param header. The header of a database entry.
	 * @param sequence. The amino acid sequence of the database entry.
	 */
	/**
	 * Constructor for a database entry
	 * @param identifier. The identifier of the database entry.
	 * @param header. The header of a database entry.
	 * @param description. The description of the database entry.
	 * @param sequence. The sequence of the database entry.
	 * @param type. The type of the database entry
	 * @param subHeaderList. The subheaderlist of the database entry
	 */
	public DigFASTAEntry(String identifier, String header, String description, String sequence, DigFASTAEntry.Type type, List<String> subHeaderList){
		this.identifier 		= identifier;
		this.header 			= header;
		this.description		= description;
		this.sequence			= sequence;
		this.type 				= type;
		this.subHeaderList 		= subHeaderList;
	}

	/**
	 * Gets the list of subheaders (all Strings after the first resp. second "|")
	 * @return The list of subheaders.
	 */
	public List<String> getSubHeader() {
		return subHeaderList;
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
	 * Gets the type of protein entry.
	 * @return. DB_Type. The type of the database entry.
	 */
	public DigFASTAEntry.Type getType() {
		return this.type;
	}

	/**
	 * Gets the description of the fasta entry
	 * @return. The description. The description of the fasta entry
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Gets the uniProtID
	 * @return. The UniprotID
	 */
	public long getUniProtID() {
		return this.uniProtID;
	}

	/**
	 * Sets the uniProtID
	 * @param uniProtID
	 */
	public void setUniProtID(long uniProtID) {
		this.uniProtID = uniProtID;
	}
	
}
