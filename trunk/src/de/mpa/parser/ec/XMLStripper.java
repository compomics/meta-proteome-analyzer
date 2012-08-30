package de.mpa.parser.ec;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.compomics.util.io.filefilters.XmlFileFilter;

/**
 * Utility class to parse an XML file and strip off unwanted nodes resulting in
 * a reduced output file. This file parse the xml of all EC numbers and creates
 * a reduced file.<p>
 * Used on <a href="ftp://ftp.ebi.ac.uk/pub/databases/intenz/xml/">
 * IntEnz XML dumps</a>
 * 
 * @author R. Heyer, A. Behne
 */
public class XMLStripper {
	
	/**
	 * Parses the provided input XML file and removes the specified tags from it.
	 * 
	 * @param input The input file.
	 * @param tags2strip The array of tag names that are to be removed.
	 * @throws IOException if any IO errors occur.
	 * @throws SAXException if any parse errors occur.
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created 
	 * which satisfies the configuration requested.
	 * @return the DOM input source of the reduced document.
	 */
	public Source strip(File input, String[] tags2strip)
			throws SAXException, IOException, ParserConfigurationException {
		Source domSrc = null;
		
		if (input != null) {
			// parse input xml document
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(input);
			
			// normalize text representation
			doc.getDocumentElement().normalize();
			
			// strip off tags
			for (String tagName : tags2strip) {
				System.out.print("Removing <" + tagName + "> tag... ");
				// find all occurrences of tag
				NodeList tags = doc.getElementsByTagName(tagName);	
				int length = tags.getLength();
				for (int i = 0; i < length; i++) {
					Node node = tags.item(0);
					// remove node from parent
					node.getParentNode().removeChild(node);
				}
				System.out.println("done.");
			}
			
			// wrap document in DOM source
			domSrc = new DOMSource(doc);
		}
		return domSrc;
	}
	
	/**
	 * Writes the provided DOM input source to the specified file location.
	 * 
	 * @param output The output file.
	 * @param domSrc The DOM input source
	 * @throws TransformerFactoryConfigurationError when it is not possible to 
	 * create a Transformer instance.
	 * @throws TransformerException if an unrecoverable error occurs during the 
	 * course of the transformation.
	 */
	public void write(File output, Source domSrc)
			throws TransformerFactoryConfigurationError, TransformerException {
		System.out.print("Writing output file... ");
		if ((output != null) && (domSrc != null)) {
			Result res = new StreamResult(output);

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(domSrc, res);
			System.out.println("done!");
		} else {
			System.err.println("failed!");
		}
	}
	
	/**
	 * Runnable main method to dynamically pick a target XML file that is to be
	 * trimmed of the specified tags.
	 * 
	 * @param args The array of tag names that are to be removed.
	 * @throws Exception if anything goes wrong.
	 */
	public static void main(String[] args) throws Exception {
		// handle arguments
		if (args.length == 0) {
			// use defaults if no arguments were provided
			args = new String[] { "cofactors", "links", "references",
					"systematic_name", "synonyms", "reactions", "history" };
		}
		
		// choose input file
		File input = null;
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new XmlFileFilter());
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			input = fc.getSelectedFile();
		} else {
			System.err.println("No file selected - aborting...");
			return;
		}
		
		XMLStripper stripper = new XMLStripper();
		
		// parse input
		Source domSrc = stripper.strip(input, args);

		// write output file
		StringBuffer nameBuf = new StringBuffer(input.getPath());
		nameBuf.insert(nameBuf.lastIndexOf("."), "_reduced");
		File output = new File(nameBuf.toString());
		
		stripper.write(output, domSrc);
	}
	
}
