package de.mpa.client.ui.panels;

import java.awt.AWTKeyStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.ui.CheckBoxTreeManager;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTitledBorder;

public class SettingsPanel extends JPanel {

	private ClientFrame clientFrame;
	private Client client;
	
	/**
	 * The spinner to define the amount of spectra to be consolidated into a transfer package.
	 */
	private JSpinner packSpn;

	private DBSearchPanel databasePnl;
	private SpecLibSearchPanel specLibPnl;
	private DeNovoSearchPanel deNovoPnl;

	private JButton processBtn;
	
	private JProgressBar currentPrg;
	private JLabel timeLbl;
	private JTextField statusTtf;
	
	public SettingsPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		this.client = clientFrame.getClient();
		this.currentPrg = clientFrame.getStatusBar().getCurrentProgressBar();
		this.statusTtf = clientFrame.getStatusBar().getCurrentStatusTextField();
		this.timeLbl = clientFrame.getStatusBar().getTimeLabel();
		initComponents();
	}

	private void initComponents() {

		CellConstraints cc = new CellConstraints();
		
		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				newForwardKeys);

		this.setLayout(new FormLayout("5dlu, p, 15dlu, p, 15dlu, p, 5dlu",
									  "5dlu, f:p, 5dlu, p, 5dlu"));
		
		// database search settings panel
		databasePnl = new DBSearchPanel();
		databasePnl.setEnabled(true);
		
		JCheckBox databaseChk = new JCheckBox("Database Searching", true);
		databaseChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				databasePnl.setEnabled(((JCheckBox) evt.getSource()).isSelected());
			}
		});
		
		ComponentTitledBorder databaseBrd = new ComponentTitledBorder(databaseChk, databasePnl);
		databasePnl.setBorder(databaseBrd);

		// spectral library search settings panel
		specLibPnl = new SpecLibSearchPanel(clientFrame);
		specLibPnl.setEnabled(false);
		
		JCheckBox specLibChk = new JCheckBox("Spectral Library Searching", false);
		specLibChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				specLibPnl.setEnabled(((JCheckBox) evt.getSource()).isSelected());
			}
		});
		
		ComponentTitledBorder specLibBrd = new ComponentTitledBorder(specLibChk, specLibPnl);
		specLibPnl.setBorder(specLibBrd);

		// de novo search settings panel
		deNovoPnl = new DeNovoSearchPanel();
		deNovoPnl.setEnabled(false);
		
		JCheckBox deNovoChk = new JCheckBox("De Novo Searching", false);
		deNovoChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				deNovoPnl.setEnabled(((JCheckBox) evt.getSource()).isSelected());
			}
		});
		
		ComponentTitledBorder deNovoBrd = new ComponentTitledBorder(deNovoChk, deNovoPnl);
		deNovoPnl.setBorder(deNovoBrd);

		// general settings panel
		JPanel processPnl = new JPanel();
		processPnl.setLayout(new FormLayout("5dlu, p, 2dlu, p:g, 2dlu, p, 5dlu",
											"0dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		processPnl.setBorder(BorderFactory.createTitledBorder("General"));
		
		packSpn = new JSpinner(new SpinnerNumberModel(1000, 1, null, 1));
		packSpn.setToolTipText("Number of spectra per transfer package"); 
		packSpn.setPreferredSize(new Dimension(packSpn.getPreferredSize().width*2,
											   packSpn.getPreferredSize().height));
		
		JCheckBox integrateChk = new JCheckBox(" Embed processed input data in spectral library");
		
		ImageIcon processIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/search.png"));
		processIcon = new ImageIcon(processIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		
		processBtn = new JButton("Start searching", processIcon);
		processBtn.setEnabled(false);
		
		processBtn.setHorizontalAlignment(SwingConstants.LEFT);
		processBtn.setFont(processBtn.getFont().deriveFont(
				Font.BOLD, processBtn.getFont().getSize2D()*1.0f));

		processBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				processBtn.setEnabled(false);
				new RunDbSearchWorker().execute();
			}
		});
		
		processPnl.add(new JLabel("Transfer up to"), cc.xy(2, 2));
		processPnl.add(packSpn, cc.xy(4, 2));
		processPnl.add(new JLabel("spectra per package"), cc.xy(6, 2));
		processPnl.add(integrateChk, cc.xyw(2, 4, 5));
		processPnl.add(processBtn, cc.xyw(4, 6, 3));
		
		// add sub-panels to main settings panel
		this.add(databasePnl, cc.xy(2, 2));
		this.add(specLibPnl, cc.xy(4, 2));
		this.add(deNovoPnl, cc.xy(6, 2));
		this.add(processPnl, cc.xy(6, 4));
	}
	


	/**
	 * RunDBSearchWorker class extending SwingWorker.
	 * 
	 * @author Thilo Muth
	 * 
	 */
	private class RunDbSearchWorker extends SwingWorker {

		protected Object doInBackground() {
			try {
				DbSearchSettings settings = databasePnl.collectDBSearchSettings();
				
				final long startTime = System.currentTimeMillis();
				
				CheckBoxTreeManager checkBoxTree = clientFrame.getFilePanel().getCheckBoxTree();
				final int maxProgress = checkBoxTree.getSelectionModel().getSelectionCount();
				
				client.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent pce) {
						if (pce.getPropertyName().equalsIgnoreCase("progress")) {
							int progressAbs = (Integer) pce.getNewValue();
							double progressRel = progressAbs*100.0/maxProgress;
							
							currentPrg.setValue((int) progressRel);
							
							long elapsedTime = System.currentTimeMillis() - startTime;
							long remainingTime = 0L;
							if (progressRel > 0.0) {
								remainingTime = (long) (elapsedTime/progressAbs*(100-progressRel)/1000);
							}
							timeLbl.setText(String.format("%02d:%02d:%02d", remainingTime/3600,
									(remainingTime%3600)/60, (remainingTime%60)));
						} else if (pce.getPropertyName().equalsIgnoreCase("new message")) {
							statusTtf.setText(pce.getNewValue().toString());
						}
					}
				});
				statusTtf.setText("PACKING SPECTRA");
				int packSize = (Integer) packSpn.getValue();
				List<File> chunkedFiles = client.packSpectra(packSize, checkBoxTree, "test");
				statusTtf.setText("PACKING SPECTRA FINISHED");
				client.sendFiles(chunkedFiles);
				client.runDbSearch(chunkedFiles, settings);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(clientFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
			}
			return 0;
		}

		@Override
		public void done() {
			processBtn.setEnabled(true);
		}
	}

	/**
	 * Method to get the spectral library search settings panel.
	 * @return
	 */
	public SpecLibSearchPanel getSpecLibSearchPanel() {
		return specLibPnl;
	}

	public JButton getProcessButton() {
		return processBtn;
	}

