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


// Import Block
import java.util.ArrayList;


public class ProteinHits {
	
	// Deklarieren String Protein_asssion +g/s
	private String Protein_accession;
	public String getProtein_accession() {
		return Protein_accession;
	}
	public void setProtein_accession(String protein_accession) {
		Protein_accession = protein_accession;
	}
	
	// Deklarieren String Protein_description + g/s
	private String Protein_description;
	public String getProtein_description() {
		return Protein_description;
	}
	public void setProtein_description(String protein_description) {
		Protein_description = protein_description;
	}
	
	// Deklarieren int Protein_Score + g/s
	private int Protein_Score;
	public int getProtein_Score() {
		return Protein_Score;
	}
	public void setProtein_Score(int protein_Score) {
		Protein_Score = protein_Score;
	}
	
	// Deklarieren Array Peptide mit Objekz PeptideHits + g/s
	private ArrayList<PeptideHits> Peptide;
	public ArrayList<PeptideHits> getPeptide() {
		return Peptide;
	}
	public void setPeptide(ArrayList<PeptideHits> peptide) {
		Peptide = peptide;
	}
}
