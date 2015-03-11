package de.mpa.client.blast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpa.client.Constants;
import de.mpa.client.blast.DbEntry.DB_Type;

/**
 * Class to BLAST a batch of sequences
 * @author Robert Heyer and Sebastian Dorl
 */
public class RunMultiBlast {
	
	 /** 
	  * The database against which the BLAST should be executed.
	  */
	public String database;
	
	/**
	 * BLAST algorithm
	 */
	public String blastFile;
	
	/**
	 * The e_value for the BLAST
	 */
	private double evalue;
	
	private List<DbEntry> queryList;
	
	private Map<String, BlastResult> blastResultMap;
	
	/**
	 * Constructor for a BLAST search process with multiple queries
	 * @param blastFile  The file of the BLAST algorithm executable
	 * @param database  The preformatted FASTA database for the BLAST algorithm
	 * @param evalue  The E-value cutoff for the BLAST algorithm
	 * @param entryList  The list of dbEntry objects for the BLAST query
	 */
	public RunMultiBlast(String blastFile, String database, double evalue, List<DbEntry> entryList){
		this.blastFile = blastFile;
		this.database = database;
		this.evalue = evalue;
		this.queryList = entryList;
	}
	
	/**
	 * Run NCBI BLASTP and retrieve the result.
	 * @throws IOException 
	 */
	public void blast() throws IOException{

		File fastaFile = File.createTempFile("blast_input", ".fasta");
		File outputFile = File.createTempFile("blast_output", ".out");
		
		// Create a temporary FASTA-File to serve as input for the BLAST
		BufferedWriter writer = new BufferedWriter(new FileWriter(fastaFile));
		for (DbEntry dbEntry : queryList) {
			// write the identifier
			writer.write(">" + dbEntry.getIdentifier());
			writer.newLine();
			// add the sequence
			writer.write(dbEntry.getSequence());
			writer.newLine();
		}
		writer.flush();
		writer.close();
		
		// Construct BLAST query
		ArrayList<String> blastQuery = new ArrayList<String>();
		blastQuery.add(blastFile);
		// Add database 
		blastQuery.add("-db");
		blastQuery.add(database);
		// Add input file
		blastQuery.add("-query");
		blastQuery.add(fastaFile.getAbsolutePath());
		// Add output file
		blastQuery.add("-out");
		blastQuery.add(outputFile.getAbsolutePath());
		// Add e-value threshold
		blastQuery.add("-evalue");
		blastQuery.add(Double.toString(evalue));
		// Add custom output format: 
		blastQuery.add("-outfmt");
		blastQuery.add("10 qacc sacc bitscore evalue stitle");
		// Add number of threads
		blastQuery.add("-num_threads");
		blastQuery.add(Integer.toString(8));
		
		blastQuery.trimToSize();

		// Construct Process
		Process process = null;
		try {
			ProcessBuilder builder = new ProcessBuilder(blastQuery);
			builder.redirectErrorStream(true);
			process = builder.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}finally{
			process.destroy();
		}
		
		// Parse contents of the output file
		BufferedReader reader = new BufferedReader(new FileReader(outputFile));
		String line;
		blastResultMap = new HashMap<String, BlastResult>();
		// parse line by line
		while ((line = reader.readLine()) != null) {
		    String[] splits = line.split("[,]");
		    String query = splits[0];
		    String subject = splits[1];
		    String bitscore = splits[2];
		    String evalue = splits[3];
		    // get accession from name
		    String[] sbjctsplit = subject.split("[|]");
		    String accession = sbjctsplit[1];
		    // get description from subject title
		    String[] titlesplit = splits[4].split("[ ]", 2);
		    String title =titlesplit[1];
		    
		    // make or get the result object
		    BlastResult result;
		    if (blastResultMap.keySet().contains(query)) { 
				// this query is known
		    	result = blastResultMap.get(query);
			} else {
				// query is new
				result = new BlastResult();
				result.setName(query);
				blastResultMap.put(query, result);
			}
		    
		    // add the hit information to the result
		    BlastHit hit = new BlastHit(accession, title);
		    hit.setScore(Double.parseDouble(bitscore));
		    hit.seteValue(Double.parseDouble(evalue));
		    result.putBlastHitsMap(hit);
		   
		}
		reader.close();
	}

	public Map<String, BlastResult> getBlastResultMap() {
		return blastResultMap;
	}

}
