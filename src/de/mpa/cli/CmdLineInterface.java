package de.mpa.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.compomics.util.Util;

import de.mpa.client.Client;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.ExportFields;
import de.mpa.client.model.FileExperiment;
import de.mpa.client.model.FileProject;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinFactory;
import de.mpa.client.settings.CometParameters;
import de.mpa.client.settings.MSGFParameters;
import de.mpa.client.settings.Parameter;
import de.mpa.client.settings.PostProcessingParameters;
import de.mpa.client.settings.XTandemParameters;
import de.mpa.client.ui.dialogs.ExportDialog;
import de.mpa.io.GenericContainer;
import de.mpa.io.ResultExporter;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.io.fasta.FastaUtilities;
import de.mpa.task.TaskManager;
import de.mpa.task.instances.SpectraTask;

/**
 * The class handles the three stages of definition, parsing and interrogation.
 *  
 * @author Thilo Muth
 *
 */
public class CmdLineInterface {
	
	/**
	 * The command line interface input.
	 */
	private CmdLineInterfaceInput cliInput;
	
	/**
     * Construct a new CmdLineInterface runnable from a CmdLineInterfaceInput Bean. 
     *
     * @param args the command line arguments
     */
    public CmdLineInterface(String[] args) {
        try {
            Options cmdOptions = new Options();
            CmdLineInterfaceParams.createOptionsCLI(cmdOptions);
            BasicParser parser = new BasicParser();
            CommandLine line = parser.parse(cmdOptions, args);

            if (!CmdLineInterfaceInput.isValidStartup(line)) {
                PrintWriter lPrintWriter = new PrintWriter(System.out);
                lPrintWriter.print("\n======================" + System.getProperty("line.separator"));
                lPrintWriter.print("MPA Portable CLI (Command Line Interface)" + System.getProperty("line.separator"));
                lPrintWriter.print("======================" + System.getProperty("line.separator"));
                lPrintWriter.print(getHeader());
                lPrintWriter.print(CmdLineInterfaceParams.getOptionsAsString());
                lPrintWriter.flush();
                lPrintWriter.close();

                System.exit(1);
            } else {
                cliInput = new CmdLineInterfaceInput(line);
                call();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    /**
     * MPA Portable CLI header message when printing the usage.
     */
    private static String getHeader() {
        return System.getProperty("line.separator")
                + "MPA Portable CLI performs the analysis of metaproteomics data using the database search algorithms X!Tandem, Comet and MS-GF+." + System.getProperty("line.separator")
                + System.getProperty("line.separator")
                + "Spectra must be provided in the Mascot Generic File (MGF) format." + System.getProperty("line.separator")
                + System.getProperty("line.separator")
                + "For further help see https://github.com/compomics/meta-proteome-analyzer." + System.getProperty("line.separator")
                + System.getProperty("line.separator")
                + "Or contact the developer (Thilo Muth) mutht@rki.de" + System.getProperty("line.separator")
                + System.getProperty("line.separator")
                + "----------------------"
                + System.getProperty("line.separator")
                + "OPTIONS"
                + System.getProperty("line.separator")
                + "----------------------" + System.getProperty("line.separator")
                + "\n";
    }
    
    /**
     * Calling this method will start the MPA processing. 
     */
    public Object call() {
        try {
        	// Initialize client.
        	Client.init(false);
        	Client client = Client.getInstance();
        	// TODO: Assign the executables right away!
        	File xtandemExecutable = cliInput.getXTandemExecutable();
            File cometExecutable = cliInput.getCometExecutable();
            File msgfExecutable = cliInput.getMSGFExecutable();
            
			// Collect search settings.
			DbSearchSettings searchSettings = new DbSearchSettings();
			searchSettings.setXTandem(cliInput.isXTandemEnabled());
			if (searchSettings.useXTandem()) {
				XTandemParameters xTandemParams = new XTandemParameters();
				Parameter threadsParameter = xTandemParams.get("spectrum, threads");
				threadsParameter.setValue(cliInput.getNumberOfThreads());
				xTandemParams.setValue("spectrum, threads", threadsParameter);
				searchSettings.setXTandemParams(xTandemParams.toString());
			}
			searchSettings.setComet(cliInput.isCometEnabled());
			if (searchSettings.useComet()) {
				CometParameters cometParams = new CometParameters();
				Parameter threadsParameter = cometParams.get("thread");
				threadsParameter.setValue(cliInput.getNumberOfThreads());
				cometParams.setValue("thread", threadsParameter);
				searchSettings.setCometParams(cometParams.toString());
			}
			searchSettings.setMSGF(cliInput.isMsgfEnabled());
			if (searchSettings.useMSGF()) {
				MSGFParameters msgfParams = new MSGFParameters();
				Parameter threadsParameter = msgfParams.get("thread");
				threadsParameter.setValue(cliInput.getNumberOfThreads());
				msgfParams.setValue("thread", threadsParameter);
				searchSettings.setMSGFParams(msgfParams.toString());
			}
			
			searchSettings.setFastaFilePath(cliInput.getDatabaseFile().getAbsolutePath());
			searchSettings.setMissedCleavages(cliInput.getNumberOfMissedCleavages());
			
			// Retrieve tolerance settings.
			if (cliInput.getFragmentIonTol().contains("da")) {
				double fragmentIonTol = Double.parseDouble(cliInput.getFragmentIonTol().substring(0, cliInput.getFragmentIonTol().indexOf("da")));
				searchSettings.setFragIonTol(fragmentIonTol);
			}
			if (cliInput.getPrecursorTol().contains("da")) {
				double precursorTol = Double.parseDouble(cliInput.getPrecursorTol().substring(0, cliInput.getPrecursorTol().indexOf("da")));
				searchSettings.setPrecIonTol(precursorTol);
				searchSettings.setPrecIonTolPPM(false);
			} else if (cliInput.getPrecursorTol().contains("ppm")) {
				double precursorTol = Double.parseDouble(cliInput.getPrecursorTol().substring(0, cliInput.getPrecursorTol().indexOf("ppm")));
				searchSettings.setPrecIonTol(precursorTol);
				searchSettings.setPrecIonTolPPM(true);
			}
			searchSettings.setIterativeSearch(cliInput.isIterativeSearchEnabled());
			searchSettings.setIterativeSearchSettings("0");
			
			// Create default CLI project + experiment.
			FileProject cliProject = new FileProject();
			cliProject.setTitle("CLI Project");
			FileExperiment cliExperiment = new FileExperiment("CLI Experiment", new Date(), cliProject);
			cliExperiment.setId(1L);
			cliExperiment.setSpectrumFiles(cliInput.getSpectrumFiles());
			
			// Create FASTA index (if not existent yet)
			File indexFile = new File(searchSettings.getFastaFilePath() + ".fb");
			if (!indexFile.exists()) {
				FastaLoader fastaLoader = FastaLoader.getInstance();
				fastaLoader.setFastaFile(cliInput.getDatabaseFile());;
	    		System.out.println(new Date() + " Creating FASTA index file...");
				fastaLoader.loadFastaFile();
				fastaLoader.writeIndexFile();
				System.out.println(new Date() + " FASTA index file creation finished...");
			}
			
			File fastaFile = cliInput.getDatabaseFile();
			
			// Create FASTA decoy database (if not existent yet)
			if (fastaFile.isFile()) {
        		// Check whether decoy FASTA file already exists - if not: create one!
            	File decoyFastaFile = new File(fastaFile.getAbsolutePath().substring(0, fastaFile.getAbsolutePath().indexOf(".fasta")) + "_decoy.fasta");
            	if (!decoyFastaFile.exists()) {
            		System.out.println(new Date() + " Creating FASTA decoy file...");
            		decoyFastaFile = FastaUtilities.createDecoyDatabase(fastaFile);
            		System.out.println(new Date() + " FASTA decoy file creation finished...");
            	}
			}
			
			// Set MGF files within the client.
			client.setMgfFiles(cliInput.getSpectrumFiles());
			
			String concatenatedFileNames = "";
			for (File file : client.getMgfFiles()) {
				concatenatedFileNames += file.getName();
			}
			// Get instance of job manager.
			TaskManager jobManager = TaskManager.getInstance();
			// Clear the job manager to account for unfinished jobs in the queue.
			jobManager.clear();
			
			System.out.println(new Date() + " Indexing MS/MS spectra...");
			SpectraTask spectraJob = new SpectraTask(cliInput.getSpectrumFiles());
			jobManager.addJob(spectraJob);
			jobManager.run();
			System.out.println(new Date() + " " + GenericContainer.numberTotalSpectra + " MS/MS spectra have been indexed.");
			System.out.println(new Date() + " Database search running (using " + cliInput.getNumberOfThreads() + " threads)...");
			long start = System.currentTimeMillis();
             
			// Run the searches
			client.runSearches(searchSettings);
			
			long stop = System.currentTimeMillis();
			long processingTimeMilliseconds = stop - start;
	        double processingTimeSeconds = ((double) processingTimeMilliseconds) / 1000;
	        int nSeconds = (int) processingTimeSeconds;
	        
			System.out.println(new Date() + " Database search finished ("+ nSeconds +" seconds).");
			
			// Retrieve the search result.
			System.out.println(new Date() + " Retrieving search results.");
			DbSearchResult dbSearchResult = cliExperiment.getSearchResult();
			System.out.println(new Date() + " Search results retrieval finished.");
			
			// Apply post-processing parameters.
			PostProcessingParameters postProcessingParams = new PostProcessingParameters();
			Parameter fdrParameter = postProcessingParams.get("FDR");
			fdrParameter.setValue(cliInput.getFDRThreshold());
			postProcessingParams.setValue("FDR", fdrParameter);
			int fdrThreshold = (int) ((Double) fdrParameter.getValue() * 100);
			// Generate meta-proteins
			System.out.println(new Date() + " Generating meta-proteins and applying FDR threshold of " + fdrThreshold + "%...");
			MetaProteinFactory.determineTaxonomyAndCreateMetaProteins(dbSearchResult, postProcessingParams);
			System.out.println(new Date() + " Meta-protein generation and FDR filtering finished.");
			
			// Generate a list of default export headers.
			System.out.println(new Date() + " Exporting processed results to the output folder...");
			ExportDialog exportDialog = new ExportDialog(null, "", false, false, ExportFields.getInstance());
			exportDialog.collectHeaderSet();
			ResultExporter.exportProteins(cliInput.getOutputFile().getAbsolutePath() + File.separator + concatenatedFileNames + "_proteins.csv", dbSearchResult, exportDialog.getExportHeaders());
			ResultExporter.exportPeptides(cliInput.getOutputFile().getAbsolutePath() + File.separator + concatenatedFileNames + "_peptides.csv", dbSearchResult, exportDialog.getExportHeaders());
			ResultExporter.exportPSMs(cliInput.getOutputFile().getAbsolutePath() + File.separator + concatenatedFileNames + "_psms.csv", dbSearchResult, exportDialog.getExportHeaders());
			ResultExporter.exportIdentifiedSpectra(cliInput.getOutputFile().getAbsolutePath() + File.separator + concatenatedFileNames + "_spectrum_ids.csv", dbSearchResult, exportDialog.getExportHeaders());
			ResultExporter.exportMetaProteins(cliInput.getOutputFile().getAbsolutePath() + File.separator + concatenatedFileNames + "_metaproteins.csv", dbSearchResult, exportDialog.getExportHeaders());
			ResultExporter.exportMetaProteinTaxonomy(cliInput.getOutputFile().getAbsolutePath() + File.separator + concatenatedFileNames + "_metaprotein_taxa.csv", dbSearchResult, exportDialog.getExportHeaders());
			System.out.println(new Date() + " Processed results export finished.");
			
			// Empty the search hits from the GenericContainer
			GenericContainer.SearchHits.clear();
        } catch (Exception e) {
        	e.printStackTrace();
            return 1;
        }
        return 0;
    }
	
    /**
     * Starts the command line interface of the MPA portable - used as main class in the JAR file.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new CmdLineInterface(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	/**
     * Returns a list of files as imported from the command line option. 
     * 
     * @param optionInput the command line option 
     * @param fileExtentions the file extensions to be considered 
     * @return a list of file candidates 
     * @throws FileNotFoundException exception thrown whenever a file is not 
     * found 
     */ 
    public static List<File> getFiles(String optionInput, List<String> fileExtentions) throws FileNotFoundException { 
        List<File> result = new ArrayList<File>(); 
        List<String> files = splitInput(optionInput); 
        if (files.size() == 1) { 
            File testFile = new File(files.get(0)); 
            if (testFile.exists()) { 
                if (testFile.isDirectory()) { 
                    for (File childFile : testFile.listFiles()) { 
                        String fileName = Util.getFileName(childFile.getAbsolutePath()); 
                        for (String extention : fileExtentions) { 
                            if (fileName.toLowerCase().endsWith(extention.toLowerCase())) { 
                                if (childFile.exists()) { 
                                    result.add(childFile); 
                                    break; 
                                } else { 
                                    throw new FileNotFoundException(childFile.getAbsolutePath() + " not found."); 
                                } 
                            } 
                        } 
                    } 
                } else { 
                    String fileName = Util.getFileName(testFile.getAbsolutePath()); 
                    for (String extention : fileExtentions) { 
                        if (fileName.toLowerCase().endsWith(extention.toLowerCase())) { 
                            result.add(testFile); 
                            break; 
                        } 
                    } 
                } 
            } else { 
                throw new FileNotFoundException(files.get(0) + " not found."); 
            } 
        } else { 
            for (String file : files) { 
                for (String extention : fileExtentions) { 
                    if (file.toLowerCase().endsWith(extention.toLowerCase())) { 
                        File testFile = new File(file); 
                        if (testFile.exists()) { 
                            result.add(testFile); 
                        } else { 
                            throw new FileNotFoundException(file + " not found."); 
                        } 
                        break; 
                    } 
                } 
            } 
        } 
        return result; 
    } 
    
    /**
     * Splits the input of comma separated command line input and returns the 
     * results as an arraylist. 
     * 
     * @param cliInput the CLI input 
     * @return an arraylist containing the results, empty list if empty string 
     */ 
    public static List<String> splitInput(String cliInput) { 
        List<String> results = new ArrayList<String>(); 
 
        // empty input, return the empty list 
        if (cliInput == null || cliInput.trim().length() == 0) { 
            return results; 
        } 
 
        for (String tempInput : cliInput.split(",")) { 
            results.add(tempInput.trim()); 
        } 
        return results; 
    } 

}
