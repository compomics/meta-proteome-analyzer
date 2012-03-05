package de.mpa.job.instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.mpa.job.Job;
import de.mpa.job.SearchType;

/**
 * Executes an XTandem job.
 * @author Thilo Muth
 *
 */
public class XTandemJob extends Job {	
	
	private final static String INPUT_TARGET_FILE = "input_target.xml";
	private final static String INPUT_DECOY_FILE = "input_decoy.xml";
    private final static String PARAMETER_FILE = "parameters.xml";   
    private final static String TAXONOMY_FILE = "taxonomy.xml";
    private final static String TAXONOMY_DECOY_FILE = "taxonomy_decoy.xml";
    private String filename; 
	private File xTandemFile;
    private File inputFile;
    private File parameterFile;
    private File taxonomyFile;
	private File mgfFile;
	private String searchDB;
	private double fragmentTol;
	private double precursorTol;
	private String precursorUnit;	
	private SearchType searchType;

	/**
	 * Constructor for the XTandemJob..
	 * @param mgfFile
	 * @param fragmentTol
	 * @param precursorTol
	 * @param precursorUnit
	 */
	public XTandemJob(File mgfFile, String searchDB, double fragmentTol, double precursorTol, boolean precursorPPM, SearchType searchType) {
		this.mgfFile = mgfFile;
		this.searchDB = searchDB;
		this.fragmentTol = fragmentTol;
		this.precursorTol = precursorTol;
		if(precursorPPM){ 
			this.precursorUnit = "ppm";
		} else {
			this.precursorUnit = "Daltons";
		}
		this.searchType = searchType;
		this.xTandemFile = new File(JobConstants.XTANDEM_PATH);
		if(searchType == SearchType.TARGET){
			this.inputFile = new File(xTandemFile, INPUT_TARGET_FILE);
			this.filename = JobConstants.XTANDEM_OUTPUT_PATH + mgfFile.getName().substring(0, mgfFile.getName().length() - 4) + "_target.xml";
			buildTaxonomyFile();
			buildInputFile();
			
		} else if (searchType == SearchType.DECOY){
			this.inputFile = new File(xTandemFile, INPUT_DECOY_FILE);
			this.filename = JobConstants.XTANDEM_OUTPUT_PATH + mgfFile.getName().substring(0, mgfFile.getName().length() - 4) + "_decoy.xml";
			buildTaxonomyDecoyFile();
			buildInputDecoyFile();
		}
		buildParameterFile();
		initJob();
	}
	
	
	/**
	 * Constructs the input.xml file needed for the XTandem process.
	 */
	private void buildInputFile(){
	       
	        try {
	            BufferedWriter bw = new BufferedWriter(new FileWriter(inputFile));
	            bw.write("<?xml version=\"1.0\"?>\n"
	                    + "<bioml>\n"
	                    + "\t<note type=\"input\" label=\"list path, default parameters\">" + PARAMETER_FILE + "</note>\n"
	                    + "\t<note type=\"input\" label=\"list path, taxonomy information\">" + TAXONOMY_FILE + "</note>\n"
	                    + "\t<note type=\"input\" label=\"protein, taxon\">"  + searchDB + "</note>\n"
	                    + "\t<note type=\"input\" label=\"spectrum, path\">"+ mgfFile.getAbsolutePath() + "</note>\n"
	                    + "\t<note type=\"input\" label=\"output, path\">" + filename + "</note>\n"
	                    + "</bioml>\n");
	            bw.flush();
	            bw.close();
	        } catch (IOException ioe) {
	           ioe.printStackTrace();
	        }
	    }
	
