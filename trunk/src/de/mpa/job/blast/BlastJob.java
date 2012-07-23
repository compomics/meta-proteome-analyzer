package de.mpa.job.blast;

import java.io.File;

import de.mpa.job.Job;
import de.mpa.job.instances.JobConstants;
import de.mpa.parser.pepnovo.QueryFile;
import de.mpa.parser.pepnovo.QueryParser;

/**
 * This class represents a BLAST job. It takes the input sequences from the query file and compares it to a FASTA database by starting an actual blastp search.
 * @author T.Muth
 *
 */
public class BlastJob extends Job {

	private File blastFile;
    private String searchDB;
    private File queryInput;
    private File mgfFile;

    public BlastJob(File mgfFile, String searchDB) {
    	this.blastFile = new File(JobConstants.BLAST_PATH);
    	this.mgfFile = mgfFile;
        this.searchDB = searchDB;
        buildQueryFile();
        initJob();
    }
    
    /**
     * Parses the PepNovo+ query file and writes a Blast compatible query input file.
     */
    private void buildQueryFile() {
    	QueryFile queryFile = QueryParser.read(JobConstants.PEPNOVO_OUTPUT_PATH + mgfFile.getName() + "_query.txt");
    	this.queryInput = new File(QueryParser.write(queryFile, queryFile.getFilename() + "_formatted"));
	}

	/**
	 * This method initializes the job.
	 */
	private void initJob() {
		// Full path to executable.
		procCommands.add(blastFile.getAbsolutePath() + "/" + JobConstants.BLAST_EXE);
		
		 // Target database.
		procCommands.add("-db");
		procCommands.add(JobConstants.FASTA_PATH + searchDB);
		
        // File with input Peptides.
		procCommands.add("-query");
		procCommands.add(queryInput.getAbsolutePath());
		
        // Results file.
		procCommands.add("-out");
		procCommands.add(JobConstants.PEPNOVO_OUTPUT_PATH + mgfFile.getName() + ".out");
		
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
	
	
	/**
	 * TODO: Writes the final output with the perfect (100%) blast hits.
	 * The parsing should be done afterwards!
	 * 
	 */
//	private void parseAndWrite() {		
//		try {			
//			final BlastParser parser = new BlastParser(blastOutput);			
//			final String output = blastOutput.getAbsolutePath().substring(0, blastOutput.getAbsolutePath().indexOf(suffix));
//			finalOutput = new File(output + "_blast.out");
//			final BufferedWriter writer = new BufferedWriter(new FileWriter(finalOutput));
//			final List<BlastQuery> queries = parser.getQueries();
//			for (BlastQuery q : queries) {
//				final List<BlastHit> hits = q.getPerfectBlastHits();
//				for (BlastHit hit : hits) {
//					Entry entry = null;
//					String targetid = hit.getTargetID();
//					if(targetid.contains(".fa")){						
//						final int index = Integer.valueOf(targetid.substring(0, targetid.indexOf("_")));
//						// Get the specific FASTA entry						
//						entry = fastaDB.getEntry(index);
//						writer.write(hit.getParentQuery().getQueryID() + "\t" + hit.getParentQuery().getQuerySequence() + "\t" + index + "\t" + entry.getName() + "\t"	+ hit.getTargetStart() + "\t" + hit.getTargetEnd() + "\t" + entry.getSequenceAsString());
//						writer.newLine();	
//					} else {						
//						entry = fastaDB.getEntry(targetid);
//						writer.write(hit.getParentQuery().getQueryID() + "\t" + hit.getParentQuery().getQuerySequence() + "\t" + fastaDB.getID(targetid) + "\t" + entry.getName() + "\t"	+ hit.getTargetStart() + "\t" + hit.getTargetEnd() + "\t" + entry.getSequenceAsString());
//						writer.newLine();	
//					}
//				}
//			}
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
}
