package de.mpa.client.blast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.mpa.client.Constants;
import de.mpa.client.blast.DbEntry.DB_Type;

/**
 * Class to BLAST a a certain sequence
 * @author Robert Heyer
 */
public class RunBlast {
	
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
	
/**
 * Methode to build and run a BLAST query
 * @param blastFile. The file of the BLAST algorithm.
 * @param database. The database for the BLAST algorithm
 * @param evalue. The evalue cutoff for the BLAST algorithm
 * @param dbEntry. The dbEntry for the BLAST query
 */
	public static BlastResult blast(String blastFile, String database, double evalue, DbEntry dbEntry){
		// Result object
		BlastResult blastRes = new BlastResult();
		
		// Creates dummy FASTA
		try {
			createDummFasta(dbEntry);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Construct BLAST query
		ArrayList<String> blastQuery = new ArrayList<String>();
		blastQuery.add(blastFile);
		// Add database 
		blastQuery.add("-db");
		blastQuery.add(database);
		// Add input file
		blastQuery.add("-query");
		blastQuery.add(Constants.BLAST_DUMMY_FASTA_FILE);
		// Add output file
		blastQuery.add("-out");
		blastQuery.add(Constants.BLAST_OUTPUT_XML);
		// Add evalue threshold
		blastQuery.add("-evalue");
		blastQuery.add(""+ evalue);
		
		// Add output format xml
		blastQuery.add("-outfmt");
		blastQuery.add(Integer.toString(5));
		
		blastQuery.trimToSize();

		// Construct Process
		Process process = null;
		try {
//			System.out.println(blastQuery);
			ProcessBuilder pb = new ProcessBuilder(blastQuery);
//			pb.directory(new File(""));
			pb.redirectErrorStream(true);
			process = pb.start();
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}finally{
			process.destroy();
		}
		
		return blastRes = BlastParser.parseBlastHit(Constants.BLAST_OUTPUT_XML);
	}

	/**
	 * CreateDummyFasta
	 * @param dbEntry. The protein entry, which should be BLASTed
	 * @throws IOException
	 */
	private static void createDummFasta(DbEntry dbEntry) throws IOException{{
			// Write identifier
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Constants.BLAST_DUMMY_FASTA_FILE)));
			// String of the current line.
			bw.write(DB_Type.UNIPROTSPROT.dbStartFlag + dbEntry.getIdentifier());
			// Add subheaders if more than two one entry is available (first is the identifier)
			List<String> subHeader = dbEntry.getSubHeader();
			if (subHeader != null && subHeader.size()>1) {
				for (int i = 1; i < subHeader.size(); i++) {
					bw.write("|" + subHeader.get(i) );
				}
			}
			bw.newLine();
			bw.write(dbEntry.getSequence());
			bw.newLine();
			//Flushed the writer
			bw.flush();
		}
	}
}
