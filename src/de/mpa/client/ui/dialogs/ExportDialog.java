package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ExportFields;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ConfirmFileChooser;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.io.ExportHeader;
import de.mpa.io.ResultExporter;
import de.mpa.io.ResultExporter.ExportHeaderType;

/**
 * This dialog enables the result export of proteins, peptides. PSMs, meta-protein, taxonomies etc.
 * 
 * @author T. Muth
 *
 */
public class ExportDialog extends JDialog {
	
	/**
	 * The ClientFrame
	 */
	private ClientFrame owner;
	
	/**
	 * The instance of the client.
	 */
	private Client client = Client.getInstance();
	
	/**
	 * Class containing the default values for the checkboxes
	 */
	private ExportFields exportFields;

	/**
	 * The tab pane for all dialogs
	 */
	private JTabbedPane tabPane;
	
	/**
	 * The protein export panel.
	 */
	private JPanel proteinPanel;
	private JCheckBox proteinNumberCbx;
	private JCheckBox proteinAccessionCbx;
	private JCheckBox proteinDescriptionCbx;
	private JCheckBox proteinSpeciesCbx;
	private JCheckBox proteinSeqCoverageCbx;
	private JCheckBox proteinMolWeightCbx;
	private JCheckBox proteinPiCbx;
	private JCheckBox proteinPepCountCbx;
	private JCheckBox proteinSpecCountCbx;
	private JCheckBox proteinEmPAICbx;
	private JCheckBox proteinNSAFCbx;
	private JCheckBox proteinSequenceCbx;
	private JCheckBox proteinPeptidesCbx;
	
	/**
	 * The peptide export panel
	 */
	private JPanel peptidePanel;
	private JCheckBox peptideTaxGroup;
	private JCheckBox peptideProteinAccessionsCbx;
	private JCheckBox peptideSequenceCbx;
	private JCheckBox peptideProtCountCbx;
	private JCheckBox peptideSpecCountCbx;
	private JCheckBox peptideNumberCbx;
	private AbstractButton sharedPeptidesOnlyCbx;
	private JCheckBox uniquePeptidesOnlyCbx;
	private JCheckBox peptideTaxIdCbx;
	private JCheckBox peptideTaxRank;
	
	/**
	 * The psm export panel.
	 */
	private JPanel psmsPanel;
	private JCheckBox psmNumberCbx;
	private JCheckBox psmProteinAccessionCbx;
	private JCheckBox psmPeptideSequenceCbx;
	private JCheckBox psmSpectrumTitleCbx;
	private JCheckBox psmScoreCbx;
	private JCheckBox psmSearchEngineCbx;
	private JCheckBox psmQValueCbx;
	private JCheckBox psmChargeCbx;
	
	/**
	 * The metaproteins export panel
	 */
	private JPanel metaproteinPanel;
	private JCheckBox metaproteinNumberCbx;
	private JCheckBox metaproteinAccessionsCbx;
	private JCheckBox metaproteinDescriptionCbx;
	private JCheckBox metaproteinTaxonomyCbx;
	private JCheckBox metaproteinSeqCoverageCbx;
	private AbstractButton metaproteinNSAFCbx;
	private AbstractButton metaproteinEmPAICbx;
	private AbstractButton metaproteinSpecCountCbx;
	private AbstractButton metaproteinPepCountCbx;
	private JCheckBox metaproteinAIdCbx;
	private JCheckBox metaproteinsPeptidesCbx;

	/**
	 * The taxonomy export panel
	 */
	private JPanel taxonomyPanel;
	private JCheckBox taxonomySpecificSpecCountCbx;
	private JCheckBox taxonomyKingdomCbx;
	private JCheckBox taxonomyPhylumCbx;
	private JCheckBox taxonomyOrderCbx;
	private JCheckBox taxonomySuperKingdomCbx;
	private JCheckBox taxonomyClassCbx;
	private JCheckBox taxonomyFamilyCbx;
	private JCheckBox taxonomyGenusCbx;
	private JCheckBox taxonomySpeciesCbx;
	private JCheckBox taxonomyUnclassifiedCbx;
	private JCheckBox taxonomySpecificPeptidesCbx;
	
	/**
	 * Headers for the export
	 */
	private List<ExportHeader> exportHeaders;
	
	
	public ExportDialog(ClientFrame owner, String title, boolean modal, ExportFields exportFields) {
		super(owner, title, modal);
		this.owner = owner;
		this.exportFields = exportFields;
		initComponents();
		showDialog();
	}

