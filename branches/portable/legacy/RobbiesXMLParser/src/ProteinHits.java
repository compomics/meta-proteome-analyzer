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

import java.util.ArrayList;


public class ProteinHits {

	// Deklarieren String Protein_asssion +g/s
	private String Protein_accession = "";
	// Deklarieren String Protein_description + g/s
	private String Protein_description = "";
	// Deklarieren int Protein_Score + g/s
	private double Protein_Score = 0.0;	
	// Deklarieren int Protein_Score + g/s
	private double Protein_Mass = 0.0;	
	// Deklarieren Array Peptide mit Objekz PeptideHits + g/s
	private ArrayList<PeptideHits> Peptide;

	//Definition von Gettern und Settern
	public String getProtein_accession() {
		return Protein_accession;}
	public void setProtein_accession(String protein_accession) {
		Protein_accession = protein_accession;}
	public String getProtein_description() {
		return Protein_description;}
	public void setProtein_description(String protein_description) {
		Protein_description = protein_description;}
	public double getProtein_Score() {
		return Protein_Score;}
	public void setProtein_Score(double protein_Score) {
		Protein_Score = protein_Score;}
	public ArrayList<PeptideHits> getPeptide() {
		return Peptide;}
	public void setPeptide(ArrayList<PeptideHits> peptide) {
		Peptide = peptide;}
	public double getProtein_Mass() {
		return Protein_Mass;}
	public void setProtein_Mass(double protein_Mass) {
		Protein_Mass = protein_Mass;}	
}
