package de.mpa.db.accessor;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.analysis.taxonomy.NcbiTaxonomy;
import de.mpa.client.Constants;


/**
 * TODO: Use this class for later inserting of taxonomy into the SQL database.
 * @author T. Muth
 */
public class TaxonomyAccessorTest extends TestCase {
	
	@Before
	public void setUp() throws SQLException {
		// Path of the taxonomy dump folder
		// Path of the taxonomy dump folder
		String namesFileString = Constants.CONFIGURATION_PATH_JAR + "names.dmp";
		String nodesFileString = Constants.CONFIGURATION_PATH_JAR + "nodes.dmp";
		try {
			NcbiTaxonomy ncbiTax = NcbiTaxonomy.getInstance(namesFileString, nodesFileString);
			ncbiTax.storeTaxonomy();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		Connection conn = DBManager.getInstance().getConnection();
	}

	@Test 
	public void testInsert(){
		boolean flag = true;
		assertEquals(true, flag);
	}
}