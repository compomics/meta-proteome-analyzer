package de.mpa.parser.mascot.xml;
/* Name:				RobbiesDomParser
 * Last changed:		02.11.2011
 * Author:				Robbie
 * Description:			class to parse mascot xml files (using DOM parsing)
 */
//import******************************************************************************
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
			List<ProteinHit> proteinHits = new ArrayList<ProteinHit>();
			// grab all <hit> nodes
			NodeList hitList = doc.getElementsByTagName("hit");
			// iterate over hitList
			for (int i = 0; i < hitList.getLength(); i++) {
				ProteinHit proteinHit = new ProteinHit();
				Node hitNode = hitList.item(i);
				
				int num = Integer.parseInt(hitNode.getAttributes().item(0).getNodeValue());
				proteinHit.setHitNumber(num);
				
				// check for element node
				if (hitNode.getNodeType() == Node.ELEMENT_NODE) {					
					Element hitElement = (Element) hitNode;	

					ArrayList<String> accessions = new ArrayList<String>();
					ArrayList<String> descriptions = new ArrayList<String>();
					ArrayList<Double> scores = new ArrayList<Double>();
					ArrayList<Double> masses = new ArrayList<Double>();
					
					// gather protein attributes
					NodeList protList = hitElement.getElementsByTagName("protein");
					for (int j = 0; j < protList.getLength(); j++) {
						Node protNode = protList.item(j);
						Element protElement = (Element) protNode;
						accessions.add(protNode.getAttributes().item(0).getNodeValue());
						// get protein accession
						try {
							descriptions.add(getTagValue("prot_desc", protElement));
						} catch (Exception e) {
							if (verbose) {
								System.out.println("WARNING: no prot_desc found at hit " + num + 
										" in protein " + (j+1) + " in file " + xmlFile.getName());
							}
							descriptions.add(null);
						}
						// get protein score
						try {
							scores.add(Double.parseDouble(getTagValue("prot_score", protElement)));
						} catch (Exception e) {
							if (verbose) {
								System.out.println("WARNING: no prot_score found at hit " + num + 
										" in protein " + (j+1) + " in file " + xmlFile.getName());
							}
							scores.add(null);
						}
						// get protein mass
						try {
							masses.add(Double.parseDouble(getTagValue("prot_mass", protElement)));
						} catch (Exception e) {
							if (verbose) {
								System.out.println("WARNING: no prot_mass found at hit " + num + 
										" in protein " + (j+1) + " in file " + xmlFile.getName());
							}
							masses.add(null);
						}
					}
					
					// store protein attributes
					proteinHit.setAccessions(accessions);
					proteinHit.setDescriptions(descriptions);
					proteinHit.setScores(scores);
					proteinHit.setMasses(masses);
					
//					// get protein accession
//					proteinHit.setAccessions(hitElement.getElementsByTagName("protein").item(0).getAttributes().item(0).getNodeValue());
//					// get protein description
//					try {
//						proteinHit.setDescriptions(getTagValue("prot_desc", hitElement));
//					} catch (Exception e) {
//						if (verbose) {
//							System.out.println("WARNING: no prot_desc found at protein hit " + num + 
//									" in file " + xmlFile.getName());
//						}
//					}					
//					// get protein score
//					try {
//						proteinHit.setScores(Double.parseDouble(getTagValue("prot_score", hitElement)));
//					} 
//					catch (Exception e) {
//						if (verbose) {
//							System.out.println("WARNING: no prot_score found at protein hit " + num + 
//								" in file " + xmlFile.getName());
//						}
//					}
//					// get protein mass
//					try {
//						proteinHit.setMasses(Double.parseDouble(getTagValue("prot_mass", hitElement)));
//					} 
//					catch (Exception e) {
//						if (verbose) {
//							System.out.println("WARNING: no prot_mass found at protein hit " + num + 
//								" in file " + xmlFile.getName());
//						}
//					}

					// grab peptide hits
					List<PeptideHit> peptideHits = new ArrayList<PeptideHit>();
					// peptide list is always the same for every protein child node in a hit,
					// therefore simply grab from first child
					NodeList peptideList = ((Element) protList.item(0)).getElementsByTagName("peptide");
					// iterate over peptides in peptideList
					for (int j = 0; j < peptideList.getLength(); j++) {
						PeptideHit peptideHit = new PeptideHit(proteinHit);
						
						Node peptideNode = peptideList.item(j);
						
						NamedNodeMap pepAttributes = peptideNode.getAttributes();
						Map<String, String> attributes = new HashMap<String, String>(3);
						for (int k = 0; k < pepAttributes.getLength(); k++) {
							Node item = pepAttributes.item(k);
							attributes.put(item.getNodeName(), item.getNodeValue());
						}
						peptideHit.setAttributes(attributes);
						
						Element peptideElement = (Element) peptideNode;
						// query rest
						if (peptideNode.getNodeType() == Node.ELEMENT_NODE) {					
							
							// get peptide title
							try {
								// prune random number (introduced by Mascot) from end of string
								String scanTitle = getTagValue("pep_scan_title", peptideElement);
								int lastBracket = scanTitle.lastIndexOf("(");
								if (lastBracket != -1) {	// title contains left bracket
									try {
										// provoke exception
										long prn = Long.parseLong(scanTitle.substring(lastBracket+1, scanTitle.length()-1));
										// no exception thrown, therefore check whether the parsed long is very large
										// (which is indicative of Mascot's added random number term)
										if (prn >= 31122099235959L) {
											// prune substring
											scanTitle = scanTitle.substring(0, lastBracket-1);
											System.out.println(scanTitle);
										}
									} catch (Exception e) {
										// do nothing
									}
								}
								peptideHit.setScanTitle(scanTitle);
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
						mascotRecord.addPepMapEntry(peptideHit.getScanTitle(), peptideHit);
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
