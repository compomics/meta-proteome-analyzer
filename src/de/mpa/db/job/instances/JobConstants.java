package de.mpa.db.job.instances;

import de.mpa.util.PropertyLoader;

public class JobConstants {

	// Dataset path
    public static final String TRANSFER_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/transfer/";
	
	// Software path
    public static final String MAIN_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/";

	// Path to the fasta files
    public static final String FASTA_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/fasta/";
	
	// X!Tandem
    public static final String XTANDEM_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/xtandem/";
	public static final String XTANDEM_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/xtandem/bin/";
	public static final String XTANDEM_EXE = "tandem.exe";
	
	// Omssa
    public static final String OMSSA_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/omssa/";
	public static final String OMSSA_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/omssa/";
	public static final String OMSSA_EXE = "omssacl";
	
	// Crux
    public static final String CRUX_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/crux/";
	public static final String CRUX_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/crux/bin/";
	public static final String CRUX_EXE = "crux";
	
	// Inspect
    public static final String INSPECT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/inspect/";
	public static final String INSPECT_EXE = "inspect";
	public static final String INSPECT_INPUT_FILE = "input.txt";
	public static final String INSPECT_RAW_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/inspect/raw/";
	public static final String INSPECT_PVALUED_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/inspect/pvalued/";
	
	// Pepnovo+
    public static final String PEPNOVO_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/pepnovo/";
	public static final String PEPNOVO_OUTPUT_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/pepnovo/";
	public static final String PEPNOVO_EXE = "PepNovo_bin";
	
	// QVality
    public static final String QVALITY_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/qvality/bin/";
	public static final String QVALITY_EXE = "qvality";
	
	// The decoy suffix string
    public static final String SUFFIX_DECOY = "_decoy";
	
	// Blastp
    public static final String BLAST_PATH = PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/software/blast/bin/";
	public static final String BLAST_EXE = "blastp";
}
