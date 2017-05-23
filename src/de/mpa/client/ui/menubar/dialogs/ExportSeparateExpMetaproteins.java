package de.mpa.client.ui.menubar.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ExportFields;
import de.mpa.client.settings.ResultParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.inputpanel.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.sharedelements.ScreenConfig;
import de.mpa.client.ui.sharedelements.dialogs.ConfirmFileChooser;
import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.db.mysql.accessor.ExpProperty;
import de.mpa.db.mysql.accessor.ExperimentTableAccessor;
import de.mpa.io.ExportHeader;
import de.mpa.io.ResultExporter;
import de.mpa.model.MPAExperiment;
import de.mpa.model.MPAProject;
import de.mpa.model.analysis.MetaProteinFactory;
import de.mpa.model.analysis.UniProtUtilities;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.MetaProteinHit;
import de.mpa.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.model.taxonomy.TaxonomyNode;
import de.mpa.model.taxonomy.TaxonomyUtils;

/**
 * Export dialog to export proteins for multiple experiments from one project.
 * @author Robert Heyer
 */
@SuppressWarnings("serial")
public class ExportSeparateExpMetaproteins extends JDialog {

	/**
	 * The client frame
	 */
	private final ClientFrame owner;

	/**
	 * The local map of meta-protein generation-related parameters.
	 */
	private final ResultParameters metaParams;

	//	/**
	//	 * Textfield for project ID
	//	 */
	//	private JTextField projectIDtxt;	

	/**
	 * List of the names of the selected experiments
	 */
	private static final LinkedList<Long> expList = new LinkedList<Long>();


	private String taxonSpecies;
	/**
	 * @param owner. The owner of the dialog.
	 * @param title. The title of the dialog.
	 */
	public ExportSeparateExpMetaproteins(ClientFrame owner, String title) {
		super(owner, title);
		metaParams = new ResultParameters();
		this.owner = owner;
		this.initComponents();
		this.showDialog();
	}

