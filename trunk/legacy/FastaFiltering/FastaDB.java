import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This file represents a FASTA database.
 * @author Thilo Muth
 *
 */
public class FastaDB {
	
	/**
	 * The filename of the FASTA-Database
	 */
	private final String filename;

	/**
	 * The list of FASTA entries.
	 */
	private final List<Entry> entries;
	
	/**
	 * Map for the entries, key is taxon id.
	 */
	private Map<Integer, Entry> id2entryMap;
	
	
	/**
	 * Map for the Entry Ids, key is the accession.
	 */
	private Map<String, Integer> accession2IdMap;
	

	public FastaDB(final String filename, final Map<Integer, Entry> id2entryMap, final Map<String, Integer> accession2IdMap) {
		super();
		this.filename = filename;
		this.entries = new ArrayList<Entry>(id2entryMap.values());
		this.id2entryMap = id2entryMap;
		this.accession2IdMap = accession2IdMap;		
	}

	public String getFilename() {
		return filename;
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public Entry getEntry(final int index){
		return id2entryMap.get(index);
	}
	
	public Entry getEntry(final String accession){
		return id2entryMap.get(accession2IdMap.get(accession));
	}
	
	public int getID(final String accession) {
		return accession2IdMap.get(accession);
	}
}


