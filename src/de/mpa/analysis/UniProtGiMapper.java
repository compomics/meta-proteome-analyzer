package de.mpa.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides a method to retrieve a mapping of NCBI GI numbers to
 * UniProt accessions via HTTP query.<br>
 * Code based on <a href="http://www.uniprot.org/faq/28">http://www.uniprot.org/faq/28</a>.
 * 
 * @author R. Heyer
 */
public class UniProtGiMapper {
	
	/**
	 * 
	 * @param shortList
	 * @return
	 * @throws IOException
	 */
	private static Map<String, String> queryGiToUniProtMapping(List<String> shortList) throws IOException {
		
		// Init result map
		Map<String, String> gi2acc = new HashMap<String, String>();

		// Abort prematurely when an empty list was provided
		if (!shortList.isEmpty()) {
			
			// Build query string
			StringBuilder request = new StringBuilder("http://www.uniprot.org/mapping/?from=P_GI&to=ACC&format=tab&query=");
			request.append(shortList.get(0));
			for (int i = 1; i < shortList.size(); i++) {	// Append GI numbers
				request.append("+");
				request.append(shortList.get(i));
			}
			
			// Establish Connection
			URL url = new URL(request.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			conn.setDoInput(true);
			conn.connect();
			int status = conn.getResponseCode();
			
			if (status == HttpURLConnection.HTTP_OK) {
				// Read results from retrieved stream
				InputStream reader = conn.getInputStream();
				URLConnection.guessContentTypeFromStream(reader);
				StringBuilder builder = new StringBuilder();
				int a = 0;
				while ((a = reader.read()) != -1) {
					builder.append((char) a);
				}
				// Insert mappings into result map
				String result = builder.toString();
				String[] split = result.split("[\t\n]");	// tab-separated format with line breaks
				// Skip first row, therefore i = 2
				for (int i = 2; i < split.length; i += 2) {
					String key = split[i];
					if (gi2acc.containsKey(key)) {
						System.out.println("Existing mapping for gi|" + key + " detected, overwriting...");
					}
					gi2acc.put(key, split[i + 1]);
				}
			} else {
				System.err.println("HTTP request " + request + " failed!");
				conn.disconnect();
			}
		}
		return gi2acc;
	}

	/**
	 * Method to retrieve a mapping of the provided GI numbers to UniProt accessions via HTTP query.
	 * @param giList Set of GI numbers
	 * @return Map containing GI number-to-UniProtKB accession pairs
	 * @throws IOException if the retrieval process failed during execution
	 */
	public static Map<String, String> retrieveGiToUniProtMapping(List<String> giList) throws IOException {

		// Init result map
		Map<String, String> gi2acc = new HashMap<String, String>();
		
		int batchSize = 512;
		int maxBatchCount = giList.size() / batchSize;
		for (int i = 0; i < maxBatchCount; i++) {
			int startIndex = i * batchSize;
			int endIndex = (i + 1) * batchSize - 1;
			List<String> shortList = new ArrayList<String>(giList.subList(startIndex, endIndex));
			startIndex = endIndex + 1;
			gi2acc.putAll(queryGiToUniProtMapping(shortList));
			shortList.clear();
		}
		gi2acc.putAll(queryGiToUniProtMapping(new ArrayList<String>(giList.subList(maxBatchCount * batchSize, giList.size()))));
		
		return gi2acc;
	}
}