package de.mpa.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.mpa.fastaLoader.FastaLoaderTest;
import de.mpa.settings.SearchEngineParameterExportTest;
import de.mpa.taxonomy.NcbiTaxonomyTest;
import de.mpa.webservice.ServerImplTest;

@RunWith(Suite.class)
@SuiteClasses( {	
	FastaLoaderTest.class,
	SearchEngineParameterExportTest.class,
	NcbiTaxonomyTest.class,
	ServerImplTest.class,
	})

/**
 * Runs all relevant tests as whole TestSuite.
 */
public class ServerTestSuite {}
