package de.mpa.client.ui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StatusPanel extends JPanel {

//	private ClientFrame clientFrame;
	private JProgressBar currentPrg;
	private JProgressBar totalPrg;
	private JTextField statusTtf;
	private JLabel timeLbl;

	public StatusPanel(ClientFrame clientFrame) {
//		this.clientFrame = clientFrame;
		initComponents();
	}

	private void initComponents() {

		CellConstraints cc = new CellConstraints();

		this.setLayout(new FormLayout("0dlu:g, 2dlu, 0dlu:g, 2dlu, 0dlu:g, 2dlu, 0dlu:g, p, 0dlu:g",
									  "f:p:g"));

//		JProgressBar dummyPrg = new JProgressBar();
//		Border progressBorder = dummyPrg.getBorder();
		BevelBorder bevelBrd = new ThinBevelBorder(BevelBorder.LOWERED);
		
		// Project/Experiment name
		JTextField projectTtf = new JTextField("ProjectName - ExperimentName");
		projectTtf.setBorder(null);
		projectTtf.setEditable(false);
		
		JPanel projectPnl = new JPanel(new FormLayout("2dlu, p, 2dlu",
													  "2dlu, f:p:g, 2dlu"));
		projectPnl.add(projectTtf, cc.xy(2,2));
		projectPnl.setBorder(bevelBrd);

		// Settings info
		JPanel settingsPnl = new JPanel(new FormLayout("2dlu, p, 2dlu",
				  									   "2dlu, f:p:g, 2dlu"));
		JTextField settingsTtf = new JTextField("C:\\Temp\\Settings01.txt");
		settingsTtf.setBorder(null);
		settingsTtf.setEditable(false);

		settingsPnl.add(settingsTtf, cc.xy(2,2));
		settingsPnl.setBorder(bevelBrd);
		
		// generic text field
		JPanel textPnl = new JPanel(new FormLayout("2dlu, p, 2dlu",
												   "2dlu, f:p:g, 2dlu"));
		
		JTextField textTtf = new JTextField("Please wait... search in progress...");
		textTtf.setEditable(false);
		textTtf.setBorder(null);
		
		textPnl.add(textTtf, cc.xy(2, 2));
		textPnl.setBorder(bevelBrd);
		
		// progress bars
		JPanel progressPnl = new JPanel(new FormLayout("0dlu, p:g, 0dlu",
				   "0dlu, f:p:g(0.66666), f:p:g(0.33333), 0dlu"));
		currentPrg = new JProgressBar();
		currentPrg.setStringPainted(true);
		currentPrg.setValue(23);
		currentPrg.setBorder(null);
		currentPrg.setPreferredSize(new Dimension(currentPrg.getPreferredSize().width,
				(int) (currentPrg.getPreferredSize().height*0.66666)));
		
		totalPrg = new JProgressBar();
		totalPrg.setStringPainted(false);
		totalPrg.setValue(57);
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
		JPanel currentStatusPnl = new JPanel(new FormLayout("2dlu, p, 2dlu", "2dlu, f:p:g, 2dlu"));
		
		statusTtf = new JTextField("X!TANDEM RUNNING");
		statusTtf.setBorder(null);
		statusTtf.setEditable(false);
		
		currentStatusPnl.add(statusTtf, cc.xy(2,2));
		currentStatusPnl.setBorder(bevelBrd);
		
		this.add(projectPnl, cc.xy(1, 1));
		this.add(settingsPnl, cc.xy(3, 1));
		this.add(textPnl, cc.xy(5, 1));
		this.add(progressPnl, cc.xy(7, 1));
		this.add(timePnl, cc.xy(8, 1));
		this.add(currentStatusPnl, cc.xy(9, 1));

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
}
