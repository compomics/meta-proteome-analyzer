package de.mpa.job.evaluation;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.junit.internal.matchers.Each;

import de.mpa.db.DBManager;
import de.mpa.parser.mascot.xml.MascotRecord;
import de.mpa.parser.mascot.xml.MascotXMLParser;
import de.mpa.parser.mascot.xml.PeptideHit;
import de.mpa.parser.mascot.xml.ProteinHit;

public class SortTables {

	private static Connection conn;
	private static TreeMap<String, Description> dbSearchResults;
	private static SaveProtList saveProtList;
	private static SaveRawData saveRawData;
	private static TreeMap<String, Description> peptideHitKomplet= new TreeMap<String, Description>();
	private static TreeMap<String, ProtResultEntry> protHitKomplet = new TreeMap<String, ProtResultEntry>();
	
    /**
	 * @param args
	 */	
	public static void main(String[] args) {
	
		String[] runs = new String[] {	"Bande1",
										"Bande2",
										"Bande3",
										"Bande4",
										"Bande5",
										"Bande6",
										"Bande7",
										"Bande8",
										"Bande9",
										"Bande10"};
//										"Bandekomplett"};
	
		
//connection to db**********************************************
		try {
			DBManager dbManager = new DBManager();
			conn = dbManager.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
// SQL query****************************************************
	
	//iterate over all runs
		for (int i = 0; i < runs.length; i++) {
		try {
			//SQL query
			String fileName = runs[i];
			ArrayList<Description>inspectHits = findAllInspectHits(fileName);
			ArrayList<Description>xtandemHits = findAllXtandemHits(fileName);
			ArrayList<Description> omssaHits = findAllOmssaHits(fileName);
			ArrayList<Description> mascotHits = findAllMascotHits(fileName);
			//combine Results
			ReadData dataReader = new ReadData(inspectHits, xtandemHits, omssaHits,mascotHits);
			dbSearchResults = dataReader.getProtResults();
			//save raw data
			 saveRawData = new SaveRawData(dbSearchResults,runs[i]+ "_"+"1_Rawdata_Peptide.csv");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
// Data Analyses*****************************************************
		TreeMap<String, Description> pepResultsAccesion = new TreeMap<String, Description>();
		try {
			//remove all not Ecoli Entries
			for (String key : dbSearchResults.keySet()){
			if (dbSearchResults.get(key).getDesc().contains("Escherichia coli")){
				Description description = new Description(dbSearchResults.get(key).getDesc());
				description.setAccession(dbSearchResults.get(key).getAccession());
				description.setSequence(dbSearchResults.get(key).getSequence());
				description.setFileName(dbSearchResults.get(key).getFileName());
				description.setInspectR(dbSearchResults.get(key).isInspectR());
				description.setXtandemR(dbSearchResults.get(key).isXtandemR());
				description.setOmssaR(dbSearchResults.get(key).isOmssaR());
				description.setMascotR(dbSearchResults.get(key).isMascotR());
				pepResultsAccesion.put(key, description);
			}
			}
			SaveRawData saveRawData = new SaveRawData(pepResultsAccesion,runs[i]+ "_"+"2_EcoliPeptides.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
// 1. Protein Hits*****************************************************
		//-->protList all ProtEntries, collects all peptides for each protein
		
		TreeMap<String, ProtResultEntry> finalProtResultList = new TreeMap<String, ProtResultEntry>();
		
		for (String key : pepResultsAccesion.keySet()) {
			if (finalProtResultList.containsKey(pepResultsAccesion.get(key).getAccession()))// is in finalProtResultList 
			{
				String keyInFinalList 		= pepResultsAccesion.get(key).getAccession();
				if (finalProtResultList.get(keyInFinalList).getSequence().contains(pepResultsAccesion.get(key).getSequence())){
					
				}  //sequence is the same 
				else{
					//add Sequences
					finalProtResultList.get(keyInFinalList).setSequence(finalProtResultList.get(keyInFinalList).getSequence() + "_" +pepResultsAccesion.get(key).getSequence());
					//add anz peptides
					finalProtResultList.get(keyInFinalList).setAnzPeptide(finalProtResultList.get(keyInFinalList).getAnzPeptide()+1);
					//add dbsearch type
					if (pepResultsAccesion.get(key).isInspectR())
					{
						finalProtResultList.get(keyInFinalList).setInpectHit(1+ finalProtResultList.get(keyInFinalList).getInpectHit());	
					}
					if (pepResultsAccesion.get(key).isXtandemR())
					{
						finalProtResultList.get(keyInFinalList).setXtandemHit(1+ finalProtResultList.get(keyInFinalList).getXtandemHit());	
					}
					if (pepResultsAccesion.get(key).isOmssaR())
					{
						finalProtResultList.get(keyInFinalList).setOmssaHit(1+ finalProtResultList.get(keyInFinalList).getOmssaHit());	
					}
					if (pepResultsAccesion.get(key).isMascotR())
					{
						finalProtResultList.get(keyInFinalList).setMascotHit(1+ finalProtResultList.get(keyInFinalList).getMascotHit());	
					}
				}
			}
			else{// not in finalProtResultsList
				ProtResultEntry protResultsEntry = new ProtResultEntry();
				protResultsEntry.setAccession(pepResultsAccesion.get(key).getAccession());
				protResultsEntry.setDesc(pepResultsAccesion.get(key).getDesc());
				protResultsEntry.setAnzPeptide(1);
				if (pepResultsAccesion.get(key).isInspectR())
				{
					protResultsEntry.setInpectHit(1);	
				}
				if (pepResultsAccesion.get(key).isXtandemR())
				{
					protResultsEntry.setXtandemHit(1);	
				}
				if (pepResultsAccesion.get(key).isOmssaR())
				{
					protResultsEntry.setOmssaHit(1);	
				}
				if (pepResultsAccesion.get(key).isMascotR())
				{
					protResultsEntry.setMascotHit(1);	
				}
				protResultsEntry.setSequence(pepResultsAccesion.get(key).getSequence());
				String keyProt= pepResultsAccesion.get(key).getAccession();
				finalProtResultList.put(keyProt,protResultsEntry);
			}
		}
		saveProtList = new SaveProtList(finalProtResultList,runs[i]+ "_"+"3_ProteinList.csv");

// 2. Protein Hits*****************************************************
		//-->protList all unique ProtEntries
		// remove double strains without new sequences
		for (String key : finalProtResultList.keySet()) {
			String locus = finalProtResultList.get(key).getAccession();
			locus = locus.substring(0,locus.indexOf("_"));
			for (String key2 : finalProtResultList.keySet()) {
				// not same entry
				if (key!=key2){
					// but same loci
					String locus2 = finalProtResultList.get(key2).getAccession();
					locus2 = locus2.substring(0,locus2.indexOf("_"));
					if (locus.matches(locus2))	
					{
						// and no extra peptides
						String sequence1 = finalProtResultList.get(key).getSequence();
						String sequence2 = finalProtResultList.get(key2).getSequence();
						if (sequence1.contains(sequence2)) {
							if (finalProtResultList.get(key).getSubOrdinaryHit()==0){
								try {
									finalProtResultList.get(key2).setSubOrdinaryHit(1+finalProtResultList.get(key2).getSubOrdinaryHit());
								} catch (Exception e) {
								}	
							}
							}
						}
					}
			}
		}
		saveProtList = new SaveProtList(finalProtResultList,runs[i]+ "_"+"4_ProteinList_filtered.csv");
	
// 3. Protein Hits*****************************************************		
		
			TreeMap<String, Description> pepResultsLocus = new TreeMap<String, Description>();
			
			// new pepList with locus as key
			for (String key : pepResultsAccesion.keySet()) {
				Description descLocus 	= 	new Description(pepResultsAccesion.get(key).getDesc());
				String accession 		= 	pepResultsAccesion.get(key).getAccession();
				String locus 			= 	accession.substring(0,accession.indexOf("_"));	
				descLocus.setAccession(locus);
				descLocus.setSequence(pepResultsAccesion.get(key).getSequence());
				descLocus.setFileName(pepResultsAccesion.get(key).getFileName());
				descLocus.setInspectR(pepResultsAccesion.get(key).isInspectR());
				descLocus.setXtandemR(pepResultsAccesion.get(key).isXtandemR());
				descLocus.setOmssaR(pepResultsAccesion.get(key).isOmssaR());
				descLocus.setCruxR(pepResultsAccesion.get(key).isCruxR());
				descLocus.setMascotR(pepResultsAccesion.get(key).isMascotR());
				String keyL				= locus + "_"+ descLocus.getSequence();
				try {
					pepResultsLocus.put(keyL, descLocus);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			 saveRawData = new SaveRawData(pepResultsLocus,runs[i]+ "_"+"2_EcoliPeptides_locus.csv");
			
			// new ProteinList where peptides with the same protein locus are mapped
			TreeMap<String, ProtResultEntry> locusProtResults = new TreeMap<String, ProtResultEntry>();
			for (String key : pepResultsLocus.keySet()) {
				if (locusProtResults.containsKey(pepResultsLocus.get(key).getAccession()))// is in locusProtResults 
				{
					String protKey 		= pepResultsLocus.get(key).getAccession();
					if (locusProtResults.get(protKey).getSequence().contains(pepResultsLocus.get(key).getSequence())){
						
					}  //sequence is the same 
					else{
						//add Sequences
						locusProtResults.get(protKey).setSequence(locusProtResults.get(protKey).getSequence() + "_" +pepResultsLocus.get(key).getSequence());
						//add anz peptides
						locusProtResults.get(protKey).setAnzPeptide(locusProtResults.get(protKey).getAnzPeptide()+1);
						//add dbsearch type
						if (pepResultsLocus.get(key).isInspectR())
						{
							locusProtResults.get(protKey).setInpectHit(1+ locusProtResults.get(protKey).getInpectHit());	
						}
						if (pepResultsLocus.get(key).isXtandemR())
						{
							locusProtResults.get(protKey).setXtandemHit(1+ locusProtResults.get(protKey).getXtandemHit());	
						}
						if (pepResultsLocus.get(key).isOmssaR())
						{
							locusProtResults.get(protKey).setOmssaHit(1+ locusProtResults.get(protKey).getOmssaHit());	
						}
						if (pepResultsLocus.get(key).isMascotR())
						{
							locusProtResults.get(protKey).setMascotHit(1+ locusProtResults.get(protKey).getMascotHit());	
						}
					}
				}
				else{// not in locusProtResultsList 
					ProtResultEntry protResultsEntry = new ProtResultEntry();
					protResultsEntry.setAccession(pepResultsLocus.get(key).getAccession());
					protResultsEntry.setDesc(pepResultsLocus.get(key).getDesc());
					protResultsEntry.setAnzPeptide(1);
					if (pepResultsLocus.get(key).isInspectR())
					{
						protResultsEntry.setInpectHit(1);	
					}
					if (pepResultsLocus.get(key).isXtandemR())
					{
						protResultsEntry.setXtandemHit(1);	
					}
					if (pepResultsLocus.get(key).isOmssaR())
					{
						protResultsEntry.setOmssaHit(1);	
					}
					if (pepResultsLocus.get(key).isMascotR())
					{
						protResultsEntry.setMascotHit(1);	
					}
					protResultsEntry.setSequence(pepResultsLocus.get(key).getSequence());
					String keyProt= pepResultsLocus.get(key).getAccession();
					locusProtResults.put(keyProt,protResultsEntry);
				}
			}
			saveProtList = new SaveProtList(locusProtResults,runs[i]+ "_"+"5_locusProtResults.csv");
		
	
		//create complete peptide and protein List
		
		for (String key : pepResultsLocus.keySet()) 
		{
			if (peptideHitKomplet.containsKey(key)==false)
			{
				Description descComplete = new Description(pepResultsLocus.get(key).getDesc());
				descComplete.setAccession(pepResultsLocus.get(key).getAccession());
				descComplete.setSequence(pepResultsLocus.get(key).getSequence());
				descComplete.setFileName(pepResultsLocus.get(key).getFileName());
				descComplete.setInspectR(pepResultsLocus.get(key).isInspectR());
				descComplete.setXtandemR(pepResultsLocus.get(key).isXtandemR());
				descComplete.setOmssaR(pepResultsLocus.get(key).isOmssaR());
				descComplete.setCruxR(pepResultsLocus.get(key).isCruxR());
				descComplete.setMascotR(pepResultsLocus.get(key).isMascotR());
				String keyComplete			= pepResultsLocus.get(key).getAccession() + "_" + pepResultsLocus.get(key).getSequence();
				peptideHitKomplet.put(keyComplete, descComplete);
			}
		}
		
		
		for (String key : locusProtResults.keySet()) {
			if (protHitKomplet.containsKey(key)==false){
				ProtResultEntry protResultsEntry = new ProtResultEntry();
				protResultsEntry.setAccession(locusProtResults.get(key).getAccession());
				protResultsEntry.setAnzPeptide(locusProtResults.get(key).getAnzPeptide());
				protResultsEntry.setDesc(locusProtResults.get(key).getDesc());
				protResultsEntry.setSequence(locusProtResults.get(key).getSequence());
				protResultsEntry.setSubOrdinaryHit(locusProtResults.get(key).getSubOrdinaryHit());
				protResultsEntry.setInpectHit(locusProtResults.get(key).getInpectHit());
				protResultsEntry.setXtandemHit(locusProtResults.get(key).getXtandemHit());
				protResultsEntry.setOmssaHit(locusProtResults.get(key).getOmssaHit());
				protResultsEntry.setMascotHit(locusProtResults.get(key).getMascotHit());
				protHitKomplet.put(key, protResultsEntry);
			}
		}
		}
		
		saveRawData = new SaveRawData(peptideHitKomplet,"CompletePeptideList.csv");
		saveProtList = new SaveProtList(protHitKomplet,"CompleteProtList.csv");
		System.out.println("ready");
		
	}
	// SQL querys +Mascot Parser*********************************************************************************************	
	/**
	 * Method to get Inspect Hits
	 * @param fileNamePrefix string containing filename prefix
	 * @return
	 * @throws SQLException
	 */
	private static ArrayList<Description> findAllInspectHits(String fileNamePrefix) throws SQLException {
		ArrayList<Description> temp = new ArrayList<Description>();
		PreparedStatement ps = conn.prepareStatement("SELECT pr.accession, pr.description, s.filename, p.sequence" + 
				" FROM searchspectrum s, peptide p, protein pr, pep2prot p2p, inspecthit x" + 
				" WHERE x.p_value < 0.05 AND p.peptideid = x.fk_peptideid AND s.spectrumid = x.fk_spectrumid" +
				" AND pr.proteinid = p2p.fk_proteinsid AND p.peptideid = p2p.fk_peptideid AND s.filename like ?");
		ps.setString(1, fileNamePrefix+"%");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp.add(new Description(rs));
		}
		rs.close();
		ps.close();
		return temp;
	}
	
	/**
	 * Method to get Inspect Hits
	 * @param fileNamePrefix string containing filename prefix
	 * @return
	 * @throws SQLException
	 */
	private static ArrayList<Description> findAllXtandemHits(String fileNamePrefix) throws SQLException {
		ArrayList<Description> temp = new ArrayList<Description>();
		PreparedStatement ps = conn.prepareStatement("SELECT pr.accession, pr.description, s.filename, p.sequence"+
													" FROM searchspectrum s, peptide p, protein pr, pep2prot p2p, xtandemhit x "+
													" WHERE x.qvalue < 0.05 and p.peptideid = x.fk_peptideid and s.spectrumid = x.fk_spectrumid"+
													" and pr.proteinid = p2p.fk_proteinsid and p.peptideid = p2p.fk_peptideid and s.filename like ?");
		ps.setString(1, fileNamePrefix+"%");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp.add(new Description(rs));
		}
		rs.close();
		ps.close();
		return temp;
	}
	
	/**
	 * Method to get Inspect Hits
	 * @param fileNamePrefix string containing filename prefix
	 * @return
	 * @throws SQLException
	 */
	private static ArrayList<Description> findAllOmssaHits(String fileNamePrefix) throws SQLException {
		ArrayList<Description> temp = new ArrayList<Description>();
		PreparedStatement ps = conn.prepareStatement("SELECT pr.accession, pr.description, s.filename, p.sequence" +
				" FROM searchspectrum s, peptide p, protein pr, pep2prot p2p, omssahit x" +
				" WHERE x.pvalue < 0.05 AND x.evalue <0.05 AND p.peptideid = x.fk_peptideid AND s.spectrumid = x.fk_spectrumid" +
				" AND pr.proteinid = p2p.fk_proteinsid AND p.peptideid = p2p.fk_peptideid AND s.filename LIKE ?");
		ps.setString(1, fileNamePrefix+"%");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			temp.add(new Description(rs));
		}
		rs.close();
		ps.close();
		return temp;
	}
	
	/**
	 * Method to get Mascot Hits
	 * @param fileNamePrefix string containing filename prefix
	 * @return
	 * @throws SQLException
	 */
	private static ArrayList<Description> findAllMascotHits(String fileNamePrefix) {
		ArrayList<Description> temp = new ArrayList<Description>();
		MascotXMLParser mascotXMLParser = new MascotXMLParser(new File(fileNamePrefix+ ".xml"));
		MascotRecord mascotRecord = mascotXMLParser.parse();
		for (ProteinHit protHit : mascotRecord.getProteinHits()) {
			String accession	 	= protHit.getAccessions().get(0);
			String desc 		 	= protHit.getDescriptions().get(0);
			desc= desc.replaceAll(";", "");
			for (PeptideHit pepHit : protHit.getPeptideHits()) {
				String sequence 	= pepHit.getSequence();
				String fileName		= fileNamePrefix +"_" + pepHit.getAttributes().get("query");
				Description description = new Description(desc);
				description.setAccession(accession);
				description.setSequence(sequence);
				description.setFileName(fileName);
				temp.add(description);
			}
		}
		return temp;
	}

}
