package de.mpa.model.blast;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * This class parse a BLAST result file.
 * @author R. Heyer
 */
public class BlastParser {

	/**
	 * A Blast result
	 */
	BlastResult blastRes;

	/**
	 * Parse a BLAST-xml-file.
	 * @param blastResFile
	 * @return 
	 * @return The BLAST results
	 */
	public static BlastResult parseBlastHit(String blastResFile){
		// parse xml document					
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		// The BLAST result object.
		BlastResult blastRes = new BlastResult();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(blastResFile));
			doc.getDocumentElement().normalize();					

			// Parse BLAST.xml header
			NodeList blastHeaderList = doc.getElementsByTagName("BlastOutput");		
			for (int temp1 = 0; temp1 < blastHeaderList.getLength(); temp1++) {
				Node nNode = blastHeaderList.item(temp1);
				NodeList childNodesList = nNode.getChildNodes();
				for (int temp2 = 0; temp2 < childNodesList.getLength(); temp2++) {
					Node cNode =childNodesList.item(temp2);
					// Get BLAST database
					if (cNode.getNodeName().equals("BlastOutput_db")) {
						blastRes.setDatabase(cNode.getTextContent());
					}
				}
			}
			
			// Parse BLAST query
			NodeList IterationHeaderList = doc.getElementsByTagName("Iteration");		
			for (int temp1 = 0; temp1 < IterationHeaderList.getLength(); temp1++) {
				Node nNode = IterationHeaderList.item(temp1);
				NodeList childNodesList = nNode.getChildNodes();
				for (int temp2 = 0; temp2 < childNodesList.getLength(); temp2++) {
					Node cNode =childNodesList.item(temp2);
					// Get query name
					if (cNode.getNodeName().equals("Iteration_query-def")) {
						blastRes.setName(cNode.getTextContent());
					}
					// Get query length
					if (cNode.getNodeName().equals("Iteration_query-len")) {
						blastRes.setLength(Integer.valueOf(cNode.getTextContent()));
					}
				}
			}
			
			// Parse BLAST hits
			String accession, name;
			BlastHit blastHit = null;
			NodeList HitHeaderList = doc.getElementsByTagName("Hit");		
			for (int temp1 = 0; temp1 < HitHeaderList.getLength(); temp1++) {
				Node nNode = HitHeaderList.item(temp1);
				NodeList childNodesList = nNode.getChildNodes();
				for (int temp2 = 0; temp2 < childNodesList.getLength(); temp2++) {
					Node cNode = childNodesList.item(temp2);
					
					// Gets the name of the BLAST hit
					if (cNode.getNodeName().equals("Hit_def")) {
						String hitName 	= cNode.getTextContent();
						String[] split 	=  hitName.split("[|]");
						accession 		= split[1];
						name 			= split[2];
						blastHit = new BlastHit(accession, name);
						blastRes.putBlastHitsMap(blastHit);

					}
					
					// Gets the name of the BLAST hit
					if (cNode.getNodeName().equals("Hit_len")) {
						int hitLength 	= Integer.valueOf(cNode.getTextContent());
						blastHit.setLength(hitLength);
						blastRes.putBlastHitsMap(blastHit);
					}
					
					// Get informations for the BLAST hit
					if (cNode.getNodeName().equals("Hit_hsps")) {
						NodeList hitHspChilds = cNode.getChildNodes();
						for (int temp3 = 0; temp3 < hitHspChilds.getLength(); temp3++) {
							Node ccNode = hitHspChilds.item(temp3);
							if (ccNode.getNodeName().equals("Hsp")) {
								NodeList hspChilds = ccNode.getChildNodes();
								for (int temp4 = 0; temp4 < hspChilds.getLength(); temp4++) {
									Node cccNode = hspChilds.item(temp4);
									
									// Gets the bit-Score
									if (cccNode.getNodeName().equals("Hsp_bit-score")) {
										double bitScore 	= Double.valueOf(cccNode.getTextContent());
										blastHit.setScore(bitScore);
										blastRes.putBlastHitsMap(blastHit);
									}
									// Gets the HSP_evalue
									if (cccNode.getNodeName().equals("Hsp_evalue")) {
										double evalue 	= Double.valueOf(cccNode.getTextContent());
										blastHit.seteValue(evalue);
										blastRes.putBlastHitsMap(blastHit);	
									}
									// HSP identity
									if (cccNode.getNodeName().equals("Hsp_identity")) {
											String identity 	= cccNode.getTextContent();
											blastHit.setIdentities(Double.parseDouble(identity));
											blastRes.putBlastHitsMap(blastHit);	
									}
									
									// Positives
									if (cccNode.getNodeName().equals("Hsp_positive")) {
										String positives 	= cccNode.getTextContent();
										blastHit.setPositives(positives);
										blastRes.putBlastHitsMap(blastHit);	
									}
									
									// Hsp gaps
									if (cccNode.getNodeName().equals("Hsp_gaps")) {
										String gaps 	= cccNode.getTextContent();
										blastHit.setGaps(gaps);
										blastRes.putBlastHitsMap(blastHit);	
									}
									
									// Query Sequence
									if (cccNode.getNodeName().equals("Hsp_qseq")) {
										String qSequence 	= cccNode.getTextContent();
										blastHit.setQuery(qSequence);
										blastRes.putBlastHitsMap(blastHit);	
									}
									
									// Result sequence
									if (cccNode.getNodeName().equals("Hsp_hseq")) {
										String resSequence 	= cccNode.getTextContent();
										blastHit.setSbjct(resSequence);
										blastRes.putBlastHitsMap(blastHit);	
									}
								}
							}
						}
					}
				}
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return blastRes;
	}
}
