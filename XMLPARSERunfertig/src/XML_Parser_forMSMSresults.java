/** Beschreibung******************************************************
 *  Parser für xmls aus Mascot Ergebnissen basierend auf Pull Parser 3
 * C:\Documents and Settings\heyer\Desktop\xpp3-1.1.3.4.C\README.html
 * @author Robert Heyer
 * Date 12.10.2011
 *********************************************************************/


/** Import von Packages **********************************************/

//import Pull Parser
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

// import einladen von Daten
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Vector;
/** ******************************************************************/



/** Hauptfunktion***************************************************/
 
public class XML_Parser_forMSMSresults {

	// basteln von Array in dem ich meine Variablen speichern kann
	
	


	
		
	
	
	// Hauptfunnktion in der ich arbeite
	public static void main(String[] args)throws IOException {
	System.out.println("Yuhu ich fange an!!!");

	//TabelleXML File1 =new TabelleXML();
	//File1.setNameXML("klaus");
	
	//ArrayList<ProteinHits> gdrkeg = File1.setHits(gdrkeg);

	//Name des XML Files
	String NameXMLDatei ="Spot24.xml";
	System.out.println(NameXMLDatei);
	
	//Versuchfunktion falls es kein XML gibt
	try {
		// Erzeugen von Filereader und Übergabe des Filenamens an Konstruktor
		FileReader fr = new FileReader(NameXMLDatei);
	    //FileReader, wobei dessen Kontruktor der Filereaderübergeben wird
		BufferedReader br = new BufferedReader(fr);
		// alternativ BufferedReader br = new BufferedReader(new FileReader(NameXMLDatei));
		
		
		// durchgehen des eingelesenen Texten und speichern des Textes in Variable XMLText
			// Definiert Variable für Zeile
			String zeile ="";
			// Definieren Variable (gebufferter String) für Gesamttext
			StringBuffer XMLText = new StringBuffer();
			// Speichert aktuelle Zeile in zeile und prüft ob diese Null ist
			while( (zeile = br.readLine()) != null )
			 {//System.out.println(zeile); 
			 XMLText.append(zeile + "\n");}
			// Ausgabe des Ergebnisses
			System.out.println(XMLText);
			
			// schließen der einlese Funktion
		br.close();	
	} catch (Exception e) {System.out.println("XML nicht gefunden");}
	
	
	
		
	
		

/**Klammer die Text Abschließen*************************************************/ 
	}// 1 Klammer: main
}// 2 Klammer: XML_Parser_forMSMSresults
/******************************************************************************/



