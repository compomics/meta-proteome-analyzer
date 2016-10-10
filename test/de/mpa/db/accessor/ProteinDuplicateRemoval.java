package de.mpa.db.accessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpa.db.DBManager;


/**
 * Helper class that removes duplicate entries from the database (duplicate protein accession number).
 * @author K. Schallert
 *
 */
public class ProteinDuplicateRemoval {
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
	 * Method that removes duplicate entries from the database (duplicate accession number).
	 * Finds protein duplicates, removes them and updates all tables using FK_PROTEINID to a single protein.
	 * The protein with the oldest creationdate is chosen to replace all duplicates.
	 * @author K. Schallert
	 *
	 */
	@Test
	@Ignore
	public void RemoveDuplicates() throws SQLException {
		HashMap<String, HashMap<Long, java.sql.Timestamp>> protein_map = new HashMap<>();
		PreparedStatement prs = conn.prepareStatement("SELECT protein.proteinid, protein.accession, protein.creationdate FROM protein");
		ResultSet aRS = prs.executeQuery();
		while (aRS.next()) {
			// cylce through protein table and put accessions and proteinids into map
			String acc = (String) aRS.getObject("accession");
			Long protid = (Long) aRS.getLong("proteinid");
			java.sql.Timestamp creationdate = aRS.getTimestamp("creationdate");
			if (protein_map.containsKey(acc)) {
				protein_map.get(acc).put(protid, creationdate);
			} else {
				HashMap<Long, java.sql.Timestamp> current_protids = new HashMap<Long, java.sql.Timestamp>();
				current_protids.put(protid, creationdate);
				protein_map.put(acc, current_protids);
			}
		}
		// some feedback
		int uniprotentry_updates = 0;
		int pep2prot_updates = 0;
		int mascothit_updates = 0;
		int cruxhit2prot_updates = 0;
		int omssahit_updates = 0;
		int inspecthit_updates = 0;
		int xtandemhit_updates = 0;
		int protein_deletions = 0;
		// cycle through all accessions and find those with more than one entry
		for (String accession : protein_map.keySet()) {			
			if (protein_map.get(accession).size() != 1) {
				System.out.println("Removing: "+ accession);
				int this_accession_deletions = 0;
				HashMap<Long, java.sql.Timestamp> protein_ids = protein_map.get(accession);				
				// not all entries are equal, some already have blast updated data
				// first we need to choose the protein entry that remains, choose the oldest
				Long current_chosen_id = null;
				java.sql.Timestamp current_creationdate = null; 
				for (Long protid : protein_ids.keySet()) {
					// first entry
					if (current_chosen_id == null) {
						current_chosen_id = protid;
						current_creationdate = protein_ids.get(protid);
					// check date against current entry
					} else if (current_creationdate.after(protein_ids.get(protid))) {
						current_chosen_id = protid;
						current_creationdate = protein_ids.get(protid);
					}
				}
				// after that current_chosen_id is the one that remains
				// new we delete and replace table entries
				for (Long protid : protein_ids.keySet()) {
					if (protid != current_chosen_id) {
						// all database manipulation happens here
						// the FK_PROTEINID is found in
						//
						// pep2prot table
						//
						List<Pep2protTableAccessor> pep2prot_list = new ArrayList<Pep2protTableAccessor>();
				        prs = conn.prepareStatement("select pp.* from pep2prot pp where pp.fk_proteinid = ?");
				        prs.setLong(1, protid);
				        aRS = prs.executeQuery();				        
				        while (aRS.next()) {
				        	Pep2protTableAccessor hit = new Pep2protTableAccessor(aRS);
				        	pep2prot_list.add(hit);	
				        }
				        aRS.close();
				        prs.close();				        
				        for (Pep2protTableAccessor pep2prot : pep2prot_list) {
				        	pep2prot.setFk_proteinid(current_chosen_id);
				        	pep2prot.update(conn);
							conn.commit();
							pep2prot_updates++;
				        }	
						//
						// uniprotentry table
						//
						List<UniprotentryTableAccessor> uniprotentry_list = new ArrayList<UniprotentryTableAccessor>();
				        prs = conn.prepareStatement("select ue.* from uniprotentry ue where ue.fk_proteinid = ?");
				        prs.setLong(1, protid);
				        aRS = prs.executeQuery();				        
				        while (aRS.next()) {
				        	UniprotentryTableAccessor hit = new UniprotentryTableAccessor(aRS);
				        	uniprotentry_list.add(hit);	
				        }
				        aRS.close();
				        prs.close();				        
				        for (UniprotentryTableAccessor uniprotentry : uniprotentry_list) {
				        	uniprotentry.update(conn);
							conn.commit();
							uniprotentry_updates++;
				        }	
						//
						// mascothit table
						//
						List<MascothitTableAccessor> mascothit_list = new ArrayList<MascothitTableAccessor>();
				        prs = conn.prepareStatement("select mh.* from mascothit mh where mh.fk_proteinid = ?");
				        prs.setLong(1, protid);
				        aRS = prs.executeQuery();				        
				        while (aRS.next()) {
				        	MascothitTableAccessor hit = new MascothitTableAccessor(aRS);
				        	mascothit_list.add(hit);	
				        }
				        aRS.close();
				        prs.close();				        
				        for (MascothitTableAccessor mascothit : mascothit_list) {
				        	mascothit.setFk_proteinid(current_chosen_id);
				        	mascothit.update(conn);
							conn.commit();							
							mascothit_updates++;
				        }
						//
						// omssahit table
						//
						List<OmssahitTableAccessor> omssahit_list = new ArrayList<OmssahitTableAccessor>();
				        prs = conn.prepareStatement("select oh.* from omssahit oh where oh.fk_proteinid = ?");
				        prs.setLong(1, protid);
				        aRS = prs.executeQuery();				        
				        while (aRS.next()) {
				        	OmssahitTableAccessor hit = new OmssahitTableAccessor(aRS);
				        	omssahit_list.add(hit);	
				        }
				        aRS.close();
				        prs.close();				        
				        for (OmssahitTableAccessor omssahit : omssahit_list) {
				        	omssahit.setFk_proteinid(current_chosen_id);
				        	omssahit.update(conn);
							conn.commit();
							omssahit_updates++;
				        }
						//
						// xtandemhit table
						//
						List<XtandemhitTableAccessor> xtandemhit_list = new ArrayList<XtandemhitTableAccessor>();
				        prs = conn.prepareStatement("select xh.* from xtandemhit xh where xh.fk_proteinid = ?");
				        prs.setLong(1, protid);
				        aRS = prs.executeQuery();				        
				        while (aRS.next()) {
				        	XtandemhitTableAccessor hit = new XtandemhitTableAccessor(aRS);
				        	xtandemhit_list.add(hit);	
				        }
				        aRS.close();
				        prs.close();				        
				        for (XtandemhitTableAccessor xtandemhit : xtandemhit_list) {
				        	xtandemhit.setFk_proteinid(current_chosen_id);
				        	xtandemhit.update(conn);
							conn.commit();							
							xtandemhit_updates++;
				        }
						//
						// inspecthit table
						//
						List<InspecthitTableAccessor> inspecthit_list = new ArrayList<InspecthitTableAccessor>();
				        prs = conn.prepareStatement("select ih.* from inspecthit ih where ih.fk_proteinid = ?");
				        prs.setLong(1, protid);
				        aRS = prs.executeQuery();				        
				        while (aRS.next()) {
				        	InspecthitTableAccessor hit = new InspecthitTableAccessor(aRS);
				        	inspecthit_list.add(hit);	
				        }
				        aRS.close();
				        prs.close();				        
				        for (InspecthitTableAccessor inspecthit : inspecthit_list) {
				        	inspecthit.setFk_proteinid(current_chosen_id);
				        	inspecthit.update(conn);
							conn.commit();
							inspecthit_updates++;
				        }
						//
						// cruxhittoprot table
				        //
						List<Cruxhit2protTableAccessor> cruxhittoprot_list = new ArrayList<Cruxhit2protTableAccessor>();
				        prs = conn.prepareStatement("select cx.* from cruxhit2prot cx where cx.fk_proteinid = ?");
				        prs.setLong(1, protid);
				        aRS = prs.executeQuery();				        
				        while (aRS.next()) {
				        	Cruxhit2protTableAccessor hit = new Cruxhit2protTableAccessor(aRS);
				        	cruxhittoprot_list.add(hit);	
				        }
				        aRS.close();
				        prs.close();				        
				        for (Cruxhit2protTableAccessor cruxhit2prot : cruxhittoprot_list) {
				        	cruxhit2prot.setFk_proteinid(current_chosen_id);
				        	cruxhit2prot.update(conn);
							conn.commit();							
							cruxhit2prot_updates++;
				        }
				        //
				        // protein deletion last
				        //
						List<ProteinTableAccessor> protein_list = new ArrayList<ProteinTableAccessor>();
				        prs = conn.prepareStatement("select pr.* from protein pr where pr.proteinid = ?");
				        prs.setLong(1, protid);
				        aRS = prs.executeQuery();				        
				        while (aRS.next()) {
				        	ProteinTableAccessor hit = new ProteinTableAccessor(aRS);
				        	protein_list.add(hit);	
				        }
				        aRS.close();
				        prs.close();	        
				        for (ProteinTableAccessor protein : protein_list) {
				        	protein.delete(conn);
							conn.commit();
							protein_deletions++;
							this_accession_deletions++;
				        }
					}
				}
				// some feedback
				System.out.println("Removed "+this_accession_deletions+" duplicates of: "+ accession);
			}				
		}
		// some feedback
		System.out.println("Duplicate(s) deleted: "+ protein_deletions);
		System.out.println("uniprotentry_updates: "+ uniprotentry_updates);
		System.out.println("pep2prot_updates: "+ pep2prot_updates);
		System.out.println("mascothit_updates: "+ mascothit_updates);
		System.out.println("cruxhit2prot_updates: "+ cruxhit2prot_updates);
		System.out.println("omssahit_updates: "+ omssahit_updates);
		System.out.println("inspecthit_updates: "+ inspecthit_updates);
		System.out.println("xtandemhit_updates: "+ xtandemhit_updates);
	}	
}
