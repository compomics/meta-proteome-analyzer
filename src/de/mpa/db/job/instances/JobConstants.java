package de.mpa.db.job.instances;

import de.mpa.util.PropertyLoader;

public class JobConstants {

	// Dataset path
	public final static String TRANSFER_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/transfer/";
	
	// Software path
	public final static String MAIN_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/";

	// Path to the fasta files
	public final static String FASTA_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/fasta/";
	
	// X!Tandem
	public final static String XTANDEM_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/xtandem/";
	public final static String XTANDEM_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/xtandem/bin/";
	public final static String XTANDEM_EXE = "tandem.exe";
	
	// Omssa
	public final static String OMSSA_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/omssa/";
	public final static String OMSSA_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/omssa/";
	public final static String OMSSA_EXE = "omssacl";
	
	// Crux
	public final static String CRUX_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/crux/";
	public final static String CRUX_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/crux/bin/";
	public final static String CRUX_EXE = "crux";
	
	// Inspect
	public final static String INSPECT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/inspect/";
	public final static String INSPECT_EXE = "inspect";
	public final static String INSPECT_INPUT_FILE = "input.txt";	
	public final static String INSPECT_RAW_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/inspect/raw/";
	public final static String INSPECT_PVALUED_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/inspect/pvalued/";
	
	// Pepnovo+
	public final static String PEPNOVO_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/pepnovo/";
	public final static String PEPNOVO_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/pepnovo/";
	public final static String PEPNOVO_EXE = "PepNovo_bin";
	
	// QVality
	public final static String QVALITY_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/qvality/bin/";
	public final static String QVALITY_EXE = "qvality";
	
	// The decoy suffix string
	public final static String SUFFIX_DECOY = "_decoy";
	
	// Blastp
	public final static String BLAST_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/blast/bin/";
	public final static String BLAST_EXE = "blastp";
}
