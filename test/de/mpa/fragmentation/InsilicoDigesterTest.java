package de.mpa.fragmentation;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.analysis.Masses;

public class InsilicoDigesterTest extends TestCase{

	Fragmentizer insilicoDigester;
	@Before
	public void setUp(){
		insilicoDigester = new Fragmentizer("LDRLD", Masses.getMap(), 1);
	}
	//TODO Test a,b,c,z Ions
	
	/**
	 * Test InsilicoDigester: AIons
	 */
	@Test 
	public void testAIons(){
		Map<String, FragmentIon[]> fragmentIons = insilicoDigester.getFragmentIons();
		FragmentIon[] aIons = fragmentIons.get("a");
		assertEquals(470.3085 ,aIons[3].getMZ(), 0.001);
	}
	
	/**
	 * Test InsilicoDigester: BIons
	 */
	@Test 
	public void testBIons(){
		Map<String, FragmentIon[]> fragmentIons = insilicoDigester.getFragmentIons();
		FragmentIon[] bIons = fragmentIons.get("b");
		assertEquals(229.1183 ,bIons[1].getMZ(), 0.001);
	}
	
	/**
	 * Test InsilicoDigester: BIons	-H2O
	 */
	@Test
	public void testbH2O(){
		Map<String, FragmentIon[]> fragmentIons = insilicoDigester.getFragmentIons();
		FragmentIon[] bH2OIons = fragmentIons.get("b_H2O");
		assertEquals(367.2088, bH2OIons[2].getMZ(), 0.001);
	}
	
	
	/**
	 * Test InsilicoDigester: CIons
	 */
	@Test 
	public void testCIons(){
		Map<String, FragmentIon[]> fragmentIons = insilicoDigester.getFragmentIons();
		FragmentIon[] cIons = fragmentIons.get("c");
		assertEquals(515.3300 ,cIons[3].getMZ(), 0.001);
	}
	
	/**
	 * Test InsilicoDigester: XIons
	 */
	@Test
	public void testXIons(){
		Map<String, FragmentIon[]> fragmentIons = insilicoDigester.getFragmentIons();
		FragmentIon[] xIons = fragmentIons.get("x");
		assertEquals(160.0240 , xIons[0].getMZ(), 0.001);
	}
	
	
	/**
	 * Test InsilicoDigester: YIons
	 */
	@Test
	public void testYIons(){
		Map<String, FragmentIon[]> fragmentIons = insilicoDigester.getFragmentIons();
		FragmentIon[] yIons = fragmentIons.get("y");
		assertEquals(247.1288, yIons[1].getMZ(), 0.001);
	}
	
	/**
	 * Test InsilicoDigester: ZIons	
	 */
	@Test
	public void testZIons(){
		Map<String, FragmentIon[]> fragmentIons = insilicoDigester.getFragmentIons();
		FragmentIon[] zIons = fragmentIons.get("z");
		assertEquals(387.2112, zIons[2].getMZ(), 0.001);
	}
	
	
	

}
