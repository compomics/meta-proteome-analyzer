package de.mpa.client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.mpa.analysis.KeggMaps;
import de.mpa.client.ui.DelegateColor;
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
	public final static String VER_NUMBER = "0.8";
	
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
	public static final String[] FASTA_DB = { "uniprot_sprot", "uniprot_human", "uniprot_trembl", "uniprot_canis", "ncbi_canis", "uniprot_archaea", "ncbi_archaea", "uniprot_sprot_bacteria", "uniprot_sprot_archaea", "human_gut", "metadb_potsdam", "uniprot_methylcoenzyme", "uniprot_arch_bact_rat", "uniprot_methanobrevibacter", "uniprot_lactobacillus", "pyrococcus"};
	
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
	 * Path string of folder containing configuration resources for the jar built.
	 */
	public static final String CONFIGURATION_PATH_JAR = "conf";
	
	/**
	 * Path string of folder containing spectrum resources.
	 */
	public static final String DEFAULT_SPECTRA_PATH = "test/de/mpa/resources/";
	
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
			"Mascot Generic Format File (*.mgf)");
	public static final FileFilter MPA_FILE_FILTER = new ExtensionFileFilter(".mpa", false,
			"MetaProteomeAnalyzer Project File (*.mpa)");
	public static final FileFilter CSV_FILE_FILTER = new ExtensionFileFilter(".csv", false,
			"CSV File, tab-separated (*.csv)");
	public static final FileFilter PNG_FILE_FILTER = new ExtensionFileFilter(".png", false,
			"Portable Network Graphics (*.png)");
	public static final FileFilter DAT_FILE_FILTER = new ExtensionFileFilter(".dat", false,
			"Raw Mascot Result File (*.dat)");
	public static final FileFilter GRAPHML_FILE_FILTER = new ExtensionFileFilter(".graphml", false,
			"GraphML File (*.graphml)");
	public static final FileFilter EXCEL_XML_FILE_FILTER = new ExtensionFileFilter(".xml", false,
			"Microsoft Excel 2003 XML File (*.xml)");

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
	public static final int PROT_WEBRESOURCE = 12;
	
	/**
	 * Enumeration holding UI-related colors.
	 * @author A. Behne
	 */
	public enum UIColor {
		/* Button colors */
		BUTTON_FOCUS_COLOR("button.focusColor", "Button Focus"),
		/* Text Selection colors */
		TEXT_SELECTION_FONT_COLOR("textSelection.fontColor",
				"Text Selection Font Color"),
		TEXT_SELECTION_BACKGROUND_COLOR("textSelection.backgroundColor", 
				"Text Selection Background Color"),
		/* Titled Panel colors */
		TITLED_PANEL_START_COLOR("titledPanel.startColor", "Titled Panel Gradient Start"),
		TITLED_PANEL_END_COLOR("titledPanel.endColor", "Titled Panel Gradient End"),
		TITLED_PANEL_FONT_COLOR("titledPanel.fontColor", "Titled Panel Label Text"),
		/* Task Pane colors */
		TASK_PANE_BACKGROUND_COLOR("taskPane.backgroundColor", "Task Pane Background Color"),
		/* Bar Chart Panel colors */
		BAR_CHART_PANEL_FOREGROUND_START_COLOR("barChartPanel.foregroundStartColor",
				"Bar Chart Panel Foreground Gradient Start"),
		BAR_CHART_PANEL_FOREGROUND_END_COLOR("barChartPanel.foregroundEndColor",
				"Bar Chart Panel Foreground Gradient End"),
		BAR_CHART_PANEL_BACKGROUND_START_COLOR("barChartPanel.backgroundStartColor",
				"Bar Chart Panel Background Gradient Start"),
		BAR_CHART_PANEL_BACKGROUND_END_COLOR("barChartPanel.backgroundEndColor",
				"Bar Chart Panel Background Gradient End"),
		/* Scrollbar colors */
		SCROLLBAR_THUMB_COLOR("scrollBar.thumbColor", "Scroll Bar Thumb Color"),
		/* Table colors */
		TABLE_SELECTION_COLOR("table.selColor", "Table Selection Background Color"),
		TABLE_FOCUS_HIGHLIGHT_COLOR("table.hlColor", "Table Focus Highlight Color"),
		/* Horizontal Bar Chart Highlighter colors */
		HORZ_BAR_CHART_HIGHLIGHTER_A_START_COLOR("horzBarChartHighlighterA.startColor",
				"Table Horizontal Bar Chart A Gradient Start"),
		HORZ_BAR_CHART_HIGHLIGHTER_A_END_COLOR("horzBarChartHighlighterA.endColor",
				"Table Horizontal Bar Chart A Gradient End"),
		HORZ_BAR_CHART_HIGHLIGHTER_B_START_COLOR("horzBarChartHighlighterB.startColor",
				"Table Horizontal Bar Chart B Gradient Start"),
		HORZ_BAR_CHART_HIGHLIGHTER_B_END_COLOR("horzBarChartHighlighterB.endColor",
				"Table Horizontal Bar Chart B Gradient End"),
		HORZ_BAR_CHART_HIGHLIGHTER_C_START_COLOR("horzBarChartHighlighterC.startColor",
				"Table Horizontal Bar Chart C Gradient Start"),
		HORZ_BAR_CHART_HIGHLIGHTER_C_END_COLOR("horzBarChartHighlighterC.endColor",
				"Table Horizontal Bar Chart C Gradient End"),
		/* Vertical Bar Chart Highlighter colors */
		VERT_BAR_CHART_HIGHLIGHTER_A_START_COLOR("horzBarChartHighlighterA.startColor",
				"Table Vertical Bar Chart A Gradient Start"),
		VERT_BAR_CHART_HIGHLIGHTER_A_END_COLOR("vertBarChartHighlighterA.endColor",
				"Table Vertical Bar Chart A Gradient End"),
		VERT_BAR_CHART_HIGHLIGHTER_B_START_COLOR("vertBarChartHighlighterB.startColor",
				"Table Vertical Bar Chart B Gradient Start"),
		VERT_BAR_CHART_HIGHLIGHTER_B_END_COLOR("vertBarChartHighlighterB.endColor",
				"Table Vertical Bar Chart B Gradient End"),
		VERT_BAR_CHART_HIGHLIGHTER_C_START_COLOR("vertBarChartHighlighterC.startColor",
				"Table Vertical Bar Chart C Gradient Start"),
		VERT_BAR_CHART_HIGHLIGHTER_C_END_COLOR("vertBarChartHighlighterC.endColor",
				"Table Vertical Bar Chart C Gradient End"),
		VERT_BAR_CHART_HIGHLIGHTER_D_START_COLOR("vertBarChartHighlighterD.startColor",
				"Table Vertical Bar Chart D Gradient Start"),
		VERT_BAR_CHART_HIGHLIGHTER_D_END_COLOR("vertBarChartHighlighterD.endColor",
				"Table Vertical Bar Chart D Gradient End"),
		VERT_BAR_CHART_HIGHLIGHTER_E_START_COLOR("vertBarChartHighlighterE.startColor",
				"Table Vertical Bar Chart E Gradient Start"),
		VERT_BAR_CHART_HIGHLIGHTER_E_END_COLOR("vertBarChartHighlighterE.endColor",
				"Table Vertical Bar Chart E Gradient End"),
		/* Progress Bar colors */
//		PROGRESS_BAR_START_COLOR("progressBar.startColor", "Progress Bar Gradient Start"),
//		PROGRESS_BAR_END_COLOR("progressBar.endColor", "Progress Bar Gradient End");
		PROGRESS_BAR_FOREGROUND_COLOR("progressBar.foregroundColor", "Progress Bar Foreground Color");
		
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
		 * A delegate color usable to re-direct to this UI color.
		 */
		private DelegateColor delegate;
		
		/**
		 * Constructs an UI color enum member using the specified 
		 * <code>UIManager</code> key and a descriptive string.
		 * @param key the <code>UIManager</code> key
		 * @param description the descriptive string
		 */
		private UIColor(String key, String description) {
			this.key = key;
			this.description = description;
			this.reset();
		}

		/**
		 * Resets the associated color to its default value.
		 */
		public void reset() {
			this.setColor(this.defaultColor);
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
			return UIManager.getColor(this.getKey());
		}
		
		/**
		 * Returns the color delegating to this UI color.
		 * @return the delegate color
		 */
		public DelegateColor getDelegateColor() {
			if (delegate == null) {
				delegate = new DelegateColor(this);
			}
			return delegate;
		}

		/**
		 * Sets the associated color.
		 * @param color the color to set
		 */
		public void setColor(Color color) {
			UIManager.put(this.getKey(), color);
		}
		
		/**
		 * Sets the default color.
		 * @param defaultColor the color to set as default.
		 */
		public void setDefaultColor(Color defaultColor) {
			this.defaultColor = defaultColor;
		}

		@Override
		public String toString() {
			return description;
		}
		
	}
	
	/**
	 * The folder containing theme files.
	 */
	public static final String THEME_FOLDER = "themes/";
	
	/**
	 * The name of the default theme.
	 */
	public static final String DEFAULT_THEME_NAME = "Sky Blue";
	
	/**
	 * Hard-coded backup of the default theme, use when file-based default theme is missing.
	 */
	public static final UITheme DEFAULT_THEME = new UITheme(
			DEFAULT_THEME_NAME,
			new Color(195, 212, 232),
			new HashMap<UIColor, Color>() {
		{
			put(UIColor.BUTTON_FOCUS_COLOR, new Color(195, 212, 232));
			put(UIColor.TEXT_SELECTION_FONT_COLOR, new Color(0, 0, 0));
			put(UIColor.TEXT_SELECTION_BACKGROUND_COLOR, new Color(195, 212, 232));
			put(UIColor.TITLED_PANEL_START_COLOR, new Color(166, 202, 240));
			put(UIColor.TITLED_PANEL_END_COLOR, new Color(107, 147, 193));
			put(UIColor.TITLED_PANEL_FONT_COLOR, new Color(255, 255, 255));
			put(UIColor.TASK_PANE_BACKGROUND_COLOR, new Color(195, 212, 232));
			put(UIColor.BAR_CHART_PANEL_FOREGROUND_START_COLOR, new Color(0, 127, 0));
			put(UIColor.BAR_CHART_PANEL_FOREGROUND_END_COLOR, new Color(127, 255, 127));
			put(UIColor.BAR_CHART_PANEL_BACKGROUND_START_COLOR, new Color(64, 64, 64));
			put(UIColor.BAR_CHART_PANEL_BACKGROUND_END_COLOR, new Color(192, 192, 192));
			put(UIColor.SCROLLBAR_THUMB_COLOR, new Color(195, 212, 232));
			put(UIColor.TABLE_SELECTION_COLOR, new Color(195, 212, 232));
			put(UIColor.TABLE_FOCUS_HIGHLIGHT_COLOR, new Color(166, 202, 240));
			put(UIColor.HORZ_BAR_CHART_HIGHLIGHTER_A_START_COLOR, new Color(0, 127, 0));
			put(UIColor.HORZ_BAR_CHART_HIGHLIGHTER_A_END_COLOR, new Color(127, 255, 127));
			put(UIColor.HORZ_BAR_CHART_HIGHLIGHTER_B_START_COLOR, new Color(127, 0, 0));
			put(UIColor.HORZ_BAR_CHART_HIGHLIGHTER_B_END_COLOR, new Color(255, 127, 127));
			put(UIColor.HORZ_BAR_CHART_HIGHLIGHTER_C_START_COLOR, new Color(127, 63, 0));
			put(UIColor.HORZ_BAR_CHART_HIGHLIGHTER_C_END_COLOR, new Color(255, 191, 127));
			put(UIColor.VERT_BAR_CHART_HIGHLIGHTER_A_START_COLOR, new Color(0, 127, 0));
			put(UIColor.VERT_BAR_CHART_HIGHLIGHTER_A_END_COLOR, new Color(127, 255, 127));
			put(UIColor.VERT_BAR_CHART_HIGHLIGHTER_B_START_COLOR, new Color(0, 127, 127));
			put(UIColor.VERT_BAR_CHART_HIGHLIGHTER_B_END_COLOR, new Color(127, 255, 255));
			put(UIColor.VERT_BAR_CHART_HIGHLIGHTER_C_START_COLOR, new Color(0, 0, 127));
			put(UIColor.VERT_BAR_CHART_HIGHLIGHTER_C_END_COLOR, new Color(127, 127, 255));
			put(UIColor.VERT_BAR_CHART_HIGHLIGHTER_D_START_COLOR, new Color(127, 0, 127));
			put(UIColor.VERT_BAR_CHART_HIGHLIGHTER_D_END_COLOR, new Color(255, 127, 255));
			put(UIColor.VERT_BAR_CHART_HIGHLIGHTER_E_START_COLOR, new Color(127, 0, 0));
			put(UIColor.VERT_BAR_CHART_HIGHLIGHTER_E_END_COLOR, new Color(255, 127, 127));
//			put(UIColor.PROGRESS_BAR_START_COLOR, new Color(169, 191, 217));
//			put(UIColor.PROGRESS_BAR_END_COLOR, new Color(214, 233, 255));
			put(UIColor.PROGRESS_BAR_FOREGROUND_COLOR, new Color(195, 212, 232));
		}
	});
	
	/**
	 * The list of UI themes.
	 */
	public static List<UITheme> THEMES = new ArrayList<UITheme>();	// is finalized in starter class
	
	/**
	 * Class for parsing and storing UI themes.
	 * @author A. Behne
	 */
	public static class UITheme {

		/**
		 * The title of the theme.
		 */
		private String title;
		
		/**
		 * The prototype color used in previewing the theme.
		 */
		private Color prototypeColor;
		
		/**
		 * Map for storing colors and their respective UI enum.
		 */
		private Map<UIColor, Color> colorMap;
		
		/**
		 * Constructs a theme using the specified color map.
		 * @param colorMap the color map
		 */
		public UITheme(String title, Color prototypeColor,
				Map<UIColor, Color> colorMap) {
			this.title = title;
			this.prototypeColor = prototypeColor;
			this.colorMap = colorMap;
		}
		
		/**
		 * Constructs a theme by parsing the specified theme file.
		 * @param path the theme file
		 */
		public UITheme(File file) {
			colorMap = new LinkedHashMap<UIColor, Color>();
			this.parse(file);
		}
		
		/**
		 * Convenience method to get a theme by its name;
		 * @param title the theme title
		 * @return the desired theme or <code>null</code>
		 *  if no theme with the specified name exists
		 */
		public static UITheme valueOf(String title) {
			if (title != null) {
				for (UITheme theme : THEMES) {
					if (title.equals(theme.getTitle())) {
						return theme;
					}
				}
			}
			return null;
		}
		
		/**
		 * Parses the theme file located at the specified path and stores its
		 * values.
		 * @param path the path to the theme file
		 */
		private void parse(File file) {
			
			if (!file.exists()) {
				System.err.println("File \'" + file + "\' not found!");
				return;
			}
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				// Parse first line
				String line = br.readLine();
				String[] split = line.split(",");
				// Extract title and prototype color from first line
				this.title = split[0];
				this.prototypeColor = new Color(
						Integer.valueOf(split[1]),
						Integer.valueOf(split[2]),
						Integer.valueOf(split[3]));
				// Parse remaining files
				while ((line = br.readLine()) != null) {
					split = line.split(",");
					// Extract UI color reference and default color
					UIColor uiColor = UIColor.valueOf(split[0]);
					Color color = new Color(
							Integer.valueOf(split[1]),
							Integer.valueOf(split[2]),
							Integer.valueOf(split[3]));
					// Map UI color reference to default color
					colorMap.put(uiColor, color);
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		/**
		 * Applies the colors stored in this theme.
		 */
		public void applyTheme() {
			for (Entry<UIColor, Color> entry : colorMap.entrySet()) {
				UIColor uiColor = entry.getKey();
				Color color = entry.getValue();
				uiColor.setDefaultColor(color);
				uiColor.reset();
			}
		}
		
		/**
		 * Writes a theme configuration to the specified file.
		 * @param file the theme file
		 */
		public void writeToFile(File file) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				Color protoCol = this.getPrototypeColor();
				// write header info
				bw.write(this.getTitle() + ","
						+ protoCol.getRed() + ","
						+ protoCol.getGreen() + ","
						+ protoCol.getBlue());
				bw.newLine();
				// write color constants
				for (Entry<UIColor, Color> entry : colorMap.entrySet()) {
					Color col = entry.getValue();
					bw.write(entry.getKey().name() + ","
							+ col.getRed() + ","
							+ col.getGreen() + ","
							+ col.getBlue());
					bw.newLine();
				}
				bw.flush();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Returns the title of the theme.
		 * @return the theme title.
		 */
		public String getTitle() {
			return this.title;
		}
		
		/**
		 * Returns the prototype color of this theme.
		 * @return the prototype color
		 */
		public Color getPrototypeColor() {
			if (this.prototypeColor == null) {
				return this.colorMap.get(UIColor.TEXT_SELECTION_BACKGROUND_COLOR);
			}
			return this.prototypeColor;
		}
		
		/**
		 * Returns the color mappings.
		 * @return the color map
		 */
		public Map<UIColor, Color> getColorMap() {
			return this.colorMap;
		}
		
		@Override
		public String toString() {
			return this.getTitle();
		}
		
	}
}
