package de.mpa.parser.ec;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.ui.Constants;


public class EcParserTest extends TestCase {
	private Map<String, ECEntry> ecMap;

	@Before
	public void setUp() {
		// Initialize the EC-number Map
		String path = this.getClass().getResource("EcParserTest.class").getPath();
		if (path.indexOf("/" + Constants.APPTITLE) != -1) {
	            path = path.substring(1, path.lastIndexOf("/" + Constants.APPTITLE) + Constants.APPTITLE.length() + 1);
	            path = path.replace("%20", " ");
	            path = path.replace("%5b", "[");
	            path = path.replace("%5d", "]");
	            path += "/conf";
		}
		String paramsPath = path + "/ECreduced.xml";
		ecMap = ECReader.readEC(paramsPath);		
	}

	@Test
	public void testParse() {
		assertEquals("Oxidoreductases", ecMap.get("1.-.-.-").getName());
		assertEquals("To this class belong all enzymes catalyzing oxido-reductions. The substrate oxidized is regarded as hydrogen or electron donor. The classification is based on 'donor:acceptor oxidoreductase'. The common name is 'dehydrogenase', wherever this is possible; as an alternative, 'acceptor reductase' can be used. 'Oxidase' is used only where O(2) is an acceptor. Classification is difficult in some cases, because of the lack of specificity toward the acceptor.", ecMap.get("1.-.-.-").getDescription());
		
		System.out.println(ecMap.get("1.1.-.-").getName());
		assertEquals("Acting on the CH-OH Group of Donors", ecMap.get("1.1.-.-").getName());
		assertEquals("This subclass contains all dehydrogenases acting on primary alcohols, secondary alcohols and hemi-acetals. They are further classified according to the acceptor which can be NAD(+) or NADP(+) (subclass EC 1.1.1), cytochrome (EC 1.1.2), oxygen (EC 1.1.3), a disulfide (EC 1.1.4), quinone (EC 1.1.5) or another acceptor (EC 1.1.99).", ecMap.get("1.1.-.-").getDescription());
		
		assertEquals("Transferring One-Carbon Groups", ecMap.get("2.1.-.-").getName());
		assertEquals("This subclass contains the methyltransferases (EC 2.1.1), the hydroxymethyl-, formyl- and related transferases (EC 2.1.2), the carboxyl- and carbamoyl-transferases (EC 2.1.3), and the amidino-transferases (EC 2.1.4).", ecMap.get("2.1.-.-").getDescription());
		
		
		assertEquals("With NAD(+) or NADP(+) as acceptor", ecMap.get("1.1.1.-").getName());
		
		assertEquals("Alcohol dehydrogenase", ecMap.get("1.1.1.1").getName());
		assertEquals("A zinc protein. Acts on primary or secondary alcohols or hemi-acetalss with very broad specificity; however the enzyme oxidizes methanol much more poorly than ethanol. The animal, but not the yeast, enzyme acts also on cyclic secondary alcohols.", ecMap.get("1.1.1.1").getDescription());
		

		assertEquals("Cobaltochelatase", ecMap.get("6.6.1.2").getName());
		assertEquals("This enzyme, which forms part of the aerobic cobalamin biosynthesis pathway, is a type I chelatase, being heterotrimeric and ATP-dependent. It comprises two components, one of which corresponds to CobN and the other is composed of two polypeptides, specified by cobS and cobT in Pseudomonas denitrificans, and named CobST (1). Hydrogenobyrinic acid is a very poor substrate. ATP can be replaced by dATP or CTP but the reaction proceeds more slowly. CobN exhibits a high affinity for hydrogenobyrinic acid a,c-diamide. The oligomeric protein CobST possesses at least one sulfhydryl group that is essential for ATP-binding. Once the Co(2+) is inserted, the next step in the pathway ensures that the cobalt is ligated securely by reducing Co(II) to Co(I). This step is carried out by EC 1.16.8.1, cob(II)yrinic acid a,c-diamide reductase.", ecMap.get("6.6.1.2").getDescription());
		
		
	}
}