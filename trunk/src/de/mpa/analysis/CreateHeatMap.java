package de.mpa.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jfree.data.xy.MatrixSeries;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.ko.KO;
import de.mpa.analysis.UniprotAccessor.KeywordOntology;
import de.mpa.analysis.UniprotAccessor.TaxonomyRank;
import de.mpa.client.Constants;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.taxonomy.NcbiTaxonomy;

/**
 * Create the heatmap from the dbSearchResultObject
 * @author R. Heyer
 */
public class CreateHeatMap {

	/**
	 * The dbsearchResultObject.
	 */
	private DbSearchResult dbSearchresultObject;

	/**
	 * The x-labels of the heatmap.
	 */
	private String[] xLabels;

	/**
	 * The y-labels of the heatmap.
	 */
	private String[] yLabels;

	/**
	 * The matrix for the heatmap.
	 */
	private MatrixSeries matrix;

	/**
	 * Type of the x-Axis
	 */
	private String xAxis;

	/**
	 * Type of the y-Axis
	 */
	private String yAxis;

	/**
	 * Type of the z-Axis
	 */
	private String zAxis;

	/**
	 * Map for xAxis objects and entries for the entries in the y axis.
	 */
	Map<Object, ArrayList<String>> xAxisMap;

	/**
	 * Set for yAxis objects.
	 */
	Set<String> yAxisSet;

	/**
	 * ProteinHitsList for generating all y-entries
	 */
	ProteinHitList protHits;


	/**
	 * Standard constructor to create a heatmap.
	 * @param dbSearchresultObject
	 */
	public CreateHeatMap(DbSearchResult dbSearchresultObject, String xAxis, String yAxis, String zAxis) {
		this.dbSearchresultObject 	= dbSearchresultObject;
		this.xAxis 					= xAxis;
		this.yAxis 					= yAxis;
		this.zAxis 					= zAxis;

		// Create the heatmap
		create();
	}

	/**
	 * Constructor to create a default heatmap as placeholder.
	 */
	public CreateHeatMap() {
		createDefault();
	}

	/**
	 * Create heatmap from dbSearchResultObject.
	 */
	private void create() {

		// Create xAxisSet
		createAxisSets();

		// Collect Xlabels
		xLabels = collectXLabels();

		// Create y-labels
		yLabels = collectYLabels();

		// Create the matriy
		matrix = createMatrix();
	}

	/**
	 * Method to create x-Axis Set
	 * @return Set<Object> x-Axis
	 */
	private void createAxisSets() {
		// List for values for the x
		xAxisMap = new TreeMap<Object, ArrayList<String>>();
		// Set for the y entries
		yAxisSet = new TreeSet<String>();
	
		// Create map for the x-axis (X-Object and values for y-axis) and save y-values in set for y-axis 
		if (xAxis.equals("PEPTIDE")) {
			List<ProteinHit> protHits = dbSearchresultObject.getProteinHitList();
			for (ProteinHit proteinHit : protHits) {
				List<PeptideHit> peptideHitList = proteinHit.getPeptideHitList();
				for (PeptideHit pepHit : peptideHitList) {
					ArrayList<String> addYValues = addYValues(proteinHit);
					if (xAxisMap.get(pepHit) != null) {
						addYValues.addAll(xAxisMap.get(pepHit));
					}
					xAxisMap.put(pepHit,addYValues);	
				}
			}
		}else if (xAxis.equals("PROTEINE")) {
			List<ProteinHit> proteinHitList = dbSearchresultObject.getProteinHitList();
			for (ProteinHit proteinHit : proteinHitList) {
				ArrayList<String> addYValues = addYValues(proteinHit);
				xAxisMap.put(proteinHit,addYValues );	
			}
		}else if (xAxis.equals("METAPROTEINE")) {
			ProteinHitList metaProteins = dbSearchresultObject.getMetaProteins();
			for (ProteinHit metaProtHit : metaProteins) {
				ArrayList<String> addYValues = addYValues(metaProtHit);
				xAxisMap.put(metaProtHit,addYValues );	
			}
		}
	}

