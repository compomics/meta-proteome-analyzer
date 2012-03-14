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

import de.mpa.client.ui.ComponentTitledBorder;
import de.mpa.client.ui.Constants;

public class DeNovoSearchPanel extends JPanel {


	private JComboBox dnEnzymesCbx;
	private JComboBox dnMSCbx;
	private JSpinner dnFragTolSpn;
	private JSpinner dnThresholdSpn;
	private JSpinner dnNumSolutionsSpn;
//	private JCheckBox dnPepknownChx;
	private JTable dnPTMtbl;
	private JPanel parPnl;
	private JScrollPane dnPTMscp;
	private JPanel ptmsPnl;

	public DeNovoSearchPanel() {
		initComponents();
	}

	/**
	 * Method to initialize the panel's components
	 */
	private void initComponents() {
		
		CellConstraints cc = new CellConstraints();
		
		this.setLayout(new FormLayout("7dlu, p, 7dlu",					// col
									  "0dlu, f:p, 5dlu, f:p:g, 7dlu"));	// row

		// Parameters		
		parPnl = new JPanel();
		parPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",		// col
										"0dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p"));	// row
		parPnl.setBorder(new ComponentTitledBorder(new JLabel("Parameters"), parPnl));

		// Enzymes
		dnEnzymesCbx = new JComboBox(Constants.DN_ENZYMES);
		dnEnzymesCbx.setToolTipText("Choose the enzyme of the protein digest");
		//dnEnzymesCbx.setRenderer(new RightAlignListCellRenderer());

		// MS
		dnMSCbx=new JComboBox(Constants.MASS_SPECTROMETERS);
		dnMSCbx.setToolTipText("Select your mass spectrometer");
		//dnMS.setRenderer(new RightAlignListCellRenderer());

		// Fragment tolerance
		dnFragTolSpn = new JSpinner(new SpinnerNumberModel(0.3, 0.0, null, 0.05));
		dnFragTolSpn.setEditor(new JSpinner.NumberEditor(dnFragTolSpn, "0.00"));
		dnFragTolSpn.setToolTipText("Choose your fragment mass tolerance");

		// for right aligned text in spinners in windows LAF
//		private class RightAlignListCellRenderer extends JLabel implements ListCellRenderer<String> {
//			@Override
//			public Component getListCellRendererComponent(
//					JList<? extends String> list, String value, int index,
//					boolean isSelected, boolean cellHasFocus) {
//				this.setText(value);
//				this.setHorizontalAlignment(JLabel.RIGHT);
//				return this;
//			}
//		}

		// Threshold peptides
		dnThresholdSpn = new JSpinner(new SpinnerNumberModel(1000, 0, null, 1));
		dnThresholdSpn.setToolTipText("Apply peptide threshold");

		// Maximum number of peptides
		dnNumSolutionsSpn = new JSpinner(new SpinnerNumberModel(10, 0, null, 1));
		dnNumSolutionsSpn.setToolTipText("Select the maximum number of peaks for de novo sequencing ");

//		dnPepknownChx = new JCheckBox("Remove known peptides");
//		dnPepknownChx.setToolTipText("Remove all identified peptides");

		
		parPnl.add(new JLabel("Protease"), cc.xy(2, 2));
		parPnl.add(dnEnzymesCbx,cc.xyw(4, 2, 5));
		parPnl.add(new JLabel("Spectrometer"),cc.xy(2, 4));
		parPnl.add(dnMSCbx,cc.xyw(4, 4, 5));
		parPnl.add(new JSeparator(), cc.xyw(2, 6, 7));
		parPnl.add(new JLabel("Fragment mass tolerance"), cc.xyw(2, 8, 3));
		parPnl.add(dnFragTolSpn,cc.xy(6, 8));
		parPnl.add(new JLabel("Da"),cc.xy(8, 8));
		parPnl.add(new JLabel("Peptide intensity threshold"),cc.xyw(2, 10, 3));
		parPnl.add(dnThresholdSpn,cc.xy(6, 10));
		parPnl.add(new JLabel("Number of peptides"),cc.xyw(2, 12, 3));
		parPnl.add(dnNumSolutionsSpn,cc.xy(6, 12));
