package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import scala.actors.threadpool.Arrays;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTableHeader;
import de.mpa.client.ui.ComponentHeaderRenderer;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.SortableCheckBoxTreeTable;
import de.mpa.client.ui.SortableCheckBoxTreeTableNode;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.icons.IconConstants;

/**
 * Dialog implementation showing a checkbox tree view for selecting sub-trees.
 * 
 * @author R. Heyer, A. Behne
 */
public class TreeSelectionDialog extends JDialog {

	/**
	 * The tree table component of this dialog.
	 */
	protected CheckBoxTreeTable treeTbl;
	
	/**
	 * The cached checkbox tree selection paths.
	 */
	protected TreePath[] selPaths;

	/**
	 * Constructs a tree selection dialog from the specified checkbox tree table root node.
	 * @param owner the parent frame of the dialog
	 * @param title the title of the dialog
	 * @param modal the modality of the dialog
	 * @param root the root node of the tree to display
	 */
	public TreeSelectionDialog(Frame owner, String title, boolean modal, CheckBoxTreeTableNode root) {
		super(owner, title, modal);
		
		// initialize components
		this.initComponents(root);
		
		// configure dialog
		this.setIconImage(owner.getIconImage());
		this.pack();
		this.setMinimumSize(this.getPreferredSize());
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// reset and dispose
				cancelDialog();
			}
		});
		
		ScreenConfig.centerInComponent(this, ClientFrame.getInstance());
	}

	/**
	 * Initializes and lays out the dialog's components.
	 * @param root the root node of the tree view
	 */
	public void initComponents(final CheckBoxTreeTableNode root) {
		
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new FormLayout(
				"5dlu, 250px:g, 5dlu",
				"5dlu, f:p:g, 5dlu, b:p, 5dlu"));
		
		/* Configure top tree panel */
		JPanel treePnl = new JPanel(new FormLayout(
				"4dlu, p:g, 4dlu",
				"0dlu, f:p:g, 4dlu"));
		treePnl.setBorder(BorderFactory.createTitledBorder("Selection Tree"));
		
		SortableTreeTableModel treeMdl = new SortableTreeTableModel(root);
		treeMdl.setColumnIdentifiers(Arrays.asList(new String[] { root.getUserObject().toString() }));
		treeMdl.setHideEmpty(false);
		
		treeTbl = new SortableCheckBoxTreeTable(treeMdl);
		treeTbl.setIndents(6, 4, 2);
		TableConfig.configureColumnControl(treeTbl);
		
		CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
		cbtsm.setSelectionPath(root.getPath());
		cbtsm.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				// check whether root selection has been affected
				if (evt.getPath().getPathCount() == 1) {
					// update header checkbox selection via model to bypass action listener
					TableColumnExt column = treeTbl.getColumnExt(treeTbl.convertColumnIndexToView(0));
					ComponentHeaderRenderer renderer = (ComponentHeaderRenderer) column.getHeaderRenderer();
					// sanity check
					if (renderer != null) {
						JCheckBox selChk = (JCheckBox) renderer.getComponent();
						selChk.getModel().setSelected(evt.isAddedPath());
					}
				}
				// repaint header to show updated header checkbox state
				treeTbl.getTableHeader().repaint(treeTbl.getTableHeader().getHeaderRect(0));
			}
		});

		treeTbl.setTableHeader(new ComponentTableHeader(treeTbl.getColumnModel()));
		
		JScrollPane treeScpn = new JScrollPane(treeTbl);
		treeScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		treePnl.add(treeScpn, CC.xy(2, 2));
		
		/* Configure bottom button panel */
		JPanel buttonPnl = new JPanel(new FormLayout("0px:g, p, 5dlu, p, 1px", "p"));
		((FormLayout) buttonPnl.getLayout()).setColumnGroups(new int[][] { { 2, 4 } });
		
		// TODO: maybe create help button (lightbulb or question mark icon) displaying bubble popup containing some helpful instructions
		
		// Create button to accept changes and close the dialog
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// simply close the dialog
				dispose();
			}
		});
		
		// Create button to dismiss changes and close the dialog
		JButton cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		cancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// reset and dispose
				cancelDialog();
			}
		});

		buttonPnl.add(okBtn, CC.xy(2, 1));
		buttonPnl.add(cancelBtn, CC.xy(4, 1));
		
		// add everything to content pane
		contentPane.add(treePnl, CC.xy(2, 2));
		contentPane.add(buttonPnl, CC.xy(2, 4));

	}
	
	/**
	 * Inserts a new root into the dialog's tree table.
	 * @param root the root to set
	 */
	public void setRoot(SortableCheckBoxTreeTableNode root) {
		// insert root
		((DefaultTreeTableModel) this.treeTbl.getTreeTableModel()).setRoot(root);
		// select whole tree
		this.treeTbl.getCheckBoxTreeSelectionModel().setSelectionPath(root.getPath());
	}
	
	@Override
	public void setVisible(boolean b) {
		this.cachePaths();
		super.setVisible(b);
	}
	
	/**
	 * Updates the checkbox tree selection cache.
	 */
	protected void cachePaths() {
		// cache selection state
		this.selPaths = this.treeTbl.getCheckBoxTreeSelectionModel().getSelectionPaths();
	}
	
	/**
	 * Resets the dialog's tree checkbox selection paths.
	 */
	protected void resetPaths() {
		// reset selection state to cached value
		this.treeTbl.getCheckBoxTreeSelectionModel().setSelectionPaths(this.selPaths);
	}
	
	/**
	 * Resets the checkbox tree selection state and disposes the dialog.
	 */
	protected void cancelDialog() {
		this.resetPaths();
		this.dispose();
	}
	
	/**
	 * Returns the checkbox tree selection paths.
	 * @return the selection paths
	 */
	public TreePath[] getSelectionPaths() {
		TreePath[] selPaths = this.treeTbl.getCheckBoxTreeSelectionModel().getSelectionPaths();
		TreePath[] resPaths = null;
		if (selPaths != null) {
			resPaths = new TreePath[selPaths.length];
			// iterate selection paths
			for (int i = 0; i < selPaths.length; i++) {
				TreePath selPath = selPaths[i];
				Object[] resPath = new Object[selPath.getPathCount()];
				// clone selection path using the underlying user objects instead
				for (int j = 0; j < resPath.length; j++) {
					// FIXME: Exception in thread "AWT-EventQueue-0" java.lang.ClassCastException: java.lang.String cannot be cast to org.jdesktop.swingx.treetable.TreeTableNode
					resPath[j] = ((TreeTableNode) selPath.getPathComponent(j)).getUserObject();
				}
				resPaths[i] = new TreePath(resPath);
			}
		}
		return resPaths;
	}
	
}
