package de.mpa.client.ui.panels;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import no.uib.jsparklines.extra.HtmlLinksRenderer;

import com.compomics.util.Util;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.model.DbSearchResult;
import de.mpa.client.model.PeptideHit;
import de.mpa.client.model.ProteinHitSet;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.Constants;
import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.Pepnovohit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.XTandemhit;

public class DbSearchResultPanel extends JPanel{
	
	private ClientFrame clientFrame;
	private DbSearchResultPanel dbSearchResultPnl;
	private JTable querySpectraTbl;
	private JScrollPane querySpectraTblJScrollPane;
	private JTable xTandemTbl;
	private JScrollPane xTandemTblJScrollPane;
	private JTable omssaTbl;
	private JScrollPane omssaTblJScrollPane;
	private JTable cruxTbl;
	private JScrollPane cruxTblJScrollPane;
	private JTable inspectTbl;
	private JScrollPane inspectTblJScrollPane;
	private Map<String, List<Omssahit>> ommsaResults;
	private Map<String, List<XTandemhit>> xTandemResults;
	private Map<String, List<Cruxhit>> cruxResults;
	private Map<String, List<Inspecthit>> inspectResults;
	private Map<String, List<Pepnovohit>> pepnovoResults;
	private Map<String, Integer> voteMap;
	private DbSearchResult dbSearchResult;
	private ProteinHitSet proteins;
	private Map<String, List<PeptideHit>> peptideHits;
	
