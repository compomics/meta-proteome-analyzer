package de.mpa.client.ui.resultspanel.dialogs;

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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ExportFields;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.sharedelements.ScreenConfig;
import de.mpa.client.ui.sharedelements.dialogs.ConfirmFileChooser;
import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.io.ExportHeader;
import de.mpa.io.ResultExporter;
import de.mpa.model.analysis.UniProtUtilities;

/**
 * This dialog enables the result export of proteins, peptides. PSMs,
 * meta-protein, taxonomies etc.
 *
 * @author T. Muth
 */
@SuppressWarnings("serial")
public class ExportDialog extends JDialog {

	/**
	 * The ClientFrame
	 */
	private final ClientFrame owner;

	/**
	 * The instance of the client.
	 */
	private final Client client = Client.getInstance();

	/**
	 * Class containing the default values for the checkboxes
	 */
	private final ExportFields exportFields;

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
	 * the comboboxes for chord data exporter
	 */

	private JComboBox<String> chordTaxonomyLevel;
	private JComboBox<String> chordKeywordLevel;
	private JSlider chordSlider;

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
		this.initComponents();
		this.showDialog();
	}

	/**
	 * Initializes and lays out all components inside this dialog grouped by
	 * sections identifiers.
	 */
	private void initComponents() {

		this.tabPane = new JTabbedPane();

		JPanel chordPanel = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		JPanel chordFeaturePnl = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		JPanel chordButtonPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu"));
		JButton chordExportBtn = new JButton("Export Proteins", IconConstants.CHECK_ICON);

		// combobox for taxonomy level

		String comboBoxTaxonomyList[] = UniProtUtilities.TAXONOMY_RANKS_MAP.keySet()
				.toArray(new String[UniProtUtilities.TAXONOMY_RANKS_MAP.keySet().size()]);
		chordTaxonomyLevel = new JComboBox<>(comboBoxTaxonomyList);

		String comboBoxKeywordList[] = { UniProtUtilities.KeywordCategory.BIOLOGICAL_PROCESS.toString(),
				UniProtUtilities.KeywordCategory.CELLULAR_COMPONENT.toString(),
				UniProtUtilities.KeywordCategory.CODING_SEQUNCE_DIVERSITY.toString(),
				UniProtUtilities.KeywordCategory.DEVELOPMENTAL_STAGE.toString(),
				UniProtUtilities.KeywordCategory.DISEASE.toString(), UniProtUtilities.KeywordCategory.DOMAIN.toString(),
				UniProtUtilities.KeywordCategory.LIGAND.toString(),
				UniProtUtilities.KeywordCategory.MOLECULAR_FUNCTION.toString(),
				UniProtUtilities.KeywordCategory.PTM.toString(),
				UniProtUtilities.KeywordCategory.TECHNICAL_TERM.toString() };
		chordKeywordLevel = new JComboBox<>(comboBoxKeywordList);

		// size of result matrix
		chordSlider = new JSlider();
		chordSlider.setMaximum(50);
		chordSlider.setMinimum(5);
		chordSlider.setMajorTickSpacing(10);
		chordSlider.setMinorTickSpacing(1);
		chordSlider.createStandardLabels(1);
		chordSlider.setPaintTicks(true);
		chordSlider.setPaintLabels(true);
		chordSlider.setValue(25);

		chordFeaturePnl.add(chordSlider, CC.xy(2, 2));
		chordFeaturePnl.add(chordTaxonomyLevel, CC.xy(2, 4));
		chordFeaturePnl.add(chordKeywordLevel, CC.xy(2, 6));

		chordExportBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		chordExportBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		chordExportBtn.setHorizontalAlignment(SwingConstants.LEFT);
		chordExportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Start export
				ExportDialog.this.chooseExporter("Export Chord Data");
				// Store entries from the protein checkboxes
				ExportDialog.this.storeEntries();
			}
		});
		// // Protein 'Close' button
		// JButton chordCancelBtn = new JButton("Close", IconConstants.SAVE_ICON);
		// chordCancelBtn.setRolloverIcon(IconConstants.SAVE_ROLLOVER_ICON);
		// chordCancelBtn.setPressedIcon(IconConstants.SAVE_PRESSED_ICON);
		// chordCancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		// chordCancelBtn.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// // Store entries from the protein checkboxes
		// ExportDialog.this.storeEntries();
		// ExportDialog.this.close();
		// }
		// });

		// Protein 'Close' button
		JButton chordRefuseBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		chordRefuseBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		chordRefuseBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		chordRefuseBtn.setHorizontalAlignment(SwingConstants.LEFT);
		chordRefuseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close
				ExportDialog.this.close();
			}
		});
		// Add protein buttons to the protein button bar
		chordButtonPnl.add(chordExportBtn, CC.xy(2, 2));
		// chordButtonPnl.add(chordCancelBtn, CC.xy(4, 2));
		chordButtonPnl.add(chordRefuseBtn, CC.xy(6, 2));
		// Add all to the proteinpanel
		chordPanel.add(chordFeaturePnl, CC.xy(2, 2));
		chordPanel.add(chordButtonPnl, CC.xy(2, 4));

		// PROTEINS section
		JPanel proteinPanel = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		// Protein features

		JPanel proteinFeaturePnl = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		this.proteinNumberCbx = new JCheckBox();
		this.proteinNumberCbx.setText("Protein No.");
		this.proteinNumberCbx.setSelected(this.exportFields.proteinNumber);
		this.proteinAccessionCbx = new JCheckBox();
		this.proteinAccessionCbx.setText("Protein Accession");
		this.proteinAccessionCbx.setSelected(this.exportFields.proteinAccession);
		this.proteinDescriptionCbx = new JCheckBox();
		this.proteinDescriptionCbx.setText("Protein Description");
		this.proteinDescriptionCbx.setSelected(this.exportFields.proteinDescription);
		this.proteinSpeciesCbx = new JCheckBox();
		this.proteinSpeciesCbx.setText("Protein Taxonomy");
		this.proteinSpeciesCbx.setSelected(this.exportFields.proteinTaxonomy);
		this.proteinSeqCoverageCbx = new JCheckBox();
		this.proteinSeqCoverageCbx.setText("Sequence Coverage");
		this.proteinSeqCoverageCbx.setSelected(this.exportFields.proteinSeqCoverage);
		this.proteinSequenceCbx = new JCheckBox();
		this.proteinSequenceCbx.setText("Protein Sequence");
		this.proteinSequenceCbx.setSelected(this.exportFields.proteinSequence);
		this.proteinMolWeightCbx = new JCheckBox();
		this.proteinMolWeightCbx.setText("Molecular Weight");
		this.proteinMolWeightCbx.setSelected(this.exportFields.proteinMolWeight);
		this.proteinPiCbx = new JCheckBox();
		this.proteinPiCbx.setText("Isoelectric Point");
		this.proteinPiCbx.setSelected(this.exportFields.proteinPi);
		this.proteinPepCountCbx = new JCheckBox();
		this.proteinPepCountCbx.setText("Peptide Count");
		this.proteinPepCountCbx.setSelected(this.exportFields.proteinPepCount);
		this.proteinSpecCountCbx = new JCheckBox();
		this.proteinSpecCountCbx.setText("Spectral Count");
		this.proteinSpecCountCbx.setSelected(this.exportFields.proteinSpecCount);
		this.proteinEmPAICbx = new JCheckBox();
		this.proteinEmPAICbx.setText("emPAI");
		this.proteinEmPAICbx.setSelected(this.exportFields.proteinEmPAI);
		this.proteinNSAFCbx = new JCheckBox();
		this.proteinNSAFCbx.setText("NSAF");
		this.proteinNSAFCbx.setSelected(this.exportFields.proteinNSAF);
		this.proteinPeptidesCbx = new JCheckBox();
		this.proteinPeptidesCbx.setText("Peptides");
		this.proteinPeptidesCbx.setSelected(this.exportFields.proteinPeptides);
		// Add protein features to proteinFeaturePnl
		proteinFeaturePnl.add(this.proteinNumberCbx, CC.xy(2, 2));
		proteinFeaturePnl.add(this.proteinAccessionCbx, CC.xy(2, 4));
		proteinFeaturePnl.add(this.proteinDescriptionCbx, CC.xy(2, 6));
		proteinFeaturePnl.add(this.proteinSpeciesCbx, CC.xy(2, 8));
		proteinFeaturePnl.add(this.proteinSeqCoverageCbx, CC.xy(2, 10));
		proteinFeaturePnl.add(this.proteinPepCountCbx, CC.xy(2, 12));
		proteinFeaturePnl.add(this.proteinNSAFCbx, CC.xy(4, 2));
		proteinFeaturePnl.add(this.proteinEmPAICbx, CC.xy(4, 4));
		proteinFeaturePnl.add(this.proteinSpecCountCbx, CC.xy(4, 6));
		proteinFeaturePnl.add(this.proteinPiCbx, CC.xy(4, 8));
		proteinFeaturePnl.add(this.proteinMolWeightCbx, CC.xy(4, 10));
		proteinFeaturePnl.add(this.proteinSequenceCbx, CC.xy(4, 12));
		proteinFeaturePnl.add(this.proteinPeptidesCbx, CC.xy(2, 14));
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
				ExportDialog.this.chooseExporter("Export Proteins");
				// Store entries from the protein checkboxes
				ExportDialog.this.storeEntries();
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
				ExportDialog.this.storeEntries();
				ExportDialog.this.close();
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
				ExportDialog.this.close();
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
		JPanel peptideFeaturesPnl = new JPanel(
				new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		this.peptideNumberCbx = new JCheckBox();
		this.peptideNumberCbx.setText("Peptide No.");
		this.peptideNumberCbx.setSelected(this.exportFields.peptideNumber);
		this.peptideProteinAccessionsCbx = new JCheckBox();
		this.peptideProteinAccessionsCbx.setText("Protein Accession(s)");
		this.peptideProteinAccessionsCbx.setSelected(this.exportFields.peptideProteinAccessions);
		this.peptideSequenceCbx = new JCheckBox();
		this.peptideSequenceCbx.setText("Peptide Sequence");
		this.peptideSequenceCbx.setSelected(this.exportFields.peptideSequence);
		this.uniquePeptidesOnlyCbx = new JCheckBox();
		this.uniquePeptidesOnlyCbx.setText("Unique Peptides Only");
		this.uniquePeptidesOnlyCbx.setSelected(this.exportFields.uniquePeptidesOnly);
		this.uniquePeptidesOnlyCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (ExportDialog.this.uniquePeptidesOnlyCbx.isSelected()
						&& ExportDialog.this.sharedPeptidesOnlyCbx.isSelected())
					ExportDialog.this.sharedPeptidesOnlyCbx.setSelected(false);
			}
		});
		this.sharedPeptidesOnlyCbx = new JCheckBox();
		this.sharedPeptidesOnlyCbx.setText("Shared Peptides Only");

		this.sharedPeptidesOnlyCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (ExportDialog.this.uniquePeptidesOnlyCbx.isSelected()
						&& ExportDialog.this.sharedPeptidesOnlyCbx.isSelected())
					ExportDialog.this.uniquePeptidesOnlyCbx.setSelected(false);
			}
		});
		if (this.exportFields.sharedPeptidesOnly && !this.exportFields.uniquePeptidesOnly) {
			this.sharedPeptidesOnlyCbx.setSelected(this.exportFields.sharedPeptidesOnly);
		}
		// Additional peptide features
		this.peptideProtCountCbx = new JCheckBox();
		this.peptideProtCountCbx.setText("Protein Count");
		this.peptideProtCountCbx.setSelected(this.exportFields.peptideProtCount);
		this.peptideSpecCountCbx = new JCheckBox();
		this.peptideSpecCountCbx.setText("Spectral Count");
		this.peptideSpecCountCbx.setSelected(this.exportFields.peptideSpecCount);
		this.peptideTaxGroup = new JCheckBox();
		this.peptideTaxGroup.setText("Taxonomic Group");
		this.peptideTaxGroup.setSelected(this.exportFields.peptideTaxGroup);
		this.peptideTaxRank = new JCheckBox();
		this.peptideTaxRank.setText("Taxonomic Rank");
		this.peptideTaxRank.setSelected(this.exportFields.peptideTaxRank);
		this.peptideTaxIdCbx = new JCheckBox();
		this.peptideTaxIdCbx.setText("NCBI Taxonomy ID");
		this.peptideTaxIdCbx.setSelected(this.exportFields.peptideTaxId);
		// Add features to the panel
		peptideFeaturesPnl.add(this.peptideNumberCbx, CC.xy(2, 2));
		peptideFeaturesPnl.add(this.peptideProteinAccessionsCbx, CC.xy(2, 4));
		peptideFeaturesPnl.add(this.peptideSequenceCbx, CC.xy(2, 6));
		peptideFeaturesPnl.add(this.uniquePeptidesOnlyCbx, CC.xy(2, 8));
		peptideFeaturesPnl.add(this.sharedPeptidesOnlyCbx, CC.xy(2, 10));
		peptideFeaturesPnl.add(this.peptideProtCountCbx, CC.xy(4, 2));
		peptideFeaturesPnl.add(this.peptideSpecCountCbx, CC.xy(4, 4));
		peptideFeaturesPnl.add(this.peptideTaxGroup, CC.xy(4, 6));
		peptideFeaturesPnl.add(this.peptideTaxRank, CC.xy(4, 8));
		peptideFeaturesPnl.add(this.peptideTaxIdCbx, CC.xy(4, 10));

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
				ExportDialog.this.chooseExporter("Export Peptides");
				// Store entries from the peptide checkboxes
				ExportDialog.this.storeEntries();
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
				ExportDialog.this.storeEntries();
				// Close
				ExportDialog.this.close();
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
				ExportDialog.this.close();
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
		JPanel psmFeaturesPanel = new JPanel(
				new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		this.psmNumberCbx = new JCheckBox();
		this.psmNumberCbx.setText("PSM No.");
		this.psmNumberCbx.setSelected(this.exportFields.psmNumber);
		this.psmProteinAccessionCbx = new JCheckBox();
		this.psmProteinAccessionCbx.setText("Protein Accession(s)");
		this.psmProteinAccessionCbx.setSelected(this.exportFields.psmProteinAccession);
		this.psmPeptideSequenceCbx = new JCheckBox();
		this.psmPeptideSequenceCbx.setText("Peptide Sequence");
		this.psmPeptideSequenceCbx.setSelected(this.exportFields.psmPeptideSequence);
		this.psmSpectrumTitleCbx = new JCheckBox();
		this.psmSpectrumTitleCbx.setText("Spectrum Title");
		this.psmSpectrumTitleCbx.setSelected(this.exportFields.psmSpectrumTitle);
		this.psmChargeCbx = new JCheckBox();
		this.psmChargeCbx.setText("Charge");
		this.psmChargeCbx.setSelected(this.exportFields.psmCharge);
		this.psmSearchEngineCbx = new JCheckBox();
		this.psmSearchEngineCbx.setText("Search Engine");
		this.psmSearchEngineCbx.setSelected(this.exportFields.psmSearchEngine);
		this.psmQValueCbx = new JCheckBox();
		this.psmQValueCbx.setText("q-Value");
		this.psmQValueCbx.setSelected(this.exportFields.psmQValue);
		this.psmScoreCbx = new JCheckBox();
		this.psmScoreCbx.setText("Score");
		this.psmScoreCbx.setSelected(false);

		// Add to PSM features panel.
		psmFeaturesPanel.add(this.psmNumberCbx, CC.xy(2, 2));
		psmFeaturesPanel.add(this.psmProteinAccessionCbx, CC.xy(2, 4));
		psmFeaturesPanel.add(this.psmPeptideSequenceCbx, CC.xy(2, 6));
		psmFeaturesPanel.add(this.psmSpectrumTitleCbx, CC.xy(2, 8));
		psmFeaturesPanel.add(this.psmChargeCbx, CC.xy(4, 2));
		psmFeaturesPanel.add(this.psmSearchEngineCbx, CC.xy(4, 4));
		psmFeaturesPanel.add(this.psmQValueCbx, CC.xy(4, 6));
		// psmFeaturesPanel.add(this.psmScoreCbx, CC.xy(4, 8));
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
				ExportDialog.this.chooseExporter("Export PSMs");
				// Store entries from the PSM checkboxes
				ExportDialog.this.storeEntries();
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
				ExportDialog.this.storeEntries();
				// Close
				ExportDialog.this.close();
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
				ExportDialog.this.close();
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
		JPanel spectrumFeaturesPanel = new JPanel(
				new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		this.spectrumNumberCbx = new JCheckBox();
		this.spectrumNumberCbx.setText("Spectrum Number");
		this.spectrumNumberCbx.setSelected(this.exportFields.spectrumID);
		this.spectrumIDCbx = new JCheckBox();
		this.spectrumIDCbx.setText("Spectrum ID");
		this.spectrumIDCbx.setSelected(this.exportFields.spectrumID);
		this.spectrumTitleCbx = new JCheckBox();
		this.spectrumTitleCbx.setText("Spectrum Title");
		this.spectrumTitleCbx.setSelected(this.exportFields.spectrumTitle);
		this.spectrumPeptidesCbx = new JCheckBox();
		this.spectrumPeptidesCbx.setText("Peptide(s)");
		this.spectrumPeptidesCbx.setSelected(this.exportFields.spectrumPeptides);
		this.spectrumAccessionsCbx = new JCheckBox();
		this.spectrumAccessionsCbx.setText("Protein Accession(s)");
		this.spectrumAccessionsCbx.setSelected(this.exportFields.spectrumAccessions);

		// Add to spectrum features panel.
		spectrumFeaturesPanel.add(this.spectrumNumberCbx, CC.xy(2, 2));
		spectrumFeaturesPanel.add(this.spectrumIDCbx, CC.xy(4, 2));
		spectrumFeaturesPanel.add(this.spectrumTitleCbx, CC.xy(2, 4));
		spectrumFeaturesPanel.add(this.spectrumPeptidesCbx, CC.xy(4, 4));
		spectrumFeaturesPanel.add(this.spectrumAccessionsCbx, CC.xy(2, 6));

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
				ExportDialog.this.chooseExporter("Export Identified Spectra");
				// Store entries from the PSM checkboxes.
				ExportDialog.this.storeEntries();
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
				ExportDialog.this.storeEntries();
				// Close
				ExportDialog.this.close();
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
				ExportDialog.this.close();
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
		JPanel metaproteinFeaturePanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu,p, 5dlu, p, 5dlu"));
		this.metaproteinNumberCbx = new JCheckBox();
		this.metaproteinNumberCbx.setText("Meta-Protein No.");
		this.metaproteinNumberCbx.setSelected(this.exportFields.metaproteinNumber);
		this.metaproteinAccessionsCbx = new JCheckBox();
		this.metaproteinAccessionsCbx.setText("Meta-Protein Accession");
		this.metaproteinAccessionsCbx.setSelected(this.exportFields.metaproteinAccessions);
		this.metaproteinDescriptionCbx = new JCheckBox();
		this.metaproteinDescriptionCbx.setText("Meta-Protein Description");
		this.metaproteinDescriptionCbx.setSelected(this.exportFields.metaproteinDescription);
		this.metaproteinTaxonomyCbx = new JCheckBox();
		this.metaproteinTaxonomyCbx.setText("Meta-Protein Taxonomy");
		this.metaproteinTaxonomyCbx.setSelected(this.exportFields.metaproteinTaxonomy);
		this.metaproteinUniRef100Cbx = new JCheckBox();
		this.metaproteinUniRef100Cbx.setText("Meta-Protein UniRef100");
		this.metaproteinUniRef100Cbx.setSelected(this.exportFields.metaprotUniRef100);
		this.metaproteinUniRef90Cbx = new JCheckBox();
		this.metaproteinUniRef90Cbx.setText("Meta-Protein UniRef90");
		this.metaproteinUniRef90Cbx.setSelected(this.exportFields.metaprotUniRef90);
		this.metaproteinUniRef50Cbx = new JCheckBox();
		this.metaproteinUniRef50Cbx.setText("Meta-Protein UniRef50");
		this.metaproteinUniRef50Cbx.setSelected(this.exportFields.metaprotUniRef50);
		this.metaproteinKOCbx = new JCheckBox();
		this.metaproteinKOCbx.setText("Meta-Protein KO");
		this.metaproteinKOCbx.setSelected(this.exportFields.metaprotKO);
		this.metaproteinECCbx = new JCheckBox();
		this.metaproteinECCbx.setText("Meta-Protein EC");
		this.metaproteinECCbx.setSelected(this.exportFields.metaprotEC);
		this.metaproteinSeqCoverageCbx = new JCheckBox();
		this.metaproteinSeqCoverageCbx.setText("Sequence Coverage");
		this.metaproteinSeqCoverageCbx.setSelected(this.exportFields.metaproteinSeqCoverage);
		this.metaproteinPepCountCbx = new JCheckBox();
		this.metaproteinPepCountCbx.setText("Peptide Count");
		this.metaproteinPepCountCbx.setSelected(this.exportFields.metaproteinPepCount);
		this.metaproteinSpecCountCbx = new JCheckBox();
		this.metaproteinSpecCountCbx.setText("Spectral Count");
		this.metaproteinSpecCountCbx.setSelected(this.exportFields.metaproteinSpecCount);
		this.metaproteinsProteinsCbx = new JCheckBox();
		this.metaproteinsProteinsCbx.setText("Proteins");
		this.metaproteinsProteinsCbx.setSelected(this.exportFields.metaproteinProteins);
		this.metaproteinsPeptidesCbx = new JCheckBox();
		this.metaproteinsPeptidesCbx.setText("Peptides");
		this.metaproteinsPeptidesCbx.setSelected(this.exportFields.metaproteinPeptides);
		// Add to metaprotein feature panel
		metaproteinFeaturePanel.add(this.metaproteinNumberCbx, CC.xy(2, 2));
		metaproteinFeaturePanel.add(this.metaproteinAccessionsCbx, CC.xy(2, 4));
		metaproteinFeaturePanel.add(this.metaproteinDescriptionCbx, CC.xy(2, 6));
		metaproteinFeaturePanel.add(this.metaproteinTaxonomyCbx, CC.xy(2, 8));
		metaproteinFeaturePanel.add(this.metaproteinUniRef100Cbx, CC.xy(2, 10));
		metaproteinFeaturePanel.add(this.metaproteinUniRef90Cbx, CC.xy(2, 12));
		metaproteinFeaturePanel.add(this.metaproteinUniRef50Cbx, CC.xy(2, 14));
		metaproteinFeaturePanel.add(this.metaproteinPepCountCbx, CC.xy(4, 2));
		metaproteinFeaturePanel.add(this.metaproteinSpecCountCbx, CC.xy(4, 4));
		metaproteinFeaturePanel.add(this.metaproteinsProteinsCbx, CC.xy(4, 6));
		metaproteinFeaturePanel.add(this.metaproteinsPeptidesCbx, CC.xy(4, 8));
		metaproteinFeaturePanel.add(this.metaproteinKOCbx, CC.xy(4, 10));
		metaproteinFeaturePanel.add(this.metaproteinECCbx, CC.xy(4, 12));

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
				ExportDialog.this.chooseExporter("Export MetaProteins");
				// Store fields for the metaprotein checkboxes
				ExportDialog.this.storeEntries();
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
				ExportDialog.this.storeEntries();
				// Close
				ExportDialog.this.close();
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
				ExportDialog.this.close();
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
		JPanel metaProteinTaxonomyFeaturesPanel = new JPanel(
				new FormLayout("5dlu, p, 30dlu, p, 5dlu", "5dlu, p, 5dlu"));
		this.metaProteinTaxonomySpecificPeptidesCbx = new JCheckBox();
		this.metaProteinTaxonomySpecificPeptidesCbx.setText("No. Peptides");
		this.metaProteinTaxonomySpecificPeptidesCbx.setSelected(this.exportFields.metaproteinTaxonomySpecificPeptides);
		this.metaProteinTaxonomyKronaSpecCountCbx = new JCheckBox();
		this.metaProteinTaxonomyKronaSpecCountCbx.setText("Spectral Count (Krona)");
		this.metaProteinTaxonomyKronaSpecCountCbx.setSelected(this.exportFields.metaproteinTaxonomyKronaSpecCount);
		metaProteinTaxonomyFeaturesPanel.add(this.metaProteinTaxonomySpecificPeptidesCbx, CC.xy(2, 2));
		metaProteinTaxonomyFeaturesPanel.add(this.metaProteinTaxonomyKronaSpecCountCbx, CC.xy(4, 2));

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
				ExportDialog.this.chooseExporter("Export Meta-Protein Taxonomy");
				// Store fields for taxonomy checkboxes
				ExportDialog.this.storeEntries();
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
				ExportDialog.this.storeEntries();
				// Close
				ExportDialog.this.close();
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
				ExportDialog.this.close();
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
		JPanel taxonomyFeaturesPanel = new JPanel(new FormLayout("5dlu, p, 30dlu, p, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		this.taxonomyUnclassifiedCbx = new JCheckBox();
		this.taxonomyUnclassifiedCbx.setText("Unclassified");
		this.taxonomyUnclassifiedCbx.setSelected(this.exportFields.taxonomyUnclassified);
		this.taxonomySuperKingdomCbx = new JCheckBox();
		this.taxonomySuperKingdomCbx.setText("Superkingdom");
		this.taxonomySuperKingdomCbx.setSelected(this.exportFields.taxonomySuperKingdom);
		this.taxonomyKingdomCbx = new JCheckBox();
		this.taxonomyKingdomCbx.setText("Kingdom");
		this.taxonomyKingdomCbx.setSelected(this.exportFields.taxonomyKingdom);
		this.taxonomyPhylumCbx = new JCheckBox();
		this.taxonomyPhylumCbx.setText("Phylum");
		this.taxonomyPhylumCbx.setSelected(this.exportFields.taxonomyPhylum);
		this.taxonomyOrderCbx = new JCheckBox();
		this.taxonomyOrderCbx.setText("Order");
		this.taxonomyOrderCbx.setSelected(this.exportFields.taxonomyOrder);
		this.taxonomyClassCbx = new JCheckBox();
		this.taxonomyClassCbx.setText("Class");
		this.taxonomyClassCbx.setSelected(this.exportFields.taxonomyClass);
		this.taxonomyFamilyCbx = new JCheckBox();
		this.taxonomyFamilyCbx.setText("Family");
		this.taxonomyFamilyCbx.setSelected(this.exportFields.taxonomyFamily);
		this.taxonomyGenusCbx = new JCheckBox();
		this.taxonomyGenusCbx.setText("Genus");
		this.taxonomyGenusCbx.setSelected(this.exportFields.taxonomyGenus);
		this.taxonomySpeciesCbx = new JCheckBox();
		this.taxonomySpeciesCbx.setText("Species");
		this.taxonomySpeciesCbx.setSelected(this.exportFields.taxonomySpecies);
		this.taxonomySubspeciesCbx = new JCheckBox();
		this.taxonomySubspeciesCbx.setText("Subspecies");
		this.taxonomySubspeciesCbx.setSelected(this.exportFields.taxonomySubspecies);
		this.taxonomySpecificPeptidesCbx = new JCheckBox();
		this.taxonomySpecificPeptidesCbx.setText("No. Peptides");
		this.taxonomySpecificPeptidesCbx.setSelected(this.exportFields.taxonomySpecificPeptides);
		this.taxonomySpecificSpecCountCbx = new JCheckBox();
		this.taxonomySpecificSpecCountCbx.setText("Spectral Count");
		this.taxonomySpecificSpecCountCbx.setSelected(this.exportFields.taxonomySpecificSpecCount);
		this.taxonomyKronaSpecCountCbx = new JCheckBox();
		this.taxonomyKronaSpecCountCbx.setText("Spectral Count (Krona)");
		this.taxonomyKronaSpecCountCbx.setSelected(this.exportFields.taxonomyKronaSpecCount);

		// Add to taxonomyFeaturesPnl
		taxonomyFeaturesPanel.add(this.taxonomyUnclassifiedCbx, CC.xy(2, 2));
		taxonomyFeaturesPanel.add(this.taxonomySuperKingdomCbx, CC.xy(2, 4));
		taxonomyFeaturesPanel.add(this.taxonomyKingdomCbx, CC.xy(2, 6));
		taxonomyFeaturesPanel.add(this.taxonomyPhylumCbx, CC.xy(2, 8));
		taxonomyFeaturesPanel.add(this.taxonomyClassCbx, CC.xy(2, 10));
		taxonomyFeaturesPanel.add(this.taxonomyOrderCbx, CC.xy(2, 12));
		taxonomyFeaturesPanel.add(this.taxonomyFamilyCbx, CC.xy(2, 14));
		taxonomyFeaturesPanel.add(this.taxonomyGenusCbx, CC.xy(4, 2));
		taxonomyFeaturesPanel.add(this.taxonomySpeciesCbx, CC.xy(4, 4));
		taxonomyFeaturesPanel.add(this.taxonomySubspeciesCbx, CC.xy(4, 6));
		taxonomyFeaturesPanel.add(this.taxonomySpecificPeptidesCbx, CC.xy(4, 8));
		taxonomyFeaturesPanel.add(this.taxonomySpecificSpecCountCbx, CC.xy(4, 10));
		taxonomyFeaturesPanel.add(this.taxonomyKronaSpecCountCbx, CC.xy(4, 12));

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
				ExportDialog.this.chooseExporter("Export Protein Taxonomy");
				// Store fields for taxonomy checkboxes
				ExportDialog.this.storeEntries();
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
				ExportDialog.this.storeEntries();
				// Close
				ExportDialog.this.close();
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
				ExportDialog.this.close();
			}
		});

		// Add to taxonomy button panel
		taxButtonPnl.add(taxExportBtn, CC.xy(2, 2));
		taxButtonPnl.add(taxCancelBtn, CC.xy(4, 2));
		taxButtonPnl.add(taxRefuseBtn, CC.xy(6, 2));
		// Add to psm panel
		taxonomyPnl.add(taxonomyFeaturesPanel, CC.xy(2, 2));
		taxonomyPnl.add(taxButtonPnl, CC.xy(2, 4));

		this.tabPane.addTab("Meta-Proteins", metaProteinPnl);
		this.tabPane.addTab("Proteins", proteinPanel);
		this.tabPane.addTab("Peptides", peptidePnl);
		this.tabPane.addTab("PSMs", psmPanel);
		this.tabPane.addTab("Identified Spectra", spectrumPanel);
		this.tabPane.addTab("Krona Data", metaProteinTaxonomyPnl);
		this.tabPane.addTab("Protein Taxonomy", taxonomyPnl);
		this.tabPane.addTab("Chord Data", chordPanel);

		Container cp = getContentPane();
		cp.setLayout(new FormLayout("5dlu, r:p, 5dlu", "5dlu, f:p:g, 5dlu"));

		cp.add(this.tabPane, CC.xy(2, 2));

	}

	/**
	 * This method shows the dialog.
	 */
	private void showDialog() {
		// Configure size and position
		pack();
		setResizable(false);
		ScreenConfig.centerInScreen(this);

		// Show dialog
		setVisible(true);
	}

	/**
	 * Close method for the dialog.
	 */
	private void close() {
		// Save the export headers
		this.dispose();
	}

	/**
	 * Helper method to select the chosen exporter.
	 */
	private void chooseExporter(String exportTyp) {
		JFileChooser chooser = new ConfirmFileChooser(this.owner.getLastSelectedFolder());
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

			setCursor(new Cursor(Cursor.WAIT_CURSOR));

			try {
				if (selectedFile.exists()) {
					selectedFile.delete();
				}
				selectedFile.createNewFile();
				this.collectHeaderSet();

				if (this.tabPane.getSelectedIndex() == 0) {
					resultType = "Meta-Proteins";
					// TODO TAKE CARE ABOUT THIS
					ResultExporter.exportMetaProteins(selectedFile.getPath(), this.client.getDatabaseSearchResult(),
							this.exportHeaders);
				} else if (this.tabPane.getSelectedIndex() == 1) {
					resultType = "Proteins";
					ResultExporter.exportProteins(selectedFile.getPath(), this.client.getDatabaseSearchResult(),
							this.exportHeaders);
				} else if (this.tabPane.getSelectedIndex() == 2) {
					resultType = "Peptides";
					ResultExporter.exportPeptides(selectedFile.getPath(), this.client.getDatabaseSearchResult(),
							this.exportHeaders);
				} else if (this.tabPane.getSelectedIndex() == 3) {
					resultType = "PSMs";
					ResultExporter.exportPSMs(selectedFile.getPath(), this.client.getDatabaseSearchResult(),
							this.exportHeaders);
				} else if (this.tabPane.getSelectedIndex() == 4) {
					resultType = "Identified Spectra";
					ResultExporter.exportIdentifiedSpectra(selectedFile.getPath(),
							this.client.getDatabaseSearchResult(), this.exportHeaders);
				} else if (this.tabPane.getSelectedIndex() == 5) {
					resultType = "Meta-Protein Taxonomy";
					ResultExporter.exportMetaProteinTaxonomy(selectedFile.getPath(),
							this.client.getDatabaseSearchResult(), this.exportHeaders);
				} else if (this.tabPane.getSelectedIndex() == 6) {
					resultType = "Protein Taxonomy";
					ResultExporter.exportProteinTaxonomy(selectedFile.getPath(), this.client.getDatabaseSearchResult(),
							this.exportHeaders);
				} else if (this.tabPane.getSelectedIndex() == 7) {
					resultType = "Export Chord Data";
					ResultExporter.exportChordData(selectedFile.getPath(), this.client.getDatabaseSearchResult(),
							this.chordTaxonomyLevel.getSelectedItem().toString(),
							this.chordKeywordLevel.getSelectedItem().toString(), 
							this.chordSlider.getValue(), 
							this.exportHeaders);
				}

				this.owner.setLastSelectedFolder(selectedFile.getPath());

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "An error occured when exporting the results.",
						"Error Results Exporting", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
			// Close the dialog after export has been finished.
			this.close();
			JOptionPane.showMessageDialog(this,
					"Successfully exported " + resultType + " to the file " + selectedFile.getName() + ".",
					"Export successful!", JOptionPane.INFORMATION_MESSAGE);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Collect the headers for the export.
	 */
	private void collectHeaderSet() {
		// Initialize set on demand.
		if (this.exportHeaders == null) {
			this.exportHeaders = new ArrayList<ExportHeader>();
		}

		// Meta-Proteins
		if (this.metaproteinNumberCbx.isSelected())
			this.exportHeaders.add(new ExportHeader(1, this.metaproteinNumberCbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinAccessionsCbx.isSelected())
			exportHeaders.add(new ExportHeader(2, metaproteinAccessionsCbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinDescriptionCbx.isSelected())
			exportHeaders.add(new ExportHeader(3, metaproteinDescriptionCbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinTaxonomyCbx.isSelected()) {
			exportHeaders.add(new ExportHeader(4, metaproteinTaxonomyCbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(5, UniProtUtilities.TaxonomyRank.SUPERKINGDOM.toString(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(6, UniProtUtilities.TaxonomyRank.KINGDOM.toString(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(7, UniProtUtilities.TaxonomyRank.PHYLUM.toString(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(8, UniProtUtilities.TaxonomyRank.CLASS.toString(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(9, UniProtUtilities.TaxonomyRank.ORDER.toString(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(10, UniProtUtilities.TaxonomyRank.FAMILY.toString(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(11, UniProtUtilities.TaxonomyRank.GENUS.toString(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
			exportHeaders.add(new ExportHeader(12, UniProtUtilities.TaxonomyRank.SPECIES.toString(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
		}

		if (metaproteinUniRef100Cbx.isSelected())
			exportHeaders.add(new ExportHeader(13, metaproteinUniRef100Cbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinUniRef90Cbx.isSelected())
			exportHeaders.add(new ExportHeader(14, metaproteinUniRef90Cbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinUniRef50Cbx.isSelected())
			exportHeaders.add(new ExportHeader(15, metaproteinUniRef50Cbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinKOCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(16, metaproteinKOCbx.getText(), ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinECCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(17, metaproteinECCbx.getText(), ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinPepCountCbx.isSelected())
			exportHeaders.add(new ExportHeader(18, metaproteinPepCountCbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinSpecCountCbx.isSelected())
			exportHeaders.add(new ExportHeader(19, metaproteinSpecCountCbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinsProteinsCbx.isSelected())
			exportHeaders.add(new ExportHeader(20, metaproteinsProteinsCbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));
		if (metaproteinsPeptidesCbx.isSelected())
			exportHeaders.add(new ExportHeader(21, metaproteinsPeptidesCbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINS));

		// Meta-protein taxonomy
		if (metaProteinTaxonomySpecificPeptidesCbx.isSelected())
			exportHeaders.add(new ExportHeader(1, metaProteinTaxonomySpecificPeptidesCbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINTAXONOMY));
		if (metaProteinTaxonomyKronaSpecCountCbx.isSelected())
			exportHeaders.add(new ExportHeader(2, metaProteinTaxonomyKronaSpecCountCbx.getText(),
					ResultExporter.ExportHeaderType.METAPROTEINTAXONOMY));

		// Protein Taxonomy
		if (taxonomyUnclassifiedCbx.isSelected())
			exportHeaders.add(new ExportHeader(1, taxonomyUnclassifiedCbx.getText(),
					ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomySuperKingdomCbx.isSelected())
			exportHeaders.add(new ExportHeader(2, taxonomySuperKingdomCbx.getText(),
					ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomyKingdomCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(3, taxonomyKingdomCbx.getText(), ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomyPhylumCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(4, taxonomyPhylumCbx.getText(), ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomyClassCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(5, taxonomyClassCbx.getText(), ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomyOrderCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(6, taxonomyOrderCbx.getText(), ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomyFamilyCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(7, taxonomyFamilyCbx.getText(), ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomyGenusCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(8, taxonomyGenusCbx.getText(), ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomySpeciesCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(9, taxonomySpeciesCbx.getText(), ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomySubspeciesCbx.isSelected())
			exportHeaders.add(new ExportHeader(10, taxonomySubspeciesCbx.getText(),
					ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomySpecificPeptidesCbx.isSelected())
			exportHeaders.add(new ExportHeader(11, taxonomySpecificPeptidesCbx.getText(),
					ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomySpecificSpecCountCbx.isSelected())
			exportHeaders.add(new ExportHeader(12, taxonomySpecificSpecCountCbx.getText(),
					ResultExporter.ExportHeaderType.PROTEINTAXONOMY));
		if (taxonomyKronaSpecCountCbx.isSelected())
			exportHeaders.add(new ExportHeader(13, taxonomyKronaSpecCountCbx.getText(),
					ResultExporter.ExportHeaderType.PROTEINTAXONOMY));

		// Proteins
		if (proteinNumberCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(1, proteinNumberCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinAccessionCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(2, proteinAccessionCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinDescriptionCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(3, proteinDescriptionCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinSpeciesCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(4, proteinSpeciesCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinSeqCoverageCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(5, proteinSeqCoverageCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinPepCountCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(6, proteinPepCountCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinNSAFCbx.isSelected())
			exportHeaders.add(new ExportHeader(7, proteinNSAFCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinEmPAICbx.isSelected())
			exportHeaders.add(new ExportHeader(8, proteinEmPAICbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinSpecCountCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(9, proteinSpecCountCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinPiCbx.isSelected())
			exportHeaders.add(new ExportHeader(10, proteinPiCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinMolWeightCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(11, proteinMolWeightCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinSequenceCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(12, proteinSequenceCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));
		if (proteinPeptidesCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(13, proteinPeptidesCbx.getText(), ResultExporter.ExportHeaderType.PROTEINS));

		// Peptides
		if (peptideNumberCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(1, peptideNumberCbx.getText(), ResultExporter.ExportHeaderType.PEPTIDES));
		if (peptideProteinAccessionsCbx.isSelected())
			exportHeaders.add(new ExportHeader(2, peptideProteinAccessionsCbx.getText(),
					ResultExporter.ExportHeaderType.PEPTIDES));
		if (peptideSequenceCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(3, peptideSequenceCbx.getText(), ResultExporter.ExportHeaderType.PEPTIDES));
		if (uniquePeptidesOnlyCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(4, uniquePeptidesOnlyCbx.getText(), ResultExporter.ExportHeaderType.PEPTIDES));
		if (sharedPeptidesOnlyCbx.isSelected())
			exportHeaders.add(
					new ExportHeader(5, sharedPeptidesOnlyCbx.getText(), ResultExporter.ExportHeaderType.PEPTIDES));
		if (peptideProtCountCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(6, peptideProtCountCbx.getText(), ResultExporter.ExportHeaderType.PEPTIDES));
		if (peptideSpecCountCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(7, peptideSpecCountCbx.getText(), ResultExporter.ExportHeaderType.PEPTIDES));
		if (peptideTaxGroup.isSelected())
			exportHeaders.add(new ExportHeader(8, peptideTaxGroup.getText(), ResultExporter.ExportHeaderType.PEPTIDES));
		if (peptideTaxRank.isSelected())
			exportHeaders.add(new ExportHeader(9, peptideTaxRank.getText(), ResultExporter.ExportHeaderType.PEPTIDES));
		if (peptideTaxIdCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(10, peptideTaxIdCbx.getText(), ResultExporter.ExportHeaderType.PEPTIDES));

		// PSMs
		if (psmNumberCbx.isSelected())
			exportHeaders.add(new ExportHeader(1, psmNumberCbx.getText(), ResultExporter.ExportHeaderType.PSMS));
		if (psmProteinAccessionCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(2, psmProteinAccessionCbx.getText(), ResultExporter.ExportHeaderType.PSMS));
		if (psmPeptideSequenceCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(3, psmPeptideSequenceCbx.getText(), ResultExporter.ExportHeaderType.PSMS));
		if (psmSpectrumTitleCbx.isSelected())
			exportHeaders.add(new ExportHeader(4, psmSpectrumTitleCbx.getText(), ResultExporter.ExportHeaderType.PSMS));
		if (psmChargeCbx.isSelected())
			exportHeaders.add(new ExportHeader(5, psmChargeCbx.getText(), ResultExporter.ExportHeaderType.PSMS));
		if (psmSearchEngineCbx.isSelected())
			exportHeaders.add(new ExportHeader(6, psmSearchEngineCbx.getText(), ResultExporter.ExportHeaderType.PSMS));
		if (psmQValueCbx.isSelected())
			exportHeaders.add(new ExportHeader(7, psmQValueCbx.getText(), ResultExporter.ExportHeaderType.PSMS));
		if (psmScoreCbx.isSelected())
			exportHeaders.add(new ExportHeader(8, psmScoreCbx.getText(), ResultExporter.ExportHeaderType.PSMS));

		// Identified Spectra
		if (spectrumNumberCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(1, spectrumNumberCbx.getText(), ResultExporter.ExportHeaderType.SPECTRA));
		if (spectrumIDCbx.isSelected())
			exportHeaders.add(new ExportHeader(2, spectrumIDCbx.getText(), ResultExporter.ExportHeaderType.SPECTRA));
		if (spectrumTitleCbx.isSelected())
			exportHeaders.add(new ExportHeader(3, spectrumTitleCbx.getText(), ResultExporter.ExportHeaderType.SPECTRA));
		if (spectrumPeptidesCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(4, spectrumPeptidesCbx.getText(), ResultExporter.ExportHeaderType.SPECTRA));
		if (spectrumAccessionsCbx.isSelected())
			exportHeaders
					.add(new ExportHeader(5, spectrumAccessionsCbx.getText(), ResultExporter.ExportHeaderType.SPECTRA));
	}

	/**
	 * This method stores the protein entries into the export fields.
	 */
	private void storeEntries() {
		// Protein entries
		this.exportFields.proteinNumber = this.proteinNumberCbx.isSelected();
		this.exportFields.proteinAccession = this.proteinAccessionCbx.isSelected();
		this.exportFields.proteinDescription = this.proteinDescriptionCbx.isSelected();
		this.exportFields.proteinTaxonomy = this.proteinSpeciesCbx.isSelected();
		this.exportFields.proteinSeqCoverage = this.proteinSeqCoverageCbx.isSelected();
		this.exportFields.proteinMolWeight = this.proteinMolWeightCbx.isSelected();
		this.exportFields.proteinPi = this.proteinPiCbx.isSelected();
		this.exportFields.proteinPepCount = this.proteinPepCountCbx.isSelected();
		this.exportFields.proteinSpecCount = this.proteinSpecCountCbx.isSelected();
		this.exportFields.proteinEmPAI = this.proteinEmPAICbx.isSelected();
		this.exportFields.proteinNSAF = this.proteinNSAFCbx.isSelected();
		this.exportFields.proteinSequence = this.proteinSequenceCbx.isSelected();
		this.exportFields.proteinPeptides = this.proteinPeptidesCbx.isSelected();

		// Peptide entries
		this.exportFields.peptideNumber = this.peptideNumberCbx.isSelected();
		this.exportFields.peptideProteinAccessions = this.peptideProteinAccessionsCbx.isSelected();
		this.exportFields.peptideSequence = this.peptideSequenceCbx.isSelected();
		this.exportFields.peptideTaxGroup = this.peptideTaxGroup.isSelected();
		this.exportFields.uniquePeptidesOnly = this.uniquePeptidesOnlyCbx.isSelected();
		this.exportFields.sharedPeptidesOnly = this.sharedPeptidesOnlyCbx.isSelected();
		this.exportFields.peptideProtCount = this.peptideProtCountCbx.isSelected();
		this.exportFields.peptideSpecCount = this.peptideSpecCountCbx.isSelected();
		this.exportFields.peptideTaxRank = this.peptideTaxRank.isSelected();
		this.exportFields.peptideTaxId = this.peptideTaxIdCbx.isSelected();

		// PSM entries
		this.exportFields.psmNumber = this.psmNumberCbx.isSelected();
		this.exportFields.psmProteinAccession = this.psmProteinAccessionCbx.isSelected();
		this.exportFields.psmPeptideSequence = this.psmPeptideSequenceCbx.isSelected();
		this.exportFields.psmSpectrumTitle = this.psmSpectrumTitleCbx.isSelected();
		this.exportFields.psmCharge = this.psmChargeCbx.isSelected();
		this.exportFields.psmSearchEngine = this.psmSearchEngineCbx.isSelected();
		this.exportFields.psmQValue = this.psmQValueCbx.isSelected();
		this.exportFields.psmScore = this.psmScoreCbx.isSelected();

		// Meta-protein entries
		this.exportFields.metaproteinNumber = this.metaproteinNumberCbx.isSelected();
		this.exportFields.metaproteinAccessions = this.metaproteinAccessionsCbx.isSelected();
		this.exportFields.metaproteinDescription = this.metaproteinDescriptionCbx.isSelected();
		this.exportFields.metaproteinTaxonomy = this.metaproteinTaxonomyCbx.isSelected();
		this.exportFields.metaprotUniRef100 = this.metaproteinUniRef100Cbx.isSelected();
		this.exportFields.metaprotUniRef90 = this.metaproteinUniRef90Cbx.isSelected();
		this.exportFields.metaprotUniRef50 = this.metaproteinUniRef50Cbx.isSelected();
		this.exportFields.metaprotKO = this.metaproteinKOCbx.isSelected();
		this.exportFields.metaprotEC = this.metaproteinECCbx.isSelected();
		this.exportFields.metaproteinSeqCoverage = this.metaproteinSeqCoverageCbx.isSelected();
		this.exportFields.metaproteinPepCount = this.metaproteinPepCountCbx.isSelected();
		this.exportFields.metaproteinSpecCount = this.metaproteinSpecCountCbx.isSelected();
		this.exportFields.metaproteinPeptides = this.metaproteinsPeptidesCbx.isSelected();

		// Protein taxonomy entries
		this.exportFields.taxonomyUnclassified = this.taxonomyUnclassifiedCbx.isSelected();
		this.exportFields.taxonomySuperKingdom = this.taxonomySuperKingdomCbx.isSelected();
		this.exportFields.taxonomyKingdom = this.taxonomyKingdomCbx.isSelected();
		this.exportFields.taxonomyPhylum = this.taxonomyPhylumCbx.isSelected();
		this.exportFields.taxonomyClass = this.taxonomyClassCbx.isSelected();
		this.exportFields.taxonomyOrder = this.taxonomyOrderCbx.isSelected();
		this.exportFields.taxonomyFamily = this.taxonomyFamilyCbx.isSelected();
		this.exportFields.taxonomyGenus = this.taxonomyGenusCbx.isSelected();
		this.exportFields.taxonomySpecies = this.taxonomySpeciesCbx.isSelected();
		this.exportFields.taxonomySubspecies = this.taxonomySubspeciesCbx.isSelected();
		this.exportFields.taxonomySpecificPeptides = this.taxonomySpecificPeptidesCbx.isSelected();
		this.exportFields.taxonomySpecificSpecCount = this.taxonomySpecificSpecCountCbx.isSelected();

		// Meta-protein taxonomy entries
		this.exportFields.metaproteinTaxonomySpecificPeptides = this.metaProteinTaxonomySpecificPeptidesCbx
				.isSelected();
		this.exportFields.metaproteinTaxonomyKronaSpecCount = this.metaProteinTaxonomyKronaSpecCountCbx.isSelected();
	}
}
