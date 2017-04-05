package de.mpa.fastadigest;

import java.io.BufferedWriter;
import java.io.IOException;
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
		NCBIREFERENCE 	(">ref|"),
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
	 * Constructor for a database entry
	 * @param identifier. The identifier of the database entry.
	 * @param header. The header of a database entry.
	 * @param sequence. The amino acid sequence of the database entry.
	 */
	public DigFASTAEntry(String identifier, String header, String sequence, DigFASTAEntry.Type type, List<String> subHeaderList){
		this.identifier 		= identifier;
		this.header 			= header;
		this.sequence			= sequence;
		this.type 				= type;
		this.subHeaderList 		= subHeaderList;
	}

	/**
	 * Write a FASTA database entry.
	 * @param bw. BufferedWriter that writes the data.
	 * @throws IOException
	 */
	public void writeEntry(BufferedWriter bw) throws IOException {

		// Keep database Format
		if (getType().equals(DigFASTAEntry.Type.UNIPROTSPROT)) {
			bw.write(getType().dbStartFlag + getIdentifier());
			bw.write("|" + getSubHeader().get(1));
		} else if (getType().equals(DigFASTAEntry.Type.SILICO_PEPTIDE)) {
			bw.write(">pep|"  + getIdentifier());
		} else {
			bw.write(">generic|"  + getIdentifier());
			bw.write("|"  + "Metagenome unknown");
		}

		// >generic_some_tag|proten_accession|a description for this protein

		bw.newLine();
		// Add sequence, but perform a line break after 58 chars (with line break 59-60)
		bw.append(sequence);
		bw.newLine();
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
	
}
