package de.mpa.client.ui;

import javax.swing.tree.DefaultMutableTreeNode;

import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.Organism;

public class TaxonTreeNode extends DefaultMutableTreeNode {
	
	public TaxonTreeNode(Object taxonObj) {
		if(taxonObj instanceof Organism) {
			userObject = ((Organism) taxonObj).getScientificName().getValue();
		} else if (taxonObj instanceof NcbiTaxon) {
			userObject = ((NcbiTaxon) taxonObj).getValue();
		}
		
	}
	
	public boolean isLeaf(){
		return false;
	}

}
