package de.mpa.analysis;

import java.util.logging.Level;
import java.util.logging.Logger;

import jaligner.Alignment;
import jaligner.Sequence;
import jaligner.SmithWatermanGotoh;
import jaligner.matrix.Matrix;
import jaligner.matrix.MatrixLoader;
import jaligner.matrix.MatrixLoaderException;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Method to test the JAligner library,
 * @author Robert Heyer
 *
 */
public class JAlignerTest extends TestCase{
	
	String seq1 = "MAYEAQYYPGATSVGANRRKHMSGKLEKLREISDEDLTAVLGHRAPGSDYPSTHPPLAEMGEPACSIREAVAATPGAAAGDRVRYVQFADSMYNAPATPYFRSYFAAINFRGVDPGTLSG" + 
	"RQIVEARERDMEQCAKVQMETEMTDPALAGMRGATVHGHSVRLQEDGVMFDMLDRRRLEGGVIIMDKDQVAIPLDRKVNLGKPMSSEEAAKRTTIYRVDNVAFRDDAEVIEWVHRVFDQRTSYGFQPK";
	String seq2 = "MAYKPQYGPGTSIVAANRRKQMDPKQKLEKVRSVTDEDIVLILGHRAPGSAYPTAHPPLAEQQEVNCPMRKLVKPTEGAKAGDRVRYIQFADSMFNAPSQPYQRTYTEMYRFRGIDPGTLSGRQIVECRERDLEKYAKDLIETEMFDPALVGIRGATVHGHSLRLAEDGMMFDMLQRNVLGEDGIVKYVKNQIGEPLDRAVAVGKPMDEKWLKAHTTIYHSLVGTSYRDDTEYIEYVQRIHSLRTKYGFMPKEE";
	private Alignment align;
	
	@Before
	public void setUp(){
		Matrix matrix;
		Logger log = Logger.getLogger(SmithWatermanGotoh.class.getName());
		log.setLevel(Level.OFF);
		log = Logger.getLogger(MatrixLoader.class.getName());
		log.setLevel(Level.OFF);
		try {
			matrix = MatrixLoader.load("PAM30");
			align = SmithWatermanGotoh.align(new Sequence(seq1), new Sequence(seq2),matrix, 10.0f, 0.5f);
		} catch (MatrixLoaderException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test(){
		assertEquals(248, seq1.length());
		assertEquals(254, seq2.length());
		assertEquals(822.5f, align.getScore());
		assertEquals(170, align.getSimilarity());
		assertEquals(153, align.getIdentity());
	}
}