	public DbSearchResultPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
	}

	/**
	 * Construct the MS2 results panel.
	 */
	private void initComponents() {
		// Cellcontraints
		CellConstraints cc = new CellConstraints();
		
		JScrollPane querySpectraTblJScrollPane = new JScrollPane();
	
		dbSearchResultPnl = this;
		dbSearchResultPnl.setLayout(new FormLayout("5dlu, p, 5dlu", "5dlu, p:g, 5dlu, p:g, 5dlu, p"));
	
		final JPanel spectraPnl = new JPanel();
		spectraPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu, f:p:g, 5dlu"));
		spectraPnl.setBorder(BorderFactory.createTitledBorder("Query Spectra"));
	
		// Setup the tables
		setupDbSearchResultTableProperties();
	
		// List with loaded query spectra
		//querySpectraLst = new JList()
	
		querySpectraTbl.addMouseListener(new java.awt.event.MouseAdapter() {
	
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				querySpectraTableMouseClicked(evt);
			}
		});
	
		querySpectraTbl.addKeyListener(new java.awt.event.KeyAdapter() {
	
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				querySpectraTableKeyReleased(evt);
			}
		});
	
		querySpectraTbl.setOpaque(false);
		querySpectraTblJScrollPane.setViewportView(querySpectraTbl);
		querySpectraTblJScrollPane.setPreferredSize(new Dimension(500, 300));
	
		JComboBox spectraCbx = new JComboBox();
		JButton updateBtn = new JButton("Get results");
		JPanel topPnl = new JPanel(new FormLayout("p:g, 40dlu, p", "p:g"));
		topPnl.add(spectraCbx, cc.xy(1, 1));
		topPnl.add(updateBtn, cc.xy(3, 1));
		updateBtn.setPreferredSize(new Dimension(150, 20));
	
		updateBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				for(File file : clientFrame.getFilePanel().files){
					dbSearchResult = clientFrame.getClient().getDbSearchResult(file);
					updateDbResultsTable();
				}
			}
		});
	
		spectraPnl.add(topPnl, cc.xy(2, 2));
		spectraPnl.add(querySpectraTblJScrollPane, cc.xy(2, 4));
	
		// PSMs
		final JPanel psmPnl = new JPanel();
		psmPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu",  "5dlu, p, 5dlu, p, 5dlu"));
		psmPnl.setBorder(BorderFactory.createTitledBorder("Peptide-to-spectrum Matches"));
	
		// X!Tandem
		final JPanel xtandemPnl = new JPanel(new FormLayout("p:g", "p:g"));
		xtandemPnl.setBorder(BorderFactory.createTitledBorder("X!Tandem"));
		xTandemTblJScrollPane = new JScrollPane();
		xTandemTblJScrollPane.setPreferredSize(new Dimension(500, 100));
		xTandemTblJScrollPane.setViewportView(xTandemTbl);
		xtandemPnl.add(xTandemTblJScrollPane, cc.xy(1, 1));
		psmPnl.add(xtandemPnl, cc.xy(2, 2));
	
		// Omssa
		final JPanel omssaPnl = new JPanel(new FormLayout("p:g", "p:g"));
		omssaPnl.setBorder(BorderFactory.createTitledBorder("Omssa"));
		omssaTblJScrollPane = new JScrollPane();
		omssaTblJScrollPane.setPreferredSize(new Dimension(500, 100));
		omssaTblJScrollPane.setViewportView(omssaTbl);
		omssaPnl.add(omssaTblJScrollPane, cc.xy(1, 1));
		psmPnl.add(omssaPnl, cc.xy(4, 2));
	
		// Crux
		final JPanel cruxPnl = new JPanel(new FormLayout("p:g", "p:g"));
		cruxPnl.setBorder(BorderFactory.createTitledBorder("Crux"));
		cruxTblJScrollPane = new JScrollPane();
		cruxTblJScrollPane.setPreferredSize(new Dimension(500, 100));
		cruxTblJScrollPane.setViewportView(cruxTbl);
		cruxPnl.add(cruxTblJScrollPane, cc.xy(1, 1));
		psmPnl.add(cruxPnl, cc.xy(2, 4));
	
		// Inspect
		final JPanel inspectPnl = new JPanel(new FormLayout("p:g", "p:g"));
		inspectPnl.setBorder(BorderFactory.createTitledBorder("Inspect"));
		inspectTblJScrollPane = new JScrollPane();
		inspectTblJScrollPane.setPreferredSize(new Dimension(500, 100));
		inspectTblJScrollPane.setViewportView(inspectTbl);
		inspectPnl.add(inspectTblJScrollPane, cc.xy(1, 1));
		psmPnl.add(inspectPnl, cc.xy(4, 4));
		dbSearchResultPnl.add(spectraPnl, cc.xy(2,2));
		dbSearchResultPnl.add(psmPnl, cc.xy(2,4));
	
	}

	private void setupDbSearchResultTableProperties(){
		// Query table
		querySpectraTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Title", "m/z", "Charge", "Identified"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		querySpectraTbl.getColumn(" ").setMinWidth(30);
		querySpectraTbl.getColumn(" ").setMaxWidth(30);
		querySpectraTbl.getColumn("m/z").setMinWidth(100);
		querySpectraTbl.getColumn("m/z").setMaxWidth(100);
		querySpectraTbl.getColumn("Charge").setMinWidth(100);
		querySpectraTbl.getColumn("Charge").setMaxWidth(100);
		querySpectraTbl.getColumn("Identified").setMinWidth(80);
		querySpectraTbl.getColumn("Identified").setMaxWidth(80);
		xTandemTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Peptide", "Accession", "e-value", "hyperscore", "PEP", "q-value"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
	
		xTandemTbl.getColumn(" ").setMinWidth(30);
		xTandemTbl.getColumn(" ").setMaxWidth(30);
		xTandemTbl.getColumn("Accession").setCellRenderer(new HtmlLinksRenderer(Constants.SELECTED_ROW_HTML_FONT_COLOR, Constants.NOT_SELECTED_ROW_HTML_FONT_COLOR));
		xTandemTbl.getColumn("e-value").setMinWidth(80);
		xTandemTbl.getColumn("e-value").setMaxWidth(80);
		xTandemTbl.getColumn("hyperscore").setMinWidth(90);
		xTandemTbl.getColumn("hyperscore").setMaxWidth(90);
		xTandemTbl.getColumn("PEP").setMinWidth(80);
		xTandemTbl.getColumn("PEP").setMaxWidth(80);
		xTandemTbl.getColumn("q-value").setMinWidth(80);
		xTandemTbl.getColumn("q-value").setMaxWidth(80);
	
		omssaTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Peptide", "Accession", "e-value", "p-value", "PEP", "q-value"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
	
		omssaTbl.getColumn(" ").setMinWidth(30);
		omssaTbl.getColumn(" ").setMaxWidth(30);
		omssaTbl.getColumn("Accession").setCellRenderer(new HtmlLinksRenderer(Constants.SELECTED_ROW_HTML_FONT_COLOR, Constants.NOT_SELECTED_ROW_HTML_FONT_COLOR));
		omssaTbl.getColumn("e-value").setMinWidth(80);
		omssaTbl.getColumn("e-value").setMaxWidth(80);
		omssaTbl.getColumn("p-value").setMinWidth(80);
		omssaTbl.getColumn("p-value").setMaxWidth(80);
		omssaTbl.getColumn("PEP").setMinWidth(80);
		omssaTbl.getColumn("PEP").setMaxWidth(80);
		omssaTbl.getColumn("q-value").setMinWidth(80);
		omssaTbl.getColumn("q-value").setMaxWidth(80);
	
		cruxTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Peptide", "Accession", "xCorr", "q-value"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
	
		cruxTbl.getColumn(" ").setMinWidth(30);
		cruxTbl.getColumn(" ").setMaxWidth(30);
		cruxTbl.getColumn("xCorr").setMinWidth(90);
		cruxTbl.getColumn("xCorr").setMaxWidth(90);
		cruxTbl.getColumn("q-value").setMinWidth(90);
		cruxTbl.getColumn("q-value").setMaxWidth(90);
	
		inspectTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Peptide", "Accession", "f-score", "p-value"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
	
		inspectTbl.getColumn(" ").setMinWidth(30);
		inspectTbl.getColumn(" ").setMaxWidth(30);
		inspectTbl.getColumn("f-score").setMinWidth(90);
		inspectTbl.getColumn("f-score").setMaxWidth(90);
		inspectTbl.getColumn("p-value").setMinWidth(90);
		inspectTbl.getColumn("p-value").setMaxWidth(90);
	
		// Reordering not allowed
		querySpectraTbl.getTableHeader().setReorderingAllowed(false);
		xTandemTbl.getTableHeader().setReorderingAllowed(false);
		omssaTbl.getTableHeader().setReorderingAllowed(false);
		cruxTbl.getTableHeader().setReorderingAllowed(false);
		inspectTbl.getTableHeader().setReorderingAllowed(false);
	}
	
	/**
	 * Update the PSM tables based on the spectrum selected.
	 * 
	 * @param evt
	 */
	private void querySpectraTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		int row = querySpectraTbl.getSelectedRow();

		// Condition if one row is selected.
		if (row != -1) {

			// Empty tables.
			clearDbResultTables();

			String spectrumName = querySpectraTbl.getValueAt(row, 1).toString();
			if (xTandemResults.containsKey(spectrumName)) {
				List<XTandemhit> xTandemList = xTandemResults.get(spectrumName);
				for (int i = 0; i < xTandemList.size(); i++) {
					XTandemhit hit = xTandemList.get(i);
					((DefaultTableModel) xTandemTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							hit.getAccession(),
							Util.roundDouble(hit.getEvalue().doubleValue(), 5), 
							Util.roundDouble(hit.getHyperscore().doubleValue(), 5),
							Util.roundDouble(hit.getPep().doubleValue(), 5),
							Util.roundDouble(hit.getQvalue().doubleValue(), 5)});
				}
			}

			if (ommsaResults.containsKey(spectrumName)) {
				List<Omssahit> omssaList = ommsaResults.get(spectrumName);
				for (int i = 0; i < omssaList.size(); i++) {
					Omssahit hit = omssaList.get(i);
					((DefaultTableModel) omssaTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							hit.getAccession(),
							Util.roundDouble(hit.getEvalue().doubleValue(), 5), 
							Util.roundDouble(hit.getPvalue().doubleValue(), 5), 
							Util.roundDouble(hit.getPep().doubleValue(), 5),
							Util.roundDouble(hit.getQvalue().doubleValue(), 5)});
				}
			}

			if (cruxResults.containsKey(spectrumName)) {
				List<Cruxhit> cruxList = cruxResults.get(spectrumName);
				for (int i = 0; i < cruxList.size(); i++) {
					Cruxhit hit = cruxList.get(i);
					((DefaultTableModel) cruxTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							hit.getAccession(),
							Util.roundDouble(hit.getXcorr_score().doubleValue(), 5), 
							Util.roundDouble(hit.getQvalue().doubleValue(), 5)});
				}
			}


			if (inspectResults.containsKey(spectrumName)) {
				List<Inspecthit> inspectList = inspectResults.get(spectrumName);
				for (int i = 0; i < inspectList.size(); i++) {
					Inspecthit hit = inspectList.get(i);
					((DefaultTableModel) inspectTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							hit.getAccession(),
							Util.roundDouble(hit.getF_score().doubleValue(), 5), 
							Util.roundDouble(hit.getP_value().doubleValue(), 5)});
				}
			}
		}
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}


	/**
	 * @see #querySpectraTableKeyReleased(java.awt.event.MouseEvent)
	 */
	private void querySpectraTableKeyReleased(KeyEvent evt) {
		querySpectraTableMouseClicked(null);
	}
	
	/**
	 * Clears the result tables.
	 */
	private void clearDbResultTables(){
		// Remove PSMs from all result tables        	 
		while (xTandemTbl.getRowCount() > 0) {
			((DefaultTableModel) xTandemTbl.getModel()).removeRow(0);
		}
		while (omssaTbl.getRowCount() > 0) {
			((DefaultTableModel) omssaTbl.getModel()).removeRow(0);
		}
		while (cruxTbl.getRowCount() > 0) {
			((DefaultTableModel) cruxTbl.getModel()).removeRow(0);
		}
		while (inspectTbl.getRowCount() > 0) {
			((DefaultTableModel) inspectTbl.getModel()).removeRow(0);
		}
	}
	
	private void updateDbResultsTable(){
		List<Searchspectrum> querySpectra = dbSearchResult.getQuerySpectra();
		xTandemResults = dbSearchResult.getxTandemResults();
		ommsaResults = dbSearchResult.getOmssaResults();      
		cruxResults = dbSearchResult.getCruxResults();        	
		inspectResults = dbSearchResult.getInspectResults();   
		voteMap = dbSearchResult.getVoteMap();
		proteins = dbSearchResult.getProteins();
		peptideHits = new HashMap<String, List<PeptideHit>>();

		if (querySpectra != null) {
			for (int i = 0; i < querySpectra.size(); i++) {
				Searchspectrum spectrum = querySpectra.get(i);
				String title = spectrum.getSpectrumname();

				((DefaultTableModel) querySpectraTbl.getModel()).addRow(new Object[]{
						i + 1,
						title,
						spectrum.getPrecursor_mz(),
						spectrum.getCharge(), 
						voteMap.get(title) + " / 4"});
			}
		}

		if (proteins != null) {
//TODO: Do in the ProteinResultPanel
//			Set<String> accessions = proteins.getPeptideHits().keySet();
//			Iterator<String> accIter = accessions.iterator();
//			int i = 0;
//			while(accIter.hasNext()){
//				String accession = accIter.next();	
//				List<PeptideHit> peptides = proteins.getPeptideHits(accession);
//				peptideHits.put(accession, peptides);
//				int nPeptides = peptides.size();
//				((DefaultTableModel) proteinResultTbl.getModel()).addRow(new Object[]{
//						i + 1,
//						accession,
//						proteins.getProteinHit(accession).getDescription(),
//						nPeptides, 
//						"",
//						"", 
//				""});
//				i++;
//			}
		}
	}

}
