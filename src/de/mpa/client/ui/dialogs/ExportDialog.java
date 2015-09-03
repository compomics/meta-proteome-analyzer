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
	private JCheckBox psmNumberCbx;
	private JCheckBox psmProteinAccessionCbx;
	private JCheckBox psmPeptideSequenceCbx;
	private JCheckBox psmSpectrumTitleCbx;
	private JCheckBox psmScoreCbx;
	private JCheckBox psmSearchEngineCbx;
	private JCheckBox psmQValueCbx;
	private JCheckBox psmPEPCbx;
	private JCheckBox psmChargeCbx;
	
	/**
	 * The identified spectra export panel.
	 */
	private JCheckBox spectrumNumberCbx;
	private JCheckBox spectrumIDCbx;
	private JCheckBox spectrumTitleCbx;
	private JCheckBox spectrumPeptidesCbx;
	private JCheckBox spectrumAccessionsCbx;

	/**
	 * The metaproteins export panel
	 */
	private JCheckBox metaproteinNumberCbx;
	private JCheckBox metaproteinAccessionsCbx;
	private JCheckBox metaproteinDescriptionCbx;
	private JCheckBox metaproteinTaxonomyCbx;
	private JCheckBox metaproteinUniRef100Cbx;
	private JCheckBox metaproteinUniRef90Cbx;
	private JCheckBox metaproteinUniRef50Cbx;
	private JCheckBox metaproteinKOCbx;
	private JCheckBox metaproteinECCbx;
	private JCheckBox metaproteinSeqCoverageCbx;
	private JCheckBox metaproteinPepCountCbx;
	private JCheckBox metaproteinSpecCountCbx;
	private JCheckBox metaproteinsPeptidesCbx;
	private JCheckBox metaproteinsProteinsCbx;

	/**
	 * The taxonomy export panel
	 */
	private JCheckBox taxonomySpecificSpecCountCbx;
	private JCheckBox taxonomyKingdomCbx;
	private JCheckBox taxonomyPhylumCbx;
	private JCheckBox taxonomyOrderCbx;
	private JCheckBox taxonomySuperKingdomCbx;
	private JCheckBox taxonomyClassCbx;
	private JCheckBox taxonomyFamilyCbx;
	private JCheckBox taxonomyGenusCbx;
	private JCheckBox taxonomySpeciesCbx;
	private JCheckBox taxonomySubspeciesCbx;
	private JCheckBox taxonomyUnclassifiedCbx;
	private JCheckBox taxonomySpecificPeptidesCbx;
	private JCheckBox taxonomyKronaSpecCountCbx;
	
	/**
	 * The meta-protein taxonomy export panel
	 */
	private JCheckBox metaProteinTaxonomySpecificPeptidesCbx;
	private JCheckBox metaProteinTaxonomyKronaSpecCountCbx;
	
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
		JPanel proteinPanel = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		// Protein features
		JPanel proteinFeaturePnl = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
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
		proteinSpeciesCbx.setText("Protein Taxonomy");
		proteinSpeciesCbx.setSelected(exportFields.proteinTaxonomy);
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
		// Add protein features to proteinFeaturePnl
		proteinFeaturePnl.add(proteinNumberCbx, CC.xy(2,  2));
		proteinFeaturePnl.add(proteinAccessionCbx, CC.xy(2,  4));
		proteinFeaturePnl.add(proteinDescriptionCbx, CC.xy(2,  6));
		proteinFeaturePnl.add(proteinSpeciesCbx, CC.xy(2,  8));
		proteinFeaturePnl.add(proteinSeqCoverageCbx, CC.xy(2,  10));
		proteinFeaturePnl.add(proteinPepCountCbx, CC.xy(2,  12));
		proteinFeaturePnl.add(proteinNSAFCbx, CC.xy(4,  2));
		proteinFeaturePnl.add(proteinEmPAICbx, CC.xy(4,  4));
		proteinFeaturePnl.add(proteinSpecCountCbx, CC.xy(4,  6));
		proteinFeaturePnl.add(proteinPiCbx, CC.xy(4,  8));
		proteinFeaturePnl.add(proteinMolWeightCbx, CC.xy(4,  10));
		proteinFeaturePnl.add(proteinSequenceCbx, CC.xy(4,  12));
		proteinFeaturePnl.add(proteinPeptidesCbx, CC.xy(2, 14));
		// Protein button function
		JPanel proteinButtonPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu"));
		// Protein export button
		JButton proteinExportBtn = new JButton("Export Proteins", IconConstants.CHECK_ICON);
		proteinExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		proteinExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		proteinExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		proteinExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Start export
				chooseExporter("Export Proteins");
				// Store entries from the protein checkboxes
				storeEntries();
			}
		});
		// Protein 'Close' button
		JButton proteinCancelBtn = new JButton("Close", IconConstants.SAVE_ICON);
		proteinCancelBtn.setRolloverIcon(IconConstants.SAVE_ROLLOVER_ICON);
		proteinCancelBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
		proteinCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		proteinCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Store entries from the protein checkboxes
				storeEntries();
				close();
			}
		});
		// Protein 'Close' button
		JButton proteinRefuseBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		proteinRefuseBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		proteinRefuseBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		proteinRefuseBtn.setHorizontalAlignment(SwingConstants.LEFT);
		proteinRefuseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close
				close();
			}
		});
		// Add protein buttons to the protein button bar
		proteinButtonPnl.add(proteinExportBtn, CC.xy(2, 2));
		proteinButtonPnl.add(proteinCancelBtn, CC.xy(4, 2));
		proteinButtonPnl.add(proteinRefuseBtn, CC.xy(6, 2));
		// Add all to the proteinpanel
		proteinPanel.add(proteinFeaturePnl, CC.xy(2, 2));
		proteinPanel.add(proteinButtonPnl, CC.xy(2, 4));

		// PEPTIDES section 
		JPanel peptidePnl = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		JPanel peptideFeaturesPnl = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
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
		// Add features to the panel
		peptideFeaturesPnl.add(peptideNumberCbx, CC.xy(2,  2));
		peptideFeaturesPnl.add(peptideProteinAccessionsCbx, CC.xy(2,  4));
		peptideFeaturesPnl.add(peptideSequenceCbx, CC.xy(2,  6));
		peptideFeaturesPnl.add(uniquePeptidesOnlyCbx, CC.xy(2,  8));
		peptideFeaturesPnl.add(sharedPeptidesOnlyCbx, CC.xy(2,  10));
		peptideFeaturesPnl.add(peptideProtCountCbx, CC.xy(4,  2));
		peptideFeaturesPnl.add(peptideSpecCountCbx, CC.xy(4,  4));
		peptideFeaturesPnl.add(peptideTaxGroup, CC.xy(4,  6));
		peptideFeaturesPnl.add(peptideTaxRank, CC.xy(4,  8));
		peptideFeaturesPnl.add(peptideTaxIdCbx, CC.xy(4,  10));

		// Create button panel
		JPanel peptideButtonPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p ,5dlu", "5dlu, p , 5dlu"));
		// Peptide 'OK' button
		JButton peptideExportBtn = new JButton("Export Peptides", IconConstants.CHECK_ICON);
		peptideExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		peptideExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		peptideExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		peptideExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Start export
				chooseExporter("Export Peptides");
				// Store entries from the peptide checkboxes
				storeEntries();
			}
		});
		// Peptide 'Close' button
		JButton peptideCancelBtn = new JButton("Close", IconConstants.SAVE_ICON);
		peptideCancelBtn.setRolloverIcon(IconConstants.SAVE_ROLLOVER_ICON);
		peptideCancelBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
		peptideCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		peptideCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Store entries from the peptide checkboxes
				storeEntries();
				// Close
				close();
			}
		});
		// Peptide 'Cancel' button
		JButton peptideRefuseBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		peptideRefuseBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		peptideRefuseBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		peptideRefuseBtn.setHorizontalAlignment(SwingConstants.LEFT);
		peptideRefuseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close
				close();
			}
		});
		// Add to peptide button bar
		peptideButtonPnl.add(peptideExportBtn, CC.xy(2, 2));
		peptideButtonPnl.add(peptideCancelBtn, CC.xy(4, 2));
		peptideButtonPnl.add(peptideRefuseBtn, CC.xy(6, 2));
		// Add to peptide panel
		peptidePnl.add(peptideFeaturesPnl, CC.xy(2, 2));
		peptidePnl.add(peptideButtonPnl, CC.xy(2, 4));

		// PSMs section
		JPanel psmPanel = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		JPanel psmFeaturesPanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
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
		psmPEPCbx = new JCheckBox();
		psmPEPCbx.setText("PEP");
		psmPEPCbx.setSelected(exportFields.psmPEP);
		psmScoreCbx = new JCheckBox();
		psmScoreCbx.setText("Score");
		psmScoreCbx.setSelected(exportFields.psmScore);
		
		// Add to PSM features panel.
		psmFeaturesPanel.add(psmNumberCbx, CC.xy(2,  2));
		psmFeaturesPanel.add(psmProteinAccessionCbx, CC.xy(2,  4));
		psmFeaturesPanel.add(psmPeptideSequenceCbx, CC.xy(2,  6));		
		psmFeaturesPanel.add(psmSpectrumTitleCbx, CC.xy(2,  8));
		psmFeaturesPanel.add(psmChargeCbx, CC.xy(2,  10));
		psmFeaturesPanel.add(psmSearchEngineCbx, CC.xy(4,  4));
		psmFeaturesPanel.add(psmQValueCbx, CC.xy(4,  6));		
		psmFeaturesPanel.add(psmPEPCbx, CC.xy(4,  8));
		psmFeaturesPanel.add(psmScoreCbx, CC.xy(4,  2));
		
		// PSM button bar
		JPanel psmButtonPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu"));
		// PSM export button
		JButton psmExportBtn = new JButton("Export PSMs", IconConstants.CHECK_ICON);
		psmExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		psmExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		psmExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		psmExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Start export
				chooseExporter("Export PSMs");
				// Store entries from the PSM checkboxes
				storeEntries();
			}
		});
		// Configure PSM 'Close' button
		JButton psmCancelBtn = new JButton("Close", IconConstants.SAVE_ICON);
		psmCancelBtn.setRolloverIcon(IconConstants.SAVE_ROLLOVER_ICON);
		psmCancelBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
		psmCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		psmCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Store entries from the PSM checkboxes
				storeEntries();
				// Close
				close();
			}
		});
		// Configure PSM 'Cancel' button
		JButton psmRefuseBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		psmRefuseBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		psmRefuseBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		psmRefuseBtn.setHorizontalAlignment(SwingConstants.LEFT);
		psmRefuseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close
				close();
			}
		});
		// Add to PSM button bar
		psmButtonPnl.add(psmExportBtn, CC.xy(2, 2));
		psmButtonPnl.add(psmCancelBtn, CC.xy(4, 2));
		psmButtonPnl.add(psmRefuseBtn, CC.xy(6, 2));
		// Add to PSM panel
		psmPanel.add(psmFeaturesPanel, CC.xy(2, 2));
		psmPanel.add(psmButtonPnl, CC.xy(2, 4));
		
		// Identified SPECTRA section
		JPanel spectrumPanel = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		JPanel spectrumFeaturesPanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		spectrumNumberCbx = new JCheckBox();
		spectrumNumberCbx.setText("Spectrum Number");
		spectrumNumberCbx.setSelected(exportFields.spectrumID);
		spectrumIDCbx = new JCheckBox();
		spectrumIDCbx.setText("Spectrum ID");
		spectrumIDCbx.setSelected(exportFields.spectrumID);
		spectrumTitleCbx = new JCheckBox();
		spectrumTitleCbx.setText("Spectrum Title");
		spectrumTitleCbx.setSelected(exportFields.spectrumTitle);
		spectrumPeptidesCbx = new JCheckBox();
		spectrumPeptidesCbx.setText("Peptide(s)");
		spectrumPeptidesCbx.setSelected(exportFields.spectrumPeptides);
		spectrumAccessionsCbx = new JCheckBox();
		spectrumAccessionsCbx.setText("Protein Accession(s)");
		spectrumAccessionsCbx.setSelected(exportFields.spectrumAccessions);
		
		// Add to spectrum features panel.
		spectrumFeaturesPanel.add(spectrumNumberCbx, CC.xy(2,  2));
		spectrumFeaturesPanel.add(spectrumIDCbx, CC.xy(4,  2));
		spectrumFeaturesPanel.add(spectrumTitleCbx, CC.xy(2,  4));
		spectrumFeaturesPanel.add(spectrumPeptidesCbx, CC.xy(4,  4));		
		spectrumFeaturesPanel.add(spectrumAccessionsCbx, CC.xy(2,  6));
		
		// Spectrum button bar
		JPanel spectrumButtonPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu"));
		
		// Spectrum export button
		JButton spectrumExportBtn = new JButton("Export Spectra", IconConstants.CHECK_ICON);
		spectrumExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		spectrumExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		spectrumExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		spectrumExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Start export
				chooseExporter("Export Identified Spectra");
				// Store entries from the PSM checkboxes.
				storeEntries();
			}
		});
		// Configure 'Close' button
		JButton spectrumCancelBtn = new JButton("Close", IconConstants.SAVE_ICON);
		spectrumCancelBtn.setRolloverIcon(IconConstants.SAVE_ROLLOVER_ICON);
		spectrumCancelBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
		spectrumCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		spectrumCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Store entries from the PSM checkboxes
				storeEntries();
				// Close
				close();
			}
		});
		// Spectrum 'Cancel' button
		JButton spectrumRefuseBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		spectrumRefuseBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		spectrumRefuseBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		spectrumRefuseBtn.setHorizontalAlignment(SwingConstants.LEFT);
		spectrumRefuseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close
				close();
			}
		});
		// Add to spectrum button bar.
		spectrumButtonPnl.add(spectrumExportBtn, CC.xy(2, 2));
		spectrumButtonPnl.add(spectrumCancelBtn, CC.xy(4, 2));
		spectrumButtonPnl.add(spectrumRefuseBtn, CC.xy(6, 2));
		
		// Add to spectrum panel.
		spectrumPanel.add(spectrumFeaturesPanel, CC.xy(2, 2));
		spectrumPanel.add(spectrumButtonPnl, CC.xy(2, 4));

		// META-PROTEINS section
		JPanel metaProteinPnl = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		JPanel metaproteinFeaturePanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu,p, 5dlu, p, 5dlu"));
		metaproteinNumberCbx = new JCheckBox();
		metaproteinNumberCbx.setText("Meta-Protein No.");
		metaproteinNumberCbx.setSelected(exportFields.metaproteinNumber);
		metaproteinAccessionsCbx = new JCheckBox();
		metaproteinAccessionsCbx.setText("Meta-Protein Accession");
		metaproteinAccessionsCbx.setSelected(exportFields.metaproteinAccessions);
		metaproteinDescriptionCbx = new JCheckBox();
		metaproteinDescriptionCbx.setText("Meta-Protein Description");
		metaproteinDescriptionCbx.setSelected(exportFields.metaproteinDescription);
		metaproteinTaxonomyCbx = new JCheckBox();
		metaproteinTaxonomyCbx.setText("Meta-Protein Taxonomy");
		metaproteinTaxonomyCbx.setSelected(exportFields.metaproteinTaxonomy);
		metaproteinUniRef100Cbx = new JCheckBox();
		metaproteinUniRef100Cbx.setText("Meta-Protein UniRef100");
		metaproteinUniRef100Cbx.setSelected(exportFields.metaprotUniRef100);
		metaproteinUniRef90Cbx = new JCheckBox();
		metaproteinUniRef90Cbx.setText("Meta-Protein UniRef90");
		metaproteinUniRef90Cbx.setSelected(exportFields.metaprotUniRef90);
		metaproteinUniRef50Cbx = new JCheckBox();
		metaproteinUniRef50Cbx.setText("Meta-Protein UniRef50");
		metaproteinUniRef50Cbx.setSelected(exportFields.metaprotUniRef50);
		metaproteinKOCbx = new JCheckBox();
		metaproteinKOCbx.setText("Meta-Protein KO");
		metaproteinKOCbx.setSelected(exportFields.metaprotKO);
		metaproteinECCbx = new JCheckBox();
		metaproteinECCbx.setText("Meta-Protein EC");
		metaproteinECCbx.setSelected(exportFields.metaprotEC);
		metaproteinSeqCoverageCbx = new JCheckBox();
		metaproteinSeqCoverageCbx.setText("Sequence Coverage");
		metaproteinSeqCoverageCbx.setSelected(exportFields.metaproteinSeqCoverage);		
		metaproteinPepCountCbx = new JCheckBox();
		metaproteinPepCountCbx.setText("Peptide Count");
		metaproteinPepCountCbx.setSelected(exportFields.metaproteinPepCount);
		metaproteinSpecCountCbx = new JCheckBox();
		metaproteinSpecCountCbx.setText("Spectral Count");
		metaproteinSpecCountCbx.setSelected(exportFields.metaproteinSpecCount);
		metaproteinsProteinsCbx = new JCheckBox();
		metaproteinsProteinsCbx.setText("Proteins");
		metaproteinsProteinsCbx.setSelected(exportFields.metaproteinProteins);
		metaproteinsPeptidesCbx = new JCheckBox();
		metaproteinsPeptidesCbx.setText("Peptides");
		metaproteinsPeptidesCbx.setSelected(exportFields.metaproteinPeptides);
		// Add to metaprotein feature panel
		metaproteinFeaturePanel.add(metaproteinNumberCbx, CC.xy(2,  2));
		metaproteinFeaturePanel.add(metaproteinAccessionsCbx, CC.xy(2,  4));
		metaproteinFeaturePanel.add(metaproteinDescriptionCbx, CC.xy(2,  6));		
		metaproteinFeaturePanel.add(metaproteinTaxonomyCbx, CC.xy(2,  8));
		metaproteinFeaturePanel.add(metaproteinUniRef100Cbx, CC.xy(2,  10));
		metaproteinFeaturePanel.add(metaproteinUniRef90Cbx, CC.xy(2,  12));
		metaproteinFeaturePanel.add(metaproteinUniRef50Cbx, CC.xy(2,  14));
		metaproteinFeaturePanel.add(metaproteinPepCountCbx, CC.xy(4,  2));
		metaproteinFeaturePanel.add(metaproteinSpecCountCbx, CC.xy(4, 4));
		metaproteinFeaturePanel.add(metaproteinsProteinsCbx, CC.xy(4, 6));
		metaproteinFeaturePanel.add(metaproteinsPeptidesCbx, CC.xy(4, 8));
		metaproteinFeaturePanel.add(metaproteinKOCbx, CC.xy(4,  10));
		metaproteinFeaturePanel.add(metaproteinECCbx, CC.xy(4,  12));
		
		
		
		// Button bar for metaproteins
		JPanel metaProteinButtonPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu"));
		// Metaproteins 'Ok' button
		JButton metaProtExportBtn = new JButton("Export Metaproteins", IconConstants.CHECK_ICON);
		metaProtExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		metaProtExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		metaProtExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		metaProtExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Start export
				chooseExporter("Export MetaProteins");
				// Store fields for the metaprotein checkboxes
				storeEntries();
			}
		});
		// Metaprotein configure 'close' button
		JButton metaProtCancelBtn = new JButton("Close", IconConstants.SAVE_ICON);
		metaProtCancelBtn.setRolloverIcon(IconConstants.SAVE_ROLLOVER_ICON);
		metaProtCancelBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
		metaProtCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		metaProtCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Store fields for the metaprotein checkboxes
				storeEntries();
				// Close
				close();
			}
		});
		// Metaprotein 'cancel' button
		JButton metaProteinRefuseBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		metaProteinRefuseBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		metaProteinRefuseBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		metaProteinRefuseBtn.setHorizontalAlignment(SwingConstants.LEFT);
		metaProteinRefuseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close
				close();
			}
		});
		// Add to metaprotein button panel
		metaProteinButtonPnl.add(metaProtExportBtn, CC.xy(2, 2));
		metaProteinButtonPnl.add(metaProtCancelBtn, CC.xy(4, 2));
		metaProteinButtonPnl.add(metaProteinRefuseBtn, CC.xy(6, 2));
		// Add to psm panel
		metaProteinPnl.add(metaproteinFeaturePanel, CC.xy(2, 2));
		metaProteinPnl.add(metaProteinButtonPnl, CC.xy(2, 4));
		
		JPanel metaProteinTaxonomyPnl = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
		JPanel metaProteinTaxonomyFeaturesPanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu"));
		metaProteinTaxonomySpecificPeptidesCbx = new JCheckBox();
		metaProteinTaxonomySpecificPeptidesCbx.setText("No. Peptides");
		metaProteinTaxonomySpecificPeptidesCbx.setSelected(exportFields.metaproteinTaxonomySpecificPeptides);		
		metaProteinTaxonomyKronaSpecCountCbx = new JCheckBox();
		metaProteinTaxonomyKronaSpecCountCbx.setText("Spectral Count (Krona)");
		metaProteinTaxonomyKronaSpecCountCbx.setSelected(exportFields.metaproteinTaxonomyKronaSpecCount);		
		metaProteinTaxonomyFeaturesPanel.add(metaProteinTaxonomySpecificPeptidesCbx, CC.xy(2,  2));		
		metaProteinTaxonomyFeaturesPanel.add(metaProteinTaxonomyKronaSpecCountCbx, CC.xy(4,  2));
		
		// Taxonomy button panel
		JPanel metaProtTaxPanel = new JPanel(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu"));
		// Taxonomy 'OK' function
		JButton metaProtTaxExportBtn = new JButton("Export Meta-Protein Taxonomy", IconConstants.CHECK_ICON);
		metaProtTaxExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		metaProtTaxExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		metaProtTaxExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		metaProtTaxExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Start the exporter
				chooseExporter("Export Meta-Protein Taxonomy");
				// Store fields for taxonomy checkboxes
				storeEntries();
			}
		});
		// Taxonomy configure 'Close' button
		JButton metaProtTaxCancelBtn = new JButton("Close", IconConstants.SAVE_ICON);
		metaProtTaxCancelBtn.setRolloverIcon(IconConstants.SAVE_ROLLOVER_ICON);
		metaProtTaxCancelBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
		metaProtTaxCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		metaProtTaxCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Store fields for taxonomy checkboxes
				storeEntries();
				// Close
				close();
			}
		});
		// Taxonomy 'Cancel' button
		JButton metaProtTaxRefuseBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		metaProtTaxRefuseBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		metaProtTaxRefuseBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		metaProtTaxRefuseBtn.setHorizontalAlignment(SwingConstants.LEFT);
		metaProtTaxRefuseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close
				close();
			}
		});

		// Add to taxonomy button panel
		metaProtTaxPanel.add(metaProtTaxExportBtn, CC.xy(2, 2));
		metaProtTaxPanel.add(metaProtTaxCancelBtn, CC.xy(4, 2));
		metaProtTaxPanel.add(metaProtTaxRefuseBtn, CC.xy(6, 2));
		
		// Add to psm panel
		metaProteinTaxonomyPnl.add(metaProteinTaxonomyFeaturesPanel, CC.xy(2, 2));
		metaProteinTaxonomyPnl.add(metaProtTaxPanel, CC.xy(2, 4));
		
		// TAXONOMY section
		JPanel taxonomyPnl = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
		JPanel taxonomyFeaturesPanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
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
		taxonomySpeciesCbx.setText("Species");
		taxonomySpeciesCbx.setSelected(exportFields.taxonomySpecies);
		taxonomySubspeciesCbx = new JCheckBox();
		taxonomySubspeciesCbx.setText("Subspecies");
		taxonomySubspeciesCbx.setSelected(exportFields.taxonomySubspecies);
		taxonomySpecificPeptidesCbx = new JCheckBox();
		taxonomySpecificPeptidesCbx.setText("No. Peptides");
		taxonomySpecificPeptidesCbx.setSelected(exportFields.taxonomySpecificPeptides);
		taxonomySpecificSpecCountCbx = new JCheckBox();
		taxonomySpecificSpecCountCbx.setText("Spectral Count");
		taxonomySpecificSpecCountCbx.setSelected(exportFields.taxonomySpecificSpecCount);
		taxonomyKronaSpecCountCbx = new JCheckBox();
		taxonomyKronaSpecCountCbx.setText("Spectral Count (Krona)");
		taxonomyKronaSpecCountCbx.setSelected(exportFields.taxonomyKronaSpecCount);

		// Add to taxonomyFeaturesPnl
		taxonomyFeaturesPanel.add(taxonomyUnclassifiedCbx, CC.xy(2,  2));		
		taxonomyFeaturesPanel.add(taxonomySuperKingdomCbx, CC.xy(2,  4));
		taxonomyFeaturesPanel.add(taxonomyKingdomCbx, CC.xy(2,  6));
		taxonomyFeaturesPanel.add(taxonomyPhylumCbx, CC.xy(2,  8));
		taxonomyFeaturesPanel.add(taxonomyClassCbx, CC.xy(2,  10));
		taxonomyFeaturesPanel.add(taxonomyOrderCbx, CC.xy(2,  12));
		taxonomyFeaturesPanel.add(taxonomyFamilyCbx, CC.xy(2,  14));
		taxonomyFeaturesPanel.add(taxonomyGenusCbx, CC.xy(4,  2));
		taxonomyFeaturesPanel.add(taxonomySpeciesCbx, CC.xy(4,  4));
		taxonomyFeaturesPanel.add(taxonomySubspeciesCbx, CC.xy(4,  6));
		taxonomyFeaturesPanel.add(taxonomySpecificPeptidesCbx, CC.xy(4,  8));
		taxonomyFeaturesPanel.add(taxonomySpecificSpecCountCbx, CC.xy(4,  10));
		taxonomyFeaturesPanel.add(taxonomyKronaSpecCountCbx, CC.xy(4,  12));

		// Taxonomy button panel
		JPanel taxButtonPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu"));
		// Taxonomy 'OK' function
		JButton taxExportBtn = new JButton("Export Protein Taxonomy", IconConstants.CHECK_ICON);
		taxExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		taxExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		taxExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		taxExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Start the exporter
				chooseExporter("Export Protein Taxonomy");
				// Store fields for taxonomy checkboxes
				storeEntries();
			}
		});
		// Taxonomy configure 'Close' button
		JButton taxCancelBtn = new JButton("Close", IconConstants.SAVE_ICON);
		taxCancelBtn.setRolloverIcon(IconConstants.SAVE_ROLLOVER_ICON);
		taxCancelBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
		taxCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		taxCancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Store fields for taxonomy checkboxes
				storeEntries();
				// Close
				close();
			}
		});
		// Taxonomy 'Cancel' button
		JButton taxRefuseBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		taxRefuseBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		taxRefuseBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		taxRefuseBtn.setHorizontalAlignment(SwingConstants.LEFT);
		taxRefuseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close
				close();
			}
		});

		// Add to taxonomy button panel
		taxButtonPnl.add(taxExportBtn, CC.xy(2, 2));
		taxButtonPnl.add(taxCancelBtn, CC.xy(4, 2));
		taxButtonPnl.add(taxRefuseBtn, CC.xy(6, 2));
		// Add to psm panel
		taxonomyPnl.add(taxonomyFeaturesPanel, CC.xy(2, 2));
		taxonomyPnl.add(taxButtonPnl, CC.xy(2, 4));

		tabPane.addTab("Meta-Proteins", metaProteinPnl);
		tabPane.addTab("Proteins", proteinPanel);
		tabPane.addTab("Peptides", peptidePnl);
		tabPane.addTab("PSMs", psmPanel);
		tabPane.addTab("Identified Spectra", spectrumPanel);
		tabPane.addTab("Meta-Protein Taxonomy", metaProteinTaxonomyPnl);
		tabPane.addTab("Protein Taxonomy", taxonomyPnl);

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
	private void chooseExporter(String exportTyp) {
		JFileChooser chooser = new ConfirmFileChooser(owner.getLastSelectedFolder());
		chooser.setFileFilter(Constants.CSV_FILE_FILTER);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle(exportTyp);
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
					//TODO CARE ABOUT THIS
					ResultExporter.exportMetaProteins(selectedFile.getPath(), client.getDatabaseSearchResult(), exportHeaders);
					System.out.println("");
					} else if (tabPane.getSelectedIndex() == 1) {
					resultType = "Proteins";
					ResultExporter.exportProteins(selectedFile.getPath(), client.getDatabaseSearchResult(), exportHeaders);
				} else if (tabPane.getSelectedIndex() == 2) {
					resultType = "Peptides";
					ResultExporter.exportPeptides(selectedFile.getPath(), client.getDatabaseSearchResult(), exportHeaders);
				} else if (tabPane.getSelectedIndex() == 3) {
					resultType = "PSMs";
					ResultExporter.exportPSMs(selectedFile.getPath(), client.getDatabaseSearchResult(), exportHeaders);
				} else if (tabPane.getSelectedIndex() == 4) {
					resultType = "Identified Spectra";
					ResultExporter.exportIdentifiedSpectra(selectedFile.getPath(), client.getDatabaseSearchResult(), exportHeaders);
				} else if (tabPane.getSelectedIndex() == 5) {
					resultType = "Meta-Protein Taxonomy";
					ResultExporter.exportMetaProteinTaxonomy(selectedFile.getPath(), client.getDatabaseSearchResult(), exportHeaders);
				} else if (tabPane.getSelectedIndex() == 6) {
					resultType = "Protein Taxonomy";
					ResultExporter.exportProteinTaxonomy(selectedFile.getPath(), client.getDatabaseSearchResult(), exportHeaders);
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

	/**
	 * Collect the headers for the export.
	 */
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
		if(metaproteinUniRef100Cbx.isSelected()) exportHeaders.add(new ExportHeader(5, metaproteinUniRef100Cbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinUniRef90Cbx.isSelected()) exportHeaders.add(new ExportHeader(6, metaproteinUniRef90Cbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinUniRef50Cbx.isSelected()) exportHeaders.add(new ExportHeader(7, metaproteinUniRef50Cbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinKOCbx.isSelected()) exportHeaders.add(new ExportHeader(8, metaproteinKOCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinECCbx.isSelected()) exportHeaders.add(new ExportHeader(9, metaproteinECCbx.getText() , ExportHeaderType.METAPROTEINS));
		
		if(metaproteinPepCountCbx.isSelected()) exportHeaders.add(new ExportHeader(10, metaproteinPepCountCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinSpecCountCbx.isSelected()) exportHeaders.add(new ExportHeader(11, metaproteinSpecCountCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinsProteinsCbx.isSelected()) exportHeaders.add(new ExportHeader(12, metaproteinsProteinsCbx.getText() , ExportHeaderType.METAPROTEINS));
		if(metaproteinsPeptidesCbx.isSelected()) exportHeaders.add(new ExportHeader(13, metaproteinsPeptidesCbx.getText() , ExportHeaderType.METAPROTEINS));
		
		// Meta-protein taxonomy
		if(metaProteinTaxonomySpecificPeptidesCbx.isSelected()) exportHeaders.add(new ExportHeader(1, metaProteinTaxonomySpecificPeptidesCbx.getText(), ExportHeaderType.METAPROTEINTAXONOMY));
		if(metaProteinTaxonomyKronaSpecCountCbx.isSelected()) exportHeaders.add(new ExportHeader(2, metaProteinTaxonomyKronaSpecCountCbx.getText(), ExportHeaderType.METAPROTEINTAXONOMY));
		
		// Protein Taxonomy
		if(taxonomyUnclassifiedCbx.isSelected()) exportHeaders.add(new ExportHeader(1, taxonomyUnclassifiedCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomySuperKingdomCbx.isSelected()) exportHeaders.add(new ExportHeader(2, taxonomySuperKingdomCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomyKingdomCbx.isSelected()) exportHeaders.add(new ExportHeader(3, taxonomyKingdomCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomyPhylumCbx.isSelected()) exportHeaders.add(new ExportHeader(4, taxonomyPhylumCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomyClassCbx.isSelected()) exportHeaders.add(new ExportHeader(5, taxonomyClassCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomyOrderCbx.isSelected()) exportHeaders.add(new ExportHeader(6, taxonomyOrderCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomyFamilyCbx.isSelected()) exportHeaders.add(new ExportHeader(7, taxonomyFamilyCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomyGenusCbx.isSelected()) exportHeaders.add(new ExportHeader(8, taxonomyGenusCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomySpeciesCbx.isSelected()) exportHeaders.add(new ExportHeader(9, taxonomySpeciesCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomySubspeciesCbx.isSelected()) exportHeaders.add(new ExportHeader(10, taxonomySubspeciesCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomySpecificPeptidesCbx.isSelected()) exportHeaders.add(new ExportHeader(11, taxonomySpecificPeptidesCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomySpecificSpecCountCbx.isSelected()) exportHeaders.add(new ExportHeader(12, taxonomySpecificSpecCountCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));
		if(taxonomyKronaSpecCountCbx.isSelected()) exportHeaders.add(new ExportHeader(13, taxonomyKronaSpecCountCbx.getText(), ExportHeaderType.PROTEINTAXONOMY));

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
		if(psmPEPCbx.isSelected()) exportHeaders.add(new ExportHeader(8, psmPEPCbx.getText(), ExportHeaderType.PSMS));
		if(psmScoreCbx.isSelected()) exportHeaders.add(new ExportHeader(9, psmScoreCbx.getText(), ExportHeaderType.PSMS));
		
		// Identified Spectra
		if(spectrumNumberCbx.isSelected()) exportHeaders.add(new ExportHeader(1, spectrumNumberCbx.getText(), ExportHeaderType.SPECTRA));
		if(spectrumIDCbx.isSelected()) exportHeaders.add(new ExportHeader(2, spectrumIDCbx.getText(), ExportHeaderType.SPECTRA));
		if(spectrumTitleCbx.isSelected()) exportHeaders.add(new ExportHeader(3, spectrumTitleCbx.getText(), ExportHeaderType.SPECTRA));
		if(spectrumPeptidesCbx.isSelected()) exportHeaders.add(new ExportHeader(4, spectrumPeptidesCbx.getText(), ExportHeaderType.SPECTRA));
		if(spectrumAccessionsCbx.isSelected()) exportHeaders.add(new ExportHeader(5, spectrumAccessionsCbx.getText(), ExportHeaderType.SPECTRA));
	}

	/**
	 * This method stores the protein entries into the export fields.
	 */
	private void storeEntries() {
		// Protein entries
		exportFields.proteinNumber			= proteinNumberCbx.isSelected(); 
		exportFields.proteinAccession		= proteinAccessionCbx.isSelected();
		exportFields.proteinDescription		= proteinDescriptionCbx.isSelected();
		exportFields.proteinTaxonomy			= proteinSpeciesCbx.isSelected();
		exportFields.proteinSeqCoverage		= proteinSeqCoverageCbx.isSelected();
		exportFields.proteinMolWeight		= proteinMolWeightCbx.isSelected();
		exportFields.proteinPi				= proteinPiCbx.isSelected();
		exportFields.proteinPepCount		= proteinPepCountCbx.isSelected();
		exportFields.proteinSpecCount		= proteinSpecCountCbx.isSelected();
		exportFields.proteinEmPAI			= proteinEmPAICbx.isSelected();
		exportFields.proteinNSAF			= proteinNSAFCbx.isSelected();
		exportFields.proteinSequence		= proteinSequenceCbx.isSelected();
		exportFields.proteinPeptides		= proteinPeptidesCbx.isSelected();

		// Peptide entries
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

		// PSM entries
		exportFields.psmNumber					= psmNumberCbx.isSelected();
		exportFields.psmProteinAccession		= psmProteinAccessionCbx.isSelected();
		exportFields.psmPeptideSequence			= psmPeptideSequenceCbx.isSelected();
		exportFields.psmSpectrumTitle			= psmSpectrumTitleCbx.isSelected();
		exportFields.psmCharge					= psmChargeCbx.isSelected();
		exportFields.psmSearchEngine			= psmSearchEngineCbx.isSelected();
		exportFields.psmQValue					= psmQValueCbx.isSelected();
		exportFields.psmPEP						= psmPEPCbx.isSelected();
		exportFields.psmScore					= psmScoreCbx.isSelected();

		// Meta-protein entries
		exportFields.metaproteinNumber		= metaproteinNumberCbx.isSelected();
		exportFields.metaproteinAccessions	= metaproteinAccessionsCbx.isSelected();
		exportFields.metaproteinDescription	= metaproteinDescriptionCbx.isSelected();
		exportFields.metaproteinTaxonomy	= metaproteinTaxonomyCbx.isSelected();
		exportFields.metaprotUniRef100		= metaproteinUniRef100Cbx.isSelected();
		exportFields.metaprotUniRef90		= metaproteinUniRef90Cbx.isSelected();
		exportFields.metaprotUniRef50		= metaproteinUniRef50Cbx.isSelected();
		exportFields.metaprotKO				= metaproteinKOCbx.isSelected();
		exportFields.metaprotEC				= metaproteinECCbx.isSelected();
		exportFields.metaproteinSeqCoverage	= metaproteinSeqCoverageCbx.isSelected();
		exportFields.metaproteinPepCount 	= metaproteinPepCountCbx.isSelected();
		exportFields.metaproteinSpecCount	= metaproteinSpecCountCbx.isSelected();
		exportFields.metaproteinPeptides	= metaproteinsPeptidesCbx.isSelected();

		// Protein taxonomy entries
		exportFields.taxonomyUnclassified		= taxonomyUnclassifiedCbx.isSelected();
		exportFields.taxonomySuperKingdom		= taxonomySuperKingdomCbx.isSelected();
		exportFields.taxonomyKingdom			= taxonomyKingdomCbx.isSelected();
		exportFields.taxonomyPhylum				= taxonomyPhylumCbx.isSelected();
		exportFields.taxonomyClass				= taxonomyClassCbx.isSelected();
		exportFields.taxonomyOrder				= taxonomyOrderCbx.isSelected();
		exportFields.taxonomyFamily				= taxonomyFamilyCbx.isSelected();
		exportFields.taxonomyGenus				= taxonomyGenusCbx.isSelected();
		exportFields.taxonomySpecies			= taxonomySpeciesCbx.isSelected();
		exportFields.taxonomySubspecies			= taxonomySubspeciesCbx.isSelected();
		exportFields.taxonomySpecificPeptides	= taxonomySpecificPeptidesCbx.isSelected();
		exportFields.taxonomySpecificSpecCount	= taxonomySpecificSpecCountCbx.isSelected();
		
		// Meta-protein taxonomy entries
		exportFields.metaproteinTaxonomySpecificPeptides = metaProteinTaxonomySpecificPeptidesCbx.isSelected();
		exportFields.metaproteinTaxonomyKronaSpecCount = metaProteinTaxonomyKronaSpecCountCbx.isSelected();
	}
}
