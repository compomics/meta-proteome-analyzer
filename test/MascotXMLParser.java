
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
					try {
						proteinHit.setScore(Double.parseDouble(getTagValue("prot_score", hitElement)));
					} 
					catch (Exception e) {
						e.printStackTrace();
						System.out.println("ProteinScore bei Hit:"+ i+ "nicht gefunden");
					}
					// ProteinMass
					try {
						proteinHit.setMass(Double.parseDouble(getTagValue("prot_mass", hitElement)));
					} 
					catch (Exception e) {
						e.printStackTrace();
						System.out.println("ProteinMasse bei Hit:"+ i+ "nicht gefunden");
					}

					// Einlesen der Peptide
					List<PeptideHit> peptideHits = new ArrayList<PeptideHit>();	
					NodeList peptideList = hitElement.getElementsByTagName("peptide");
					for (int j = 0; j < peptideList.getLength(); j++) {
						PeptideHit peptideHit = new PeptideHit();
						// durchgehen aller Peptide in PeptideNodeList
						Node peptideNode = peptideList.item(j);		
						peptideHit.setDescription(peptideNode.getAttributes().item(0) +" "+ peptideNode.getAttributes().item(1)+" "+ peptideNode.getAttributes().item(2));
						Element peptideElement = (Element) peptideNode;
						// Rest abfragen
						if (peptideNode.getNodeType() == Node.ELEMENT_NODE) {					
							//Peptide_seq
							peptideHit.setSequence(getTagValue("pep_seq", peptideElement));
							// Peptide_ MZ
							try {
								peptideHit.setMz(Double.parseDouble(getTagValue("pep_exp_mz", peptideElement)));
							} 
							catch (Exception e) {
								e.printStackTrace();
								System.out.println("Peptide MZ bei Hit:"+ i+ "und Peptide "+j+"nicht gefunden");
							}
							// Peptide_Ladung
							try {
								peptideHit.setCharge(Integer.parseInt(getTagValue("pep_exp_z", peptideElement)));
							} 
							catch (Exception e) {
								e.printStackTrace();
								System.out.println("Peptide Charge bei Hit:"+ i+ "und Peptide "+j+"nicht gefunden");
							}
						}
						//Hinzufügen des peptideHit zur Liste der peptideHis
						peptideHits.add(peptideHit);		
					}
					//Hinzufügen der Liste peptideHits zu proteinHit
					proteinHit.setPeptideHits(peptideHits);		
				}
				proteinHits.add(proteinHit);
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
}
