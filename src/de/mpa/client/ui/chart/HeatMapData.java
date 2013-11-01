package de.mpa.client.ui.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jfree.data.xy.MatrixSeries;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.Hit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.ui.chart.HeatMapPane.Axis;

/**
 * Container class for heat map-related data.
 * @author R. Heyer, A. Behne
 */
public class HeatMapData {

	/**
	 * The database search result reference.
	 */
	private DbSearchResult result;

	/**
	 * The type of data to be displayed on the horizontal axis.
	 */
	private ChartType xAxisType;

	/**
	 * The type of data to be displayed on the vertical axis.
	 */
	private ChartType yAxisType;

	/**
	 * The type of data to be displayed on the color axis.
	 */
	private HierarchyLevel zAxisType;

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
	 * The maximum value inside the value matrix.
	 */
	private double max;

	/**
	 * Constructor to create a default placeholder heat map data.
	 */
	public HeatMapData() {
		this.createDefault();
	}

	/**
	 * Creates a heat map data container from the specified result object and axis identifiers
	 * @param result the search result object
	 * @param xAxisType the x axis identifier
	 * @param yAxisType the y axis identifier
	 * @param zAxisType the z axis identifier
	 */
	public HeatMapData(DbSearchResult result, ChartType xAxisType, ChartType yAxisType, HierarchyLevel zAxisType) {
		this.result = result;
		this.xAxisType = xAxisType;
		this.yAxisType = yAxisType;
		this.zAxisType = zAxisType;
		
		this.createMatrix();
	}

	/**
	 * Generates matrix data and axis labels from a database search result reference.
	 */
	protected void createMatrix() {
		// Initialize list of hit objects
		List<Hit> hitList = new ArrayList<Hit>();
		
		// Determine type of hits to iterate, fill hit list accordingly
		switch (this.zAxisType) {
		case META_PROTEIN_LEVEL:
			hitList.addAll(result.getMetaProteins());
			break;
		case PROTEIN_LEVEL:
			hitList.addAll(result.getProteinHitList());
			break;
		case PEPTIDE_LEVEL:
			hitList.addAll(((ProteinHitList) result.getProteinHitList()).getPeptideSet());
			break;
		case SPECTRUM_LEVEL:
			hitList.addAll(((ProteinHitList) result.getProteinHitList()).getMatchSet());
			break;
		default:
			// if we get here something went wrong - investigate!
			System.err.println("ERROR: Unknown hierarchy level specified in " + 
					Thread.currentThread().getStackTrace()[0]);
			break;
		}

		// Initialize labels and matrix as local lists
		List<String> xLabels = new ArrayList<String>(), yLabels = new ArrayList<String>();
		List<List<Integer>> matrix = new ArrayList<List<Integer>>();
		int max = 0;
		// Iterate hit objects
//		int counter = 0;
		for (Hit hit : hitList) {
			int row = -1, col = -1;
			// Get set of hit properties for horizontal axis type
			Set<Object> xProps = hit.getProperties(xAxisType);
			// Iterate properties
			for (Object xProp : xProps) {
				// Check whether property has been stored before, set matrix column index accordingly
				int xIndex = xLabels.indexOf(xProp.toString());
				if (xIndex != -1) {
					// Property already exists, matrix column index equals label list index
					col = xIndex;
				} else {
					// Property has not been stored yet, append it to label list
					col = xLabels.size();
					xLabels.add(xProp.toString());
				}
				
				// Get set of hit properties for vertical axis type
				Set<Object> yProps = hit.getProperties(this.yAxisType);
				// Iterate properties
				for (Object yProp : yProps) {
					// Check whether property has been stored before, set matrix row index accordingly
					int yIndex = yLabels.indexOf(yProp.toString());
					if (yIndex != -1) {
						// Property already exists, matrix row index equals label list index
						row = yIndex;
					} else {
						// Property has not been stored yet, append it to label list
						row = yLabels.size();
						yLabels.add(yProp.toString());
					}
					
					// Get matrix row or initialize a new one if row index exceeds matrix size
					if (row >= matrix.size()) {
						matrix.add(new ArrayList<Integer>());
					}
					List<Integer> matrixRow = matrix.get(row);
					if (col >= matrixRow.size()) {
						// Column index exceeds row length,
						// pad matrix row with null elements ...
						while (matrixRow.size() <= col) {
							matrixRow.add(null);
						}
						// ... and initialize new element
						matrixRow.set(col, 1);
					} else {
						// Fetch column element from matrix row
						Integer val = matrixRow.get(col);
						// Increment existing value or initialize new one
						val = (val != null) ? val + 1 : 1;
						matrixRow.set(col, val);
					}
					// Determine global upper value boundary
					max = Math.max(max, matrixRow.get(col));
				}
			}
//			System.out.println("" + (counter++) + "/" + hitList.size());
		}

		if (xLabels.isEmpty()) {
			xLabels.add("No data");
			max = 1;
		}
		if (yLabels.isEmpty()) {
			yLabels.add("No data");
			max = 1;
		}
		
		// Cache list contents as arrays
		this.xLabels = xLabels.toArray(new String[0]);
		this.yLabels = yLabels.toArray(new String[0]);
		// Create matrix series object
		this.matrix = new MatrixSeries("matrix", yLabels.size(), xLabels.size());
		for (int i = 0; i < matrix.size(); i++) {
			List<Integer> matrixRow = matrix.get(i);
			for (int j = 0; j < matrixRow.size(); j++) {
				Integer val = matrixRow.get(j);
				if (val != null) {
					this.matrix.update(i, j, val);
				}
			}
		}
		// Cache maximum value
		this.max = max;
	}

