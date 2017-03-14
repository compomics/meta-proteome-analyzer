package de.mpa.db.job.blast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BlastParser {

	/**
	 * Blast queries.
	 */
	private final List<BlastQuery> blastQueries = new ArrayList<BlastQuery>();

	/**
	 * The file handle to the Blast output file.
	 */
	private final File blastOutputFile;

	/**
	 * Constructs a parser for Blast output.
	 * 
	 * @param blastOutputFile
	 */
	public BlastParser(File blastOutputFile) {
		this.blastOutputFile = blastOutputFile;
	}

	/**
	 * This method initializes the parser by separating the queries as
	 * individual objects.
	 */
	public void read() {
		try {
			// Create a reader for the file.
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.blastOutputFile)));

			StringBuffer strBuf = null;
			String line;
			while ((line = bReader.readLine()) != null) {
				if (line.toUpperCase().startsWith("# BLASTP")) {
					// New section has started
					if (strBuf != null) {
						BlastQuery lBlastQuery = new BlastQuery(strBuf.toString());
                        this.blastQueries.add(lBlastQuery);
					}
					strBuf = new StringBuffer();
				}
				if (strBuf != null) {
					strBuf.append(line).append("\n");
				}
			}

			if (strBuf == null) {
				bReader.close();
				throw new Exception("No BlastP output in the given file!!");
			} else if (!strBuf.toString().equals("")) {
				BlastQuery query = new BlastQuery(strBuf.toString());
                this.blastQueries.add(query);
			}
			bReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the number of queries.
	 * 
	 * @return Number of queries.
	 */
	public int getNumberOfQueries() {
		return this.blastQueries.size();
	}

	/**
	 * Returns the BLAST queries.
	 * 
	 * @return BLAST queries.
	 */
	public List<BlastQuery> getQueries() {
		return this.blastQueries;
	}
}
