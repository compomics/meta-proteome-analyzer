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
        this.buildQueryFile();
        this.initJob();
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
        this.procCommands.add("-db");
//		procCommands.add(JobConstants.FASTA_PATH + searchDB);
		
        // File with input Peptides.
        this.procCommands.add("-query");
        this.procCommands.add(this.queryInput.getAbsolutePath());
		
        // Results file.
        this.procCommands.add("-out");
//		procCommands.add(JobConstants.PEPNOVO_OUTPUT_PATH + mgfFile.getName() + ".out");
		
		// Optimized for short searches with < 30 amino acids
        this.procCommands.add("-task");
        this.procCommands.add("blastp-short");

        this.procCommands.add("-matrix");
        this.procCommands.add("PAM30");
		
        // Limit the number of output hits.
        // BlastP Documentation:
        // "If the query range of a hit is enveloped by that of at least this many higher-scoring hits, delete the hit."
        this.procCommands.add("-culling_limit");
        this.procCommands.add(Integer.toString(10));
        
        // Word size
        this.procCommands.add("-word_size");
        this.procCommands.add(Integer.toString(2));
        
        // E-value
        this.procCommands.add("-evalue");
        this.procCommands.add(Integer.toString(20000));

        this.procCommands.add("-seg");
        this.procCommands.add("no");
		
		// Output format
        this.procCommands.add("-outfmt");
        this.procCommands.add(Integer.toString(7));

        this.procCommands.trimToSize();

        this.setDescription("BLAST");
        this.procBuilder = new ProcessBuilder(this.procCommands);

        this.procBuilder.directory(this.blastFile);
		// set error out and std out to same stream
        this.procBuilder.redirectErrorStream(true);
	}
}
