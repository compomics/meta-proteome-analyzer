package de.mpa.client.ui;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.Organism;

public class TaxonTreeNode extends DefaultMutableTreeTableNode {
	
	private Object[] userObjects;
	
	public TaxonTreeNode(Object... userObjects) {
		if(userObjects[0] instanceof Organism) {
			userObject = ((Organism) userObjects[0]).getScientificName().getValue();
			this.userObjects = new Object[5];
			this.userObjects[0] = userObject;
			this.userObjects[2] = getChildCount();
		} else if (userObjects[0] instanceof NcbiTaxon) {
			userObject = ((NcbiTaxon) userObjects[0]).getValue();
			this.userObjects = new Object[2];
			this.userObjects[0] = userObjects[0];
		}
	}
	
	@Override
	public boolean isLeaf(){
		return false;
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
		switch (column) {
		case 2:
			return this.getChildCount();
		default:
			return this.userObjects[column];
		}
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
