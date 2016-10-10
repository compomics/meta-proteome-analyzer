package de.mpa.analysis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Test;

import de.mpa.db.DBManager;
import de.mpa.db.accessor.ExperimentTableAccessor;
import de.mpa.db.accessor.ProteinTableAccessor;
import de.mpa.db.accessor.Uniprotentry;


public class DeleteBLASTHits {
	
	@Ignore
	@Test
	public void delete_blasthits() throws SQLException {
		UniProtUtilities.deleteblasthits();
	}
	
	@Ignore
	@Test
	public void revert_old_blasthits() throws SQLException {
		
		// connect to db
		Connection conn = DBManager.getInstance().getConnection();
		// find all blast proteins -> contain "_BLAST_"
		List<ProteinTableAccessor> proteinlist = new ArrayList<ProteinTableAccessor>();
		TreeMap<String, List<String>> experimentMap = new TreeMap<String, List<String>>();
		TreeMap<String, String> protein_exp = new TreeMap<String, String>();
		//System.out.println("SELECT * FROM protein p WHERE p.description LIKE 'MG:%'");
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM protein p WHERE p.description LIKE 'MG:%'");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
		    proteinlist.add(new ProteinTableAccessor(rs));
		}
		System.out.println("Revertlist-Size: "+proteinlist.size());
		rs.close();
		ps.close();
		//System.out.println("SELECT * FROM protein p WHERE p.description LIKE 'MG:%'");
//		ps = conn.prepareStatement("SELECT * FROM protein p WHERE p.description LIKE 'BLAST:%'");
//		rs = ps.executeQuery();
//		while (rs.next()) {
//		    proteinlist.add(new ProteinTableAccessor(rs));
//		}
//		rs.close();
//		ps.close();
//		

		
		