	/**
	 * Constructs the input.xml file needed for the XTandem process.
	 */
	private void buildInputDecoyFile(){
	       
	        try {
	            BufferedWriter bw = new BufferedWriter(new FileWriter(inputFile));
	            bw.write("<?xml version=\"1.0\"?>\n"
	                    + "<bioml>\n"
	                    + "\t<note type=\"input\" label=\"list path, default parameters\">" + PARAMETER_FILE + "</note>\n"
	                    + "\t<note type=\"input\" label=\"list path, taxonomy information\">" + TAXONOMY_DECOY_FILE + "</note>\n"
	                    + "\t<note type=\"input\" label=\"protein, taxon\">"  + searchDB  + "_decoy" + "</note>\n"
	                    + "\t<note type=\"input\" label=\"spectrum, path\">"+ mgfFile.getAbsolutePath() + "</note>\n"
	                    + "\t<note type=\"input\" label=\"output, path\">" + filename + "</note>\n"
	                    + "</bioml>\n");
	            bw.flush();
	            bw.close();
	        } catch (IOException ioe) {
	           ioe.printStackTrace();
	        }
	    }
	
	/**
     * This method builds taxonomy file.
     */
    public void buildTaxonomyFile() {
        taxonomyFile = new File(xTandemFile, TAXONOMY_FILE);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(taxonomyFile));
            bw.write(
                    "<?xml version=\"1.0\"?>\n"
                            + "<bioml label=\"x! taxon-to-file matching list\">\n"
                            + "\t<taxon label=\"" + searchDB + "\">\n"
                            + "\t\t<file format=\"peptide\" URL=\"" + JobConstants.FASTA_PATH + searchDB + ".fasta" + "\" />\n"
                            + "\t</taxon>\n"
                            + "</bioml>");
            bw.flush();
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
	/**
     * This method builds taxonomy file.
     */
    public void buildTaxonomyDecoyFile() {
        taxonomyFile = new File(xTandemFile, TAXONOMY_DECOY_FILE);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(taxonomyFile));
            bw.write(
                    "<?xml version=\"1.0\"?>\n"
                            + "<bioml label=\"x! taxon-to-file matching list\">\n"
                            + "\t<taxon label=\"" + searchDB + "_decoy" + "\">\n"
                            + "\t\t<file format=\"peptide\" URL=\"" + JobConstants.FASTA_PATH + searchDB + "_decoy.fasta" + "\" />\n"
                            + "\t</taxon>\n"
                            + "</bioml>");
            bw.flush();
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    /**
     * Constructs the parameters file.
     */
    private void buildParameterFile() {

        parameterFile = new File(xTandemFile, PARAMETER_FILE);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(parameterFile));
            bw.write(
                    "<?xml version=\"1.0\"?>\n"
                            + "<?xml-stylesheet type=\"text/xsl\" href=\"tandem-input-style.xsl\"?>\n"
                            + "<bioml>\n"
                            + "<note>list path parameters</note>\n"
                            + "\t<note type=\"input\" label=\"list path, default parameters\">default_input.xml</note>\n"
                            + "\t\t<note>This value is ignored when it is present in the default parameter\n"
                            + "\t\tlist path.</note>\n"
                            + "\t<note type=\"input\" label=\"list path, taxonomy information\">" + TAXONOMY_FILE + "</note>\n"
                            + "\n"
                            + "<note>spectrum parameters</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, fragment monoisotopic mass error\">" + fragmentTol + "</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, parent monoisotopic mass error plus\">" + precursorTol + "</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, parent monoisotopic mass error minus\">" + precursorTol + "</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, parent monoisotopic mass isotope error\">yes</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, fragment monoisotopic mass error units\">Daltons</note>\n"
                            + "\t<note>The value for this parameter may be 'Daltons' or 'ppm': all other values are ignored</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, parent monoisotopic mass error units\">" + precursorUnit + "</note>\n"
                            + "\t\t<note>The value for this parameter may be 'Daltons' or 'ppm': all other values are ignored</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, fragment mass type\">monoisotopic</note>\n"
                            + "\t\t<note>values are monoisotopic|average </note>\n"
                            + "\n"
                            + "<note>spectrum conditioning parameters</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, dynamic range\">100.0</note>\n"
                            + "\t\t<note>The peaks read in are normalized so that the most intense peak\n"
                            + "\t\tis set to the dynamic range value. All peaks with values of less that\n"
                            + "\t\t1, using this normalization, are not used. This normalization has the\n"
                            + "\t\toverall effect of setting a threshold value for peak intensities.</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, total peaks\">50</note> \n"
                            + "\t\t<note>If this value is 0, it is ignored. If it is greater than zero (lets say 50),\n"
                            + "\t\tthen the number of peaks in the spectrum with be limited to the 50 most intense\n"
                            + "\t\tpeaks in the spectrum. X! tandem does not do any peak finding: it only\n"
                            + "\t\tlimits the peaks used by this parameter, and the dynamic range parameter.</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, maximum parent charge\">4</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, use noise suppression\">yes</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, minimum parent m+h\">500.0</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, minimum fragment mz\">150.0</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, minimum peaks\">15</note> \n"
                            + "\t<note type=\"input\" label=\"spectrum, threads\">4</note>\n"
                            + "\t<note type=\"input\" label=\"spectrum, sequence batch size\">1000</note>\n"
                            + "\t\n"
                            + "<note>residue modification parameters</note>\n"
                            + "\t<note type=\"input\" label=\"residue, modification mass\">57.021464@C</note>\n"
                            + "\t\t<note>carbamidomethyl C\n"
                            + "\t\t</note>\n"
                            + "\t<note type=\"input\" label=\"residue, potential modification mass\">15.994915@M</note>\n"
                            + "\t\t<note>oxidation of M\n"
                            + "\t\t</note>\n"
                            + "\t<note type=\"input\" label=\"residue, potential modification motif\"></note>\n"
                            + "\t\t<note>The format of this parameter is similar to residue, modification mass,\n"
                            + "\t\twith the addition of a modified PROSITE notation sequence motif specification.\n"
                            + "\t\tFor example, a value of 80@[ST!]PX[KR] indicates a modification\n"
                            + "\t\tof either S or T when followed by P, and residue and the a K or an R.\n"
                            + "\t\tA value of 204@N!{P}[ST]{P} indicates a modification of N by 204, if it\n"
                            + "\t\tis NOT followed by a P, then either an S or a T, NOT followed by a P.\n"
                            + "\t\tPositive and negative values are allowed.\n"
                            + "\t\t</note>\n"
                            + "\n"
                            + "<note>protein parameters</note>\n"
                            + "\t<note type=\"input\" label=\"protein, taxon\">all</note>\n"
                            + "\t\t<note>This value is interpreted using the information in taxonomy.xml.</note>\n"
                            + "\t<note type=\"input\" label=\"protein, cleavage site\">[RK]|{P}</note>\n"
                            + "\t\t<note>this setting corresponds to the enzyme trypsin. The first characters\n"
                            + "\t\tin brackets represent residues N-terminal to the bond - the '|' pipe -\n"
                            + "\t\tand the second set of characters represent residues C-terminal to the\n"
                            + "\t\tbond. The characters must be in square brackets (denoting that only\n"
                            + "\t\tthese residues are allowed for a cleavage) or french brackets (denoting\n"
                            + "\t\tthat these residues cannot be in that position). Use UPPERCASE characters.\n"
                            + "\t\tTo denote cleavage at any residue, use [X]|[X] and reset the \n"
                            + "\t\tscoring, maximum missed cleavage site parameter (see below) to something like 50.\n"
                            + "\t\t</note>\n"
                            + "\t<note type=\"input\" label=\"protein, modified residue mass file\"></note>\n"
                            + "\t<note type=\"input\" label=\"protein, cleavage C-terminal mass change\">+17.002735</note>\n"
                            + "\t<note type=\"input\" label=\"protein, cleavage N-terminal mass change\">+1.007825</note>\n"
                            + "\t<note type=\"input\" label=\"protein, N-terminal residue modification mass\">0.0</note>\n"
                            + "\t<note type=\"input\" label=\"protein, C-terminal residue modification mass\">0.0</note>\n"
                            + "\t<note type=\"input\" label=\"protein, homolog management\">no</note>\n"
                            + "\t\t<note>if yes, an upper limit is set on the number of homologues kept for a particular spectrum</note>\n"
                            + "\n"
                            + "<note>model refinement parameters</note>\n"
                            + "\t<note type=\"input\" label=\"refine\">yes</note>\n"
                            + "\t<note type=\"input\" label=\"refine, modification mass\"></note>\n"
                            + "\t<note type=\"input\" label=\"refine, sequence path\"></note>\n"
                            + "\t<note type=\"input\" label=\"refine, tic percent\">20</note>\n"
                            + "\t<note type=\"input\" label=\"refine, spectrum synthesis\">yes</note>\n"
                            + "\t<note type=\"input\" label=\"refine, maximum valid expectation value\">0.1</note>\n"
                            + "\t<note type=\"input\" label=\"refine, potential N-terminus modifications\">+42.010565@[</note>\n"
                            + "\t<note type=\"input\" label=\"refine, potential C-terminus modifications\"></note>\n"
                            + "\t<note type=\"input\" label=\"refine, unanticipated cleavage\">yes</note>\n"
                            + "\t<note type=\"input\" label=\"refine, potential modification mass\"></note>\n"
                            + "\t<note type=\"input\" label=\"refine, point mutations\">no</note>\n"
                            + "\t<note type=\"input\" label=\"refine, use potential modifications for full refinement\">no</note>\n"
                            + "\t<note type=\"input\" label=\"refine, point mutations\">no</note>\n"
                            + "\t<note type=\"input\" label=\"refine, potential modification motif\"></note>\n"
                            + "\t<note>The format of this parameter is similar to residue, modification mass,\n"
                            + "\t\twith the addition of a modified PROSITE notation sequence motif specification.\n"
                            + "\t\tFor example, a value of 80@[ST!]PX[KR] indicates a modification\n"
                            + "\t\tof either S or T when followed by P, and residue and the a K or an R.\n"
                            + "\t\tA value of 204@N!{P}[ST]{P} indicates a modification of N by 204, if it\n"
                            + "\t\tis NOT followed by a P, then either an S or a T, NOT followed by a P.\n"
                            + "\t\tPositive and negative values are allowed.\n"
                            + "\t\t</note>\n"
                            + "\n"
                            + "<note>scoring parameters</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, minimum ion count\">4</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, maximum missed cleavage sites\">2</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, x ions\">no</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, y ions\">yes</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, z ions\">no</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, a ions\">no</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, b ions\">yes</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, c ions\">no</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, cyclic permutation\">no</note>\n"
                            + "\t\t<note>if yes, cyclic peptide sequence permutation is used to pad the scoring histograms</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, include reverse\">no</note>\n"
                            + "\t\t<note>if yes, then reversed sequences are searched at the same time as forward sequences</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, cyclic permutation\">no</note>\n"
                            + "\t<note type=\"input\" label=\"scoring, include reverse\">no</note>\n"
                            + "\n"
                            + "<note>output parameters</note>\n"
                            + "\t<note type=\"input\" label=\"output, log path\"></note>\n"
                            + "\t<note type=\"input\" label=\"output, message\"></note>\n"
                            + "\t<note type=\"input\" label=\"output, one sequence copy\">no</note>\n"
                            + "\t<note type=\"input\" label=\"output, sequence path\"></note>\n"
                            + "\t<note type=\"input\" label=\"output, path\">output.xml</note>\n"
                            + "\t<note type=\"input\" label=\"output, sort results by\">spectrum</note>\n"
                            + "\t\t<note>values = protein|spectrum (spectrum is the default)</note>\n"
                            + "\t<note type=\"input\" label=\"output, path hashing\">no</note>\n"
                            + "\t\t<note>values = yes|no</note>\n"
                            + "\t<note type=\"input\" label=\"output, xsl path\">tandem-style.xsl</note>\n"
                            + "\t<note type=\"input\" label=\"output, parameters\">yes</note>\n"
                            + "\t\t<note>values = yes|no</note>\n"
                            + "\t<note type=\"input\" label=\"output, performance\">yes</note>\n"
                            + "\t\t<note>values = yes|no</note>\n"
                            + "\t<note type=\"input\" label=\"output, spectra\">yes</note>\n"
                            + "\t\t<note>values = yes|no</note>\n"
                            + "\t<note type=\"input\" label=\"output, histograms\">yes</note>\n"
                            + "\t\t<note>values = yes|no</note>\n"
                            + "\t<note type=\"input\" label=\"output, proteins\">yes</note>\n"
                            + "\t\t<note>values = yes|no</note>\n"
                            + "\t<note type=\"input\" label=\"output, sequences\">yes</note>\n"
                            + "\t\t<note>values = yes|no</note>\n"
                            + "\t<note type=\"input\" label=\"output, one sequence copy\">no</note>\n"
                            + "\t\t<note>values = yes|no, set to yes to produce only one copy of each protein sequence in the output xml</note>\n"
                            + "\t<note type=\"input\" label=\"output, results\">all</note>\n"
                            + "\t\t<note>values = all|valid|stochastic</note>\n"
                            + "\t<note type=\"input\" label=\"output, maximum valid expectation value\">1.0</note>\n"
                            + "\t\t<note>value is used in the valid|stochastic setting of output, results</note>\n"
                            + "\t<note type=\"input\" label=\"output, histogram column width\">30</note>\n"
                            + "\t\t<note>values any integer greater than 0. Setting this to '1' makes cutting and pasting histograms\n"
                            + "\t\tinto spread sheet programs easier.</note>\n"
                            + "<note type=\"description\">ADDITIONAL EXPLANATIONS</note>\n"
                            + "\t<note type=\"description\">Each one of the parameters for X! tandem is entered as a labeled note\n"
                            + "\t\t\tnode. In the current version of X!, keep those note nodes\n"
                            + "\t\t\ton a single line.\n"
                            + "\t</note>\n"
                            + "\t<note type=\"description\">The presence of the type 'input' is necessary if a note is to be considered\n"
                            + "\t\t\tan input parameter.\n"
                            + "\t</note>\n"
                            + "\t<note type=\"description\">Any of the parameters that are paths to files may require alteration for a \n"
                            + "\t\t\tparticular installation. Full path names usually cause the least trouble,\n"
                            + "\t\t\tbut there is no reason not to use relative path names, if that is the\n"
                            + "\t\t\tmost convenient.\n"
                            + "\t</note>\n"
                            + "\t<note type=\"description\">Any parameter values set in the 'list path, default parameters' file are\n"
                            + "\t\t\treset by entries in the normal input file, if they are present. Otherwise,\n"
                            + "\t\t\tthe default set is used.\n"
                            + "\t</note>\n"
                            + "\t<note type=\"description\">The 'list path, taxonomy information' file must exist.\n"
                            + "\t\t</note>\n"
                            + "\t<note type=\"description\">The directory containing the 'output, path' file must exist: it will not be created.\n"
                            + "\t\t</note>\n"
                            + "\t<note type=\"description\">The 'output, xsl path' is optional: it is only of use if a good XSLT style sheet exists.\n"
                            + "\t\t</note>\n"
                            + "\n"
                            + "</bioml>\n");
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
		setDescription("X!TANDEM " + searchType.name() + " SEARCH");
		
		// full path to executable
		procCommands.add(xTandemFile.getAbsolutePath() + File.separator + JobConstants.XTANDEM_EXE);

		// Link to the input file
		procCommands.add(inputFile.getAbsolutePath());

		procCommands.trimToSize();
		procBuilder = new ProcessBuilder(procCommands);
		procBuilder.directory(xTandemFile);
		
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
	
	/**
	 * Returns the actual XTandem outfile name including the path.
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}
}