//		parPnl.add(dnPepknownChx,cc.xyw(2, 14, 5));

		// Panel PTMs
		ptmsPnl = new JPanel();
		ptmsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
										 "0dlu, f:p:g, 5dlu"));	// row
		ptmsPnl.setBorder(new ComponentTitledBorder(new JLabel("PTMs"), ptmsPnl));

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

		
//		// Start panel
//		JPanel statusPnl = new JPanel();
//		statusPnl.setLayout(new FormLayout("5dlu, p, 15dlu, p, 5dlu, p:g, 5dlu",	// col
//				"p, 5dlu,"));							// row
//		statusPnl.setBorder(new TitledBorder("Search status"));
//		// Start button
//		dnStartBtn = new JButton("Run de-novo search");
//		dnStartBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				dnStartBtn.setEnabled(false);
//				RunDenovoSearchWorker worker = new RunDenovoSearchWorker();
//				worker.execute();
//
//				// Easter egg stuff
//				Image image = null;
//				try {
//					image = ImageIO.read(new File("docu/Nerds.jpg"));
//
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				JLabel label = new JLabel("Das passiert wenn man wahllos Knoepfe drueckt!!!", new ImageIcon(image),JLabel.CENTER);
//				label.setVerticalTextPosition(JLabel.BOTTOM);
//				label.setHorizontalTextPosition(JLabel.CENTER);
//				JOptionPane.showMessageDialog(clientFrame, label,"Erwischt!!", JOptionPane.PLAIN_MESSAGE);
//			}
//		});
//		statusPnl.add(dnStartBtn,cc.xy(2, 1));
//		// Progress bar
//		statusPnl.add(new JLabel("Progress"),cc.xy(4, 1));
//		JProgressBar denovoPrg = new JProgressBar(0,100);
//		denovoPrg.setStringPainted(true);
//		denovoPrg.setValue(0);
//		statusPnl.add(denovoPrg, cc.xy(6, 1));

		// add panels
		this.add(parPnl, cc.xy(2, 2));
		this.add(ptmsPnl, cc.xy(2, 4));
//		this.add(statusPnl, cc.xyw(2, 4, 3));
	}
	
	@Override
	public void setEnabled(boolean enabled) {
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

//	private DenovoSearchSettings collectDenovoSettings(){
//		DenovoSearchSettings settings = new DenovoSearchSettings();
//		settings.setDnEnzyme(dnEnzymesCbx.getSelectedItem().toString());
//		settings.setDnMS(dnMSCbx.getSelectedItem().toString());
//		settings.setDnFragmentTolerance((Double) dnFragTolSpn.getValue());
//		settings.setDnPeptideIntThresh((Integer)dnThresholdSpn.getValue() );
//		settings.setDnNumSolutions((Integer) dnNumSolutionsSpn.getValue());
//		settings.setDnRemoveAllPep((boolean) dnPepknownChx.isSelected());
//		String mods = "";
//		for (int row = 0; row < dnPTMtbl.getRowCount(); row++) {
//			if ((Boolean) dnPTMtbl.getValueAt(row, 1)){
//				mods += (String) dnPTMtbl.getValueAt(row, 0);
//			}
//		}
//		settings.setDnPTMs(mods);
//		return settings;
//	}	

//	/**
//	 * RunDBSearchWorker class extending SwingWorker.
//	 * @author Thilo Muth
//	 *
//	 */
//	private class RunDenovoSearchWorker	extends SwingWorker {
//
//		protected Object doInBackground() throws Exception {
//			DenovoSearchSettings settings = collectDenovoSettings();
//			List<File> chunkedFiles = client.packFiles(1000, clientFrame.getFilePanel().getCheckBoxTree(), "test");
//			client.sendFiles(chunkedFiles);
//			client.runDenovoSearch(chunkedFiles, settings);
//			return 0;
//		}
//
//		@Override
//		public void done() {
//			dnStartBtn.setEnabled(true);
//		}
//	}

}
