import java.util.ArrayList;


	public class XMLTabelle {

	
	// Deklarieren String Name XML +g/s
	private String NameXML = "";
	//Deklarieren String URI URL wo dat drin ist
	private String uRI = "";
	// String für internen Filename
	private String filename = "";
	// Deklarieren Array ProteinHits +g/s
	private ArrayList<ProteinHits> Hits;


	//Definition von Gettern und Settern
	public String getNameXML() {
		return this.NameXML;}
	public void setNameXML(String nameXML) {
		this.NameXML = nameXML;}
	public void setHits(ArrayList<ProteinHits> hits) {
		Hits = hits;}
	public ArrayList<ProteinHits> getHits() {
		return Hits;}
	public String getuRI() {
		return uRI;}
	public void setuRI(String uRI) {
		this.uRI = uRI;	}
	public String getFilename() {
		return filename;}
	public void setFilename(String filename) {
		this.filename = filename;}	
	
}
