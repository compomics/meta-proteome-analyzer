package de.mpa.client.ui.panels;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.Painter;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.DenovoSearchSettings;
import de.mpa.client.SearchSettings;
import de.mpa.client.SpecSimSettings;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.Constants;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.MascotGenericFileReader.LoadMode;

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
	
	public SettingsPanel() {
		this.clientFrame = ClientFrame.getInstance();
		this.client = Client.getInstance();
		initComponents();
	}

	private void initComponents() {
		
		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				newForwardKeys);

		FormLayout layout = new FormLayout("5dlu, p, 10dlu, p, 10dlu, p, 5dlu, p:g, 5dlu",
		  "5dlu, f:p, 8dlu, f:p, 8dlu, b:p:g, 5dlu");
		layout.setColumnGroups(new int[][] {{2,4,6}});

		this.setLayout(layout);
				
		Border ttlBorder = PanelConfig.getTitleBorder();
		Painter ttlPainter = PanelConfig.getTitlePainter();
		Font ttlFont = PanelConfig.getTitleFont();
		Color ttlForeground = PanelConfig.getTitleForeground();
		final Color bgCol = UIManager.getColor("Label.background");
		
		// database search settings panel
		databasePnl = new DBSearchPanel(clientFrame);
		databasePnl.setEnabled(true);
		
		final JCheckBox databaseChk = new JCheckBox("Database Search", true) {
			public void paint(Graphics g) {
				g.setColor(bgCol);
				g.fillRect(0, 3, 12, 12);
				super.paint(g);
			}
		};
		databaseChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				databasePnl.setEnabled(((JCheckBox) evt.getSource()).isSelected());
			}
		});
		databaseChk.setFont(ttlFont);
		databaseChk.setForeground(ttlForeground);
		databaseChk.setFocusPainted(false);
		databaseChk.setOpaque(false);
		
		JXTitledPanel dbTtlPnl = new JXTitledPanel(" ", databasePnl);
		dbTtlPnl.setLeftDecoration(databaseChk);
		dbTtlPnl.setTitleFont(ttlFont);
		dbTtlPnl.setTitlePainter(ttlPainter);
		dbTtlPnl.setBorder(ttlBorder);
		
		// spectral library search settings panel
		specLibPnl = new SpecLibSearchPanel(clientFrame);
		specLibPnl.setEnabled(false);
		
		JCheckBox specLibChk = new JCheckBox("Spectral Library Search", false) {
			public void paint(Graphics g) {
				g.setColor(bgCol);
				g.fillRect(0, 3, 12, 12);
				super.paint(g);
			}
		};
		specLibChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				specLibPnl.setEnabled(((JCheckBox) evt.getSource()).isSelected());
			}
		});
		specLibChk.setFont(ttlFont);
		specLibChk.setForeground(ttlForeground);
		specLibChk.setFocusPainted(false);
		specLibChk.setOpaque(false);
		
		JXTitledPanel slTtlPnl = new JXTitledPanel(" ", specLibPnl);
		slTtlPnl.setLeftDecoration(specLibChk);
		slTtlPnl.setTitleFont(ttlFont);
		slTtlPnl.setTitlePainter(ttlPainter);
		slTtlPnl.setBorder(ttlBorder);
		
		// de novo search settings panel
		deNovoPnl = new DeNovoSearchPanel(clientFrame);
		deNovoPnl.setEnabled(false);
		
		JCheckBox deNovoChk = new JCheckBox("De-novo Search", false) {
			public void paint(Graphics g) {
				g.setColor(bgCol);
				g.fillRect(0, 3, 12, 12);
				super.paint(g);
			}
		};
		deNovoChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				deNovoPnl.setEnabled(((JCheckBox) evt.getSource()).isSelected());
			}
		});
		deNovoChk.setFont(ttlFont);
		deNovoChk.setForeground(ttlForeground);
		deNovoChk.setFocusPainted(false);
		deNovoChk.setOpaque(false);
		
		JXTitledPanel dnTtlPnl = new JXTitledPanel(" ", deNovoPnl);
		dnTtlPnl.setLeftDecoration(deNovoChk);
		dnTtlPnl.setTitleFont(ttlFont);
		dnTtlPnl.setTitlePainter(ttlPainter);
		dnTtlPnl.setBorder(ttlBorder);
		
		// general settings panel
		JPanel processPnl = new JPanel();
		processPnl.setLayout(new FormLayout("5dlu, p, 2dlu, p:g, 2dlu, p, 5dlu",
											"5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		
		packSpn = new JSpinner(new SpinnerNumberModel(1000L, 1L, null, 100L));
		packSpn.setToolTipText("Number of spectra per transfer package"); 
		packSpn.setPreferredSize(new Dimension(packSpn.getPreferredSize().width*2,
											   packSpn.getPreferredSize().height));
		
		JCheckBox integrateChk = new JCheckBox("Add processed spectra to spectral library");
		
		ImageIcon processIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/search.png"));
		processIcon = new ImageIcon(processIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));

		processBtn = new JButton("Start searching", processIcon);
		processBtn.setEnabled(false);
		
//		processBtn.setHorizontalAlignment(SwingConstants.LEFT);
		processBtn.setFont(processBtn.getFont().deriveFont(
				Font.BOLD, processBtn.getFont().getSize2D()*1.25f));

		processBtn.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				new ProcessWorker().execute();
			}
		});
		
		processPnl.add(new JLabel("Transfer"), CC.xy(2, 2));
		processPnl.add(packSpn, CC.xy(4, 2));
		processPnl.add(new JLabel("spectra per package"), CC.xy(6, 2));
		processPnl.add(integrateChk, CC.xyw(2, 4, 5));
		processPnl.add(processBtn, CC.xyw(2, 6, 5));

		JXTitledPanel procTtlPnl = new JXTitledPanel("General", processPnl);
		procTtlPnl.setTitleFont(ttlFont);
		procTtlPnl.setTitlePainter(ttlPainter);
		procTtlPnl.setBorder(ttlBorder);
		
		// XXX: just a placeholder, remove or relocate button/functionality
		JButton quickBtn = new JButton("Quick Search File", IconConstants.LIGHTNING_ICON);
		quickBtn.setRolloverIcon(IconConstants.LIGHTNING_ROLLOVER_ICON);
		quickBtn.setPressedIcon(IconConstants.LIGHTNING_PRESSED_ICON);
		quickBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(Constants.MGF_FILE_FILTER);
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(false);
				int result = fc.showOpenDialog(clientFrame);
				if (result == JFileChooser.APPROVE_OPTION) {
					new SwingWorker() {
						protected Object doInBackground() throws Exception {
							File file = null;
							List<String> filenames = new ArrayList<String>();
							FileOutputStream fos = null;
							client.firePropertyChange("indeterminate", false, true);
							client.firePropertyChange("new message", null, "READING SPECTRUM FILE");
							MascotGenericFileReader reader = new MascotGenericFileReader(fc.getSelectedFile(), LoadMode.SURVEY);
							client.firePropertyChange("indeterminate", true, false);
							client.firePropertyChange("new message", null, "READING SPECTRUM FILE FINISHED");
							List<Long> spectrumPositions = reader.getSpectrumPositions(false);
							long numSpectra = 0L;
							long maxSpectra = (long) spectrumPositions.size();
							long packageSize = (Long) packSpn.getValue();
							client.firePropertyChange("resetall", 0L, maxSpectra);
							client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
							// iterate over all spectra
							for (Long pos : spectrumPositions) {
								if ((numSpectra % packageSize) == 0) {
									if (fos != null) {
										fos.close();
										client.uploadFile(file.getName(), client.getBytesFromFile(file));
										file.delete();
									}
									file = new File("quick_batch" + (numSpectra/packageSize) + ".mgf");
									filenames.add(file.getName());
									fos = new FileOutputStream(file);
									long remaining = maxSpectra - numSpectra;
									firePropertyChange("resetcur", 0L, (remaining > packageSize) ? packageSize : remaining);
								}
								MascotGenericFile mgf = reader.loadNthSpectrum((int) numSpectra, pos);
								mgf.writeToStream(fos);
								fos.flush();
								firePropertyChange("progressmade", 0L, ++numSpectra);
							}
							fos.close();
							client.uploadFile(file.getName(), client.getBytesFromFile(file));
							client.firePropertyChange("new message", null, "PACKING AND SENDING FILES FINISHED");
							
							// collect search settings
							DbSearchSettings dbss = (databasePnl.isEnabled()) ? databasePnl.gatherDBSearchSettings() : null;
							SpecSimSettings sss = (specLibPnl.isEnabled()) ? specLibPnl.gatherSpecSimSettings() : null;
							DenovoSearchSettings dnss = (deNovoPnl.isEnabled()) ? deNovoPnl.collectDenovoSettings() : null;
							
							SearchSettings settings = new SearchSettings(dbss, sss, dnss,
									clientFrame.getProjectPanel().getCurrentExperimentId());
							
							client.firePropertyChange("new message", null, "SEARCHES RUNNING");
							// dispatch search request
							client.runSearches(filenames, settings);
							
							return null;
						}
					}.execute();
				}
			}
		});
		JPanel quickPnl = new JPanel(new FormLayout("r:p:g, 5dlu", "b:p:g, 5dlu"));
		quickPnl.add(quickBtn, CC.xy(1, 1));
		this.add(quickPnl, CC.xy(4, 4));
		
		JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p", "b:p:g"));
		
		navPnl.add(clientFrame.createNavigationButton(false, true), CC.xy(1, 1));
		navPnl.add(clientFrame.createNavigationButton(true, true), CC.xy(3, 1));
		