	/**
	 * Initializes and lays out all components inside this dialog grouped by sections identifiers.
	 */
	private void initComponents() {
	
		tabPane = new JTabbedPane();
		// PROTEINS section
		proteinPanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		proteinNumberCbx = new JCheckBox();
		proteinNumberCbx.setText("Protein No.");
		proteinNumberCbx.setSelected(exportFields.proteinNumber);
		proteinAccessionCbx = new JCheckBox();
		proteinAccessionCbx.setText("Protein Accession");
		proteinAccessionCbx.setSelected(exportFields.proteinAccession);
		proteinDescriptionCbx = new JCheckBox();
		proteinDescriptionCbx.setText("Protein Description");
		proteinDescriptionCbx.setSelected(exportFields.proteinDescription);
		proteinSpeciesCbx = new JCheckBox();
		proteinSpeciesCbx.setText("Protein Species");
		proteinSpeciesCbx.setSelected(exportFields.proteinSpecies);
		proteinSeqCoverageCbx = new JCheckBox();
		proteinSeqCoverageCbx.setText("Sequence Coverage");
		proteinSeqCoverageCbx.setSelected(exportFields.proteinSeqCoverage);
		proteinSequenceCbx = new JCheckBox();
		proteinSequenceCbx.setText("Protein Sequence");
		proteinSequenceCbx.setSelected(exportFields.proteinSequence);
		proteinMolWeightCbx = new JCheckBox();
		proteinMolWeightCbx.setText("Molecular Weight");
		proteinMolWeightCbx.setSelected(exportFields.proteinMolWeight);
		proteinPiCbx = new JCheckBox();
		proteinPiCbx.setText("Isoelectric Point");
		proteinPiCbx.setSelected(exportFields.proteinPi);
		proteinPepCountCbx = new JCheckBox();
		proteinPepCountCbx.setText("Peptide Count");
		proteinPepCountCbx.setSelected(exportFields.proteinPepCount);
		proteinSpecCountCbx = new JCheckBox();
		proteinSpecCountCbx.setText("Spectral Count");
		proteinSpecCountCbx.setSelected(exportFields.proteinSpecCount);
		proteinEmPAICbx = new JCheckBox();
		proteinEmPAICbx.setText("emPAI");
		proteinEmPAICbx.setSelected(exportFields.proteinEmPAI);
		proteinNSAFCbx = new JCheckBox();
		proteinNSAFCbx.setText("NSAF");
		proteinNSAFCbx.setSelected(exportFields.proteinNSAF);
		proteinPeptidesCbx = new JCheckBox();
		proteinPeptidesCbx.setText("Peptides");
		proteinPeptidesCbx.setSelected(exportFields.proteinPeptides);
		// Ok and cancel function
		JButton proteinExportBtn = new JButton("Export Proteins", IconConstants.CHECK_ICON);
		proteinExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		proteinExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		proteinExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		proteinExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseExporter();
				
				// Store entries from the protein checkboxes
				exportFields.proteinNumber			= proteinNumberCbx.isSelected(); 
				exportFields.proteinAccession		= proteinAccessionCbx.isSelected();
				exportFields.proteinDescription		= proteinDescriptionCbx.isSelected();
				exportFields.proteinSpecies			= proteinSpeciesCbx.isSelected();
				exportFields.proteinSeqCoverage		= proteinSeqCoverageCbx.isSelected();
				exportFields.proteinMolWeight		= proteinMolWeightCbx.isSelected();
				exportFields.proteinPi				= proteinPiCbx.isSelected();
				exportFields.proteinPepCount		= proteinPepCountCbx.isSelected();
				exportFields.proteinSpecCount		= proteinSpecCountCbx.isSelected();
				exportFields.proteinEmPAI			= proteinEmPAICbx.isSelected();
				exportFields.proteinNSAF			= proteinNSAFCbx.isSelected();
				exportFields.proteinSequence		= proteinSequenceCbx.isSelected();
				exportFields.proteinPeptides		= proteinPeptidesCbx.isSelected();
			}
		});
		// Configure 'Cancel' button
		JButton proteinCancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		proteinCancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		proteinCancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		proteinCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		proteinCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		proteinPanel.add(proteinNumberCbx, CC.xy(2,  2));
		proteinPanel.add(proteinAccessionCbx, CC.xy(2,  4));
		proteinPanel.add(proteinDescriptionCbx, CC.xy(2,  6));
		proteinPanel.add(proteinSpeciesCbx, CC.xy(2,  8));
		proteinPanel.add(proteinSeqCoverageCbx, CC.xy(2,  10));
		proteinPanel.add(proteinPepCountCbx, CC.xy(2,  12));
		proteinPanel.add(proteinNSAFCbx, CC.xy(4,  2));
		proteinPanel.add(proteinEmPAICbx, CC.xy(4,  4));
		proteinPanel.add(proteinSpecCountCbx, CC.xy(4,  6));
		proteinPanel.add(proteinPiCbx, CC.xy(4,  8));
		proteinPanel.add(proteinMolWeightCbx, CC.xy(4,  10));
		proteinPanel.add(proteinSequenceCbx, CC.xy(4,  12));
		proteinPanel.add(proteinPeptidesCbx, CC.xy(2, 14));
		proteinPanel.add(proteinExportBtn, CC.xy(2,16));
		proteinPanel.add(proteinCancelBtn, CC.xy( 4, 16));
		
		// PEPTIDES section 
		peptidePanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		peptideNumberCbx = new JCheckBox();
		peptideNumberCbx.setText("Peptide No.");
		peptideNumberCbx.setSelected(exportFields.peptideNumber);
		peptideProteinAccessionsCbx = new JCheckBox();
		peptideProteinAccessionsCbx.setText("Protein Accession(s)");
		peptideProteinAccessionsCbx.setSelected(exportFields.peptideProteinAccessions);
		peptideSequenceCbx = new JCheckBox();
		peptideSequenceCbx.setText("Peptide Sequence");
		peptideSequenceCbx.setSelected(exportFields.peptideSequence);
		uniquePeptidesOnlyCbx = new JCheckBox();
		uniquePeptidesOnlyCbx.setText("Unique Peptides Only");
		uniquePeptidesOnlyCbx.setSelected(exportFields.uniquePeptidesOnly);
		uniquePeptidesOnlyCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(uniquePeptidesOnlyCbx.isSelected() && sharedPeptidesOnlyCbx.isSelected()) sharedPeptidesOnlyCbx.setSelected(false);
			}
		});
		sharedPeptidesOnlyCbx = new JCheckBox();
		sharedPeptidesOnlyCbx.setText("Shared Peptides Only");
		
		sharedPeptidesOnlyCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(uniquePeptidesOnlyCbx.isSelected() && sharedPeptidesOnlyCbx.isSelected()) uniquePeptidesOnlyCbx.setSelected(false);
			}
		});
		if (exportFields.sharedPeptidesOnly && !exportFields.uniquePeptidesOnly ) {
			sharedPeptidesOnlyCbx.setSelected(exportFields.sharedPeptidesOnly);
		}
		// Additional peptide features
		peptideProtCountCbx = new JCheckBox();
		peptideProtCountCbx.setText("Protein Count");
		peptideProtCountCbx.setSelected(exportFields.peptideProtCount);
		peptideSpecCountCbx = new JCheckBox();
		peptideSpecCountCbx.setText("Spectral Count");
		peptideSpecCountCbx.setSelected(exportFields.peptideSpecCount);
		peptideTaxGroup = new JCheckBox();
		peptideTaxGroup.setText("Taxonomic Group");
		peptideTaxGroup.setSelected(exportFields.peptideTaxGroup);
		peptideTaxRank = new JCheckBox();
		peptideTaxRank.setText("Taxonomic Rank");
		peptideTaxRank.setSelected(exportFields.peptideTaxRank);
		peptideTaxIdCbx = new JCheckBox();
		peptideTaxIdCbx.setText("NCBI Taxonomy ID");
		peptideTaxIdCbx.setSelected(exportFields.peptideTaxId);
		// Ok and cancel function
		JButton peptideExportBtn = new JButton("Export Peptides", IconConstants.CHECK_ICON);
		peptideExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		peptideExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		peptideExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		peptideExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseExporter();
				// Store entries from the peptide checkboxes
				exportFields.peptideNumber				= peptideNumberCbx.isSelected();
				exportFields.peptideProteinAccessions	= peptideProteinAccessionsCbx.isSelected();
				exportFields.peptideSequence			= peptideSequenceCbx.isSelected();
				exportFields.peptideTaxGroup			= peptideTaxGroup.isSelected();
				exportFields.uniquePeptidesOnly			= uniquePeptidesOnlyCbx.isSelected();
				exportFields.sharedPeptidesOnly			= sharedPeptidesOnlyCbx.isSelected();
				exportFields.peptideProtCount			= peptideProtCountCbx.isSelected();
				exportFields.peptideSpecCount			= peptideSpecCountCbx.isSelected();
				exportFields.peptideTaxRank				= peptideTaxRank.isSelected();
				exportFields.peptideTaxId				= peptideTaxIdCbx.isSelected();
			}
		});
		// Configure 'Cancel' button
		JButton peptideCancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		peptideCancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		peptideCancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		peptideCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		peptideCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		peptidePanel.add(peptideNumberCbx, CC.xy(2,  2));
		peptidePanel.add(peptideProteinAccessionsCbx, CC.xy(2,  4));
		peptidePanel.add(peptideSequenceCbx, CC.xy(2,  6));
		peptidePanel.add(uniquePeptidesOnlyCbx, CC.xy(2,  8));
		peptidePanel.add(sharedPeptidesOnlyCbx, CC.xy(2,  10));
		peptidePanel.add(peptideProtCountCbx, CC.xy(4,  2));
		peptidePanel.add(peptideSpecCountCbx, CC.xy(4,  4));
		peptidePanel.add(peptideTaxGroup, CC.xy(4,  6));
		peptidePanel.add(peptideTaxRank, CC.xy(4,  8));
		peptidePanel.add(peptideTaxIdCbx, CC.xy(4,  10));
		peptidePanel.add(peptideExportBtn, CC.xy(2, 12));
		peptidePanel.add(peptideCancelBtn,CC.xy(4, 12));
		
		// PSMs section
		psmsPanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		psmNumberCbx = new JCheckBox();
		psmNumberCbx.setText("PSM No.");
		psmNumberCbx.setSelected(exportFields.psmNumber);
		psmProteinAccessionCbx = new JCheckBox();
		psmProteinAccessionCbx.setText("Protein Accession(s)");
		psmProteinAccessionCbx.setSelected(exportFields.psmProteinAccession);
		psmPeptideSequenceCbx = new JCheckBox();
		psmPeptideSequenceCbx.setText("Peptide Sequence");
		psmPeptideSequenceCbx.setSelected(exportFields.psmPeptideSequence);
		psmSpectrumTitleCbx = new JCheckBox();
		psmSpectrumTitleCbx.setText("Spectrum Title");
		psmSpectrumTitleCbx.setSelected(exportFields.psmSpectrumTitle);
		psmChargeCbx = new JCheckBox();
		psmChargeCbx.setText("Charge");
		psmChargeCbx.setSelected(exportFields.psmCharge);
		psmSearchEngineCbx = new JCheckBox();
		psmSearchEngineCbx.setText("Search Engine");
		psmSearchEngineCbx.setSelected(exportFields.psmSearchEngine);
		psmQValueCbx = new JCheckBox();
		psmQValueCbx.setText("q-Value");
		psmQValueCbx.setSelected(exportFields.psmQValue);
		psmScoreCbx = new JCheckBox();
		psmScoreCbx.setText("Score");
		psmScoreCbx.setSelected(exportFields.psmScore);
		// Ok and cancel function
		JButton psmExportBtn = new JButton("Export PSMs", IconConstants.CHECK_ICON);
		psmExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		psmExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		psmExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		psmExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseExporter();
				// Store entries from the PSM checkboxes
				exportFields.psmNumber					= psmNumberCbx.isSelected();
				exportFields.psmProteinAccession		= psmProteinAccessionCbx.isSelected();
				exportFields.psmPeptideSequence			= psmPeptideSequenceCbx.isSelected();
				exportFields.psmSpectrumTitle			= psmSpectrumTitleCbx.isSelected();
				exportFields.psmCharge					= psmChargeCbx.isSelected();
				exportFields.psmSearchEngine			= psmSearchEngineCbx.isSelected();
				exportFields.psmQValue					= psmQValueCbx.isSelected();
				exportFields.psmScore					= psmScoreCbx.isSelected();
			}
		});
		// Configure 'Cancel' button
		JButton psmCancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		psmCancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		psmCancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		psmCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		psmCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		psmsPanel.add(psmNumberCbx, CC.xy(2,  2));
		psmsPanel.add(psmProteinAccessionCbx, CC.xy(2,  4));
		psmsPanel.add(psmPeptideSequenceCbx, CC.xy(2,  6));		
		psmsPanel.add(psmSpectrumTitleCbx, CC.xy(2,  8));
		psmsPanel.add(psmChargeCbx, CC.xy(4,  2));
		psmsPanel.add(psmSearchEngineCbx, CC.xy(4,  4));
		psmsPanel.add(psmQValueCbx, CC.xy(4,  6));		
		psmsPanel.add(psmScoreCbx, CC.xy(4,  8));
		psmsPanel.add(psmExportBtn, CC.xy(2, 10));
		psmsPanel.add(psmCancelBtn, CC.xy(4, 10));
		
		// META-PROTEINS section
		metaproteinPanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		metaproteinNumberCbx = new JCheckBox();
		metaproteinNumberCbx.setText("Protein No.");
		metaproteinNumberCbx.setSelected(exportFields.metaproteinNumber);
		metaproteinAccessionsCbx = new JCheckBox();
		metaproteinAccessionsCbx.setText("Protein Accession(s)");
		metaproteinAccessionsCbx.setSelected(exportFields.metaproteinAccessions);
		metaproteinDescriptionCbx = new JCheckBox();
		metaproteinDescriptionCbx.setText("Meta-Protein Description");
		metaproteinDescriptionCbx.setSelected(exportFields.metaproteinDescription);
		metaproteinTaxonomyCbx = new JCheckBox();
		metaproteinTaxonomyCbx.setText("Meta-Protein Taxonomy");
		metaproteinTaxonomyCbx.setSelected(exportFields.metaproteinTaxonomy);
		metaproteinAIdCbx = new JCheckBox();
		metaproteinAIdCbx.setText("Alignment Identity");
		metaproteinAIdCbx.setSelected(exportFields.metaproteinAId);
		metaproteinSeqCoverageCbx = new JCheckBox();
		metaproteinSeqCoverageCbx.setText("Sequence Coverage");
		metaproteinSeqCoverageCbx.setSelected(exportFields.metaproteinSeqCoverage);		
		metaproteinPepCountCbx = new JCheckBox();
		metaproteinPepCountCbx.setText("Peptide Count");
		metaproteinPepCountCbx.setSelected(exportFields.metaproteinPepCount);
		metaproteinSpecCountCbx = new JCheckBox();
		metaproteinSpecCountCbx.setText("Spectral Count");
		metaproteinSpecCountCbx.setSelected(exportFields.metaproteinSpecCount);
		metaproteinEmPAICbx = new JCheckBox();
		metaproteinEmPAICbx.setText("emPAI");
		metaproteinEmPAICbx.setSelected(exportFields.metaproteinEmPAI);
		metaproteinNSAFCbx = new JCheckBox();
		metaproteinNSAFCbx.setText("NSAF");
		metaproteinNSAFCbx.setSelected(exportFields.metaproteinNSAF);
		metaproteinsPeptidesCbx = new JCheckBox();
		metaproteinsPeptidesCbx.setText("Peptides");
		metaproteinsPeptidesCbx.setSelected(exportFields.metaproteinPeptides);
		// Ok and cancel function
		JButton metaProtExportBtn = new JButton("Export Metaproteins", IconConstants.CHECK_ICON);
		metaProtExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		metaProtExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		metaProtExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		metaProtExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseExporter();
				// Store fields for the metaprotein checkboxes
				exportFields.metaproteinNumber		= metaproteinNumberCbx.isSelected();
				exportFields.metaproteinAccessions	= metaproteinAccessionsCbx.isSelected();
				exportFields.metaproteinDescription	= metaproteinDescriptionCbx.isSelected();
				exportFields.metaproteinTaxonomy	= metaproteinTaxonomyCbx.isSelected();
				exportFields.metaproteinSeqCoverage	= metaproteinSeqCoverageCbx.isSelected();
				exportFields.metaproteinAId			= metaproteinAIdCbx.isSelected();
				exportFields.metaproteinNSAF		= metaproteinNSAFCbx.isSelected();
				exportFields.metaproteinEmPAI		= metaproteinEmPAICbx.isSelected();
				exportFields.metaproteinSpecCount	= metaproteinSpecCountCbx.isSelected();
				exportFields.metaproteinPepCount	= metaproteinPepCountCbx.isSelected();
				exportFields.metaproteinPeptides	= metaproteinsPeptidesCbx.isSelected();
			}
		});
		// Configure 'Cancel' button
		JButton MetaProtCancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		MetaProtCancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		MetaProtCancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		MetaProtCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		MetaProtCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		metaproteinPanel.add(metaproteinNumberCbx, CC.xy(2,  2));
		metaproteinPanel.add(metaproteinAccessionsCbx, CC.xy(2,  4));
		metaproteinPanel.add(metaproteinDescriptionCbx, CC.xy(2,  6));		
		metaproteinPanel.add(metaproteinTaxonomyCbx, CC.xy(2,  8));
		metaproteinPanel.add(metaproteinSeqCoverageCbx, CC.xy(2,  10));
		metaproteinPanel.add(metaproteinsPeptidesCbx, CC.xy(2, 12));
		metaproteinPanel.add(metaproteinAIdCbx, CC.xy(4,  2));
		metaproteinPanel.add(metaproteinNSAFCbx, CC.xy(4,  4));
		metaproteinPanel.add(metaproteinEmPAICbx, CC.xy(4,  6));
		metaproteinPanel.add(metaproteinSpecCountCbx, CC.xy(4,  8));
		metaproteinPanel.add(metaproteinPepCountCbx, CC.xy(4,  10));
		metaproteinPanel.add(metaProtExportBtn, CC.xy(2, 14));
		metaproteinPanel.add(MetaProtCancelBtn, CC.xy(4, 14));
		
		// TAXONOMY section
		taxonomyPanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));

		taxonomyUnclassifiedCbx = new JCheckBox();
		taxonomyUnclassifiedCbx.setText("Unclassified");
		taxonomyUnclassifiedCbx.setSelected(exportFields.taxonomyUnclassified);		
		taxonomySuperKingdomCbx = new JCheckBox();
		taxonomySuperKingdomCbx.setText("Superkingdom");
		taxonomySuperKingdomCbx.setSelected(exportFields.taxonomySuperKingdom);		
		taxonomyKingdomCbx = new JCheckBox();
		taxonomyKingdomCbx.setText("Kingdom");
		taxonomyKingdomCbx.setSelected(exportFields.taxonomyKingdom);
		taxonomyPhylumCbx = new JCheckBox();
		taxonomyPhylumCbx.setText("Phylum");
		taxonomyPhylumCbx.setSelected(exportFields.taxonomyPhylum);
		taxonomyOrderCbx = new JCheckBox();
		taxonomyOrderCbx.setText("Order");
		taxonomyOrderCbx.setSelected(exportFields.taxonomyOrder);
		taxonomyClassCbx= new JCheckBox();
		taxonomyClassCbx.setText("Class");
		taxonomyClassCbx.setSelected(exportFields.taxonomyClass);
		taxonomyFamilyCbx = new JCheckBox();
		taxonomyFamilyCbx.setText("Family");
		taxonomyFamilyCbx.setSelected(exportFields.taxonomyFamily);
		taxonomyGenusCbx = new JCheckBox();
		taxonomyGenusCbx.setText("Genus");
		taxonomyGenusCbx.setSelected(exportFields.taxonomyGenus);
		taxonomySpeciesCbx = new JCheckBox();
		taxonomySpeciesCbx.setText("Species / LCA");
		taxonomySpeciesCbx.setSelected(exportFields.taxonomySpecies);
		taxonomySpecificPeptidesCbx = new JCheckBox();
		taxonomySpecificPeptidesCbx.setText("No. Specific Peptides");
		taxonomySpecificPeptidesCbx.setSelected(exportFields.taxonomySpecificPeptides);
