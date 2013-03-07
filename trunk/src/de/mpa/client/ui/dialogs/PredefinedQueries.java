package de.mpa.client.ui.dialogs;


/**
 * Contains the predefined queries for the graph database system.
 * @author Thilo Muth
 *
 */
public class PredefinedQueries {
	
	/**
	 * Get all unique peptides query.
	 */
	public static final String GETALLUNIQUEPEPTIDES = "Get All Unique Peptides()";
	
	/**
	 * Get all shared peptide query.
	 */
	public static final String GETALLSHAREDPEPTIDES = "Get All Shared Peptides()";
	
	/**
	 * Get all peptides for protein.
	 */
	public static final String GETPEPTIDESFORPROTEIN = "Get All Peptides For Protein With (Accession X)";
	
	/**
	 * Get all proteins for species.
	 */
	public static final String GETPROTEINSFORSPECIES = "Get All Proteins For Species With (Name X)";
	
	/**
	 * Get all peptides for species.
	 */
	public static final String GETPEPTIDESFORSPECIES = "Get All Peptides For Species With (Name X)";
	
	/**
	 * get all
	 */
	public static final String GETPROTEINSFORENZYME = "Get All Proteins For Enzyme With (EC-Number X)";
	
	
	
}
