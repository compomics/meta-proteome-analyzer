package de.mpa.client.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.Organism;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import de.mpa.client.model.dbsearch.ProteinHit;

public class TaxonTreeNode extends DefaultMutableTreeTableNode {
	
	private Object[] userObjects;

	private static final int NAME = 0;
	private static final int DESCRIPTION = 1;
	private static final int PROTEIN_COUNT = 2;
	private static final int SPECTRAL_COUNT = 3;
	
	/**
	 * Constructs a taxonomic view tree node using UniProtJAPI taxonomy data or a protein hit.
	 */
	public TaxonTreeNode(Object obj) {
		this.userObjects = new Object[4];
		this.userObjects[SPECTRAL_COUNT] = new HashSet<Long>();
		if (obj instanceof NcbiTaxon) {
			this.userObjects[NAME] = ((NcbiTaxon) obj).getValue();
		} else if (obj instanceof Organism) {
			this.userObjects[NAME] = ((Organism) obj).getScientificName().getValue();
		} else if (obj instanceof ProteinHit) {
			ProteinHit ph = (ProteinHit) obj;
			this.userObjects[NAME] = ph.getAccession();
			this.userObjects[DESCRIPTION] = ph.getDescription();
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
	
	@SuppressWarnings("unchecked")
	protected void addSpectrumIDs(Set<Long> spectrumIDs) {
		((Collection<Long>) this.userObjects[SPECTRAL_COUNT]).addAll(spectrumIDs);
		if (getParent().getParent() != null) {	// if parent is not root
			((TaxonTreeNode) getParent()).addSpectrumIDs(spectrumIDs);
		}
	}
	
	@Override
	public Object getUserObject() {
		return this.getUserObject(0);
	}

	public Object getUserObject(int i) {
		return this.userObjects[i];
	}

	@Override
	public int getColumnCount() {
		return this.userObjects.length;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getValueAt(int column) {
		switch (column) {
		case PROTEIN_COUNT:
			if (isLeaf()) {
				return null;
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
			return ((Collection) userObjects[SPECTRAL_COUNT]).size();	
		default:
			return this.userObjects[column];
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValueAt(Object aValue, int column) {
		switch (column) {
		case SPECTRAL_COUNT:
			addSpectrumIDs((Set<Long>) aValue);
			break;
		default:
			this.userObjects[column] = aValue;
			break;
		}
	}
	
	public void removeAllChildren() {
		for (int i = getChildCount()-1; i >= 0; i--) {
			remove(i);
		}
	}
	
	@Override
	public String toString() {
		return this.userObjects[NAME].toString();
	}

}
