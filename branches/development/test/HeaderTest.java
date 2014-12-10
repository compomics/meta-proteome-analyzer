import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;


public class HeaderTest  extends TestCase{

	
	@Before
	public void setUp() throws SQLException {
		System.out.println("Test");
	}
	
	@Test
	public void testOmssaParameters() {
		
//		String composedHeader1 = ">" + "sp|P46191|SYK_MYCHP" + " " + "DESCRIPTION";
//		String composedHeader2 = ">" + "contig-30284000041_2" + " " + "DESCRIPTION";
//		String composedHeader3 = ">" + "ATB_B_contig11075-2" + " " + "DESCRIPTION";
//		Header header = Header.parseFromFASTA(composedHeader1);
//		System.out.println("HEADER: " + header.getAccession() + ":" +header.getDescription() );
//		Header header2 = Header.parseFromFASTA(composedHeader2);
//		System.out.println("HEADER: " + header2.getAccession() + ":" +header2.getDescription() );
//		Header header3 = Header.parseFromFASTA(composedHeader3);
//		System.out.println("HEADER: " + header3.getAccession() + ":" +header3.getDescription() );
		
		String test= ">sp|P16116|ALDR_BOVIN";
		String[] split = test.split("[|]");
		System.out.println(split[0]);
		System.out.println(split[1]);
	
		assertEquals(true, true);
		
		
	}
}
