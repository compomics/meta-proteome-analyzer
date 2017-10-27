package de.mpa.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import com.compomics.software.cli.CommandLineUtils;


/**
 * This class provides command line options for the MPA CLI application.
 *
 * @author Thilo Muth
 */
public class CmdLineInterfaceInput {
	    /**
	     * The spectrum files.
	     */
	    private List<File> spectrumFiles;
	    
	    /**
	     * The output folder.
	     */
	    private File outputFolder;
	    
	    /**
	     * The protein database file.
	     */
	    private File databaseFile;
	    
	    /**
	     * The number of maximum allowed missed cleavages.
	     */
	    private int nMissedCleavages;
	    
	    /**
	     * The precursor tolerance.
	     */
	    private String precursorTol;
	    
	    /**
	     * The fragment ion tolerance.
	     */
	    private String fragmentIonTol;
	    
	    /**
	     * Enables semi-tryptic cleavage, default == false.
	     */
	    private boolean semiTrypticCleavage = false;
	    
	    /**
	     * Enables fragmentation method, default is 1 (CID), the following values are possible:
	     * 1 = CID
	     * 2 = HCD
	     * 3 = ETD
	     */	    
	    private int fragmentationMethod = 1;
	    
	    /**
	     * Enables X!Tandem, default == true.
	     */
	    private boolean xTandemEnabled = true;
	    
	    /**
	     * Enables Comet, default == false.
	     */
	    private boolean cometEnabled = false;
	    
	    /**
	     * Enables MS-GF+, default == false.
	     */
	    private boolean msgfEnabled = false;
	    
	    /**
	     * Enables peptide indexing, default == true.
	     */
	    private boolean peptideIndexingEnabled = true;

	    /**
	     * Number of threads to use (optional parameter). Defaults to the number of cores available.
	     */
	    private int nThreads = Runtime.getRuntime().availableProcessors();
	    
	    /**
	     * Enables iterative search, default == false (optional parameter).
	     */
	    private int iterativeSearch = -1;
	    
	    /**
	     * Enables meta-protein generation, default == true (optional parameter).
	     */
	    private boolean metaProteinGenerationEnabled = true;
	    
	    /**
	     * The peptide rule chosen for meta-protein generation.
	     */
	    private int peptideRule = 0;
	    
	    /**
	     * The sequence cluster rule chosen for meta-protein generation (default is -1 == off).
	     */
	    private int clusterRule = -1;
	    
	    /**
	     * The (maximum) taxonomy rule for meta-protein generation (default is -1 == off).
	     */
	    private int taxonomyRule = -1;
	    
	    /**
	     * Applied FDR threshold, default = 0.05 (5% FDR) (optional parameter).
	     */
	    private double fdrThreshold = 0.05;

