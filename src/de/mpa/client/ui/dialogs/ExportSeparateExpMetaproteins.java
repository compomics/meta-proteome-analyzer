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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.UniProtUtilities.Keyword;
import de.mpa.analysis.UniProtUtilities.KeywordCategory;
import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.analysis.taxonomy.TaxonomyUtils;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ExportFields;
import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.model.AbstractProject;
import de.mpa.client.model.DatabaseExperiment;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinFactory;
import de.mpa.client.model.dbsearch.MetaProteinFactory.ClusterRule;
import de.mpa.client.model.dbsearch.MetaProteinFactory.PeptideRule;
import de.mpa.client.model.dbsearch.MetaProteinFactory.TaxonomyRule;
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
import de.mpa.io.ResultExporter.ExportHeaderType;

/**
 * Export dialog to export proteins for multiple experiments from one project.
 * @author Robert Heyer
 */
public class ExportSeparateExpMetaproteins extends JDialog {

	/**
	 * The client frame
	 */
	private ClientFrame owner;

	/**
	 * The local map of meta-protein generation-related parameters.
	 */
	private ResultParameters metaParams;
	
//	/**
//	 * Textfield for project ID
//	 */
//	private JTextField projectIDtxt;	
	
	/**
	 * List of the names of the selected experiments
	 */
	private static LinkedList<Long> expList = new LinkedList<Long>();
	

	private String taxonSpecies;
	/**
	 * @param owner. The owner of the dialog.
	 * @param title. The title of the dialog.
	 */
	public ExportSeparateExpMetaproteins(ClientFrame owner, String title) {
		super(owner, title);
		this.metaParams = new ResultParameters();
		this.owner = owner;
		initComponents();
		showDialog();
	}
	
