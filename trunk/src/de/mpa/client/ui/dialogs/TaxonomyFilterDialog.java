package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.taxonomy.TaxonomyNode;

/** Dialog to define a overall taxonomy chart
 * @author R. Heyer
 */
public class TaxonomyFilterDialog extends JDialog{

	/** 
	 * The model of the taxonomy tree.
	 */
	private DefaultTreeModel taxModel;
	
	/** 
	 * The taxonomy ID of the choosen filter, standard value is 1 for root.
	 */
	private long filterID = 1; 
	/**
	 *  Standard constructor
	 * @param taxModel. The model of the taxonomy tree.
	 */
	public TaxonomyFilterDialog(DefaultTreeModel 	taxModel, ClientFrame clientFrame){
		super(clientFrame, "Choose taxonomic group to include.", true); // Connection to parent panel
		this.taxModel = taxModel;
		initComponents();
	}

	/**
	 * Initialize components.
	 */
	public void initComponents(){
		JPanel taxDialogPnl = new JPanel();
		taxDialogPnl.setBorder(BorderFactory.createTitledBorder("Taxonomy Filter"));
		taxDialogPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, p:g, 5dlu, p:g, 5dlu"));
		// Create tree and scroll pane
		final JTree taxTree = new JTree(taxModel);
		taxTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		taxTree.setSelectionRow(0);// Root is selected as standard
		taxTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
			    DefaultMutableTreeNode node = (DefaultMutableTreeNode)taxTree.getLastSelectedPathComponent();
			    TaxonomyNode taxNode = (TaxonomyNode) node.getUserObject();
			    filterID = taxNode.getId();
			}
		});
		JScrollPane taxTreeScp = new JScrollPane(taxTree);
		taxTreeScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		taxTreeScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		taxTreeScp.setPreferredSize(new Dimension(350,300));
		taxDialogPnl.add(taxTreeScp, CC.xyw(2, 2,3));
		
		// Define Dialog
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		JButton cancelBtn = new JButton("CANCEL", IconConstants.CANCEL_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CANCEL_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.CANCEL_PRESSED_ICON);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		taxDialogPnl.add(okBtn, CC.xy(2, 4));
		taxDialogPnl.add(cancelBtn, CC.xy(4, 4));
		
		// Add panels to the dialog content pane
		Container contentPane = this.getContentPane();
		contentPane.setPreferredSize(new Dimension(400, 400));
		contentPane.add(taxDialogPnl);
	}
	
	/**
	 * This method shows the dialog.
	 */
	public long showDialog() {
		// Configure size and position
		this.pack();
		ScreenConfig.centerInScreen(this);
		this.setResizable(false);
		// Show dialog
		this.setVisible(true);
		// Request focus
		this.requestFocus();
		
		return filterID;
	}
}