//	/**
//	 * Worker class to process spectral library search queries.
//	 */
//	public class SpecLibSearchWorker extends SwingWorker {
//
//		private ClientFrame clientFrame;
//		private Client client;
//
//		private double maxProgress;
//		private HashMap<String, ArrayList<RankedLibrarySpectrum>> resultMap;
//
//		public SpecLibSearchWorker(ClientFrame clientFrame) {
//			this.clientFrame = clientFrame;
//			this.client = clientFrame.getClient();
//			this.maxProgress = clientFrame.getFilePanel().getCheckBoxTree().getSelectionModel().getSelectionCount();
//		}
//
//		protected Object doInBackground() throws Exception {
//
//			// appear busy
//			//			setProgress(0);
//			firePropertyChange("progress", null, 0);
//			processBtn.setEnabled(false);
//			clientFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//
//			// clone file selection tree, discard unselected branches/leaves
//			CheckBoxTreeSelectionModel selectionModel = clientFrame.getFilePanel().getCheckBoxTree().getSelectionModel();
//			DefaultMutableTreeNode fileRoot = (DefaultMutableTreeNode) clientFrame.getFilePanel().getCheckBoxTree().getModel().getRoot();
//			DefaultTreeModel queryModel = (DefaultTreeModel) clientFrame.getQueryTree().getModel();
//			DefaultMutableTreeNode queryRoot = (DefaultMutableTreeNode) queryModel.getRoot();
//			queryRoot.removeAllChildren();
//
//			for (int i = 0; i < fileRoot.getChildCount(); i++) {
//				DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode) fileRoot.getChildAt(i);
//				TreePath filePath = new TreePath(fileNode.getPath());
//				if ((selectionModel.isPathSelected(filePath, true)) || 
//						(selectionModel.isPartiallySelected(filePath))) {
//					// create a new node containing only the file tree node's selected sub-nodes
//					DefaultMutableTreeNode fileNodeClone = new DefaultMutableTreeNode(fileNode.getUserObject());
//					queryModel.insertNodeInto(fileNodeClone, queryRoot, queryRoot.getChildCount());
//					for (int j = 0; j < fileNode.getChildCount(); j++) {
//						DefaultMutableTreeNode spectrumNode = (DefaultMutableTreeNode) fileNode.getChildAt(j);
//						TreePath spectrumPath = new TreePath(spectrumNode.getPath());
//						if (selectionModel.isPathSelected(spectrumPath, true)) {
//							DefaultMutableTreeNode spectrumNodeClone = new DefaultMutableTreeNode(spectrumNode.getUserObject());
//							queryModel.insertNodeInto(spectrumNodeClone, fileNodeClone, fileNodeClone.getChildCount());
//						}
//					}
//				}
//			}
//
//			// register property change listener with client
//			PropertyChangeListener listener = new PropertyChangeListener() {
//				@Override
//				public void propertyChange(PropertyChangeEvent evt) {
//					//					setProgress((int)((Integer)evt.getNewValue()/maxProgress*100.0));
//					firePropertyChange("progress", null, (int)((Integer)evt.getNewValue()/maxProgress*100.0));
//				}
//			};
//			client.addPropertyChangeListener(listener);
//
//			// consolidate selected spectra into files
//			clientFrame.appendToLog("Packing files... ");
//			firePropertyChange("text", null, "Packing...");
//			long startTime = System.currentTimeMillis();
//
//			List<File> files = client.packFiles((Integer) packSpn.getValue(), clientFrame.getFilePanel().getCheckBoxTree(), "batch_");
//
//			clientFrame.appendToLog("done (took " + (System.currentTimeMillis()-startTime)/1000.0 + " seconds)\n");
//
//			// process files
//			firePropertyChange("text", null, "Processing...");
//			clientFrame.appendToLog("Processing files...");
//
//			SpecSimSettings specSet = specLibPnl.gatherSpecSimSettings();
//
//			resultMap = new HashMap<String, ArrayList<RankedLibrarySpectrum>>();
//			client.initDBConnection();
//			startTime = System.currentTimeMillis();
//			//			setProgress(0);
//			firePropertyChange("progress", null, 0);
//			for (File file : files) {
//				resultMap.putAll(client.searchSpecLib(file, specSet));
//			}
//			// TODO: maybe use this map only in the panel ? 
//			clientFrame.setResultMap(resultMap);
//
//			// clean up
//			client.closeDBConnection();
//			client.removePropertyChangeListener(listener);
//			clientFrame.appendToLog("done (took " + (System.currentTimeMillis()-startTime)/1000.0 + " seconds)\n");
//
//			return 0;
//		}
//
//		@Override
//		public void done() {
//			firePropertyChange("text", null, "Processing done!");
//			processBtn.setEnabled(true);
//			((DefaultTreeModel) clientFrame.getQueryTree().getModel()).reload();
//			clientFrame.setCursor(null);	//turn off the wait cursor
//		}
//	}

}
