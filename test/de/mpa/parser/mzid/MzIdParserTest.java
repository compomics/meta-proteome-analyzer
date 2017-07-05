package de.mpa.parser.mzid;

import java.io.File;
import java.util.List;

import org.junit.Test;

import de.mpa.client.model.SearchHit;
import de.mpa.io.GenericContainer;
import de.mpa.io.parser.mzid.MzidParser;
import junit.framework.TestCase;

public class MzIdParserTest extends TestCase {

	@Test
	public void testParser() {
		// Open the input mzIdentML file for parsing
		File mzidFile = new File("test/de/mpa/resources/F119768.mzid");
		MzidParser parser = new MzidParser(mzidFile);
		parser.parse();
		
		List<SearchHit> searchHits = GenericContainer.SearchHits;
		for (SearchHit searchHit : searchHits) {
			System.out.println(searchHit.toString());
		}
	}

}
