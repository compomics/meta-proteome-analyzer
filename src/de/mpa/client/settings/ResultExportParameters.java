package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;

/**
 * Parameter map holding result CSV export parameters.
 * 
 * @author A. Behne
 */
// TODO: this class is currently unused, implement or remove!
public class ResultExportParameters extends ParameterMap {

	@Override
	public void initDefaults() {
//		// meta-protein parameters
//		this.put("meta", new Parameter(
//				"Accession|Description|Taxonomy||"
//				+ "Peptide Count|Spectral Count||"
//				+ "Child Proteins|Child Peptides",
//				new Boolean[][] { { true, true, true }, { true, true }, { true, true } }, "Meta-Proteins",
//				"Include meta-protein accession.|Include meta-protein description.|Include meta-protein taxonomy.||"
//				+ "Include meta-protein peptide count.|Include meta-protein spectral count.||"
//				+ "Include meta-protein child proteins.|Include protein child peptides."));
//		JButton metaBtn = new JButton("Export Meta-Proteins", IconConstants.EXCEL_EXPORT_ICON);
//		metaBtn.setRolloverIcon(IconConstants.EXCEL_EXPORT_ROLLOVER_ICON);
//		metaBtn.setPressedIcon(IconConstants.EXCEL_EXPORT_PRESSED_ICON);
//		this.put("metaBtn", new Parameter(null, metaBtn, "Meta-Proteins",
//				"Export meta-proteins to CSV containing the selected fields."));
//		
//		// protein parameters
//		this.put("prot", new Parameter(
//				"Accession|Description|Taxonomy||"
//				+ "Seq. Coverage|Mol. Weight|Isoel. Point||"
//				+ "Peptide Count|Spectral Count||"
//				+ "NSAF|emPAI||"
//				+ "Sequence|Child Peptides",
//				new Boolean[][] { { true, true, true }, { true, true, true }, { true, false }, { true, false }, { true, false } }, "Proteins",
//				"Include protein accession.|Include protein description.|Include protein taxonomy.||"
//				+ "Include sequence coverage.|Include molecular weight|Include isoelectric point.||"
//				+ "Include peptide count.|Include spectral count.||"
//				+ "Include NSAF.|Include emPAI.||"
//				+ "Include protein sequence.|Include child peptides."));
//		JButton protBtn = new JButton("Export Proteins", IconConstants.EXCEL_EXPORT_ICON);
//		protBtn.setRolloverIcon(IconConstants.EXCEL_EXPORT_ROLLOVER_ICON);
//		protBtn.setPressedIcon(IconConstants.EXCEL_EXPORT_PRESSED_ICON);
//		this.put("protBtn", new Parameter(null, protBtn, "Proteins",
//				"Export proteins to CSV containing the selected fields."));
//		
//		// taxonomy parameters
//		this.put("tax", new Parameter(
//				"Superkingdom|Kingdom|Phylum||"
//				+ "Order|Class|Family||"
//				+ "Genus|Species|Unclassified||"
//				+ "Peptide Count| Spectral Count",
//				new Boolean[][] { { true, true, true}, { true, true, true }, { true, true, true }, { true, true } }, "Taxonomy",
//				"Include superkingdom taxonomy.|Include kingdom taxonomy.|Include phylum taxonomy.||"
//				+ "Include order taxonomy.|Include class taxonomy.|Include family taxonomy.||"
//				+ "Include genus taxonomy.|Include species taxonomy.|Include unclassified taxonomies.||"
//				+ "Include peptide count.|Include spectral count."));
//		JButton taxBtn = new JButton("Export Taxonomy", IconConstants.EXCEL_EXPORT_ICON);
//		taxBtn.setRolloverIcon(IconConstants.EXCEL_EXPORT_ROLLOVER_ICON);
//		taxBtn.setPressedIcon(IconConstants.EXCEL_EXPORT_PRESSED_ICON);
//		this.put("taxBtn", new Parameter(null, taxBtn, "Taxonomy",
//				"Export taxonomy to CSV containing the selected fields."));
	}

	@Override
	public File toFile(String path) throws IOException {
		return null;
	}

}
