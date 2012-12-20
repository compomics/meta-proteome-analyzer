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
		int expected = 123;
		
		// Init default parameter map
		XTandemParameters params = new XTandemParameters();
		
		// Grab and modify a single parameter
		Parameter parameter = params.get("spectrum, minimum peaks");
		parameter.setValue(expected);
		params.put("spectrum, minimum peaks", parameter);
		
		// Create file representation of parameter map
		File file = params.toFile("input.xml");
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("spectrum, minimum peaks")) {
				String value = line.substring(line.indexOf(">", 1) + 1,
						line.lastIndexOf("<", line.length() - 1));
				assertEquals(expected, Integer.parseInt(value));
				break;
			}
		}
	}

}
