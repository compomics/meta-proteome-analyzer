package de.mpa.client.ui;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Field;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.kraken.interfaces.uniprot.description.NameType;

public class ProteinTreeNode extends DefaultMutableTreeNode {
	
	public ProteinTreeNode(String accession, ProteinDescription desc) {
		userObject = accession + " | " + getProteinNames(desc) + " | " + getECNumberString(desc);
	}
	
	/**
	 * ProteinTreeNode is always a leaf.
	 */
	public boolean isLeaf(){
		return true;
	}
	
	/**
	 * Returns the protein name(s) as formatted string
	 * @param desc ProteinDescription object.
	 * @return Protein name(s) as formatted string.
	 */
	public String getProteinNames(ProteinDescription desc) {
		StringBuilder sb = new StringBuilder();
		
		List<Name> names = desc.getSection().getNames();
		for (Name name : names) {
			
			List<Field> fields = name.getFields();
			for (Field field : fields) {
				// Exclude EC number here.
				if(field.getType() != FieldType.EC){
					sb.append(field.getValue());
				}
				
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns the EC number(s) as formatted string.
	 * @param desc ProteinDescription object.
	 * @return EC number(s) as formatted string.
	 */
	public String getECNumberString(ProteinDescription desc) {
		StringBuilder sb = new StringBuilder();
		List<String> ecNumbers = desc.getEcNumbers();
		for (int i = 0; i < ecNumbers.size(); i++) {
			if(i == 0){
				sb.append("EC=" + ecNumbers.get(i));
			} else {
				sb.append("; " + ecNumbers.get(i));
			}
		}
		return sb.toString();
	}
}
