package de.mpa.client.ui.dialogs;

import java.awt.Frame;

import javax.swing.RowFilter;

import org.jdesktop.swingx.treetable.TreeTableNode;

import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.ClientFrame;

/**
 * Singleton pattern implementation of a taxonomy-specific tree selection dialog.
 * 
 * @author A. Behne
 */
public class TaxonomySelectionDialog extends TreeSelectionDialog {

	/**
	 * The singleton instance of this dialog.
	 */
	private static TaxonomySelectionDialog instance;
	
	/**
	 * Hidden super constructor.
	 */
	private TaxonomySelectionDialog(Frame owner, String title,
			boolean modal, CheckBoxTreeTableNode root) {
		super(owner, title, modal, root);
	}
	
	/**
	 * Returns the singleton instance of the taxonomy tree selection dialog.
	 * @return
	 */
	public static TaxonomySelectionDialog getInstance() {
		if (instance == null) {
			instance = new TaxonomySelectionDialog(
					ClientFrame.getInstance(), "Choose taxa to include",
					true, new CheckBoxTreeTableNode("Taxonomy"));
		}
		return instance;
	}
	
	@Override
	protected void cachePaths() {
		super.cachePaths();
		
		// since we reuse the taxonomy protein view tree we need to hide the protein leaf nodes
		this.treeTbl.setRowFilter(new RowFilter<Object, Object>() {
			@Override
			public boolean include(
					RowFilter.Entry<? extends Object, ? extends Object> entry) {
				TreeTableNode node = (TreeTableNode) entry.getValue(-1);
				if (node.getUserObject() instanceof ProteinHit) {
					return false;
				}
				return true;
			}
			
		});
	}
	
}
