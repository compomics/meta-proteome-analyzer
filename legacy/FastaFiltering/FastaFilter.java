import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastaFilter {
	
	/**
	 * The FASTA file to be filtered.
	 */
	private File fastaFile;
	
	/**
	 * The list of filter string patterns.
	 */
	private List<String> filterStrings;

	/**
	 * Constructs a FASTA filter object instance.
	 * 
	 * @param fastaFile the FASTA file instance
	 * @param filterStrings the list of filter string patterns
	 */
	public FastaFilter(File fastaFile, List<String> filterStrings) {
		this.fastaFile = fastaFile;
		this.filterStrings = filterStrings;
	}

	/**
	 * Parses a specified FASTA file and writes protein entries whose headers
	 * match the specified filter strings to a separate output file.
	 * 
	 * @param fastaFile The file to be filtered
	 * @param filterStrings The filter string patterns
	 */
	public void filter() throws IOException {
		
		File fastaOutput = new File(fastaFile.getPath().replace(".", "_filtered."));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fastaOutput)));

		BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
		String line;
		boolean ofInterest = false;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(">")) {		// new header found
				ofInterest = false;
				for (String filterString : filterStrings) {
					ofInterest |= line.contains(filterString);
					if (ofInterest) {
						break;
					}
				}
				//					ofInterest = line.contains(filterString);
			}
			if (ofInterest) {
				bw.write(line);
				bw.flush();
				bw.write("\n");
			}
		}

	}
	
	/**
	 * This method parses proteinIDs and sequences from the FASTA file.
	 * 
	 * @return FastaDB db
	 */
	public FastaDB read(File fastaFile) throws IOException {
		FastaDB database = null;

		ArrayList<String> proteinIDs = new ArrayList<String>();
		ArrayList<String> proteinSeqs = new ArrayList<String>();
		
		StringBuffer stringBf = new StringBuffer();
		boolean firstline = true;
		
		BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
		String nextLine;
		while ((nextLine = reader.readLine()) != null) {
			// skip empty lines
			if (nextLine.trim().length() > 0) {
				// check for header lines
				if (nextLine.startsWith(">")) {
					// trim '>' character
					nextLine = nextLine.substring(1);
					if (!firstline) {
						// add sequence
						proteinSeqs.add(stringBf.toString());
						// re-initialize buffer
						stringBf = new StringBuffer();
					}
					// add header
					proteinIDs.add(nextLine);
				} else {
					// append partial sequence
					stringBf.append(nextLine);
				}
				firstline = false;
			}
		}
		proteinSeqs.add(stringBf.toString());

		// Map of FASTA entries
		Map<Integer, Entry> entryMap = new HashMap<Integer, Entry>();
		Map<String, Integer> entryIdMap = new HashMap<String, Integer>();

		for(int i = 0; i < proteinIDs.size(); i++){
			entryMap.put(i+1, new Entry((i+1), getFormattedName(proteinIDs.get(i)), proteinSeqs.get(i)));
			entryIdMap.put(getFormattedName(proteinIDs.get(i)), i+1);
		}

		// Instantiate the FastaDB object.
		database = new FastaDB(fastaFile.getName(), entryMap, entryIdMap);			
		reader.close();

		return database;
	}
	
	public String getFormattedName(String accession) {
		String[] split = accession.split(" ");
		return split[0];
	}

	/**
	 * Utility method to read entries from a specified FASTA file and write them
	 * out to a different file if the specified String parameters are contained
	 * in the protein headers.
	 * 
	 * @param args
	 *            The list of String arguments. First argument must be a path
	 *            pointing to the FASTA file to be filtered. The following
	 *            arguments specify String identifiers for pattern matching in
	 *            protein headers.
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Must specify a file path and at least one filter String pattern.");
			return;
		}
		
		File fastaFile = new File(args[0]);
		
		List<String> filterStrings = new ArrayList<String>();
		for (int i = 1; i < args.length; i++) {
			filterStrings.add(args[i]);
		}
		
		FastaFilter ff = new FastaFilter(fastaFile, filterStrings);

		try {
			System.out.print("Filtering... ");
			ff.filter();
			System.out.println("done.");
		} catch (IOException e) {
			System.err.println("aborted.");
			e.printStackTrace();
		}
	}
}