	/**
	 * Initializes and lays out all components inside this dialog grouped by sections identifiers.
	 */
	private void initComponents() {
		// Create BLAST dialog
		JPanel selectExperimentsDlgPnl 	= new JPanel(new FormLayout("5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g"));		
		final JXTable expTbl 			= new JXTable(createExpTable());
		expTbl.setAutoResizeMode(JXTable.AUTO_RESIZE_LAST_COLUMN);
		JScrollPane expTblSp = new JScrollPane(expTbl);		
		selectExperimentsDlgPnl.add(expTblSp, CC.xyw(2,  2,7));
		expTbl.setPreferredSize(new Dimension(400,400));
		// Configure 'OK' button
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO Robbies update task
				int[] selectedRows = expTbl.getSelectedRows();
				for (int i : selectedRows) {
					Long expName = (Long)expTbl.getValueAt(i, 0);
					expList.add(expName);
				}
				// Check Params
				ClusterRule clusterRule = (ClusterRule) metaParams.get("clusterRule").getValue();
				System.out.println("SETTINGS CLUSTER RULE: " + clusterRule.name());
				PeptideRule peptideRule = (PeptideRule) metaParams.get("peptideRule").getValue();
				System.out.println("SETTINGS PEPTIDE RULE: " + peptideRule.name());
				TaxonomyRule taxonomyRule = (TaxonomyRule) metaParams.get("taxonomyRule").getValue();
				System.out.println("SETTINGS TAXONOMY RULE: " + taxonomyRule.name());
				Object value = metaParams.get("FDR").getValue();
				System.out.println("SETTINGS FDR: " +  value);
				//okBtn.setEnabled(false);
					try {
						fetchAndExport();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				close();
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
				close();
			}
		});
	

		// create button-in-button component for advanced result fetching settings
		final JButton settingsBtn = new JButton(IconConstants.SETTINGS_SMALL_ICON);
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
						true, metaParams) == AdvancedSettingsDialog.DIALOG_CHANGED_ACCEPTED) {
				}
				
				
			}
		});
		
		selectExperimentsDlgPnl.add(okBtn,CC.xy(2, 4) );
		selectExperimentsDlgPnl.add(cancelBtn,CC.xy(4, 4) );
		selectExperimentsDlgPnl.add(settingsBtn,CC.xy(6, 4) );
		Container cp = this.getContentPane();
		cp.setLayout(new FormLayout("5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu, f:p:g"));
		cp.setPreferredSize(new Dimension(500,500));
		cp.add(selectExperimentsDlgPnl, CC.xyw(2,  2,7));	
				
		}

	/**
	 * This method shows the dialog.
	 */
	private void showDialog() {
		// Configure size and position
		this.pack();
		this.setResizable(true);
		ScreenConfig.centerInScreen(this);

		// Show dialog
		this.setVisible(true);
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
		dispose();
	}
	
	/**
	 * Fetch the results from the database and create outputs
	 * @throws SQLException
	 * @throws IOException 
	 */
	private void fetchAndExport() throws SQLException, IOException{
		System.out.println("START EXPORT");
		this.close();
		
		
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
		for (Long current_list_exp : this.expList) {
			experimentAccs.add(ExperimentAccessor.findExperimentByID(current_list_exp, conn));
		}
		for (ExperimentAccessor experimentAcc : experimentAccs) {
			List<ExpProperty> expProps = ExpProperty.findAllPropertiesOfExperiment(experimentAcc.getExperimentid(), conn);
			DatabaseExperiment exp = new DatabaseExperiment(experimentAcc, expProps, project);
			experiments.add(exp);
		}
		
		// Get the path for the export
		ExportFields exportFields = ExportFields.getInstance();
		JFileChooser chooser = new ConfirmFileChooser(owner.getLastSelectedFolder());
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
			exportHeaders.add(new ExportHeader(1, "Meta-Protein No.",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(2, "Meta-Protein Accession",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(3, "Meta-Protein Description",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(4, "Meta-Protein Taxonomy",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(5, "Superkingdom",  			ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(6, "Kingdom",  				ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(7, "Phylum",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(8, "Class",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(9, "Order",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(10, "Family",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(11, "Genus",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(12, "Species",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(13, "Meta-Protein UniRef100",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(14, "Meta-Protein UniRef90",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(15, "Meta-Protein UniRef50",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(16, "Meta-Protein KO",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(17, "Meta-Protein EC",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(18, "Peptide Count",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(19, "Spectral Count",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(20, "Proteins",  ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(21, "Peptides",  ExportHeaderType.METAPROTEINS));

		//TODO FLAG for testing
		int flag = 0;
		// Ontology Maps
		 Map<String, Keyword> ontologyMap = UniProtUtilities.ONTOLOGY_MAP;
		// Export the Data for the individual Experiments
		  
		
		client.firePropertyChange("resetall", 0L, experiments.size());
		client.firePropertyChange("resetcur", 0L, experiments.size());
		for (AbstractExperiment exp : experiments) {
			client.firePropertyChange("progressmade", true, false);
			// Get DB Search Result Object
			DbSearchResult dbSearchResult = exp.getSearchResult();
			// Create Metaproteins
			MetaProteinFactory.determineTaxonomyAndCreateMetaProteins(dbSearchResult, metaParams );
			// Export Metaproteins
			ResultExporter.exportMetaProteins(selectedFile.getPath() + "_MP_UniRef50_allSpecies_" +dbSearchResult.getExperimentTitle(), dbSearchResult, exportHeaders);
			
			// Get metaprotein list.
			ProteinHitList metaProteins = dbSearchResult.getMetaProteins();
			
			// Create maps for results
			TreeMap<TaxonomyNode, Set<SpectrumMatch>> taxMap = new TreeMap<TaxonomyNode, Set<SpectrumMatch>>();
			TreeMap<String, Set<SpectrumMatch>> speciesMap = new TreeMap<String, Set<SpectrumMatch>>();
			TreeMap<String, Set<SpectrumMatch>> biolFuncMap = new TreeMap<String, Set<SpectrumMatch>>();
			
			// Iterate over metaprotein hits
			for (ProteinHit metaProt : metaProteins) {

				MetaProteinHit mp = (MetaProteinHit)metaProt;
				
				// Check for taxonomy level
				TaxonomyNode taxNode = mp.getTaxonomyNode();
				// Bacteria ==2 | Archaea == 2157 
				boolean root = TaxonomyUtils.belongsToGroup(taxNode, 1);
				boolean bacteria = TaxonomyUtils.belongsToGroup(taxNode, 2);
				boolean archaea = TaxonomyUtils.belongsToGroup(taxNode, 2157);
				if (bacteria || archaea) {
					
					// Add ontology
					List<String> keywords = mp.getUniProtEntry().getKeywords();
					for (String keyword : keywords) {
						KeywordCategory KeyWordtype = KeywordCategory.valueOf(ontologyMap.get(keyword).getCategory());
						
						if (KeyWordtype.equals(KeywordCategory.BIOLOGICAL_PROCESS)) {
							if (biolFuncMap.get(keyword)== null) {
								biolFuncMap.put(keyword, mp.getMatchSet());
							}else{
								Set<SpectrumMatch> ontoSet = biolFuncMap.get(keyword);
								Set<SpectrumMatch> matchSet = mp.getMatchSet();
								ontoSet.addAll(mp.getMatchSet());
								biolFuncMap.put(keyword, ontoSet);
							}
						}
					}
					
					// Add taxonomy Data
					TaxonomyNode orderTaxNode = taxNode.getParentNode(TaxonomyRank.ORDER);
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
					taxonSpecies = TaxonomyUtils.getTaxonNameByRank(taxNode, TaxonomyRank.SPECIES);
					if (speciesMap.get(taxonSpecies)== null) {
						speciesMap.put(taxonSpecies, mp.getMatchSet());
					}else{
						Set<SpectrumMatch> taxSet = speciesMap.get(taxonSpecies);
						taxSet.addAll(mp.getMatchSet());
						speciesMap.put(taxonSpecies, taxSet);
					}
				}
			}
			
			// Export Ontologies 
			BufferedWriter ontoWriter = new BufferedWriter(new FileWriter(new File(selectedFile.getPath() + "_Onto_BiolFunction_UniRef50_allSpecies_" +dbSearchResult.getExperimentTitle())));
			for (Entry<String, Set<SpectrumMatch>> taxEntry : biolFuncMap.entrySet()) {
				ontoWriter.write(taxEntry.getKey() + Constants.TSV_FILE_SEPARATOR + taxEntry.getValue().size());
				ontoWriter.newLine();
				ontoWriter.flush();
			}
			ontoWriter.close();
			
			// Export Taxonomy 
			BufferedWriter taxWriter = new BufferedWriter(new FileWriter(new File(selectedFile.getPath() + "_Tax_Order_UniRef50_allSpecies_" +dbSearchResult.getExperimentTitle())));
			for (Entry<TaxonomyNode, Set<SpectrumMatch>> taxEntry : taxMap.entrySet()) {
				TaxonomyNode taxnode = taxEntry.getKey();
				String superkingdom;
				String kingdom;
				String phylum;
				String classe;
				String order;
				if (taxnode.getID() != 0) {
					 superkingdom 	= taxnode.getParentNode(TaxonomyRank.SUPERKINGDOM).getName();
					 kingdom		= taxnode.getParentNode(TaxonomyRank.KINGDOM).getName();
					 phylum			= taxnode.getParentNode(TaxonomyRank.PHYLUM).getName();
					 classe 		= taxnode.getParentNode(TaxonomyRank.CLASS).getName();
					 order 			= taxnode.getParentNode(TaxonomyRank.ORDER).getName();
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
			for (Entry<String, Set<SpectrumMatch>> taxEntry : speciesMap.entrySet()) {
				taxSpeciesWriter.write(taxEntry.getKey() + Constants.TSV_FILE_SEPARATOR + taxEntry.getValue().size());
				taxSpeciesWriter.newLine();
				taxSpeciesWriter.flush();
			}
			taxSpeciesWriter.close();
			
			
			
			flag++;
			System.out.println("Finish Experiment: " +  exp.getID()+ " " +flag  + " of " + experiments.size() + ": " +  exp.getTitle());
		}
		System.out.println("EXPORT FINISHED");
		clientFrame.setEnabled(true);
		client.firePropertyChange("new message", null, "EXPORT FINISHED");
		client.firePropertyChange("indeterminate", true, false); 
		this.expList.clear();
	}
	
	
}
