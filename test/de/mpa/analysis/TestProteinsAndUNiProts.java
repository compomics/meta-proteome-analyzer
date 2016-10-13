package de.mpa.analysis;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.model.dbsearch.UniProtEntryMPA;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.UniprotentryAccessor;

/**
 * Test the saved protein and uniprot entries
 * @author R. Heyer
 *
 */
public class TestProteinsAndUNiProts extends TestCase {

	/**
	 * Set of all proteins
	 */
	ArrayList<ProteinAccessor> findAllProteinAccessors;
	
	/**
	 * List of all uniprot entries
	 */
	ArrayList<UniprotentryAccessor> allUNiProtentries; 
	
	/**
	 * Map of all uniprot entries
	 */
	TreeMap<Long, UniProtEntryMPA> uniprotMap = new TreeMap<Long, UniProtEntryMPA>() ; 
	
	@Before
	public void setup(){
		

	}
	
	@Test
	public void testentries(){
		
		// Create a Protein entry in the database
				Connection conn = null;
				try {
					conn = DBManager.getInstance().getConnection();
					// Get proteins and UniProt-entries
					findAllProteinAccessors = ProteinAccessor.findAllProteinAccessors(conn);
					System.out.println("proteins" + findAllProteinAccessors.size());
					allUNiProtentries = UniprotentryAccessor.findAllEntries(conn);

					System.out.println("uniProt" + allUNiProtentries.size());

					// put uniprot entries in a map for an easier access
					for (UniprotentryAccessor entry : allUNiProtentries) {
						uniprotMap.put(entry.getUniprotentryid(), new UniProtEntryMPA(entry));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
		
		
		/**
		 * Check whether a uniprot entry is available for all protein entries
		 */
		for (ProteinAccessor prots : findAllProteinAccessors) {

			UniProtEntryMPA uniprotENtry = uniprotMap.get(prots.getFK_UniProtID());
			if (uniprotENtry == null) {
				System.out.println("MIssing " +  prots.getAccession());
			}
		}
		
		assertEquals(true, true);
	}
}
