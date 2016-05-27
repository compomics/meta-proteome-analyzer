package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpa.db.DBManager;

/**
 * Helper class that removes spectra without any associated data (from deleted experiments).
 * 
 * @author K. Schallert
 *
 */
public class OrphanedSepctrumRemoval {
	/**
	 * DB Connection.
	 */
	private Connection conn;
	@Before
	public void setUp() throws SQLException {
		// Path of the taxonomy dump folder
		conn = DBManager.getInstance().getConnection();
	}
	
	/**
	 * No documentation yet. Method doesnt work yet
	 * 
	 * @author K. Schallert
	 *
	 */
	@Test
	@Ignore
	public void ReomveOrphanedSpectra() throws SQLException {
		conn.setAutoCommit(false);
		// only the spectra with an associated searchspectrum should actually remain in the database
		// get all spectrum ids in the searchspectrum table
		Map<Long, Integer> used_spectra_ids = new TreeMap<Long, Integer>();			
		PreparedStatement prs = conn.prepareStatement("SELECT ss.fk_spectrumid FROM searchspectrum ss");		
		ResultSet aRS = prs.executeQuery();			
		int counter = 0;
		while (aRS.next()) {
			counter++;
			if ((counter % 1000000) == 0) {System.out.println("SearchSpectrum: "+counter);}			
			used_spectra_ids.put(aRS.getLong("fk_spectrumid"), 0);			
		}
		prs.close();
		aRS.close();
		System.out.println("Total number of searchspectra: "+used_spectra_ids.size());		

		// get all spectrum ids from the spectrum table (these are the actual spectra)		
		prs = conn.prepareStatement("SELECT s.spectrumid FROM spectrum s");		
		aRS = prs.executeQuery();
		System.out.println("Columns: "+aRS.getMetaData().getColumnCount());
		List<Long> all_spectra_ids = new ArrayList<Long>();		
		int total_count = 0;
		int orphaned_spectrum_counter = 0;				
		while (aRS.next()) {
			total_count++;			
			if ((total_count % 1000000) == 0) {System.out.println("Spectrum: "+total_count);}
			Long current_id = aRS.getLong(1);
			all_spectra_ids.add(current_id);	
		}
		prs.close();
		aRS.close();
		System.out.println("Total amount of spectra: "+all_spectra_ids.size());
				
		// we now remove all values where we have a match between both lists
		List<Long> orphaned_spectra = new ArrayList<Long>();
		counter = 0;
		for (int all_index = 0; all_index < all_spectra_ids.size(); all_index++) {
			//Long spectrum_id = all_spectra_ids.get(all_index);
			counter++;
			if ((counter % 1000000) == 0) {System.out.println("Cycling through all spectra: "+counter);}
			if (used_spectra_ids.containsKey(all_spectra_ids.get(all_index))) {				
				// pass
			} else {
				orphaned_spectrum_counter++;
				orphaned_spectra.add(all_spectra_ids.get(all_index));
			}
		}		
		System.out.println("Delete-Size: "+orphaned_spectra.size());
		
		// actual deletion
		counter = 0;
		Statement stmt = conn.createStatement();
		for (Long spectrum_id : orphaned_spectra) {
			counter++;
			PreparedStatement prs2 = conn.prepareStatement("SELECT s.* FROM spectrum s WHERE s.spectrumid = ?");
			prs2.setLong(1, spectrum_id);
			ResultSet aRS2 = prs2.executeQuery();			
			if ((counter % 100) == 0) {
				System.out.println("Deleted: "+ spectrum_id);	
				System.out.println("Deleted: "+ counter);
			}
			while (aRS2.next())  {
				SpectrumTableAccessor spectrum = new SpectrumTableAccessor(aRS2);
				stmt.executeUpdate("DELETE sp.* " +
						  "FROM spec2pep sp WHERE sp.fk_spectrumid = " + spectrum.getSpectrumid());
				spectrum.delete(conn);
				conn.commit();
			}
			prs2.close();
			aRS2.close();
		}
		conn.commit();		
		System.out.println("Number of orphaned spectra: "+orphaned_spectrum_counter);
	}
}
