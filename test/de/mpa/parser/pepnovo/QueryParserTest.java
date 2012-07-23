package de.mpa.parser.pepnovo;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class QueryParserTest extends TestCase {
	
	@Test
	public void testParse() {
		File file = new File(getClass().getClassLoader().getResource("Test_BSA_10.out_full.txt").getPath());
		QueryFile queryFile = QueryParser.read(file.getAbsolutePath());
		List<QueryEntry> queryEntries = queryFile.getQueryEntries();
		QueryEntry queryEntry = queryEntries.get(0);
		assertEquals(0, queryEntry.getSpectrumNumber());
		assertEquals(7, queryEntry.getNumSequences());
		
		List<String> queries = queryEntry.getQueries();	
		assertEquals("BNFVADESHAGXXDK", queries.get(0));
		assertEquals("BNFVADESHAXXDK", queries.get(3));
		assertEquals("BFNVADESHAXXXDK", queries.get(6));
	}

}
