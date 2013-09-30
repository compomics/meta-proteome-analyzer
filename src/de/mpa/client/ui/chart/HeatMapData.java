package de.mpa.client.ui.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jfree.data.xy.MatrixSeries;

import com.compomics.util.experiment.identification.matches.SpectrumMatch;

import de.mpa.analysis.UniprotAccessor;
import de.mpa.analysis.UniprotAccessor.KeywordOntology;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.chart.OntologyPieChart.OntologyChartType;
import de.mpa.client.ui.chart.TaxonomyPieChart.TaxonomyChartType;
import de.mpa.taxonomy.NcbiTaxonomy;

/**
 * Container class for heat map-related data.
 * @author R. Heyer, A. Behne
 */
public class HeatMapData {

	/**
	 * The x axis labels.
	 */
	private String[] xLabels;

	/**
	 * The y axis labels.
	 */
	private String[] yLabels;

	/**
	 * The heat map value matrix.
	 */
	private MatrixSeries matrix;

	/**
	 * Map for xAxis objects and entries for the entries in the y axis.
	 */
	private Map<Object, List<String>> xAxisMap;

	/**
	 * Set for yAxis objects.
	 */
//	private Set<String> yAxisSet;

	/**
	 * The maximum value inside the value matrix.
	 */
	private double max;

	/**
	 * Creates a heat map data container from the specified result object and axis identifiers
	 * @param result the search result object
	 * @param xAxis the x axis identifier
	 * @param yAxis the y axis identifier
	 * @param zAxis the z axis identifier
	 */
	public HeatMapData(DbSearchResult result, Object xAxis, Object yAxis, Object zAxis) {
		
		// Create set of x axis items
		this.createAxisSets(xAxis, yAxis, result);

		// Create the value matrix
		this.createMatrix(xAxis, zAxis);
	}

	/**
	 * Constructor to create a default placeholder heat map data.
	 */
	public HeatMapData() {
		this.createDefault();
	}

