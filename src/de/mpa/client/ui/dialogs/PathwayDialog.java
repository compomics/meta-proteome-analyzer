package de.mpa.client.ui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.xml.rpc.ServiceException;

import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.Options;

import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TreeTableRowSorter;
import de.mpa.main.Parameters;

/**
 * This class initialize dialog for KEGG database
 * @author R. Heyer
 */
public class PathwayDialog extends JDialog {

//	private static JScrollPane taxTreeScpn;
//	private static JScrollPane treeScpn;
//	private static HashSet<String> pathsbyKeggID;
//	private static JXTree taxTree;
//	private static JXTree tree;
//	private static String[] getKosByPathway;
	
	public PathwayDialog() {
		super(ClientFrame.getInstance(), "Choose KEGG pathway", true);
		
		initComponents();
		
		initKOs();

		// center and show dialog
		ScreenConfig.centerInScreen(this);
		this.setVisible(true);
	}

	private void initComponents() {
		// set up main container
		Container contentPane = this.getContentPane();
		FormLayout mainLyt = new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, b:p, 5dlu");
		mainLyt.setColumnGroups(new int[][] { { 2, 4 } });
		contentPane.setLayout(mainLyt);

		// construct left-hand side pathway tree panel
		JPanel pathPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "f:p:g, 5dlu, p, 5dlu"));
		pathPnl.setBorder(BorderFactory.createTitledBorder("KEGG Pathways"));
		
		SortableTreeTableModel pathTreeMdl = new SortableTreeTableModel(
				Parameters.getInstance().getKeggPathwayTreeRoot()) {
			@Override
			public void sort() {
				super.sort();
				modelSupport.fireNewRoot();
			}
		};
		
		final JXTreeTable pathTree = new JXTreeTable(
				pathTreeMdl) {
			/* Overrides of sorting-related methods forwarding to JXTreeTable's hooks. */
			@Override
			public void setSortable(boolean sortable) {
				superSetSortable(sortable);
			}
			@Override
			public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
				superSetAutoCreateRowSorter(autoCreateRowSorter);
			}
			@Override
			public void setRowSorter(RowSorter<? extends TableModel> sorter) {
				superSetRowSorter(sorter);
			}
			@Override
			public RowFilter<?, ?> getRowFilter() {
				return ((TreeTableRowSorter<?>) getRowSorter()).getRowFilter();
			}
			@Override
			@SuppressWarnings("unchecked")
			public <R extends TableModel> void setRowFilter(RowFilter<? super R, ? super Integer> filter) {
		            // all fine, because R extends TableModel
		            ((TreeTableRowSorter<R>) getRowSorter()).setRowFilter(filter);
		    }
		};
		pathTree.setSortable(true);
		pathTree.setAutoCreateRowSorter(true);
		pathTree.setRowSorter(new TreeTableRowSorter<TableModel>(pathTree));
		pathTree.setLargeModel(true);
		
