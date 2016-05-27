package de.mpa.db.job.blast;

import java.io.File;

import de.mpa.db.job.Job;

/**
 * This class represents a BLAST job. It takes the input sequences from the query file and compares it to a FASTA database by starting an actual blastp search.
 * @author T.Muth
 *
 */
public class BlastJob extends Job {

	private File blastFile;
//    private String searchDB;
    private File queryInput;
//   private File mgfFile;

    public BlastJob(File mgfFile, String searchDB) {
//    	this.blastFile = new File(JobConstants.BLAST_PATH);
//    	this.mgfFile = mgfFile;
//        this.searchDB = searchDB;
        buildQueryFile();
        initJob();
    }
    
    /**
     * Parses the PepNovo+ query file and writes a Blast compatible query input file.
     */
    private void buildQueryFile() {
//    	QueryFile queryFile = QueryParser.read(JobConstants.PEPNOVO_OUTPUT_PATH + mgfFile.getName() + "_query.txt");
//    	this.queryInput = new File(QueryParser.write(queryFile, queryFile.getFilename() + "_formatted"));
	}

	/**
	 * This method initializes the job.
	 */
	private void initJob() {
		// Full path to executable.
//		procCommands.add(blastFile.getAbsolutePath() + "/" + JobConstants.BLAST_EXE);
		
		 // Target database.
		procCommands.add("-db");
//		procCommands.add(JobConstants.FASTA_PATH + searchDB);
		
        // File with input Peptides.
		procCommands.add("-query");
		procCommands.add(queryInput.getAbsolutePath());
		
        // Results file.
		procCommands.add("-out");
//		procCommands.add(JobConstants.PEPNOVO_OUTPUT_PATH + mgfFile.getName() + ".out");
		
		// Optimized for short searches with < 30 amino acids
		procCommands.add("-task");
		procCommands.add("blastp-short");
		
		procCommands.add("-matrix");
		procCommands.add("PAM30");
		
        // Limit the number of output hits.
        // BlastP Documentation:
        // "If the query range of a hit is enveloped by that of at least this many higher-scoring hits, delete the hit."
		procCommands.add("-culling_limit");
		procCommands.add(Integer.toString(10));
        
        // Word size
		procCommands.add("-word_size");
		procCommands.add(Integer.toString(2));
        
        // E-value
		procCommands.add("-evalue");
		procCommands.add(Integer.toString(20000));
		
		procCommands.add("-seg");
		procCommands.add("no");
		
		// Output format
		procCommands.add("-outfmt");
		procCommands.add(Integer.toString(7));
		
		procCommands.trimToSize();

		setDescription("BLAST");
		procBuilder = new ProcessBuilder(procCommands);

		procBuilder.directory(blastFile);
		// set error out and std out to same stream
		procBuilder.redirectErrorStream(true);
	}
}
