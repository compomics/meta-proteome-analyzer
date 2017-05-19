package de.mpa.io.parser.mascot.xml;

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
 * 
 * @author R. Heyer
 */
public class MascotXMLParser {
	
	private final File xmlFile;
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
			Document doc = dBuilder.parse(this.xmlFile);
			doc.getDocumentElement().normalize();					
			// set xml name
			mascotRecord.setXmlFilename(this.xmlFile.getName());
			
			// header starts here
			NodeList nHeaderList = doc.getElementsByTagName("header");		
			for (int temp = 0; temp < nHeaderList.getLength(); temp++) {
				Node nNode = nHeaderList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					mascotRecord.setURI(this.getTagValue("URI", eElement));
					mascotRecord.setInputFilename(this.getTagValue("FILENAME", eElement));
					mascotRecord.setNumQueries(Integer.parseInt(this.getTagValue("NumQueries", eElement)));
				}
			}
			
			// gather global list of modifications
			List<MascotModification> globalMods = new ArrayList<MascotModification>();
			NodeList varMods = doc.getElementsByTagName("variable_mods");
			if (varMods.getLength() > 0) {
				varMods = ((Element) varMods.item(0)).getElementsByTagName("modification");
				for (int i = 0; i < varMods.getLength(); i++) {
					Node varMod = varMods.item(i);
					globalMods.add(new MascotModification(varMod, false));
				}
			}
			
			// generate list of protein hits		
			List<MascotProteinHit> proteinHits = new ArrayList<MascotProteinHit>();
			// grab all <hit> nodes
			NodeList hitList = doc.getElementsByTagName("hit");
			// iterate over hitList
			for (int i = 0; i < hitList.getLength(); i++) {
				MascotProteinHit proteinHit = new MascotProteinHit();
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
							descriptions.add(this.getTagValue("prot_desc", protElement));
						} catch (Exception e) {
							if (this.verbose) {
								System.err.println("WARNING: no prot_desc found at hit " + num + 
										" in protein " + (j+1) + " in file " + this.xmlFile.getName());
							}
							descriptions.add(null);
						}
						// get protein score
						try {
							scores.add(Double.parseDouble(this.getTagValue("prot_score", protElement)));
						} catch (Exception e) {
							if (this.verbose) {
								System.err.println("WARNING: no prot_score found at hit " + num + 
										" in protein " + (j+1) + " in file " + this.xmlFile.getName());
							}
							scores.add(null);
						}
						// get protein mass
						try {
							masses.add(Double.parseDouble(this.getTagValue("prot_mass", protElement)));
						} catch (Exception e) {
							if (this.verbose) {
								System.err.println("WARNING: no prot_mass found at hit " + num + 
										" in protein " + (j+1) + " in file " + this.xmlFile.getName());
							}
							masses.add(null);
						}
					}
					
					// store protein attributes
					proteinHit.setAccessions(accessions);
					proteinHit.setDescriptions(descriptions);
					proteinHit.setScores(scores);
					proteinHit.setMasses(masses);
					
					// grab peptide hits
					List<MascotPeptideHit> peptideHits = new ArrayList<MascotPeptideHit>();
					// peptide list is always the same for every protein child node in a hit,
					// therefore simply grab from first child
					NodeList peptideList = ((Element) protList.item(0)).getElementsByTagName("peptide");
					// iterate over peptides in peptideList
					for (int j = 0; j < peptideList.getLength(); j++) {
						MascotPeptideHit peptideHit = new MascotPeptideHit(proteinHit);
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
							
							// get peptide mass
							try {
								peptideHit.setMz(Double.parseDouble(this.getTagValue("pep_exp_mz", peptideElement)));
							} 
							catch (Exception e) {
								if (this.verbose) {
									System.err.println("WARNING: no pep_exp_mz found at protein hit " + num +
											", peptide " + (j+1) + " in file " + this.xmlFile.getName());
								}
							}

							// get peptide charge
							try {
								peptideHit.setCharge(Integer.parseInt(this.getTagValue("pep_exp_z", peptideElement)));
							} 
							catch (Exception e) {
								if (this.verbose) {
									System.err.println("WARNING: no pep_exp_z found at protein hit " + num +
											", peptide " + (j+1) + " in file " + this.xmlFile.getName());
								}
							}

							// get peptide sequence
							try {
								peptideHit.setSequence(this.getTagValue("pep_seq", peptideElement));
							} catch (Exception e) {
								if (this.verbose) {
									System.err.println("WARNING: no pep_seq found at protein hit " + num +
											", peptide " + (j+1) + " in file " + this.xmlFile.getName());
								}
							}

							// get variable peptide modifications
							try {
								Map<Integer, MascotModification> pepMods = new HashMap<Integer, MascotModification>();
								NodeList nodes = peptideElement.getElementsByTagName("pep_var_mod_pos");
								if (nodes.getLength() > 0) {
									nodes = nodes.item(0).getChildNodes();
									if (nodes.getLength() > 0) {
										// split residue index notation,
										// first item corresponds to C-terminal mods
										// middle item corresponds to residual mods
										// last item corresponds to N-terminal mods
										String[] indexes = nodes.item(0).getNodeValue().split("\\.");
										for (int k = 0; k < indexes[1].length(); k++) {
											char index = indexes[1].charAt(k);
											if (index != '0') {
												pepMods.put(k, globalMods.get(Integer.parseInt(String.valueOf(index)) - 1));
											}
										}
									}
								}
								peptideHit.setModifications(pepMods);
							} catch (Exception e) {
								if (this.verbose) {
									System.err.println("WARNING: no pep_var_mod found at protein hit " + num +
											", peptide " + (j+1) + " in file " + this.xmlFile.getName());
								}
							}
							
							// TODO: parse fixed peptide modifications

							// get peptide scan title
							try {
								// prune random number (introduced by Mascot) from end of string
								String scanTitle = this.getTagValue("pep_scan_title", peptideElement);
								int lastBracket = scanTitle.lastIndexOf("(");
								if (lastBracket != -1) {	// title contains left bracket
									try {
										String bracketTerm = scanTitle.substring(lastBracket+1, scanTitle.length()-1);
										if (bracketTerm.contains("=")) {
											bracketTerm = bracketTerm.substring(bracketTerm.indexOf("=") + 1);
										}
										// provoke exception
										long prn = Long.parseLong(bracketTerm);
										// no exception thrown, therefore check whether the parsed long is very large
										// (which is indicative of Mascot's added random number term)
										if (prn >= 31122099235959L) {
											// prune substring
											scanTitle = scanTitle.substring(0, lastBracket);
										}
									} catch (Exception e) {
										// do nothing
									}
								}
								// remove leading/trailing whitespaces
								scanTitle = scanTitle.trim();

								peptideHit.setScanTitle(scanTitle);
							} catch (Exception e) {
								if (this.verbose) {
									System.err.println("WARNING: no pep_scan_title found at protein hit " + num +
											", peptide " + (j+1) + " in file " + this.xmlFile.getName());
								}
							}
						}
						// add peptideHit to list of hits
						peptideHits.add(peptideHit);
						mascotRecord.addPeptide(peptideHit.getScanTitle(), peptideHit);
					}
					// add list of hits to proteinHit
					proteinHit.setPeptides(peptideHits);
				}
				proteinHits.add(proteinHit);
			}
			mascotRecord.setProteins(proteinHits);
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
		Node nValue = nlList.item(0);

		return nValue.getNodeValue();
	}
}