	/**
	 * Create default placeholder heat map data.
	 */
	protected void createDefault() {
		// Generate value matrix
		int height = 25, width = 27;
		matrix = new MatrixSeries("matrix", height, width);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				double mij = Math.sin((j + 0.5) / width * Math.PI) *
						Math.cos((i + 0.5) / height * Math.PI - Math.PI / 2.0);
				matrix.update(i, j, Math.round(mij * 100.0));
			}
		}
		// Generate x axis labels
		xLabels = new String[width];
		for (int i = 0; i < xLabels.length; i++) {
			xLabels[i] = "" + (i + 1);
		}
		// Generate y axis labels
		yLabels = new String[height];
		for (int i = 0; i < yLabels.length; i++) {
			yLabels[i] = "" + (char) (i + 'A');
		}
	}

	/**
	 * Creates x-to-y item mapping.
	 */
	private void createAxisSets(Object xAxis, Object yAxis, DbSearchResult result) {
		// List for values for the x
		xAxisMap = new TreeMap<Object, List<String>>();
	
		// Create map for the x-axis (X-Object and values for y-axis) and save y-values in set for y-axis
		if (xAxis instanceof TaxonomyChartType) {
			// TODO implement this
			System.err.println("ERROR: not yet implemented!");
		} else if (xAxis instanceof OntologyChartType) {
			// TODO implement this
			System.err.println("ERROR: not yet implemented!");
		} else if (xAxis instanceof HierarchyLevel) {
			switch ((HierarchyLevel) xAxis) {
			case META_PROTEIN_LEVEL:
				for (ProteinHit metaProtHit : result.getMetaProteins()) {
					xAxisMap.put(metaProtHit, this.addYValues(yAxis, metaProtHit));
				}
				break;
			case PROTEIN_LEVEL:
				for (ProteinHit proteinHit : result.getProteinHitList()) {
					xAxisMap.put(proteinHit, this.addYValues(yAxis, proteinHit));
				}
				break;
			case PEPTIDE_LEVEL:
				for (ProteinHit proteinHit : result.getProteinHitList()) {
					for (PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
						ArrayList<String> yValues = this.addYValues(yAxis, proteinHit);
						List<String> oldValues = xAxisMap.get(peptideHit);
						if (oldValues != null) {
							yValues.addAll(oldValues);
						}
						xAxisMap.put(peptideHit, yValues);
					}
				}
				break;
			case SPECTRUM_LEVEL:
				// TODO implement this
				System.err.println("ERROR: not yet implemented!");
				break;
			default:
				System.err.println("ERROR: unknown hierarchy level specified!");
				break;
			}
		} else {
			System.err.println("ERROR: unknown identifier specified!");
		}
	}

	/**
	 * Method to create y-axis set.
	 * @return y-axis set.
	 */
	private ArrayList<String> addYValues(Object yAxis, ProteinHit protHit) {
		// Collect non-redundant list of entries for y-Axis
		ArrayList<String> yEntry = new ArrayList<String>();
		
		if (yAxis instanceof TaxonomyChartType) {
			String taxName = NcbiTaxonomy.getTaxNameByRank(protHit.getTaxonomyNode(), ((TaxonomyChartType) yAxis).getRank());
			yEntry.add(taxName);
		} else if (yAxis instanceof OntologyChartType) {
			KeywordOntology ontology = ((OntologyChartType) yAxis).getOntology();
			// Get the ontology map
			Map<String, KeywordOntology> ontologyMap = UniprotAccessor.ONTOLOGY_MAP;
			// Check all keywords for biological process
			List<String> keywords = protHit.getUniprotEntry().getKeywords();
			if (keywords != null) {
				// Check all keywords
				for (String keyword : keywords) {
					KeywordOntology mapOntology = ontologyMap.get(keyword);
					if (mapOntology != null) {
						if (mapOntology.equals(ontology)) {
							yEntry.add(keyword);
						}
					}
				}
			}
		} else if (yAxis instanceof HierarchyLevel) {
			switch ((HierarchyLevel) yAxis) {
				case META_PROTEIN_LEVEL:
					// TODO implement this
					System.err.println("ERROR: not yet implemented!");
					break;
				case PROTEIN_LEVEL:
					Set<ProteinHit> metaProtSet = ((MetaProteinHit) protHit)
					.getProteinSet();
					for (ProteinHit metaProts : metaProtSet) {
						yEntry.add(metaProts.getAccession());
					}
					break;
				case PEPTIDE_LEVEL:
					List<PeptideHit> pepHitList = protHit.getPeptideHitList();
					for (PeptideHit pepHit : pepHitList) {
						yEntry.add(pepHit.getSequence());
					}
					break;
				case SPECTRUM_LEVEL:
					// TODO implement this
					System.err.println("ERROR: not yet implemented!");
					break;
				default:
					System.err.println("ERROR: unknown hierarchy level specified!");
					break;	
			}
		} else {
			System.err.println("ERROR: unknown identifier specified!");
			// TODO implement E.C./pathway options
			
//			if (yAxis.equals("EC_NUMBER")) {
//				List<String> ecNumbers = protHit.getUniprotEntry().getProteinDescription().getEcNumbers();
//				for (String ecNum : ecNumbers) {
//					yEntry.add(ecNum);
//					yAxisSet.add(ecNum);
//				}
//			}
//			if (yAxis.equals("PATHWAY")) {
//				// Add pathways by KO
//				List<DatabaseCrossReference> dcr = protHit.getUniprotEntry().getDatabaseCrossReferences(DatabaseType.KO);
//				for (int i = 0; i < dcr.size(); i++) {
//					// Get Ko number to match it to the pathways
//					DatabaseCrossReference ref = dcr.get(i);
//					String ko = ((KO) ref).getKOIdentifier().getValue();
//					List<Short> pathwaysByKO = KeggAccessor.getInstance().getPathwaysByKO(ko.substring(1));
//					if (pathwaysByKO != null) {
//						for (Short pathway : pathwaysByKO) {
//							Object[] keggPathwayPath = Constants.getKEGGPathwayPath(pathway);
//							if (keggPathwayPath != null && keggPathwayPath.length > 0) {
//								yEntry.add(keggPathwayPath[3].toString());
//								yAxisSet.add(keggPathwayPath[3].toString());
//							}
//						}
//					}
//				}
//				// Add pathways by EC
//				List<String> ecNumbers = protHit.getUniprotEntry().getProteinDescription().getEcNumbers();
//				for (String ecNumb : ecNumbers) {
//					List<Short> pathwaysByEC = KeggAccessor.getInstance().getPathwaysByEC(ecNumb);
//					if (pathwaysByEC != null) {
//						for (Short pathway : pathwaysByEC) {
//							Object[] keggPathwayPath = Constants.getKEGGPathwayPath(pathway);
//							if (keggPathwayPath != null && keggPathwayPath.length > 0) {
//								yEntry.add(keggPathwayPath[3].toString());
//								yAxisSet.add(keggPathwayPath[3].toString());
//							}
//						}
//					}
//				}
//			}
		}
		
		return yEntry;
	}

	/**
	 * Creates the heat map value matrix.
	 * @return the value matrix 
	 */
	private MatrixSeries createMatrix(Object xAxis, Object zAxis) {

		// Gather x axis labels
		this.collectXLabels();
		
		// Generate set of y axis labels
		Set<String> yAxisSet = this.collectYLabels();
		
		// Create matrix
		matrix = new MatrixSeries("matrix", yAxisSet.size(), xAxisMap.size());
		max = 0;
		// Go through matrix and save entries
		int j = 0;
		for (Entry<Object, List<String>> xEntry : xAxisMap.entrySet()) {
			List<String> xAxisList = xEntry.getValue();
			Object key = xEntry.getKey();
			int i = 0;
			for (String yEntry : yAxisSet) {
				int value = 0;
				if (xAxisList.contains(yEntry)) {
					switch ((HierarchyLevel) zAxis) {
						case META_PROTEIN_LEVEL:
						case PROTEIN_LEVEL:
							value = 1;
							break;
						case PEPTIDE_LEVEL:
							if (xAxis instanceof HierarchyLevel) {
								switch ((HierarchyLevel) xAxis) {
									case META_PROTEIN_LEVEL:
									case PROTEIN_LEVEL:
										value = ((ProteinHit) key).getPeptideCount();
										break;
									case PEPTIDE_LEVEL:
										value = 1;
										break;
									case SPECTRUM_LEVEL:
										// TODO implement this
										System.err.println("ERROR: not yet implemented!");
										break;
									default:
										System.err.println("ERROR: unknown hierarchy level specified!");
										break;
								}
							}
							break;
						case SPECTRUM_LEVEL:
							if (xAxis instanceof HierarchyLevel) {
								switch ((HierarchyLevel) xAxis) {
									case META_PROTEIN_LEVEL:
									case PROTEIN_LEVEL:
										value = ((ProteinHit) key).getSpectralCount();
										break;
									case PEPTIDE_LEVEL:
										value = ((PeptideHit) key).getSpectralCount();
										break;
									case SPECTRUM_LEVEL:
										// TODO implement this
										System.err.println("ERROR: not yet implemented!");
										break;
									default:
										System.err.println("ERROR: unknown hierarchy level specified!");
										break;
								}
							}
							break;
						default:
							System.err.println("ERROR: unknown hierarchy level specified!");
							break;
					}
				}
				matrix.update(i, j, value);
				max = Math.max(max, value);
				i++;
			}
			j++;
		}
		return matrix;
	}

	/**
	 * Method to collect x axis labels.
	 */
	private void collectXLabels() {
		// Collect x axis labels
		xLabels = new String[xAxisMap.size()];
		int xIndex = 0;
		for (Object obj : xAxisMap.keySet()) {
			// TODO: implement taxonomies/ontologies/etc.
			String label = null;
			if (obj instanceof ProteinHit) {
				label = ((ProteinHit) obj).getAccession();
			} else if (obj instanceof PeptideHit) {
				label = ((PeptideHit) obj).getSequence();
			} else if (obj instanceof SpectrumMatch) {
				// TODO implement this
				System.err.println("ERROR: not yet implemented!");
			} else {
				System.err.println("ERROR: unknown x axis element type!");
			}
			xLabels[xIndex++] = label;
		}
	}

	/**
	 * Method to collect y axis labels.
	 */
	private Set<String> collectYLabels() {
		Set<String> yAxisSet = new TreeSet<String>();
		for (List<String> values : xAxisMap.values()) {
			yAxisSet.addAll(values);
		}
		yLabels = yAxisSet.toArray(new String[0]);
		return yAxisSet;
	}

	/**
	 * Returns the value matrix.
	 * @return the value matrix.
	 */
	public MatrixSeries getMatrix() {
		return matrix;
	}

	/**
	 * Returns the maximum data value.
	 * @return the maximum
	 */
	public double getMaximum() {
		return max;
	}

	/**
	 * Returns the x axis labels.
	 * @return the x axis labels.
	 */
	public String[] getXLabels() {
		return xLabels;
	}

	/**
	 * Returns the y axis labels.
	 * @return the y axis labels.
	 */
	public String[] getYLabels() {
		return yLabels;
	}

}
