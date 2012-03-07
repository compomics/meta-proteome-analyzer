package de.mpa.client.ui.panels;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.DenovoSearchSettings;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.Constants;

public class DeNovoPanel extends JPanel {

	private ClientFrame clientFrame;
	private Client client;

	private JComboBox dnEnzymesCbx;
	private JComboBox dnMSCbx;
	private JSpinner dnFragTolSpn;
	private JSpinner dnThresholdSpn;
	private JSpinner dnNumSolutionsSpn;
	private JCheckBox dnPepknownChx;
	private JTable dnPTMtbl;
	private JButton dnStartBtn;

	public DeNovoPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		this.client = clientFrame.getClient();
		initComponents();
	}

	private void initComponents() {
		CellConstraints cc = new CellConstraints();
		this.setLayout(new FormLayout("5dlu, p, 5dlu, f:p, 5dlu",	// col
				"5dlu, f:p, 5dlu, p, 5dlu"));	// row

		// Parameters		
		JPanel parPnl = new JPanel();
		parPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",				// col
				"p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));	// row
		parPnl.setBorder(new TitledBorder("Parameters"));

		// Enzymes
		parPnl.add(new JLabel("Protease"), cc.xy(2, 1));
		dnEnzymesCbx = new JComboBox(Constants.DN_ENZYMES);
		dnEnzymesCbx.setToolTipText("Choose the enzyme of the protein digest");
		//dnEnzymesCbx.setRenderer(new RightAlignListCellRenderer());
		parPnl.add(dnEnzymesCbx,cc.xyw(4, 1, 5));

		// MS
		parPnl.add(new JLabel("Spectrometer"),cc.xy(2, 3));
		dnMSCbx=new JComboBox(Constants.MASS_SPECTROMETERS);
		dnMSCbx.setToolTipText("Select your mass spectrometer");
		//dnMS.setRenderer(new RightAlignListCellRenderer());
		parPnl.add(dnMSCbx,cc.xyw(4, 3, 5));

		// Fragment tolerance
		parPnl.add(new JLabel("Fragment mass tolerance"), cc.xyw(2, 5, 3));
		dnFragTolSpn = new JSpinner(new SpinnerNumberModel(0.3, 0.0, null, 0.05));
		dnFragTolSpn.setEditor(new JSpinner.NumberEditor(dnFragTolSpn, "0.00"));
		dnFragTolSpn.setToolTipText("Choose your fragment mass tolerance");
		parPnl.add(dnFragTolSpn,cc.xy(6, 5));
		parPnl.add(new JLabel("Da"),cc.xy(8, 5));

		// for right aligned text in spinners in windows LAF
		//	private class RightAlignListCellRenderer extends JLabel implements ListCellRenderer<String> {
		//		@Override
		//		public Component getListCellRendererComponent(
		//				JList<? extends String> list, String value, int index,
		//				boolean isSelected, boolean cellHasFocus) {
		//			this.setText(value);
		//			this.setHorizontalAlignment(JLabel.RIGHT);
		//			return this;
		//		}
		//	}

		// Threshold peptides
		parPnl.add(new JLabel("Peptide intensity threshold"),cc.xyw(2, 7, 3));
		dnThresholdSpn = new JSpinner(new SpinnerNumberModel(1000, 0, null, 1));
		dnThresholdSpn.setToolTipText("Apply peptide threshold");
		parPnl.add(dnThresholdSpn,cc.xy(6, 7));

		// Maximum number of peptides
		parPnl.add(new JLabel("Number of peptides"),cc.xyw(2, 9, 3));
		dnNumSolutionsSpn = new JSpinner(new SpinnerNumberModel(10, 0, null, 1));
		dnNumSolutionsSpn.setToolTipText("Select the maximum number of peaks for de novo sequencing ");
		parPnl.add(dnNumSolutionsSpn,cc.xy(6, 9));

		dnPepknownChx = new JCheckBox("Remove known peptides");
		dnPepknownChx.setToolTipText("Remove all identified peptides");
		//		parPnl.add(dnPepknownChx,cc.xyw(2, 11, 5));

		// Panel PTMs
		JPanel ptmsPnl = new JPanel();
		ptmsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",	// col
				"f:p:g, 5dlu,"));	// row
		ptmsPnl.setBorder(new TitledBorder("PTMs"));

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
		dnPTMtbl = new JTable(model);
		dnPTMtbl.getColumnModel().getColumn(1).setMaxWidth(dnPTMtbl.getColumnModel().getColumn(1).getMinWidth());
		dnPTMtbl.setShowVerticalLines(false);
		JScrollPane dnPTMscp = new JScrollPane(dnPTMtbl);
		//		dnPTMscp.setPreferredSize(new Dimension(0, 0));
		dnPTMscp.setPreferredSize(new Dimension(200, 0));
		dnPTMscp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		dnPTMscp.setToolTipText("Choose possible PTMs");
		ptmsPnl.add(dnPTMscp,cc.xy(2,1));

		// Start panel
		JPanel statusPnl = new JPanel();
		statusPnl.setLayout(new FormLayout("5dlu, p, 15dlu, p, 5dlu, p:g, 5dlu",	// col
				"p, 5dlu,"));							// row
		statusPnl.setBorder(new TitledBorder("Search status"));
		// Start button
		dnStartBtn = new JButton("Run de-novo search");
		dnStartBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dnStartBtn.setEnabled(false);
				RunDenovoSearchWorker worker = new RunDenovoSearchWorker();
				worker.execute();

				// Easter egg stuff
				Image image = null;
				try {
					image = ImageIO.read(new File("docu/Nerds.jpg"));

				} catch (IOException e1) {
					e1.printStackTrace();
				}
				JLabel label = new JLabel("Das passiert wenn man wahllos Knoepfe drueckt!!!", new ImageIcon(image),JLabel.CENTER);
				label.setVerticalTextPosition(JLabel.BOTTOM);
				label.setHorizontalTextPosition(JLabel.CENTER);
				JOptionPane.showMessageDialog(clientFrame, label,"Erwischt!!", JOptionPane.PLAIN_MESSAGE);
			}
		});
		statusPnl.add(dnStartBtn,cc.xy(2, 1));
		// Progress bar
		statusPnl.add(new JLabel("Progress"),cc.xy(4, 1));
		JProgressBar denovoPrg = new JProgressBar(0,100);
		denovoPrg.setStringPainted(true);
		denovoPrg.setValue(0);
		statusPnl.add(denovoPrg, cc.xy(6, 1));

		// add panels
		this.add(parPnl, cc.xy(2, 2));
		this.add(ptmsPnl, cc.xy(4, 2));
		this.add(statusPnl, cc.xyw(2, 4, 3));
	}

	private DenovoSearchSettings collectDenovoSettings(){
		DenovoSearchSettings settings = new DenovoSearchSettings();
		settings.setDnEnzyme(dnEnzymesCbx.getSelectedItem().toString());
		settings.setDnMS(dnMSCbx.getSelectedItem().toString());
		settings.setDnFragmentTolerance((Double) dnFragTolSpn.getValue());
		settings.setDnPeptideIntThresh((Integer)dnThresholdSpn.getValue() );
		settings.setDnNumSolutions((Integer) dnNumSolutionsSpn.getValue());
		settings.setDnRemoveAllPep((boolean) dnPepknownChx.isSelected());
		String mods = "";
		for (int row = 0; row < dnPTMtbl.getRowCount(); row++) {
			if ((Boolean) dnPTMtbl.getValueAt(row, 1)){
				mods += (String) dnPTMtbl.getValueAt(row, 0);
			}
		}
		settings.setDnPTMs(mods);
		return settings;
	}	

	/**
	 * RunDBSearchWorker class extending SwingWorker.
	 * @author Thilo Muth
	 *
	 */
	private class RunDenovoSearchWorker	extends SwingWorker {

		protected Object doInBackground() throws Exception {
			DenovoSearchSettings settings = collectDenovoSettings();
			List<File> chunkedFiles = client.packFiles(1000, clientFrame.getFilePanel().getCheckBoxTree(), "test");
			client.sendFiles(chunkedFiles);
			client.runDenovoSearch(chunkedFiles, settings);
			return 0;
		}

		@Override
		public void done() {
			dnStartBtn.setEnabled(true);
		}
	}

}
