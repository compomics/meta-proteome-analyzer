package de.mpa.analysis.taxonomy;

import gnu.trove.map.hash.TIntIntHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.client.Constants;
import de.mpa.util.Formatter;

/**
 * This class constructs an index file for the taxonomy (phylogentic tree) from NCBI:
 * ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdmp.zip
 * @author R. Heyer, T. Muth
 */
@Deprecated
public class NcbiTaxonomy implements Serializable {
	
	/**
	 * Serialization ID set to default == 1L;
	 */
	private static final long serialVersionUID = 1L; 
	
	/**
	 * Instance of the NCBI taxonomy.
	 */
	private static NcbiTaxonomy instance;

	/**
	 * Map containing taxonomy id-to-byte position pairs w.r.t. 'names.dmp'
	 */ 
	@Deprecated
	private TIntIntHashMap namesMap;

	/**
	 * Map containing taxonomy id-to-byte position pairs w.r.t. 'nodes.dmp'
	 */
	@Deprecated
	private TIntIntHashMap nodesMap;

//	/**
//	 * Input reader instance for names.dmp file.
//	 */
//	private RandomAccessFile namesRaf;
//
//	/**
//	 * Input reader instance for nodes.dmp file.
//	 */
//	private RandomAccessFile nodesRaf;

	/**
	 * Filename of the taxonomy index file.
	 */
	@Deprecated
	private static final String INDEX_FILENAME = "taxonomy.index";


	/**
	 * The root node constant.
	 */
	@Deprecated
	public static final TaxonomyNode ROOT_NODE = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root");

	/**
	 * Empty constructor for NCBI tax maps
	 */
	private NcbiTaxonomy() {
		try {
//			namesRaf = new RandomAccessFile(new File(
//					this.getClass().getResource(Constants.CONFIGURATION_PATH + "names.dmp").toURI()), "r");
//			nodesRaf = new RandomAccessFile(new File(
//					this.getClass().getResource(Constants.CONFIGURATION_PATH + "nodes.dmp").toURI()), "r");
			readIndexFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get NCBI taxonomy maps instance.
	 * @return the taxonomy maps instance.
	 */
	@Deprecated
	public static NcbiTaxonomy getInstance() {
		if (instance == null) {
			instance = new NcbiTaxonomy();
		}
		return instance;
	}

	/**
	 * Indexes NCBI taxonomy dump files and writes them to an index file in the
	 * default folder.
	 * @throws Exception if an I/O error occurs
	 */
	@Deprecated
	public void createIndexFile() throws Exception {
		this.createIndexFile(Constants.CONFIGURATION_PATH);
	}

	/**
	 * Indexes NCBI taxonomy dump files and writes them to an index file in the
	 * specified folder.
	 * @param path path string pointing to the target folder
	 * @throws Exception 
	 */
	@Deprecated
	public void createIndexFile(String path) throws Exception {

		// NCBI names.dmp file
		File namesFile = new File(this.getClass().getResource(path + "names.dmp").toURI());

		// NCBI nodes.dmp file
		File nodesFile = new File(this.getClass().getResource(path + "nodes.dmp").toURI());

		// Map for NCBI names file 
		namesMap = new TIntIntHashMap();

		// Map for NCBI nodes file 
		nodesMap = new TIntIntHashMap();

		// Gets the number of chars for a new line
		int newline = Formatter.determineNewlineChars(nodesFile).length;

		// The start byte position
		int pos = 0;

		BufferedReader br;
		String line;

		// Read names file
		br = new BufferedReader(new FileReader(namesFile));
		// example format: "1	|	root	|		|	scientific name	|"
		while ((line = br.readLine()) != null) {
			if (line.endsWith("c name\t|")) {
				String id = line.substring(0, line.indexOf('\t'));
				namesMap.put(Integer.valueOf(id), pos);
			}
			pos += line.getBytes().length + newline;
		}
		br.close();

		// Read node file and create index map
		br = new BufferedReader(new FileReader(nodesFile));
		// example format: "2	|	131567	|	superkingdom	|		|	0	|	0	|	11	|	0	|	0	|	0	|	0	|	0	|		|"
		pos = 0;
		while ((line = br.readLine()) != null) {
			String id = line.substring(0, line.indexOf('\t'));
			nodesMap.put(Integer.valueOf(id), pos);
			pos += line.getBytes().length + newline;
		}
		br.close();

		this.writeIndexFile();
	}

	/**
	 * Write the index files.
	 * @throws Exception if an I/O error occurs
	 */
	private void writeIndexFile() throws Exception {

		File indexFile;
		String name = Constants.CONFIGURATION_PATH + INDEX_FILENAME;
		URL url = this.getClass().getResource(name);
		if (url == null) {
			indexFile = new File("bin" + name);
			indexFile.createNewFile();
		} else {
			indexFile = new File(url.toURI());
		}
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(indexFile)));
		oos.writeObject(namesMap);
		oos.writeObject(nodesMap);
		oos.flush();
		oos.close();
		System.out.println("done");
		System.out.println(indexFile.getAbsolutePath());
		namesMap.clear();
		nodesMap.clear();
	}

	/**
	 * Reads the index files.
	 * @throws Exception if an I/O error occurs
	 */
	@Deprecated
	public void readIndexFile() throws Exception {
		InputStream inStream = this.getClass().getResourceAsStream(Constants.CONFIGURATION_PATH + INDEX_FILENAME);
		if (inStream != null) {
			ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(inStream));
			namesMap = (TIntIntHashMap) ois.readObject();
			nodesMap = (TIntIntHashMap) ois.readObject();
			ois.close();
		} else {
			System.err.println("ERROR: \"" + Constants.CONFIGURATION_PATH + INDEX_FILENAME + "\" not found!");
		}
	}

	/**
	 * Returns the nodes map for external use.
	 * @return Map containing the taxonomy nodes.
	 */
	@Deprecated
	public TIntIntHashMap getNodesMap() {
		return nodesMap;
	}
	
	
}
