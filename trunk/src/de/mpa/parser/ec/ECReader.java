package de.mpa.parser.ec;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 
 * @author heyer
 *
 */
public class ECReader {

	/**
	 * Maps E.C. numbers to their descriptions read from a specified flat file stream. 
	 * 
	 * @param is the file input stream of the description flat file
	 * @return Map containing E.C.-to-Description pairs
	 */
	public static Map<String, ECEntry> readEC(InputStream is) {

		// File of reduced xml File
		// File xmlFile = new File(filePath);

		// ec Entries
		ECEntry ecEntry = new ECEntry();

		// Map with EC number as key and name of EC-number
		Map<String, ECEntry> ecMap = new TreeMap<String, ECEntry>();

		// EC-numbers
		String ecNumberLevel1;
		String ecNumberLevel2;
		String ecNumberLevel3;
		String ecNumberLevel4;

		// EC: name
		String ecNameLevel1 = null;
		String ecNameLevel2 = null;
		String ecNameLevel3 = null;
		String ecNameLevel4 = null;

		// EC: description
		String ecDescLevel1 = null;
		String ecDescLevel2 = null;
		String ecDescLevel3 = null;// empty in xml
		String ecDescLevel4 = null;// empty in xml

		// parse xml document
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			// System.out.println(xmlFile);
			Document doc = dBuilder.parse(new InputSource(is));
			// normalize text representation
			doc.getDocumentElement().normalize();

			// Level 1 nodes
			NodeList level1NodeList = doc.getElementsByTagName("ec_class");
			for (int i = 0; i < level1NodeList.getLength(); i++) {
				if (level1NodeList.item(i).getNodeName() == "ec_class") {

					// Get EC-entries
					Node level1Node = level1NodeList.item(i);
					ecNumberLevel1 = level1Node.getAttributes().item(0)
							.getTextContent();

					if (level1Node.getChildNodes().item(1).getNodeName() == "name") {
						ecNameLevel1 = level1Node.getChildNodes().item(1)
								.getTextContent();
					}
					if (level1Node.getChildNodes().item(3).getNodeName() == "description") {
						ecDescLevel1 = level1Node.getChildNodes().item(3)
								.getTextContent();
					}
					// Fill maps
					ecEntry = new ECEntry();
					ecEntry.setNumber(ecNumberLevel1 + ".-.-.-");
					ecEntry.setName(ecNameLevel1);
					ecEntry.setDescription(ecDescLevel1);
					ecMap.put(ecNumberLevel1 + ".-.-.-", ecEntry);

					// Level 2 nodes
					NodeList level2NodeList = level1Node.getChildNodes();
					for (int j = 0; j < level2NodeList.getLength(); j++) {
						// First entries are name and description
						if (level2NodeList.item(j).getNodeName() == "ec_subclass") {
							Node level2Node = level2NodeList.item(j);

							// Get EC: entries
							ecNumberLevel2 = ecNumberLevel1
									+ "."
									+ level2Node.getAttributes().item(0)
											.getTextContent();
							if (level2Node.getChildNodes().item(1)
									.getNodeName() == "name") {
								ecNameLevel2 = level2Node.getChildNodes().item(
										1).getTextContent();
							}
							if (level2Node.getChildNodes().item(3)
									.getNodeName() == "description") {
								ecDescLevel2 = level2Node.getChildNodes().item(
										3).getTextContent();
							}

							// Fill maps
							ecEntry = new ECEntry();
							ecEntry.setNumber(ecNumberLevel2 + ".-.-");
							ecEntry.setName(ecNameLevel2);
							ecEntry.setDescription(ecDescLevel2);
							ecMap.put(ecNumberLevel2 + ".-.-", ecEntry);

							// Level 3 nodes
							NodeList level3NodeList = level2Node
									.getChildNodes();
							for (int k = 0; k < level3NodeList.getLength(); k++) { // First
																					// entries
																					// are
																					// name
																					// and
																					// description
								if (level3NodeList.item(k).getNodeName() == "ec_sub-subclass") {
									Node level3Node = level3NodeList.item(k);

									// Get EC-entries
									ecNumberLevel3 = ecNumberLevel2
											+ "."
											+ level3Node.getAttributes()
													.item(0).getTextContent();
									if (level3Node.getChildNodes().item(1)
											.getNodeName() == "name") {
										ecNameLevel3 = level3Node
												.getChildNodes().item(1)
												.getTextContent();
									}

									// Fill maps
									ecEntry = new ECEntry();
									ecEntry.setNumber(ecNumberLevel3 + ".-");
									ecEntry.setName(ecNameLevel3);
									ecEntry.setDescription(ecDescLevel3);
									ecMap.put(ecNumberLevel3 + ".-", ecEntry);

									// Level 4 nodes
									NodeList level4NodeList = level3Node
											.getChildNodes();
									for (int m = 0; m < level4NodeList
											.getLength(); m++) {
										if (level4NodeList.item(m)
												.getNodeName() == "enzyme") {

											// Get EC-entries
											Node level4Node = level4NodeList
													.item(m);
											ecNumberLevel4 = ecNumberLevel3
													+ "."
													+ level4Node
															.getAttributes()
															.item(0)
															.getTextContent();
											// Control EC number ( changed by
											// transfers or deletes
											if (ecNumberLevel4 != level4Node
													.getChildNodes().item(1)
													.getTextContent()
													.substring(3)) {
												// Delete EC_ at the beginnnig
												ecNumberLevel4 = level4Node
														.getChildNodes()
														.item(1)
														.getTextContent()
														.substring(3);
											}

											if (level4Node.getChildNodes()
													.item(3).getNodeName() == "accepted_name") {
												ecNameLevel4 = level4Node
														.getChildNodes()
														.item(3)
														.getTextContent();
											} else {
												ecNameLevel4 = level4Node
														.getChildNodes()
														.item(3).getNodeName()
														+ " "
														+ level4Node
																.getChildNodes()
																.item(3)
																.getFirstChild()
																.getNextSibling()
																.getTextContent();
											}

											try {	// Try because sometimes no comments
												if (level4Node.getChildNodes()
														.item(5).getNodeName() == "comments") {
													ecDescLevel4 = level4Node
															.getChildNodes()
															.item(5)
															.getChildNodes()
															.item(1)
															.getTextContent();
												}
											} catch (Exception e) {
											}

											// Fill maps
											ecEntry = new ECEntry();
											ecEntry.setNumber(ecNumberLevel4);
											ecEntry.setName(ecNameLevel4);
											ecEntry.setDescription(ecDescLevel4);

											ecMap.put(ecNumberLevel4, ecEntry);
										}
									}
								}
							}
						}
					}
				}
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ecMap;
	}
}
