package de.mpa.db.accessor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.model.dbsearch.Tax;
import de.mpa.db.DBManager;


/**
 * TaxonomyAccessort test class for writing the taxonomy map to a file.
 * @author T. Muth
 */
public class TaxonomyAccessorTest extends TestCase {
	
	/**
	 * Connection 
	 */
	private Connection conn;

	@Before
	public void setUp() throws SQLException {
		// Path of the taxonomy dump folder
		conn = DBManager.getInstance().getConnection();
	}

	@Test 
	public void testWriteTaxonomyMapToFile() throws SQLException{
		Map<Long, Tax> taxonomyMap = Taxonomy.retrieveTaxonomyMap(conn);

		OutputStream fos = null;

		try {
			fos = new FileOutputStream("test/de/mpa/resources/taxonomy.map");
			ObjectOutputStream o = new ObjectOutputStream(fos);
			o.writeObject(taxonomyMap);
			o.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}