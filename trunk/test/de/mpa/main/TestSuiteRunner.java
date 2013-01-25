package de.mpa.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.mpa.algorithms.CrossCorrelationTest;
import de.mpa.algorithms.EuclideanDistanceTest;
import de.mpa.algorithms.NormalizedDotProductTest;
import de.mpa.analysis.JAlignerTest;
import de.mpa.analysis.KeggAccessorTest;
import de.mpa.analysis.KeggMapsTest;
import de.mpa.analysis.ProteinAnalysisTest;
import de.mpa.analysis.UniprotAccessorTest;
import de.mpa.db.accessor.SearchSpectrumAccessorTest;
import de.mpa.db.extractor.SpectrumExtractorTest;
import de.mpa.exporter.ExportTest;
import de.mpa.exporter.ResultsDumpTest;
import de.mpa.fastaLoader.FastaLoaderTest;
import de.mpa.fragmentation.FragmentizerTest;
import de.mpa.io.SixtyFourBitStringDecoderTest;
import de.mpa.io.SixtyFourBitStringEncoderTest;
import de.mpa.job.blast.BlastParserTest;
import de.mpa.parser.mascot.xml.MascotXMLParserTest;
import de.mpa.parser.mgf.MascotGenericFileReaderTest;
import de.mpa.parser.pepnovo.QueryParserTest;
import de.mpa.settings.SearchEngineParameterExportTest;
import de.mpa.taxonomy.NcbiTaxonomyTest;
import de.mpa.webservice.ServerImplTest;

@RunWith(Suite.class)
@SuiteClasses( {	CrossCorrelationTest.class,
					EuclideanDistanceTest.class,
					NormalizedDotProductTest.class,
					
					JAlignerTest.class,
					KeggAccessorTest.class,
					KeggMapsTest.class,
					ProteinAnalysisTest.class,
					UniprotAccessorTest.class,
					
					SearchSpectrumAccessorTest.class,
					
					SpectrumExtractorTest.class,
					
					ExportTest.class,
					ResultsDumpTest.class,
					
					FastaLoaderTest.class,

					FragmentizerTest.class,
					
					SixtyFourBitStringDecoderTest.class, 
					SixtyFourBitStringEncoderTest.class,
					
					BlastParserTest.class,
					
					MascotXMLParserTest.class,
					
					MascotGenericFileReaderTest.class,
					
					QueryParserTest.class,
					
					SearchEngineParameterExportTest.class,
					
					NcbiTaxonomyTest.class,
					ServerImplTest.class
	})

/**
 * Runs all relevant tests as whole TestSuite.
 */
public class TestSuiteRunner {}
