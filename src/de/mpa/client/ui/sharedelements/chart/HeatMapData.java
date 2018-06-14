package de.mpa.client.ui.sharedelements.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jfree.data.xy.MatrixSeries;

import de.mpa.client.Client;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.Hit;

/**
 * Container class for heat map-related data.
 * @author R. Heyer, A. Behne
 */
public class HeatMapData {

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
        createDefault();
	}

	/**
	 * Creates a heat map data container from the specified result object and axis identifiers. Used for Heatmap
	 * @param result the search result object
	 * @param xAxisType the x axis identifier
	 * @param yAxisType the y axis identifier
	 * @param zAxisType the z axis identifier
	 */
	public HeatMapData(ChartType xAxisType, ChartType yAxisType, HierarchyLevel zAxisType) {
		this.xAxisType = xAxisType;
		this.yAxisType = yAxisType;
		this.zAxisType = zAxisType;
        createMatrix();
	}
	
	/**
	 * Generates matrix data and axis labels from a database search result reference.
	 */
	protected void createMatrix() {
		// Initialize list of hit objects
		ArrayList<Hit> hitList = new ArrayList<Hit>();
		DbSearchResult result = Client.getInstance().getDatabaseSearchResult();
		// Determine type of hits to iterate, fill hit list accordingly
		switch (zAxisType) {
		case META_PROTEIN_LEVEL:
//			hitList.addAll(result.getAllMetaProteins());
			hitList.addAll(result.getAllMetaProteins());
			break;
		case PROTEIN_LEVEL:
//			hitList.addAll(result.getAllProteinHits());
			hitList.addAll(result.getAllProteinHits());
			break;
		case PEPTIDE_LEVEL:
//			hitList.addAll(result.getAllPeptideHits());
			hitList.addAll(result.getAllPeptideHits());
			break;
		case SPECTRUM_LEVEL:
//			hitList.addAll(result.getAllPSMS());
			hitList.addAll(result.getAllPSMS());
			break;
		default:
			// if we get here something went wrong - investigate!
			System.err.println("ERROR: Unknown hierarchy level specified in " + 
					Thread.currentThread().getStackTrace()[0]);
			break;
		}

		// Initialize labels and matrix as local lists
		List<String> xLabels = new ArrayList<String>(), yLabels = new ArrayList<String>();
		List<List<List<Hit>>> matrix = new ArrayList<List<List<Hit>>>();
		int max = 0;
		// Iterate hit objects
//		int counter = 0;
		for (Hit hit : hitList) {
			int row = -1, col = -1;
			// Get set of hit properties for horizontal axis type
			Set<Object> xProps = hit.getProperties(this.xAxisType);
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
				Set<Object> yProps = hit.getProperties(yAxisType);
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
						matrix.add(new ArrayList<List<Hit>>());
					}
					List<List<Hit>> matrixRow = matrix.get(row);
					List<Hit> val;
					if (col >= matrixRow.size()) {
						// Column index exceeds row length,
						// pad matrix row with null elements ...
						while (matrixRow.size() <= col) {
							matrixRow.add(null);
						}
						// ... and initialize new element
						val = new ArrayList<Hit>();
					} else {
						// Fetch column element from matrix row
						val = matrixRow.get(col);
						// initialize new list if not existent
						if (val == null) {
							val = new ArrayList<Hit>();
						}
					}
					val.add(hit);
					matrixRow.set(col, val);
					// Determine global upper value boundary
					max = Math.max(max, val.size());
				}
			}
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
//		this.matrix = new MatrixSeriesExt("matrix", yLabels.size(), xLabels.size());
//		for (int i = 0; i < matrix.size(); i++) {
//			List<Integer> matrixRow = matrix.get(i);
//			for (int j = 0; j < matrixRow.size(); j++) {
//				Integer val = matrixRow.get(j);
//				if (val != null) {
//					this.matrix.update(i, j, val);
//				}
//			}
//		}
		this.matrix = new HeatMapData.MatrixSeriesExt(matrix, yLabels.size(), xLabels.size());
		
		// Cache maximum value
		this.max = max;
	}

	/**
	 * Create default placeholder heat map data.
	 */
	protected void createDefault() {
		// Generate value matrix
		int height = 26, width = 26;
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
        max = 100.0;
	}
	
	public ChartType getAxisType(HeatMapPane.Axis axis) {
		switch (axis) {
		case X_AXIS:
			return this.xAxisType;
		case Y_AXIS:
			return this.yAxisType;
		case Z_AXIS:
			return this.zAxisType;
		default:
			return null;
		}
	}

	/**
	 * Sets the type of the specified axis to the specified value and refreshes
	 * the matrix data.
	 * @param type the type to set
	 * @param axis the axis to change
	 */
	public void setAxisType(ChartType type, HeatMapPane.Axis axis) {
		this.setAxisTypes(
				(axis == HeatMapPane.Axis.X_AXIS) ? type : null,
				(axis == HeatMapPane.Axis.Y_AXIS) ? type : null,
				(axis == HeatMapPane.Axis.Z_AXIS) ? (HierarchyLevel) type : null);
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
        createMatrix();
	}
	
	/**
	 * Returns the value matrix.
	 * @return the value matrix.
	 */
	public MatrixSeries getMatrix() {
		return this.matrix;
	}

	/**
	 * Returns the maximum data value.
	 * @return the maximum
	 */
	public double getMaximum() {
		return this.max;
	}

	/**
	 * Returns the x axis labels.
	 * @return the x axis labels.
	 */
	public String[] getXLabels() {
		return this.xLabels;
	}

	/**
	 * Returns the y axis labels.
	 * @return the y axis labels.
	 */
	public String[] getYLabels() {
		return this.yLabels;
	}
	
	/**
	 * Extended matrix series for the hits.
	 * @author R.Heyer
	 */
	public static class MatrixSeriesExt extends MatrixSeries {
		
		/**
		 * The serialization ID.
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * The hit matrix.
		 */
		private final List<List<List<Hit>>> matrix;

		/**
		 * Creates an extended matrix series wrapping the provided hit matrix.
		 * @param matrix the hit matrix
		 * @param rows the number of rows
		 * @param columns the number of columns
		 */
		public MatrixSeriesExt(List<List<List<Hit>>> matrix, int rows, int columns) {
			super("matrix", rows, columns);
			this.matrix = matrix;
		}
		
		/**
		 * Returns the hit matrix.
		 * @return the hit matrix
		 */
		public List<List<List<Hit>>> getMatrix() {
			return this.matrix;
		}

		@Override
		public double get(int i, int j) {
			if (i < this.matrix.size()) {
				List<List<Hit>> row = this.matrix.get(i);
				if (j < row.size()) {
					List<Hit> val = row.get(j);
					if (val != null) {
						return val.size();
					}
				}
			}
			return 0.0;
		}
		
	}
	
}
