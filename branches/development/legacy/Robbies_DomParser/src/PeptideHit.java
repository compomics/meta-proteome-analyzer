/* Name:				RobbiesDomParser
 * letzte Änderung:		25.10.2011
 * Author:				Robbie
 * Beschreibung:		Objekt in dem PeptideInhalt des XML reingeschrieben wird
 */


public class PeptideHit {

	// Deklarieren der Variablen
	private String peptidQuery ="";
	private String sequence = "";
	private double peptideMz= 0;
	private int ladung = 0;
	
	// Deklaration der Getter und Setter
	public String getPeptidQuery() {
		return peptidQuery;
	}
	public void setPeptidQuery(String peptidQuery) {
		peptidQuery = peptidQuery;
	}
	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		sequence = sequence;
	}
	
	public double getPeptideMz() {
		return peptideMz;
	}
	public void setPeptideMz(double peptideMz) {
		peptideMz = peptideMz;
	}
	
	public int getLadung() {
		return ladung;
	}
	public void setLadung(int ladung) {
		ladung = ladung;
	}
	
	
}
