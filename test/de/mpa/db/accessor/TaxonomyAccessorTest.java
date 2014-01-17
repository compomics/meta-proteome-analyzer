package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Before;

import de.mpa.analysis.taxonomy.NcbiTaxonomy;
import de.mpa.db.DBManager;


/**
 * TODO: Use this class for later inserting of taxonomy into the SQL database.
 * @author T. Muth
 */
public class TaxonomyAccessorTest extends TestCase {
	
	/**
	 * Connection 
	 */
	private Connection conn;
	private NcbiTaxonomy ncbiTax;

	@Before
	public void setUp() throws SQLException {
		// Path of the taxonomy dump folder
		ncbiTax = NcbiTaxonomy.getInstance();
		conn = DBManager.getInstance().getConnection();
	}

//	@Test 
//	public void testInsert(){
//		try {
//			int[] keys = ncbiTax.getNodesMap().keys();
//			for (int taxID : keys) {
//				if (taxID != 1) {
//					Taxonomy.addTaxonomy((long) taxID, (long) ncbiTax.getParentTaxId(taxID), ncbiTax.getTaxonName(taxID), ncbiTax.getRank(taxID), conn);
//					if (taxID % 1000 == 0) {
//						System.out.println(taxID);
//						conn.commit();
//					}
//				}
//			}
//			conn.commit();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	

}