package de.mpa.client.ui.panels;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.JXTree;

import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.UniprotAccessor;
import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.ProteinTreeNode;
import de.mpa.client.ui.TaxonTreeNode;

public class TreePanel extends JPanel {
	
	private JXTree taxonomyTree;
	private Client client;
	private DefaultTreeModel model;
	private JButton updateBtn;
	
	/**
	 * Constructs a spectrum file selection and preview panel.
	 */
	public TreePanel() {
		client = Client.getInstance();
		this.initComponents();
	}
	
	/**
	 * Initialize the components.
	 */
	private void initComponents() {
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		
		taxonomyTree = new JXTree();
		JScrollPane treeScp = new JScrollPane(taxonomyTree);
		treeScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		treeScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		// Set the leaf icon
		ImageIcon proteinIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/protein.png"));
		taxonomyTree.setLeafIcon(proteinIcon);
		model = new DefaultTreeModel(root);
		model.setRoot(new DefaultMutableTreeNode());
		taxonomyTree.setShowsRootHandles(true);
		taxonomyTree.setRootVisible(false);
		taxonomyTree.setModel(model);

		
		updateBtn = new JButton("Update");
		updateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				UniProtAccessTask task = new UniProtAccessTask();
				task.execute();
			}
		});
		this.add(treeScp, CC.xy(2, 2));
		this.add(updateBtn, CC.xy(2, 4));
	}
	
	/**
	 * Update the tree.
	 */
	protected void updateTree(){
		// Empty the tree.
		((DefaultMutableTreeNode) model.getRoot()).removeAllChildren();
		model.reload();
		
		// Get the result object.
		DbSearchResult dbSearchResult = client.getDbSearchResult();
		for (ProteinHit proteinHit : dbSearchResult.getProteinHits().values()) {
			UniProtEntry entry = proteinHit.getUniprotEntry();

			List<NcbiTaxon> ncbiTaxa = entry.getTaxonomy();
			List<Object> taxa = new ArrayList<Object>(ncbiTaxa);
			taxa.add(entry.getOrganism());
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) model.getRoot();
			
			// Iterate the taxonomic ranks in descending order: 
			// Domain --> Phylum --> Class --> Order --> Family --> Genus --> Species
			if(taxa.size() == 7){
				for (int i = 0; i < taxa.size(); i++) {
					TaxonTreeNode childNode = new TaxonTreeNode(taxa.get(i)); 
					int childCount = parent.getChildCount();
					boolean existsNode = false;
					for (int j = 0; j < childCount; j++) {
						TaxonTreeNode subNode = (TaxonTreeNode) parent.getChildAt(j);
						if(existsNode |= subNode.getUserObject().equals(childNode.getUserObject())) {
							parent = subNode;
							break;
						}
					}
					if(!existsNode) {
						model.insertNodeInto(childNode, parent, parent.getChildCount());
						parent = childNode;
					}
				}
				ProteinTreeNode proteinNode = new ProteinTreeNode(entry.getPrimaryUniProtAccession().getValue(), entry.getProteinDescription());
				
				// Finally insert the protein itself.
				model.insertNodeInto(proteinNode, parent, parent.getChildCount());
			}
		}
	}
	
	private class UniProtAccessTask extends SwingWorker<Object, Void> {

		protected Object doInBackground() {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// Fetch the database search result.
			try {
				UniprotAccessor.retrieveUniprotEntries(client.getDbSearchResult());
			} catch (RemoteDataAccessException e) {
				e.printStackTrace();
			}
			return 0;
		}

		/**
		 * Continues when the results retrieval has finished.
		 */
		protected void done() {
			updateTree();
			taxonomyTree.expandAll();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}
