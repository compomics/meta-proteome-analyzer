package de.mpa.io.fasta.RobbiesFastaParser;

import java.util.List;

import de.mpa.io.fasta.RobbiesFastaParser.FastaParser.DB_Type;



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
	DB_Type type;
	
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
	public DbEntry(String identifier, String header, DB_Type type2, List<String> subHeaderList){
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
	public DB_Type getType() {
		return type;
	}
	
}
