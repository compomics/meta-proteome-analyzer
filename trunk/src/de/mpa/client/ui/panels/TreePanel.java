package de.mpa.client.ui.panels;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxon;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.ECNumber;
import de.mpa.analysis.UniprotAccessor;
import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.BarChartHighlighter;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.ProteinTreeNode;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.TaxonTreeNode;
import de.mpa.client.ui.TableConfig.CustomTableCellRenderer;

public class TreePanel extends JPanel {
	
	private JXTreeTable taxonomyTree;
	private JXTreeTable enzymeTree;
	private Client client;
	private JButton updateBtn;
	private DefaultTreeTableModel enzymeTreeModel;
	private DefaultTreeTableModel taxonTreeModel;
	private JXTitledPanel treeTtlPnl;
	
	/**
	 * Constructs a spectrum file selection and preview panel.
	 */
	public TreePanel() {
		client = Client.getInstance();
		this.initComponents();
	}
	
	private void setupTaxonomyTreeTable() {
		DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();
		taxonTreeModel = new DefaultTreeTableModel(root) {
			{ setColumnIdentifiers(Arrays.asList(new String[] { "Taxon", "Description", "#Proteins", "SpC"})); }
			@Override
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 2: 
				case 3:
					return Integer.class;
				case 1:
				default:
					return String.class;
				}
			}
		};
		
		taxonomyTree = new JXTreeTable(taxonTreeModel);
		BasicTreeUI btui = (BasicTreeUI) ((JXTree) (taxonomyTree.getCellRenderer(0, taxonomyTree.getHierarchicalColumn()))).getUI();
		btui.setLeftChildIndent(5);
		btui.setRightChildIndent(7);
		TableConfig.setColumnWidths(taxonomyTree, new double[] {3, 4, 1, 1});
		
		TableColumnModel tcm = taxonomyTree.getColumnModel();
		((TableColumnExt) tcm.getColumn(2)).addHighlighter(new BarChartHighlighter());
		((TableColumnExt) tcm.getColumn(3)).addHighlighter(new BarChartHighlighter());
		
		taxonomyTree.setColumnMargin(1);
		taxonomyTree.setRowMargin(1);
		taxonomyTree.setShowGrid(true);
		taxonomyTree.setColumnControlVisible(true);
		taxonomyTree.addHighlighter(TableConfig.getSimpleStriping());
	}

	private void setupEnzymeTreeTable() {
		DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();
		enzymeTreeModel = new DefaultTreeTableModel(root) {
			{ setColumnIdentifiers(Arrays.asList(new String[] {"Enzyme", "Description", "Organism", "No. Spectra"})); }
			@Override
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 1:
				case 2:
					return String.class;
				case 3:
					return Integer.class;
				default:
					return String.class;
				}
			}
		};
		
		enzymeTree = new JXTreeTable(enzymeTreeModel);
		TableConfig.setColumnWidths(enzymeTree, new double[] {3, 4, 1, 1});
		enzymeTree.getColumnModel().getColumn(3).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));
	}
	
	/**
	 * Initialize the components.
	 */
	private void initComponents() {
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		setupTaxonomyTreeTable();
		setupEnzymeTreeTable();

		JScrollPane treeScp = new JScrollPane(taxonomyTree);
		JScrollPane tree2Scp = new JScrollPane(enzymeTree);
		treeScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		tree2Scp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		// Set the leaf icon
		ImageIcon proteinIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/protein.png"));
		taxonomyTree.setLeafIcon(proteinIcon);
		enzymeTree.setLeafIcon(proteinIcon);
		
		taxonomyTree.setShowsRootHandles(true);
		taxonomyTree.setRootVisible(false);

		enzymeTree.setShowsRootHandles(true);
		enzymeTree.setRootVisible(false);
		
		updateBtn = new JButton("Update");
		updateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				UniProtAccessTask task = new UniProtAccessTask();
				task.execute();
			}
		});
		
		final CardLayout cl = new CardLayout();
		final JPanel treePnl = new JPanel(cl);
		
		final JPanel taxonTblScpPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		taxonTblScpPnl.add(treeScp, CC.xy(2, 2));
		final JPanel enzymeTblScpPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		enzymeTblScpPnl.add(tree2Scp, CC.xy(2, 2));
		
		// Add cards
		treePnl.add(taxonTblScpPnl, "Taxon");
		treePnl.add(enzymeTblScpPnl, "Enzyme");
		
		// build control button panel for card layout
		JButton nextBtn = new JButton("\u203A");
		nextBtn.setPreferredSize(new Dimension(19, 18));
		
		treeTtlPnl = PanelConfig.createTitledPanel("Taxonomy Tree", treePnl);
		treeTtlPnl.setRightDecoration(nextBtn);
		
		nextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.previous(treePnl);
				Component[] components = treePnl.getComponents();
				for (Component component : components) {
					if (component.isVisible()) {
						if (component == taxonTblScpPnl) {
							treeTtlPnl.setTitle("Taxonomy Tree");
						} else {
							treeTtlPnl.setTitle("Enzyme Tree");
						}
					}
				}
			}
		});
		
		this.add(treeTtlPnl, CC.xy(2, 2));
		this.add(updateBtn, CC.xy(2, 4));
	}
	
	/**
	 * Updates taxonomy tree.
	 */
	protected void updateTaxonomyTree() {
		// Empty the tree.
		//((DefaultMutableTreeTableNode) treeModel.getRoot()).removeAllChildren();
		//treeModel.reload();
		DefaultMutableTreeTableNode root = (DefaultMutableTreeTableNode) taxonTreeModel.getRoot();
		
		// Get the result object.
		DbSearchResult dbSearchResult = client.getDbSearchResult();
		for (ProteinHit proteinHit : dbSearchResult.getProteinHits().values()) {
			UniProtEntry entry = proteinHit.getUniprotEntry();

			List<NcbiTaxon> ncbiTaxa = entry.getTaxonomy();
			List<Object> taxa = new ArrayList<Object>(ncbiTaxa);
			taxa.add(entry.getOrganism());
			DefaultMutableTreeTableNode parent = root;
			
			// Iterate the taxonomic ranks in descending order: 
			// Domain --> Phylum --> Class --> Order --> Family --> Genus --> Species
//			if (taxa.size() == 7) {
				for (int i = 0; i < taxa.size(); i++) {
					TaxonTreeNode childNode = new TaxonTreeNode(taxa.get(i));
					 
					int childCount = parent.getChildCount();
					boolean existsNode = false;
					for (int j = 0; j < childCount; j++) {
						TaxonTreeNode subNode = (TaxonTreeNode) parent.getChildAt(j);
						if (existsNode |= subNode.getUserObject().equals(childNode.getUserObject())) {
							parent = subNode;
							break;
						}
					}
					if (!existsNode) {
						taxonTreeModel.insertNodeInto(childNode, parent, parent.getChildCount());
						parent = childNode;
					}
				}
				TaxonTreeNode proteinNode = new TaxonTreeNode(proteinHit);
				
				// Finally insert the protein itself.
				taxonTreeModel.insertNodeInto(proteinNode, parent, parent.getChildCount());
//			} else {
//				System.out.println(taxa.size());
//				for (int i = 0; i < taxa.size(); i++) {
//					System.out.println(taxa.get(i));
//				}
//			}
		}
		int maxProtCount = 0, maxSpecCount = 0;
		for (int i = 0; i < root.getChildCount(); i++) {
			TaxonTreeNode domain = (TaxonTreeNode) root.getChildAt(i);
			maxProtCount = Math.max(maxProtCount, (Integer) domain.getValueAt(taxonomyTree.convertColumnIndexToView(2)));
			maxSpecCount = Math.max(maxSpecCount, (Integer) domain.getValueAt(taxonomyTree.convertColumnIndexToView(3)));
		}
		
		FontMetrics fm = getFontMetrics(UIManager.getFont("Label.font"));
		TableColumnModel tcm = taxonomyTree.getColumnModel();
		BarChartHighlighter highlighter;
		
		highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(taxonomyTree.convertColumnIndexToView(2))).getHighlighters()[0];
		highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxProtCount)));
		highlighter.setRange(0.0, maxProtCount);

		highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(taxonomyTree.convertColumnIndexToView(3))).getHighlighters()[0];
		highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxSpecCount)));
		highlighter.setRange(0.0, maxSpecCount);
	}
	
	/**
	 * Updates enzyme tree.
	 */
	protected void updateEnzymeTree() {
		// Empty the tree.
//		((DefaultMutableTreeNode) model2.getRoot()).removeAllChildren();
//		model2.reload();
		
		// Get the result object.
		DbSearchResult dbSearchResult = client.getDbSearchResult();
		for (ProteinHit proteinHit : dbSearchResult.getProteinHits().values()) {
			UniProtEntry entry = proteinHit.getUniprotEntry();
			List<String> ecNumbers = entry.getProteinDescription().getEcNumbers();
			
			// The EC numbers.
			if(ecNumbers != null && ecNumbers.size() > 0) {
				ECNumber ecNumber = new ECNumber(ecNumbers.get(0));
				
				DefaultMutableTreeTableNode parent = (DefaultMutableTreeTableNode) enzymeTreeModel.getRoot();
				
				// Iterate the taxonomic ranks in descending order: 
				for (int i = 0; i < 4; i++) {
					
					DefaultMutableTreeTableNode childNode = new DefaultMutableTreeTableNode(ecNumber.getClassName(i)); 
					int childCount = parent.getChildCount();
					boolean existsNode = false;
					for (int j = 0; j < childCount; j++) {
						DefaultMutableTreeTableNode subNode = (DefaultMutableTreeTableNode) parent.getChildAt(j);
						if(existsNode |= subNode.getUserObject().equals(childNode.getUserObject())) {
							parent = subNode;
							break;
						}
					}
					if(!existsNode) {
						enzymeTreeModel.insertNodeInto(childNode, parent, parent.getChildCount());
						parent = childNode;
					}
				}
				ProteinTreeNode proteinNode = new ProteinTreeNode(entry.getPrimaryUniProtAccession().getValue(), entry.getProteinDescription(), entry.getOrganism(), proteinHit.getSpectralCount());
				
				// Finally insert the protein itself.
				enzymeTreeModel.insertNodeInto(proteinNode, parent, parent.getChildCount());
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
			updateTaxonomyTree();
			updateEnzymeTree();
			taxonomyTree.expandAll();
			enzymeTree.expandAll();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}
