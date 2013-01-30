package de.mpa.analysis;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class retrives the mapping from NCBI GI number to UniProt accesssion via UniProt web request.
 * Code based on: http://www.uniprot.org/faq/28.
 * @author R. Heyer
 */
public class UniProtGiMapper {

	/**
	 * Method to retrieve the GI to UniProt accession mapping via web service.
	 * @param testList 
	 * @return Map of translation GI to UniProt
	 */
	public static Map<String, String> getMapping(List<String> testList){

		// Map with the translation from GI to UniProt accession
		TreeMap<String, String> translation = new TreeMap<String, String>();
		int status;
		HttpURLConnection conn;

		// Create Query
		StringBuilder request = new StringBuilder("http://www.uniprot.org/mapping/?from=P_GI&to=ACC&format=tab&query=");
		// Append GI numbers
		for (int i = 0; i < testList.size(); i++) {
			if (i == 0) {
				request.append("" + testList.get(i));
			}else{
				request.append("+" + testList.get(i));
			}
		}
		// Create Connection
		try {
			URL url = new URL(request.toString());
			conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			conn.setDoInput(true);
			conn.connect();
			status = conn.getResponseCode();
			// Wait that query has finished		
			while (true)
			{
				int wait = 0;
				String header = conn.getHeaderField("Retry-After");
				if (header != null)
					wait = Integer.valueOf(header);
				if (wait == 0)
					break;
				conn.disconnect();
				Thread.sleep(wait * 1000);

				conn = (HttpURLConnection) new URL(request.toString()).openConnection();
				conn.setDoInput(true);
				conn.connect();
				status = conn.getResponseCode();
			}

			// Get Data
			if (status == HttpURLConnection.HTTP_OK)
			{
				InputStream reader;
				reader = conn.getInputStream();
				URLConnection.guessContentTypeFromStream(reader);
				StringBuilder builder = new StringBuilder();
				int a = 0;
				while ((a = reader.read()) != -1)
				{
					builder.append((char) a);
				}
				// Create outPut map
				String result = builder.toString();
				String[] split = result.split("[\t\n]");
				for (int j = 2; j < split.length; j= j+2) {
					translation.put(split[j], split[j+1]);
				}
			} else{
				conn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translation;
	}
}