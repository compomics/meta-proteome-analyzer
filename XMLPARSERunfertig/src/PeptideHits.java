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
public class PeptideHits {
	
	// Deklarieren von String Peptidename + g/s
	private String Peptidname;
	public String getPeptidname() {
		return Peptidname;
	}
	public void setPeptidname(String peptidname) {
		Peptidname = peptidname;
	}

	// Deklarieren von String Sequence + g/s
	private String Sequence;
	public String getSequence() {
	return Sequence;
	}
	public void setSequence(String sequence) {
	Sequence = sequence;
}
}
