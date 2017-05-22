package de.mpa.client.ui.resultspanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.settings.ResultParameters;
import de.mpa.client.ui.sharedelements.PanelConfig;
import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.db.mysql.accessor.SearchHit;
import de.mpa.model.analysis.MetaProteinFactory;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.PeptideSpectrumMatch;

/**
 * Class holding the spectrum result view (Prototype Robert).
 * 
 * @author R. Heyer
 */
@SuppressWarnings("serial")
public class SpectrumResultPanel extends JPanel {

	/**
	 * Table header Identifiers
	 */
	final int SPEC_SELECTION = 0;
	final int SPEC_SPECTRUM = 1;
	final int SPEC_PEAKS = 2;
	final int SPEC_TIC = 3;
	final int SPEC_SNR = 4;
	final int SPEC_XTANDEM = 5;
	final int SPEC_OMSSA = 6;
//	final int SPEC_INSPECT = 7;
//	final int SPEC_CRUX = 8;
	final int SPEC_MASCOT = 7;
//	final int SPEC_SPECLIB = 10;
	final int SPEC_PEPTIDE = 8;
	final int SPEC_PROTEIN = 9;

	/**
	 * The spectrum table
	 */
	private JXTable spectrumTbl;

	/**
	 * Default Constructor
	 */
	public SpectrumResultPanel(){
        this.initComponents();
	}

	/**
	 * Init components
	 */
	private void initComponents() {
        setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));

		// Set up spectrum table
        setupSpectrumTableProperties();

		// Lay out spectrum table
		JPanel specTblPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		JScrollPane spectrumScp = new JScrollPane(this.spectrumTbl,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		specTblPnl.add(spectrumScp, CC.xy(2, 2));
		
		// Create results fetch button
		JButton fetchBtn = new JButton(IconConstants.GO_DB_ICON);
		fetchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Process result fetching in background thread
				new SwingWorker<Object, Object>() {
					@Override
					protected Object doInBackground() throws Exception {
                        refreshSpectrumTable();
						return null;
					}
					@Override
					protected void done() {
						// TODO: maybe do something
					}
				};
			}
		});
		
		JXTitledPanel specTblTtlPnl = PanelConfig.createTitledPanel("Spectrum Table", specTblPnl, null, fetchBtn);
		
		// Lay out components
        add(specTblTtlPnl, CC.xy(2, 2));
        add(PanelConfig.createTitledPanel("Title", new JLabel("stuff")), CC.xy(2, 4));
	}

	/**
	 * This method sets up the spectrum results table.
	 */
	private void setupSpectrumTableProperties() {
	
		// Create protein table model
		TableModel spectrumTblMdl = new DefaultTableModel() {
			// instance initializer block
			{
                this.setColumnIdentifiers(new Object[] {
						"#", "Spectrum", "Peaks", "TIC", "SNR",
						"X", "O", "M", "Peptide", "Protein"});
			}
	
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
	
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case SPEC_SELECTION:
				case SPEC_XTANDEM:
				case SPEC_OMSSA:
				case SPEC_MASCOT:
				case SPEC_PEAKS:
					return Integer.class;
				case SPEC_TIC:
				case SPEC_SNR:
					return Double.class;
				case SPEC_SPECTRUM:
				case SPEC_PEPTIDE:
				case SPEC_PROTEIN:
				default:
					return String.class;
				}
			}
		};
        spectrumTbl = new JXTable(spectrumTblMdl) ;
	}

	/**
	 * Refreshes the spectrum table contents.
	 */
	protected void refreshSpectrumTable() {

		// Gets the result object.
		DbSearchResult result = Client.getInstance().getDatabaseSearchResult();

		if (result != null && !result.isEmpty()) {
			// Get spectrum List
			MetaProteinFactory.determineTaxonomyAndCreateMetaProteins(new ResultParameters());
			ArrayList<PeptideSpectrumMatch> specSet = result.getAllPSMS();

			// Notify status bar
			Client.getInstance().firePropertyChange("new message", null, "POPULATING TABLES");
			Client.getInstance().firePropertyChange("resetall", null, (long) specSet.size());
			Client.getInstance().firePropertyChange("resetcur", null, (long) specSet.size());

			// Gather models
			DefaultTableModel specTblMdl = (DefaultTableModel) this.spectrumTbl.getModel();

			// Iterate meta-proteins
			for (PeptideSpectrumMatch specMatch : specSet) {

				//SearchEngines
				boolean isXtandem = false, isOmssa = false, isCrux = false, isInspect = false, isMascot = false, isSpecLib = false;
				PeptideSpectrumMatch psm = (PeptideSpectrumMatch) specMatch;
				List<SearchHit> searchHits = psm.getSearchHits();
				if (searchHits != null) {
					for (SearchHit searchHit : searchHits) {
						switch (searchHit.getType()) {
						case XTANDEM:
							isXtandem = true;
							break;
						case OMSSA:
							isOmssa = true;
							break;
						case MASCOT:
							isMascot = true;
							break;
						}
					}
				} 
				// Insert spectrum data into table
				// TODO: use actual values
				specTblMdl.addRow(new Object[] {
						true,
						specMatch.getSpectrumID(), 
						1,
						1,
						1,
						isXtandem,
						isOmssa,
						isMascot,
						isSpecLib,
						"NAME",
						"NAME" });
				
				Client.getInstance().firePropertyChange("progressmade", false, true);
			}
		}
		Client.getInstance().firePropertyChange("new message", null,
				"POPULATING TABLES FINISHED");
	}
	
}
