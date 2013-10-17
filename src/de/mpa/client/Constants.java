package de.mpa.client;

import java.awt.Color;
import java.util.Enumeration;

import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.mpa.analysis.KeggMaps;
import de.mpa.client.ui.ExtensionFileFilter;
import de.mpa.io.MascotGenericFile;
import de.mpa.util.ColorUtils;

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
	public final static String VER_NUMBER = "0.7.2";
	
	/**
	 * The client frame minimum width in pixels.
	 */
	public static final int MAINFRAME_WIDTH = 1280;
	
	/**
	 * The client frame minimum height in pixels.
	 */
	public static final int MAINFRAME_HEIGHT = 800;
	
	/**
	 * The default tooltip initial delay.
	 */
	public static final int DEFAULT_TOOLTIP_DELAY = 750;
	
	/**
	 * The names of FASTA database files available for searches.
	 */
	public static final String[] FASTA_DB = { "uniprot_sprot", "uniprot_human", "uniprot_trembl", "uniprot_canis", "ncbi_canis", "uniprot_archaea", "ncbi_archaea", "uniprot_sprot_bacteria", "uniprot_sprot_archaea", "human_gut", "metadb_potsdam", "uniprot_methylcoenzyme", "uniprot_arch_bact_rat", "uniprot_methanobrevibacter", "uniprot_lactobacillus", "uniprot_ruminococcus"};
	
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
	 * Path string of folder containing configuration resources.<br>
	 * <i>/de/mpa/resources/conf/</i>
	 */
	public static final String CONFIGURATION_PATH = "/de/mpa/resources/conf/";

	/**
	 * Root node of a tree containing all pathways mapped in the KEGG database.
	 */
	public static final TreeNode KEGG_PATHWAY_ROOT = KeggMaps.readKeggTree(
			Constants.class.getResourceAsStream(CONFIGURATION_PATH + "keggPathways.txt"));
	
	/**
	 * Units for precurcor and MS/MS tolerance
	 */
	public static final String[] TOLERANCE_UNITS = {"Da", "ppm"};

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
	public static final FileFilter CSV_FILE_FILTER = new ExtensionFileFilter(".csv", false,
			"CSV (tab-separated) Files (*.csv)");
	public static final FileFilter PNG_FILE_FILTER = new ExtensionFileFilter(".png", false,
			"Portable Network Graphics (*.png)");
	public static final FileFilter DAT_FILE_FILTER = new ExtensionFileFilter(".dat", false,
			"Raw Mascot Result File (*.dat)");

	/**
	 * Constants for the axis of the heatmap.
	 */
	public static final String[] HEATMAP_XAXIS = {	"PEPTIDE",
													"PROTEINE",
													"METAPROTEINE"};
	public static final String[] HEATMAP_YAXIS = {	"TAX_SUPERKINGDOM",// Special case for metaproteins
													"TAX_KINGDOM",
													"TAX_PHYLUM",
													"TAX_CLASS",
													"TAX_ORDER",
													"TAX_FAMILY",
													"TAX_GENUS",
													"TAX_SPECIES",
													"EC_NUMBER",
													"BIOLOGICAL_PROCESS",
													"CELLULAR_COMPONENT",
													"MOLECULAR_FUNCTION",
													"PATHWAY",
													"PEPTIDE",
													"PROTEIN (METAPROT)"};
	public static final String[] HEATMAP_ZAXIS = {	"No. METAPROTEIN",
													"No. PROTEIN",
													"No. PEPTIDE",
													"No. SPECTRA"};

	// Protein table column indices
	public static final int PROT_SELECTION = 0;
	public static final int PROT_INDEX = 1;
	public static final int PROT_ACCESSION = 2;
	public static final int PROT_DESCRIPTION = 3;
	public static final int PROT_TAXONOMY = 4;
	public static final int PROT_COVERAGE = 5;
	public static final int PROT_MW = 6;
	public static final int PROT_PI = 7;
	public static final int PROT_PEPTIDECOUNT = 8;
	public static final int PROT_SPECTRALCOUNT = 9;
	public static final int PROT_EMPAI = 10;
	public static final int PROT_NSAF = 11;
	public static final int PROT_WEBRESSOURCE = 12;
	
	/**
	 * Enumeration holding UI-related colors.
	 * @author A. Behne
	 */
	public enum UIColor {
		
		TITLED_PANEL_START_COLOR("titledPanel.startColor",
				"Titled Panel Gradient Start", new Color(166, 202, 240)),
		TITLED_PANEL_END_COLOR("titledPanel.endColor",
				"Titled Panel Gradient End", new Color(107, 147, 193)),
		TITLED_PANEL_FONT_COLOR("titledPanel.fontColor",
				"Titled Panel Label Text", Color.WHITE),
		BAR_CHART_PANEL_FOREGROUND_START_COLOR("barChartPanel.foregroundStartColor",
				"Bar Chart Panel Foreground Gradient Start", ColorUtils.DARK_GREEN),
		BAR_CHART_PANEL_FOREGROUND_END_COLOR("barChartPanel.foregroundEndColor",
				"Bar Chart Panel Foreground Gradient End", ColorUtils.LIGHT_GREEN);
		
		/**
		 * The <code>UIManager</code> key.
		 */
		private String key;
		
		/**
		 * The descriptive string.
		 */
		private String description;

		/**
		 * The associated default color.
		 */
		private Color defaultColor;
		
		/**
		 * Constructs an UI color enum member using the specifed 
		 * <code>UIManager</code> key, a descriptive string and an associated color.
		 * @param key the <code>UIManager</code> key
		 * @param description the descriptive string
		 * @param color the associated color
		 */
		private UIColor(String key, String description, Color color) {
			this.key = key;
			this.description = description;
			this.defaultColor = color;
			this.reset();
		}

		/**
		 * Resets the associated color to its default value.
		 */
		public void reset() {
			UIManager.put(this.getKey(), this.defaultColor);
		}

		/**
		 * Returns the <code>UIManager</code> key.
		 * @return the key
		 */
		public String getKey() {
			return key;
		}
		
		/**
		 * Returns the associated color.
		 * @return the color
		 */
		public Color getColor() {
//			return color;
			return UIManager.getColor(this.getKey());
		}

		@Override
		public String toString() {
			return description;
		}
		
	}
	
}