	/**
	 * Initializes and lays out all components inside this dialog grouped by sections identifiers.
	 */
	private void initComponents() {
		// Create BLAST dialog
		JPanel selectExperimentsDlgPnl 	= new JPanel(new FormLayout("5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g"));		
		JXTable expTbl = new JXTable(this.createExpTable());
		expTbl.setAutoResizeMode(JXTable.AUTO_RESIZE_LAST_COLUMN);
		JScrollPane expTblSp = new JScrollPane(expTbl);		
		selectExperimentsDlgPnl.add(expTblSp, CC.xyw(2,  2,7));
		// Configure 'OK' button
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = expTbl.getSelectedRows();
				for (int i : selectedRows) {
					Long expName = (Long)expTbl.getValueAt(i, 0);
					ExportSeparateExpMetaproteins.expList.add(expName);
				}
				try {
					ExportSeparateExpMetaproteins.this.fetchAndExport();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				ExportSeparateExpMetaproteins.this.close();
			}
		});

		// Configure 'Cancel' button
		JButton cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		cancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ExportSeparateExpMetaproteins.this.close();
			}
		});


		// create button-in-button component for advanced result fetching settings
		JButton settingsBtn = new JButton(IconConstants.SETTINGS_SMALL_ICON);
		settingsBtn.setEnabled(true); 
		settingsBtn.setRolloverIcon(IconConstants.SETTINGS_SMALL_ROLLOVER_ICON);
		settingsBtn.setPressedIcon(IconConstants.SETTINGS_SMALL_PRESSED_ICON);		
		settingsBtn.setPreferredSize(new Dimension(23, 23));
		settingsBtn.setToolTipText("Advanced Settings");
		settingsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (AdvancedSettingsDialog.showDialog(
						ClientFrame.getInstance(),
						"Result Fetching settings",
						true, ExportSeparateExpMetaproteins.this.metaParams) == AdvancedSettingsDialog.DIALOG_CHANGED_ACCEPTED) {
				}


			}
		});

		selectExperimentsDlgPnl.add(okBtn,CC.xy(2, 4) );
		selectExperimentsDlgPnl.add(cancelBtn,CC.xy(4, 4) );
		selectExperimentsDlgPnl.add(settingsBtn,CC.xy(6, 4) );
		Container cp = getContentPane();
		cp.setLayout(new FormLayout("5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g"));
		cp.setPreferredSize(new Dimension(500,500));
		cp.add(selectExperimentsDlgPnl, CC.xyw(2,  2,7));	

	}

	/**
	 * This method shows the dialog.
	 */
	private void showDialog() {
		// Configure size and position
		pack();
		setResizable(true);
		ScreenConfig.centerInScreen(this);

		// Show dialog
		setVisible(true);
	}

	/**
	 * Method to create the experiment table.
	 * @return Experiment table. The table with all experiments of the project selected in the project panel.
	 */
	private DefaultTableModel createExpTable() {
		DefaultTableModel dtm = new DefaultTableModel( new Object[]{"ID","Experiments"},0);
		MPAProject selProject = ClientFrame.getInstance().getProjectPanel().getSelectedProject();
		for (MPAExperiment exp : selProject.getExperiments()) {
			dtm.addRow(new Object[]{exp.getID(),exp.getTitle()});
		}
		return dtm;
	}

	/**
	 * Close method for the dialog.
	 */
	private void close() {
		this.dispose();
	}

	/**
	 * Fetch the results from the database and create outputs
	 * @throws SQLException
	 * @throws IOException 
	 */
	private void fetchAndExport() throws SQLException, IOException{
		close();

		// Get the connection
		Client client = Client.getInstance();
		Connection conn = client.getConnection();

		client.firePropertyChange("new message", null, "EXPORT IN PROGRESS");
		client.firePropertyChange("indeterminate", false, true); 
		ClientFrame clientFrame = ClientFrame.getInstance();




		clientFrame.setEnabled(false);

		// Get the experiments		
		List<MPAExperiment> experiments = new ArrayList<>();
		MPAProject project = ClientFrame.getInstance().getProjectPanel().getSelectedProject();
		List<ExperimentTableAccessor> experimentAccs = new ArrayList<ExperimentTableAccessor>();
		for (Long current_list_exp : expList) {
			experimentAccs.add(ExperimentTableAccessor.findExperimentByID(current_list_exp, conn));
		}
		for (ExperimentTableAccessor experimentAcc : experimentAccs) {
			List<ExpProperty> expProps = ExpProperty.findAllPropertiesOfExperiment(experimentAcc.getExperimentid(), conn);
			MPAExperiment exp = new MPAExperiment(experimentAcc.getExperimentid(), project);
			experiments.add(exp);
		}

		// Get the path for the export
		ExportFields exportFields = ExportFields.getInstance();
		JFileChooser chooser = new ConfirmFileChooser(this.owner.getLastSelectedFolder());
		chooser.setFileFilter(Constants.CSV_FILE_FILTER);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setMultiSelectionEnabled(false);
		File selectedFile = null;
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
		}

		// Get Metaprotein export headers
		ArrayList<ExportHeader> exportHeaders = new ArrayList<ExportHeader>();
		exportHeaders.add(new ExportHeader(1, "Meta-Protein No.",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(2, "Meta-Protein Accession",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(3, "Meta-Protein Description",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(4, "Meta-Protein Taxonomy",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(5, "Superkingdom",  			ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(6, "Kingdom",  				ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(7, "Phylum",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(8, "Class",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(9, "Order",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(10, "Family",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(11, "Genus",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(12, "Species",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(13, "Meta-Protein UniRef100",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(14, "Meta-Protein UniRef90",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(15, "Meta-Protein UniRef50",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(16, "Meta-Protein KO",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(17, "Meta-Protein EC",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(18, "Peptide Count",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(19, "Spectral Count",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(20, "Proteins",  ResultExporter.ExportHeaderType.METAPROTEINS));
		exportHeaders.add(new ExportHeader(21, "Peptides",  ResultExporter.ExportHeaderType.METAPROTEINS));

		//TODO FLAG for testing
		int flag = 0;
		// Ontology Maps
		Map<String, UniProtUtilities.Keyword> ontologyMap = UniProtUtilities.ONTOLOGY_MAP;
		// Export the Data for the individual Experiments


		client.firePropertyChange("resetall", 0L, experiments.size());
		client.firePropertyChange("resetcur", 0L, experiments.size());
		for (MPAExperiment exp : experiments) {
			client.firePropertyChange("progressmade", true, false);
			
			// TODO: fill up with stuff
			// Get DB Search Result Object
			DbSearchResult dbSearchResult = new DbSearchResult(null, null, null);
			
			// Create Metaproteins
			MetaProteinFactory.determineTaxonomyAndCreateMetaProteins(Client.getInstance().getDatabaseSearchResult(), this.metaParams);
			// Export Metaproteins
			ResultExporter.exportMetaProteins(selectedFile.getPath() + "_MP_UniRef50_allSpecies_" +dbSearchResult.getExperimentTitle(), dbSearchResult, exportHeaders);

			// Get metaprotein list.
			ArrayList<MetaProteinHit> metaProteins = dbSearchResult.getMetaProteins();

			// Create maps for results
			TreeMap<TaxonomyNode, Set<PeptideSpectrumMatch>> taxMap = new TreeMap<TaxonomyNode, Set<PeptideSpectrumMatch>>();
			TreeMap<String, Set<PeptideSpectrumMatch>> speciesMap = new TreeMap<String, Set<PeptideSpectrumMatch>>();
			TreeMap<String, Set<PeptideSpectrumMatch>> biolFuncMap = new TreeMap<String, Set<PeptideSpectrumMatch>>();

			// Iterate over metaprotein hits
			for (MetaProteinHit mp : metaProteins) {

				// Check for taxonomy level
				TaxonomyNode taxNode = mp.getTaxonomyNode();
				// Bacteria ==2 | Archaea == 2157 
				boolean root = TaxonomyUtils.belongsToGroup(taxNode, 1);
				boolean bacteria = TaxonomyUtils.belongsToGroup(taxNode, 2);
				boolean archaea = TaxonomyUtils.belongsToGroup(taxNode, 2157);
				if (bacteria || archaea) {
					if (mp.getUniProtEntry() != null) {
						// Add ontology
						List<String> keywords = mp.getUniProtEntry().getKeywords();
						for (String keyword : keywords) {
							if (ontologyMap.containsKey(keyword)) {
								UniProtUtilities.KeywordCategory KeyWordtype = UniProtUtilities.KeywordCategory.valueOf(ontologyMap.get(keyword).getCategory());

								if (KeyWordtype.equals(UniProtUtilities.KeywordCategory.BIOLOGICAL_PROCESS)) {
									if (biolFuncMap.get(keyword)== null) {
										biolFuncMap.put(keyword, mp.getPSMS());
									}else{
										Set<PeptideSpectrumMatch> ontoSet = biolFuncMap.get(keyword);
										Set<PeptideSpectrumMatch> matchSet = mp.getPSMS();
										ontoSet.addAll(mp.getPSMS());
										biolFuncMap.put(keyword, ontoSet);
									}
								}
							}
						}

						// Add taxonomy Data
						TaxonomyNode orderTaxNode = taxNode.getParentNode(UniProtUtilities.TaxonomyRank.ORDER);
						// check whether alread in the map
						boolean alreadyInMap = false;
						for (TaxonomyNode taxMapNode : taxMap.keySet()) {
							if (taxMapNode.getID() == orderTaxNode.getID()) {
								alreadyInMap = true;
							}
						}

						if (alreadyInMap == false) {
							taxMap.put(orderTaxNode, mp.getPSMS());
						}else{
							Set<PeptideSpectrumMatch> taxSet = taxMap.get(orderTaxNode);
							taxSet.addAll(mp.getPSMS());
							taxMap.put(orderTaxNode, taxSet);
						}
						// Add taxonomy Data
						taxonSpecies = TaxonomyUtils.getTaxonNameByRank(taxNode, UniProtUtilities.TaxonomyRank.SPECIES);
						if (speciesMap.get(this.taxonSpecies)== null) {
							speciesMap.put(this.taxonSpecies, mp.getPSMS());
						}else{
							Set<PeptideSpectrumMatch> taxSet = speciesMap.get(this.taxonSpecies);
							taxSet.addAll(mp.getPSMS());
							speciesMap.put(this.taxonSpecies, taxSet);
						}
					}
				}
			}

			// Export Ontologies
			BufferedWriter ontoWriter = new BufferedWriter(new FileWriter(new File(selectedFile.getPath() + "_Onto_BiolFunction_UniRef50_allSpecies_" +dbSearchResult.getExperimentTitle())));
			for (Map.Entry<String, Set<PeptideSpectrumMatch>> taxEntry : biolFuncMap.entrySet()) {
				ontoWriter.write(taxEntry.getKey() + Constants.TSV_FILE_SEPARATOR + taxEntry.getValue().size());
				ontoWriter.newLine();
				ontoWriter.flush();
			}
			ontoWriter.close();

			// Export Taxonomy
			BufferedWriter taxWriter = new BufferedWriter(new FileWriter(new File(selectedFile.getPath() + "_Tax_Order_UniRef50_allSpecies_" +dbSearchResult.getExperimentTitle())));
			for (Map.Entry<TaxonomyNode, Set<PeptideSpectrumMatch>> taxEntry : taxMap.entrySet()) {
				TaxonomyNode taxnode = taxEntry.getKey();
				String superkingdom;
				String kingdom;
				String phylum;
				String classe;
				String order;
				if (taxnode.getID() != 0) {
					superkingdom 	= taxnode.getParentNode(UniProtUtilities.TaxonomyRank.SUPERKINGDOM).getName();
					kingdom		= taxnode.getParentNode(UniProtUtilities.TaxonomyRank.KINGDOM).getName();
					phylum			= taxnode.getParentNode(UniProtUtilities.TaxonomyRank.PHYLUM).getName();
					classe 		= taxnode.getParentNode(UniProtUtilities.TaxonomyRank.CLASS).getName();
					order 			= taxnode.getParentNode(UniProtUtilities.TaxonomyRank.ORDER).getName();
				}else {
					superkingdom	= "unknown";
					kingdom		= "unknown";
					phylum			= "unknown";
					classe 		= "unknown";
					order 			= "unknown";
				}

				taxWriter.write(order + Constants.TSV_FILE_SEPARATOR+
						superkingdom +	Constants.TSV_FILE_SEPARATOR+
						kingdom + Constants.TSV_FILE_SEPARATOR +
						phylum +	Constants.TSV_FILE_SEPARATOR+
						classe + Constants.TSV_FILE_SEPARATOR +
						order + Constants.TSV_FILE_SEPARATOR +
						taxEntry.getValue().size());
				taxWriter.newLine();
				taxWriter.flush();
			}
			taxWriter.close();

			// Export Species Taxonomy
			//TODO ADD Taxonomic path
			BufferedWriter taxSpeciesWriter = new BufferedWriter(new FileWriter(new File(selectedFile.getPath() + "_Tax_Species_UniRef50_allSpecies_" +dbSearchResult.getExperimentTitle())));
			for (Map.Entry<String, Set<PeptideSpectrumMatch>> taxEntry : speciesMap.entrySet()) {
				taxSpeciesWriter.write(taxEntry.getKey() + Constants.TSV_FILE_SEPARATOR + taxEntry.getValue().size());
				taxSpeciesWriter.newLine();
				taxSpeciesWriter.flush();
			}
			taxSpeciesWriter.close();



			flag++;
		}
		clientFrame.setEnabled(true);
		client.firePropertyChange("new message", null, "EXPORT FINISHED");
		client.firePropertyChange("indeterminate", true, false);
		expList.clear();
	}


}