//		final SortableCheckBoxTreeTable pathTree = new SortableCheckBoxTreeTable(new SortableTreeTableModel(
//				Parameters.getInstance().getKeggPathwayTreeRoot()));
		
		pathTree.setTableHeader(null);
		pathTree.setRootVisible(false);
		pathTree.setShowsRootHandles(true);
		JXTree tree = (JXTree) pathTree.getCellRenderer(0, pathTree.getHierarchicalColumn());
		tree.putClientProperty(Options.TREE_LINE_STYLE_KEY, "dashed");
		((BasicTreeUI) tree.getUI()).setLeftChildIndent(6);
		((BasicTreeUI) tree.getUI()).setRightChildIndent(8);
		pathTree.expandAll();
		JScrollPane pathTreeScpn = new JScrollPane(pathTree);
		pathTreeScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pathTreeScpn.setPreferredSize(new Dimension(300, 400));
		
		final JTextField pathFilterTtf = new JTextField();
		pathFilterTtf.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) { filter(); }
			public void insertUpdate(DocumentEvent e) { filter(); }
			public void changedUpdate(DocumentEvent e) { filter(); }
			@SuppressWarnings("unchecked")
			private void filter() {
				RowFilter<Object, Object> rowFilter = null;
				String pattern = pathFilterTtf.getText();
				if (!pattern.isEmpty()) {
					try {
						rowFilter = RowFilter.regexFilter(pattern, 0);
						pathFilterTtf.setForeground(Color.BLACK);
					} catch (PatternSyntaxException pse) {
						pathFilterTtf.setForeground(Color.RED);
					}
				}
				((TreeTableRowSorter<TableModel>) pathTree.getRowSorter()).setRowFilter(rowFilter);
			}
		});
		
		pathPnl.add(pathTreeScpn, CC.xy(2, 1));
		pathPnl.add(pathFilterTtf, CC.xy(2, 3));
		
		// construct right-hand side taxonomy tree panel
		JPanel taxPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "f:p:g, 5dlu"));
		taxPnl.setBorder(BorderFactory.createTitledBorder("KEGG Taxonomy"));
		
		JXTree taxTree = new JXTree(initTaxonomyTreeModel());
		taxTree.setRootVisible(false);
		taxTree.setShowsRootHandles(true);
		((BasicTreeUI) taxTree.getUI()).setLeftChildIndent(6);
		((BasicTreeUI) taxTree.getUI()).setRightChildIndent(8);
		JScrollPane taxTreeScpn = new JScrollPane(taxTree);
		taxTreeScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		taxTreeScpn.setPreferredSize(new Dimension(300, 400));
		
		taxPnl.add(taxTreeScpn, CC.xy(2, 1));
		
		// construct button panel
		JPanel buttonPnl = new JPanel();
		FormLayout buttonLyt = new FormLayout("0:g, p, 5dlu, p", "p");
		buttonLyt.setColumnGroups(new int[][] { { 2, 4 } });
		buttonPnl.setLayout(buttonLyt);

		final JButton okayBtn = new JButton("OK");
		JButton cancelBtn = new JButton("Cancel");
		
		buttonPnl.add(okayBtn, CC.xy(2, 1));
		buttonPnl.add(cancelBtn, CC.xy(4, 1));
		
		// install listeners
		pathTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				// TODO: perform selection checks
				if (3 > 2) {
					// refresh taxonomy tree
					
				}
			}
		});
		
		taxTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				// TODO: perform selection checks
				boolean okEnabled = (Math.random() > 0.5);
				// enable/disable 'OK' button
				okayBtn.setEnabled(okEnabled);
			}
		});
		
		okayBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				// TODO: build URI
				String tax = "ko";
				String map = "00250";
				String koIDs = "+K00813";
				String htmlFilePath = "http://www.genome.jp/kegg-bin/show_pathway?"
						+ tax + map + koIDs;
				URI uri = URI.create(htmlFilePath);
				try {
					// open URI in browser
					Desktop.getDesktop().browse(uri);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// add components to main container
		contentPane.add(pathPnl, CC.xy(2, 2));
		contentPane.add(taxPnl, CC.xy(4, 2));
		contentPane.add(buttonPnl, CC.xy(4, 4));
		
		// set the preferred size
		this.pack();
		this.setMinimumSize(this.getSize());
	}

	/**
	 * Returns the root node of the KEGG organism tree wrapped in a tree model.
	 * @return The KEGG organism tree model.
	 */
	private TreeModel initTaxonomyTreeModel() {
		TreeNode root = Parameters.getInstance().getKeggOrganismTreeRoot();
		TreeModel taxTreeMdl = new DefaultTreeModel(root);
		return taxTreeMdl;
	}
	
	/**
	 * Returns the root node of the KEGG pathway tree wrapped in a tree model.
	 * @return The KEGG organism tree model.
	 */
	private TreeModel initPathwayTreeModel() {
		TreeNode root = Parameters.getInstance().getKeggPathwayTreeRoot();
		TreeModel pathTreeMdl = new DefaultTreeModel(root);
		return pathTreeMdl;
	}

	/**
	 * Returns the Set of all KO numbers
	 * @return Set<String>. The Set of all Ko numbers.
	 */
	private Set<String> initKOs() {
		Set<String> koSet = new HashSet<String>();
		for (ProteinHit proteinHit : Client.getInstance().getDbSearchResult().getProteinHitList()) {
			koSet.addAll(proteinHit.getKoForKEGG());
		}
		return koSet;
	}

	private static HashSet<String> getPathsbyKeggID() throws ServiceException {
		// ProteinHitList
		final List<ProteinHit> proteinHitList = Client.getInstance().getDbSearchResult().getProteinHitList();
		// Set of all KeggIDs
		final Map<String, ProteinHit> keggIDMap = new HashMap<String, ProteinHit>();
		// Get Pathways
		final HashSet<String> getPathwaysByGenes = new HashSet<String>();

		// Create Connection to Kegg
		KEGGLocator locator = new KEGGLocator();
		KEGGPortType serv = locator.getKEGGPort();
		
		// Set ProgressMonitor for the use of the KEGGAPI
		Client client = Client.getInstance();
		client.firePropertyChange("resetall", null, (long) proteinHitList.size());
		client.firePropertyChange("resetcur", null, (long) proteinHitList.size());

		for (ProteinHit proteinHit : proteinHitList) {
			String[] pathways = proteinHit.getKeggPathways();
			// For first loading
			if (pathways == null) {
				String keggID = proteinHit.getKeggIDForKegg();
				if (keggID != null) {
					if (!keggIDMap.containsKey(keggID)) {
						keggIDMap.put(keggID, proteinHit);
						// try querying KEGG database
						try {
							pathways = serv.get_pathways_by_genes(new String[] { keggID } );
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						if (pathways != null) {
							proteinHit.setKeggPathways(pathways);
						}
					} else {
						proteinHit.setKeggPathways(keggIDMap.get(keggID).getKeggPathways());
					}
				}
			}

			// add pathways to global set
			if (pathways != null) {
				// strip off unnecessary characters if applicable
				for (int i = 0; i < pathways.length; i++) {
					String pw = pathways[i];
					if (pw.length() >= 13) {
						pw = pw.substring(8,13);
					}
					getPathwaysByGenes.add(pw);
				}
			}
			client.firePropertyChange("progressmade", 0L, 1L);
		}
		return getPathwaysByGenes;
	}

	public static void showKeggDialog(Component parentComponent) {
			new PathwayDialog();
	}
	
	public void derp() {
		Client.getInstance().firePropertyChange("new message", null, "BUILDING PATHWAY DIALOG");

		// Create panel for Kegg pathways
		JPanel pathwayPnl = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu,p, 5dlu, p, 5dlu"));
		pathwayPnl.setBorder(BorderFactory.createTitledBorder("Pathways"));

		// Create tree with pathways
		Map<String, Character> keggAllPathwayMap = Parameters.getInstance().getKeggPathwayMap();
		// Get List of all identified pathways
		HashSet<String> pathsByKeggID = null;
		try {
			pathsByKeggID = getPathsbyKeggID();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		// Show all Pathways in a Tree
		MutableTreeNode rootNode = new DefaultMutableTreeNode("<html><b>Pathways</b><html>");
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		DefaultMutableTreeNode levelA = null;
		DefaultMutableTreeNode levelB = null;
		DefaultMutableTreeNode levelC = null;
		levelA = new DefaultMutableTreeNode("01100 metabolic map");
		treeModel.insertNodeInto(levelA, rootNode, rootNode.getChildCount());

		for (Entry<String, Character>  entry : keggAllPathwayMap.entrySet()) {
			if (entry.getValue().equals('A')) {
				levelA = new DefaultMutableTreeNode(entry.getKey());
				treeModel.insertNodeInto(levelA, rootNode, rootNode.getChildCount());
			}
			if (entry.getValue().equals('B')) {
				levelB = new DefaultMutableTreeNode(entry.getKey());	
				treeModel.insertNodeInto(levelB, levelA, levelA.getChildCount());
			}
			if (entry.getValue().equals('C')) {
				if (pathsByKeggID.contains(entry.getKey().substring(0, 5))) {
					levelC = new DefaultMutableTreeNode(entry.getKey());	
					treeModel.insertNodeInto(levelC, levelB, levelB.getChildCount());
				}
			}
		}
		final JXTree tree = new JXTree(treeModel);
		tree.setSelectionRow(1);
		// ActionListener to denied Nodes of the pathways
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				String name = ((DefaultMutableTreeNode) tse.getPath().getLastPathComponent()).getUserObject().toString();
				if (name.charAt(0) != '0') {
					tree.setSelectionRow(1);
				}
			}
		});
		JScrollPane treeScpn = new JScrollPane(tree);
		treeScpn.setPreferredSize(new Dimension(400, 200));
		pathwayPnl.add(treeScpn, CC.xy(2, 2));

		// Create tree with taxons
		Map<String, Character> keggAllTaxonomiesMap = Parameters.getInstance().getKeggTaxonomyMap();
		// Show all Pathways in a Tree
		MutableTreeNode taxRootNode = new DefaultMutableTreeNode("<html><b>Taxons</b><html>");
		DefaultTreeModel taxTreeModel= new DefaultTreeModel(taxRootNode);
		DefaultMutableTreeNode taxLevelA = null;
		DefaultMutableTreeNode taxLevelB = null;
		DefaultMutableTreeNode taxLevelC = null;
		DefaultMutableTreeNode taxLevelD = null;
		DefaultMutableTreeNode taxLevelE = null;
		taxLevelA = new DefaultMutableTreeNode("map No defined taxonomy");
		taxTreeModel.insertNodeInto(taxLevelA, taxRootNode, taxRootNode.getChildCount());
		for (Entry<String, Character>  entry : keggAllTaxonomiesMap.entrySet()) {
			if (entry.getValue().equals('A')) {
				taxLevelA = new DefaultMutableTreeNode(entry.getKey());
				taxTreeModel.insertNodeInto(taxLevelA, taxRootNode, taxRootNode.getChildCount());
			}
			if (entry.getValue().equals('B')) {
				taxLevelB = new DefaultMutableTreeNode(entry.getKey());
				taxTreeModel.insertNodeInto(taxLevelB, taxLevelA, taxLevelA.getChildCount());
			}
			if (entry.getValue().equals('C')) {
				taxLevelC = new DefaultMutableTreeNode(entry.getKey());
				taxTreeModel.insertNodeInto(taxLevelC, taxLevelB, taxLevelB.getChildCount());
			}
			if (entry.getValue().equals('D')) {
				taxLevelD = new DefaultMutableTreeNode(entry.getKey());
				taxTreeModel.insertNodeInto(taxLevelD, taxLevelC, taxLevelC.getChildCount());
			}
			if (entry.getValue().equals('E')) {
				taxLevelE = new DefaultMutableTreeNode(entry.getKey());
				taxTreeModel.insertNodeInto(taxLevelE, taxLevelD, taxLevelD.getChildCount());
			}
		}
		final JXTree taxTree = new JXTree(taxTreeModel);
		// Action Listener to avoid Nodes as taxonomy
		taxTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				String name = ((DefaultMutableTreeNode) tse.getPath().getLastPathComponent()).getUserObject().toString();
				if ((name.charAt(3)!=' ')) {
					taxTree.setSelectionRow(1);
				}
				if (name.equals("Red algae (1)")) {
					taxTree.setSelectionRow(1);
				}
			}
		});
		taxTree.setSelectionRow(1);
		JScrollPane taxTreeScpn = new JScrollPane(taxTree);
		taxTreeScpn.setPreferredSize(new Dimension(400, 200));
		pathwayPnl.add(taxTreeScpn, CC.xy(2, 4));

		Client.getInstance().firePropertyChange("new message", null, "BUILDING PATHWAY DIALOG FINISHED");
		// Functionality for Kegg pathway