	/**
	 * Method to create y-axis set.
	 * @return 
	 * @return y-axis set.
	 */
	private  ArrayList<String> addYValues(ProteinHit protHit) {
		// Collect nonredundant list of entries for y-Axis
		ArrayList<String> yEntry = new ArrayList<String>();
		if (yAxis.equals("TAX_SUPERKINGDOM")) {
			String taxNameByRank = NcbiTaxonomy.getTaxNameByRank(protHit.getTaxonomyNode(), TaxonomyRank.SUPERKINGDOM);
			yEntry.add(taxNameByRank);
			yAxisSet.add(taxNameByRank);
		}else if (yAxis.equals("TAX_KINGDOM")) {
			String taxNameByRank = NcbiTaxonomy.getTaxNameByRank(protHit.getTaxonomyNode(), TaxonomyRank.KINGDOM);
			yEntry.add(taxNameByRank);
			yAxisSet.add(taxNameByRank);
		}else if (yAxis.equals("TAX_PHYLUM")) {
			String taxNameByRank = NcbiTaxonomy.getTaxNameByRank(protHit.getTaxonomyNode(), TaxonomyRank.PHYLUM);
			yEntry.add(taxNameByRank);
			yAxisSet.add(taxNameByRank);
		}else if (yAxis.equals("TAX_CLASS")) {
			String taxNameByRank = NcbiTaxonomy.getTaxNameByRank(protHit.getTaxonomyNode(), TaxonomyRank.CLASS);
			yEntry.add(taxNameByRank);
			yAxisSet.add(taxNameByRank);
		}else if (yAxis.equals("TAX_ORDER")) {
			String taxNameByRank = NcbiTaxonomy.getTaxNameByRank(protHit.getTaxonomyNode(), TaxonomyRank.ORDER);
			yEntry.add(taxNameByRank);
			yAxisSet.add(taxNameByRank);
		}else if (yAxis.equals("TAX_FAMILY")) {
			String taxNameByRank = NcbiTaxonomy.getTaxNameByRank(protHit.getTaxonomyNode(), TaxonomyRank.FAMILY);
			yEntry.add(taxNameByRank);
			yAxisSet.add(taxNameByRank);
		}else if (yAxis.equals("TAX_GENUS")) {
			String taxNameByRank = NcbiTaxonomy.getTaxNameByRank(protHit.getTaxonomyNode(), TaxonomyRank.GENUS);
			yEntry.add(taxNameByRank);
			yAxisSet.add(taxNameByRank);
		}else if (yAxis.equals("TAX_SPECIES")) {
			String taxNameByRank = NcbiTaxonomy.getTaxNameByRank(protHit.getTaxonomyNode(), TaxonomyRank.SPECIES);
			yEntry.add(taxNameByRank);
			yAxisSet.add(taxNameByRank);
		}else if (yAxis.equals("EC_NUMBER")) {
			List<String> ecNumbers = protHit.getUniprotEntry().getProteinDescription().getEcNumbers();
			for (String ecNum : ecNumbers) {
				yEntry.add(ecNum);
				yAxisSet.add(ecNum);
			}
		}else if (yAxis.equals("BIOLOGICAL_PROCESS")) {
			// Get the ontology map
			Map<String, KeywordOntology> ontologyMap = UniprotAccessor.ONTOLOGY_MAP;
			// Check all keywords for biological process
			List<Keyword> keywords = protHit.getUniprotEntry().getKeywords();
			if (keywords != null) {
				// Check all keywords
				for (Keyword kw : keywords) {
					String keyword = kw.getValue();
					if (ontologyMap.containsKey(keyword) && ontologyMap.get(keyword).equals(KeywordOntology.BIOLOGICAL_PROCESS)) {
						yEntry.add(keyword);
						yAxisSet.add(keyword);
					}
				}
			}
		}else if (yAxis.equals("CELLULAR_COMPONENT")) {
			// Get the ontology map
			Map<String, KeywordOntology> ontologyMap = UniprotAccessor.ONTOLOGY_MAP;
			// Check all keywords for biological process
			List<Keyword> keywords = protHit.getUniprotEntry().getKeywords();
			if (keywords != null) {
				// Check all keywords
				for (Keyword kw : keywords) {
					String keyword = kw.getValue();
					if (ontologyMap.containsKey(keyword) && ontologyMap.get(keyword).equals(KeywordOntology.CELLULAR_COMPONENT)) {
						yEntry.add(keyword);
						yAxisSet.add(keyword);
					}
				}
			}
		}else if (yAxis.equals("MOLECULAR_FUNCTION")) {
			// Get the ontology map
			Map<String, KeywordOntology> ontologyMap = UniprotAccessor.ONTOLOGY_MAP;
			// Check all keywords for biological process
			List<Keyword> keywords = protHit.getUniprotEntry().getKeywords();
			if (keywords != null) {
				// Check all keywords
				for (Keyword kw : keywords) {
					String keyword = kw.getValue();
					if (ontologyMap.containsKey(keyword) && ontologyMap.get(keyword).equals(KeywordOntology.MOLECULAR_FUNCTION)) {
						yEntry.add(keyword);
						yAxisSet.add(keyword);
					}
				}
			}
		}else if (yAxis.equals("PATHWAY")) {
			// Add pathways by KO
			List<DatabaseCrossReference> dcr = protHit.getUniprotEntry().getDatabaseCrossReferences(DatabaseType.KO);
			for (int i = 0; i < dcr.size(); i++) {
				// Get Ko number to match it to the pathways
				DatabaseCrossReference ref = dcr.get(i);
				String ko = ((KO) ref).getKOIdentifier().getValue();
				List<Short> pathwaysByKO = KeggAccessor.getInstance().getPathwaysByKO(ko.substring(1));
				if (pathwaysByKO != null) {
					for (Short pathway : pathwaysByKO) {
						Object[] keggPathwayPath = Constants.getKEGGPathwayPath(pathway);
						if (keggPathwayPath != null && keggPathwayPath.length > 0) {
							yEntry.add(keggPathwayPath[3].toString());
							yAxisSet.add(keggPathwayPath[3].toString());
						}
					}
				}
			}
			// Add pathways by EC
			List<String> ecNumbers = protHit.getUniprotEntry().getProteinDescription().getEcNumbers();
			for (String ecNumb : ecNumbers) {
				List<Short> pathwaysByEC = KeggAccessor.getInstance().getPathwaysByEC(ecNumb);
				if (pathwaysByEC != null) {
					for (Short pathway : pathwaysByEC) {
						Object[] keggPathwayPath = Constants.getKEGGPathwayPath(pathway);
						if (keggPathwayPath != null && keggPathwayPath.length > 0) {
							yEntry.add(keggPathwayPath[3].toString());
							yAxisSet.add(keggPathwayPath[3].toString());
						}
					}
				}
			}
		}else if(yAxis.equals("PEPTIDE")) {
				List<PeptideHit> pepHitList = protHit.getPeptideHitList();
				for (PeptideHit pepHit : pepHitList) {
					yEntry.add(pepHit.getSequence());
					yAxisSet.add(pepHit.getSequence());
				}
			}else if(yAxis.equals("PROTEIN")) {
			Set<ProteinHit> metaProtSet = ((MetaProteinHit)protHit).getProteinSet();
				for (ProteinHit metaProts : metaProtSet) {
					yEntry.add(metaProts.getAccession());
					yAxisSet.add(metaProts.getAccession());
				}
			}
		return yEntry;
	}

