package de.mpa.client.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultCaret;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;

public class StatusPanel extends JPanel {

	/**
	 * The client instance
	 */
	private Client client;
	
	/**
	 * The progress bar displaying current process progress.
	 */
	private JProgressBar currentPrg = new JProgressBar();
	
	/**
	 * The progress bar displaying total process progress.
	 */
	private JProgressBar totalPrg = new JProgressBar();
	
	/**
	 * A text field displaying the current process status.
	 */
	private JTextField statusTtf;
	
	/**
	 * A label displaying estimated remaining time to finish the current process.
	 */
	private JLabel timeLbl;
	
	/**
	 * A text field displaying the currently selected project's name.
	 */
	private JTextField projectTtf;
	
	/**
	 * A text field displaying the currently selected experiment's name.
	 */
	private JTextField experimentTtf;

	/**
	 * Creates the client frame's status bar panel.
	 */
	public StatusPanel() {
		client = Client.getInstance();
		initComponents();
		initListener();
	}

	/**
	 * Creates and configures the status bar components.
	 */
	private void initComponents() {

		CellConstraints cc = new CellConstraints();

		this.setLayout(new FormLayout("0dlu:g(3), 0dlu:g(3), 2dlu, 0dlu:g(3), 2dlu, 0dlu:g(5), p, 0dlu:g(4)",
									  "f:p:g"));

		BevelBorder bevelBrd = new ThinBevelBorder(BevelBorder.LOWERED);
		
		// Project name
		JPanel projectPnl = new JPanel(new FormLayout("2dlu, p:g, 2dlu",
													  "2dlu, f:p:g, 2dlu"));
		projectPnl.setBorder(bevelBrd);

		// TODO: use binding to synchronize text field values with project properties
		projectTtf = new JTextField("None");
		projectTtf.setBorder(null);
		projectTtf.setEditable(false);
		projectTtf.setCaret(new DefaultCaret() { public void paint(Graphics g) {} });

		projectPnl.add(projectTtf, cc.xy(2,2));

		// Experiment name
		JPanel experimentPnl = new JPanel(new FormLayout("2dlu, p:g, 2dlu",
														 "2dlu, f:p:g, 2dlu"));
		experimentPnl.setBorder(bevelBrd);
		
		experimentTtf = new JTextField("None");
		experimentTtf.setBorder(null);
		experimentTtf.setEditable(false);
		experimentTtf.setCaret(new DefaultCaret() { public void paint(Graphics g) {} });
		
		experimentPnl.add(experimentTtf, cc.xy(2,2));

		// Settings info
		JPanel settingsPnl = new JPanel(new FormLayout("2dlu, p:g, 2dlu",
				  									   "2dlu, f:p:g, 2dlu"));
		JTextField settingsTtf = new JTextField("C:\\Temp\\Settings01.txt");
		settingsTtf.setBorder(null);
		settingsTtf.setEditable(false);
		settingsTtf.setCaret(new DefaultCaret() { public void paint(Graphics g) {} });

		settingsPnl.add(settingsTtf, cc.xy(2,2));
		settingsPnl.setBorder(bevelBrd);
		
//		// generic text field
//		JPanel textPnl = new JPanel(new FormLayout("2dlu, p:g, 2dlu",
//												   "2dlu, f:p:g, 2dlu"));
//		
//		JTextField textTtf = new JTextField();
//		textTtf.setEditable(false);
//		textTtf.setBorder(null);
//		textTtf.setCaret(new DefaultCaret() { public void paint(Graphics g) {} });
//		
//		textPnl.add(textTtf, cc.xy(2, 2));
//		textPnl.setBorder(bevelBrd);
		
		// progress bars
		JPanel progressPnl = new JPanel(new FormLayout("0dlu, p:g, 0dlu",
				   "0dlu, f:p:g(0.66666), f:p:g(0.33333), 0dlu"));
		currentPrg = new JProgressBar();
		currentPrg.setStringPainted(true);
		currentPrg.setValue(100);
		currentPrg.setBorder(null);
		currentPrg.setPreferredSize(new Dimension(currentPrg.getPreferredSize().width,
				(int) (currentPrg.getPreferredSize().height*0.66666)));
		
		totalPrg = new JProgressBar();
		totalPrg.setStringPainted(false);
		totalPrg.setValue(100);
		totalPrg.setBorder(null);
		totalPrg.setPreferredSize(new Dimension(totalPrg.getPreferredSize().width,
				(int) (totalPrg.getPreferredSize().height*0.33333)));
		
		// remaining time display
		JPanel timePnl = new JPanel(new FormLayout("2dlu, p, 2dlu", "2dlu, f:p:g, 2dlu"));

		timeLbl = new JLabel("00:00:00");

		timePnl.add(timeLbl, cc.xy(2, 2));
		timePnl.setBorder(bevelBrd);
				
		progressPnl.add(currentPrg, cc.xy(2,2));
		progressPnl.add(totalPrg, cc.xy(2,3));
		progressPnl.setBorder(bevelBrd);
		
		// current status message display
		JPanel currentStatusPnl = new JPanel(new FormLayout("2dlu, p:g, 2dlu",
				"2dlu, f:p:g, 2dlu"));
		
		statusTtf = new JTextField();
		statusTtf.setBorder(null);
		statusTtf.setEditable(false);
		statusTtf.setCaret(new DefaultCaret() { public void paint(Graphics g) {} });
		
		currentStatusPnl.add(statusTtf, cc.xy(2,2));
		currentStatusPnl.setBorder(bevelBrd);
		
		this.add(projectPnl, cc.xy(1, 1));
		this.add(experimentPnl, cc.xy(2, 1));
		this.add(settingsPnl, cc.xy(4, 1));
//		this.add(textPnl, cc.xy(6, 1));
		this.add(progressPnl, cc.xy(6, 1));
		this.add(timePnl, cc.xy(7, 1));
		this.add(currentStatusPnl, cc.xy(8, 1));

	}

