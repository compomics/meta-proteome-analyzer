package de.mpa.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.mpa.algorithms.CrossCorrelationTest;
import de.mpa.algorithms.EuclideanDistanceTest;
import de.mpa.algorithms.NormalizedDotProductTest;
import de.mpa.algorithms.denovo.GappedPeptideCombinerTest;
import de.mpa.algorithms.denovo.GappedPeptideTest;
import de.mpa.db.extractor.SpectrumExtractorTest;
import de.mpa.db.extractor.SpectrumSpectrumMatchTest;
import de.mpa.exporter.ExportTest;
import de.mpa.fastaLoader.TestFastaLoader;
import de.mpa.fragmentation.FragmentizerTest;
import de.mpa.io.ParserTest;
import de.mpa.io.SixtyFourBitStringDecoderTest;
import de.mpa.io.SixtyFourBitStringEncoderTest;
import de.mpa.parser.mascot.xml.MascotXMLParserTest;

@RunWith(Suite.class)
@SuiteClasses( {	CrossCorrelationTest.class,
					EuclideanDistanceTest.class,
					NormalizedDotProductTest.class,
					GappedPeptideCombinerTest.class, 
					SpectrumExtractorTest.class,
					SpectrumSpectrumMatchTest.class, 
					ExportTest.class, TestFastaLoader.class,
					FragmentizerTest.class, 
					ParserTest.class,
					SixtyFourBitStringDecoderTest.class, 
					SixtyFourBitStringEncoderTest.class,
					MascotXMLParserTest.class,
					GappedPeptideTest.class
	})

/**
 * Runs all relevant tests as whole TestSuite.
 */
public class TestSuiteRunner {}
