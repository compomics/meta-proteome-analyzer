package de.mpa.client.ui;

import java.util.List;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import uk.ac.ebi.kraken.interfaces.uniprot.Organism;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;

public class ProteinTreeNode extends DefaultMutableTreeTableNode {
	
	private Object[] userObjects;

	public ProteinTreeNode(Object... userObjects) {
		super(userObjects[0]);
		this.userObjects = userObjects;
		if(userObjects[1] instanceof ProteinDescription) {
			userObjects[1] = getProteinName((ProteinDescription) userObjects[1]);
		}
		if(userObjects[2] instanceof Organism) {
			userObjects[2] = ((Organism) userObjects[2]).getScientificName().getValue();
		}
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
	public String getProteinName(ProteinDescription desc) {
		Name name = null;
		
		// Recommended name only.
		if(desc.hasRecommendedName()){
			name = desc.getRecommendedName();
		} else if(desc.hasAlternativeNames()) {
			name = desc.getAlternativeNames().get(0);
		} else if(desc.hasSubNames()) {
			name = desc.getSubNames().get(0);
		}
		return name == null ? "" : name.getFieldsByType(FieldType.FULL).get(0).getValue();
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
	
	@Override
	public Object getUserObject() {
		return this.getUserObject(0);
	}

	private Object getUserObject(int i) {
		return this.userObjects[i];
	}

	@Override
	public int getColumnCount() {
		return this.userObjects.length;
	}
	
	@Override
	public Object getValueAt(int column) {
		return this.userObjects[column];
	}
	
	@Override
	public void setValueAt(Object aValue, int column) {
		this.userObjects[column] = aValue;
	}
	
	public void removeAllChildren() {
		for (int i = getChildCount()-1; i >= 0; i--) {
			remove(i);
		}
	}
}
