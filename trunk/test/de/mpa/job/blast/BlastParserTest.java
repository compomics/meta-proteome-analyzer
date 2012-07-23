package de.mpa.job.blast;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class BlastParserTest extends TestCase {
	private BlastParser parser;

	@Before
	public void setUp() {
		File file = new File(getClass().getClassLoader().getResource("Blast_BSA_10.out").getPath());
		parser = new BlastParser(file);
	}
	
	@Test
	public void testParse() {
		parser.read();
		List<BlastQuery> queries = parser.getQueries();
		BlastQuery query = queries.get(0);
		BlastHit firstHit = query.getFirstHit();
		assertEquals("Spectrum_6|Query_1|Score:", firstHit.getQueryId());
		assertEquals(83.33, firstHit.getIdentity());
		assertEquals(12, firstHit.getLength());
		assertEquals(78, firstHit.getTargetStart());
		assertEquals(88, firstHit.getTargetEnd());
		assertEquals("sp|P14639|ALBU_SHEEP", firstHit.getTargetID());
		assertEquals(7.0, firstHit.getEValue());
		assertEquals(27.8, firstHit.getBitScore());
	}
}
