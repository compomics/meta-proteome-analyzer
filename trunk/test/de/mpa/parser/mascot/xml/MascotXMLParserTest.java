package de.mpa.parser.mascot.xml;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.Spec2pep;
import de.mpa.io.parser.mascot.xml.MascotPeptideHit;
import de.mpa.io.parser.mascot.xml.MascotProteinHit;
import de.mpa.io.parser.mascot.xml.MascotRecord;
import de.mpa.io.parser.mascot.xml.MascotXMLParser;

public class MascotXMLParserTest extends TestCase {
	
	public void testMascotXMLParsing() throws SQLException {

//		long expId = 3L;
//		File xmlFile = new File("BSA1.xml");
//		long expId = 5L;
//		File xmlFile = new File("BSA2.xml");
//		File xmlFile = new File("BSA3.xml");
//		File xmlFile = new File("BSA4.xml");
//		File xmlFile = new File("BSA5.xml");
//		long expId = 54L;
//		File xmlFile = new File("Ecoli_01.xml");
//		long expId = 55L;
//		File xmlFile = new File("Ecoli_02.xml");
//		File xmlFile = new File("Ecoli_03.xml");
//		File xmlFile = new File("Ecoli_04.xml");
//		File xmlFile = new File("Ecoli_05.xml");
//		long expId = 275L;
//		File xmlFile = new File("04F_12_Replikatmessung_1_RE3_01_5912.xml");
		long expId = 276L;
//		File xmlFile = new File("04F_12_Replikatmessung_2_RE3_01_5913.xml");
//		File xmlFile = new File("04F_12_Replikatmessung_3_RE3_01_5914.xml");
		File xmlFile = new File("04F_12_Replikatmessung_4_RE3_01_5915.xml");
		
		
		MascotRecord record = new MascotXMLParser(xmlFile).parse();
		
		Set<String> titles = new HashSet<String>();
		for (MascotProteinHit protein : record.getProteins()) {
			for (MascotPeptideHit peptide : protein.getPeptides()) {
				if (peptide.hasModifications()) {
					titles.add(peptide.getScanTitle());
				}
			}
		}
		
		Connection conn = DBManager.getInstance().getConnection();
		
		StringBuilder stmt = new StringBuilder("SELECT p.*, s2p.*, s.* FROM spectrum s "
				+ "INNER JOIN libspectrum ls ON s.spectrumid = ls.fk_spectrumid "
				+ "INNER JOIN spec2pep s2p ON s.spectrumid = s2p.fk_spectrumid "
				+ "INNER JOIN peptide p ON s2p.fk_peptideid = p.peptideid "
				+ "WHERE s.title IN (");
		for (String title : titles) {
			stmt.append("\'");
			stmt.append(title);
			stmt.append("\',");
		}
		stmt.replace(stmt.length() - 1, stmt.length(), ") ");
		stmt.append("AND ls.fk_experimentid = ?");
		
		PreparedStatement ps = conn.prepareStatement(stmt.toString(),
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		ps.setLong(1, expId);
		
		ResultSet rs = ps.executeQuery();
		Map<String, List<MascotPeptideHit>> peptideMap = record.getPeptideMap();
		while (rs.next()) {
			// get spectrum/peptide information from resul
			String dbTitle = rs.getString("title");
			String dbSequence = rs.getString("sequence");
			
			// get corresponding peptides
			List<MascotPeptideHit> peptides = peptideMap.get(dbTitle);
			// iterate peptides to find one that has a matching peptide sequence
			for (MascotPeptideHit peptide : peptides) {
				if (peptide.getSequence().equals(dbSequence)) {
					// get sequence containing modifications
					String modSequence = peptide.getModifiedSequence();
					
					// check whether peptide with sequence containing mods is already in remote database
					PreparedStatement ps2 = conn.prepareStatement(
							  "SELECT p.* FROM peptide p "
							+ "WHERE p.sequence = ?");
					ps2.setString(1, modSequence);
					
					ResultSet rs2 = ps2.executeQuery();
					long peptideId;
					if (!rs2.next()) {
						// modified peptide sequence has not been found, create new entry
						HashMap<Object, Object> dataPeptide = new HashMap<Object, Object>(2);
						dataPeptide.put(PeptideAccessor.SEQUENCE, modSequence);
						PeptideAccessor pepAcc = new PeptideAccessor(dataPeptide);
						pepAcc.persist(conn);
						peptideId = (Long) pepAcc.getGeneratedKeys()[0];
					} else {
						// modified peptide sequence is already in database, grab id
						peptideId = rs2.getLong("peptideid");
					}
					rs2.close();
					ps2.close();
					
					// update spec2pep entry
					Spec2pep spec2pep = new Spec2pep(rs);
					spec2pep.setFk_peptideid(peptideId);
					spec2pep.update(conn);
				}
			}
		}
		rs.close();
		ps.close();
		
		conn.commit();
	}
	
//	private MascotXMLParser parser;
//
//	@Before
//	public void setUp() {
//		// TODO: add proper mascot xml file to test/de/mpa/resources
//		File file = new File("Ecoli_01.xml");
//		parser = new MascotXMLParser(file);
//	}
//	
//	@Test
//	public void testParse() {
//		// MascotRecord
//		MascotRecord record = parser.parse();
//		
//		// File name test
//		assertEquals("Spot24.xml", record.getXmlFilename());
//		
//		// Number of queries
//		assertEquals(1001, record.getNumQueries());
//		
//		// MGF file 
//		assertEquals("23925373020409089.mgf", record.getInputFilename());
//	}
//	
//	@Test
//	public void testParse2(){
//		
//		File bande1XML = new File(getClass().getClassLoader().getResource("ESI/xml/Bande1.xml").getPath());
//		
//		MascotXMLParser parser = new MascotXMLParser(bande1XML);
//		
//		// MascotRecord 
//		MascotRecord record = parser.parse();
//		
//		// File name test
//		assertEquals("Bande1.xml", record.getXmlFilename());
//		
//		// Number of queries
//		System.out.println(record.getNumQueries());
//		
//		// Peptide map
//		System.out.println(record.getPeptides());
//		
//	}
	
}

