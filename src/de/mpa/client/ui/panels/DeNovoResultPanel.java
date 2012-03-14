package de.mpa.client.ui.panels;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.model.DenovoSearchResult;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.Constants;
import de.mpa.db.accessor.Pepnovohit;
import de.mpa.db.accessor.Spectrum;

public class DeNovoResultPanel extends JPanel {

	private ClientFrame clientFrame;
	private Client client;
	private JTable pepnovoTbl;
	private JTable queryDnSpectraTbl;
	private Map<String, List<Pepnovohit>> pepnovoResults;
	protected Object filePnl;
	private DenovoSearchResult denovoSearchResult;

	public DeNovoResultPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		this.client = clientFrame.getClient();
		initComponents();
	}

	private void initComponents() {
		CellConstraints cc = new CellConstraints();
		JScrollPane queryDnSpectraTblJScrollPane = new JScrollPane();
		this.setLayout(new FormLayout("5dlu, f:p:g, 5dlu, f:p:g, 5dlu",					// col
				"5dlu, f:p:g,5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));	// row
		// Choose your spectra
		JPanel dnResSpectrumPnl = new JPanel();
		dnResSpectrumPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",				// col
				"5dlu, p, 5dlu, f:p:g, 5dlu,"));	// row
		dnResSpectrumPnl.setBorder(BorderFactory.createTitledBorder("Query Spectra"));

		// Setup the tables
		setupDenovoSearchResultTableProperties();

		queryDnSpectraTbl.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryDnSpectraTableMouseClicked(evt);
			}
		});

		queryDnSpectraTbl.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				queryDnSpectraTableKeyReleased(evt);
			}
		});

		queryDnSpectraTbl.setOpaque(false);
		queryDnSpectraTblJScrollPane.setViewportView(queryDnSpectraTbl);
		queryDnSpectraTblJScrollPane.setPreferredSize(new Dimension(500, 300));

		JComboBox spectraCbx2 = new JComboBox();
		JButton updateDnBtn = new JButton("Get results");
		JPanel topPnl = new JPanel(new FormLayout("p:g, 40dlu, p", "p:g"));
		topPnl.add(spectraCbx2, cc.xy(1, 1));
		topPnl.add(updateDnBtn, cc.xy(3, 1));
		updateDnBtn.setPreferredSize(new Dimension(150, 20));

