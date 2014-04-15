package de.mpa.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.mpa.analysis.UniprotAccessorTest;
import de.mpa.db.accessor.SearchSpectrumAccessorTest;
import de.mpa.db.extractor.SpectrumExtractorTest;
import de.mpa.fastaLoader.FastaLoaderTest;
import de.mpa.settings.SearchEngineParameterExportTest;
import de.mpa.taxonomy.NcbiTaxonomyTest;
import de.mpa.webservice.ServerImplTest;

@RunWith(Suite.class)
@SuiteClasses( {	
	UniprotAccessorTest.class,
	SearchSpectrumAccessorTest.class,
	SpectrumExtractorTest.class,
	FastaLoaderTest.class,
	SearchEngineParameterExportTest.class,
	NcbiTaxonomyTest.class,
	ServerImplTest.class,
	SpectrumExtractorTest.class
	})

/**
 * Runs all relevant tests as whole TestSuite.
 */
public class ServerTestSuite {}