	    /**
	     * The CmdLineInterfaceInput receives the user-defined arguments from a command line.
	     * @param line the command line
	     * @throws FileNotFoundException thrown if a spectrum or search parameter file cannot be found
	     * @throws IOException thrown if there are problems accessing a spectrum or search parameter file
	     * @throws ClassNotFoundException thrown if the search parameters cannot be processed
	     */
	    public CmdLineInterfaceInput(CommandLine line) throws FileNotFoundException, IOException, ClassNotFoundException {

	        // Mandatory parameters.
	        spectrumFiles = getSpectrumFiles(line.getOptionValue(CmdLineInterfaceParams.SPECTRUM_FILES.id));
	        outputFolder = new File(line.getOptionValue(CmdLineInterfaceParams.OUTPUT_FOLDER.id));
	        databaseFile = new File(line.getOptionValue(CmdLineInterfaceParams.DATABASE_FILE.id));
	        nMissedCleavages = Integer.parseInt(line.getOptionValue(CmdLineInterfaceParams.MISSED_CLEAV.id).trim());
	        precursorTol = line.getOptionValue(CmdLineInterfaceParams.PRECURSOR_TOL.id).trim().toLowerCase();
	        fragmentIonTol = line.getOptionValue(CmdLineInterfaceParams.FRAGMENTION_TOL.id).trim().toLowerCase();
	        
	        // Database search engine parameters
	        if (line.hasOption(CmdLineInterfaceParams.XTANDEM.id)) {
	            String xtandemOption = line.getOptionValue(CmdLineInterfaceParams.XTANDEM.id);
	            xTandemEnabled = xtandemOption.trim().equals("1");
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.COMET.id)) {
	            String cometOption = line.getOptionValue(CmdLineInterfaceParams.COMET.id);
	            cometEnabled = cometOption.trim().equals("1");
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.MSGF.id)) {
	            String msgfOption = line.getOptionValue(CmdLineInterfaceParams.MSGF.id);
	            msgfEnabled = msgfOption.trim().equals("1");
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.PEPTIDE_INDEX.id)) {
	            String peptidIndexOption = line.getOptionValue(CmdLineInterfaceParams.PEPTIDE_INDEX.id);
	            peptideIndexingEnabled = peptidIndexOption.trim().equals("1");
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.SEMI_TRYPTIC.id)) {
	        	String semiTrypticOption = line.getOptionValue(CmdLineInterfaceParams.SEMI_TRYPTIC.id);
	            semiTrypticCleavage = semiTrypticOption.trim().equals("1");
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.FRAGMENTATION_METHOD.id)) {
	        	fragmentationMethod = Integer.parseInt(line.getOptionValue(CmdLineInterfaceParams.FRAGMENTATION_METHOD.id));
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.ITERATIVE_SEARCH.id)) {
	            iterativeSearch = Integer.parseInt(line.getOptionValue(CmdLineInterfaceParams.ITERATIVE_SEARCH.id).trim());
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.GENERATE_METAPROTEINS.id)) {
	            String generateMetaproteinsOption = line.getOptionValue(CmdLineInterfaceParams.GENERATE_METAPROTEINS.id);
	            metaProteinGenerationEnabled = generateMetaproteinsOption.trim().equals("1");
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.PEPTIDE_RULE.id)) {
	        	peptideRule = Integer.parseInt(line.getOptionValue(CmdLineInterfaceParams.PEPTIDE_RULE.id).trim());
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.CLUSTER_RULE.id)) {
	        	clusterRule = Integer.parseInt(line.getOptionValue(CmdLineInterfaceParams.CLUSTER_RULE.id).trim());
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.TAXONOMY_RULE.id)) {
	        	taxonomyRule = Integer.parseInt(line.getOptionValue(CmdLineInterfaceParams.TAXONOMY_RULE.id).trim());
	        }
	        
	        if (line.hasOption(CmdLineInterfaceParams.FDR_THRESHOLD.id)) {
	            fdrThreshold = Double.parseDouble(line.getOptionValue(CmdLineInterfaceParams.FDR_THRESHOLD.id).trim());
	        }

	        if (line.hasOption(CmdLineInterfaceParams.THREADS.id)) {
	            nThreads = Integer.parseInt(line.getOptionValue(CmdLineInterfaceParams.THREADS.id));
	        }
	    }

	    /**
	     * Returns the spectrum files.
	     * @return the spectrum files
	     */
	    public List<File> getSpectrumFiles() {
	        return spectrumFiles;
	    }

	    /**
	     * Returns the output folder.
	     * @return the output folder
	     */
	    public File getOutputFile() {
	        return outputFolder;
	    }

	    /**
	     * Returns the database file.
	     * @return the database file
	     */
	    public File getDatabaseFile() {
	        return databaseFile;
	    }
	    
	    /**
	     * Returns the number of maximum missed cleavages.
	     * @return the number of maximum missed cleavages 
	     */
	    public int getNumberOfMissedCleavages() {
			return nMissedCleavages;
		}
	    
	    /**
	     * Returns the precursor tolerance as string.
	     * @return the precursor tolerance
	     */
		public String getPrecursorTol() {
			return precursorTol;
		}
		
		/**
		 * Returns the fragment ion tolerance as string.
		 * @return the fragment ion tolerance
		 */
		public String getFragmentIonTol() {
			return fragmentIonTol;
		}

		/**
	     * Returns a list of spectrum files as imported from the command line option.
	     * @param optionInput the command line option
	     * @return a list of file candidates
	     * @throws FileNotFoundException exception thrown whenever a file is not found
	     */
	    public static ArrayList<File> getSpectrumFiles(String optionInput) throws FileNotFoundException {
	    	ArrayList<String> extentions = new ArrayList<String>();
	        extentions.add(".mgf");
	        return CommandLineUtils.getFiles(optionInput, extentions);
	    }
	    
	    /**
	     * Returns whether semi-tryptic cleavage is enabled.
	     * @return true if semi-tryptic cleavage is enabled.
	     */
	    public boolean isSemiTrypticCleavageEnabled() {
	    	return semiTrypticCleavage;
	    }
	    
	    /**
	     * Returns the fragmentation method id.
	     * @return the fragmentation  method id
	     */
	    public int getFragmentationMethod() {
			return fragmentationMethod;
		}
	    

		/**
	     * Returns whether X!Tandem is enabled.
	     * @return true if X!Tandem is enabled
	     */
	    public boolean isXTandemEnabled() {
			return xTandemEnabled;
		}
	    
	    /**
	     * Returns whether Comet is enabled.
	     * @return true if Comet is enabled
	     */
		public boolean isCometEnabled() {
			return cometEnabled;
		}
		
		/**
		 * Returns whether MS-GF+ is enabled.
		 * @return true if MS-GF+ is enabled
		 */
		public boolean isMsgfEnabled() {
			return msgfEnabled;
		}
		
		/**
		 * Returns whether peptide indexing is enabled.
		 * @return true if peptide indexing is enabled
		 */
		public boolean isPeptideIndexingEnabled() {
			return peptideIndexingEnabled;
		}

		/**
		 * Returns the iterative search identifier.
		 * @return the iterative search identifier
		 */
		public int getIterativeSearch() {
			return iterativeSearch;
		}
		
		/**
		 * Returns whether meta-protein generation is enabled.
		 * @return true if meta-protein generation is enabled
		 */
		public boolean isMetaProteinGenerationEnabled() {
			return metaProteinGenerationEnabled;
		}
		
		/**
		 * Returns the peptide rule identifier.
		 * @return the peptide rule identifier
		 */
		public int getPeptideRule() {
			return peptideRule;
		}
		
		/**
		 * Returns the sequence cluster rule identifier.
		 * @return the sequence cluster rule identifier
		 */
		public int getClusterRule() {
			return clusterRule;
		}
		
		/**
		 * Returns the taxonomy rule identifier.
		 * @return the taxonomy rule identifier
		 */
		public int getTaxonomyRule() {
			return taxonomyRule;
		}

		/**
		 * Returns the FDR threshold to be applied for filtering the results.
		 * @return the FDR threshold to be applied
		 */
		public double getFDRThreshold() {
			return fdrThreshold;
		}

		/**
	     * Returns the number of threads to use.
	     * @return the number of threads to use
	     */
	    public int getNumberOfThreads() {
	        return nThreads;
	    }

	    /**
	     * Verifies the command line start parameters.
	     * @param cmdLine the command line to validate
	     * @return true if the startup was valid
	     * @throws FileNotFoundException thrown if a spectrum file cannot be found
	     */
	    public static boolean isValidStartup(CommandLine cmdLine) throws IOException {

	        if (cmdLine.getOptions().length == 0) {
	            return false;
	        }

	        if (!cmdLine.hasOption(CmdLineInterfaceParams.SPECTRUM_FILES.id) || cmdLine.getOptionValue(CmdLineInterfaceParams.SPECTRUM_FILES.id).equals("")) {
	            System.out.println(System.getProperty("line.separator") + "Spectrum files not specified." + System.getProperty("line.separator"));
	            return false;
	        } else {
	            ArrayList<File> tempSpectrumFiles = CmdLineInterfaceInput.getSpectrumFiles(cmdLine.getOptionValue(CmdLineInterfaceParams.SPECTRUM_FILES.id));
	            for (File file : tempSpectrumFiles) {
	                if (!file.exists()) {
	                    System.out.println(System.getProperty("line.separator") + "File \'" + file.getName() + "\' not found." + System.getProperty("line.separator"));
	                    return false;
	                }
	            }
	        }

	        if (!cmdLine.hasOption(CmdLineInterfaceParams.OUTPUT_FOLDER.id) || cmdLine.getOptionValue(CmdLineInterfaceParams.OUTPUT_FOLDER.id).equals("")) {
	            System.out.println(System.getProperty("line.separator") + "Output folder not specified." + System.getProperty("line.separator"));
	            return false;
	        } else {
	            File file = new File((cmdLine.getOptionValue(CmdLineInterfaceParams.OUTPUT_FOLDER.id)));
	            if (!file.exists()) {
	                System.out.println(System.getProperty("line.separator") + "Output folder \'" + file.getName() + "\' not found." + System.getProperty("line.separator"));
	                return false;
	            }
	        }
	        
	        if (!cmdLine.hasOption(CmdLineInterfaceParams.DATABASE_FILE.id) || cmdLine.getOptionValue(CmdLineInterfaceParams.OUTPUT_FOLDER.id).equals("")) {
	            System.out.println(System.getProperty("line.separator") + "Database file not specified." + System.getProperty("line.separator"));
	            return false;
	        } else {
	            File file = new File((cmdLine.getOptionValue(CmdLineInterfaceParams.DATABASE_FILE.id)));
	            if (!file.exists()) {
	                System.out.println(System.getProperty("line.separator") + "Database file \'" + file.getName() + "\' not found." + System.getProperty("line.separator"));
	                return false;
	            }
	        }
	        
	        if (!cmdLine.hasOption(CmdLineInterfaceParams.MISSED_CLEAV.id) || cmdLine.getOptionValue(CmdLineInterfaceParams.MISSED_CLEAV.id).equals("")) {
	            System.out.println(System.getProperty("line.separator") + "Number of allowed missed cleavages not specified." + System.getProperty("line.separator"));
	            return false;
	        } else {
	            int nMissedCleavages = Integer.parseInt(cmdLine.getOptionValue(CmdLineInterfaceParams.MISSED_CLEAV.id));
	            
	            if (nMissedCleavages < 0 || nMissedCleavages > 5) {
	                System.out.println(System.getProperty("line.separator") + "Number of maximum missed cleavages must be a number between 0 and 5." + System.getProperty("line.separator"));
	                return false;
	            }
	        }
	        
	        if (!cmdLine.hasOption(CmdLineInterfaceParams.PRECURSOR_TOL.id) || cmdLine.getOptionValue(CmdLineInterfaceParams.PRECURSOR_TOL.id).equals("")) {
	            System.out.println(System.getProperty("line.separator") + "Precursor tolerance not specified." + System.getProperty("line.separator"));
	            return false;
	        } else {
	            String precTol = cmdLine.getOptionValue(CmdLineInterfaceParams.PRECURSOR_TOL.id).toLowerCase();
	            if (!precTol.endsWith("da") && !precTol.endsWith("ppm")) {
	            	System.out.println(System.getProperty("line.separator") + "Precursor tolerance must be provided either in ppm (e.g. 10ppm) or Dalton (e.g. 0.1Da)." + System.getProperty("line.separator"));
	                return false;
	            }
	        }
	        
	        if (!cmdLine.hasOption(CmdLineInterfaceParams.FRAGMENTION_TOL.id) || cmdLine.getOptionValue(CmdLineInterfaceParams.FRAGMENTION_TOL.id).equals("")) {
	            System.out.println(System.getProperty("line.separator") + "Fragment ion tolerance not specified." + System.getProperty("line.separator"));
	            return false;
	        } else {
	            String fragTol = cmdLine.getOptionValue(CmdLineInterfaceParams.FRAGMENTION_TOL.id).toLowerCase();
	            if (!fragTol.endsWith("da")) {
	            	System.out.println(System.getProperty("line.separator") + "Fragment ion tolerance must be provided either in Dalton (e.g. 0.5Da)." + System.getProperty("line.separator"));
	                return false;
	            }
	        }
	        return true;
	    }
}
