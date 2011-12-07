package de.mpa.client.ui;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;

public class Constants {
	public static final int MAINFRAME_WIDTH = 1280;
	public static final int MAINFRAME_HEIGHT = 800;
	//TODO: Thilo Enzymes,PTM's and DB's tuning 
	public static final String[] FASTA_DB = {"uniprot_sprot"};
	public static final String[] ENZYMES = {"Trypsin", "Semi-tryptic"};
	public static final String[] dnDatabase = {"NCBI","uniprot_sprot"};
	public static final String[] dnEnzymes= {"tryptic","(semi-tryptic)"};
	public static final String[] PTMs = {"Oxidation (M)",
		 "Carbamidomethyl (C)",};
	public static final String[]  ms = {"ESI-TRAP (LTQ)","(MALDI-ToF/ToF)","(Qstar)"};

// Title of the application
	public final static String APPTITLE = "MetaProteomicsAnalyzer";
	
	// Version number
	public final static String VER_NUMBER = "0.3";
}
