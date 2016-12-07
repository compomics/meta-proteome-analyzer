package de.mpa.task.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.mpa.client.DbSearchSettings;
import de.mpa.task.Task;

/**
 * Class for executing the Comet search engine task.
 * 
 * @author Thilo Muth
 */

public class CometTask extends Task {
    
	/**
	 * Search parameters string.
	 */
	private String params;	
    
	/**
	 * Filename.
	 */
    private String filename;
        
    /**
     * Comet executable.
     */
    private File cometExecutable;
    
    /**
     * MGF file instance.
     */
	private File mgfFile;
	
	/**
	 * Parameter file instance.
	 */
	private File parameterFile;
	
	/**
	 * Predefined parameters file for Comet search engine.
	 */
	private final static String PARAMETER_FILE = "comet.params"; 
	
	/**
	 * Search database.
	 */
	private String searchDB;
	
	// Mass tolerance for fragment ions
    private double fragmentTol;
    
    // Mass tolerance for precursor ions
    private double precursorTol;
    
    // Maximum number of missed cleavages.
    private int nMissedCleavages;
    
    // String of precursor ion tolerance unit ( ppm versus Da)
    private boolean isPrecursorTolerancePpm;
    
    // Filename of the decoy database.
    private String decoyFilename;
    
	/**
	 * Constructor for starting the OMSSA search engine.
	 * @param mgfFile Spectrum file
	 * @param searchSettings Search settings
	 */
	public CometTask(File mgfFile, DbSearchSettings searchSettings) {
		this.mgfFile = mgfFile;
		this.searchDB = searchSettings.getFastaFilePath();
		this.params = searchSettings.getCometParams();
		this.fragmentTol = searchSettings.getFragIonTol();
		this.precursorTol = searchSettings.getPrecIonTol();
		this.nMissedCleavages = searchSettings.getMissedCleavages();
		this.isPrecursorTolerancePpm = searchSettings.isPrecIonTolPpm();
		this.filename = mgfFile.getAbsolutePath().substring(0, mgfFile.getAbsolutePath().indexOf(".mgf")) + "_comet.txt";
		this.decoyFilename = mgfFile.getAbsolutePath().substring(0, mgfFile.getAbsolutePath().indexOf(".mgf")) + "_comet.decoy.txt";
		this.cometExecutable = new File(algorithmProperties.getProperty("path.comet"));
		buildParametersFile();
		initJob();
	}
	
	/**
     * This method builds taxonomy file.
     */
    public void buildParametersFile() {
    	parameterFile = new File(cometExecutable, PARAMETER_FILE);
    	String[] parameters = params.split(";");
    	for (String string : parameters) {
			System.out.println(string);
		}
    	
    	String precursorUnit; 
    	if (isPrecursorTolerancePpm) {
    		precursorUnit = "2";
    	} else {
    		precursorUnit = "0";
    	}
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(parameterFile));
            
