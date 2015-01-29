package de.mpa.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.mpa.fragmentation.FragmentizerTest;
import de.mpa.io.SixtyFourBitStringDecoderTest;
import de.mpa.io.SixtyFourBitStringEncoderTest;
import de.mpa.parser.ec.ECReaderTest;
import de.mpa.parser.mgf.MascotGenericFileReaderTest;

@RunWith(Suite.class)
@SuiteClasses( {
				ECReaderTest.class,
				MascotGenericFileReaderTest.class,
				FragmentizerTest.class,
				SixtyFourBitStringDecoderTest.class, 
				SixtyFourBitStringEncoderTest.class,
	})

public class ClientTestSuite {}
