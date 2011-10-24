/** Beschreibung******************************************************
 *  Parser für xmls aus Mascot Ergebnissen basierend auf Pull Parser 3
 * C:\Documents and Settings\heyer\Desktop\xpp3-1.1.3.4.C\README.html
 * @author Robert Heyer
 * Date 12.10.2011
 *********************************************************************/

/** Import von Packages **********************************************/

//import Pull Parser
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//import org.xmlpull.v1.XmlPullParserFactory;

// import einladen von Daten
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Vector;

/** Hauptfunktion ***************************************************/

public class RobbiesXMLParser_main {


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//Deklarieren von Klassenobjekt was alles wiedergibt
		XMLTabelle ergebnisseXMLParsen = new XMLTabelle();


		// Name des XML Files
		String NameXMLDatei = "Spot24.xml";
		System.out.println(NameXMLDatei);

		// Versuchfunktion falls es kein XML gibt
		// Variable XMLText
		StringBuffer XMLText = new StringBuffer();



		try {
			// Erzeugen von Filereader und Übergabe des Filenamens an
			// Konstruktor
			FileReader fr = new FileReader(NameXMLDatei);
			// FileReader, wobei dessen Kontruktor der Filereaderübergeben wird
			BufferedReader br = new BufferedReader(fr);
			// alternativ BufferedReader br = new BufferedReader(new
			// FileReader(NameXMLDatei));

			// durchgehen des eingelesenen Texten und speichern des Textes in
			// Definiert Variable für Zeile
			String zeile = "";
			// Definieren Variable (gebufferter String) für Gesamttext

			// Speichert aktuelle Zeile in zeile und prüft ob diese Null ist
			while ((zeile = br.readLine()) != null) {// System.out.println(zeile);


				// if URI dann speichern
				if (ergebnisseXMLParsen.getuRI() == "") {
					ergebnisseXMLParsen.setuRI(getTagContents(zeile,"URI"));					
				}

				//if FILENAME dann speichern
				if (ergebnisseXMLParsen.getFilename() == "") {
					ergebnisseXMLParsen.setFilename(getTagContents(zeile,"FILENAME"));					
				}
				// überprüft ob es Hits gibt
				if (zeile.contains("<hits/>")){
					//	ergebnisseXMLParsen.setHits(null);//wenn keine Hits keine Einträge
					break; //beenden da keine Hits drin
				}

				//überprüfen on Hits gefunden dann Hits einlesen
				if (zeile.contains("<hits>")){
					ArrayList<ProteinHits> listeProteinHits = new ArrayList<ProteinHits>();

					ProteinHits proteinHit = new ProteinHits();
					while(!(zeile = br.readLine()).contains("</hits>")){

						// objekt erneuern
						if (zeile.contains("<hit ")){
							listeProteinHits.add(proteinHit);
							proteinHit = new ProteinHits();
						}
						// if Zeile beinhalten Protein accession
						if (zeile.contains("<protein")){
							proteinHit.setProtein_accession(zeile.substring(zeile.indexOf("accession=")+11, zeile.lastIndexOf("\"")));
						}
						// Parsen ProteinDescription
						if (proteinHit.getProtein_description() == "") {
							proteinHit.setProtein_description(getTagContents(zeile,"prot_desc"));
						}

						String helper = "";
						// Parsen ProteinScore
						if (proteinHit.getProtein_Score() == 0.0) {
							helper = getTagContents(zeile,"prot_score");
							if (!helper.isEmpty()) {
								proteinHit.setProtein_Score(Double.parseDouble(helper));
							}
						}
						// Parsen ProteinMass
						if (proteinHit.getProtein_Mass() == 0.0) {
							helper = getTagContents(zeile,"prot_mass");
							if (!helper.isEmpty()) {
								proteinHit.setProtein_Mass(Double.parseDouble(helper));
							}
						}
						// Peptide Parsen

						if (zeile.contains("<peptide")){
							PeptideHits peptideHit = new PeptideHits();
							ArrayList<PeptideHits> listePeptideHits = new ArrayList<PeptideHits>();

							while(!(zeile = br.readLine()).contains("</peptide>")){
								// objekt erneuern
								if (zeile.contains("<peptide ")){
									listePeptideHits.add(peptideHit);
									peptideHit = new PeptideHits();
									// Peptide query	
									peptideHit.setPeptidQuery(zeile.substring(zeile.indexOf("peptide")+8, zeile.lastIndexOf("\"")));
								}
								// Parsen Peptide Mz
								if (peptideHit.getPeptideMz() == 0.0) {
									helper = getTagContents(zeile,"pep_exp_mze");
									if (!helper.isEmpty()) {
										peptideHit.setPeptideMz(Double.parseDouble(helper));
									}
								}
								// Parsen Peptide Charge
								if (peptideHit.getLadung()== 0.0) {
									helper = getTagContents(zeile,"pep_exp_z");
									if (!helper.isEmpty()) {
										peptideHit.setLadung(Integer.parseInt(helper));
									}
								}
								// Parsen Peptide Sequence
								if (peptideHit.getSequence() == "") {
									peptideHit.setSequence(getTagContents(zeile,"pep_seq"));
								}
							}
							listePeptideHits.add(peptideHit);
						}
					}
					listeProteinHits.add(proteinHit);
					ergebnisseXMLParsen.setHits(listeProteinHits);
				}

				XMLText.append(zeile + "\n");
			}
			// Ausgabe des Ergebnisses
			//System.out.println(XMLText);
			// schließen der einlese Funktion
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("XML nicht gefunden");
		}




	}

	/* weitere Methoden ********************** */
	private static String getTagContents(String zeile, String tag) {
		String ret = "";

		if (zeile.contains("<"+tag+"/>")){
			ret = "nix "+tag;
		}
		if (zeile.contains("<"+tag+">")){// führt automatisch aus wenn es true ist
			ret = zeile.substring(zeile.indexOf(">")+1, zeile.lastIndexOf("<"));
		}
		return ret;

	}

}
