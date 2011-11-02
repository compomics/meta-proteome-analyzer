package de.mpa.parser.mascot.xml;
/* Name:				RobbiesDomParser
 * Last changed:		02.11.2011
 * Author:				Robbie
 * Description:			class to parse mascot xml files (using DOM parsing)
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
	
	private File xmlFile;
	private boolean verbose = true;
	
	// constants
	public static final boolean SUPPRESS_WARNINGS = false;
	
	public MascotXMLParser(File xmlFile) {
		this.xmlFile = xmlFile;
	}
	
	public MascotXMLParser(File xmlFile, boolean verbose) {
		this.xmlFile = xmlFile;
		this.verbose = verbose;
	}

	/**
	 * Parses the mascot XML file.
	 * @param mascotXML
	 * @return
	 */
	public MascotRecord parse(){
		// create object to store contents
		MascotRecord mascotRecord = new MascotRecord();
		try {
			// parse xml document					
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();					
			// set xml name
			mascotRecord.setXmlFilename(xmlFile.getName());
			
			// header starts here
			NodeList nHeaderList = doc.getElementsByTagName("header");		
			for (int temp = 0; temp < nHeaderList.getLength(); temp++) {
				Node nNode = nHeaderList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					mascotRecord.setURI(getTagValue("URI", eElement));							
					mascotRecord.setMascotFilename(getTagValue("FILENAME", eElement));
					
					mascotRecord.setNumQueries(Integer.parseInt(getTagValue("NumQueries", eElement)));
				}
			}
			// generate list of protein hits		
			List<Proteinhit> proteinHits = new ArrayList<Proteinhit>();
			// grab all <hit> nodes
			NodeList hitList = doc.getElementsByTagName("hit");
			// iterate over hitList
			for (int i = 0; i < hitList.getLength(); i++) {
				Proteinhit proteinHit = new Proteinhit();
				Node hitNode = hitList.item(i);
				
				int num = Integer.parseInt(hitNode.getAttributes().item(0).getNodeValue());
				proteinHit.setNumber(num);
				
				// check for element node
				if (hitNode.getNodeType() == Node.ELEMENT_NODE) {					
					Element hitElement = (Element) hitNode;				
					// get protein accession
					proteinHit.setAccession(hitElement.getElementsByTagName("protein").item(0).getAttributes().item(0).getNodeValue());
					// get protein description
					try {
						proteinHit.setDescription(getTagValue("prot_desc", hitElement));
					} catch (Exception e) {
						if (verbose) {
							System.out.println("WARNING: no prot_desc found at protein hit " + num + 
									" in file " + xmlFile.getName());
						}
					}					
					// get protein score
					try {
						proteinHit.setScore(Double.parseDouble(getTagValue("prot_score", hitElement)));
					} 
					catch (Exception e) {
						if (verbose) {
							System.out.println("WARNING: no prot_score found at protein hit " + num + 
								" in file " + xmlFile.getName());
						}
					}
					// get protein mass
					try {
						proteinHit.setMass(Double.parseDouble(getTagValue("prot_mass", hitElement)));
					} 
					catch (Exception e) {
						if (verbose) {
							System.out.println("WARNING: no prot_mass found at protein hit " + num + 
								" in file " + xmlFile.getName());
						}
					}

					// grab peptide hits
					List<PeptideHit> peptideHits = new ArrayList<PeptideHit>();	
					NodeList peptideList = hitElement.getElementsByTagName("peptide");
					// iterate over peptides in peptideList
					for (int j = 0; j < peptideList.getLength(); j++) {
						PeptideHit peptideHit = new PeptideHit(proteinHit.getAccession());
						Node peptideNode = peptideList.item(j);		
						peptideHit.setDescription(peptideNode.getAttributes().item(0) +" "+ peptideNode.getAttributes().item(1)+" "+ peptideNode.getAttributes().item(2));
						Element peptideElement = (Element) peptideNode;
						// query rest
						if (peptideNode.getNodeType() == Node.ELEMENT_NODE) {					
							
							// get peptide title
							try {
								peptideHit.setScanTitle(getTagValue("pep_scan_title", peptideElement));
							} catch (Exception e) {
								if (verbose) {
									System.out.println("WARNING: no pep_scan_title found at protein hit " + num +
										", peptide " + (j+1) + " in file " + xmlFile.getName());
								}
							}
																					
							// get peptide sequence
							try {
							peptideHit.setSequence(getTagValue("pep_seq", peptideElement));
							} catch (Exception e) {
								if (verbose) {
									System.out.println("WARNING: no pep_seq found at protein hit " + num +
										", peptide " + (j+1) + " in file " + xmlFile.getName());
								}
							}
							// get peptide mass
							try {
								peptideHit.setMz(Double.parseDouble(getTagValue("pep_exp_mz", peptideElement)));
							} 
							catch (Exception e) {
								if (verbose) {
									System.out.println("WARNING: no pep_exp_mz found at protein hit " + num +
										", peptide " + (j+1) + " in file " + xmlFile.getName());
								}
							}
							// get peptide charge
							try {
								peptideHit.setCharge(Integer.parseInt(getTagValue("pep_exp_z", peptideElement)));
							} 
							catch (Exception e) {
								if (verbose) {
									System.out.println("WARNING: no pep_exp_z found at protein hit " + num +
										", peptide " + (j+1) + " in file " + xmlFile.getName());
								}
							}
						}
						// add peptideHit to list of hits
						peptideHits.add(peptideHit);	
						mascotRecord.addEntry(peptideHit.getScanTitle(), peptideHit);
					}
					// add list of hits to proteinHit
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
