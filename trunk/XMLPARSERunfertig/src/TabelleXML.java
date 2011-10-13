/** Beschreibung******************************************************
 *  Erzeugt Objekt in dem die Ergebnisse des XML Parsens gespeichert werden
 *  1. Ebene NAME XML
 *  - NAME XML
 *   	2.Ebene: Hits mit Objekt ProteinHits
 *  	- Protein_asccession
 *  	- Protein_description
 *  	- Protein Score	
 *  		3. Ebene: Peptide--> Objekt PeptideHits
 *  		- Peptidename
 *  		- Sequence
 *  
 * @author Robert Heyer
 * Date 13.10.2011
 *********************************************************************/

// Import Part
import java.util.ArrayList;


public class TabelleXML {
	
	// Deklarieren String Name XML +g/s
	private String NameXML = "";
	public String getNameXML() {
		return this.NameXML;
	}

	public void setNameXML(String nameXML) {
		this.NameXML = nameXML;
	}

	// Deklarieren Array ProteinHits +g/s
	private ArrayList<ProteinHits> Hits;
	public ArrayList<ProteinHits> getHits() {
		return Hits;
	}

	public void setHits(ArrayList<ProteinHits> hits) {
		Hits = hits;
	}
	
	
	
	
	
	
}
