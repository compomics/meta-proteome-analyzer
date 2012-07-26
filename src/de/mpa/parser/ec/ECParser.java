package de.mpa.parser.ec;
import java.awt.Component;
import java.io.File;
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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * This file parse the xml of all EC numbers and creates a reduced file
 * @author heyer
 *
 */
public class ECParser {

	private static Component parent;
	private static File xmlFile;
	private static File outXmlFile;

	public static void main(String [] args){
		// File Chooser to open xml file with EC number
		JFileChooser chooserFcr = new JFileChooser("C:\\Documents and Settings\\heyer\\Desktop\\IntEnzXML\\ASCII\\");
		int returnVal = chooserFcr.showOpenDialog(parent);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: " +
					chooserFcr.getSelectedFile().getName());
			xmlFile = chooserFcr.getSelectedFile();
		}

		// Parser for EC-.xml 
		// Creates new thread
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				parse();
			}
		});
	}

	/**
	 * Method to parse the xml
	 */
	public static  void parse(){
		try {
			// parse xml document					
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			// normalize text representation
			doc.getDocumentElement().normalize();	

			// Remove links
			String[] nodes2remove = new String[]{"links", "references", "synonyms","reactions","comments","history" };
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
				int returnVal = chooserOutFcr.showSaveDialog(parent);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to save this file: " +
							chooserOutFcr.getSelectedFile().getName());
					outXmlFile = chooserOutFcr.getSelectedFile();
				}
				Result result = new StreamResult(outXmlFile);

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
