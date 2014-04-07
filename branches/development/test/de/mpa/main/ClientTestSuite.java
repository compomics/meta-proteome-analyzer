package de.mpa.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.mpa.algorithms.CrossCorrelationTest;
import de.mpa.algorithms.EuclideanDistanceTest;
import de.mpa.algorithms.NormalizedDotProductTest;
import de.mpa.analysis.JAlignerTest;
import de.mpa.analysis.KeggMapsTest;
import de.mpa.analysis.ProteinAnalysisTest;
import de.mpa.fragmentation.FragmentizerTest;
import de.mpa.io.SixtyFourBitStringDecoderTest;
import de.mpa.io.SixtyFourBitStringEncoderTest;
import de.mpa.job.blast.BlastParserTest;
import de.mpa.parser.ec.ECReaderTest;
import de.mpa.parser.mgf.MascotGenericFileReaderTest;

@RunWith(Suite.class)
@SuiteClasses( {
				CrossCorrelationTest.class,
				EuclideanDistanceTest.class,
				NormalizedDotProductTest.class,
				ECReaderTest.class,
				MascotGenericFileReaderTest.class,
				ProteinAnalysisTest.class,
				KeggMapsTest.class,
				JAlignerTest.class,
				FragmentizerTest.class,
				SixtyFourBitStringDecoderTest.class, 
				SixtyFourBitStringEncoderTest.class,
				BlastParserTest.class,
	})

public class ClientTestSuite {}
