
import java.util.List;

/* Name:				RobbiesDomParser
 * letzte Änderung:		25.10.2011
 * Author:				Robbie
 * Beschreibung:		Objekt in dem InhaltXML reingeschrieben wird
 */

public class MascotRecord {
	// Deklaration der Variablen

	private String xmlFilename="";
	private String uri ="";
	private String mascotFilename= "";
	private List<Proteinhit> proteinHits;

	//Deklaration der Setter und Getter	
	
	public List<Proteinhit> getProteinHits() {
		return proteinHits;
	}
	public String getXmlFilename() {
		return xmlFilename;
	}
	public void setXmlFilename(String xmlFilename) {
		this.xmlFilename = xmlFilename;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getMascotFilename() {
		return mascotFilename;
	}
	public void setMascotFilename(String mascotFilename) {
		this.mascotFilename = mascotFilename;
	}
	public void setProteinHits(List<Proteinhit> proteinHits) {
		this.proteinHits = proteinHits;
	}
}
