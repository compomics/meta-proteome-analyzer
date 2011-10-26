/* Name:				RobbiesDomParser
 * letzte Änderung:		25.10.2011
 * Author:				Robbie
 * Beschreibung:		Main zum einlesen eines XML Files beruhend auf dem DomParser
 */
//import******************************************************************************
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class parses the Mascot XML result file and generates a MascotRecord object.
 * @author heyer
 *
 */
public class MascotXMLParser {

	/**
	 * Parses the mascot XML file.
	 * @param mascotXML
	 * @return
	 */
	public MascotRecord parse(File mascotXML){
		//Anfang*******************************************************************************		

		// Klasse in der alles gespeichert wird
		MascotRecord mascotRecord = new MascotRecord();

		try {
			// Teil der Dokument einliest					
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(mascotXML);
			doc.getDocumentElement().normalize();					

			//Name XML	
			mascotRecord.setXmlFilename(mascotXML.getName());					

			//  Header begins here
			NodeList nHeaderList = doc.getElementsByTagName("header");		
			for (int temp = 0; temp < nHeaderList.getLength(); temp++) {
				Node nNode = nHeaderList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					mascotRecord.setUri(getTagValue("URI", eElement));							
					mascotRecord.setMascotFilename(getTagValue("FILENAME", eElement));
				}
			}

			//ProteinHits			
			List<Proteinhit> proteinHits = new ArrayList<Proteinhit>();
			//Ebene "hit"
			NodeList hitList = doc.getElementsByTagName("hit");
			// Iterate the hitList
			for (int i = 0; i < hitList.getLength(); i++) {
				Proteinhit proteinHit = new Proteinhit();
				Node hitNode = hitList.item(i);								
				proteinHit.setNumber(Integer.parseInt(hitNode.getAttributes().item(0).getNodeValue()));

				// Check for element node
				if (hitNode.getNodeType() == Node.ELEMENT_NODE) {					
					Element hitElement = (Element) hitNode;				

					// Get protein accession
					proteinHit.setAccession(hitElement.getElementsByTagName("protein").item(0).getAttributes().item(0).getNodeValue());

					//Protein_desc
					proteinHit.setDescription(getTagValue("prot_desc", hitElement));

					// ProteinScore
					String helper; 
					helper = getTagValue("prot_score", hitElement);
					if (!helper.isEmpty()) {
						proteinHit.setScore(Double.parseDouble(helper));
						helper="";
					}
					// ProteinMass
					helper = getTagValue("prot_mass", hitElement);
					if (!helper.isEmpty()) {
						proteinHit.setMass(Double.parseDouble(helper));
						helper="";


						// Einlesen der Peptide
						List<PeptideHit> peptideHits = new ArrayList<PeptideHit>();	
						NodeList peptideList = hitElement.getElementsByTagName("peptide");
						System.out.println("Anzahl der Peptide zu Node"+ peptideList.getLength());
						for (int j = 0; j < peptideList.getLength(); j++) {
							PeptideHit peptideHit = new PeptideHit();
							// durchgehen aller Peptide in PeptideNodeList
							Node peptideNode = peptideList.item(i);		
							
							System.out.println(peptideNode.getAttributes().item(0) +" "+ peptideNode.getAttributes().item(1));
							
							Element peptideElement = (Element) peptideNode;
							
							System.out.println(getTagValue("pep_seq", peptideElement));
							//System.out.println(peptideNode.getAttributes().item(0) +" "+ peptideNode.getAttributes().item(1));
							// Rest abfragen
						//if (peptideNode.getNodeType() == Node.ELEMENT_NODE) {					
							//Element peptideElement = (Element) hitNode;	
//
//								//Peptide_seq
								//peptideHit.setSequence(getTagValue("pep_seq", peptideElement));
//
//								// Peptide_ MZ
//								helper = getTagValue("pep_exp_mz", peptideElement);
//								if (!helper.isEmpty()) {
//									peptideHit.setPeptideMz(Double.parseDouble(helper));
//									helper="";
//								}
//
//								// Peptide_Ladung
//								helper = getTagValue("pep_exp_z", peptideElement);
//								if (!helper.isEmpty()) {
//									peptideHit.setLadung(Integer.parseInt(helper));
//									helper="";
//								}
							//}
//							//Hinzufügen des peptideHit zur Liste der peptideHis
//							peptideHits.add(peptideHit);		


						}
						//Hinzufügen der Liste peptideHits zu proteinHit
						proteinHit.setPeptideHits(peptideHits);		
					}
					proteinHits.add(proteinHit);
				}

			}
			mascotRecord.setProteinHits(proteinHits);


		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		return mascotRecord;
	}


	/**
	 * Returns the tag value for a given element.
	 * @param sTag
	 * @param eElement
	 * @return the resultant string
	 */
	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MascotXMLParser parser = new MascotXMLParser();
		MascotRecord record = parser.parse(new File("C:\\Documents and Settings\\heyer\\workspace\\RobbiesXMLParser\\Spot24.xml"));

		List<Proteinhit> hits = record.getProteinHits();
//		for (Proteinhit hit : hits) {
//			System.out.println("_____________________________");
//			System.out.println("acc: " + hit.getAccession());
//			System.out.println("desc: " + hit.getDescription());
//			System.out.println("Mass: " + hit.getMass());
//			System.out.println("Score: " + hit.getScore());
//			List<PeptideHit> peptideHits = hit.getPeptideHits();
//			for (int i = 0; i < 2; i++){
//
//				System.out.println("Peptidename:");
//				System.out.println("Sequence:");
//				System.out.println("Ladung:");
//				System.out.println("MZ:");
//				//PeptideHit pepHit = peptideHits.get(i);
//
//			}
//		}
	}

}
