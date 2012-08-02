package de.mpa.parser.ec;
import java.awt.Component;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.chainsaw.Main;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * This file parse the xml of all EC numbers and creates a reduced file
 * @author heyer
 *
 */
public class ECParser2reduceXML {

	private static Component parent;
	private static File xmlFile;
	private static File outXmlFile;

	
	public static void main(String[] args) {
		parse();
		
	}		
	
	/**
	 * Method to parse the IntEnzXml file und to create a reduced xml
	 * @param filepath 
	 */
	public static void parse(){
		
		// File Chooser to open xml file with EC number
				JFileChooser chooserFcr = new JFileChooser();
				int returnVal = chooserFcr.showOpenDialog(parent);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " +
							chooserFcr.getSelectedFile().getName());
					xmlFile = chooserFcr.getSelectedFile();
				}
		
		try {
			System.out.println("start");
			// parse xml document					
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			// normalize text representation
			doc.getDocumentElement().normalize();	

			// Remove links
			String[] nodes2remove = new String[]{"cofactors","links", "references","systematic_name","systematic_name", "synonyms","reactions","history" };//"comments",
			//			String[] nodes2remove = new String[]{"links", "references", "synonyms","reactions","comments","history","accepted_name","systematic_name","cofactors"  };			

			for (String string : nodes2remove) {
				NodeList linksNodes = doc.getElementsByTagName(string);	
				int length = linksNodes.getLength();
				for (int i = 0; i < length; i++) {
					Node linksNode = linksNodes.item(0);
					linksNode.getParentNode().removeChild(linksNode);
					System.out.println("" + i + " of " + length);
				}
			}
			// Save as new XML
			try {
				// Prepare the DOM document for writing
				Source source = new DOMSource(doc);
				// Get the output file
				// File Chooser to open xml file with EC number
				JFileChooser chooserOutFcr = new JFileChooser("C:\\Documents and Settings\\heyer\\Desktop\\IntEnzXML\\ASCII\\");
				int returnVal2 = chooserOutFcr.showSaveDialog(parent);
				if(returnVal2 == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to save this file: " +
							chooserOutFcr.getSelectedFile().getName());
					outXmlFile = chooserOutFcr.getSelectedFile();
				}
				Result result = new StreamResult(outXmlFile + ".xml");

				// Write the DOM document to the file
				Transformer xformer = TransformerFactory.newInstance().newTransformer();
				xformer.transform(source, result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("finish");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
