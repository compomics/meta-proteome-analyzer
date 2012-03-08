package de.mpa.client.ui.panels;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.model.PeptideHit;
import de.mpa.client.model.ProteinHitSet;
import de.mpa.client.ui.ClientFrame;

/**
 * This class represents the Panel for the protein result 
 * @author T. Muth and R. Heyer
 *
 */

public class ProteinResultPanel extends JPanel {

	private ClientFrame clientFrame;
	private JScrollPane peptidesTblScp;
	private JTable proteinResultTbl;
	private JTable peptideResultTbl;
	private Map<String, List<PeptideHit>> peptideHits;
	//TODO: Get the proteins from outside
	private ProteinHitSet proteins;

	public ProteinResultPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
	}

	/**
	 * Initialize the components 
	 */
	private void initComponents(){
		
		proteinResultTbl = new JTable();
		CellConstraints cc = new CellConstraints();
		
		JScrollPane proteinTblScp = new JScrollPane();

		JPanel proteinViewPnl = new JPanel();
		proteinViewPnl.setLayout(new FormLayout("5dlu, p, 5dlu", "5dlu, t:p, 5dlu, t:p, 5dlu"));

		final JPanel proteinPnl = new JPanel();
		proteinPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",	"5dlu, f:p:g, 5dlu"));
		proteinPnl.setBorder(BorderFactory.createTitledBorder("Proteins"));

		// Setup the tables
		setupProteinResultTableProperties();

		// List with loaded query spectra
		//querySpectraLst = new JList()

		proteinResultTbl.addMouseListener(new java.awt.event.MouseAdapter() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryProteinTableMouseClicked(evt);
			}
		});

		proteinResultTbl.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				queryProteinTableKeyReleased(evt);
			}
		});

		proteinResultTbl.setOpaque(false);
		proteinTblScp.setViewportView(proteinResultTbl);
		proteinTblScp.setPreferredSize(new Dimension(1000, 300));

		proteinPnl.add(proteinTblScp, cc.xy(2, 2));

		// Peptides
		final JPanel peptidesPnl = new JPanel();
		peptidesPnl.setLayout(new FormLayout("5dlu, p, 5dlu",  "5dlu, p, 5dlu,"));
		peptidesPnl.setBorder(BorderFactory.createTitledBorder("Peptides"));

		peptidesTblScp = new JScrollPane();
		peptidesTblScp.setPreferredSize(new Dimension(1000, 250));
		peptidesTblScp.setViewportView(peptideResultTbl);
		peptidesPnl.add(peptidesTblScp, cc.xy(2, 2));

		proteinViewPnl.add(proteinPnl, cc.xy(2,2));
		proteinViewPnl.add(peptidesPnl, cc.xy(2,4));

	}
	
	/**
	 * This methode setups the protein results table
	 */
	private void setupProteinResultTableProperties(){
		// Query table
		JTable proteinResultTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Accession", "Description", "No. Peptides", "Coverage", "Spectral Count", "NSAF"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		proteinResultTbl.getColumn(" ").setMinWidth(30);
		proteinResultTbl.getColumn(" ").setMaxWidth(30);
		proteinResultTbl.getColumn("Accession").setMinWidth(100);
		proteinResultTbl.getColumn("Accession").setMaxWidth(100);

		proteinResultTbl.getColumn("No. Peptides").setMinWidth(90);
		proteinResultTbl.getColumn("No. Peptides").setMaxWidth(90);
		proteinResultTbl.getColumn("Coverage").setMinWidth(90);
		proteinResultTbl.getColumn("Coverage").setMaxWidth(90);
		proteinResultTbl.getColumn("Spectral Count").setMinWidth(90);
		proteinResultTbl.getColumn("Spectral Count").setMaxWidth(90);
		proteinResultTbl.getColumn("NSAF").setMinWidth(90);
		proteinResultTbl.getColumn("NSAF").setMaxWidth(90);

		JTable peptideResultTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Sequence", "Modification", "Unique", "PSM Votes", "No. Spectra", "Start", "End"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});

		peptideResultTbl.getColumn(" ").setMinWidth(30);
		peptideResultTbl.getColumn(" ").setMaxWidth(30);			
		peptideResultTbl.getColumn("Modification").setMinWidth(90);
		peptideResultTbl.getColumn("Modification").setMaxWidth(90);
		peptideResultTbl.getColumn("Unique").setMinWidth(90);
		peptideResultTbl.getColumn("Unique").setMaxWidth(90);
		peptideResultTbl.getColumn("PSM Votes").setMinWidth(90);
		peptideResultTbl.getColumn("PSM Votes").setMaxWidth(90);
		peptideResultTbl.getColumn("No. Spectra").setMinWidth(90);
		peptideResultTbl.getColumn("No. Spectra").setMaxWidth(90);
		peptideResultTbl.getColumn("Start").setMinWidth(90);
		peptideResultTbl.getColumn("Start").setMaxWidth(90);
		peptideResultTbl.getColumn("End").setMinWidth(90);
		peptideResultTbl.getColumn("End").setMaxWidth(90);
	}

	/**
	 * Update the peptides tables based on the protein selected.
	 * 
	 * @param evt
	 */
	private void queryProteinTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		int row = proteinResultTbl.getSelectedRow();

		// Condition if one row is selected.
		if (row != -1) {

			// Empty tables.
			clearPeptideHitsTable();

			String accession = proteinResultTbl.getValueAt(row, 1).toString();
			if (peptideHits.containsKey(accession)) {
				List<PeptideHit> peptideList = peptideHits.get(accession);
				for (int i = 0; i < peptideList.size(); i++) {
					PeptideHit hit = peptideList.get(i);
					((DefaultTableModel) peptideResultTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							"",
							"", 
							"", 
							"",
							hit.getStart(),
							hit.getEnd()});
				}
			}
		}
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

	}
	/**
	 * @see #queryDnSpectraTableKeyReleased(java.awt.event.MouseEvent)
	 */
	private void queryProteinTableKeyReleased(KeyEvent evt) {
		queryProteinTableMouseClicked(null);
	}

	/**
	 * Clears the peptide result table.
	 */
	private void clearPeptideHitsTable(){
		// Remove peptides from all result tables        	 
		while (peptideResultTbl.getRowCount() > 0) {
			((DefaultTableModel) peptideResultTbl.getModel()).removeRow(0);
		}
	}

	private void updateDbResultsTable(){
		if (proteins != null) {
			Set<String> accessions = proteins.getPeptideHits().keySet();

			Iterator<String> accIter = accessions.iterator();
			int i = 0;
			while(accIter.hasNext()){
				String accession = accIter.next();	
				List<PeptideHit> peptides = proteins.getPeptideHits(accession);
				peptideHits.put(accession, peptides);
				int nPeptides = peptides.size();
				((DefaultTableModel) proteinResultTbl.getModel()).addRow(new Object[]{
						i + 1,
						accession,
						proteins.getProteinHit(accession).getDescription(),
						nPeptides, 
						"",
						"", 
				""});
				i++;
			}
		}
	}
}