//		addExperimentBtn = new JButton("Add Experiment   ", IconConstants.ADD_PAGE_ICON);
//		addExperimentBtn.setRolloverIcon(IconConstants.ADD_PAGE_ROLLOVER_ICON);
//		addExperimentBtn.setPressedIcon(IconConstants.ADD_PAGE_PRESSED_ICON);
		
		// add sub-panels to main settings panel
		this.add(dbTtlPnl, CC.xy(2, 2));
		this.add(slTtlPnl, CC.xy(4, 2));
		this.add(dnTtlPnl, CC.xy(6, 2));
		this.add(procTtlPnl, CC.xy(6, 4));
		this.add(navPnl, CC.xyw(6, 6, 3));
	}

	/**
	 * Worker class for packing/sending input files and dispatching search requests to the server instance.
	 * 
	 * @author Thilo Muth
	 * @author Alex Behne
	 */
	private class ProcessWorker extends SwingWorker {

		protected Object doInBackground() {
			ProjectPanel projectPanel = clientFrame.getProjectPanel();
			long experimentID = projectPanel.getCurrentExperimentId();
			if (experimentID != 0L) {
				CheckBoxTreeTable checkBoxTree = clientFrame.getFilePanel().getCheckBoxTree();
				// reset progress
				client.firePropertyChange("resetall", 0L, (long) (checkBoxTree.getCheckBoxTreeSelectionModel()).getSelectionCount());
				// appear busy
				firePropertyChange("progress", null, 0);
				processBtn.setEnabled(false);
				clientFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				try {
					// pack and send files
					client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
					long packSize = (Long) packSpn.getValue();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					List<String> filenames = client.packAndSend(packSize, checkBoxTree,
							projectPanel.getCurrentExperimentContent().getExperimentTitle() + "_" + sdf.format(new Date()) + "_");
					
					// collect search settings
					DbSearchSettings dbss = (databasePnl.isEnabled()) ? databasePnl.gatherDBSearchSettings() : null;
					SpecSimSettings sss = (specLibPnl.isEnabled()) ? specLibPnl.gatherSpecSimSettings() : null;
					DenovoSearchSettings dnss = (deNovoPnl.isEnabled()) ? deNovoPnl.collectDenovoSettings() : null;
					
					SearchSettings settings = new SearchSettings(dbss, sss, dnss, experimentID);
					
					client.firePropertyChange("new message", null, "SEARCHES RUNNING");
					// dispatch search request
					client.runSearches(filenames, settings);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return 0;
			} else {
				JOptionPane.showMessageDialog(clientFrame, "No experiment selected.", "Error", JOptionPane.ERROR_MESSAGE);
				return 404;
			}
		}

		@Override
		public void done() {
			clientFrame.setCursor(null);
			processBtn.setEnabled(true);
		}
	}

	/**
	 * Returns the spectral library search settings panel reference.
	 * @return The spectral library search settings panel reference
	 */
	public SpecLibSearchPanel getSpecLibSearchPanel() {
		return specLibPnl;
	}

	/**
	 * Returns the process button reference.
	 * @return The process button reference.
	 */
	public JButton getProcessButton() {
		return processBtn;
	}

}
