package de.mpa.client;

import java.util.Enumeration;

import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.mpa.analysis.KeggMaps;
import de.mpa.client.ui.ExtensionFileFilter;
import de.mpa.io.MascotGenericFile;

/**
 * Class providing general constants and methods used throughout the whole
 * application.
 * 
 * @author T. Muth, A. Behne, R. Heyer
 */
public class Constants {

	/**
	 * The application title.
	 */
	public final static String APPTITLE = "MetaProteomeAnalyzer";

	/**
	 * The application version number.
	 */
	public final static String VER_NUMBER = "0.6.3";
	
	/**
	 * The client frame minimum width in pixels.
	 */
	public static final int MAINFRAME_WIDTH = 1280;
	
	/**
	 * The client frame minimum height in pixels.
	 */
	public static final int MAINFRAME_HEIGHT = 800;
	
	/**
	 * The names of FASTA database files available for searches.
	 */
	public static final String[] FASTA_DB = { "uniprot_sprot", "uniprot_trembl", "uniprot_canis", "ncbi_canis", "uniprot_archaea", "ncbi_archaea", "uniprot_sprot_bacteria", "uniprot_sprot_archaea", "human_gut", "metadb_potsdam", "uniprot_methylcoenzyme"};
	
	/**
	 * The names of cleaving enzymes available for database searches.
	 */
	public static final String[] DB_ENZYMES = { "Trypsin", "Semi-tryptic"};
	
	/**
	 * The names of cleaving enzymes available for de novo searches.
	 */
	public static final String[] DN_ENZYMES = { "Trypsin", "Unspecific" };
	
	/**
	 * Entities for the graph query dialog (Compound section: First parameter after GET).
	 */
	public static final String[] QUERY_ENTITIES_GET = { "Proteins", "Peptides", "Unique Peptides", "Shared Peptides", "PSMs"};
	
	/**
	 * Entities for the graph query dialog (Compound section: Second parameter after BY).
	 */
	public static final String[] QUERY_ENTITIES_BY = {  "Protein Accession",
														"Protein Sequence",
														"Peptide Sequence", 
														"Species", 
														"Enzyme",
														"Pathway",
														"Biological Process", 
														"Molecular Function", 
														"Cellullar Component"};
	
	/**
	 * The names of fragmentation models available to the PepNovo+ algorithm.
	 */
	public static final String[] DN_MODELS = { "CID_IT_TRYP",
			"CID_IT_TRYP_CSP", "CID_IT_TRYP_DB", "CID_IT_TRYP_DNVPART",
			"CID_IT_TRYP_SCORE", "CID_IT_TRYP_TAG3", "CID_IT_TRYP_TAG4",
			"CID_IT_TRYP_TAG5", "CID_IT_TRYP_TAG6", "DBC4_PEAK", "ITDNV_PEAK",
			"LTQ_COMP" };

	/**
	 * An MGF file that is empty save for two peaks which serve to auto-generate
	 * default bounds in a spectrum viewer panel.
	 */
	public static final MascotGenericFile EMPTY_MGF = new MascotGenericFile(
			"empty", ("BEGIN IONS\n" + "0.0\t999.0\n" + "90.909\t0.0\n"
					+ "END IONS"));

	/**
	 * Filter panel constant denoting alphanumeric contents.
	 */
	public static final int ALPHANUMERIC = 0;
	
	/**
	 * Filter panel constant denoting numeric contents.
	 */
	public static final int NUMERIC = 1;

	/**
	 * Root node of a tree containing all pathways mapped in the KEGG database.
	 */
	public static final TreeNode KEGG_PATHWAY_ROOT = KeggMaps.readKeggTree(
			Constants.class.getResourceAsStream("/de/mpa/resources/conf/keggPathways.txt"));

//	public static final TreeNode KEGG_TAXONOMY_ROOT = KeggMaps.readKeggTree(new File("conf/keggTaxonomyMap.txt"));

	/**
	 * Returns the names of the nodes contained in the path from the KEGG
	 * pathway root to the leaf identified by the provided pathway ID.
	 * 
	 * @param pw The pathway ID.
	 * @return The array of node names leading to the leaf identified by 
	 * the specified ID or <code>null</code> if the ID could not be found.
	 */
	public static Object[] getKEGGPathwayPath(Short pw) {
		String pwString = String.format("%05d", pw);
		// iterate tree leaves to find pathway identified by id
		Enumeration dfEnum = ((DefaultMutableTreeNode) KEGG_PATHWAY_ROOT).depthFirstEnumeration();
		while (dfEnum.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) dfEnum.nextElement();
			if (node.isLeaf()) {
				if (((String) node.getUserObject()).startsWith(pwString)) {
					return node.getUserObjectPath();
				}
			}
		}
		return null;
	}
	
	/**
	 * Concatenates two one-dimensional object arrays.
	 * 
	 * @param <T> The object type.
	 * @param A The array that is to be appended to.
	 * @param B The array that is to be appended.
	 * @return An array containing the elements of <i>A</i> and <i>B</i>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] concat(T[] A, T[] B) {
		T[] C = (T[]) new Object[A.length + B.length];
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);
		return C;
	}
	
	public static final FileFilter MGF_FILE_FILTER = new ExtensionFileFilter(".mgf", false,
			"Mascot Generic Format Files (*.mgf)");
	public static final FileFilter MPA_FILE_FILTER = new ExtensionFileFilter(".mpa", false,
			"MetaProteomeAnalyzer Project Files (*.mpa)");
	public static final FileFilter CSV_FILE_FILTER = new ExtensionFileFilter(".mpa", false,
			"CSV (tab-separated) Files (*.csv)");
	public static final FileFilter PNG_FILE_FILTER = new ExtensionFileFilter(".png", false,
	"Portable Network Graphics (*.png)");
	public static final FileFilter DAT_FILE_FILTER = new ExtensionFileFilter(".dat", false,
			"Raw Mascot Result File (*.dat)");
	
}
