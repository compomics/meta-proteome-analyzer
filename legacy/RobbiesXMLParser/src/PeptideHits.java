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
	private String PeptidQuery ="";
	// Deklarieren von String Sequence + g/s
		private String Sequence = "";
	// Deklarieren von Masse/Ladung
		private double PeptideMz= 0;
	// Deklarieren von Ladung
	private int Ladung = 0;
	
		

	public String getPeptidQuery() {
		return PeptidQuery;
	}
	public void setPeptidQuery(String peptidQuery) {
		PeptidQuery = peptidQuery;
	}
	
	public String getSequence() {
		return Sequence;
	}
	public void setSequence(String sequence) {
		Sequence = sequence;
	}
	
	public double getPeptideMz() {
		return PeptideMz;
	}
	public void setPeptideMz(double peptideMz) {
		PeptideMz = peptideMz;
	}
	
	public int getLadung() {
		return Ladung;
	}
	public void setLadung(int ladung) {
		Ladung = ladung;
	}
}
