package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.DenovoSearchSettings;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTitledBorder;
import de.mpa.client.ui.Constants;

public class DeNovoSearchPanel extends JPanel {

	private JComboBox dnEnzymesCbx;
	private JComboBox dnModelCbx;
	private JSpinner dnFragTolSpn;
	private JSpinner dnPrecTolSpn;
	private JSpinner dnNumSolutionsSpn;
	private JTable dnPTMtbl;
	private JPanel parPnl;
	private JScrollPane dnPTMscp;
	private JPanel ptmsPnl;
	private ClientFrame clientFrame;
	
	/**
	 * The default de-novo search panel constructor.
	 */
	public DeNovoSearchPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
	}

	/**
	 * Method to initialize the panel's components
	 */
	private void initComponents() {
		
		CellConstraints cc = new CellConstraints();
		
		this.setLayout(new FormLayout("7dlu, p, 7dlu", "0dlu, f:p, 5dlu, f:p:g, 7dlu"));

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
		ptmsPnl = new JPanel();
		ptmsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
										 "0dlu, f:p:g, 5dlu"));	// row
		ptmsPnl.setBorder(new ComponentTitledBorder(new JLabel("Modifications"), ptmsPnl));

		DefaultTableModel model = new DefaultTableModel(new Object[] {"PTM", ""}, 0) {
			public Class<?> getColumnClass(int c) {	// method allows checkboxes in table
				return getValueAt(0,c).getClass();
			}
			public boolean isCellEditable(int row, int col) { // only allow editing of checkboxes
				return ((col == 1) ? true : false);
			}
		};
		for (int i = 0; i < Constants.PTMS.length; i++) {
			model.addRow(new Object[] {Constants.PTMS[i], false});
		}
		// make table components able to appear disabled when table itself is disabled
		dnPTMtbl = new JTable(model) {
			public Component prepareRenderer(final TableCellRenderer renderer, int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				comp.setEnabled(isEnabled());	// Enable/disable renderer same as table.
				return comp;
			}
		};
		final TableCellRenderer tcr = dnPTMtbl.getTableHeader().getDefaultRenderer();
		dnPTMtbl.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp = tcr.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				comp.setEnabled(dnPTMtbl.isEnabled());
				return comp;
			}
		});
		dnPTMtbl.getTableHeader().setReorderingAllowed(false);

		dnPTMtbl.getColumnModel().getColumn(1).setMaxWidth(dnPTMtbl.getColumnModel().getColumn(1).getMinWidth());
		dnPTMtbl.setShowVerticalLines(false);
		dnPTMscp = new JScrollPane(dnPTMtbl);
		//		dnPTMscp.setPreferredSize(new Dimension(0, 0));
		dnPTMscp.setPreferredSize(new Dimension(200, 100));
		dnPTMscp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		dnPTMscp.setToolTipText("Choose possible PTMs");
		dnPTMscp.setEnabled(false);
		ptmsPnl.add(dnPTMscp,cc.xy(2,2));

		// add panels
		this.add(parPnl, cc.xy(2, 2));
		this.add(ptmsPnl, cc.xy(2, 4));
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (Component child : parPnl.getComponents()) {
			child.setEnabled(enabled);
		}
		for (Component child : dnPTMscp.getComponents()) {
			child.setEnabled(enabled);
		}
		((ComponentTitledBorder)parPnl.getBorder()).setEnabled(enabled);
		((ComponentTitledBorder)ptmsPnl.getBorder()).setEnabled(enabled);
		dnPTMtbl.setEnabled(enabled);
		dnPTMtbl.setBackground((enabled) ? UIManager.getColor("Table.background") : null);
	}
	
	/**
	 * This method collects the de-novo settings.
	 * @return The DenovoSearchSettings object sent through the webservice.
	 */
	public DenovoSearchSettings collectDenovoSettings(){
		DenovoSearchSettings dnSettings = new DenovoSearchSettings();
		// Set the current experiment id for the database search settings.
		dnSettings.setExperimentid(clientFrame.getProjectPnl().getCurrentExperimentId());
		dnSettings.setEnzyme(dnEnzymesCbx.getSelectedItem().toString());
		dnSettings.setModel(dnModelCbx.getSelectedItem().toString());
		dnSettings.setFragMassTol((Double) dnFragTolSpn.getValue());
		dnSettings.setPrecursorTol((Double)dnPrecTolSpn.getValue() );
		dnSettings.setNumSolutions((Integer) dnNumSolutionsSpn.getValue());
		String mods = "";
		for (int row = 0; row < dnPTMtbl.getRowCount(); row++) {
			if ((Boolean) dnPTMtbl.getValueAt(row, 1)){
				mods += (String) dnPTMtbl.getValueAt(row, 0);
			}
		}
		dnSettings.setMods(mods);
		return dnSettings;
	}	
}