            bw.append("# comet_version 2016.01 rev. 2");
            bw.newLine();
            bw.append("# Comet MS/MS search engine parameters file.");
            bw.newLine();
            bw.append("# Everything following the '#' symbol is treated as a comment.");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("database_name = " + searchDB);
            bw.newLine();
            bw.append("decoy_search = 2                       # 0=no (default), 1=concatenated search, 2=separate search");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("num_threads = " + parameters[4] + "# 0=poll CPU to set num threads; else specify num threads directly (max 64)");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# masses");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("peptide_mass_tolerance = " + precursorTol);
            bw.newLine();
            bw.append("peptide_mass_units = " + precursorUnit);
            bw.newLine();
            bw.append("mass_type_parent = 1                   # 0=average masses, 1=monoisotopic masses");
            bw.newLine();
            bw.append("mass_type_fragment = 1                 # 0=average masses, 1=monoisotopic masses");
            bw.newLine();
            bw.append("precursor_tolerance_type = 0           # 0=MH+ (default), 1=precursor m/z; only valid for amu/mmu tolerances");
            bw.newLine();
            bw.append("isotope_error = 1                      # 0=off, 1=on -1/0/1/2/3 (standard C13 error), 2= -8/-4/0/4/8 (for +4/+8 labeling)");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# search enzyme");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("search_enzyme_number = " + parameters[0]);
            bw.newLine();
            bw.append("num_enzyme_termini = 2");
            bw.newLine();
            bw.append("allowed_missed_cleavage = " + nMissedCleavages);
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# Up to 9 variable modifications are supported");
            bw.newLine();
            bw.append("# format:  <mass> <residues> <0=variable/else binary> <max_mods_per_peptide> <term_distance> <n/c-term> <required>");
            bw.newLine();
            bw.append("#     e.g. 79.966331 STY 0 3 -1 0 0");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("variable_mod01 = 15.9949 M 0 3 -1 0 0");
            bw.newLine();
            bw.append("variable_mod02 = 0.0 X 0 3 -1 0 0");
            bw.newLine();
            bw.append("variable_mod03 = 0.0 X 0 3 -1 0 0");
            bw.newLine();
            bw.append("variable_mod04 = 0.0 X 0 3 -1 0 0");
            bw.newLine();
            bw.append("variable_mod05 = 0.0 X 0 3 -1 0 0");
            bw.newLine();
            bw.append("variable_mod06 = 0.0 X 0 3 -1 0 0");
            bw.newLine();
            bw.append("variable_mod07 = 0.0 X 0 3 -1 0 0");
            bw.newLine();
            bw.append("variable_mod08 = 0.0 X 0 3 -1 0 0");
            bw.newLine();
            bw.append("variable_mod09 = 0.0 X 0 3 -1 0 0");
            bw.newLine();
            bw.append("max_variable_mods_in_peptide = 5");
            bw.newLine();
            bw.append("require_variable_mod = 0");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# fragment ions");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# ion trap ms/ms:  1.0005 tolerance, 0.4 offset (mono masses), theoretical_fragment_ions = 1");
            bw.newLine();
            bw.append("# high res ms/ms:    0.02 tolerance, 0.0 offset (mono masses), theoretical_fragment_ions = 0");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("fragment_bin_tol = " + fragmentTol);
            bw.newLine();
            bw.append("fragment_bin_offset = 0.0              # offset position to start the binning (0.0 to 1.0)");
            bw.newLine();
            bw.append("theoretical_fragment_ions = 0          # 0=use flanking peaks, 1=M peak only");
            bw.newLine();
            bw.append("use_A_ions = 0");
            bw.newLine();
            bw.append("use_B_ions = 1");
            bw.newLine();
            bw.append("use_C_ions = 0");
            bw.newLine();
            bw.append("use_X_ions = 0");
            bw.newLine();
            bw.append("use_Y_ions = 1");
            bw.newLine();
            bw.append("use_Z_ions = 0");
            bw.newLine();
            bw.append("use_NL_ions = 1                        # 0=no, 1=yes to consider NH3/H2O neutral loss peaks");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# output");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("output_sqtstream = 0                   # 0=no, 1=yes  write sqt to standard output");
            bw.newLine();
            bw.append("output_sqtfile = 0                     # 0=no, 1=yes  write sqt file");
            bw.newLine();
            bw.append("output_txtfile = 1                     # 0=no, 1=yes  write tab-delimited txt file");
            bw.newLine();
            bw.append("output_pepxmlfile = 0                  # 0=no, 1=yes  write pep.xml file");
            bw.newLine();
            bw.append("output_percolatorfile = 0              # 0=no, 1=yes  write Percolator tab-delimited input file");
            bw.newLine();
            bw.append("output_outfiles = 0                    # 0=no, 1=yes  write .out files");
            bw.newLine();
            bw.append("print_expect_score = 1                 # 0=no, 1=yes to replace Sp with expect in out & sqt");
            bw.newLine();
            bw.append("num_output_lines = 1                   # num peptide results to show");
            bw.newLine();
            bw.append("show_fragment_ions = 0                 # 0=no, 1=yes for out files only");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("sample_enzyme_number = 1               # Sample enzyme which is possibly different than the one applied to the search.");
            bw.newLine();
            bw.append("                                       # Used to calculate NTT & NMC in pepXML output (default=1 for trypsin).");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# mzXML parameters");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("scan_range = 0 0                       # start and scan scan range to search; 0 as 1st entry ignores parameter");
            bw.newLine();
            bw.append("precursor_charge = 0 0                 # precursor charge range to analyze; does not override any existing charge; 0 as 1st entry ignores parameter");
            bw.newLine();
            bw.append("override_charge = 0                    # 0=no, 1=override precursor charge states, 2=ignore precursor charges outside precursor_charge range, 3=see online");
            bw.newLine();
            bw.append("ms_level = 2                           # MS level to analyze, valid are levels 2 (default) or 3");
            bw.newLine();
            bw.append("activation_method = HCD                # activation method; used if activation method set; allowed ALL, CID, ECD, ETD, PQD, HCD, IRMPD");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# misc parameters");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("digest_mass_range = 600.0 5000.0       # MH+ peptide mass range to analyze");
            bw.newLine();
            bw.append("num_results = 100                      # number of search hits to store internally");
            bw.newLine();
            bw.append("skip_researching = 1                   # for '.out' file output only, 0=search everything again (default), 1=don't search if .out exists");
            bw.newLine();
            bw.append("max_fragment_charge = 3                # set maximum fragment charge state to analyze (allowed max 5)");
            bw.newLine();
            bw.append("max_precursor_charge = 6               # set maximum precursor charge state to analyze (allowed max 9)");
            bw.newLine();
            bw.append("nucleotide_reading_frame = 0           # 0=proteinDB, 1-6, 7=forward three, 8=reverse three, 9=all six");
            bw.newLine();
            bw.append("clip_nterm_methionine = 0              # 0=leave sequences as-is; 1=also consider sequence w/o N-term methionine");
            bw.newLine();
            bw.append("spectrum_batch_size = 0                # max. # of spectra to search at a time; 0 to search the entire scan range in one loop");
            bw.newLine();
            bw.append("decoy_prefix = DECOY_                  # decoy entries are denoted by this string which is pre-pended to each protein accession");
            bw.newLine();
            bw.append("output_suffix = _comet                 # add a suffix to output base names i.e. suffix \"-C\" generates base-C.pep.xml from base.mzXML input");
            bw.newLine();
            bw.append("mass_offsets =                         # one or more mass offsets to search (values substracted from deconvoluted precursor mass)");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# spectral processing");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("minimum_peaks = " + parameters[1] + "  # required minimum number of peaks in spectrum to search (default 10)");
            bw.newLine();
            bw.append("minimum_intensity = " + parameters[2] + " # minimum intensity value to read in");
            bw.newLine();
            bw.append("remove_precursor_peak = " + parameters[3] + " # 0=no, 1=yes, 2=all charge reduced precursor peaks (for ETD)");
            bw.newLine();
            bw.append("remove_precursor_tolerance = 1.5       # +- Da tolerance for precursor removal");
            bw.newLine();
            bw.append("clear_mz_range = 0.0 0.0               # for iTRAQ/TMT type data; will clear out all peaks in the specified m/z range");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# additional modifications");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("add_Cterm_peptide = 0.0");
            bw.newLine();
            bw.append("add_Nterm_peptide = 0.0");
            bw.newLine();
            bw.append("add_Cterm_protein = 0.0");
            bw.newLine();
            bw.append("add_Nterm_protein = 0.0");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("add_G_glycine = 0.0000                 # added to G - avg.  57.0513, mono.  57.02146");
            bw.newLine();
            bw.append("add_A_alanine = 0.0000                 # added to A - avg.  71.0779, mono.  71.03711");
            bw.newLine();
            bw.append("add_S_serine = 0.0000                  # added to S - avg.  87.0773, mono.  87.03203");
            bw.newLine();
            bw.append("add_P_proline = 0.0000                 # added to P - avg.  97.1152, mono.  97.05276");
            bw.newLine();
            bw.append("add_V_valine = 0.0000                  # added to V - avg.  99.1311, mono.  99.06841");
            bw.newLine();
            bw.append("add_T_threonine = 0.0000               # added to T - avg. 101.1038, mono. 101.04768");
            bw.newLine();
            bw.append("add_C_cysteine = 57.021464             # added to C - avg. 103.1429, mono. 103.00918");
            bw.newLine();
            bw.append("add_L_leucine = 0.0000                 # added to L - avg. 113.1576, mono. 113.08406");
            bw.newLine();
            bw.append("add_I_isoleucine = 0.0000              # added to I - avg. 113.1576, mono. 113.08406");
            bw.newLine();
            bw.append("add_N_asparagine = 0.0000              # added to N - avg. 114.1026, mono. 114.04293");
            bw.newLine();
            bw.append("add_D_aspartic_acid = 0.0000           # added to D - avg. 115.0874, mono. 115.02694");
            bw.newLine();
            bw.append("add_Q_glutamine = 0.0000               # added to Q - avg. 128.1292, mono. 128.05858");
            bw.newLine();
            bw.append("add_K_lysine = 0.0000                  # added to K - avg. 128.1723, mono. 128.09496");
            bw.newLine();
            bw.append("add_E_glutamic_acid = 0.0000           # added to E - avg. 129.1140, mono. 129.04259");
            bw.newLine();
            bw.append("add_M_methionine = 0.0000              # added to M - avg. 131.1961, mono. 131.04048");
            bw.newLine();
            bw.append("add_O_ornithine = 0.0000               # added to O - avg. 132.1610, mono  132.08988");
            bw.newLine();
            bw.append("add_H_histidine = 0.0000               # added to H - avg. 137.1393, mono. 137.05891");
            bw.newLine();
            bw.append("add_F_phenylalanine = 0.0000           # added to F - avg. 147.1739, mono. 147.06841");
            bw.newLine();
            bw.append("add_U_selenocysteine = 0.0000          # added to U - avg. 150.3079, mono. 150.95363");
            bw.newLine();
            bw.append("add_R_arginine = 0.0000                # added to R - avg. 156.1857, mono. 156.10111");
            bw.newLine();
            bw.append("add_Y_tyrosine = 0.0000                # added to Y - avg. 163.0633, mono. 163.06333");
            bw.newLine();
            bw.append("add_W_tryptophan = 0.0000              # added to W - avg. 186.0793, mono. 186.07931");
            bw.newLine();
            bw.append("add_B_user_amino_acid = 0.0000         # added to B - avg.   0.0000, mono.   0.00000");
            bw.newLine();
            bw.append("add_J_user_amino_acid = 0.0000         # added to J - avg.   0.0000, mono.   0.00000");
            bw.newLine();
            bw.append("add_X_user_amino_acid = 0.0000         # added to X - avg.   0.0000, mono.   0.00000");
            bw.newLine();
            bw.append("add_Z_user_amino_acid = 0.0000         # added to Z - avg.   0.0000, mono.   0.00000");
            bw.newLine();
            bw.append("");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("# COMET_ENZYME_INFO _must_ be at the end of this parameters file");
            bw.newLine();
            bw.append("#");
            bw.newLine();
            bw.append("[COMET_ENZYME_INFO]");
            bw.newLine();
            bw.append("0.  No_enzyme              0      -           -");
            bw.newLine();
            bw.append("1.  Trypsin                1      KR          P");
            bw.newLine();
            bw.append("2.  Trypsin/P              1      KR          -");
            bw.newLine();
            bw.append("3.  Lys_C                  1      K           P");
            bw.newLine();
            bw.append("4.  Lys_N                  0      K           -");
            bw.newLine();
            bw.append("5.  Arg_C                  1      R           P");
            bw.newLine();
            bw.append("6.  Asp_N                  0      D           -");
            bw.newLine();
            bw.append("7.  CNBr                   1      M           -");
            bw.newLine();
            bw.append("8.  Glu_C                  1      DE          P");
            bw.newLine();
            bw.append("9.  PepsinA                1      FL          P");
            bw.newLine();
            bw.append("10. Chymotrypsin           1      FWYL        P");
            bw.newLine();
            bw.append("");
            bw.newLine();
            
            bw.flush();
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
	/**
	 * Initializes the job, setting up the commands for the ProcessBuilder.
	 */
	private void initJob(){
		// Ensure that Comet is executable
		cometExecutable.setExecutable(true);
		
		// Full path to executable
        procCommands.add(cometExecutable.getAbsolutePath() + File.separator + algorithmProperties.getProperty("app.comet"));
        
        // Always ask for spectra included in results file
        procCommands.add(mgfFile.getAbsolutePath());
        procCommands.trimToSize();
        
        setDescription("COMET TARGET-DECOY SEARCH");
        procBuilder = new ProcessBuilder(procCommands);
        procBuilder.directory(cometExecutable);
        
        // Set error out and std out to same stream
        procBuilder.redirectErrorStream(true);
	}
	
	/**
	 * Returns the absolute path to the Comet target output file.
	 * @return filename Absolute path to the Comet target file.
	 */
	public String getFilename() {
		return filename;		
	}
	
	/**
	 * Returns the absolute path to the Comet decoy output file.
	 * @return decoyFilename Absolute path to the Comet decoy file.
	 */
	public String getDecoyFilename() {
		return decoyFilename;
	}
}