	/**
	 * Creates and registers a property listener which controls the status bar components' contents.
	 */
	private void initListener() {
		PropertyChangeListener listener = new PropertyChangeListener() {
			
			private long startTime;
			private long curProgress;
			private long totProgressTillNow;
			private long totProgress;
			private long maxCurProgress;
			private long maxTotProgress;
			
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				String name = pce.getPropertyName();
				if (name.equalsIgnoreCase("resetall")) {
					startTime = System.currentTimeMillis();
					curProgress = 0L;
					totProgressTillNow = 0L;
					totProgress = 0L;
					maxCurProgress = 0L;
					maxTotProgress = (Long) pce.getNewValue();
					currentPrg.setValue(0);
					totalPrg.setValue(0);
				} else if (name.equalsIgnoreCase("resetcur")) {
					startTime = System.currentTimeMillis();
					curProgress = 0L;
					totProgressTillNow += maxCurProgress;
					maxCurProgress = (Long) pce.getNewValue();
					currentPrg.setValue(0);
				} else if (name.equalsIgnoreCase("indeterminate")) {
					final boolean indeterminate = (Boolean) pce.getNewValue();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							currentPrg.setIndeterminate(indeterminate);
						}
					});
				} else if (name.equalsIgnoreCase("progressmade")) {
					curProgress++;
					totProgress++;
//					System.out.println("" + curProgress + "/" + maxCurProgress + "\t"+ totProgress + "/" + maxTotProgress);
					updateTime();
				} else if (name.equalsIgnoreCase("progress")) {
					curProgress = (Long) pce.getNewValue();
					totProgress = totProgressTillNow + curProgress;
					updateTime();
				} else if (name.equalsIgnoreCase("new message")) {
					statusTtf.setText(pce.getNewValue().toString());
				}
			}

			private void updateTime() {
				double curProgressRel = curProgress*100.0 / (double) maxCurProgress;
				double totProgressRel = totProgress*100.0 / (double) maxTotProgress;

				currentPrg.setValue((int) curProgressRel);
				totalPrg.setValue((int) totProgressRel);

				long elapsedTime = System.currentTimeMillis() - startTime;
				long remainingTime = 0L;
				if (curProgressRel > 0.0) {
					remainingTime = ((long) (elapsedTime/curProgressRel*(100.0-curProgressRel)) + 999L) / 1000L;
				}
				timeLbl.setText(String.format("%02d:%02d:%02d", remainingTime/3600,
						(remainingTime%3600)/60, remainingTime%60));
			}
		};
		client.addPropertyChangeListener(listener);
	}

	public JProgressBar getCurrentProgressBar() {
		return currentPrg;
	}

	public JProgressBar getTotalProgressBar() {
		return totalPrg;
	}
	
	public JTextField getCurrentStatusTextField() {
		return statusTtf;
	}

	public JLabel getTimeLabel() {
		return timeLbl;
	}

	public JTextField getProjectTextField() {
		return projectTtf;
	}

	public JTextField getExperimentTextField() {
		return experimentTtf;
	}
}