//		int dialogRes = JOptionPane.showConfirmDialog(parentComponent, pathwayPnl, "Choose KEGG pathway",  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//		// cancel == 2 & ok == 0
//		if (dialogRes == 0) {
//			// Create Webpage with Kegg database
//			URI uri = createUri(tree,taxTree);
//			try {
//				Desktop.getDesktop().browse(uri);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

//	/**
//	 * Method to create URI for link to Kegg
//	 * @param tree 
//	 * @return uri The URI for KEGG
//	 */
//	private static URI createUri(JXTree tree, JXTree taxtree) {
//
//		// Selected pathway
//		String map				= tree.getStringAt(tree.getSelectionPath());
//		map = map.trim();
//		map = map.substring(0, map.indexOf(" "));
//
//		// all KOs
//		Set<String> foundKo = initKOs();
//		// KOs for the selected pathway
//		// Create Connection to Kegg
//		KEGGLocator locator = new KEGGLocator();
//		Set<String> identifiedKos = new HashSet<String>();
//		try {
//			serv = locator.getKEGGPort();
//			getKosByPathway = serv.get_kos_by_pathway("path:map"+ map);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		// Get identified KOs for the choosen pathway
//		for (int i = 0; i < getKosByPathway.length; i++) {
//			String koPathway = getKosByPathway[i].substring(3,9);
//			if (foundKo.contains(koPathway)) {
//				identifiedKos.add(koPathway);
//			}
//		}
//		String koID = "";
//		for (String string : identifiedKos) {
//			koID+= "+" + string;
//		}
//
//		// Create taxonomy
//		String tax = taxtree.getStringAt(taxtree.getSelectionPath());
//		tax = tax.substring(0,3);
//		// Create URI
//		String htmlFilePath 	= "http://www.genome.jp/kegg-bin/show_pathway?"+tax +map + koID;
//		URI uri 				= URI.create(htmlFilePath);
//		return uri;
//	}
}