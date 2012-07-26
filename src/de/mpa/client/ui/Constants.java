package de.mpa.client.ui;

import de.mpa.io.MascotGenericFile;

public class Constants {
	public static final int MAINFRAME_WIDTH = 1280;
	public static final int MAINFRAME_HEIGHT = 800;
	public static final String[] FASTA_DB = { "uniprot_sprot", "ecoli", "methanomicrobia", "archaea", "firmicutes"};
	public static final String[] ENZYMES = { "Trypsin", "Semi-tryptic"};
	public static final String[] DN_ENZYMES = { "Trypsin", "Unspecific" };
	
	public static final String[] DN_MODELS = { "CID_IT_TRYP",
			"CID_IT_TRYP_CSP", "CID_IT_TRYP_DB", "CID_IT_TRYP_DNVPART",
			"CID_IT_TRYP_SCORE", "CID_IT_TRYP_TAG3", "CID_IT_TRYP_TAG4",
			"CID_IT_TRYP_TAG5", "CID_IT_TRYP_TAG6", "DBC4_PEAK", "ITDNV_PEAK",
			"LTQ_COMP" };

	// Title of the application
	public final static String APPTITLE = "MetaProteomeAnalyzer";

	// Version number
	public final static String VER_NUMBER = "0.5";

	public final static String SELECTED_ROW_HTML_FONT_COLOR = "#FFFFFF";
	public final static String NOT_SELECTED_ROW_HTML_FONT_COLOR = "#0101DF";

	public static final MascotGenericFile EMPTY_MGF = new MascotGenericFile(
			"empty", ("BEGIN IONS\n" + "0.0\t999.0\n" + "90.909\t0.0\n"
					+ "END IONS"));

	public static final int ALPHA_NUMERICAL = 0;
	public static final int NUMERICAL = 1;
}
