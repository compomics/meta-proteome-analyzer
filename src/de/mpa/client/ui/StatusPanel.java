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

@SuppressWarnings("serial")
public class StatusPanel extends JPanel {

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
        this.initComponents();
        this.initListener();
	}

	/**
	 * Creates and configures the status bar components.
	 */
	private void initComponents() {

		CellConstraints cc = new CellConstraints();

        setLayout(new FormLayout("0dlu:g(3), 0dlu:g(3), 2dlu, 0dlu:g(3), 2dlu, 0dlu:g(5), p, 0dlu:g(4)",
									  "f:p:g"));

		BevelBorder bevelBrd = new ThinBevelBorder(BevelBorder.LOWERED);
		
		// Project name
		JPanel projectPnl = new JPanel(new FormLayout("2dlu, p:g, 2dlu",
													  "2dlu, f:p:g, 2dlu"));
		projectPnl.setBorder(bevelBrd);

		// TODO: use binding to synchronize text field values with project properties
        this.projectTtf = new JTextField("None");
        this.projectTtf.setBorder(null);
        this.projectTtf.setEditable(false);
        this.projectTtf.setCaret(new DefaultCaret() { public void paint(Graphics g) {} });

		projectPnl.add(this.projectTtf, cc.xy(2,2));

		// Experiment name
		JPanel experimentPnl = new JPanel(new FormLayout("2dlu, p:g, 2dlu",
														 "2dlu, f:p:g, 2dlu"));
		experimentPnl.setBorder(bevelBrd);

        this.experimentTtf = new JTextField("None");
        this.experimentTtf.setBorder(null);
        this.experimentTtf.setEditable(false);
        this.experimentTtf.setCaret(new DefaultCaret() { public void paint(Graphics g) {} });
		
		experimentPnl.add(this.experimentTtf, cc.xy(2,2));

		// Settings info
		JPanel settingsPnl = new JPanel(new FormLayout("2dlu, p:g, 2dlu",
				  									   "2dlu, f:p:g, 2dlu"));
//		JTextField settingsTtf = new JTextField("C:\\Temp\\Settings01.txt");
		JTextField settingsTtf = new JTextField();
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
				   "0dlu, f:p:g(0.75), f:p:g(0.25), 0dlu"));
        this.currentPrg = new JProgressBar();
        this.currentPrg.setStringPainted(true);
        this.currentPrg.setValue(100);
        this.currentPrg.setBorder(null);
        this.currentPrg.setPreferredSize(new Dimension());

        this.totalPrg = new JProgressBar();
        this.totalPrg.setStringPainted(false);
        this.totalPrg.setValue(100);
        this.totalPrg.setBorder(null);
        this.totalPrg.setPreferredSize(new Dimension());
		
		// remaining time display
		JPanel timePnl = new JPanel(new FormLayout("2dlu, p, 2dlu", "2dlu, f:p:g, 2dlu"));

        this.timeLbl = new JLabel("00:00:00");

		timePnl.add(this.timeLbl, cc.xy(2, 2));
		timePnl.setBorder(bevelBrd);
				
		progressPnl.add(this.currentPrg, cc.xy(2,2));
		progressPnl.add(this.totalPrg, cc.xy(2,3));
		progressPnl.setBorder(bevelBrd);
		
		// current status message display
		JPanel currentStatusPnl = new JPanel(new FormLayout("2dlu, p:g, 2dlu",
				"2dlu, f:p:g, 2dlu"));

        this.statusTtf = new JTextField();
        this.statusTtf.setBorder(null);
        this.statusTtf.setEditable(false);
        this.statusTtf.setCaret(new DefaultCaret() { public void paint(Graphics g) {} });
		
		currentStatusPnl.add(this.statusTtf, cc.xy(2,2));
		currentStatusPnl.setBorder(bevelBrd);

        add(projectPnl, cc.xy(1, 1));
        add(experimentPnl, cc.xy(2, 1));
        add(settingsPnl, cc.xy(4, 1));
//		this.add(textPnl, cc.xy(6, 1));
        add(progressPnl, cc.xy(6, 1));
        add(timePnl, cc.xy(7, 1));
        add(currentStatusPnl, cc.xy(8, 1));

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
                    this.startTime = System.currentTimeMillis();
                    this.curProgress = 0L;
                    this.totProgressTillNow = 0L;
                    this.totProgress = 0L;
                    this.maxCurProgress = 0L;
                    this.maxTotProgress = ((Number) pce.getNewValue()).longValue();
                    StatusPanel.this.currentPrg.setValue(0);
                    StatusPanel.this.totalPrg.setValue(0);
				} else if (name.equalsIgnoreCase("resetcur")) {
                    this.startTime = System.currentTimeMillis();
                    this.curProgress = 0L;
                    this.totProgressTillNow += this.maxCurProgress;
                    this.maxCurProgress = ((Number) pce.getNewValue()).longValue();
                    StatusPanel.this.currentPrg.setValue(0);
				} else if (name.equalsIgnoreCase("indeterminate")) {
					boolean indeterminate = (Boolean) pce.getNewValue();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
                            StatusPanel.this.currentPrg.setIndeterminate(indeterminate);
						}
					});
				} else if (name.equalsIgnoreCase("progressmade")) {
                    this.curProgress++;
                    this.totProgress++;
                    this.updateTime();
				} else if (name.equalsIgnoreCase("progress")) {
                    this.curProgress = ((Number) pce.getNewValue()).longValue();
                    this.totProgress = this.totProgressTillNow + this.curProgress;
                    this.updateTime();
//				} else if (name.equalsIgnoreCase("orientation")) {
//					currentPrg.setComponentOrientation((ComponentOrientation) pce.getNewValue());
//				} else if (name.equalsIgnoreCase("reverseprogress")) {
//					curProgress = (Long) pce.getNewValue();
				} else if (name.equalsIgnoreCase("new message")) {
                    StatusPanel.this.statusTtf.setText(pce.getNewValue().toString());
				}
			}

			private void updateTime() {
				double curProgressRel = this.curProgress *100.0 / (double) this.maxCurProgress;
				double totProgressRel = this.totProgress *100.0 / (double) this.maxTotProgress;

                StatusPanel.this.currentPrg.setValue((int) curProgressRel);
                StatusPanel.this.totalPrg.setValue((int) totProgressRel);

				long elapsedTime = System.currentTimeMillis() - this.startTime;
				long remainingTime = 0L;
				if (curProgressRel > 0.0) {
					remainingTime = ((long) (elapsedTime/curProgressRel*(100.0-curProgressRel)) + 999L) / 1000L;
				}
                StatusPanel.this.timeLbl.setText(String.format("%02d:%02d:%02d", remainingTime/3600,
						(remainingTime%3600)/60, remainingTime%60));
			}
		};
		
		Client.getInstance().addPropertyChangeListener(listener);
		
	}

	public JProgressBar getCurrentProgressBar() {
		return this.currentPrg;
	}

	public JProgressBar getTotalProgressBar() {
		return this.totalPrg;
	}
	
	public JTextField getCurrentStatusTextField() {
		return this.statusTtf;
	}

	public JLabel getTimeLabel() {
		return this.timeLbl;
	}

	public JTextField getProjectTextField() {
		return this.projectTtf;
	}

	public JTextField getExperimentTextField() {
		return this.experimentTtf;
	}
}