	/**
	 * Create default placeholder heat map data.
	 */
	protected void createDefault() {
		// Generate value matrix
		int height = 26, width = 26;
		this.matrix = new MatrixSeries("matrix", height, width);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				double mij = Math.sin((j + 0.5) / width * Math.PI) *
						Math.cos((i + 0.5) / height * Math.PI - Math.PI / 2.0);
				this.matrix.update(i, j, Math.round(mij * 100.0));
			}
		}
		// Generate x axis labels
		this.xLabels = new String[width];
		for (int i = 0; i < this.xLabels.length; i++) {
			this.xLabels[i] = "" + (i + 1);
		}
		// Generate y axis labels
		this.yLabels = new String[height];
		for (int i = 0; i < this.yLabels.length; i++) {
			this.yLabels[i] = "" + (char) (i + 'A');
		}
		this.max = 100.0;
	}

	/**
	 * Sets the type of the specified axis to the specified value and refreshes
	 * the matrix data.
	 * @param type the type to set
	 * @param axis the axis to change
	 */
	public void setAxisType(ChartType type, Axis axis) {
		this.setAxisTypes(
				(axis == Axis.X_AXIS) ? type : null,
				(axis == Axis.Y_AXIS) ? type : null,
				(axis == Axis.Z_AXIS) ? (HierarchyLevel) type : null);
	}
	
	/**
	 * Sets the axis types to the specified values and refreshes the matrix data.
	 * @param xAxisType the x axis type
	 * @param yAxisType the y axis type
	 * @param zAxisType the z axis type
	 */
	public void setAxisTypes(ChartType xAxisType, ChartType yAxisType, HierarchyLevel zAxisType) {
		if (xAxisType != null) {
			this.xAxisType = xAxisType;
		}
		if (yAxisType != null) {
			this.yAxisType = yAxisType;
		}
		if (zAxisType != null) {
			this.zAxisType = zAxisType;
		}
		// Rebuild data matrix
		this.createMatrix();
	}

	/**
	 * Sets the result object reference.
	 * @param result the result object to set
	 */
	public void setResult(DbSearchResult result) {
		this.result = result;
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
