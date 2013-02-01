package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.tree.TreePath;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.DenovoSearchSettings;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTitledBorder;

public class DeNovoSearchPanel extends JPanel {

	private JComboBox dnEnzymesCbx;
	private JComboBox dnModelCbx;
	private JSpinner dnFragTolSpn;
	private JSpinner dnPrecTolSpn;
	private JSpinner dnNumSolutionsSpn;
	private JPanel parPnl;
	private JScrollPane ptmScp;
	private JPanel ptmPnl;
	private CheckBoxTreeTable ptmTree;
	
	/**
	 * Constructs a panel containing controls for de novo search settings.
	 */
	public DeNovoSearchPanel() {
		initComponents();
	}

	/**
	 * Method to initialize the panel's components
	 */
	private void initComponents() {
		
		CellConstraints cc = new CellConstraints();
		
		this.setLayout(new FormLayout("7dlu, p:g, 7dlu",
				"5dlu, f:p, 5dlu, f:p:g, 7dlu"));

		// Parameters		
		parPnl = new JPanel();
		parPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",		// col
										"0dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p"));	// row
		parPnl.setBorder(new ComponentTitledBorder(new JLabel("Parameters"), parPnl));

		// Enzymes
		dnEnzymesCbx = new JComboBox(Constants.DN_ENZYMES);
		dnEnzymesCbx.setToolTipText("Choose the enzyme of the protein digest");

		// MS
		dnModelCbx=new JComboBox(Constants.DN_MODELS);
		dnModelCbx.setToolTipText("Select the fragmentation model.");

		// Fragment tolerance
		dnFragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, null, 0.1));
		dnFragTolSpn.setEditor(new JSpinner.NumberEditor(dnFragTolSpn, "0.0"));
		dnFragTolSpn.setToolTipText("Choose the fragment mass tolerance.");

		// Threshold peptides
		dnPrecTolSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, null, 0.1));
		dnPrecTolSpn.setEditor(new JSpinner.NumberEditor(dnPrecTolSpn, "0.0"));
		dnPrecTolSpn.setToolTipText("Choose the precursor mass tolerance.");

		// Maximum number of peptides
		dnNumSolutionsSpn = new JSpinner(new SpinnerNumberModel(10, 0, null, 1));
		dnNumSolutionsSpn.setToolTipText("Select the maximum number of peaks for de novo sequencing ");
		
		parPnl.add(new JLabel("Enzyme:"), cc.xy(2, 2));
		parPnl.add(dnEnzymesCbx,cc.xyw(4, 2, 5));
		parPnl.add(new JLabel("Model:"),cc.xy(2, 4));
		parPnl.add(dnModelCbx,cc.xyw(4, 4, 5));
		parPnl.add(new JSeparator(), cc.xyw(2, 6, 7));
		parPnl.add(new JLabel("Precursor Mass Tolerance:"), cc.xyw(2, 8, 3));
		parPnl.add(dnPrecTolSpn,cc.xy(6, 8));
		parPnl.add(new JLabel("Da"),cc.xy(8, 8));
		parPnl.add(new JLabel("Fragment Mass Tolerance:"),cc.xyw(2, 10, 3));
		
		parPnl.add(dnFragTolSpn,cc.xy(6, 10));
		parPnl.add(new JLabel("Da"),cc.xy(8, 10));
		parPnl.add(new JLabel("No. Solutions (max):"),cc.xyw(2, 12, 3));
		parPnl.add(dnNumSolutionsSpn,cc.xy(6, 12));

		// Panel PTMs
		ptmPnl = new JPanel();
		ptmPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
										 "0dlu, f:p:g, 5dlu"));	// row
		ptmPnl.setBorder(new ComponentTitledBorder(new JLabel("Modifications"), ptmPnl));
		
		CheckBoxTreeTableNode ptmRoot = buildPTMtree();
		
		ptmTree = new CheckBoxTreeTable(ptmRoot);
		ptmTree.setRootVisible(false);
		ptmTree.setShowsRootHandles(true);
		ptmTree.setTableHeader(null);
		
		for (int i = 0; i < ptmRoot.getChildCount(); i++) {
			TreePath path = new TreePath(new Object[] { ptmRoot, ptmRoot.getChildAt(i) });
			ptmTree.expandPath(path);
		}
		
		CheckBoxTreeSelectionModel cbtsm = ptmTree.getCheckBoxTreeSelectionModel();
		cbtsm.addSelectionPath(ptmRoot.getFirstLeaf().getPath());
		
		ptmScp = new JScrollPane(ptmTree);
		// dnPTMscp.setPreferredSize(new Dimension(0, 0));
		ptmScp.setPreferredSize(new Dimension(200, 100));
		ptmScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		ptmScp.setToolTipText("Choose possible PTMs");
		ptmScp.setEnabled(false);
		ptmPnl.add(ptmScp,cc.xy(2,2));

		// add panels
		this.add(parPnl, cc.xy(2, 2));
		this.add(ptmPnl, cc.xy(2, 4));
	}
	
	/**
	 * Method to build tree of possible PepNovo+ PTMs.
	 * 
	 * @return The root node of the newly created tree.
	 */
	private CheckBoxTreeTableNode buildPTMtree() {
		
		CheckBoxTreeTableNode ptmRoot = new CheckBoxTreeTableNode();
		
		// internal PTMs
		CheckBoxTreeTableNode internal = new CheckBoxTreeTableNode("Internal");
		
		CheckBoxTreeTableNode cam = new CheckBoxTreeTableNode("Carbamidomethyl");
		cam.add(new CheckBoxTreeTableNode("C+57", true));
		for (char aa : "HDE".toCharArray())
			cam.add(new CheckBoxTreeTableNode(aa + "+57"));
		CheckBoxTreeTableNode lcam = new CheckBoxTreeTableNode("Loss-Carbamidomethyl");
		lcam.add(new CheckBoxTreeTableNode("C-57"));
		CheckBoxTreeTableNode form = new CheckBoxTreeTableNode("Formaldehyde");
		form.add(new CheckBoxTreeTableNode("W+12"));
		CheckBoxTreeTableNode kynu = new CheckBoxTreeTableNode("Kynurenin-Oxidation");
		kynu.add(new CheckBoxTreeTableNode("W+4"));
		CheckBoxTreeTableNode pot = new CheckBoxTreeTableNode("Potassium");
		for (char aa : "SKNPLRVIEMDGA".toCharArray())
			pot.add(new CheckBoxTreeTableNode(aa + "+38"));
		CheckBoxTreeTableNode t2r = new CheckBoxTreeTableNode("Thr/Arg-Substitution");
		t2r.add(new CheckBoxTreeTableNode("R+55"));
		CheckBoxTreeTableNode soad = new CheckBoxTreeTableNode("Sodium Adduct");
		for (char aa : "QVPNYTHDESFAMILG".toCharArray())
			soad.add(new CheckBoxTreeTableNode(aa + "+22"));
		CheckBoxTreeTableNode acet = new CheckBoxTreeTableNode("Acetylation");
		for (char aa : "LTSYGVMPDAK".toCharArray())
			acet.add(new CheckBoxTreeTableNode(aa + "+42"));
		CheckBoxTreeTableNode carb = new CheckBoxTreeTableNode("Carbamylation");
		for (char aa : "PANMETGDLFIQSVK".toCharArray())
			carb.add(new CheckBoxTreeTableNode(aa + "+43"));
		CheckBoxTreeTableNode dehy = new CheckBoxTreeTableNode("Dehydration");
		for (char aa : "EDST".toCharArray())
			dehy.add(new CheckBoxTreeTableNode(aa + "-18"));
		CheckBoxTreeTableNode dime = new CheckBoxTreeTableNode("Dimethylation");
		dime.add(new CheckBoxTreeTableNode("K+28"));
		CheckBoxTreeTableNode diox = new CheckBoxTreeTableNode("Dioxidation");
		for (char aa : "MW".toCharArray())
			diox.add(new CheckBoxTreeTableNode(aa + "+32"));
		CheckBoxTreeTableNode fory = new CheckBoxTreeTableNode("Formylation");
		for (char aa : "ST".toCharArray())
			fory.add(new CheckBoxTreeTableNode(aa + "+28"));
		CheckBoxTreeTableNode oxid = new CheckBoxTreeTableNode("Oxidation");
		for (char aa : "MHW".toCharArray())
			oxid.add(new CheckBoxTreeTableNode(aa + "+16"));
		CheckBoxTreeTableNode hydr = new CheckBoxTreeTableNode("Hydroxylation");
		for (char aa : "DKNP".toCharArray())
			hydr.add(new CheckBoxTreeTableNode(aa + "+16"));
		CheckBoxTreeTableNode deam = new CheckBoxTreeTableNode("Deamidation");
		for (char aa : "NQ".toCharArray())
			deam.add(new CheckBoxTreeTableNode(aa + "+1"));
		CheckBoxTreeTableNode meth = new CheckBoxTreeTableNode("Methylation");
		for (char aa : "CHKNQR".toCharArray())
			meth.add(new CheckBoxTreeTableNode(aa + "+14"));
		CheckBoxTreeTableNode phos = new CheckBoxTreeTableNode("Phosphorylation");
		for (char aa : "STY".toCharArray())
			phos.add(new CheckBoxTreeTableNode(aa + "+80"));
		CheckBoxTreeTableNode pyro = new CheckBoxTreeTableNode("Pyro-Glu");
		pyro.add(new CheckBoxTreeTableNode("Q-17"));
		CheckBoxTreeTableNode succ = new CheckBoxTreeTableNode("N-Succinimide");
		succ.add(new CheckBoxTreeTableNode("N-17"));

		internal.add(cam);	internal.add(lcam);	internal.add(form);
		internal.add(kynu);	internal.add(pot);	internal.add(t2r);
		internal.add(soad);	internal.add(acet);	internal.add(carb);
		internal.add(dehy);	internal.add(dime);	internal.add(diox);
		internal.add(fory);	internal.add(oxid);	internal.add(hydr);
		internal.add(deam);	internal.add(meth);	internal.add(phos);
		internal.add(pyro);	internal.add(succ);
		
		// C-terminal PTMs
		CheckBoxTreeTableNode cterm = new CheckBoxTreeTableNode("C-Terminal");
		
		CheckBoxTreeTableNode cami = new CheckBoxTreeTableNode("C-Amidation");
		cami.add(new CheckBoxTreeTableNode("$-1"));
		CheckBoxTreeTableNode o18 = new CheckBoxTreeTableNode("O18-Labeling");
		o18.add(new CheckBoxTreeTableNode("$+2"));
		
		cterm.add(cami);
		cterm.add(o18);
		
		// N-terminal PTMs
		CheckBoxTreeTableNode nterm = new CheckBoxTreeTableNode("N-Terminal");

		CheckBoxTreeTableNode nmet = new CheckBoxTreeTableNode("N-Methylation");
		nmet.add(new CheckBoxTreeTableNode("^+14"));
		CheckBoxTreeTableNode nace = new CheckBoxTreeTableNode("N-Acetylation");
		nace.add(new CheckBoxTreeTableNode("^+42"));
		CheckBoxTreeTableNode ntcam = new CheckBoxTreeTableNode("NT+CAM");
		ntcam.add(new CheckBoxTreeTableNode("^+57"));
		
		nterm.add(nmet);
		nterm.add(nace);
		nterm.add(ntcam);
		
		// add everything to root
		ptmRoot.add(internal);
		ptmRoot.add(cterm);
		ptmRoot.add(nterm);
		
		return ptmRoot;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (Component child : parPnl.getComponents()) {
			child.setEnabled(enabled);
		}
		for (Component child : ptmScp.getComponents()) {
			child.setEnabled(enabled);
		}
		((ComponentTitledBorder)parPnl.getBorder()).setEnabled(enabled);
		((ComponentTitledBorder)ptmPnl.getBorder()).setEnabled(enabled);
		ptmTree.setEnabled(enabled);
	}
	
	/**
	 * This method collects the de-novo settings.
	 * @return The DenovoSearchSettings object sent through the webservice.
	 */
	public DenovoSearchSettings collectDenovoSettings(){
		DenovoSearchSettings dnSettings = new DenovoSearchSettings();
		// Set the current experiment id for the database search settings.
		dnSettings.setExperimentid(ClientFrame.getInstance().getProjectPanel().getCurrentExperimentId());
		dnSettings.setEnzyme(dnEnzymesCbx.getSelectedItem().toString());
		dnSettings.setModel(dnModelCbx.getSelectedItem().toString());
		dnSettings.setFragMassTol((Double) dnFragTolSpn.getValue());
		dnSettings.setPrecursorTol((Double)dnPrecTolSpn.getValue() );
		dnSettings.setNumSolutions((Integer) dnNumSolutionsSpn.getValue());
		StringBuilder mods = new StringBuilder();
		CheckBoxTreeTableNode ptmRoot = (CheckBoxTreeTableNode) ptmTree.getTreeTableModel().getRoot();
		CheckBoxTreeTableNode ptmLeaf = ptmRoot.getFirstLeaf();
		while (ptmLeaf != null) {
			mods.append(ptmLeaf.getUserObject().toString());
			mods.append(":");
			ptmLeaf = ptmLeaf.getNextLeaf();
		}
		mods.deleteCharAt(mods.length()-1);	// remove final delimiter character
		dnSettings.setMods(mods.toString());
		return dnSettings;
	}	
}
