package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import de.mpa.client.model.dbsearch.Tax;

public class Taxonomy extends TaxonomyTableAccessor {
	/**
     * Calls the super class.
     * @param params
     */
	public Taxonomy(HashMap params) {
		super(params);
	}

	/**
	 * This constructor allows the creation of the 'ProteinTableAccessor' object based on a resultset
	 * obtained by a 'select * from Protein' query.
	 *
	 * @param	resultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public Taxonomy(ResultSet resultSet) throws SQLException {
		super(resultSet);
	}
	
   /**
	* This method will find a taxonomy entry from the current connection, based on the specified taxID.
	*
	* @param taxID long with the Tax ID
	* @param conn Connection to read the spectrum File from.
	* @return ProteinAccessor with the data.
	* @throws SQLException when the retrieval did not succeed.
	*/
   public static Taxonomy findFromTaxID(long taxID, Connection conn) throws SQLException {
	   Taxonomy temp = null;
       PreparedStatement ps = conn.prepareStatement(getBasicSelect() + " WHERE " + TAXONOMYID + " = ?");
       ps.setLong(1, taxID);
       ResultSet rs = ps.executeQuery();
       while (rs.next()) {
           temp = new Taxonomy(rs);
       }
       rs.close();
       ps.close();
       return temp;
   }
   
	/**
	 * This method fetches once all taxonomy entries from the database.
	 * @param conn SQL connection
	 * @return Map of all taxonomy entries with taxonomy IDs as key.
	 * @throws SQLException
	 */
	public static Map<Long, Tax> retrieveTaxonomyMap(Connection conn) throws SQLException {
		Map<Long, Tax>  taxonomies = new HashMap<Long, Tax>();
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while (rs.next()) {
			Taxonomy taxonomy = new Taxonomy(rs);
			Tax tax = new Tax (taxonomy.getTaxonomyid(), taxonomy.getParentid(), taxonomy.getDescription(), taxonomy.getRank());
			taxonomies.put(rs.getLong("taxonomyid"), tax);
		}
		
		rs.close();
		stat.close();
		return taxonomies;
	}
   
   /**
    * This method adds a taxonomy to the database.
    * @param taxID Taxonomic identifier (NCBI TaxID)
    * @param parentID Parent taxonomic identifier
    * @param description Taxonomic description
    * @param rank Taxonomic rank
    * @param conn Connection conn
    * @return Taxonomy object
    * @throws SQLException
    */
	public static Taxonomy addTaxonomy(Long taxID, Long parentID, String description, String rank, Connection conn) throws SQLException {
		Taxonomy taxonomy = Taxonomy.findFromTaxID(taxID, conn);
		if (taxonomy == null) {
			HashMap<Object, Object> data = new HashMap<Object, Object>(6);
			data.put(Taxonomy.TAXONOMYID, taxID);
			data.put(Taxonomy.PARENTID, parentID);
			data.put(Taxonomy.DESCRIPTION, description);
			data.put(Taxonomy.RANK, rank);
			taxonomy = new Taxonomy(data);
			taxonomy.persist(conn);		
		}
		return taxonomy;
   }
	
   	
}
