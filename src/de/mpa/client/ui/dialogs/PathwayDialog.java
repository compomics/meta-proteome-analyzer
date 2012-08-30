package de.mpa.client.ui.dialogs;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.xml.rpc.ServiceException;

import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

import org.jdesktop.swingx.JXTree;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.main.Parameters;

/**
 * This class initialize dialog for KEGG database
 * @author R. Heyer
 */
public class PathwayDialog {

	private static KEGGPortType serv;
	private static JScrollPane taxTreeScpn;
	private static JScrollPane treeScpn;
	private static HashSet<String> pathsbyKeggID;
	private static JXTree taxTree;
	private static JXTree tree;
	private static String[] getKosByPathway;

	public static void showKeggDialog(Component parentComponent) {
		Client.getInstance().firePropertyChange("new message", null, "BUILDING PATHWAY DIALOG");

		// Create panel for Kegg pathways
		JPanel pathwayPnl = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu,p, 5dlu, p, 5dlu"));
		pathwayPnl.setBorder(BorderFactory.createTitledBorder("Pathways"));

		// Create tree with pathways
		HashMap<String, String> keggAllPathwayMap = Parameters.getInstance().getKeggPathwayMap();
		// Get List of all identified pathways
		pathsbyKeggID = getPathsbyKeggID(parentComponent);
		// Show all Pathways in a Tree
		MutableTreeNode rootNode = new DefaultMutableTreeNode("<html><b>Pathways</b><html>");
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		DefaultMutableTreeNode levelA = null;
		DefaultMutableTreeNode levelB = null;
		DefaultMutableTreeNode levelC = null;
		levelA = new DefaultMutableTreeNode("01100 metabolic map");
		treeModel.insertNodeInto(levelA, rootNode, rootNode.getChildCount());
		
		for (Entry<String, String>  entry : keggAllPathwayMap.entrySet()) {
			if (entry.getValue().equals("A")) {
				levelA = new DefaultMutableTreeNode(entry.getKey());
				treeModel.insertNodeInto(levelA, rootNode, rootNode.getChildCount());
			}
			if (entry.getValue().equals("B")) {
				levelB = new DefaultMutableTreeNode(entry.getKey());	
				treeModel.insertNodeInto(levelB, levelA, levelA.getChildCount());
			}
			if (entry.getValue().equals("C")) {
				if (pathsbyKeggID.contains(entry.getKey().substring(0, 5))) {
					levelC = new DefaultMutableTreeNode(entry.getKey());	
					treeModel.insertNodeInto(levelC, levelB, levelB.getChildCount());
				}
			}
		}
		tree = new JXTree(treeModel);
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
		treeScpn = new JScrollPane(tree);
		treeScpn.setPreferredSize(new Dimension(400, 200));
		pathwayPnl.add(treeScpn, CC.xy(2, 2));

		// Create tree with taxons
		HashMap<String, String> keggAllTaxonomiesMap = Parameters.getInstance().getKeggTaxonomyMap();
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
		for (Entry<String, String>  entry : keggAllTaxonomiesMap.entrySet()) {
			if (entry.getValue().equals("A")) {
				taxLevelA = new DefaultMutableTreeNode(entry.getKey());
				taxTreeModel.insertNodeInto(taxLevelA, taxRootNode, taxRootNode.getChildCount());
			}
			if (entry.getValue().equals("B")) {
				taxLevelB = new DefaultMutableTreeNode(entry.getKey());
				taxTreeModel.insertNodeInto(taxLevelB, taxLevelA, taxLevelA.getChildCount());
			}
			if (entry.getValue().equals("C")) {
				taxLevelC = new DefaultMutableTreeNode(entry.getKey());
				taxTreeModel.insertNodeInto(taxLevelC, taxLevelB, taxLevelB.getChildCount());
			}
			if (entry.getValue().equals("D")) {
				taxLevelD = new DefaultMutableTreeNode(entry.getKey());
				taxTreeModel.insertNodeInto(taxLevelD, taxLevelC, taxLevelC.getChildCount());
			}
			if (entry.getValue().equals("E")) {
				taxLevelE = new DefaultMutableTreeNode(entry.getKey());
				taxTreeModel.insertNodeInto(taxLevelE, taxLevelD, taxLevelD.getChildCount());
			}
		}
		taxTree = new JXTree(taxTreeModel);
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
		taxTreeScpn = new JScrollPane(taxTree);
		taxTreeScpn.setPreferredSize(new Dimension(400, 200));
		pathwayPnl.add(taxTreeScpn, CC.xy(2, 4));

		Client.getInstance().firePropertyChange("new message", null, "BUILDING PATHWAY DIALOG FINISHED");
		// Functionality for Kegg pathway
		int dialogRes = JOptionPane.showConfirmDialog(parentComponent, pathwayPnl, "Choose KEGG pathway",  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		// cancel == 2 & ok == 0
		if (dialogRes == 0) {
			// Create Webpage with Kegg database
			URI uri = createUri(tree,taxTree);
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the Set of all KO numbers
	 * @return Set<String>. The Set of all Ko numbers.
	 */
	private static Set<String> getKO() {
		List<ProteinHit> proteinHitList = Client.getInstance().getDbSearchResult().getProteinHitList();
		Set<String> koSet = new HashSet<String>();
		for (ProteinHit proteinHit : proteinHitList) {
			koSet.addAll(proteinHit.getKoForKEGG());
		}
		return koSet;
	}

	/**
	 * Method to return all pathways
	 * @return 
	 * @return String[]
	 */
	private static  HashSet<String> getPathsbyKeggID(Component parentComponent) {
		// ProteinHitList
		final List<ProteinHit> proteinHitList = Client.getInstance().getDbSearchResult().getProteinHitList();
		// Set of all KeggIDs
		final Map<String, ProteinHit> keggIDMap = new HashMap<String, ProteinHit>();
		// Get Pathways
		final HashSet<String>getPathwaysByGenes = new HashSet<String>();

		// Create Connection to Kegg
		KEGGLocator locator = new KEGGLocator();
		try {
			serv = locator.getKEGGPort();
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}

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

/**
 * Method to create URI for link to Kegg
 * @param tree 
 * @return uri The URI for KEGG
 */
private static URI createUri(JXTree tree, JXTree taxtree) {

	// Selected pathway
	String map				= tree.getStringAt(tree.getSelectionPath());
	map = map.trim();
	map = map.substring(0, map.indexOf(" "));

	// all KOs
	Set<String> foundKo = getKO();
	// KOs for the selected pathway
	// Create Connection to Kegg
	KEGGLocator locator = new KEGGLocator();
	Set<String> identifiedKos = new HashSet<String>();
	try {
		serv = locator.getKEGGPort();
		getKosByPathway = serv.get_kos_by_pathway("path:map"+ map);
	} catch (Exception e) {
		e.printStackTrace();
	}
	// Get identified KOs for the choosen pathway
	for (int i = 0; i < getKosByPathway.length; i++) {
		String koPathway = getKosByPathway[i].substring(3,9);
		if (foundKo.contains(koPathway)) {
			identifiedKos.add(koPathway);
		}
	}
	String koID = "";
	for (String string : identifiedKos) {
		koID+= "+" + string;
	}

	// Create taxonomy
	String tax = taxtree.getStringAt(taxtree.getSelectionPath());
	tax = tax.substring(0,3);
	// Create URI
	String htmlFilePath 	= "http://www.genome.jp/kegg-bin/show_pathway?"+tax +map + koID;
	URI uri 				= URI.create(htmlFilePath);
	return uri;
}
}