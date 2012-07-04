package de.mpa.client.ui;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import de.mpa.client.model.dbsearch.ProteinHit;

import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.Organism;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;

public class TaxonTreeNode extends DefaultMutableTreeTableNode {
	
	private Object[] userObjects;

	private static final int NAME = 0;
	private static final int DESCRIPTION = 1;
	private static final int PROTEIN_COUNT = 2;
	private static final int SPECTRAL_COUNT = 3;
	
	public TaxonTreeNode(Object... userObjects) {
		this.userObjects = new Object[4];
		if (userObjects[0] instanceof NcbiTaxon) {
			this.userObjects[NAME] = ((NcbiTaxon) userObjects[0]).getValue();
		} else if (userObjects[0] instanceof Organism) {
			this.userObjects[NAME] = ((Organism) userObjects[0]).getScientificName().getValue();
		} else if (userObjects[0] instanceof ProteinHit) {
			ProteinHit ph = (ProteinHit) userObjects[0];
			this.userObjects[NAME] = ph.getUniprotEntry().getPrimaryUniProtAccession().getValue();
			this.userObjects[DESCRIPTION] = getProteinName(ph.getUniprotEntry().getProteinDescription());
			this.userObjects[SPECTRAL_COUNT] = ph.getSpectralCount();
		}
	}
	
	/**
	 * Returns the protein name(s) as formatted string
	 * @param desc ProteinDescription object.
	 * @return Protein name(s) as formatted string.
	 */
	public String getProteinName(ProteinDescription desc) {
		Name name = null;
		
		if (desc.hasRecommendedName()) {
			name = desc.getRecommendedName();
		} else if (desc.hasAlternativeNames()) {
			name = desc.getAlternativeNames().get(0);
		} else if (desc.hasSubNames()) {
			name = desc.getSubNames().get(0);
		}
		return (name == null) ? "unknown" : name.getFieldsByType(FieldType.FULL).get(0).getValue();
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
		case PROTEIN_COUNT:
			if (isLeaf()) {
				return 0;
			} else if (getChildAt(0).isLeaf()) {
				return getChildCount();
			} else {
				int res = 0;
				for (int i = 0; i < getChildCount(); i++) {
					res += (Integer) (getChildAt(i).getValueAt(PROTEIN_COUNT));
				}
				return res;
			}
		case SPECTRAL_COUNT:
			if (isLeaf()) {
				return userObjects[SPECTRAL_COUNT];
			} else {
				int res = 0;
				for (int i = 0; i < getChildCount(); i++) {
					res += (Integer) (getChildAt(i).getValueAt(SPECTRAL_COUNT));
				}
				return res;
			}
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