	/**
	 * Methode to collect ylabels.
	 * @return y-labels.
	 */
	private String[] collectYLabels() {
		yLabels = new String[yAxisSet.size()];
		int y = 0;
		for (String string : yAxisSet) {
			yLabels[y++] = string;
		}
		return yLabels;
	}


	/**
	 * Method to collet xLabels.
	 * @return Xlabels
	 */
	private String[] collectXLabels() {
		// Collect x-labels
		xLabels = new String[xAxisMap.size()];
		int xIndex = 0;
		for (Object obj : xAxisMap.keySet()) {
			if (xAxis.equals("PEPTIDE")) {
				xLabels[xIndex++] = ((PeptideHit) obj).getSequence();
			}else if (xAxis.equals("PROTEINE")) {
				xLabels[xIndex++] = ((ProteinHit) obj).getAccession();
			}else if (xAxis.equals("METAPROTEINE")) {
				xLabels[xIndex++] = ((MetaProteinHit) obj).getAccession();
			}
		}
		return xLabels;
	}

	/**
	 * Create the matrix for the heatmap -->(name, rows( x-axis), columns ( y-axis)) 
	 * @return matrix 
	 */
	private MatrixSeries createMatrix() {
		// Create matrix
		matrix = new MatrixSeries("matrix", yAxisSet.size(), xAxisMap.size()); // MatrixSeries(name, height, width)
		int width = 0;
		// Go through matrix and save entries
		for ( Object  key :  xAxisMap.keySet()) { // width
			ArrayList<String> arrayList = xAxisMap.get(key); // height
			int height = 0;
			for (String yEntry : yAxisSet) {
				if (arrayList.contains(yEntry)) {
					if (zAxis.equals("No. METAPROTEIN")) {
						matrix.update(height, width,1);	
					}else if (zAxis.equals("No. PROTEIN")) {
						matrix.update( height, width,1);	
					}else if (zAxis.equals("No. PEPTIDE")) {
						if (xAxis.equals("METAPROTEINE") ||xAxis.equals("PROTEINE")) {
							matrix.update( height, width,((ProteinHit)key).getPeptideCount());	
						}else if (xAxis.equals("PEPTIDE")) {
							matrix.update( height, width, 1);	
						}	
					}else if (zAxis.equals("No. SPECTRA")) {
						if (xAxis.equals("METAPROTEINE") ||xAxis.equals("PROTEINE")) {
							matrix.update( height, width,((ProteinHit)key).getSpectralCount());	
						}else if (xAxis.equals("PEPTIDE")) {
							matrix.update( height, width, ((PeptideHit)key).getSpectralCount());	
						}	
					}
				}else{
					matrix.update( height,width, 0);
				}
				height++;
			}
			width++;
		}
		return matrix;
	}

	/**
	 * Create default heatmap as placeholder
	 */
	private void createDefault() {
		// Create heatmap
		int height = 25, width = 27;
		matrix = new MatrixSeries("1", height, width);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				double mij = Math.sin((j + 0.5) / width * Math.PI) *
						Math.cos((i + 0.5) / height * Math.PI - Math.PI / 2.0);
				matrix.update(i, j, mij);
			}
		}

		xLabels = new String[width];
		for (int i = 0; i < xLabels.length; i++) {
			xLabels[i] = "" + (i + 1);
		}

		yLabels = new String[height];
		for (int i = 0; i < yLabels.length; i++) {
			yLabels[i] = "" + (char) (i + 'A');
		}
	}

	/**
	 * Gets the matrix of the heatmap.
	 * @return matrix.
	 */
	public MatrixSeries getSeries() {
		return matrix;
	}

	/**
	 * Gets the x-labels of the heatmap.
	 * @return xlabels.
	 */
	public String[] getxLabels() {
		return xLabels;
	}

	/**
	 * Gets the y-labels of the heatmap.
	 * @return ylabels.
	 */
	public String[] getyLabels() {
		return yLabels;
	}
}
