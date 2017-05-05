package de.mpa.client.ui.dialogs;

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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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

import de.mpa.analysis.MetaProteinFactory;
import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.analysis.taxonomy.TaxonomyUtils;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ExportFields;
import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.model.AbstractProject;
import de.mpa.client.model.DatabaseExperiment;
import de.mpa.client.model.MultipleDatabaseExperiments;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.settings.ResultParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ConfirmFileChooser;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.ExperimentAccessor;
import de.mpa.io.ExportHeader;
import de.mpa.io.ResultExporter;

/**
 * Export dialog to export proteins for multiple experiments from one project.
 * @author Robert Heyer
 */
@SuppressWarnings("serial")
public class ExportCombinedExpMetaproteins extends JDialog {

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
	public ExportCombinedExpMetaproteins(ClientFrame owner, String title) {
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
		JXTable expTbl 			= new JXTable(this.createExpTable());
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
					ExportCombinedExpMetaproteins.expList.add(expName);
				}
				// Check Params
				MetaProteinFactory.ClusterRule clusterRule = (MetaProteinFactory.ClusterRule) ExportCombinedExpMetaproteins.this.metaParams.get("clusterRule").getValue();
				System.out.println("SETTINGS CLUSTER RULE: " + clusterRule.name());
				MetaProteinFactory.PeptideRule peptideRule = (MetaProteinFactory.PeptideRule) ExportCombinedExpMetaproteins.this.metaParams.get("peptideRule").getValue();
				System.out.println("SETTINGS PEPTIDE RULE: " + peptideRule.name());
				MetaProteinFactory.TaxonomyRule taxonomyRule = (MetaProteinFactory.TaxonomyRule) ExportCombinedExpMetaproteins.this.metaParams.get("taxonomyRule").getValue();
				System.out.println("SETTINGS TAXONOMY RULE: " + taxonomyRule.name());
				Object value = ExportCombinedExpMetaproteins.this.metaParams.get("FDR").getValue();
				System.out.println("SETTINGS FDR: " +  value);
				try {
					ExportCombinedExpMetaproteins.this.fetchAndExport();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				ExportCombinedExpMetaproteins.this.close();
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
				ExportCombinedExpMetaproteins.this.close();
			}
		});

		// create button-in-button component for advanced result fetching settings
		JButton settingsBtn = new JButton(IconConstants.SETTINGS_SMALL_ICON);
		settingsBtn.setEnabled(!Client.isViewer()); 
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
						true, ExportCombinedExpMetaproteins.this.metaParams) == AdvancedSettingsDialog.DIALOG_CHANGED_ACCEPTED) {
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


		AbstractProject selProject = ClientFrame.getInstance().getProjectPanel().getSelectedProject();
		for (AbstractExperiment exp : selProject.getExperiments()) {
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
	@SuppressWarnings("static-access")
	private void fetchAndExport() throws SQLException, IOException{
		System.out.println("START EXPORT");
		close();


		// Get the connection
		Client client = Client.getInstance();
		Connection conn = client.getConnection();

		client.firePropertyChange("new message", null, "EXPORT IN PROGRESS");
		client.firePropertyChange("indeterminate", false, true); 
		ClientFrame clientFrame = ClientFrame.getInstance();		
		clientFrame.setEnabled(false);

		// Get the experiments 		
		List<AbstractExperiment> experiments = new ArrayList<>();
		AbstractProject project = ClientFrame.getInstance().getProjectPanel().getSelectedProject();
		List<ExperimentAccessor> experimentAccs = new ArrayList<ExperimentAccessor>();
		for (Long current_list_exp : expList) {
			experimentAccs.add(ExperimentAccessor.findExperimentByID(current_list_exp, conn));
		}
		for (ExperimentAccessor experimentAcc : experimentAccs) {
			List<ExpProperty> expProps = ExpProperty.findAllPropertiesOfExperiment(experimentAcc.getExperimentid(), conn);
			DatabaseExperiment exp = new DatabaseExperiment(experimentAcc, expProps, project);
			experiments.add(exp);
		}

		MultipleDatabaseExperiments multipleDatabaseExperiments = new MultipleDatabaseExperiments(expList, "MultipleExperimentObject", new Timestamp(Calendar.getInstance().getTime().getTime()), ClientFrame.getInstance().getProjectPanel().getSelectedProject());

		// Get the path for the export
		@SuppressWarnings("unused")
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
		@SuppressWarnings("unused")
		int flag = 0;
		// Ontology Maps
		Map<String, UniProtUtilities.Keyword> ontologyMap = UniProtUtilities.ONTOLOGY_MAP;
		// Export the Data for the individual Experiments
		DbSearchResult dbSearchResult = multipleDatabaseExperiments.getSearchResult();

		// Get DB Search Result Object
		//DbSearchResult dbSearchResult = exp.getSearchResult();
		// Create Metaproteins
		MetaProteinFactory.determineTaxonomyAndCreateMetaProteins(dbSearchResult, this.metaParams);
		// Export Metaproteins
		ResultExporter.exportMetaProteins(selectedFile.getPath() + "_MP_UniRef50_allSpecies_" +dbSearchResult.getExperimentTitle(), dbSearchResult, exportHeaders);

		// Get metaprotein list.
		ProteinHitList metaProteins = dbSearchResult.getMetaProteins();

		// Create maps for results
		TreeMap<TaxonomyNode, Set<SpectrumMatch>> taxMap = new TreeMap<TaxonomyNode, Set<SpectrumMatch>>();
		TreeMap<String, Set<SpectrumMatch>> speciesMap = new TreeMap<String, Set<SpectrumMatch>>();
		TreeMap<String, Set<SpectrumMatch>> biolFuncMap = new TreeMap<String, Set<SpectrumMatch>>();

		client.firePropertyChange("resetall", 0L, metaProteins.size());
		client.firePropertyChange("resetcur", 0L, metaProteins.size());
		// Iterate over metaprotein hits
		for (ProteinHit metaProt : metaProteins) {
			MetaProteinHit mp = (MetaProteinHit) metaProt;

			client.firePropertyChange("progressmade", true, false);
			// Check for taxonomy level
			TaxonomyNode taxNode = mp.getTaxonomyNode();
			// Bacteria ==2 | Archaea == 2157 
			@SuppressWarnings("unused")
			boolean root = TaxonomyUtils.belongsToGroup(taxNode, 1);
			boolean bacteria = TaxonomyUtils.belongsToGroup(taxNode, 2);
			boolean archaea = TaxonomyUtils.belongsToGroup(taxNode, 2157);
			if (bacteria || archaea) {
				if (mp.getUniProtEntry() != null) {
					// Add ontology
					List<String> keywords = mp.getUniProtEntry().getKeywords();
					for (String keyword : keywords) {
						UniProtUtilities.KeywordCategory KeyWordtype = UniProtUtilities.KeywordCategory.valueOf(ontologyMap.get(keyword).getCategory());

						if (KeyWordtype.equals(UniProtUtilities.KeywordCategory.BIOLOGICAL_PROCESS)) {
							if (biolFuncMap.get(keyword)== null) {
								biolFuncMap.put(keyword, mp.getMatchSet());
							}else{
								Set<SpectrumMatch> ontoSet = biolFuncMap.get(keyword);
								@SuppressWarnings("unused")
								Set<SpectrumMatch> matchSet = mp.getMatchSet();
								ontoSet.addAll(mp.getMatchSet());
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
					taxMap.put(orderTaxNode, mp.getMatchSet());
				}else{
					Set<SpectrumMatch> taxSet = taxMap.get(orderTaxNode);
					taxSet.addAll(mp.getMatchSet());
					taxMap.put(orderTaxNode, taxSet);
				}
				// Add taxonomy Data
				taxonSpecies = TaxonomyUtils.getTaxonNameByRank(taxNode, UniProtUtilities.TaxonomyRank.SPECIES);
				if (speciesMap.get(this.taxonSpecies)== null) {
					speciesMap.put(this.taxonSpecies, mp.getMatchSet());
				}else{
					Set<SpectrumMatch> taxSet = speciesMap.get(this.taxonSpecies);
					taxSet.addAll(mp.getMatchSet());
					speciesMap.put(this.taxonSpecies, taxSet);
				}
			}
		}

		// Export Ontologies
		BufferedWriter ontoWriter = new BufferedWriter(new FileWriter(new File(selectedFile.getPath() + "_Onto_BiolFunction_UniRef50_allSpecies_" +dbSearchResult.getExperimentTitle())));
		for (Map.Entry<String, Set<SpectrumMatch>> taxEntry : biolFuncMap.entrySet()) {
			ontoWriter.write(taxEntry.getKey() + Constants.TSV_FILE_SEPARATOR + taxEntry.getValue().size());
			ontoWriter.newLine();
			ontoWriter.flush();
		}
		ontoWriter.close();

		// Export Taxonomy
		BufferedWriter taxWriter = new BufferedWriter(new FileWriter(new File(selectedFile.getPath() + "_Tax_Order_UniRef50_allSpecies_" +dbSearchResult.getExperimentTitle())));
		for (Map.Entry<TaxonomyNode, Set<SpectrumMatch>> taxEntry : taxMap.entrySet()) {
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
		for (Map.Entry<String, Set<SpectrumMatch>> taxEntry : speciesMap.entrySet()) {
			taxSpeciesWriter.write(taxEntry.getKey() + Constants.TSV_FILE_SEPARATOR + taxEntry.getValue().size());
			taxSpeciesWriter.newLine();
			taxSpeciesWriter.flush();
		}
		taxSpeciesWriter.close();



		flag++;
		//System.out.println("Finish Experiment: " +  exp.getID()+ " " +flag  + " of " + experiments.size() + ": " +  exp.getTitle());

		System.out.println("EXPORT FINISHED");
		clientFrame.setEnabled(true);
		client.firePropertyChange("new message", null, "EXPORT FINISHED");
		client.firePropertyChange("indeterminate", true, false);
		expList.clear();
	}


}
