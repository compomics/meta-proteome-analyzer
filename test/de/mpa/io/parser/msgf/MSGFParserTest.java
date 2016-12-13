package de.mpa.io.parser.msgf;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.model.SearchHit;
import de.mpa.io.GenericContainer;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.fasta.FastaLoader;
import junit.framework.TestCase;

public class MSGFParserTest extends TestCase {
	
	private MSGFParser msgfParser;
	private File file;
	private File spectrumFile;
	
	@Before
	public void setUp() throws IOException, ClassNotFoundException {
		file = new File("test/de/mpa/resources/output/msgf_test.tsv");
		spectrumFile = new File("test/de/mpa/resources/Test1426Pfu.mgf");
		long spectrumCounter = 0;
		
		MascotGenericFileReader reader = new MascotGenericFileReader(spectrumFile);
		// Get all spectra from the reader.
		List<MascotGenericFile> spectra = reader.getSpectrumFiles(false);

		// Iterate over all spectra.
		for (MascotGenericFile mgf : spectra) {
			// The filename, remove leading and trailing whitespace.
			String title = mgf.getTitle().trim();
			
			// Fill the cache maps
			GenericContainer.SpectrumId2TitleMap.put(++spectrumCounter, title);
			GenericContainer.SpectrumTitle2FilenameMap.put(title, file.getAbsolutePath());
		}
		GenericContainer.MGFReaders.put(file.getAbsolutePath(), reader);
		GenericContainer.numberTotalSpectra = spectra.size();
		
		// The FASTA loader
		FastaLoader fastaLoader = FastaLoader.getInstance();
		fastaLoader.setFastaFile(new File("test/de/mpa/resources/fasta/pfu_yeast.fasta"));

		File indexFile = new File("test/de/mpa/resources/fasta/pfu_yeast.fasta.fb");
		if (indexFile.exists()) {
			fastaLoader.setIndexFile(indexFile);
			fastaLoader.readIndexFile();
		}
		
		GenericContainer.FastaLoader = fastaLoader;
		msgfParser = new MSGFParser(file);
	}
	
	@Test 
	public void testParse() {
		msgfParser.parse();
		List<SearchHit> searchHits = GenericContainer.SearchHits;
		SearchHit searchHit = searchHits.get(0);
		assertEquals(949L, searchHit.getSpectrumId());
		assertEquals("485.223876953125_659.78590000002", searchHit.getSpectrumTitle());
		assertEquals("HEQGAAHAADGYAR", searchHit.getPeptideSequence());
		assertEquals("Q8U2A4", searchHit.getAccession());
		assertEquals(157.0, searchHit.getScore());
		assertEquals(0.0, searchHit.getQvalue());
		assertEquals(3, searchHit.getCharge());
	}

}