//		updateDnBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				List<File> chunkedFiles = client.packFiles(1000, clientFrame.getFilePanel().getCheckBoxTree(), "test");
//				for(File file : chunkedFiles){
//					denovoSearchResult = client.getDenovoSearchResult(file);
//					updateDenovoResultsTable();
//				}
//			}
//		});

		dnResSpectrumPnl.add(topPnl, cc.xy(2, 2));
		dnResSpectrumPnl.add(queryDnSpectraTblJScrollPane, cc.xy(2, 4));

		// De novo results
		JPanel dnRSeqPnl = new JPanel();
		dnRSeqPnl.setLayout(new FormLayout("5dlu, f:p:g, 5dlu",	// col
				"5dlu, f:p, 5dlu"));	// row
		dnRSeqPnl.setBorder(new TitledBorder("PepNovo results"));

		JScrollPane dnRseqScp = new JScrollPane(pepnovoTbl);
		dnRseqScp.setPreferredSize(new Dimension(100,200));
		dnRseqScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		dnRseqScp.setToolTipText("Select spectra");
		dnRSeqPnl.add(dnRseqScp,cc.xy(2, 2));

		// Use BLAST
		JPanel dnRBlastPnl = new JPanel();
		dnRBlastPnl.setLayout(new FormLayout("5dlu, f:p:g, 5dlu",											// col
				"5dlu, f:p, 5dlu, f:p, 5dlu,f:p, 5dlu,f:p, 5dlu,f:p, 5dlu"));	// row
		dnRBlastPnl.setBorder(new TitledBorder("BLAST search"));
		dnRBlastPnl.add(new JLabel("Database for BLAST search:"),cc.xy(2, 2));
		JComboBox dnRBLastCbx = new JComboBox(Constants.DNBLAST_DB);
		dnRBLastCbx.setToolTipText("Choose database for BLAST search");
		dnRBlastPnl.add(dnRBLastCbx,cc.xy(2, 4));
		JButton dnRStartBLASTBtn = new JButton("BLAST search");

		dnRStartBLASTBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JLabel dnRlabel = new JLabel("<html> Ein 22-jaehriger Mann lernt in einer " +
						"Bar eine aeltere Frau kennen. Trotz ihrem Alter von 57 Jahren, sind sich die " +
						"beiden sehr sympathisch. Sie unterhalten sich lange, beginnen zu fummeln und zu <br>" +
						"knutschen.Dann meint sie: Hast du es schon einmal mit Mutter und Tochter zusammen  " +
						"gemacht? Er antwortet: Nein, aber das waere sicher ein geiles Erlebnis! Sie sagt: " +
						"Komm mit mir nach Hause; das wird deine Nacht!Er denkt: So geil, bis morgen frueh <br>" +
						" durchhoekern und das mit 2 Frauen - ein Traum. Als sie zu Hause die Tuere oeffnet " +
						"und sie beide in den Flur treten, ruft sie: Mutti, bist du noch wach?!!!!!</html>",JLabel.CENTER);
				dnRlabel.setVerticalTextPosition(JLabel.BOTTOM);
				dnRlabel.setHorizontalTextPosition(JLabel.CENTER);
				JOptionPane.showMessageDialog(clientFrame, dnRlabel,"Erwischt!!", JOptionPane.PLAIN_MESSAGE);
			}});

		// ShowBLASTResults
		dnRBlastPnl.add(dnRStartBLASTBtn,cc.xy(2, 6));
		// Progress Bar
		dnRBlastPnl.add(new JLabel("Progress"),cc.xy(2, 8));
		JProgressBar blastsearchPrg = new JProgressBar(0, 100);
		blastsearchPrg.setStringPainted(true);
		blastsearchPrg.setValue(0);
		dnRBlastPnl.add(blastsearchPrg, cc.xy(2, 10));

		//See BLAST results
		JPanel dnRBlastRPnl = new JPanel();
		dnRBlastRPnl.setBorder(new TitledBorder("BLAST results"));

		// Spectra Plot

		//			dnRPlotPnl = new JPanel();
		//			dnRPlotPnl.setLayout(new FormLayout("5dlu, f:p:g, 5dlu",
		//											"5dlu, f:p:g, 5dlu"));
		//			
		//			dnRPlotPnl.setBorder(new TitledBorder("Spectra"));
		//			dnRPlotPnl2= new PlotPanel2(null);
		//			dnRPlotPnl.add(dnRPlotPnl2,cc.xy(2, 2));

		// Add panelsdenovoResSpectrumPnl
		this.add(dnResSpectrumPnl,cc.xyw(2, 2,3));
		this.add(dnRSeqPnl,cc.xy(2, 4));
		this.add(dnRBlastPnl,cc.xy(4, 4));
		//denovoResPnl.add(dnRBlastRPnl,cc.xy(6, 4));
		//denovoResPnl.add(dnRPlotPnl,cc.xyw(2, 6, 5));
	}

	/**
	 * Clears the result tables.
	 */
	private void clearDenovoResultTables(){
		// Remove PSMs from all result tables        	 
		while (pepnovoTbl.getRowCount() > 0) {
			((DefaultTableModel) pepnovoTbl.getModel()).removeRow(0);
		}
	}

	private void setupDenovoSearchResultTableProperties(){
		// Query table
		queryDnSpectraTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Title", "m/z", "Charge", "Identified"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		queryDnSpectraTbl.getColumn(" ").setMinWidth(30);
		queryDnSpectraTbl.getColumn(" ").setMaxWidth(30);
		queryDnSpectraTbl.getColumn("m/z").setMinWidth(100);
		queryDnSpectraTbl.getColumn("m/z").setMaxWidth(100);
		queryDnSpectraTbl.getColumn("Charge").setMinWidth(100);
		queryDnSpectraTbl.getColumn("Charge").setMaxWidth(100);
		queryDnSpectraTbl.getColumn("Identified").setMinWidth(80);
		queryDnSpectraTbl.getColumn("Identified").setMaxWidth(80);

		pepnovoTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Peptide", "N-Gap", "C-Gap", "Score"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});

		pepnovoTbl.getColumn(" ").setMinWidth(30);
		pepnovoTbl.getColumn(" ").setMaxWidth(30);
		pepnovoTbl.getColumn("N-Gap").setMinWidth(90);
		pepnovoTbl.getColumn("N-Gap").setMaxWidth(90);
		pepnovoTbl.getColumn("C-Gap").setMinWidth(90);
		pepnovoTbl.getColumn("C-Gap").setMaxWidth(90);
		pepnovoTbl.getColumn("Score").setMinWidth(90);
		pepnovoTbl.getColumn("Score").setMaxWidth(90);
	}

	/**
	 * @see #queryDnSpectraTableKeyReleased(java.awt.event.MouseEvent)
	 */
	private void queryDnSpectraTableKeyReleased(KeyEvent evt) {
		queryDnSpectraTableMouseClicked(null);
	}

	/**
	 * Update the PSM tables based on the spectrum selected.
	 * 
	 * @param evt
	 */
	private void queryDnSpectraTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		int row = queryDnSpectraTbl.getSelectedRow();

		// Condition if one row is selected.
		if (row != -1) {

			// Empty tables.
			clearDenovoResultTables();

			String spectrumName = queryDnSpectraTbl.getValueAt(row, 1).toString();
			if (pepnovoResults.containsKey(spectrumName)) {
				List<Pepnovohit> pepnovoList = pepnovoResults.get(spectrumName);
				for (int i = 0; i < pepnovoList.size(); i++) {
					Pepnovohit hit = pepnovoList.get(i);
					((DefaultTableModel) pepnovoTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							hit.getN_gap(),
							hit.getC_gap(), 
							hit.getPnvscore()});
				}
			}
		}
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void updateDenovoResultsTable(){
		List<Spectrum> querySpectra = denovoSearchResult.getQuerySpectra();
		pepnovoResults = denovoSearchResult.getPepnovoResults();
		String identified;
	
		if (querySpectra != null) {
			for (int i = 0; i < querySpectra.size(); i++) {
				Spectrum spectrum = querySpectra.get(i);
				String title = spectrum.getTitle();
				if(pepnovoResults.containsKey(title)){
					identified = "yes";
				} else {
					identified = "no";
				}
	
				((DefaultTableModel) queryDnSpectraTbl.getModel()).addRow(new Object[]{
						i + 1,
						title,
						spectrum.getPrecursor_mz(),
						spectrum.getPrecursor_charge(), 
						identified});
			}
		}
	}


}
