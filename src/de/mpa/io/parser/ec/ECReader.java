package de.mpa.io.parser.ec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.mpa.client.Constants;
import de.mpa.main.Starter;

/**
 * 
 * 
 * @author A. Behne
 */
public class ECReader {

	/**
	 * Maps E.C. numbers to their descriptions read from the specified IntEnz XML file stream. 
	 * @param is the file input stream of the IntEnz XML file
	 * @return map containing E.C. number-to-E.C. entry object pairs
	 */
	public static Map<String, ECEntry> readXML(InputStream is) {

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
	
	/**
	 * Reads the enzyme classes stored in the file at the specified path
	 * location into a tree structure.
	 * @param path the path to the ENZYME classes definition file
	 * @return the root node of the enzyme classes tree
	 */
	public static ECNode readEnzymeClasses() {
		ECNode root = new ECNode("root");
		ECNode parent = root;
		ECNode child = null;
		String path = null;
		Reader in;
		
		path = "" + Constants.CONFIGURATION_PATH_JAR + Constants.SEP + "enzclass.txt";
		
		
		try {
			in = new FileReader(new File(path));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
		
					
		try (BufferedReader br = new BufferedReader(in)) {
			int currentDepth = 0;
			String line;
			while ((line = br.readLine()) != null) {
				// check whether line starts with number
				if (line.matches("^[0-9].*")) {
					// create new child node
					ECNode newChild = root.createNode(line);
					if (newChild != null) {
						// determine depth
						int depth = 3 - StringUtils.countMatches(newChild.getIdentifier(), "-");
						// check whether depth changed
						if (depth < currentDepth) {
							// move up in hierarchy
							int delta = currentDepth - depth;
							for (int i = 0; i < delta; i++) {
								parent = (ECNode) parent.getParent();
							}
						} else if (depth > currentDepth) {
							// last created node becomes new parent
							parent = child;
						}
						currentDepth = depth;
						// insert child into tree hierarchy
						parent.add(newChild);
						child = newChild;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return root;
	}

	/**
	 * Reads the enzyme data stored in the definition file stored at the
	 * specified path location and inserts them in the tree structure below the
	 * specified root node.
	 * @param root the root node of the enzyme classes tree
	 * @param path the path to the ENZYME definition file
	 */
	public static void readEnzymes(ECNode root) {
		Reader in;
		String path = null;
		
		path = "" + Constants.CONFIGURATION_PATH_JAR + Constants.SEP + "enzyme.dat";
		
		try {
			in = new FileReader(new File(path));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return;
		}

		try (BufferedReader br = new BufferedReader(in)) {
			String id = null;
			String desc = null;
			String comments = null;
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() > 5) {
					// extract line value
					String value = line.substring(5);
					// check whether line starts with specific key tokens
					if (line.startsWith("ID")) {
						// insert node using previously stored data, if it exists
						insertNode(root, new ECNode(id, desc, comments));
						
						// assign identifier
						id = value;
						// reset comments string
						comments = ""; 
					} else if (line.startsWith("DE")) {
						// assign description
						desc = value;
					} else if (line.startsWith("CC")) {	// comments token
						if (value.startsWith("-!-")) {
							if (!comments.isEmpty()) {
								comments += "<p>";
							}
						}
						value = value.substring(4);
						comments += value + " ";
					}
				}
			}
			// insert final node
			insertNode(root, new ECNode(id, desc, comments));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convenience method to insert the specified child node into the tree
	 * structure rooted at the specified root node.
	 * @param root the root node
	 * @param newChild the child to insert
	 */
	private static void insertNode(ECNode root, ECNode newChild) {
		String id = newChild.getIdentifier();
		if (id != null) {
			// identify parent node
			ECNode parent = ECReader.getNode(root, id);
			if (parent != null) {
				// insert child node into tree structure
				parent.add(newChild);
			}
		}
	}

	/**
	 * 
	 * @param parent
	 * @param identifier
	 * @return
	 */
	private static ECNode getNode(ECNode parent, String identifier) {
		short[] ecNums = toArray(identifier);
		int depth = parent.getLevel() + 1;
		for (int i = depth; i < 5; i++) {
			// set out-of-depth levels to zero
			short[] ecTmp = Arrays.copyOf(Arrays.copyOf(ecNums, i), 4);
			String idTmp = toString(ecTmp);
			Enumeration<ECNode> children = parent.children();
			while (children.hasMoreElements()) {
				ECNode child = children.nextElement();
				if (child.getIdentifier().equals(idTmp)) {
					parent = child;
					break;
				}
			}
		}
		return parent;
	}
	
//	/**
//	 * Returns the enzyme node with the specified identifier below the specified
//	 * parent node.
//	 * @param parent the parent node
//	 * @param identifier the identifier
//	 * @return the path of tree nodes from the root to the desired node or
//	 *  <code>null</code> if no such path exists
//	 */
//	public static ECNode getNode(ECNode parent, String identifier) {
//		if (identifier != null) {
//			Enumeration<ECNode> dfe = parent.depthFirstEnumeration();
//			while (dfe.hasMoreElements()) {
//				ECNode node = dfe.nextElement();
//				if (identifier.equals(node.getIdentifier())) {
//					return node;
//				}
//			}
//		}
//		return null;
//	}
	
	/**
	 * Returns the path of enzyme nodes from the root of the enzyme tree to the
	 * node with the specified identifier string below the specified parent
	 * node.
	 * 
	 * @param parent the parent node
	 * @param identifier the identifier
	 * @return the path of tree nodes from the root to the desired node or
	 *  <code>null</code> if no such path exists
	 */
	public static ECNode[] getPath(ECNode parent, String identifier) {
		ECNode node = getNode(parent, identifier);
		if (node != null) {
			TreeNode[] path = node.getPath();
			ECNode[] res = new ECNode[path.length];
			System.arraycopy(path, 0, res, 0, res.length);
			return res;
		}
		return null;
	}
	
	/**
	 * Converts an E.C. number string of the format <code>#.#.#.#</code> into an
	 * array of shorts.
	 * @param ecString the E.C. number string
	 * @return an array containing numerical representations of the E.C. number
	 *  levels or 0 if the string contains unparseable sections
	 */
	public static short[] toArray(String ecString) {
		short[] ec = new short[4];
		String[] ecTokens = ecString.split("[.]");
		for (int i = 0; i < ec.length; i++) {
			try {
				ec[i] = Short.parseShort(ecTokens[i]);
			} catch (Exception e) {
				ec[i] = 0;
			}
		}
		return ec;
	}
	
	/**
	 * Converts an array of E.C. numbers to a string representation in the
	 * format <code>#.#.#.#</code>.
	 * @param ec the E.C. number array
	 * @return an E.C. number string
	 */
	public static String toString(short[] ec) {
		if (ec.length != 4) {
			return null;
		}
		String ecString = "" + ec[0];
		for (int i = 1; i < 4; i++) {
			short num = ec[i];
			ecString += "." + ((num == 0) ? "-" : num);
		}
		return ecString;
	}
	
}