package de.mpa.job.instances;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class MakeBlastdbJobTest extends TestCase {
	
		private MakeBlastdbJob job;

		@Before
		public void setUp() {
			job = new MakeBlastdbJob(new File("test/de/mpa/resources/test.fasta"));
		}
		
		@Test 
		public void testMakeBlastdbIndexing() {
			assertNotNull(job);
			assertEquals("MAKEBLASTDB JOB", job.getDescription());
			
			job.run();
		}

}