		int up = 0;
		int prots = 0;
		int to_revert = 0;
		int mascots = 0;
		System.out.println("Revertlist-Size: "+proteinlist.size());
		// cycle through proteins for reversion
		for (ProteinTableAccessor protein : proteinlist) {
			prots++;
			if ((prots % 1000) == 0) {
				System.out.print("\n Proteins: " + prots + " of "+ proteinlist.size());
			}
			
			PreparedStatement ps2;
			PreparedStatement ps3;
			ResultSet rs2;
			ResultSet rs3;
			ExperimentTableAccessor exAc;
			// experiments
			ps = conn.prepareStatement("SELECT * FROM xtandemhit x WHERE x.fk_proteinid = ?");
			ps.setLong(1, protein.getProteinid());
			rs = ps.executeQuery();
			while (rs.next()) {
				ps3 = conn.prepareStatement("SELECT * FROM searchspectrum ss WHERE ss.searchspectrumid = ?");			
				ps3.setLong(1, rs.getLong("fk_searchspectrumid"));
				rs3 = ps3.executeQuery();
				rs3.next();
				ps2 = conn.prepareStatement("SELECT * FROM experiment ex WHERE ex.experimentid = ?");
				ps2.setLong(1, rs3.getLong("fk_experimentid"));
				rs2 = ps2.executeQuery();
				rs2.next();
				exAc = new ExperimentTableAccessor(rs2);
				if (experimentMap.containsKey(exAc.getTitle())) {
					experimentMap.get(exAc.getTitle()).add(protein.getAccession());
					
				} else {
					List<String> prot_list = new ArrayList<String>();
					prot_list.add(protein.getAccession());
					experimentMap.put(exAc.getTitle(), prot_list);
				}
				protein_exp.put(protein.getAccession(), exAc.getTitle());
				ps3.close();
				rs3.close();
				ps2.close();
				rs2.close();
			}
			ps = conn.prepareStatement("SELECT * FROM omssahit o WHERE o.fk_proteinid = ?");
			ps.setLong(1, protein.getProteinid());
			rs = ps.executeQuery();
			while (rs.next()) {
				ps3 = conn.prepareStatement("SELECT * FROM searchspectrum ss WHERE ss.searchspectrumid = ?");
				ps3.setLong(1, rs.getLong("fk_searchspectrumid"));
				rs3 = ps3.executeQuery();
				rs3.next();
				ps2 = conn.prepareStatement("SELECT * FROM experiment ex WHERE ex.experimentid = ?");
				ps2.setLong(1, rs3.getLong("fk_experimentid"));
				rs2 = ps2.executeQuery();
				rs2.next();
				exAc = new ExperimentTableAccessor(rs2);
				if (experimentMap.containsKey(exAc.getTitle())) {
					experimentMap.get(exAc.getTitle()).add(protein.getAccession());
				} else {
					List<String> prot_list = new ArrayList<String>();
					prot_list.add(protein.getAccession());
					experimentMap.put(exAc.getTitle(), prot_list);
				}
				protein_exp.put(protein.getAccession(), exAc.getTitle());
				ps3.close();
				rs3.close();
				ps2.close();
				rs2.close();
			}
		
			ps = conn.prepareStatement("SELECT * FROM mascothit m WHERE m.fk_proteinid = ?");
			ps.setLong(1, protein.getProteinid());
			rs = ps.executeQuery();
			while (rs.next()) {
				ps3 = conn.prepareStatement("SELECT * FROM searchspectrum ss WHERE ss.searchspectrumid = ?");
				ps3.setLong(1, rs.getLong("fk_searchspectrumid"));
				rs3 = ps3.executeQuery();
				rs3.next();		
				ps2 = conn.prepareStatement("SELECT * FROM experiment ex WHERE ex.experimentid = ?");
				ps2.setLong(1, rs3.getLong("fk_experimentid"));
				rs2 = ps2.executeQuery();
				rs2.next();
				exAc = new ExperimentTableAccessor(rs2);
				if (experimentMap.containsKey(exAc.getTitle())) {
					experimentMap.get(exAc.getTitle()).add(protein.getAccession());
				} else {
					List<String> prot_list = new ArrayList<String>();
					prot_list.add(protein.getAccession());
					experimentMap.put(exAc.getTitle(), prot_list);
				}
				protein_exp.put(protein.getAccession(), exAc.getTitle());
				ps3.close();
				rs3.close();
				ps2.close();
				rs2.close();
			}
			rs.close();
			ps.close();
//			System.out.println("Reverting: "+prot_acc);
			if (protein_exp.containsKey(protein.getAccession())) {
				//System.out.println("Protein : "+ protein.getAccession() + " in : "+ protein_exp.get(protein.getAccession()));
//				if (protein_exp.get(protein.getAccession()).contains("BGP")) {
					//System.out.println("Protein from: "+protein_exp.get(protein.getAccession()));
					to_revert++;
					if (protein_exp.get(protein.getAccession()).startsWith("Mascot")) {
						mascots++;
					}
					//protein.setAccession(prot_acc);			
					protein.setDescription("Metagenome Unknown");		
					// find the uniprotentry to this protein 
					Uniprotentry uniprotentry = Uniprotentry.findFromProteinID(protein.getProteinid(), conn);
					// and delete it
					if (uniprotentry != null) {
						up++;
		//				System.out.println("UniprotEntry: "+uniprotentry.getUniprotentryid());
						uniprotentry.delete(conn);
					}
					protein.update(conn);
					conn.commit();
		//			System.out.println("Reverted: " + protein.getAccession());
//				}
			} else {
				System.out.println("No Experiment: "+ protein.getAccession());
			}
		}
		System.out.println("UniprotEntries: "+up);
		System.out.println("Revert: "+to_revert);
		System.out.println("Mascots: "+mascots);
		//System.out.println("Experimentmap: "+experimentMap);
		for (String ex : experimentMap.keySet()) {
			System.out.println(experimentMap.size() + " : " + ex);
		}
		conn.commit();
	}
}