//		taxonomyUnspecificPeptidesCbx = new JCheckBox();
//		taxonomyUnspecificPeptidesCbx.setText("No. Unspecific Peptides");
//		taxonomyUnspecificPeptidesCbx.setSelected(true);
		taxonomySpecificSpecCountCbx = new JCheckBox();
		taxonomySpecificSpecCountCbx.setText("Specific Spectral Counts");
		taxonomySpecificSpecCountCbx.setSelected(exportFields.taxonomySpecificSpecCount);
//		taxonomyUnspecificSpecCountCbx = new JCheckBox();
//		taxonomyUnspecificSpecCountCbx.setText("Unspecific Spectral Counts");
//		taxonomyUnspecificSpecCountCbx.setSelected(true);
		// Ok and cancel function
		JButton taxExportBtn = new JButton("Export Taxonomy", IconConstants.CHECK_ICON);
		taxExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		taxExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		taxExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		taxExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseExporter();
				// Store fields for taxonomy checkboxes
				exportFields.taxonomyUnclassified		= taxonomyUnclassifiedCbx.isSelected();
				exportFields.taxonomySuperKingdom		= taxonomySuperKingdomCbx.isSelected();
				exportFields.taxonomyKingdom			= taxonomyKingdomCbx.isSelected();
				exportFields.taxonomyPhylum				= taxonomyPhylumCbx.isSelected();
				exportFields.taxonomyOrder				= taxonomyOrderCbx.isSelected();
				exportFields.taxonomyClass				= taxonomyClassCbx.isSelected();
				exportFields.taxonomyFamily				= taxonomyFamilyCbx.isSelected();
				exportFields.taxonomyGenus				= taxonomyGenusCbx.isSelected();
				exportFields.taxonomySpecies			= taxonomySpeciesCbx.isSelected();
				exportFields.taxonomySpecificPeptides	= taxonomySpecificPeptidesCbx.isSelected();
				exportFields.taxonomySpecificSpecCount	= taxonomySpecificSpecCountCbx.isSelected();
			}
		});
		// Configure 'Cancel' button
		JButton taxCancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		taxCancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		taxCancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		taxCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		taxCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		taxonomyPanel.add(taxonomyUnclassifiedCbx, CC.xy(2,  2));		
		taxonomyPanel.add(taxonomySuperKingdomCbx, CC.xy(2,  4));
		taxonomyPanel.add(taxonomyKingdomCbx, CC.xy(2,  6));
		taxonomyPanel.add(taxonomyPhylumCbx, CC.xy(2,  8));
		taxonomyPanel.add(taxonomyOrderCbx, CC.xy(2,  10));
		taxonomyPanel.add(taxonomyClassCbx, CC.xy(2,  12));
		taxonomyPanel.add(taxonomyFamilyCbx, CC.xy(4,  2));
		taxonomyPanel.add(taxonomyGenusCbx, CC.xy(4,  4));
		taxonomyPanel.add(taxonomySpeciesCbx, CC.xy(4,  6));
		taxonomyPanel.add(taxonomySpecificPeptidesCbx, CC.xy(4,  8));
		taxonomyPanel.add(taxonomySpecificSpecCountCbx, CC.xy(4,  10));
		taxonomyPanel.add(taxExportBtn, CC.xy(2, 14));
		taxonomyPanel.add(taxCancelBtn, CC.xy(4, 14));
		
		tabPane.addTab("Meta-Proteins", metaproteinPanel);
		tabPane.addTab("Taxonomy", taxonomyPanel);
		tabPane.addTab("Proteins", proteinPanel);
		tabPane.addTab("Peptides", peptidePanel);
		tabPane.addTab("PSMs", psmsPanel);
		
		
		Container cp = this.getContentPane();		
		cp.setLayout(new FormLayout("5dlu, r:p, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		cp.add(tabPane, CC.xy(2,  2));
		
	}
	
	/**
	 * This method shows the dialog.
	 */
	private void showDialog() {
		// Configure size and position
		this.pack();
		this.setResizable(false);
		ScreenConfig.centerInScreen(this);
		
		// Show dialog
		this.setVisible(true);
	}
	
	/**
	 * Close method for the dialog.
	 */
	private void close() {
		// Save the export headers
		dispose();
	}
	
	/**
	 * Helper method to select the chosen exporter.
	 */
	private void chooseExporter() {
		JFileChooser chooser = new ConfirmFileChooser(owner.getLastSelectedFolder());
		chooser.setFileFilter(Constants.CSV_FILE_FILTER);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle("Export Spectra File Details");
		File selectedFile;
		String resultType = null;
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			selectedFile = chooser.getSelectedFile();

			if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
				selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
			}

			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

			try {
				if (selectedFile.exists()) {
					selectedFile.delete();
				}
				selectedFile.createNewFile();
				collectHeaderSet();
				
				if (tabPane.getSelectedIndex() == 0) {
					resultType = "Meta-Proteins";
					ResultExporter.exportMetaProteins(selectedFile.getPath(), client.getDbSearchResult(), exportHeaders);
				} else if (tabPane.getSelectedIndex() == 1) {
					resultType = "Taxonomy";
					ResultExporter.exportTaxonomy(selectedFile.getPath(), client.getDbSearchResult(), exportHeaders);
				} else if (tabPane.getSelectedIndex() == 2) {
					resultType = "Proteins";
					ResultExporter.exportProteins(selectedFile.getPath(), client.getDbSearchResult(), exportHeaders);
				} else if (tabPane.getSelectedIndex() == 3) {
					resultType = "Peptides";
					ResultExporter.exportPeptides(selectedFile.getPath(), client.getDbSearchResult(), exportHeaders);
				} else if (tabPane.getSelectedIndex() == 4) {
					resultType = "PSMs";
					ResultExporter.exportPSMs(selectedFile.getPath(), client.getDbSearchResult(), exportHeaders);
				}

				owner.setLastSelectedFolder(selectedFile.getPath());

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"An error occured when exporting the results.",
						"Error Results Exporting", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} 
			// Close the dialog after export has been finished.
			close();
			JOptionPane.showMessageDialog(this,	"Successfully exported " + resultType + " to the file "	+ selectedFile.getName() + ".",	"Export successful!", JOptionPane.INFORMATION_MESSAGE);
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void collectHeaderSet() {
		
		// Initialize set on demand.
		if(exportHeaders == null) {
			exportHeaders = new ArrayList<ExportHeader>();
		}
		
		// Meta-Proteins
		if(metaproteinNumberCbx.isSelected()) exportHeaders.add(new ExportHeader(1, metaproteinNumberCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinAccessionsCbx.isSelected()) exportHeaders.add(new ExportHeader(2, metaproteinAccessionsCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinDescriptionCbx.isSelected()) exportHeaders.add(new ExportHeader(3, metaproteinDescriptionCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinTaxonomyCbx.isSelected()) exportHeaders.add(new ExportHeader(4, metaproteinTaxonomyCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinSeqCoverageCbx.isSelected()) exportHeaders.add(new ExportHeader(5, metaproteinSeqCoverageCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinAIdCbx.isSelected()) exportHeaders.add(new ExportHeader(6, metaproteinAIdCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinNSAFCbx.isSelected()) exportHeaders.add(new ExportHeader(7, metaproteinNSAFCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinEmPAICbx.isSelected()) exportHeaders.add(new ExportHeader(8, metaproteinEmPAICbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinSpecCountCbx.isSelected()) exportHeaders.add(new ExportHeader(9, metaproteinSpecCountCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinPepCountCbx.isSelected()) exportHeaders.add(new ExportHeader(10, metaproteinPepCountCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinsPeptidesCbx.isSelected()) exportHeaders.add(new ExportHeader(11, metaproteinsPeptidesCbx.getText() , ExportHeaderType.METAPROTEINS));
		// Taxonomy
		if(taxonomyUnclassifiedCbx.isSelected()) exportHeaders.add(new ExportHeader(1, taxonomyUnclassifiedCbx.getText(), ExportHeaderType.TAXONOMY));
		if(taxonomySuperKingdomCbx.isSelected()) exportHeaders.add(new ExportHeader(2, taxonomySuperKingdomCbx.getText(), ExportHeaderType.TAXONOMY));
		if(taxonomyKingdomCbx.isSelected()) exportHeaders.add(new ExportHeader(3, taxonomyKingdomCbx.getText(), ExportHeaderType.TAXONOMY));
		if(taxonomyPhylumCbx.isSelected()) exportHeaders.add(new ExportHeader(4, taxonomyPhylumCbx.getText(), ExportHeaderType.TAXONOMY));
		if(taxonomyOrderCbx.isSelected()) exportHeaders.add(new ExportHeader(5, taxonomyOrderCbx.getText(), ExportHeaderType.TAXONOMY));
		if(taxonomyClassCbx.isSelected()) exportHeaders.add(new ExportHeader(6, taxonomyClassCbx.getText(), ExportHeaderType.TAXONOMY));
		if(taxonomyFamilyCbx.isSelected()) exportHeaders.add(new ExportHeader(7, taxonomyFamilyCbx.getText(), ExportHeaderType.TAXONOMY));
		if(taxonomyGenusCbx.isSelected()) exportHeaders.add(new ExportHeader(8, taxonomyGenusCbx.getText(), ExportHeaderType.TAXONOMY));
		if(taxonomySpeciesCbx.isSelected()) exportHeaders.add(new ExportHeader(9, taxonomySpeciesCbx.getText(), ExportHeaderType.TAXONOMY));
		if(taxonomySpecificPeptidesCbx.isSelected()) exportHeaders.add(new ExportHeader(10, taxonomySpecificPeptidesCbx.getText(), ExportHeaderType.TAXONOMY));
//		if(taxonomyUnspecificPeptidesCbx.isSelected()) exportHeaders.add(new ExportHeader(11, taxonomyUnspecificPeptidesCbx.getText(), ExportHeaderType.TAXONOMY));
		if(taxonomySpecificSpecCountCbx.isSelected()) exportHeaders.add(new ExportHeader(11, taxonomySpecificSpecCountCbx.getText(), ExportHeaderType.TAXONOMY));
//		if(taxonomyUnspecificSpecCountCbx.isSelected()) exportHeaders.add(new ExportHeader(13, taxonomyUnspecificSpecCountCbx.getText(), ExportHeaderType.TAXONOMY));
		
		// Proteins
		if(proteinNumberCbx.isSelected()) exportHeaders.add(new ExportHeader(1, proteinNumberCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinAccessionCbx.isSelected()) exportHeaders.add(new ExportHeader(2, proteinAccessionCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinDescriptionCbx.isSelected()) exportHeaders.add(new ExportHeader(3, proteinDescriptionCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinSpeciesCbx.isSelected()) exportHeaders.add(new ExportHeader(4, proteinSpeciesCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinSeqCoverageCbx.isSelected()) exportHeaders.add(new ExportHeader(5, proteinSeqCoverageCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinPepCountCbx.isSelected()) exportHeaders.add(new ExportHeader(6, proteinPepCountCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinNSAFCbx.isSelected()) exportHeaders.add(new ExportHeader(7, proteinNSAFCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinEmPAICbx.isSelected()) exportHeaders.add(new ExportHeader(8, proteinEmPAICbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinSpecCountCbx.isSelected()) exportHeaders.add(new ExportHeader(9, proteinSpecCountCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinPiCbx.isSelected()) exportHeaders.add(new ExportHeader(10, proteinPiCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinMolWeightCbx.isSelected()) exportHeaders.add(new ExportHeader(11, proteinMolWeightCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinSequenceCbx.isSelected()) exportHeaders.add(new ExportHeader(12, proteinSequenceCbx.getText(), ExportHeaderType.PROTEINS));
		if(proteinPeptidesCbx.isSelected()) exportHeaders.add(new ExportHeader(13, proteinPeptidesCbx.getText(), ExportHeaderType.PROTEINS));
		
		// Peptides
		if(peptideNumberCbx.isSelected()) exportHeaders.add(new ExportHeader(1, peptideNumberCbx.getText(), ExportHeaderType.PEPTIDES));
		if(peptideProteinAccessionsCbx.isSelected()) exportHeaders.add(new ExportHeader(2, peptideProteinAccessionsCbx.getText(), ExportHeaderType.PEPTIDES));
		if(peptideSequenceCbx.isSelected()) exportHeaders.add(new ExportHeader(3, peptideSequenceCbx.getText(), ExportHeaderType.PEPTIDES));
		if(uniquePeptidesOnlyCbx.isSelected()) exportHeaders.add(new ExportHeader(4, uniquePeptidesOnlyCbx.getText(), ExportHeaderType.PEPTIDES));
		if(sharedPeptidesOnlyCbx.isSelected()) exportHeaders.add(new ExportHeader(5, sharedPeptidesOnlyCbx.getText(), ExportHeaderType.PEPTIDES));
		if(peptideProtCountCbx.isSelected()) exportHeaders.add(new ExportHeader(6, peptideProtCountCbx.getText(), ExportHeaderType.PEPTIDES));
		if(peptideSpecCountCbx.isSelected()) exportHeaders.add(new ExportHeader(7, peptideSpecCountCbx.getText(), ExportHeaderType.PEPTIDES));
		if(peptideTaxGroup.isSelected()) exportHeaders.add(new ExportHeader(8, peptideTaxGroup.getText(), ExportHeaderType.PEPTIDES));
		if(peptideTaxRank.isSelected()) exportHeaders.add(new ExportHeader(9, peptideTaxRank.getText(), ExportHeaderType.PEPTIDES));
		if(peptideTaxIdCbx.isSelected()) exportHeaders.add(new ExportHeader(10, peptideTaxIdCbx.getText(), ExportHeaderType.PEPTIDES));
		
		// PSMs
		if(psmNumberCbx.isSelected()) exportHeaders.add(new ExportHeader(1, psmNumberCbx.getText(), ExportHeaderType.PSMS));
		if(psmProteinAccessionCbx.isSelected()) exportHeaders.add(new ExportHeader(2, psmProteinAccessionCbx.getText(), ExportHeaderType.PSMS));
		if(psmPeptideSequenceCbx.isSelected()) exportHeaders.add(new ExportHeader(3, psmPeptideSequenceCbx.getText(), ExportHeaderType.PSMS));
		if(psmSpectrumTitleCbx.isSelected()) exportHeaders.add(new ExportHeader(4, psmSpectrumTitleCbx.getText(), ExportHeaderType.PSMS));
		if(psmChargeCbx.isSelected()) exportHeaders.add(new ExportHeader(5, psmChargeCbx.getText(), ExportHeaderType.PSMS));
		if(psmSearchEngineCbx.isSelected()) exportHeaders.add(new ExportHeader(6, psmSearchEngineCbx.getText(), ExportHeaderType.PSMS));
		if(psmQValueCbx.isSelected()) exportHeaders.add(new ExportHeader(7, psmQValueCbx.getText(), ExportHeaderType.PSMS));
		if(psmScoreCbx.isSelected()) exportHeaders.add(new ExportHeader(8, psmScoreCbx.getText(), ExportHeaderType.PSMS));
	}
}
