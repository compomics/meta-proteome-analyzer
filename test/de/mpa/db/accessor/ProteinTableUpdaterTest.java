package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import de.mpa.analysis.ReducedProteinData;
import de.mpa.analysis.UniProtUtilities;
import de.mpa.db.DBManager;

/**
 * Helper class to maintain and update the protein table:
 * Corrects for misspelled accession, sequences and duplicate entries.
 * @author T. Muth
 *
 */
public class ProteinTableUpdaterTest {
	
	/**
	 * DB Connection.
	 */
	private Connection conn;

	@Before
	public void setUp() throws SQLException {
		// Path of the taxonomy dump folder
		conn = DBManager.getInstance().getConnection();
	}
	
	@Test
	@Ignore
	public void testUpdateProteinEntries() throws SQLException {
		Map<String, Long> accession2IdMap = new TreeMap<String, Long>();
		
		System.out.println("Selecting protein entries having NULL description...");
		PreparedStatement ps = conn.prepareStatement(ProteinAccessor.getBasicSelect() + " WHERE description IS NULL" );
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			accession2IdMap.put(rs.getString("accession"), rs.getLong("proteinid"));
		}
		rs.close();
		ps.close();
		
		// Retrieve the UniProt entries.
		List<String> accessions = new ArrayList<String>();

		Map<String, ReducedProteinData> proteinDataMap = new HashMap<String, ReducedProteinData>();
		
		System.out.println("Checking for accessions being UniProt conform...");
		for (String accession : accession2IdMap.keySet()) {
			// UniProt accession
			if (accession.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
				accessions.add(accession);		
			}
		}
		
		System.out.println("Retrieving UniProt entries...");
		if (!accessions.isEmpty()) {
			proteinDataMap = UniProtUtilities.retrieveProteinData(accessions, false);			
		}
		
