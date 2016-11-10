package de.mpa.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.mpa.analysis.UniProtJapiTest;
import de.mpa.db.accessor.SearchSpectrumAccessorTest;
import de.mpa.db.extractor.SpectrumExtractorTest;
import de.mpa.settings.SearchEngineParameterExportTest;
import de.mpa.taxonomy.NcbiTaxonomyTest;
import de.mpa.webservice.ServerImplTest;

@RunWith(Suite.class)
@SuiteClasses( {	
	UniProtJapiTest.class,
	SearchSpectrumAccessorTest.class,
	SpectrumExtractorTest.class,
	SearchEngineParameterExportTest.class,
	NcbiTaxonomyTest.class,
	ServerImplTest.class,
	SpectrumExtractorTest.class
	})

/**
 * Runs all relevant tests as whole TestSuite.
 */
public class ServerTestSuite {}
