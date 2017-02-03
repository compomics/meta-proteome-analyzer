package de.mpa.cli;

import org.apache.commons.cli.Options;

/**
 * Parameters set for the MPA command line interface.
 *  
 * @author Thilo Muth
 */
public enum CmdLineInterfaceParams {
    SPECTRUM_FILES("spectrum_files", "Spectrum files (MGF format), comma separated list or an entire folder.", true),
    DATABASE_FILE("database", "The filename of the protein database (FASTA format).", true),
    MISSED_CLEAV("missed_cleav", "The number of maximum allowed missed cleavages.", true),
    PRECURSOR_TOL("prec_tol", "The precursor tolerance (in Dalton, e.g. 0.5Da or PPM, e.g. 10ppm).", true),
    FRAGMENTION_TOL("frag_tol", "The fragment ion tolerance (in Dalton, e.g. 0.5Da or PPM, e.g. 10ppm).", true),
    OUTPUT_FOLDER("output_folder", "The output folder for exporting the results.", true),
    GENERATE_METAPROTEINS("generate_metaproteins", "Turn meta-protein generation (aka. protein grouping) on or off (1: on, 0: off, default is '1').", false),
    ITERATIVE_SEARCH("iterative_search", "Turn iterative (aka. two-step) searching on or off (1: on, 0: off, default is '0').", false),
    FDR_THRESHOLD("fdr_threshold", "The applied FDR threshold for filtering the results (default is 0.05 == 5% FDR).", false),
    THREADS("threads", "The number of threads to use for the processing. Default is the number of cores available.", false),
    XTANDEM("xtandem", "Turn X!Tandem+ database search algorithm on or off (1: on, 0: off, default is '1'). (At least one search engine needs to be enabled.)", false),
    COMET("comet", "Turn Comet database search algorithm on or off (1: on, 0: off, default is '0'). (At least one search engine needs to be enabled.)", false),
    MSGF("msgf", "Turn MS-GF+ database search algorithm on or off (1: on, 0: off, default is '0'). (At least one search engine needs to be enabled.)", false),
    XTANDEM_LOCATION("pepnovo_folder", "The X!Tandem executable, defaults to the OS dependent versions included with MPA.", false),
    COMET_LOCATION("directag_folder", "The Comet executable, defaults to the OS dependent versions included with MPA.", false),
    MSGF_LOCATION("pnovo_folder", "The MS-GF+ executable, defaults to the OS dependent versions included with MPA.", false);

    /**
     * Short Id for the CLI parameter.
     */
    public String id;
    /**
     * Explanation for the CLI parameter.
     */
    public String description;
    /**
     * Boolean indicating whether the parameter is mandatory.
     */
    public boolean mandatory;

    /**
     * Private constructor managing the various variables for the enum
     * instances.
     *
     * @param id the id
     * @param description the description
     * @param mandatory is the parameter mandatory
     */
    private CmdLineInterfaceParams(String id, String description, boolean mandatory) {
        this.id = id;
        this.description = description;
        this.mandatory = mandatory;
    }

    /**
     * Creates the options for the command line interface based on the possible
     * values.
     *
     * @param options the options object where the options will be added
     */
    public static void createOptionsCLI(Options options) {
        for (CmdLineInterfaceParams cliParams : values()) {
            options.addOption(cliParams.id, true, cliParams.description);
        }
        
        // Path setup
//        CmdLineInterfaceParams.createOptionsCLI(options);
    }

    /**
     * Returns the options as a string.
     *
     * @return the options as a string
     */
    public static String getOptionsAsString() {

        String output = "";
        String formatter = "%-35s";

        output += "Mandatory Parameters:\n\n";
        output += "-" + String.format(formatter, SPECTRUM_FILES.id) + " " + SPECTRUM_FILES.description + "\n";
        output += "-" + String.format(formatter, DATABASE_FILE.id) + " " + DATABASE_FILE.description + "\n";
        output += "-" + String.format(formatter, MISSED_CLEAV.id) + " " + MISSED_CLEAV.description + "\n";
        output += "-" + String.format(formatter, PRECURSOR_TOL.id) + " " + PRECURSOR_TOL.description + "\n";
        output += "-" + String.format(formatter, FRAGMENTION_TOL.id) + " " + FRAGMENTION_TOL.description + "\n";
        output += "-" + String.format(formatter, OUTPUT_FOLDER.id) + " " + OUTPUT_FOLDER.description + "\n";
        
        output += "\n\nOptional Parameters:\n\n";
        output += "-" + String.format(formatter, ITERATIVE_SEARCH.id) + " " + ITERATIVE_SEARCH.description + "\n";
        output += "-" + String.format(formatter, GENERATE_METAPROTEINS.id) + " " + GENERATE_METAPROTEINS.description + "\n";
        output += "-" + String.format(formatter, FDR_THRESHOLD.id) + " " + FDR_THRESHOLD.description + "\n";
        output += "-" + String.format(formatter, THREADS.id) + " " + THREADS.description + "\n";
        output += "-" + String.format(formatter, XTANDEM.id) + " " + XTANDEM.description + "\n";
        output += "-" + String.format(formatter, COMET.id) + " " + COMET.description + "\n";
        output += "-" + String.format(formatter, MSGF.id) + " " + MSGF.description + "\n";
        output += "-" + String.format(formatter, XTANDEM_LOCATION.id) + " " + XTANDEM_LOCATION.description + "\n";
        output += "-" + String.format(formatter, COMET_LOCATION.id) + " " + COMET_LOCATION.description + "\n";
        output += "-" + String.format(formatter, MSGF_LOCATION.id) + " " + MSGF_LOCATION.description + "\n";

        return output;
    }
}

