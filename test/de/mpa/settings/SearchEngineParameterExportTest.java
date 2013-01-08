package de.mpa.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.client.settings.Parameter;
import de.mpa.client.settings.XTandemParameters;

/**
 * Class containing tests for the various database search engine parameter export formats.
 * 
 * @author A. Behne
 */
public class SearchEngineParameterExportTest extends TestCase {
	
	@Test
	public void testXtandemExport() throws IOException {
		int minPeaks = 123;
		Boolean[][] ions = new Boolean[][] { { true, false, false }, { false, false, true } };
		
		// Init default parameter map
		XTandemParameters params = new XTandemParameters();
		
		// Grab and modify a bunch of parameters
		String key = "spectrum, minimum peaks";
		Parameter parameter = params.get(key);
		parameter.setValue(minPeaks);
		params.put(key, parameter);
		
		key = "scoring, ions";
		parameter = params.get(key);
		parameter.setValue(ions);
		params.put(key, parameter);
		
		// Create file representation of parameter map
		File file = params.toFile("test/de/mpa/resources/input.xml");
		
		// Dig through file and try to recover inserted non-default values
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains(key)) {
				String value = line.substring(line.indexOf(">", 1) + 1,
						line.lastIndexOf("<", line.length() - 1));
				assertEquals(minPeaks, Integer.parseInt(value));
				break;
			}
		}
		br.close();
	}

}