		for (String acc : accessions) {
			ProteinAccessor proteinAccessor = ProteinAccessor.findFromID(accession2IdMap.get(acc), conn);
			ReducedProteinData reducedProteinData = proteinDataMap.get(acc);
			if (reducedProteinData != null) {
				if (reducedProteinData.getUniProtEntry() != null) {
					if (reducedProteinData.getUniProtEntry().getProteinDescription() != null) {
						ProteinDescription desc = reducedProteinData.getUniProtEntry().getProteinDescription();
						Name name = null;
						
						// Recommended name only.
						if(desc.hasRecommendedName()){
							name = desc.getRecommendedName();
						} else if(desc.hasAlternativeNames()) {
							name = desc.getAlternativeNames().get(0);
						} else if(desc.hasSubNames()) {
							name = desc.getSubNames().get(0);
						}
						String description = name == null ? "" : name.getFieldsByType(FieldType.FULL).get(0).getValue();
						proteinAccessor.setDescription(description);
						proteinAccessor.setSource("UniProt");
						proteinAccessor.update(conn);
						conn.commit();
					}
				}
			}
		}
		System.out.println("Size: " + accessions.size());
	}
	
	@Test
	@Ignore
	public void testUpdateMisspelledSequenceEntries() throws SQLException {
		Map<String, Long> accession2IdMap = new TreeMap<String, Long>();
		
		System.out.println("Selecting protein entries having \"misspelled\" sequence...");
		PreparedStatement ps = conn.prepareStatement(ProteinAccessor.getBasicSelect() + " WHERE sequence LIKE '%prot%'" );
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			accession2IdMap.put(rs.getString("accession"), rs.getLong("proteinid"));
		}
		rs.close();
		ps.close();
		int count = 0;
		for (String acc : accession2IdMap.keySet()) {
			ProteinAccessor proteinAccessor = ProteinAccessor.findFromID(accession2IdMap.get(acc), conn);
			String[] split = proteinAccessor.getSequence().split("prot");
			String sequence = split[1];
			if(Pattern.matches("[a-zA-Z]+", sequence)){
				proteinAccessor.setSequence(sequence);
				count++;
			} else {
				char[] charArray = sequence.toCharArray();
				String newSequence = "";
				for (char c : charArray) {
					Character ch = Character.valueOf(c);
					if (!Character.isDigit(ch)) {
						newSequence += c;
					}
				}
				proteinAccessor.setSequence(newSequence);
			}
			proteinAccessor.update(conn);
			conn.commit();
		}
		System.out.println(count);
	}
	
	@Test
	@Ignore
	public void testUpdateProteinEntriesWithEmptySequence() throws SQLException {
		Map<String, Long> accession2IdMap = new TreeMap<String, Long>();
		
		System.out.println("Selecting protein entries having no real sequence...");
		PreparedStatement ps = conn.prepareStatement(ProteinAccessor.getBasicSelect());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			if (rs.getString("sequence").length() < 2) {
				accession2IdMap.put(rs.getString("accession"), rs.getLong("proteinid"));
			}
		}
		rs.close();
		ps.close();
		
		// Retrieve the UniProt entries.
		List<String> accessions = new ArrayList<String>();
		List<String> otherAccessions = new ArrayList<String>();

		Map<String, ReducedProteinData> proteinDataMap = new HashMap<String, ReducedProteinData>();
		
		System.out.println("Checking for accessions being UniProt conform...");
		List<String> keySet = new ArrayList<String>(accession2IdMap.keySet());
		for (String accession : keySet) {
			// UniProt accession
			if (accession.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
				accessions.add(accession);		
			} else {
				if (accession.contains("sp|")) {
					String[] split = accession.split("\\|");
					if (split[1].matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
						accession2IdMap.put(split[1], accession2IdMap.get(accession));
						accessions.add(split[1]);		
					}
				} else {
					otherAccessions.add(accession);
				}
			}
		}
		
		System.out.println(otherAccessions.size());
		
		System.out.println("Retrieving " + accessions.size() + " UniProt entries...");
		if (!accessions.isEmpty()) {
			proteinDataMap = UniProtUtilities.retrieveProteinData(accessions, false);			
		}
		
		for (String acc : accessions) {
			ProteinAccessor proteinAccessor = ProteinAccessor.findFromID(accession2IdMap.get(acc), conn);
			ReducedProteinData reducedProteinData = proteinDataMap.get(acc);
			if (reducedProteinData != null) {
				if (reducedProteinData.getUniProtEntry() != null) {
					if (reducedProteinData.getUniProtEntry().getSequence() != null) {
						String sequence = reducedProteinData.getUniProtEntry().getSequence().getValue();
						proteinAccessor.setSequence(sequence);
						proteinAccessor.setSource("UniProt");
						proteinAccessor.update(conn);
						conn.commit();
					}
				}
			}
		}
		System.out.println("Size: " + accessions.size());
	}
	
	@Test
	@Ignore
	public void removeDuplicateProteinEntryReferences() throws SQLException {
		Map<String, Long> accession2IdMap = new TreeMap<String, Long>();
		Map<String, Long> duplicatesMap = new TreeMap<String, Long>();
		
		System.out.println("Selecting duplicate protein entries...");
		PreparedStatement ps = conn.prepareStatement(ProteinAccessor.getBasicSelect());
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			count++;
			if (accession2IdMap.get(rs.getString("accession")) != null) {
				duplicatesMap.put(rs.getString("accession") +"_Dupl" +count, rs.getLong("proteinid"));
			} else {
				accession2IdMap.put(rs.getString("accession"), rs.getLong("proteinid"));
			}
		}
		rs.close();
		ps.close();
		
		// Iterate the duplicates.
		Set<String> keySet = duplicatesMap.keySet();
		System.out.println("Selecting and updating search hits...");
		
		int totalentries = keySet.size();
		int entrycounter = 0; 
		for (String key : keySet) {
			entrycounter = entrycounter + 1;
			System.out.println("Entry: " + entrycounter + " of " + totalentries);
			Long proteinID = duplicatesMap.get(key);
			
			String[] split = key.split("_Dupl");
			Long newProteinID = accession2IdMap.get(split[0]);
			List<Pep2prot> findLinkByProteinID = Pep2prot.findLinkByProteinID(proteinID, conn);
			for (Pep2prot pep2prot : findLinkByProteinID) {
				pep2prot.setFk_proteinid(newProteinID);
				pep2prot.update(conn);
				conn.commit();
			}
	    	List<Cruxhit2protTableAccessor> temp = new ArrayList<Cruxhit2protTableAccessor>();
	    	
	        PreparedStatement ps2 = conn.prepareStatement("select x.* from cruxhit2prot x where x.fk_proteinid = ?");
	        ps2.setLong(1, proteinID);
	        ResultSet rs2 = ps2.executeQuery();
	        while (rs2.next()) {
	        	Cruxhit2protTableAccessor hit = new Cruxhit2protTableAccessor(rs2);
	            temp.add(hit);	
	        }
	        rs2.close();
	        ps2.close();
	        
	        for (Cruxhit2protTableAccessor xTandemhit : temp) {
				xTandemhit.setFk_proteinid(newProteinID);
				xTandemhit.update(conn);
				conn.commit();
			}
		}
		System.out.println("Duplicates: " + duplicatesMap.size());
	}
	
	@Test
	@Ignore
	public void removeDuplicatePep2ProtEntries() throws SQLException {
		Map<String, Long> accession2IdMap = new TreeMap<String, Long>();
		Map<String, Long> duplicatesMap = new TreeMap<String, Long>();
		
		System.out.println("Selecting duplicate pep2prot entries...");
		PreparedStatement ps = conn.prepareStatement(Pep2prot.getBasicSelect());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String concat = rs.getLong("fk_peptideid") + "_" + rs.getLong("fk_proteinid"); 
			if (accession2IdMap.get(concat) != null) {
				duplicatesMap.put(concat, rs.getLong("pep2protid"));
			} else {
				accession2IdMap.put(concat, rs.getLong("pep2protid"));
			}
		}
		rs.close();
		ps.close();
		
		List<Long> values = new ArrayList<Long>(duplicatesMap.values());
		for (Long pep2protID : values) {
			List<Pep2prot> pep2prots = new ArrayList<Pep2prot>();
			
			PreparedStatement preparedStatement = conn.prepareStatement(Pep2prot.getBasicSelect() + " WHERE pep2protid = ?");
			preparedStatement.setLong(1, pep2protID);
			ResultSet set = preparedStatement.executeQuery();
			while (set.next()) {
				pep2prots.add(new Pep2prot(set));
			}
			set.close();
			preparedStatement.close();
			for (Pep2prot pep2prot : pep2prots) {
				pep2prot.delete(conn);
			}
			conn.commit();
		}
		System.out.println("Deleted Duplicates: " + values.size());
	}
	
	@Test
	@Ignore
	public void removeDuplicateProteinEntries() throws SQLException {
		Map<String, Long> accession2IdMap = new TreeMap<String, Long>();
		Map<String, Long> duplicatesMap = new TreeMap<String, Long>();
		
		System.out.println("Selecting duplicate protein entries...");
		PreparedStatement ps = conn.prepareStatement(ProteinAccessor.getBasicSelect());
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			count++;
			if (accession2IdMap.get(rs.getString("accession")) != null) {
				duplicatesMap.put(rs.getString("accession") +"_Dupl" +count, rs.getLong("proteinid"));
			} else {
				accession2IdMap.put(rs.getString("accession"), rs.getLong("proteinid"));
			}
		}
		rs.close();
		ps.close();
		
		// Iterate the duplicates.
		Set<String> keySet = duplicatesMap.keySet();
		for (String acc : keySet) {
			Long proteinID = duplicatesMap.get(acc);
			ProteinAccessor protein = ProteinAccessor.findFromID(proteinID, conn);
			protein.delete(conn);
			conn.commit();
		}
		System.out.println("Deleted " + keySet.size() + " duplicate proteins (by accession)...");
	}
	
	@Test
	@Ignore
	public void testUpdateMisspelledAccessionEntries() throws SQLException {
		Map<String, Long> accession2IdMap = new TreeMap<String, Long>();
		
		System.out.println("Selecting protein entries having \"misspelled\" accession...");
		PreparedStatement ps = conn.prepareStatement(ProteinAccessor.getBasicSelect() + " WHERE accession LIKE '%|%'" );
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			accession2IdMap.put(rs.getString("accession"), rs.getLong("proteinid"));
		}
		rs.close();
		ps.close();
		int count = 0;
		for (String acc : accession2IdMap.keySet()) {
			ProteinAccessor proteinAccessor = ProteinAccessor.findFromID(accession2IdMap.get(acc), conn);
			String[] split = proteinAccessor.getAccession().split("\\|");
			proteinAccessor.setAccession(split[1]);
			proteinAccessor.update(conn);
			conn.commit();
			count++;
		}
		System.out.println("Updated " + count + " misspelled protein accessions...");
	}
	
	@Test
	@Ignore
	public void testUpdateProteinEntriesWithUniProtSource() throws SQLException {
		Map<String, Long> accession2IdMap = new TreeMap<String, Long>();
		
		System.out.println("Selecting all protein entries...");
		PreparedStatement ps = conn.prepareStatement(ProteinAccessor.getBasicSelect());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
				accession2IdMap.put(rs.getString("accession"), rs.getLong("proteinid"));
		}
		rs.close();
		ps.close();
		
		// Retrieve the UniProt entries.
		List<String> accessions = new ArrayList<String>();
		
		System.out.println("Checking for accessions being UniProt conform...");
		List<String> keySet = new ArrayList<String>(accession2IdMap.keySet());
		for (String accession : keySet) {
			// UniProt accession
			if (accession.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]")) {
				accessions.add(accession);		
			} 
		}
		
		System.out.println("Updating " + accessions.size() + " UniProt entries and their source field...");
		
		for (String acc : accessions) {
			ProteinAccessor proteinAccessor = ProteinAccessor.findFromID(accession2IdMap.get(acc), conn);
			proteinAccessor.setSource("UniProt");
			proteinAccessor.update(conn);
			conn.commit();
		}
	}
}
